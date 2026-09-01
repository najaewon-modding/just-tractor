package net.njw.justtractor.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.njw.justtractor.client.model.TractorModel;
import net.njw.justtractor.entity.TractorEntity;

public final class TractorRenderer extends EntityRenderer<TractorEntity, TractorRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/block/white_concrete.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entitySolid(TEXTURE);
    private static final int BODY_COLOR = ARGB.color(255, 214, 126, 36);
    private static final int DETAIL_COLOR = ARGB.color(255, 50, 53, 60);
    private static final int WHEEL_COLOR = ARGB.color(255, 42, 45, 50);
    private final TractorModel model;

    public TractorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TractorModel(context.bakeLayer(TractorModel.LAYER_LOCATION));
        this.shadowRadius = 1.1F;
    }

    @Override
    public TractorRenderState createRenderState() {
        return new TractorRenderState();
    }

    @Override
    public void extractRenderState(TractorEntity entity, TractorRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot(partialTick);
        state.wheelRotation = entity.getWheelRotation();
        state.steeringAngle = entity.getSteeringAngle();
    }

    @Override
    public void submit(TractorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -25.0F / 16.0F, 0.0F);
        this.model.setupAnim(state);
        submitPart(collector, this.model.body(), poseStack, state, BODY_COLOR);
        submitPart(collector, this.model.details(), poseStack, state, DETAIL_COLOR);
        submitPart(collector, this.model.frontLeftSteering(), poseStack, state, WHEEL_COLOR);
        submitPart(collector, this.model.frontRightSteering(), poseStack, state, WHEEL_COLOR);
        submitPart(collector, this.model.rearLeftWheel(), poseStack, state, WHEEL_COLOR);
        submitPart(collector, this.model.rearRightWheel(), poseStack, state, WHEEL_COLOR);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private static void submitPart(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, TractorRenderState state, int color) {
        collector.submitModelPart(part, poseStack, RENDER_TYPE, state.lightCoords, OverlayTexture.NO_OVERLAY, null, false, false, color, null, state.outlineColor);
    }
}