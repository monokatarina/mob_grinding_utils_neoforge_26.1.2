package mob_grinding_utils.events;

import mob_grinding_utils.ModItems;
import mob_grinding_utils.ModSounds;
import mob_grinding_utils.network.ChickenSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChickenFuseEvent {

    public static ItemStack getSpawnEgg(EntityType<?> entityType) {
        return SpawnEggItem.byId(entityType)
                .map(holder -> new ItemStack(holder.value()))
                .orElse(ItemStack.EMPTY);
    }

    @SubscribeEvent
    public void startChickenFuse(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Chicken chicken)) {
            return;
        }

        Level world = chicken.level();
        if (world.isClientSide()) {
            return;
        }

        CompoundTag nbt = chicken.getPersistentData();
        if (!nbt.contains("shouldExplode")) {
            return;
        }

        int startTime = nbt.getInt("countDown").orElse(0);
        if (startTime <= 19) {
            nbt.putInt("countDown", startTime + 1);
            PacketDistributor.sendToAllPlayers(new ChickenSyncPacket(chicken, nbt));
            return;
        }

        nbt.getString("mguMobName").ifPresent(name -> EntityType.byString(name).ifPresent(mob -> {
            ItemStack eggItem = getSpawnEgg(mob);
            if (!eggItem.isEmpty()) {
                chicken.spawnAtLocation((ServerLevel) world, eggItem);
            }
        }));

        if (nbt.getBoolean("nutritious").orElse(false)) {
            chicken.spawnAtLocation((ServerLevel) world, new ItemStack(ModItems.GOLDEN_EGG.get()));
        }

        if (nbt.getBoolean("cursed").orElse(false)) {
            chicken.spawnAtLocation((ServerLevel) world, new ItemStack(ModItems.ROTTEN_EGG.get()));
            chicken.playSound(ModSounds.SPOOPY_CHANGE.get(), 1F, 1F);
        } else {
            chicken.playSound(ModSounds.CHICKEN_RISE.get(), 0.5F, 1F);
        }

        for (int k = 0; k < 4; ++k) {
            ItemStack stack = new ItemStack(Items.FEATHER);
            ItemEntity feather = new ItemEntity(
                    world,
                    chicken.getX() + (world.getRandom().nextFloat() * chicken.getBbWidth() * 2.0F) - chicken.getBbWidth(),
                    chicken.getY() + (world.getRandom().nextFloat() * chicken.getBbHeight()),
                    chicken.getZ() + (world.getRandom().nextFloat() * chicken.getBbWidth() * 2.0F) - chicken.getBbWidth(),
                    stack
            );
            world.addFreshEntity(feather);
        }

        chicken.remove(Entity.RemovalReason.DISCARDED);
    }
}

