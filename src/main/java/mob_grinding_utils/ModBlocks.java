package mob_grinding_utils;


import com.google.common.collect.ImmutableSet;
import mob_grinding_utils.blocks.*;
import mob_grinding_utils.itemblocks.BlockItemTank;
import mob_grinding_utils.itemblocks.MGUBlockItem;
import mob_grinding_utils.tile.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.NullMarked;
import mob_grinding_utils.util.RL;

import java.util.Set;

public class ModBlocks {
	private static Block.Properties blockProps(String name, Block.Properties properties) {
		return properties.setId(ResourceKey.create(Registries.BLOCK, RL.mgu(name)));
	}

	private static Item.Properties itemProps(String name, Item.Properties properties) {
		return properties.setId(ResourceKey.create(Registries.ITEM, RL.mgu(name)));
	}

	public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Reference.MOD_ID);
	public static DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Reference.MOD_ID);
	public static DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Reference.MOD_ID);
	public static DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Reference.MOD_ID);

	public static MGUBlockReg<BlockFan, MGUBlockItem, TileEntityFan> FAN = new MGUBlockReg<>("fan",
		props -> new BlockFan(blockProps("fan", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.METAL))),
		(b, props) -> new MGUBlockItem(b, itemProps("fan", props)), TileEntityFan::new);

	public static MGUBlockReg<BlockSaw, MGUBlockItem, TileEntitySaw> SAW = new MGUBlockReg<>("saw",
		props -> new BlockSaw(blockProps("saw", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("saw", props)), TileEntitySaw::new);

	public static MGUBlockReg<BlockAbsorptionHopper, MGUBlockItem, TileEntityAbsorptionHopper> ABSORPTION_HOPPER = new MGUBlockReg<>("absorption_hopper",
		props -> new BlockAbsorptionHopper(blockProps("absorption_hopper", props.mapColor(MapColor.COLOR_BLACK).forceSolidOn().strength(1.0F, 2000.0F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("absorption_hopper", props)), TileEntityAbsorptionHopper::new);

	public static MGUBlockReg<BlockSpikes, MGUBlockItem, ?> SPIKES = new MGUBlockReg<>("spikes",
		props -> new BlockSpikes(blockProps("spikes", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("spikes", props)));

	public static MGUBlockReg<BlockTank, BlockItemTank, TileEntityTank> TANK = new MGUBlockReg<>("tank",
		props -> new BlockTank(blockProps("tank", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.GLASS).noOcclusion())),
		(b, props) -> new BlockItemTank(b, 32000, itemProps("tank", props)), TileEntityTank::new);

	public static MGUBlockReg<BlockTankSink, BlockItemTank, TileEntitySinkTank> TANK_SINK = new MGUBlockReg<>("tank_sink",
		props -> new BlockTankSink(blockProps("tank_sink", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.GLASS).noOcclusion())),
		(b, props) -> new BlockItemTank(b, 32000, itemProps("tank_sink", props)), TileEntitySinkTank::new);

	public static MGUBlockReg<BlockXPTap, MGUBlockItem, TileEntityXPTap> XP_TAP = new MGUBlockReg<>("xp_tap",
		props -> new BlockXPTap(blockProps("xp_tap", props.mapColor(MapColor.NONE).strength(1.0F, 2000.0F).forceSolidOn().sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("xp_tap", props)), TileEntityXPTap::new);

	public static MGUBlockReg<BlockWitherMuffler, MGUBlockItem, ?> WITHER_MUFFLER = new MGUBlockReg<>("wither_muffler",
		props -> new BlockWitherMuffler(blockProps("wither_muffler", props.mapColor(MapColor.COLOR_BLACK).strength(0.5F, 2000F).sound(SoundType.WOOL))),
		(b, props) -> new MGUBlockItem(b, itemProps("wither_muffler", props)));

	public static MGUBlockReg<BlockDragonMuffler, MGUBlockItem, ?> DRAGON_MUFFLER = new MGUBlockReg<>("dragon_muffler",
		props -> new BlockDragonMuffler(blockProps("dragon_muffler", props.mapColor(MapColor.COLOR_BLACK).strength(0.5F, 2000F).sound(SoundType.WOOL))),
		(b, props) -> new MGUBlockItem(b, itemProps("dragon_muffler", props)));

	public static MGUBlockReg<BlockDarkOakStone, MGUBlockItem, ?> DARK_OAK_STONE = new MGUBlockReg<>("dark_oak_stone",
		props -> new BlockDarkOakStone(blockProps("dark_oak_stone", props.mapColor(MapColor.COLOR_BROWN).strength(1.5F, 10F).sound(SoundType.STONE).lightLevel(bState -> 7))),
		(b, props) -> new MGUBlockItem(b, itemProps("dark_oak_stone", props)));

	public static MGUBlockReg<BlockEntityConveyor, MGUBlockItem, ?> ENTITY_CONVEYOR = new MGUBlockReg<>("entity_conveyor",
		props -> new BlockEntityConveyor(blockProps("entity_conveyor", props.mapColor(MapColor.COLOR_GRAY).strength(0.5F, 2000.0F).sound(SoundType.STONE).isValidSpawn((state, reader, pos, entitytype) -> true))),
		(b, props) -> new MGUBlockItem(b, itemProps("entity_conveyor", props)));

	public static MGUBlockReg<BlockEnderInhibitorOn, MGUBlockItem, ?> ENDER_INHIBITOR_ON = new MGUBlockReg<>("ender_inhibitor_on",
		props -> new BlockEnderInhibitorOn(blockProps("ender_inhibitor_on", props.mapColor(MapColor.COLOR_GRAY).forceSolidOn().strength(0.2F, 2000F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("ender_inhibitor_on", props)));

	public static MGUBlockReg<BlockEnderInhibitorOff, MGUBlockItem, ?> ENDER_INHIBITOR_OFF = new MGUBlockReg<>("ender_inhibitor_off",
		props -> new BlockEnderInhibitorOff(blockProps("ender_inhibitor_off", props.mapColor(MapColor.COLOR_GRAY).forceSolidOn().strength(0.2F, 2000F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("ender_inhibitor_off", props)));

	public static MGUBlockReg<BlockTintedGlass, MGUBlockItem, ?> TINTED_GLASS = new MGUBlockReg<>("tinted_glass",
		props -> new BlockTintedGlass(blockProps("tinted_glass", props.mapColor(MapColor.COLOR_BLACK).strength(1.0F, 2000.0F).sound(SoundType.GLASS).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("tinted_glass", props)));

	public static MGUBlockReg<BlockTankJumbo, BlockItemTank, TileEntityJumboTank> JUMBO_TANK = new MGUBlockReg<>("jumbo_tank",
		props -> new BlockTankJumbo(blockProps("jumbo_tank", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new BlockItemTank(b, 1024000, itemProps("jumbo_tank", props)), TileEntityJumboTank::new);

	public static MGUBlockReg<BlockXPSolidifier, MGUBlockItem, TileEntityXPSolidifier> XPSOLIDIFIER = new MGUBlockReg<>("xpsolidifier",
		props -> new BlockXPSolidifier(blockProps("xpsolidifier", props.mapColor(MapColor.COLOR_GRAY).strength(1.0F, 2000.0F).sound(SoundType.METAL).noOcclusion())),
		(b, props) -> new MGUBlockItem(b, itemProps("xpsolidifier", props)), TileEntityXPSolidifier::new);

	//public static Material MATERIAL_DREADFUL_DIRT = new Material(MaterialColor.DIRT, false, true, false, true, true, false, PushReaction.NORMAL);
	public static MGUBlockReg<BlockDreadfulDirt, MGUBlockItem, ?> DREADFUL_DIRT = new MGUBlockReg<>("dreadful_dirt",
		props -> new BlockDreadfulDirt(blockProps("dreadful_dirt", props.mapColor(MapColor.COLOR_PURPLE).strength(1.0F, 2000.0F).sound(SoundType.GRAVEL).randomTicks()
				.isValidSpawn((state, level, pos, entitytype) -> entitytype.getCategory() == MobCategory.MONSTER)
		)),
		(b, props) -> new MGUBlockItem(b, itemProps("dreadful_dirt", props)));

	public static MGUBlockReg<BlockSolidXP, MGUBlockItem, ?> SOLID_XP_BLOCK = new MGUBlockReg<>("solid_xp_block",
		props -> new BlockSolidXP(blockProps("solid_xp_block", props.mapColor(MapColor.COLOR_GREEN).friction(0.8F).sound(ModSounds.SOLID_XP_BLOCK).noOcclusion().strength(1.5F, 10F))),
		(b, props) -> new MGUBlockItem(b, itemProps("solid_xp_block", props)));

	public static MGUBlockReg<BlockDelightfulDirt, MGUBlockItem, ?> DELIGHTFUL_DIRT = new MGUBlockReg<>("delightful_dirt",
		props -> new BlockDelightfulDirt(blockProps("delightful_dirt", props.mapColor(MapColor.COLOR_LIGHT_GREEN).strength(1.0F, 2000.0F).sound(SoundType.GRAVEL).randomTicks()
				.isValidSpawn((state, level, pos, entitytype) -> entitytype.getCategory() == MobCategory.CREATURE)
		)),
		(b, props) -> new MGUBlockItem(b, itemProps("delightful_dirt", props)));

	public static MGUBlockReg<BlockEntitySpawner, MGUBlockItem, TileEntityMGUSpawner> ENTITY_SPAWNER = new MGUBlockReg<>("entity_spawner",
		props -> new BlockEntitySpawner(blockProps("entity_spawner", props.mapColor(MapColor.COLOR_GRAY).strength(4.0F, 2000.0F).sound(SoundType.METAL).noOcclusion().randomTicks())),
		(b, props) -> new MGUBlockItem(b, itemProps("entity_spawner", props)), TileEntityMGUSpawner::new);

	public static DeferredHolder<FluidType, FluidType> XPTYPE = FLUID_TYPES.register("fluid_xp", () -> new FluidType(FluidType.Properties.create()
			.temperature(300)
			.lightLevel(10)
			.viscosity(1500)
			.density(800)
			.canConvertToSource(false)
			.canDrown(false)
			.canSwim(true)
			.descriptionId("mob_grinding_utils.fluid_xp")
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.EXPERIENCE_ORB_PICKUP)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.PLAYER_LEVELUP))
			{
				@NullMarked
				@Override
				public ItemStack getBucket(FluidStack stack) {
					return new ItemStack(ModItems.FLUID_XP_BUCKET.get());
				}
			});
	public static DeferredHolder<Fluid, BaseFlowingFluid> FLUID_XP = FLUIDS.register("fluid_xp",
		() -> new BaseFlowingFluid.Source(ModBlocks.xp_properties) );
	public static DeferredHolder<Fluid, BaseFlowingFluid> FLUID_XP_FLOWING = FLUIDS.register("fluid_xp_flowing",
		() -> new BaseFlowingFluid.Flowing(ModBlocks.xp_properties) );
	public static DeferredBlock<MGUFlowingFluidBlock> FLUID_XP_BLOCK = BLOCKS.register("fluid_xp",
		() -> new MGUFlowingFluidBlock(FLUID_XP,blockProps("fluid_xp", Block.Properties.of().liquid().noCollision().replaceable().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable())));

	private static final BaseFlowingFluid.Properties xp_properties = new BaseFlowingFluid.Properties(() -> XPTYPE.get(), () -> FLUID_XP.get(), () -> FLUID_XP_FLOWING.get())
			.block(() -> FLUID_XP_BLOCK.get())
			.bucket(() -> ModItems.FLUID_XP_BUCKET.get());

	public static final Set<MGUBlockReg<?,?,?>> TAB_ORDER = ImmutableSet.of(
		FAN, SAW, SPIKES, ABSORPTION_HOPPER, TANK, TANK_SINK, JUMBO_TANK,
		XP_TAP, WITHER_MUFFLER, DRAGON_MUFFLER, DARK_OAK_STONE, ENTITY_CONVEYOR, ENTITY_SPAWNER,
		ENDER_INHIBITOR_ON, ENDER_INHIBITOR_OFF, TINTED_GLASS, DREADFUL_DIRT, DELIGHTFUL_DIRT,
		XPSOLIDIFIER, SOLID_XP_BLOCK
	);

	public static void init(IEventBus evt) {
		BLOCKS.register(evt);
		TILE_ENTITIES.register(evt);
		FLUIDS.register(evt);
		FLUID_TYPES.register(evt);
	}
}

