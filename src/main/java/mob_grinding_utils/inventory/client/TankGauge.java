package mob_grinding_utils.inventory.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import mob_grinding_utils.util.RL;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TankGauge extends AbstractWidget {

    private static final Identifier FLUID_XP_TEXTURE = RL.mgu("textures/block/fluid_xp.png");

    private final ResourceHandler<FluidResource> tank;
    private final Identifier texture;

    public TankGauge(
            int x,
            int y,
            int width,
            int height,
            ResourceHandler<FluidResource> tank
    ) {
        this(x, y, width, height, tank, null);
    }

    public TankGauge(
            int x,
            int y,
            int width,
            int height,
            ResourceHandler<FluidResource> tank,
            Identifier texture
    ) {
        super(x, y, width, height, Component.empty());
        this.tank = tank;
        this.texture = texture;
    }

    public FluidResource getFluid() {
        if (tank.size() == 0) {
            return FluidResource.EMPTY;
        }

        return tank.getResource(0);
    }

    public int getFluidAmount() {
        if (tank.size() == 0) {
            return 0;
        }

        return tank.getAmountAsInt(0);
    }

    public int getFluidCapacity() {
        if (tank.size() == 0) {
            return 0;
        }

        FluidResource resource = tank.getResource(0);

        return tank.getCapacityAsInt(0, resource);
    }

    public int getScaledAmount() {
        int capacity = getFluidCapacity();

        if (capacity <= 0) {
            return 0;
        }

        return Math.min(height, getFluidAmount() * height / capacity);
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        FluidResource fluid = getFluid();
        int amount = getFluidAmount();
        int capacity = getFluidCapacity();
        int scaled = getScaledAmount();

        graphics.fill(
                getX(),
                getY(),
                getX() + width,
                getY() + height,
                0x66000000
        );

        if (!fluid.isEmpty() && amount > 0 && capacity > 0 && scaled > 0) {
            int top = getY() + (height - scaled);
            drawFluidTexture(graphics, getX() + 1, top, Math.max(1, width - 2), scaled);
        }

        if (texture != null) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    getX(),
                    getY(),
                    0.0F,
                    0.0F,
                    width,
                    height,
                    width,
                    height,
                    width,
                    height,
                    0xFFFFFFFF
            );
        }
    }

    @Override
    protected void updateWidgetNarration(
            NarrationElementOutput narrationElementOutput
    ) {
    }

    private void drawFluidTexture(GuiGraphicsExtractor graphics, int x, int y, int drawWidth, int drawHeight) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                FLUID_XP_TEXTURE,
                x,
                y,
                0.0F,
                0.0F,
                drawWidth,
                drawHeight,
                16,
                256
        );
    }
}

