package net.njw.justtractor.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public final class TractorEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION = SynchedEntityData.defineId(TractorEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_STEERING_ANGLE = SynchedEntityData.defineId(TractorEntity.class, EntityDataSerializers.FLOAT);
    private static final double MAX_FORWARD_SPEED = 0.20;
    private static final double MAX_REVERSE_SPEED = 0.10;
    private static final double ACCELERATION = 0.012;
    private static final double DECELERATION = 0.020;
    private static final float TURN_SPEED = 2.5F;
    private static final float MAX_STEERING_ANGLE = 0.45F;
    private static final float STEERING_SPEED = 0.08F;
    private static final double REAR_WHEEL_RADIUS = 9.0 / 16.0;
    private static final double GRAVITY = 0.08;
    private double currentSpeed;
    private float currentSteeringAngle;
    private float localWheelRotation;
    private boolean clientForward;
    private boolean clientBackward;
    private boolean clientLeft;
    private boolean clientRight;
    private boolean localControl;

    public TractorEntity(EntityType<? extends TractorEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_WHEEL_ROTATION, 0.0F);
        builder.define(DATA_STEERING_ANGLE, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
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
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            if (this.localControl && this.getControllingPassenger() != null) {
                tickControlledMovement();
            } else {
                this.localControl = false;
            }

            return;
        }

        if (this.getControllingPassenger() instanceof ServerPlayer player) {
            updateServerAnimation(player.getLastClientInput());
        } else {
            this.currentSpeed = 0.0;
            this.currentSteeringAngle = approach(this.currentSteeringAngle, 0.0F, STEERING_SPEED);
            this.entityData.set(DATA_STEERING_ANGLE, this.currentSteeringAngle);
            tickUncontrolledPhysics();
        }
    }

    public void controlFromClient(Input input) {
        if (!this.level().isClientSide()) {
            return;
        }

        this.clientForward = input.forward();
        this.clientBackward = input.backward();
        this.clientLeft = input.left();
        this.clientRight = input.right();
        this.localControl = true;
    }

    private void tickControlledMovement() {
        double targetSpeed = getTargetSpeed(this.clientForward, this.clientBackward);
        double acceleration = targetSpeed == 0.0 ? DECELERATION : ACCELERATION;

        this.currentSpeed = approach(this.currentSpeed, targetSpeed, acceleration);

        float steeringInput = getSteeringInput(this.clientLeft, this.clientRight);
        float targetSteeringAngle = steeringInput * MAX_STEERING_ANGLE;

        this.currentSteeringAngle = approach(
                this.currentSteeringAngle,
                targetSteeringAngle,
                STEERING_SPEED
        );

        if (Math.abs(this.currentSpeed) > 0.001 && Math.abs(this.currentSteeringAngle) > 0.001F) {
            float speedRatio = (float) Math.min(
                    Math.abs(this.currentSpeed) / MAX_FORWARD_SPEED,
                    1.0
            );

            float reverseFactor = this.currentSpeed >= 0.0 ? 1.0F : -1.0F;
            float steeringRatio = this.currentSteeringAngle / MAX_STEERING_ANGLE;

            this.setYRot(
                    Mth.wrapDegrees(
                            this.getYRot()
                                    + steeringRatio
                                    * TURN_SPEED
                                    * speedRatio
                                    * reverseFactor
                    )
            );
        }

        double radians = Math.toRadians(this.getYRot());
        double velocityX = -Math.sin(radians) * this.currentSpeed;
        double velocityZ = Math.cos(radians) * this.currentSpeed;
        double velocityY = this.getDeltaMovement().y;

        this.setDeltaMovement(velocityX, velocityY, velocityZ);
        this.applyGravity();

        double oldX = this.getX();
        double oldZ = this.getZ();

        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 movement = this.getDeltaMovement();

        if (this.onGround()) {
            this.setDeltaMovement(movement.x, 0.0, movement.z);
        } else {
            this.setDeltaMovement(
                    movement.x,
                    movement.y * 0.98,
                    movement.z
            );
        }

        if (this.horizontalCollision) {
            this.currentSpeed *= 0.35;
        }

        double movedX = this.getX() - oldX;
        double movedZ = this.getZ() - oldZ;
        double distance = Math.sqrt(movedX * movedX + movedZ * movedZ);

        if (this.currentSpeed < 0.0) {
            distance = -distance;
        }

        this.localWheelRotation += (float) (distance / REAR_WHEEL_RADIUS);
        this.localWheelRotation = wrapRadians(this.localWheelRotation);
    }

    private void tickUncontrolledPhysics() {
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 movement = this.getDeltaMovement();

        if (this.onGround()) {
            this.setDeltaMovement(
                    movement.x * 0.8,
                    0.0,
                    movement.z * 0.8
            );
        } else {
            this.setDeltaMovement(
                    movement.x * 0.98,
                    movement.y * 0.98,
                    movement.z * 0.98
            );
        }
    }

    private void updateServerAnimation(Input input) {
        double targetSpeed = getTargetSpeed(
                input.forward(),
                input.backward()
        );

        double acceleration = targetSpeed == 0.0
                ? DECELERATION
                : ACCELERATION;

        this.currentSpeed = approach(
                this.currentSpeed,
                targetSpeed,
                acceleration
        );

        float steeringInput = getSteeringInput(
                input.left(),
                input.right()
        );

        float targetSteeringAngle =
                steeringInput * MAX_STEERING_ANGLE;

        this.currentSteeringAngle = approach(
                this.currentSteeringAngle,
                targetSteeringAngle,
                STEERING_SPEED
        );

        this.entityData.set(
                DATA_STEERING_ANGLE,
                this.currentSteeringAngle
        );

        if (Math.abs(this.currentSpeed) > 0.001) {
            float wheelRotation =
                    this.entityData.get(DATA_WHEEL_ROTATION)
                            + (float) (
                            this.currentSpeed
                                    / REAR_WHEEL_RADIUS
                    );

            this.entityData.set(
                    DATA_WHEEL_ROTATION,
                    wrapRadians(wheelRotation)
            );
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (player.isSecondaryUseActive() || this.isVehicle()) {
            return InteractionResult.PASS;
        }

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
        return passenger instanceof Player
                && this.getPassengers().isEmpty();
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        float radians =
                (float) Math.toRadians(-this.getYRot());

        Vec3 seatOffset =
                new Vec3(0.0, 0.95, -0.50).yRot(radians);

        return this.position().add(seatOffset);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }

    @Override
    public boolean hurtServer(
            ServerLevel level,
            DamageSource source,
            float damage
    ) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public float getWheelRotation() {
        if (this.level().isClientSide() && this.localControl) {
            return this.localWheelRotation;
        }

        return this.entityData.get(DATA_WHEEL_ROTATION);
    }

    public float getSteeringAngle() {
        if (this.level().isClientSide() && this.localControl) {
            return this.currentSteeringAngle;
        }

        return this.entityData.get(DATA_STEERING_ANGLE);
    }

    private void faceForward(Player player) {
        float yaw = this.getYRot();

        player.absSnapRotationTo(
                yaw,
                player.getXRot()
        );

        player.setYBodyRot(yaw);
        player.setYHeadRot(yaw);
    }

    private static double getTargetSpeed(
            boolean forward,
            boolean backward
    ) {
        if (forward == backward) {
            return 0.0;
        }

        return forward
                ? MAX_FORWARD_SPEED
                : -MAX_REVERSE_SPEED;
    }

    private static float getSteeringInput(
            boolean left,
            boolean right
    ) {
        if (left == right) {
            return 0.0F;
        }

        return left ? -1.0F : 1.0F;
    }

    private static double approach(
            double value,
            double target,
            double amount
    ) {
        if (value < target) {
            return Math.min(value + amount, target);
        }

        if (value > target) {
            return Math.max(value - amount, target);
        }

        return target;
    }

    private static float approach(
            float value,
            float target,
            float amount
    ) {
        if (value < target) {
            return Math.min(value + amount, target);
        }

        if (value > target) {
            return Math.max(value - amount, target);
        }

        return target;
    }

    private static float wrapRadians(float value) {
        float twoPi =
                (float) (Math.PI * 2.0);

        if (value > twoPi || value < -twoPi) {
            value %= twoPi;
        }

        return value;
    }
}