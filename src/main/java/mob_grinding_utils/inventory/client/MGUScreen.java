package mob_grinding_utils.inventory.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nonnull;

public abstract class MGUScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final Identifier TEX;

    protected MGUScreen(
            T container,
            Inventory inventory,
            Component title,
            Identifier texture,
            int imageWidth,
            int imageHeight
    ) {
        super(container, inventory, title, imageWidth, imageHeight);
        this.TEX = texture;
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEX,
                leftPos,
                topPos,
                0.0F,
                0.0F,
                imageWidth,
                imageHeight,
                256,
                256,
                256,
                256,
                0xFFFFFFFF
        );

        extractBackground(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }

    protected void extractBackground(GuiGraphicsExtractor graphics) {
    }

    @Override
    protected void extractLabels(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY
    ) {
    }

    protected void drawCenteredString(
            GuiGraphicsExtractor graphics,
            Component text,
            int x,
            int y,
            int color
    ) {
        String value = text.getString();
        graphics.text(
                font,
                text,
                x - font.width(value) / 2,
                y,
                color,
                false
        );
    }
}

