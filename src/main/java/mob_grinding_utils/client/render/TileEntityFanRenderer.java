package mob_grinding_utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mob_grinding_utils.tile.TileEntityFan;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
public class TileEntityFanRenderer implements BlockEntityRenderer<TileEntityFan, TileEntityFanRenderer.FanRenderState> {
	public TileEntityFanRenderer(Context context) {
	}

	@Override
	public FanRenderState createRenderState() {
		return new FanRenderState();
	}

	@Override
	public void extractRenderState(TileEntityFan blockEntity, FanRenderState state, float partialTick, Vec3 cameraPos, CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
		state.showRenderBox = blockEntity.showRenderBox;
		state.renderAabb = blockEntity.getAABBForRender();
	}

	@Override
	public void submit(FanRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if (!state.showRenderBox || state.renderAabb == null) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(-0.0005D, -0.0005D, -0.0005D);
		poseStack.scale(0.999F, 0.999F, 0.999F);
		submitNodeCollector.submitCustomGeometry(
				poseStack,
				RenderTypes.LINES,
				(pose, buffer) -> renderWireBox(pose, buffer, state.renderAabb, state.lightCoords, 0F, 0F, 1F, 1F)
		);
		poseStack.popPose();
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
				.setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
				.setNormal(pose, 0.0F, 1.0F, 0.0F)
				.setLineWidth(1.0F);
	}

	public static class FanRenderState extends BlockEntityRenderState {
		public boolean showRenderBox;
		public AABB renderAabb;
	}
}

