package mob_grinding_utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.blocks.BlockTank;
import mob_grinding_utils.blocks.BlockTankJumbo;
import mob_grinding_utils.blocks.BlockTankSink;
import mob_grinding_utils.client.ModelLayers;
import mob_grinding_utils.models.ModelTankBlock;
import mob_grinding_utils.tile.TileEntityJumboTank;
import mob_grinding_utils.tile.TileEntitySinkTank;
import mob_grinding_utils.tile.TileEntityTank;
import mob_grinding_utils.util.RL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;
public class TileEntityTankRenderer implements BlockEntityRenderer<TileEntityTank, TileEntityTankRenderer.TankRenderState> {
	private static final Identifier TANK_TEXTURE = RL.mgu("textures/tiles/tank.png");
	private static final Identifier TANK_SINK_TEXTURE = RL.mgu("textures/tiles/tank_sink.png");
	private static final Identifier TANK_JUMBO_TEXTURE = RL.mgu("textures/tiles/tank_jumbo.png");
	private static final Identifier FLUID_XP_TEXTURE = RL.mgu("block/fluid_xp");
	private final ModelTankBlock tankModel;

	public TileEntityTankRenderer(Context context) {
		this.tankModel = new ModelTankBlock(context.bakeLayer(ModelLayers.TANK));
	}

	@Override
	public TankRenderState createRenderState() {
		return new TankRenderState();
	}

	@Override
	public void extractRenderState(
			TileEntityTank blockEntity,
			TankRenderState state,
			float partialTick,
			net.minecraft.world.phys.Vec3 cameraPos,
			ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
	) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
		var fluidResource = blockEntity.tank.getResource(0);
		state.fluidStack = FluidUtil.getStack(blockEntity.tank, 0);
		state.capacity = blockEntity.tank.getCapacityAsInt(0, fluidResource);
		state.shellTexture = blockEntity instanceof TileEntityJumboTank
				? TANK_JUMBO_TEXTURE
				: blockEntity instanceof TileEntitySinkTank
				? TANK_SINK_TEXTURE
				: TANK_TEXTURE;
	}

	@Override
	public void submit(TankRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.5D, 1.5D, 0.5D);
		poseStack.scale(-0.9999F, -0.9999F, 0.9999F);
		submitShell(state, poseStack, submitNodeCollector);

		if (state.fluidStack != null && !state.fluidStack.isEmpty() && state.capacity > 0) {
			TextureAtlas fluidAtlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
			TextureAtlasSprite fluidSprite = fluidAtlas.getSprite(FLUID_XP_TEXTURE);
			float amountRatio = (float) state.fluidStack.getAmount() / (float) state.capacity;
			float height = 0.96875F * amountRatio;
			submitNodeCollector.submitCustomGeometry(
					poseStack,
					Sheets.translucentBlockSheet(),
					(pose, buffer) -> renderFluidCuboid(
							pose,
							buffer,
							fluidSprite,
							1.984375F,
							0.015625F,
							0.015625F,
							height,
							0.015625F,
							1.984375F,
							0xFFFFFFFF,
							state.lightCoords
					)
			);
		}
		poseStack.popPose();
	}

	private void submitShell(TankRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		collector.submitModelPart(
				this.tankModel.tank_box,
				poseStack,
				RenderTypes.entityCutout(state.shellTexture),
				state.lightCoords,
				OverlayTexture.NO_OVERLAY,
				null,
				false,
				false,
				-1,
				null,
				0
		);
	}

	private void renderFluidCuboid(
			PoseStack.Pose pose,
			VertexConsumer buffer,
			TextureAtlasSprite sprite,
			float xMax,
			float xMin,
			float yMin,
			float height,
			float zMin,
			float zMax,
			int color,
			int lightCoords
	) {
		float uMin = sprite.getU0();
		float uMax = sprite.getU1();
		float vMin = sprite.getV0();
		float vMax = sprite.getV1();
		float vHeight = vMax - vMin;
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		float alpha = (color >>> 24) / 255.0F;

		addVertexWithUV(buffer, pose, xMax, height, zMax, uMax, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, height, zMin, uMin, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, height, zMin, uMin, vMax, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, height, zMax, uMax, vMax, red, green, blue, alpha, lightCoords);

		addVertexWithUV(buffer, pose, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, yMin, zMin, uMin, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, height, zMin, uMin, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, height, zMin, uMax, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);

		addVertexWithUV(buffer, pose, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, height, zMax, uMin, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, height, zMax, uMax, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, yMin, zMax, uMax, vMin, red, green, blue, alpha, lightCoords);

		addVertexWithUV(buffer, pose, xMax, yMin, zMin, uMin, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, height, zMin, uMin, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, height, zMax, uMax, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, yMin, zMax, uMax, vMin, red, green, blue, alpha, lightCoords);

		addVertexWithUV(buffer, pose, xMin, yMin, zMax, uMin, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, height, zMax, uMin, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, height, zMin, uMax, vMin + (vHeight * height), red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, yMin, zMin, uMax, vMin, red, green, blue, alpha, lightCoords);

		addVertexWithUV(buffer, pose, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, lightCoords);
		addVertexWithUV(buffer, pose, xMin, yMin, zMin, uMax, vMax, red, green, blue, alpha, lightCoords);
	}

	private void addVertexWithUV(
			VertexConsumer buffer,
			PoseStack.Pose pose,
			float x,
			float y,
			float z,
			float u,
			float v,
			float red,
			float green,
			float blue,
			float alpha,
			int lightCoords
	) {
		buffer.addVertex(pose, x / 2f, y, z / 2f)
				.setColor(red, green, blue, alpha)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setUv2(lightCoords, 240)
				.setNormal(pose, 1, 0, 0);
	}

	public static class TankRenderState extends BlockEntityRenderState {
		public @Nullable FluidStack fluidStack;
		public int capacity;
		public Identifier shellTexture = TANK_TEXTURE;
	}
}

