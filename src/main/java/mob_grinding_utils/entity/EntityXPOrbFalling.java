package mob_grinding_utils.entity;

import mob_grinding_utils.tile.TileEntitySinkTank;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import java.util.Optional;

public class EntityXPOrbFalling extends ExperienceOrb {

	private int fallingAge;
	public int delayBeforeCanPickup;

	public EntityXPOrbFalling(
			Level level,
			double x,
			double y,
			double z,
			int expValue
	) {
		super(level, x, y, z, expValue);

		setYRot((float) (Math.random() * 360.0D));
		setDeltaMovement(0.0D, 0.0D, 0.0D);
	}

	@Override
	public void tick() {
		/*
		 * Não chamamos super.tick(), porque o comportamento padrão do
		 * ExperienceOrb faz o orbe seguir o jogador. Este orbe deve apenas cair.
		 */
		if (delayBeforeCanPickup > 0) {
			delayBeforeCanPickup--;
		}

		xo = getX();
		yo = getY();
		zo = getZ();

		setDeltaMovement(
				getDeltaMovement().add(
						0.0D,
						-0.03D,
						0.0D
				)
		);

		if (!level().noCollision(getBoundingBox())) {
			moveTowardsClosestSpace(
					getX(),
					(getBoundingBox().minY
							+ getBoundingBox().maxY) / 2.0D,
					getZ()
			);
		}

		move(
				MoverType.SELF,
				getDeltaMovement()
		);

		if (onGround()) {
			setDeltaMovement(
					getDeltaMovement().multiply(
							1.0D,
							-0.9D,
							1.0D
					)
			);
		}

		tickCount++;
		fallingAge++;

		if (fallingAge >= 6000) {
			discard();
		}
	}

	@Override
	public void playerTouch(Player player) {
		if (level().isClientSide()) {
			return;
		}

		if (delayBeforeCanPickup != 0
				|| player.takeXpDelay != 0) {
			return;
		}

		if (NeoForge.EVENT_BUS.post(
				new PlayerXpEvent.PickupXp(
						player,
						this
				)
		).isCanceled()) {
			return;
		}

		player.takeXpDelay = 2;
		player.take(this, 1);

		int remainingXp = getValue();

		Optional<EnchantedItemInUse> enchantedItem =
				EnchantmentHelper.getRandomItemWith(
						EnchantmentEffectComponents.REPAIR_WITH_XP,
						player,
						ItemStack::isDamaged
				);

		if (enchantedItem.isPresent()) {
			ItemStack itemStack =
					enchantedItem.get().itemStack();

			if (!itemStack.isEmpty()
					&& itemStack.isDamaged()) {
				int durabilityToRepair = Math.min(
						(int) (
								remainingXp
										* itemStack.getXpRepairRatio()
						),
						itemStack.getDamageValue()
				);

				remainingXp -= durabilityToXp(
						durabilityToRepair
				);

				itemStack.setDamageValue(
						itemStack.getDamageValue()
								- durabilityToRepair
				);
			}
		}

		if (remainingXp > 0) {
			TileEntitySinkTank.addPlayerXP(
					player,
					remainingXp
			);
		}

		discard();
	}

	private int durabilityToXp(int durability) {
		return durability / 2;
	}
}

