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
        state.wheelRotation = entity.getWheelRotation(partialTick);
        state.steeringAngle = entity.getSteeringAngle();
    }

    @Override
    public void submit(TractorRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -25.0F / 16.0F, 0.0F);

        submitPart(collector, this.model.body(), poseStack, state, BODY_COLOR);
        submitPart(collector, this.model.details(), poseStack, state, DETAIL_COLOR);

        submitFrontWheel(collector, this.model.frontLeftWheel(), poseStack, state, -13.0F, 19.0F, -12.0F);
        submitFrontWheel(collector, this.model.frontRightWheel(), poseStack, state, 13.0F, 19.0F, -12.0F);

        submitRearWheel(collector, this.model.rearLeftWheel(), poseStack, state, -13.5F, 16.0F, 10.0F);
        submitRearWheel(collector, this.model.rearRightWheel(), poseStack, state, 13.5F, 16.0F, 10.0F);

        poseStack.popPose();

        super.submit(state, poseStack, collector, camera);
    }

    private static void submitFrontWheel(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, TractorRenderState state, float x, float y, float z) {
        poseStack.pushPose();

        poseStack.translate(x / 16.0F, y / 16.0F, z / 16.0F);
        poseStack.mulPose(Axis.YP.rotation(state.steeringAngle));
        poseStack.mulPose(Axis.XP.rotation(state.wheelRotation));

        submitPart(collector, part, poseStack, state, WHEEL_COLOR);

        poseStack.popPose();
    }

    private static void submitRearWheel(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, TractorRenderState state, float x, float y, float z) {
        float tx = x / 16.0F;
        float ty = y / 16.0F;
        float tz = z / 16.0F;

        poseStack.pushPose();

        poseStack.translate(tx, ty, tz);
        poseStack.mulPose(Axis.XP.rotation(state.wheelRotation));
        poseStack.translate(-tx, -ty, -tz);

        submitPart(collector, part, poseStack, state, WHEEL_COLOR);

        poseStack.popPose();
    }

    private static void submitPart(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, TractorRenderState state, int color) {
        collector.submitModelPart(part, poseStack, RENDER_TYPE, state.lightCoords, OverlayTexture.NO_OVERLAY, null, false, false, color, null, state.outlineColor);
    }
}