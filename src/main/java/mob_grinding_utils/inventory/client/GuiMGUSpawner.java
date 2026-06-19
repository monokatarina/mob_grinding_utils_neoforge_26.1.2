package mob_grinding_utils.inventory.client;

import mob_grinding_utils.inventory.server.ContainerMGUSpawner;
import mob_grinding_utils.tile.TileEntityMGUSpawner;
import mob_grinding_utils.util.RL;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiMGUSpawner extends MGUScreen<ContainerMGUSpawner> {
    protected final ContainerMGUSpawner container;
    private final TileEntityMGUSpawner tile;

    public GuiMGUSpawner(ContainerMGUSpawner container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name, RL.mgu("textures/gui/entity_spawner_gui.png"), 176, 226);
        this.container = container;
        this.tile = this.container.tile;
    }

    @Override
    public void init() {
        super.init();
        clearWidgets();

        Button.OnPress message = button -> {};

        addRenderableWidget(new GuiMGUButton(leftPos + 101, topPos + 113, GuiMGUButton.Size.LARGE, 0, Component.empty(), button -> tile.showRenderBox = !tile.showRenderBox));

        addRenderableWidget(new GuiMGUButton(leftPos + 101, topPos + 25, GuiMGUButton.Size.SMALL, 1, Component.literal("-"), message));
        addRenderableWidget(new GuiMGUButton(leftPos + 153, topPos + 25, GuiMGUButton.Size.SMALL, 2, Component.literal("+"), message));
        addRenderableWidget(new GuiMGUButton(leftPos + 101, topPos + 59, GuiMGUButton.Size.SMALL, 3, Component.literal("-"), message));
        addRenderableWidget(new GuiMGUButton(leftPos + 153, topPos + 59, GuiMGUButton.Size.SMALL, 4, Component.literal("+"), message));
        addRenderableWidget(new GuiMGUButton(leftPos + 101, topPos + 93, GuiMGUButton.Size.SMALL, 5, Component.literal("-"), message));
        addRenderableWidget(new GuiMGUButton(leftPos + 153, topPos + 93, GuiMGUButton.Size.SMALL, 6, Component.literal("+"), message));
    }

    @Override
    protected void extractBackground(GuiGraphicsExtractor graphics) {
        int progress = tile.getProgressScaled(28);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEX, leftPos + 44, topPos + 71 - progress, 178.0F, 28.0F - progress, 16, 28, 256, 256, 256, 256, 0xFFFFFFFF);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 8, imageHeight - 220, 4210752, false);

        graphics.text(font, Component.translatable("block.mob_grinding_utils.absorption_hopper_d_u"), 102, 14, 4210752, false);
        graphics.text(font, Component.translatable("block.mob_grinding_utils.absorption_hopper_n_s"), 102, 48, 4210752, false);
        graphics.text(font, Component.translatable("block.mob_grinding_utils.absorption_hopper_w_e"), 102, 82, 4210752, false);

        drawCenteredString(graphics, Component.literal(!tile.showRenderBox ? "Show Area" : "Hide Area"), 135, 117, 14737632);

        if (tile.getProgress() > 0) {
            drawCenteredString(graphics, Component.literal("Attempting Spawn"), 52, 98, 4210752);
        }

        drawCenteredString(graphics, Component.literal(String.valueOf(tile.getoffsetY())), 135, 29, 5285857);
        drawCenteredString(graphics, Component.literal(String.valueOf(tile.getoffsetZ())), 135, 63, 5285857);
        drawCenteredString(graphics, Component.literal(String.valueOf(tile.getoffsetX())), 135, 97, 5285857);
    }
}

