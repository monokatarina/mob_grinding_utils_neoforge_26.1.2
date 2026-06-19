package mob_grinding_utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.client.ModelLayers;
import mob_grinding_utils.models.ModelAHConnect;
import mob_grinding_utils.tile.TileEntityAbsorptionHopper;
import mob_grinding_utils.tile.TileEntityAbsorptionHopper.EnumStatus;
import mob_grinding_utils.util.RL;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
public class TileEntityAbsorptionRenderer implements BlockEntityRenderer<TileEntityAbsorptionHopper, TileEntityAbsorptionRenderer.AbsorptionRenderState> {
	private static final Identifier ITEM_TEXTURE = RL.mgu("textures/tiles/absorption_hopper_connects_items.png");
	private static final Identifier FLUID_TEXTURE = RL.mgu("textures/tiles/absorption_hopper_connects_fluids.png");
	private final ModelAHConnect connectionModel;

	public TileEntityAbsorptionRenderer(Context context) {
		this.connectionModel = new ModelAHConnect(context.bakeLayer(ModelLayers.ABSORPTION_HOPPER));
	}

	@Override
	public AbsorptionRenderState createRenderState() {
		return new AbsorptionRenderState();
	}

	@Override
	public void extractRenderState(TileEntityAbsorptionHopper blockEntity, AbsorptionRenderState state, float partialTick, Vec3 cameraPos, CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
		state.status = blockEntity.status.clone();
		state.showRenderBox = blockEntity.showRenderBox;
		state.renderAabb = blockEntity.getAABBForRender();
	}

	@Override
	public void submit(AbsorptionRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if (state.status == null) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);
		for (Direction facing : Direction.values()) {
			if (state.status[facing.ordinal()] == EnumStatus.STATUS_OUTPUT_ITEM) {
				poseStack.pushPose();
				applyFacing(poseStack, facing);
				poseStack.translate(0.0D, 0.0D, 0.0D);
				submitNodeCollector.submitModelPart(
						this.connectionModel.plate,
						poseStack,
						RenderTypes.entitySolid(ITEM_TEXTURE),
						state.lightCoords,
						OverlayTexture.NO_OVERLAY,
						null,
						false,
						false,
						-1,
						null,
						0
				);
				submitNodeCollector.submitModelPart(
						this.connectionModel.pipe,
						poseStack,
						RenderTypes.entitySolid(ITEM_TEXTURE),
						state.lightCoords,
						OverlayTexture.NO_OVERLAY,
						null,
						false,
						false,
						-1,
						null,
						0
				);
				poseStack.popPose();
			}

			if (state.status[facing.ordinal()] == EnumStatus.STATUS_OUTPUT_FLUID) {
				poseStack.pushPose();
				applyFacing(poseStack, facing);
				submitNodeCollector.submitModelPart(
						this.connectionModel.plate,
						poseStack,
						RenderTypes.entitySolid(FLUID_TEXTURE),
						state.lightCoords,
						OverlayTexture.NO_OVERLAY,
						null,
						false,
						false,
						-1,
						null,
						0
				);
				submitNodeCollector.submitModelPart(
						this.connectionModel.pipe,
						poseStack,
						RenderTypes.entitySolid(FLUID_TEXTURE),
						state.lightCoords,
						OverlayTexture.NO_OVERLAY,
						null,
						false,
						false,
						-1,
						null,
						0
				);
				poseStack.popPose();
			}
		}
		poseStack.popPose();

		if (state.showRenderBox && state.renderAabb != null) {
			poseStack.pushPose();
			poseStack.translate(-0.0005D, -0.0005D, -0.0005D);
			poseStack.scale(0.999F, 0.999F, 0.999F);
			submitNodeCollector.submitCustomGeometry(
					poseStack,
					RenderTypes.LINES,
					(pose, buffer) -> renderWireBox(pose, buffer, state.renderAabb, state.lightCoords, 1F, 1F, 0F, 1F)
			);
			poseStack.popPose();
		}
	}

	private void renderWireBox(PoseStack.Pose pose, VertexConsumer buffer, AABB box, int lightCoords, float red, float green, float blue, float alpha) {
		double minX = box.minX;
		double minY = box.minY;
		double minZ = box.minZ;
		double maxX = box.maxX;
		double maxY = box.maxY;
		double maxZ = box.maxZ;

		vertex(buffer, pose, minX, minY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, minY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, minY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, minY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, minY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, minY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, minY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, minY, minZ, red, green, blue, alpha, lightCoords);

		vertex(buffer, pose, minX, maxY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, maxY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, maxY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, maxY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, maxY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, maxY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, maxY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, maxY, minZ, red, green, blue, alpha, lightCoords);

		vertex(buffer, pose, minX, minY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, maxY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, minY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, maxY, minZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, minY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, maxX, maxY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, minY, maxZ, red, green, blue, alpha, lightCoords);
		vertex(buffer, pose, minX, maxY, maxZ, red, green, blue, alpha, lightCoords);
	}

	private void vertex(VertexConsumer buffer, PoseStack.Pose pose, double x, double y, double z, float red, float green, float blue, float alpha, int lightCoords) {
		buffer.addVertex(pose, (float) x, (float) y, (float) z)
				.setColor(red, green, blue, alpha)
				.setUv2(lightCoords, 240)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setNormal(pose, 0.0F, 1.0F, 0.0F)
				.setLineWidth(1.0F);
	}

	private void applyFacing(PoseStack poseStack, Direction facing) {
		switch (facing) {
			case UP -> poseStack.mulPose(Axis.XP.rotationDegrees(180F));
			case DOWN -> {
			}
			case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90F));
			case SOUTH -> poseStack.mulPose(Axis.XN.rotationDegrees(90F));
			case WEST -> poseStack.mulPose(Axis.ZN.rotationDegrees(90F));
			case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90F));
		}
	}

	public static class AbsorptionRenderState extends BlockEntityRenderState {
		public EnumStatus[] status;
		public boolean showRenderBox;
		public AABB renderAabb;
	}
}

