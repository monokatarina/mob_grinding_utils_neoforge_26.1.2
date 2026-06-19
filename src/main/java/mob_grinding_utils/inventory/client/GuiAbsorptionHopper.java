package mob_grinding_utils.inventory.client;

import mob_grinding_utils.inventory.server.ContainerAbsorptionHopper;
import mob_grinding_utils.network.BEGuiClick;
import mob_grinding_utils.tile.TileEntityAbsorptionHopper;
import mob_grinding_utils.util.RL;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class GuiAbsorptionHopper extends MGUScreen<ContainerAbsorptionHopper> {
    private static final String GUI_ROOT = "textures/gui/absorption_hopper/";
    private static final Identifier BG = RL.mgu(GUI_ROOT + "absorption_hopper_bg.png");
    private static final Identifier BUTTON_SMALL = RL.mgu(GUI_ROOT + "absorption_hopper_button_small.png");
    private static final Identifier BUTTON_MEDIUM = RL.mgu(GUI_ROOT + "absorption_hopper_button_medium.png");
    private static final Identifier BUTTON_LARGE = RL.mgu(GUI_ROOT + "absorption_hopper_button_large.png");
    private static final Identifier TANK_GAUGE = RL.mgu(GUI_ROOT + "absorption_hopper_tank_gauge.png");

    protected final ContainerAbsorptionHopper container;
    private final TileEntityAbsorptionHopper tile;
    private TankGauge tankGauge;

    public GuiAbsorptionHopper(ContainerAbsorptionHopper container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, BG, 248, 226);
        this.container = container;
        this.tile = this.container.hopper;
    }

    @Override
    protected void init() {
        super.init();
        clearWidgets();

        Button.OnPress message = button -> {
            int id = ((GuiTextureButton) button).getId();
            applyLocalClick(id);
            ClientPacketDistributor.sendToServer(new BEGuiClick(tile.getBlockPos(), id));
        };

        addRenderableWidget(new GuiTextureButton(leftPos + 7, topPos + 17, 32, 16, BUTTON_MEDIUM, 0, 0, 31, 16, 0, Component.literal("Down"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 7, topPos + 34, 32, 16, BUTTON_MEDIUM, 0, 0, 31, 16, 1, Component.literal("Up"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 7, topPos + 51, 32, 16, BUTTON_MEDIUM, 0, 0, 31, 16, 2, Component.literal("North"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 82, topPos + 17, 32, 16, BUTTON_MEDIUM, 0, 0, 31, 16, 3, Component.literal("South"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 82, topPos + 34, 32, 16, BUTTON_MEDIUM, 0, 0, 31, 16, 4, Component.literal("West"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 82, topPos + 51, 32, 16, BUTTON_MEDIUM, 0, 0, 31, 16, 5, Component.literal("East"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 173, topPos + 113, 68, 16, BUTTON_LARGE, 0, 0, 68, 16, 6, Component.empty(), button -> {
            applyLocalClick(6);
            ClientPacketDistributor.sendToServer(new BEGuiClick(tile.getBlockPos(), 6));
        }));
        addRenderableWidget(new GuiTextureButton(leftPos + 173, topPos + 25, 16, 16, BUTTON_SMALL, 0, 0, 16, 16, 7, Component.literal("-"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 225, topPos + 25, 16, 16, BUTTON_SMALL, 0, 0, 16, 16, 8, Component.literal("+"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 173, topPos + 59, 16, 16, BUTTON_SMALL, 0, 0, 16, 16, 9, Component.literal("-"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 225, topPos + 59, 16, 16, BUTTON_SMALL, 0, 0, 16, 16, 10, Component.literal("+"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 173, topPos + 93, 16, 16, BUTTON_SMALL, 0, 0, 16, 16, 11, Component.literal("-"), message));
        addRenderableWidget(new GuiTextureButton(leftPos + 225, topPos + 93, 16, 16, BUTTON_SMALL, 0, 0, 16, 16, 12, Component.literal("+"), message));

        tankGauge = new TankGauge(leftPos + 156, topPos + 8, 6, 120, tile.getTank(null), TANK_GAUGE);
        addRenderableWidget(tankGauge);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, getTitle(), 8, 6, 4210752, false);

        graphics.text(font, Component.translatable("block.mob_grinding_utils.absorption_hopper_d_u"), 174, 14, 4210752, false);
        graphics.text(font, Component.translatable("block.mob_grinding_utils.absorption_hopper_n_s"), 174, 48, 4210752, false);
        graphics.text(font, Component.translatable("block.mob_grinding_utils.absorption_hopper_w_e"), 174, 82, 4210752, false);

        String areaLabel = !tile.showRenderBox ? "Show Area" : "Hide Area";
        graphics.text(font, areaLabel, 207 - font.width(areaLabel) / 2, 117, opaqueColor(14737632), true);

        TileEntityAbsorptionHopper.EnumStatus DOWN = tile.getSideStatus(Direction.DOWN);
        TileEntityAbsorptionHopper.EnumStatus UP = tile.getSideStatus(Direction.UP);
        TileEntityAbsorptionHopper.EnumStatus NORTH = tile.getSideStatus(Direction.NORTH);
        TileEntityAbsorptionHopper.EnumStatus SOUTH = tile.getSideStatus(Direction.SOUTH);
        TileEntityAbsorptionHopper.EnumStatus WEST = tile.getSideStatus(Direction.WEST);
        TileEntityAbsorptionHopper.EnumStatus EAST = tile.getSideStatus(Direction.EAST);

        graphics.text(font, Component.literal(DOWN.getSerializedName()), 58 - font.width(DOWN.getSerializedName()) / 2, 21, opaqueColor(getModeColour(DOWN.ordinal())), true);
        graphics.text(font, Component.literal(UP.getSerializedName()), 58 - font.width(UP.getSerializedName()) / 2, 38, opaqueColor(getModeColour(UP.ordinal())), true);
        graphics.text(font, Component.literal(NORTH.getSerializedName()), 58 - font.width(NORTH.getSerializedName()) / 2, 55, opaqueColor(getModeColour(NORTH.ordinal())), true);
        graphics.text(font, Component.literal(SOUTH.getSerializedName()), 133 - font.width(SOUTH.getSerializedName()) / 2, 21, opaqueColor(getModeColour(SOUTH.ordinal())), true);
        graphics.text(font, Component.literal(WEST.getSerializedName()), 133 - font.width(WEST.getSerializedName()) / 2, 38, opaqueColor(getModeColour(WEST.ordinal())), true);
        graphics.text(font, Component.literal(EAST.getSerializedName()), 133 - font.width(EAST.getSerializedName()) / 2, 55, opaqueColor(getModeColour(EAST.ordinal())), true);
        graphics.text(font, Component.literal(String.valueOf(tile.getoffsetY())), 207 - font.width(String.valueOf(tile.getoffsetY())) / 2, 29, opaqueColor(5285857), true);
        graphics.text(font, Component.literal(String.valueOf(tile.getoffsetZ())), 207 - font.width(String.valueOf(tile.getoffsetZ())) / 2, 63, opaqueColor(5285857), true);
        graphics.text(font, Component.literal(String.valueOf(tile.getoffsetX())), 207 - font.width(String.valueOf(tile.getoffsetX())) / 2, 97, opaqueColor(5285857), true);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void extractBackground(GuiGraphicsExtractor graphics) {
        graphics.blit(
                net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                TANK_GAUGE,
                leftPos + 156,
                topPos + 8,
                0.0F,
                0.0F,
                6,
                120,
                6,
                120,
                6,
                120,
                0xFFFFFFFF
        );
    }

    public int getModeColour(int index) {
        return switch (index) {
            case 0 -> 16711680;
            case 1 -> 5285857;
            case 2 -> 16776960;
            default -> 16776960;
        };
    }

    private int opaqueColor(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    private void applyLocalClick(int buttonID) {
        switch (buttonID) {
            case 0, 1, 2, 3, 4, 5 -> tile.toggleMode(Direction.values()[buttonID]);
            case 6 -> tile.toggleRenderBox();
            case 7, 8, 9, 10, 11, 12 -> tile.toggleOffset(buttonID);
        }
    }
}

