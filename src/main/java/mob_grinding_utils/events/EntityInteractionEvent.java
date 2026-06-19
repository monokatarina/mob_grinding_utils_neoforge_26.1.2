package mob_grinding_utils.events;

import mob_grinding_utils.ModItems;
import mob_grinding_utils.components.MGUComponents;
import mob_grinding_utils.items.ItemGMChickenFeed;
import mob_grinding_utils.network.ChickenSyncPacket;
import mob_grinding_utils.util.RL;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityInteractionEvent {

    @SubscribeEvent
    public void clickOnEntity(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Chicken chicken)) {
            return;
        }

        if (chicken.isBaby()) {
            return;
        }

        ItemStack eventItem = event.getItemStack();
        if (eventItem.isEmpty() || !(eventItem.getItem() instanceof ItemGMChickenFeed)) {
            return;
        }

        Level world = chicken.level();
        if (world.isClientSide()) {
            return;
        }

        CompoundTag nbt = chicken.getPersistentData();
        if (!nbt.contains("shouldExplode")) {
            chicken.setInvulnerable(true);
            nbt.putBoolean("shouldExplode", true);
            nbt.putInt("countDown", 0);

            if (eventItem.has(MGUComponents.MOB_DNA)) {
                nbt.putString(
                        "mguMobName",
                        eventItem.getOrDefault(MGUComponents.MOB_DNA, RL.mc("chicken")).toString()
                );
            }

            if (eventItem.getItem() == ModItems.GM_CHICKEN_FEED_CURSED.get()) {
                nbt.putBoolean("cursed", true);
            }

            if (eventItem.getItem() == ModItems.NUTRITIOUS_CHICKEN_FEED.get()) {
                nbt.putBoolean("nutritious", true);
            }

            PacketDistributor.sendToPlayersNear(
                    (ServerLevel) world,
                    null,
                    chicken.getX(),
                    chicken.getY(),
                    chicken.getZ(),
                    32,
                    new ChickenSyncPacket(chicken, nbt)
            );
        }

        Vec3 vec3d = chicken.getDeltaMovement();
        chicken.setDeltaMovement(vec3d.x, 0.06D, vec3d.z);
        chicken.setNoGravity(true);

        if (!event.getEntity().getAbilities().instabuild) {
            event.getItemStack().shrink(1);
        }
    }
}

