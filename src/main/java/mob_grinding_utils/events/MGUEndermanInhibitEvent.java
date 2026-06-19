package mob_grinding_utils.events;

import mob_grinding_utils.blocks.BlockEnderInhibitorOff;
import mob_grinding_utils.blocks.BlockEnderInhibitorOn;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;

public class MGUEndermanInhibitEvent {

	@SuppressWarnings("resource")
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void teleportEvent(EntityTeleportEvent event) {
		if (event.getEntity().level().isClientSide()
				|| event instanceof EntityTeleportEvent.TeleportCommand
				|| event instanceof EntityTeleportEvent.SpreadPlayersCommand) {
			return;
		}

		if (event.getEntity() instanceof LivingEntity entity && getIsInhibited(entity)) {
			event.setCanceled(true);
		}
	}

	private boolean getIsInhibited(@Nonnull LivingEntity entity) {
		AABB box = entity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D);
		int minX = Mth.floor(box.minX);
		int maxX = Mth.floor(box.maxX);
		int minY = Mth.floor(box.minY);
		int maxY = Mth.floor(box.maxY);
		int minZ = Mth.floor(box.minZ);
		int maxZ = Mth.floor(box.maxZ);
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					BlockState state = entity.level().getBlockState(mutablePos.set(x, y, z));
					if (state.getBlock() instanceof BlockEnderInhibitorOn
							&& !(state.getBlock() instanceof BlockEnderInhibitorOff)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}

