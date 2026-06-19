package mob_grinding_utils.inventory.client;

import mob_grinding_utils.inventory.server.ContainerFan;
import mob_grinding_utils.network.BEGuiClick;
import mob_grinding_utils.tile.TileEntityFan;
import mob_grinding_utils.util.RL;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
public class GuiFan extends MGUScreen<ContainerFan> {
    private static final String GUI_ROOT = "textures/gui/absorption_hopper/";
    private static final net.minecraft.resources.Identifier BUTTON_LARGE = RL.mgu(GUI_ROOT + "absorption_hopper_button_large.png");

    protected final ContainerFan container;
    private final TileEntityFan tile;

    public GuiFan(
            ContainerFan container,
            Inventory inventory,
            Component title
    ) {
        super(
                container,
                inventory,
                title,
                RL.mgu("textures/gui/fan_gui.png"),
                176,
                150
        );

        this.container = container;
        this.tile = container.fan;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(
                new GuiTextureButton(
                        leftPos + 54,
                        topPos + 42,
                        68,
                        16,
                        BUTTON_LARGE,
                        0,
                        0,
                        68,
                        16,
                        0,
                        Component.empty(),
                        ignored -> {
                            tile.showRenderBox = !tile.showRenderBox;
                            ClientPacketDistributor.sendToServer(
                                    new BEGuiClick(
                                            tile.getBlockPos(),
                                            0
                                    )
                            );
                        }
                )
        );
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        String label = !tile.showRenderBox ? "Show Area" : "Hide Area";
        graphics.text(font, Component.literal(label), 88 - font.width(label) / 2, 46, opaqueColor(14737632), true);
    }

    private int opaqueColor(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }
}

