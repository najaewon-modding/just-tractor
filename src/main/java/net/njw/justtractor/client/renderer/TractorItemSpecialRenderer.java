package net.njw.justtractor.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.njw.justtractor.client.model.TractorModel;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public final class TractorItemSpecialRenderer implements NoDataSpecialModelRenderer {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/block/white_concrete.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entitySolid(TEXTURE);
    private static final int BODY_COLOR = ARGB.color(255, 214, 126, 36);
    private static final int DETAIL_COLOR = ARGB.color(255, 50, 53, 60);
    private static final int WHEEL_COLOR = ARGB.color(255, 42, 45, 50);
    private final TractorModel model;

    public TractorItemSpecialRenderer(ModelPart root) {
        this.model = new TractorModel(root);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -25.0F / 16.0F, 0.0F);
        submitPart(collector, this.model.body(), poseStack, lightCoords, BODY_COLOR, outlineColor);
        submitPart(collector, this.model.details(), poseStack, lightCoords, DETAIL_COLOR, outlineColor);
        submitFrontWheel(collector, this.model.frontLeftWheel(), poseStack, lightCoords, outlineColor, -13.0F, 19.0F, -12.0F);
        submitFrontWheel(collector, this.model.frontRightWheel(), poseStack, lightCoords, outlineColor, 13.0F, 19.0F, -12.0F);
        submitPart(collector, this.model.rearLeftWheel(), poseStack, lightCoords, WHEEL_COLOR, outlineColor);
        submitPart(collector, this.model.rearRightWheel(), poseStack, lightCoords, WHEEL_COLOR, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {}

    private static void submitFrontWheel(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, int lightCoords, int outlineColor, float x, float y, float z) {
        poseStack.pushPose();
        poseStack.translate(x / 16.0F, y / 16.0F, z / 16.0F);
        submitPart(collector, part, poseStack, lightCoords, WHEEL_COLOR, outlineColor);
        poseStack.popPose();
    }

    private static void submitPart(SubmitNodeCollector collector, ModelPart part, PoseStack poseStack, int lightCoords, int color, int outlineColor) {
        collector.submitModelPart(part, poseStack, RENDER_TYPE, lightCoords, OverlayTexture.NO_OVERLAY, null, false, false, color, null, outlineColor);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public TractorItemSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new TractorItemSpecialRenderer(context.entityModelSet().bakeLayer(TractorModel.LAYER_LOCATION));
        }
    }
}
