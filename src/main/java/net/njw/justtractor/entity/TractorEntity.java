package net.njw.justtractor.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.njw.justtractor.item.TractorItem;
import net.njw.justtractor.menu.TractorInventoryMenu;
import net.njw.justtractor.menu.TractorUpgradeMenu;

public final class TractorEntity extends Entity implements HasCustomInventoryScreen {
    private static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION = SynchedEntityData.defineId(TractorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_STEERING_ANGLE = SynchedEntityData.defineId(TractorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_FORTUNE_LEVEL = SynchedEntityData.defineId(TractorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_SPEED_LEVEL = SynchedEntityData.defineId(TractorEntity.class, EntityDataSerializers.INT);

    public static final int MAX_FORTUNE_LEVEL = 3;
    public static final int MAX_SPEED_LEVEL = 5;

    private static final int INVENTORY_SIZE = 27;
    private static final int ATTACHMENT_INVENTORY_SIZE = 1;
    private static final double BASE_MAX_FORWARD_SPEED = 0.20;
    private static final double BASE_MAX_REVERSE_SPEED = 0.10;
    private static final double SPEED_BONUS_PER_LEVEL = 0.10;
    private static final double ACCELERATION = 0.012;
    private static final double DECELERATION = 0.020;
    private static final float TURN_SPEED = 2.5F;
    private static final float MAX_STEERING_ANGLE = 0.45F;
    private static final float STEERING_SPEED = 0.08F;
    private static final double REAR_WHEEL_RADIUS = 9.0 / 16.0;
    private static final double GRAVITY = 0.08;
    private static final double ENTITY_COLLISION_MARGIN = 0.2;
    private static final int RECOVERY_HITS = 4;
    private static final int RECOVERY_HIT_RESET_TICKS = 40;

    private final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE) {
        @Override
        public boolean stillValid(Player player) {
            return !TractorEntity.this.isRemoved() && TractorEntity.this.getControllingPassenger() == player;
        }
    };

    private final SimpleContainer attachmentInventory = new SimpleContainer(ATTACHMENT_INVENTORY_SIZE) {
        @Override
        public boolean stillValid(Player player) {
            return !TractorEntity.this.isRemoved() && TractorEntity.this.getControllingPassenger() == player;
        }
    };

    private double currentSpeed;
    private float currentSteeringAngle;
    private double localWheelRotation;
    private boolean clientForward;
    private boolean clientBackward;
    private boolean clientLeft;
    private boolean clientRight;
    private boolean allowTractorPush;
    private int recoveryHitCount;
    private int lastRecoveryHitTick = -RECOVERY_HIT_RESET_TICKS - 1;

    public TractorEntity(EntityType<? extends TractorEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_WHEEL_ROTATION, 0.0F);
        builder.define(DATA_STEERING_ANGLE, 0.0F);
        builder.define(DATA_FORTUNE_LEVEL, 0);
        builder.define(DATA_SPEED_LEVEL, 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.inventory.clearContent();
        this.inventory.fromItemList(input.listOrEmpty("Inventory", ItemStack.CODEC));
        this.attachmentInventory.clearContent();
        this.attachmentInventory.fromItemList(input.listOrEmpty("Attachment", ItemStack.CODEC));
        this.setFortuneLevel(input.getIntOr("FortuneLevel", 0));
        this.setSpeedLevel(input.getIntOr("SpeedLevel", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        this.inventory.storeAsItemList(output.list("Inventory", ItemStack.CODEC));
        this.attachmentInventory.storeAsItemList(output.list("Attachment", ItemStack.CODEC));
        output.putInt("FortuneLevel", this.getFortuneLevel());
        output.putInt("SpeedLevel", this.getSpeedLevel());
    }

    @Override
    protected double getDefaultGravity() {
        return GRAVITY;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    private static boolean canVehicleCollide(Entity vehicle, Entity entity) {
        return !vehicle.isPassengerOfSameVehicle(entity) && (entity.canBeCollidedWith(vehicle) || entity.isPushable());
    }

    @Override
    public boolean canBeCollidedWith(Entity other) {
        return !this.isRemoved() && (other == null || !this.isPassengerOfSameVehicle(other));
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void push(double x, double y, double z) {
        if (this.allowTractorPush) {
            super.push(x, y, z);
        }
    }

    @Override
    public void push(Entity entity) {
        if (this.isPassengerOfSameVehicle(entity)) return;

        if (entity instanceof TractorEntity tractor) {
            this.allowTractorPush = true;
            tractor.allowTractorPush = true;

            try {
                super.push(entity);
            } finally {
                this.allowTractorPush = false;
                tractor.allowTractorPush = false;
            }

            return;
        }

        super.push(entity);
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || this.getControllingPassenger() != player) return;
        serverPlayer.openMenu(new SimpleMenuProvider((containerId, playerInventory, menuPlayer) -> new TractorInventoryMenu(containerId, playerInventory, this), Component.translatable("container.njw_just_tractor.tractor")));
    }

    private void openUpgradeScreen(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        serverPlayer.openMenu(new SimpleMenuProvider((containerId, playerInventory, menuPlayer) -> new TractorUpgradeMenu(containerId, playerInventory, this), Component.translatable("screen.njw_just_tractor.tractor_upgrade")));
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity passenger = this.getControllingPassenger();

        if (this.level().isClientSide()) {
            if (this.isLocalInstanceAuthoritative() && passenger != null) {
                tickControlledMovement();
            }

            pushNearbyEntities();
            return;
        }

        if (passenger instanceof ServerPlayer player) {
            updateServerAnimation(player.getLastClientInput());

            if (Math.abs(this.currentSpeed) > 0.01) {
                TractorHarvester.harvest(this, (ServerLevel)this.level());
            }
        } else {
            this.currentSpeed = 0.0;
            this.currentSteeringAngle = approach(this.currentSteeringAngle, 0.0F, STEERING_SPEED);
            this.entityData.set(DATA_STEERING_ANGLE, this.currentSteeringAngle);
            tickUncontrolledPhysics();
        }

        pushNearbyEntities();
    }

    public void controlFromClient(Input input) {
        if (!this.level().isClientSide()) return;

        this.clientForward = input.forward();
        this.clientBackward = input.backward();
        this.clientLeft = input.left();
        this.clientRight = input.right();
    }

    private void tickControlledMovement() {
        double targetSpeed = getTargetSpeed(this.clientForward, this.clientBackward);
        double acceleration = targetSpeed == 0.0 ? DECELERATION : ACCELERATION;
        this.currentSpeed = approach(this.currentSpeed, targetSpeed, acceleration);

        float steeringInput = getSteeringInput(this.clientLeft, this.clientRight);
        float targetSteeringAngle = steeringInput * MAX_STEERING_ANGLE;
        this.currentSteeringAngle = approach(this.currentSteeringAngle, targetSteeringAngle, STEERING_SPEED);

        if (Math.abs(this.currentSpeed) > 0.001 && Math.abs(this.currentSteeringAngle) > 0.001F) {
            float speedRatio = (float)Math.min(Math.abs(this.currentSpeed) / getMaxForwardSpeed(), 1.0);
            float reverseFactor = this.currentSpeed >= 0.0 ? 1.0F : -1.0F;
            float steeringRatio = this.currentSteeringAngle / MAX_STEERING_ANGLE;
            this.setYRot(Mth.wrapDegrees(this.getYRot() + steeringRatio * TURN_SPEED * speedRatio * reverseFactor));
        }

        double radians = Math.toRadians(this.getYRot());
        double velocityX = -Math.sin(radians) * this.currentSpeed;
        double velocityZ = Math.cos(radians) * this.currentSpeed;
        double velocityY = this.getDeltaMovement().y;

        this.setDeltaMovement(velocityX, velocityY, velocityZ);
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 movement = this.getDeltaMovement();

        if (this.onGround()) {
            this.setDeltaMovement(movement.x, 0.0, movement.z);
        } else {
            this.setDeltaMovement(movement.x, movement.y * 0.98, movement.z * 0.98);
        }

        if (this.horizontalCollision) {
            this.currentSpeed *= 0.35;
        }

        if (Math.abs(this.currentSpeed) > 0.001) {
            this.localWheelRotation += this.currentSpeed / REAR_WHEEL_RADIUS;
        }
    }

    private void tickUncontrolledPhysics() {
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 movement = this.getDeltaMovement();

        if (this.onGround()) {
            this.setDeltaMovement(movement.x * 0.8, 0.0, movement.z * 0.8);
        } else {
            this.setDeltaMovement(movement.x * 0.98, movement.y * 0.98, movement.z * 0.98);
        }
    }

    private void updateServerAnimation(Input input) {
        double targetSpeed = getTargetSpeed(input.forward(), input.backward());
        double acceleration = targetSpeed == 0.0 ? DECELERATION : ACCELERATION;
        this.currentSpeed = approach(this.currentSpeed, targetSpeed, acceleration);

        float steeringInput = getSteeringInput(input.left(), input.right());
        float targetSteeringAngle = steeringInput * MAX_STEERING_ANGLE;
        this.currentSteeringAngle = approach(this.currentSteeringAngle, targetSteeringAngle, STEERING_SPEED);
        this.entityData.set(DATA_STEERING_ANGLE, this.currentSteeringAngle);

        if (Math.abs(this.currentSpeed) > 0.001) {
            double wheelRotation = this.entityData.get(DATA_WHEEL_ROTATION) + this.currentSpeed / REAR_WHEEL_RADIUS;
            this.entityData.set(DATA_WHEEL_ROTATION, wrapRadians(wheelRotation));
        }
    }

    private void pushNearbyEntities() {
        for (Entity entity : this.level().getPushableEntities(this, this.getBoundingBox().inflate(ENTITY_COLLISION_MARGIN, 0.0, ENTITY_COLLISION_MARGIN))) {
            if (!canVehicleCollide(this, entity)) continue;
            this.push(entity);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (player.isSecondaryUseActive()) {
            if (player.isPassenger() || this.isVehicle()) return InteractionResult.PASS;

            if (!this.level().isClientSide()) {
                openUpgradeScreen(player);
            }

            return InteractionResult.SUCCESS;
        }

        if (this.isVehicle()) return InteractionResult.PASS;

        if (this.level().isClientSide()) {
            faceForward(player);
            return InteractionResult.SUCCESS;
        }

        if (player.startRiding(this)) {
            faceForward(player);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        super.onPassengerTurned(passenger);

        if (passenger instanceof LivingEntity livingEntity) {
            livingEntity.setYBodyRot(livingEntity.getYRot());
            livingEntity.setYHeadRot(livingEntity.getYRot());
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player && this.getPassengers().isEmpty();
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        float radians = (float)Math.toRadians(-this.getYRot());
        Vec3 seatOffset = new Vec3(0.0, 0.95, -0.50).yRot(radians);
        return this.position().add(seatOffset);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (!(source.getDirectEntity() instanceof Player player)) return false;
        if (this.isVehicle()) return false;

        if (player.isCreative()) {
            recover(level);
            return true;
        }

        if (this.tickCount - this.lastRecoveryHitTick > RECOVERY_HIT_RESET_TICKS) {
            this.recoveryHitCount = 0;
        }

        this.lastRecoveryHitTick = this.tickCount;
        this.recoveryHitCount++;

        if (this.recoveryHitCount >= RECOVERY_HITS) {
            recover(level);
        } else {
            level.broadcastDamageEvent(this, source);
        }

        return true;
    }

    private void recover(ServerLevel level) {
        for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
            ItemStack stack = this.inventory.removeItemNoUpdate(slot);

            if (!stack.isEmpty()) {
                Block.popResource(level, this.blockPosition(), stack);
            }
        }

        for (int slot = 0; slot < this.attachmentInventory.getContainerSize(); slot++) {
            ItemStack stack = this.attachmentInventory.removeItemNoUpdate(slot);

            if (!stack.isEmpty()) {
                Block.popResource(level, this.blockPosition(), stack);
            }
        }

        ItemStack tractorStack = TractorItem.createStack(this.getFortuneLevel(), this.getSpeedLevel());
        Block.popResource(level, this.blockPosition(), tractorStack);
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public SimpleContainer getAttachmentInventory() {
        return this.attachmentInventory;
    }

    public ItemStack addCargo(ItemStack stack) {
        return this.inventory.addItem(stack);
    }

    public int getFortuneLevel() {
        return this.entityData.get(DATA_FORTUNE_LEVEL);
    }

    public void setFortuneLevel(int level) {
        this.entityData.set(DATA_FORTUNE_LEVEL, Math.max(0, Math.min(MAX_FORTUNE_LEVEL, level)));
    }

    public int getSpeedLevel() {
        return this.entityData.get(DATA_SPEED_LEVEL);
    }

    public void setSpeedLevel(int level) {
        this.entityData.set(DATA_SPEED_LEVEL, Math.max(0, Math.min(MAX_SPEED_LEVEL, level)));
    }

    public float getWheelRotation(float partialTick) {
        if (this.level().isClientSide() && this.isLocalInstanceAuthoritative() && this.getControllingPassenger() != null) {
            double rotation = this.localWheelRotation + this.currentSpeed / REAR_WHEEL_RADIUS * partialTick;
            return wrapRadians(rotation);
        }

        return this.entityData.get(DATA_WHEEL_ROTATION);
    }

    public float getSteeringAngle() {
        if (this.level().isClientSide() && this.isLocalInstanceAuthoritative() && this.getControllingPassenger() != null) {
            return this.currentSteeringAngle;
        }

        return this.entityData.get(DATA_STEERING_ANGLE);
    }

    private double getMaxForwardSpeed() {
        return BASE_MAX_FORWARD_SPEED * (1.0 + SPEED_BONUS_PER_LEVEL * this.getSpeedLevel());
    }

    private double getMaxReverseSpeed() {
        return BASE_MAX_REVERSE_SPEED * (1.0 + SPEED_BONUS_PER_LEVEL * this.getSpeedLevel());
    }

    private double getTargetSpeed(boolean forward, boolean backward) {
        if (forward == backward) return 0.0;
        return forward ? getMaxForwardSpeed() : -getMaxReverseSpeed();
    }

    private void faceForward(Player player) {
        float yaw = this.getYRot();
        player.absSnapRotationTo(yaw, player.getXRot());
        player.setYBodyRot(yaw);
        player.setYHeadRot(yaw);
    }

    private static float getSteeringInput(boolean left, boolean right) {
        if (left == right) return 0.0F;
        return left ? -1.0F : 1.0F;
    }

    private static double approach(double value, double target, double amount) {
        if (value < target) return Math.min(value + amount, target);
        if (value > target) return Math.max(value - amount, target);
        return target;
    }

    private static float approach(float value, float target, float amount) {
        if (value < target) return Math.min(value + amount, target);
        if (value > target) return Math.max(value - amount, target);
        return target;
    }

    private static float wrapRadians(double value) {
        double twoPi = Math.PI * 2.0;
        value %= twoPi;
        return (float)value;
    }
}
