package mob_grinding_utils.inventory.client;

import mob_grinding_utils.inventory.server.ContainerXPSolidifier;
import mob_grinding_utils.tile.TileEntityXPSolidifier;
import mob_grinding_utils.util.RL;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiXPSolidifier extends MGUScreen<ContainerXPSolidifier> {
    protected final ContainerXPSolidifier container;
    private final TileEntityXPSolidifier tile;
    private TankGauge tankGauge;

    public GuiXPSolidifier(ContainerXPSolidifier screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, RL.mgu("textures/gui/solidifier_gui.png"), 176, 186);
        container = screenContainer;
        tile = container.tile;
    }

    @Override
    protected void init() {
        super.init();
        tankGauge = new TankGauge(leftPos + 8, topPos + 18, 12, 70, tile.tank);
        addRenderableWidget(tankGauge);
        addRenderableWidget(new GuiMGUButton(leftPos + 62, topPos + 72, GuiMGUButton.Size.SOLIDIFIER, 0, Component.literal("Push"), button -> {}));
        addRenderableWidget(new GuiMGUButton(leftPos + 148, topPos + 8, GuiMGUButton.Size.SOLIDIFIER_ON, 0, Component.literal(""), button -> {}));
    }

    @Override
    protected void extractBackground(GuiGraphicsExtractor graphics) {
        graphics.text(
                font,
                Component.literal(tile.outputDirection.getSerializedName()),
                leftPos + 124 - font.width(tile.outputDirection.getSerializedName()) / 2,
                topPos + 76,
                5285857,
                false
        );

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEX, leftPos + 7, topPos + 17, 178.0F, 0.0F, 6, 71, 256, 256, 256, 256, 0xFFFFFFFF);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEX, leftPos + 91, topPos + 36, 178.0F, 73.0F, tile.getProgressScaled(24), 17, 256, 256, 256, 256, 0xFFFFFFFF);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, Component.translatable("block.mob_grinding_utils.xpsolidifier"), 7, 6, 0x404040, false);
        graphics.text(font, Component.translatable("container.inventory"), 8, this.imageHeight - 94, 4210752, false);
        drawCenteredString(graphics, Component.literal(tile.isOn ? "On" : "Off"), 158, 12, 14737632);
    }
}

