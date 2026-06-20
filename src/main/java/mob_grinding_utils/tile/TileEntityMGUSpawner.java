package mob_grinding_utils.tile;

import io.netty.buffer.Unpooled;
import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.ModTags;
import mob_grinding_utils.inventory.server.ContainerMGUSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityMGUSpawner extends BlockEntity implements MenuProvider, BEGuiClickable {

	public int spawning_progress = 0;
	public int MAX_SPAWNING_TIME = 100;
	public boolean isOn = false;

	public final ItemStacksResourceHandler inputSlots =
			new ItemStacksResourceHandler(4) {
				@Override
				protected void onContentsChanged(int index, ItemStack previousContents) {
					TileEntityMGUSpawner.this.setChanged();
				}
			};

	public final ItemStacksResourceHandler fuelSlot =
			new ItemStacksResourceHandler(1) {
				@Override
				protected void onContentsChanged(int index, ItemStack previousContents) {
					TileEntityMGUSpawner.this.setChanged();
				}
			};

	public int animationTicks;
	public int prevAnimationTicks;
	public boolean showRenderBox;
	public int offsetX;
	public int offsetY;
	public int offsetZ;

	public TileEntityMGUSpawner(BlockPos pos, BlockState state) {
		super(ModBlocks.ENTITY_SPAWNER.getTileEntityType(), pos, state);
	}

	public void toggleOnOff() {
		isOn = !isOn;
	}

	public static <T extends BlockEntity> void serverTick(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			T blockEntity
	) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!(blockEntity instanceof TileEntityMGUSpawner tile)) {
			return;
		}

		if (!tile.isOn || !tile.canOperate()) {
			if (tile.getProgress() > 0) {
				tile.setProgress(0);
			}
			return;
		}

		tile.setProgress(
				tile.getProgress() + 1 + tile.getSpeedModifierAmount()
		);

		if (tile.getProgress() >= tile.MAX_SPAWNING_TIME) {
			if (tile.spawnMobInArea(serverLevel)) {
				tile.consumeFuel();
			}

			tile.setProgress(0);
		}
	}

	public static <T extends BlockEntity> void clientTick(
			Level level,
			BlockPos blockPos,
			BlockState blockState,
			T blockEntity
	) {
		if (!(blockEntity instanceof TileEntityMGUSpawner tile)) {
			return;
		}

		if (!tile.isOn) {
			tile.prevAnimationTicks = 0;
			tile.animationTicks = 0;
			return;
		}

		tile.prevAnimationTicks = tile.animationTicks;

		if (tile.animationTicks < 360) {
			tile.animationTicks += 9;
		}

		if (tile.animationTicks >= 360) {
			tile.animationTicks -= 360;
			tile.prevAnimationTicks -= 360;
		}
	}

	private ItemStack getInputStack(int slot) {
		return ItemUtil.getStack(inputSlots, slot);
	}

	private ItemStack getFuelStack() {
		return ItemUtil.getStack(fuelSlot, 0);
	}

	private void consumeFuel() {
		if (fuelSlot.getAmountAsInt(0) <= 0) {
			return;
		}

		ItemResource resource = fuelSlot.getResource(0);

		try (Transaction transaction = Transaction.openRoot()) {
			int extracted = fuelSlot.extract(
					0,
					resource,
					1,
					transaction
			);

			if (extracted == 1) {
				transaction.commit();
			}
		}
	}

	private boolean spawnMobInArea(ServerLevel level) {
		ItemStack eggStack = getInputStack(0);

		if (!(eggStack.getItem() instanceof SpawnEggItem)) {
			return false;
		}

		EntityType<?> type = SpawnEggItem.getType(eggStack);

		if (type == null) {
			return false;
		}

		if (BuiltInRegistries.ENTITY_TYPE
				.wrapAsHolder(type)
				.is(ModTags.Entities.NO_SPAWN)) {
			return false;
		}

		AABB area = getAABBWithModifiers();

		int minX = Mth.floor(area.minX);
		int maxX = Mth.floor(area.maxX);
		int minY = Mth.floor(area.minY);
		int maxY = Mth.floor(area.maxY);
		int minZ = Mth.floor(area.minZ);
		int maxZ = Mth.floor(area.maxZ);

		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

		Entity createdEntity = type.create(
				level,
				EntitySpawnReason.SPAWNER
		);

		if (!(createdEntity instanceof Mob entity)) {
			return false;
		}

		List<BlockPos> validPositions = new ArrayList<>();

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					mutablePos.set(x, y, z);

					entity.setPos(
							mutablePos.getX() + 0.5D,
							mutablePos.getY(),
							mutablePos.getZ() + 0.5D
					);

					if (isValidSpawnLocation(level, entity)) {
						validPositions.add(mutablePos.immutable());
					}
				}
			}
		}

		if (validPositions.isEmpty()) {
			return false;
		}

		Collections.shuffle(validPositions);
		BlockPos spawnPos = validPositions.getFirst();

		entity.setPos(
				spawnPos.getX() + 0.5D,
				spawnPos.getY(),
				spawnPos.getZ() + 0.5D
		);

		EventHooks.finalizeMobSpawn(
				entity,
				level,
				level.getCurrentDifficultyAt(spawnPos),
				EntitySpawnReason.SPAWNER,
				null
		);

		level.addFreshEntity(entity);
		return true;
	}

	public boolean isValidSpawnLocation(ServerLevel level, Mob entity) {
		return EventHooks.checkSpawnPosition(
				entity,
				level,
				EntitySpawnReason.SPAWNER
		)
				&& level.getEntities(
				entity.getType(),
				entity.getBoundingBox(),
				EntitySelector.ENTITY_STILL_ALIVE
		).isEmpty()
				&& level.noCollision(entity);
	}

	public void toggleRenderBox() {
		showRenderBox = !showRenderBox;
		setChanged();
	}

	public void toggleOffset(int direction) {
		switch (direction) {
			case 1 -> {
				if (getoffsetY() >= -1 - getHeightModifierAmount()) {
					offsetY = getoffsetY() - 1;
				}
			}
			case 2 -> {
				if (getoffsetY() <= 1 + getHeightModifierAmount()) {
					offsetY = getoffsetY() + 1;
				}
			}
			case 3 -> {
				if (getoffsetZ() >= -1 - getWidthModifierAmount()) {
					offsetZ = getoffsetZ() - 1;
				}
			}
			case 4 -> {
				if (getoffsetZ() <= 1 + getWidthModifierAmount()) {
					offsetZ = getoffsetZ() + 1;
				}
			}
			case 5 -> {
				if (getoffsetX() >= -1 - getWidthModifierAmount()) {
					offsetX = getoffsetX() - 1;
				}
			}
			case 6 -> {
				if (getoffsetX() <= 1 + getWidthModifierAmount()) {
					offsetX = getoffsetX() + 1;
				}
			}
			default -> {
			}
		}

		setChanged();
	}
	public int getProgressScaled(int count) {
		return getProgress() * count / MAX_SPAWNING_TIME;
	}

	private boolean canOperate() {
		return hasSpawnEggItem() && hasFuel();
	}

	public boolean hasSpawnEggItem() {
		ItemStack stack = getInputStack(0);

		return !stack.isEmpty()
				&& stack.getItem() instanceof SpawnEggItem;
	}

	private boolean hasFuel() {
		ItemStack stack = getFuelStack();

		return !stack.isEmpty()
				&& stack.getItem() == ModItems.SOLID_XP_BABY.get();
	}
	public ResourceHandler<ItemResource> getFuelSlot(
			@Nullable Direction side
	) {
		return fuelSlot;
	}

	private boolean hasWidthUpgrade() {
		ItemStack stack = getInputStack(1);

		return !stack.isEmpty()
				&& stack.getItem() == ModItems.SPAWNER_UPGRADE_WIDTH.get();
	}

	public int getWidthModifierAmount() {
		return hasWidthUpgrade()
				? inputSlots.getAmountAsInt(1)
				: 0;
	}

	private boolean hasHeightUpgrade() {
		ItemStack stack = getInputStack(2);

		return !stack.isEmpty()
				&& stack.getItem() == ModItems.SPAWNER_UPGRADE_HEIGHT.get();
	}

	public int getHeightModifierAmount() {
		return hasHeightUpgrade()
				? inputSlots.getAmountAsInt(2)
				: 0;
	}

	private boolean hasSpeedUpgrade() {
		ItemStack stack = getInputStack(3);

		return !stack.isEmpty()
				&& stack.getItem() == ModItems.XP_SOLIDIFIER_UPGRADE.get();
	}

	public int getSpeedModifierAmount() {
		return hasSpeedUpgrade()
				? inputSlots.getAmountAsInt(3)
				: 0;
	}

	public AABB getAABBWithModifiers() {
		double x = getBlockPos().getX() + 0.5D;
		double y = getBlockPos().getY() + 0.5D;
		double z = getBlockPos().getZ() + 0.5D;

		return new AABB(
				x - 1.5D - getWidthModifierAmount(),
				y - 0.5D - getHeightModifierAmount(),
				z - 1.5D - getWidthModifierAmount(),
				x + 1.5D + getWidthModifierAmount(),
				y + 0.5D + getHeightModifierAmount(),
				z + 1.5D + getWidthModifierAmount()
		).move(getoffsetX(), getoffsetY(), getoffsetZ());
	}
	public AABB getAABBForRender() {
		return new AABB(
				-1D - getWidthModifierAmount(),
				-getHeightModifierAmount(),
				-1D - getWidthModifierAmount(),
				2D + getWidthModifierAmount(),
				1D + getHeightModifierAmount(),
				2D + getWidthModifierAmount()
		).move(getoffsetX(), getoffsetY(), getoffsetZ());
	}

	public int getoffsetX() {
		return Math.max(
				-2 - getWidthModifierAmount(),
				Math.min(offsetX, 2 + getWidthModifierAmount())
		);
	}

	public int getoffsetY() {
		return Math.max(
				-1 - getHeightModifierAmount(),
				Math.min(offsetY, 1 + getHeightModifierAmount())
		);
	}

	public int getoffsetZ() {
		return Math.max(
				-2 - getWidthModifierAmount(),
				Math.min(offsetZ, 2 + getWidthModifierAmount())
		);
	}

	private void setProgress(int counter) {
		spawning_progress = counter;
		updateBlock();
	}

	public int getProgress() {
		return spawning_progress;
	}

	public void updateBlock() {
		Level level = getLevel();

		if (level == null) {
			return;
		}

		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 3);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(
				"block.mob_grinding_utils.entity_spawner"
		);
	}

	@Override
	public @Nullable AbstractContainerMenu createMenu(
			int windowID,
			Inventory playerInventory,
			Player player
	) {
		return new ContainerMGUSpawner(
				windowID,
				playerInventory,
				new FriendlyByteBuf(Unpooled.buffer())
						.writeBlockPos(worldPosition)
		);
	}
	public @Nullable Entity getEntityToRender() {
		if (!hasSpawnEggItem()) {
			return null;
		}

		Level level = getLevel();

		if (level == null) {
			return null;
		}

		ItemStack eggStack = getInputStack(0);
		EntityType<?> type = SpawnEggItem.getType(eggStack);

		if (type == null) {
			return null;
		}

		return type.create(
				level,
				EntitySpawnReason.SPAWN_ITEM_USE
		);
	}

	@Override
	public void buttonClicked(int buttonID) {
		switch (buttonID) {
			case 0 -> toggleRenderBox();
			case 1, 2, 3, 4, 5, 6 -> toggleOffset(buttonID);
			default -> {
			}
		}

		updateBlock();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("spawning_progress", spawning_progress);
		output.putBoolean("isOn", isOn);
		output.putBoolean("showRenderBox", showRenderBox);
		output.putInt("offsetX", offsetX);
		output.putInt("offsetY", offsetY);
		output.putInt("offsetZ", offsetZ);
		saveItemHandler(output, "InputSlots", inputSlots);
		saveItemHandler(output, "FuelSlot", fuelSlot);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		spawning_progress = input.getIntOr("spawning_progress", 0);
		isOn = input.getBooleanOr("isOn", false);
		showRenderBox = input.getBooleanOr("showRenderBox", false);
		offsetX = input.getIntOr("offsetX", 0);
		offsetY = input.getIntOr("offsetY", 0);
		offsetZ = input.getIntOr("offsetZ", 0);

		loadItemHandler(input, "InputSlots", "inputSlots", inputSlots);
		loadItemHandler(input, "FuelSlot", "fuelSlot", fuelSlot);
	}

	private static void saveItemHandler(ValueOutput output, String key, ItemStacksResourceHandler handler) {
		var list = output.list(key, ItemStackWithSlot.CODEC);
		for (int slot = 0; slot < handler.size(); slot++) {
			ItemStack stack = handler.getResource(slot).toStack((int) handler.getAmountAsLong(slot));
			if (!stack.isEmpty()) {
				list.add(new ItemStackWithSlot(slot, stack));
			}
		}

		if (list.isEmpty()) {
			output.discard(key);
		}
	}

	private static void loadItemHandler(ValueInput input, String key, String legacyKey, ItemStacksResourceHandler handler) {
		boolean loaded = false;
		for (ItemStackWithSlot item : input.listOrEmpty(key, ItemStackWithSlot.CODEC)) {
			if (item.isValidInContainer(handler.size()) && !item.stack().isEmpty()) {
				handler.set(item.slot(), ItemResource.of(item.stack()), item.stack().getCount());
				loaded = true;
			}
		}

		if (loaded) {
			return;
		}

		int index = 0;
		for (ItemStack stack : input.listOrEmpty(legacyKey, ItemStack.CODEC)) {
			if (index >= handler.size()) {
				break;
			}
			if (!stack.isEmpty()) {
				handler.set(index, ItemResource.of(stack), stack.getCount());
			}
			index++;
		}
	}
}

