package mob_grinding_utils.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import mob_grinding_utils.Reference;
import mob_grinding_utils.client.ModelLayers;
import mob_grinding_utils.models.ModelSawBase;
import mob_grinding_utils.models.ModelSawBlade;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import org.joml.Vector3fc;

import java.util.function.Consumer;
public class SawSpecialItemRenderer implements NoDataSpecialModelRenderer {
    private static final Identifier BASE_TEXTURE = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/tiles/saw_base.png");
    private static final Identifier BLADE_TEXTURE = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/tiles/saw_blade.png");
    private final ModelSawBase baseModel;
    private final ModelSawBlade bladeModel;
    private final RenderType baseRenderType;
    private final RenderType bladeRenderType;

    public SawSpecialItemRenderer(EntityModelSet modelSet, net.minecraft.client.resources.model.sprite.SpriteGetter sprites) {
        this.baseModel = new ModelSawBase(modelSet.bakeLayer(ModelLayers.SAW_BASE));
        this.bladeModel = new ModelSawBlade(modelSet.bakeLayer(ModelLayers.SAW_BLADE));
        this.baseRenderType = RenderTypes.entitySolid(BASE_TEXTURE);
        this.bladeRenderType = RenderTypes.entitySolid(BLADE_TEXTURE);
    }

    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Reference.MOD_ID, "saw"), Unbaked.MAP_CODEC);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        renderBase(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);

        renderAxle(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        renderMace(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(165.0F));
        renderMace(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(285.0F));
        renderMace(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);
        poseStack.popPose();

        renderBlade(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);

        poseStack.popPose();
    }

    private void renderBase(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        collector.submitModelPart(this.baseModel.plinth, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.base, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
    }

    private void renderAxle(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        collector.submitModelPart(this.baseModel.axle, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.axle2, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.axleTop, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
    }

    private void renderMace(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        collector.submitModelPart(this.baseModel.maceBase, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.maceArm, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.mace1, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.mace2, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.mace3, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.baseModel.mace4, poseStack, this.baseRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
    }

    private void renderBlade(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.2D, -0.16D);
        poseStack.mulPose(Axis.XP.rotationDegrees(8.0F));
        renderBladeParts(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.16D);
        poseStack.mulPose(Axis.XP.rotationDegrees(-8.0F));
        renderBladeParts(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0D, -0.2D, -0.16D);
        poseStack.mulPose(Axis.XP.rotationDegrees(8.0F));
        renderBladeParts(poseStack, collector, lightCoords, overlayCoords, hasFoil, outlineColor);
        poseStack.popPose();
    }

    private void renderBladeParts(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        collector.submitModelPart(this.bladeModel.main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.back, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.front, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.left, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.right, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);

        collector.submitModelPart(this.bladeModel.tooth1Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth2Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth3Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth4Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth5Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth6Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth7Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth8Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth9Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth10Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth11Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth12Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth13Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth14Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth15Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth16Main, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);

        collector.submitModelPart(this.bladeModel.tooth1End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth2End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth3End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth4End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth5End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth6End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth7End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth8End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth9End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth10End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth11End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth12End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth13End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth14End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth15End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
        collector.submitModelPart(this.bladeModel.tooth16End, poseStack, this.bladeRenderType, lightCoords, overlayCoords, null, false, hasFoil, -1, null, outlineColor);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.baseModel.root().getExtentsForGui(poseStack, output);
        this.bladeModel.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SawSpecialItemRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new SawSpecialItemRenderer(context.entityModelSet(), context.sprites());
        }
    }
}

