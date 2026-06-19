package mob_grinding_utils.inventory.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GuiTextureButton extends Button {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final int id;

    public GuiTextureButton(
            int x,
            int y,
            int width,
            int height,
            Identifier texture,
            int u,
            int v,
            Component title,
            OnPress pressedAction
    ) {
        this(x, y, width, height, texture, u, v, width, height, -1, title, pressedAction);
    }

    public GuiTextureButton(
            int x,
            int y,
            int width,
            int height,
            Identifier texture,
            int u,
            int v,
            int textureWidth,
            int textureHeight,
            int id,
            Component title,
            OnPress pressedAction
    ) {
        super(x, y, width, height, title, pressedAction, DEFAULT_NARRATION);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.id = id;
    }

    @Override
    protected void extractContents(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                getX(),
                getY(),
                u,
                v,
                getWidth(),
                getHeight(),
                textureWidth,
                textureHeight
        );

        if (!isActive()) {
            graphics.fill(
                    getX(),
                    getY(),
                    getX() + getWidth(),
                    getY() + getHeight(),
                    0x88000000
                );
        } else if (isHoveredOrFocused()) {
            graphics.fill(
                    getX(),
                    getY(),
                    getX() + getWidth(),
                    getY() + getHeight(),
                    0x44FFFFFF
            );
        }

        if (!getMessage().getString().isEmpty()) {
            extractDefaultLabel(
                    graphics.textRendererForWidget(
                            this,
                            GuiGraphicsExtractor.HoveredTextEffects.NONE
                    )
            );
        }
    }

    public int getId() {
        return id;
    }
}

