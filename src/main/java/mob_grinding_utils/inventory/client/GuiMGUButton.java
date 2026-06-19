package mob_grinding_utils.inventory.client;

import mob_grinding_utils.util.RL;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GuiMGUButton extends Button {

    private static final Identifier TEXTURES =
            RL.mgu("textures/gui/absorption_hopper_gui.png");

    private static final Identifier SOLIDIFIER_TEXTURES =
            RL.mgu("textures/gui/solidifier_gui.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private final Size size;
    private final int id;

    public GuiMGUButton(
            int x,
            int y,
            Size size,
            int id,
            Component title,
            OnPress pressedAction
    ) {
        super(
                x,
                y,
                size.width,
                size.height,
                title,
                pressedAction,
                DEFAULT_NARRATION
        );

        this.size = size;
        this.id = id;
    }

    @Override
    protected void extractContents(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        Identifier texture = getTexture();

        /*
         * Nas três variantes normais, a textura destacada fica
         * logo abaixo da textura normal.
         */
        int textureV = size.v;

        if (isHoveredOrFocused()) {
            textureV += size.height;
        }

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                getX(),
                getY(),
                size.u,
                textureV,
                getWidth(),
                getHeight(),
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        if (!getMessage().getString().isEmpty()) {
            extractDefaultLabel(
                    graphics.textRendererForWidget(
                            this,
                            GuiGraphicsExtractor.HoveredTextEffects.NONE
                    )
            );
        }
    }

    public Identifier getTexture() {
        return switch (size) {
            case SMALL, MEDIUM, LARGE -> TEXTURES;
            case SOLIDIFIER, SOLIDIFIER_ON -> SOLIDIFIER_TEXTURES;
        };
    }

    public Size getSize() {
        return size;
    }

    public int getId() {
        return id;
    }

    public enum Size {
        SMALL(16, 16, 103, 228),
        MEDIUM(32, 16, 0, 228),
        LARGE(68, 16, 33, 228),
        SOLIDIFIER(34, 16, 178, 92),
        SOLIDIFIER_ON(20, 16, 178, 110);

        public final int width;
        public final int height;
        public final int u;
        public final int v;

        Size(
                int width,
                int height,
                int u,
                int v
        ) {
            this.width = width;
            this.height = height;
            this.u = u;
            this.v = v;
        }
    }
}
