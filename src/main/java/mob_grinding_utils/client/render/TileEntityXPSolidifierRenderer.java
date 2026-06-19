package mob_grinding_utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mob_grinding_utils.tile.TileEntityXPSolidifier;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
public class TileEntityXPSolidifierRenderer implements BlockEntityRenderer<TileEntityXPSolidifier, BlockEntityRenderState> {
	public TileEntityXPSolidifierRenderer(Context context) {
	}

	@Override
	public BlockEntityRenderState createRenderState() {
		return new BlockEntityRenderState();
	}

	@Override
	public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
	}
}

