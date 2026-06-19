package mob_grinding_utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mob_grinding_utils.Reference;
import mob_grinding_utils.client.ModelLayers;
import mob_grinding_utils.blocks.BlockSaw;
import mob_grinding_utils.models.ModelSawBase;
import mob_grinding_utils.models.ModelSawBlade;
import mob_grinding_utils.tile.TileEntitySaw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
public class TileEntitySawRenderer implements BlockEntityRenderer<TileEntitySaw, TileEntitySawRenderer.SawRenderState> {
	private final ModelSawBase baseModel;
	private final ModelSawBlade bladeModel;
	private final RenderType blockRenderType;
	private final TextureAtlasSprite baseSprite;
	private final TextureAtlasSprite bladeSprite;

	public TileEntitySawRenderer(Context context) {
		this.baseModel = new ModelSawBase(context.bakeLayer(ModelLayers.SAW_BASE));
		this.bladeModel = new ModelSawBlade(context.bakeLayer(ModelLayers.SAW_BLADE));
		this.blockRenderType = RenderTypes.entitySolid(TextureAtlas.LOCATION_BLOCKS);
		TextureAtlas atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
		this.baseSprite = atlas.getSprite(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "block/saw_base"));
		this.bladeSprite = atlas.getSprite(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "block/saw_blade"));
	}

	@Override
	public SawRenderState createRenderState() {
		return new SawRenderState();
	}

	@Override
	public void extractRenderState(TileEntitySaw blockEntity, SawRenderState state, float partialTick, net.minecraft.world.phys.Vec3 cameraPos, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
		state.facing = blockEntity.getBlockState().getValue(BlockSaw.FACING);
		state.powered = blockEntity.getBlockState().getValue(BlockSaw.POWERED);
		state.active = blockEntity.active;
		state.animationTicks = blockEntity.animationTicks;
		state.prevAnimationTicks = blockEntity.prevAnimationTicks;
		state.partialTick = partialTick;
	}

	@Override
	public void submit(SawRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		float ticks = state.animationTicks + (state.animationTicks - state.prevAnimationTicks) * state.partialTick;
		if (!state.active) {
			ticks = 0.0F;
		}

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		applyFacing(poseStack, state.facing);
		poseStack.translate(0.0D, -1.0D, 0.0D);

		renderBase(state, poseStack, submitNodeCollector);

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(ticks));
		renderAxle(state, poseStack, submitNodeCollector);
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
		renderMace(state, poseStack, submitNodeCollector);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(165.0F));
		renderMace(state, poseStack, submitNodeCollector);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(285.0F));
		renderMace(state, poseStack, submitNodeCollector);
		poseStack.popPose();

		renderBlade(state, poseStack, submitNodeCollector);

		poseStack.popPose();

		poseStack.popPose();
	}

	private void renderBase(SawRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		collector.submitModelPart(this.baseModel.plinth, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.base, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
	}

	private void renderAxle(SawRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		collector.submitModelPart(this.baseModel.axle, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.axle2, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.axleTop, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
	}

	private void renderMace(SawRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		collector.submitModelPart(this.baseModel.maceBase, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.maceArm, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.mace1, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.mace2, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.mace3, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
		collector.submitModelPart(this.baseModel.mace4, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.baseSprite);
	}

	private void renderBlade(SawRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		poseStack.pushPose();
		poseStack.translate(0.0D, 0.2D, -0.16D);
		poseStack.mulPose(Axis.XP.rotationDegrees(8.0F));
		renderBladeParts(state, poseStack, collector);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(0.0D, 0.0D, 0.16D);
		poseStack.mulPose(Axis.XP.rotationDegrees(-8.0F));
		renderBladeParts(state, poseStack, collector);
		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(0.0D, -0.2D, -0.16D);
		poseStack.mulPose(Axis.XP.rotationDegrees(8.0F));
		renderBladeParts(state, poseStack, collector);
		poseStack.popPose();
	}

	private void renderBladeParts(SawRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
		collector.submitModelPart(this.bladeModel.main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.back, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.front, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.left, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.right, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth1Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth2Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth3Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth4Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth5Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth6Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth7Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth8Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth9Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth10Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth11Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth12Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth13Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth14Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth15Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth16Main, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth1End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth2End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth3End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth4End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth5End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth6End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth7End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth8End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth9End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth10End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth11End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth12End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth13End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth14End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth15End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
		collector.submitModelPart(this.bladeModel.tooth16End, poseStack, this.blockRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bladeSprite);
	}

	private void applyFacing(PoseStack poseStack, Direction facing) {
		if (facing == null) {
			return;
		}
		switch (facing) {
			case UP -> poseStack.mulPose(Axis.YP.rotationDegrees(0.0F));
			case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
			case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
			case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
			case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
			case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
			default -> {
			}
		}
	}

	public static class SawRenderState extends BlockEntityRenderState {
		public Direction facing = Direction.SOUTH;
		public boolean powered;
		public boolean active;
		public int animationTicks;
		public int prevAnimationTicks;
		public float partialTick;
	}
}

