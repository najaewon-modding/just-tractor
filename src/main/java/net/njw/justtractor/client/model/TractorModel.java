package net.njw.justtractor.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.njw.justtractor.JustTractor;
import net.njw.justtractor.client.renderer.TractorRenderState;

public final class TractorModel extends EntityModel<TractorRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(JustTractor.MODID, "tractor"), "main");

    private final ModelPart body;
    private final ModelPart details;
    private final ModelPart frontLeftWheel;
    private final ModelPart frontRightWheel;
    private final ModelPart rearLeftWheel;
    private final ModelPart rearRightWheel;

    public TractorModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.details = root.getChild("details");
        this.frontLeftWheel = root.getChild("front_left_steering").getChild("wheel");
        this.frontRightWheel = root.getChild("front_right_steering").getChild("wheel");
        this.rearLeftWheel = root.getChild("rear_left_wheel");
        this.rearRightWheel = root.getChild("rear_right_wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

        body.addOrReplaceChild("chassis", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -4.0F, -17.0F, 22.0F, 5.0F, 34.0F), PartPose.offset(0.0F, 18.0F, 0.0F));
        body.addOrReplaceChild("hood", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -7.0F, -10.0F, 18.0F, 10.0F, 18.0F), PartPose.offset(0.0F, 14.0F, -8.0F));
        body.addOrReplaceChild("rear_body", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -5.0F, -6.0F, 20.0F, 8.0F, 12.0F), PartPose.offset(0.0F, 16.0F, 9.0F));
        body.addOrReplaceChild("left_step", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.0F, -4.0F, 5.0F, 2.0F, 8.0F), PartPose.offset(-13.0F, 20.0F, 5.0F));
        body.addOrReplaceChild("right_step", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -4.0F, 5.0F, 2.0F, 8.0F), PartPose.offset(13.0F, 20.0F, 5.0F));
        body.addOrReplaceChild("roof", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, -1.0F, -9.0F, 22.0F, 2.0F, 18.0F), PartPose.offset(0.0F, -1.0F, 8.0F));
        body.addOrReplaceChild("front_left_post", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.offset(-8.0F, 13.0F, 2.0F));
        body.addOrReplaceChild("front_right_post", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.offset(8.0F, 13.0F, 2.0F));
        body.addOrReplaceChild("rear_left_post", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.offset(-8.0F, 13.0F, 14.0F));
        body.addOrReplaceChild("rear_right_post", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -14.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.offset(8.0F, 13.0F, 14.0F));

        PartDefinition details = root.addOrReplaceChild("details", CubeListBuilder.create(), PartPose.ZERO);

        details.addOrReplaceChild("grille", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -5.0F, -1.0F, 14.0F, 8.0F, 2.0F), PartPose.offset(0.0F, 14.0F, -18.0F));
        details.addOrReplaceChild("seat_bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -4.0F, 10.0F, 3.0F, 8.0F), PartPose.offset(0.0F, 12.0F, 8.0F));
        details.addOrReplaceChild("seat_back", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -2.0F, 10.0F, 8.0F, 3.0F), PartPose.offset(0.0F, 10.0F, 12.0F));

        PartDefinition steeringColumn = details.addOrReplaceChild("steering_column", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.offsetAndRotation(0.0F, 11.0F, 1.0F, -0.45F, 0.0F, 0.0F));
        steeringColumn.addOrReplaceChild("steering_wheel", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F).texOffs(0, 0).addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 8.0F), PartPose.offset(0.0F, -7.0F, 0.0F));

        details.addOrReplaceChild("exhaust", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -12.0F, -1.5F, 3.0F, 12.0F, 3.0F).texOffs(0, 0).addBox(-2.0F, -15.0F, -2.0F, 4.0F, 3.0F, 4.0F), PartPose.offset(-6.0F, 10.0F, -11.0F));

        PartDefinition frontLeftSteering = root.addOrReplaceChild("front_left_steering", CubeListBuilder.create(), PartPose.offset(-13.0F, 19.0F, -12.0F));
        PartDefinition frontLeftWheel = frontLeftSteering.addOrReplaceChild("wheel", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 8.0F, 12.0F).texOffs(0, 0).addBox(-2.0F, -6.0F, -4.0F, 4.0F, 12.0F, 8.0F), PartPose.ZERO);
        frontLeftWheel.addOrReplaceChild("hub", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);

        PartDefinition frontRightSteering = root.addOrReplaceChild("front_right_steering", CubeListBuilder.create(), PartPose.offset(13.0F, 19.0F, -12.0F));
        PartDefinition frontRightWheel = frontRightSteering.addOrReplaceChild("wheel", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 8.0F, 12.0F).texOffs(0, 0).addBox(-2.0F, -6.0F, -4.0F, 4.0F, 12.0F, 8.0F), PartPose.ZERO);
        frontRightWheel.addOrReplaceChild("hub", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);

        PartDefinition rearLeftWheel = root.addOrReplaceChild("rear_left_wheel", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -6.0F, -9.0F, 5.0F, 12.0F, 18.0F).texOffs(0, 0).addBox(-2.5F, -9.0F, -6.0F, 5.0F, 18.0F, 12.0F), PartPose.offset(-13.5F, 16.0F, 10.0F));
        rearLeftWheel.addOrReplaceChild("hub", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -4.0F, 7.0F, 8.0F, 8.0F), PartPose.ZERO);

        PartDefinition rearRightWheel = root.addOrReplaceChild("rear_right_wheel", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -6.0F, -9.0F, 5.0F, 12.0F, 18.0F).texOffs(0, 0).addBox(-2.5F, -9.0F, -6.0F, 5.0F, 18.0F, 12.0F), PartPose.offset(13.5F, 16.0F, 10.0F));
        rearRightWheel.addOrReplaceChild("hub", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -4.0F, 7.0F, 8.0F, 8.0F), PartPose.ZERO);

        return LayerDefinition.create(mesh, 128, 128);
    }

    public ModelPart body() {
        return this.body;
    }

    public ModelPart details() {
        return this.details;
    }

    public ModelPart frontLeftWheel() {
        return this.frontLeftWheel;
    }

    public ModelPart frontRightWheel() {
        return this.frontRightWheel;
    }

    public ModelPart rearLeftWheel() {
        return this.rearLeftWheel;
    }

    public ModelPart rearRightWheel() {
        return this.rearRightWheel;
    }
}