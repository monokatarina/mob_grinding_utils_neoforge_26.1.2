package mob_grinding_utils.datagen;

import mob_grinding_utils.ModBlocks;
import mob_grinding_utils.ModItems;
import mob_grinding_utils.ModTags;
import mob_grinding_utils.recipe.BeheadingRecipe;
import mob_grinding_utils.recipe.ChickenFeedRecipe;
import mob_grinding_utils.recipe.FluidIngredient;
import mob_grinding_utils.recipe.SolidifyRecipe;
import mob_grinding_utils.util.NoAdvRecipeOutput;
import mob_grinding_utils.util.RL;
import mob_grinding_utils.util.RecipeInjector;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;


public class Recipes extends RecipeProvider {
    public Recipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    private static TagKey<Item> cItem(String path) {
        return TagKey.create(Registries.ITEM, RL.rl("c", path));
    }

    @Override
    protected void buildRecipes() {
        var consumer = new NoAdvRecipeOutput(this.output);
        //Absorption Hopper
        var noneItem = has(Items.AIR);
        shaped(RecipeCategory.MISC, ModBlocks.ABSORPTION_HOPPER.getItem())
                .pattern(" E ")
                .pattern(" O ")
                .pattern("OHO")
                .define('E', ModTags.Items.ENDER_EYES)
                .define('O', ModTags.Items.OBSIDIAN)
                .define('H', ModTags.Items.HOPPERS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("absorption_hopper"));

        // Absorption Hopper Upgrade
        shaped(RecipeCategory.MISC, ModItems.ABSORPTION_UPGRADE.get())
                .pattern(" E ")
                .pattern("ERE")
                .pattern("OHO")
                .define('E', ModTags.Items.ENDER_PEARLS)
                .define('O', ModTags.Items.OBSIDIAN)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .define('H', ModTags.Items.HOPPERS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("absorption_upgrade"));

        // Spikes
        shaped(RecipeCategory.MISC, ModBlocks.SPIKES.getItem())
                .pattern(" S ")
                .pattern("SIS")
                .define('S', Items.IRON_SWORD)
                .define('I', ModTags.Items.STORAGE_BLOCKS_IRON)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("spikes"));

        // Tank
        shaped(RecipeCategory.MISC, ModBlocks.TANK.getItem())
                .pattern("IGI")
                .pattern("GGG")
                .pattern("IGI")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('G', ModTags.Items.GLASS_BLOCKS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("tank"));

        shapeless(RecipeCategory.MISC, ModBlocks.TANK.getItem()).requires(ModBlocks.TANK.getItem(),1)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("tank_reset"));

        // Tank Sink
        shaped(RecipeCategory.MISC, ModBlocks.TANK_SINK.getItem())
                .pattern(" I ")
                .pattern("EHE")
                .pattern(" T ")
                .define('I', ModTags.Items.IRON_BARS)
                .define('E', ModTags.Items.ENDER_EYES)
                .define('H', ModTags.Items.HOPPERS)
                .define('T', ModBlocks.TANK.getItem())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("tank_sink"));

        shapeless(RecipeCategory.MISC, ModBlocks.TANK_SINK.getItem()).requires(ModBlocks.TANK_SINK.getItem(),1)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("tank_sink_reset"));

        // XP TAP
        shaped(RecipeCategory.MISC, ModBlocks.XP_TAP.getItem())
                .pattern("O ")
                .pattern("II")
                .pattern("I ")
                .define('O', ModTags.Items.OBSIDIAN)
                .define('I', ModTags.Items.INGOTS_IRON)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("xp_tap"));

        // Fan
        shaped(RecipeCategory.MISC, ModBlocks.FAN.getItem())
                .pattern("SIS")
                .pattern("IRI")
                .pattern("SIS")
                .define('S', Items.STONE_SLAB)
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('R', ModTags.Items.STORAGE_BLOCKS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("fan"));

        // Fan Upgrades
        shaped(RecipeCategory.MISC, ModItems.FAN_UPGRADE_WIDTH.get())
                .pattern("I I")
                .pattern("FFF")
                .pattern("I I")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('F', ModTags.Items.FEATHERS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("fan_upgrade_width"));

        shaped(RecipeCategory.MISC, ModItems.FAN_UPGRADE_HEIGHT.get())
                .pattern("IFI")
                .pattern(" F ")
                .pattern("IFI")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('F', ModTags.Items.FEATHERS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("fan_upgrade_height"));

        shaped(RecipeCategory.MISC, ModItems.FAN_UPGRADE_SPEED.get())
                .pattern("FIF")
                .pattern("IRI")
                .pattern("FIF")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('F', ModTags.Items.FEATHERS)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("fan_upgrade_speed"));

        // Mob Swab
        shaped(RecipeCategory.MISC, ModItems.MOB_SWAB.get())
                .pattern("  W")
                .pattern(" S ")
                .pattern("W  ")
                .define('W', ItemTags.WOOL)
                .define('S', Items.STICK)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("mob_swab"));

        // Wither Muffler
        shaped(RecipeCategory.MISC, ModBlocks.WITHER_MUFFLER.getItem())
                .pattern("WWW")
                .pattern("WSW")
                .pattern("WWW")
                .define('W', ItemTags.WOOL)
                .define('S', Items.WITHER_SKELETON_SKULL)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("wither_muffler"));

        // Dragon Muffler
        shaped(RecipeCategory.MISC, ModBlocks.DRAGON_MUFFLER.getItem())
                .pattern("WWW")
                .pattern("WEW")
                .pattern("WWW")
                .define('W', ItemTags.WOOL)
                .define('E', Items.DRAGON_EGG)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("dragon_muffler"));

        // Mob Masher
        shaped(RecipeCategory.MISC, ModBlocks.SAW.getItem())
                .pattern("SDS")
                .pattern("VRV")
                .pattern("DID")
                .define('S', Items.IRON_SWORD)
                .define('D', ModTags.Items.GEMS_DIAMOND)
                .define('V', ModBlocks.SPIKES.getItem())
                .define('R', Items.REDSTONE_BLOCK)
                .define('I', ModTags.Items.STORAGE_BLOCKS_IRON)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw"));

        // Mob Masher Upgrades
        shaped(RecipeCategory.MISC, ModItems.SAW_UPGRADE_SHARPNESS.get())
                .pattern("GSG")
                .pattern("SRS")
                .pattern("GSG")
                .define('G', ModTags.Items.NUGGETS_GOLD)
                .define('S', Items.IRON_SWORD)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw_upgrade_sharpness"));

        shaped(RecipeCategory.MISC, ModItems.SAW_UPGRADE_LOOTING.get())
                .pattern("GLG")
                .pattern("LRL")
                .pattern("GLG")
                .define('G', ModTags.Items.NUGGETS_GOLD)
                .define('L', Items.BLUE_DYE)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw_upgrade_looting"));

        shaped(RecipeCategory.MISC, ModItems.SAW_UPGRADE_FIRE.get())
                .pattern("GFG")
                .pattern("FRF")
                .pattern("GFG")
                .define('G', ModTags.Items.NUGGETS_GOLD)
                .define('F', Items.FLINT_AND_STEEL)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw_upgrade_fire"));

        shaped(RecipeCategory.MISC, ModItems.SAW_UPGRADE_SMITE.get())
                .pattern("GFG")
                .pattern("FRF")
                .pattern("GFG")
                .define('G', ModTags.Items.NUGGETS_GOLD)
                .define('F', ModTags.Items.ROTTEN_FLESH)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw_upgrade_smite"));

        shaped(RecipeCategory.MISC, ModItems.SAW_UPGRADE_ARTHROPOD.get())
                .pattern("GSG")
                .pattern("SRS")
                .pattern("GSG")
                .define('G', ModTags.Items.NUGGETS_GOLD)
                .define('S', ModTags.Items.SPIDER_EYES)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw_upgrade_arthropod"));

        shaped(RecipeCategory.MISC, ModItems.SAW_UPGRADE_BEHEADING.get())
                .pattern("GHG")
                .pattern("IRI")
                .pattern("GHG")
                .define('G', ModTags.Items.NUGGETS_GOLD)
                .define('H', Items.GOLDEN_HELMET)
                .define('I', Items.IRON_HELMET)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("saw_upgrade_beheading"));

        // Entity Conveyor
        shaped(RecipeCategory.MISC, ModBlocks.ENTITY_CONVEYOR.getItem(),6)
                .pattern(" S ")
                .pattern("IRI")
                .pattern("ISI")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('S', ModTags.Items.SLIME_BALLS)
                .define('R', ModTags.Items.STORAGE_BLOCKS_REDSTONE)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("entity_conveyor"));

        // Ender Inhibitor
        shaped(RecipeCategory.MISC, ModBlocks.ENDER_INHIBITOR_ON.getItem())
                .pattern(" R ")
                .pattern("IEI")
                .pattern(" G ")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('E', ModTags.Items.ENDER_EYES)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .define('G', ModTags.Items.GLOWSTONE_DUSTS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("ender_inhibitor"));

        //Jumbo Tank
        shaped(RecipeCategory.MISC, ModBlocks.JUMBO_TANK.getItem())
                .pattern("ITI")
                .pattern("T T")
                .pattern("ITI")
                .define('I', ModTags.Items.INGOTS_IRON)
                .define('T', ModBlocks.TANK.getItem())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("jumbo_tank"));

        shapeless(RecipeCategory.MISC, ModBlocks.JUMBO_TANK.getItem()).requires(ModBlocks.JUMBO_TANK.getItem(),1)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("jumbo_tank_reset"));

        //Tinted Glass
        shaped(RecipeCategory.MISC, ModBlocks.TINTED_GLASS.getItem(), 4)
                .pattern("CGC")
                .pattern("GCG")
                .pattern("CGC")
                .define('C', ItemTags.COALS)
                .define('G', ModTags.Items.GLASS_BLOCKS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("tinted_glass"));

        shaped(RecipeCategory.MISC, ModItems.GM_CHICKEN_FEED_CURSED.get())
                .pattern("BEB")
                .pattern("RSX")
                .pattern("BGB")
                .define('B', new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .define('E', ModTags.Items.SPIDER_EYES)
                .define('R', ModTags.Items.ROTTEN_FLESH)
                .define('S', ModTags.Items.SEEDS)
                .define('X', ModTags.Items.BONES)
                .define('G', ModTags.Items.GUNPOWDERS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("cursed_feed"));

        shaped(RecipeCategory.MISC, ModBlocks.XPSOLIDIFIER.getItem())
                .pattern(" P ")
                .pattern("CHC")
                .pattern(" T ")
                .define('P', Items.PISTON)
                .define('C', ModBlocks.ENTITY_CONVEYOR.getItem())
                .define('H', ModTags.Items.HOPPERS)
                .define('T', ModBlocks.TANK.getItem())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("xpsolidifier"));

        shapeless(RecipeCategory.MISC, ModBlocks.XPSOLIDIFIER.getItem()).requires(ModBlocks.XPSOLIDIFIER.getItem(),1)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("xpsolidifier_reset"));

        shaped(RecipeCategory.MISC, ModBlocks.ENTITY_SPAWNER.getItem())
                .pattern("EEE")
                .pattern("XRX")
                .pattern("IPI")
                .define('P', Items.PISTON)
                .define('I', ModTags.Items.STORAGE_BLOCKS_IRON)
                .define('R', ModTags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('X', ModBlocks.SOLID_XP_BLOCK.getItem())
                .define('E', ModTags.Items.ENDER_EYES)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("entity_spawner"));

        //Blank Mould
        shaped(RecipeCategory.MISC, ModItems.SOLID_XP_MOULD_BLANK.get())
                .pattern("XXX")
                .pattern("XBX")
                .pattern("XXX")
                .define('X', ModTags.Items.NUGGETS_GOLD)
                .define('B', new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("mould_blank"));

        //Mould upgrade chain, starting with blank
        shapeless(RecipeCategory.MISC, ModItems.SOLID_XP_MOULD_BABY.get())
                .requires(ModItems.SOLID_XP_MOULD_BLANK.get())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("mould_baby_upgrade"));

        //Last one in the chain should reset to blank
        shapeless(RecipeCategory.MISC, ModItems.SOLID_XP_MOULD_BLANK.get())
                .requires(ModItems.SOLID_XP_MOULD_BABY.get())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("mould_reset"));

        //Solid XP Block
        shapeless(RecipeCategory.MISC, ModBlocks.SOLID_XP_BLOCK.getItem())
                .requires(ModItems.SOLID_XP_BABY.get(), 9)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("xp_block"));
        //Uncraft
        shapeless(RecipeCategory.MISC, ModItems.SOLID_XP_BABY.get(), 9)
                .requires(ModBlocks.SOLID_XP_BLOCK.getItem())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("xp_block_uncraft"));

        //Solidifier upgrade
        shaped(RecipeCategory.MISC, ModItems.XP_SOLIDIFIER_UPGRADE.get())
                .pattern("SRS")
                .pattern("BXB")
                .pattern("SRS")
                .define('S', ModTags.Items.SUGARS)
                .define('R', ModTags.Items.DUSTS_REDSTONE)
                .define('B', ModTags.Items.BLAZE_POWDERS)
                .define('X', new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("xpsolidifier_upgrade"));

        shaped(RecipeCategory.MISC, ModItems.NUTRITIOUS_CHICKEN_FEED.get())
                .pattern("BCB")
                .pattern("PSX")
                .pattern("BWB")
                .define('B', new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .define('C', ModTags.Items.CROPS)
                .define('P', ModTags.Items.CROPS)
                .define('S', ModTags.Items.SEEDS)
                .define('X', ModTags.Items.CROPS)
                .define('W', ModTags.Items.CROPS)
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("nutritious_feed"));

        //Spawner width upgrade
        shaped(RecipeCategory.MISC, ModItems.SPAWNER_UPGRADE_WIDTH.get())
                .pattern("EEE")
                .pattern("BXB")
                .pattern("EEE")
                .define('E', ModTags.Items.EGGS)
                .define('B', ModTags.Items.BLAZE_POWDERS)
                .define('X', new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("spawner_upgrade_width"));

        //Spawner height upgrade
        shaped(RecipeCategory.MISC, ModItems.SPAWNER_UPGRADE_HEIGHT.get())
                .pattern("EBE")
                .pattern("EXE")
                .pattern("EBE")
                .define('E', ModTags.Items.EGGS)
                .define('B', ModTags.Items.BLAZE_POWDERS)
                .define('X', new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .unlockedBy("", noneItem)
                .save(consumer, recipeKey("spawner_upgrade_height"));

        shapeless(RecipeCategory.MISC, ModItems.GM_CHICKEN_FEED.get())
                .requires(ModTags.Items.SEEDS)
                .requires(ModItems.MOB_SWAB_USED.get())
                .requires(new FluidIngredient(ModTags.Fluids.EXPERIENCE).toVanilla())
                .unlockedBy("", noneItem)
                .save(new RecipeInjector<ShapelessRecipe>(consumer, ChickenFeedRecipe::new), recipeKey("gm_chicken_feed"));


        //Solidifier recipes
        consumer.accept(recipeKey("solidify/jelly_baby"), new SolidifyRecipe(Ingredient.of(ModItems.SOLID_XP_MOULD_BABY.get()), new ItemStackTemplate(ModItems.SOLID_XP_BABY.get()), 1000), null);

        generateBeheading(consumer);
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, RL.mgu(path));
    }

    private void generateBeheading(RecipeOutput consumer) {
        Head(consumer,"creeper", EntityType.CREEPER, Items.CREEPER_HEAD);
        Head(consumer,"skeleton", EntityType.SKELETON, Items.SKELETON_SKULL);
        Head(consumer,"wither_skeleton", EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL);
        Head(consumer,"zombie", EntityType.ZOMBIE, Items.ZOMBIE_HEAD);
        Head(consumer,"dragon", EntityType.ENDER_DRAGON, Items.DRAGON_HEAD);

/*
        //Heads
        OptionalHead(consumer, "blaze", "tconstruct", EntityType.BLAZE, new Identifier("tconstruct", "blaze_head"));
        OptionalHead(consumer, "enderman", "tconstruct", EntityType.ENDERMAN, new Identifier("tconstruct", "enderman_head"));
        OptionalHead(consumer, "husk", "tconstruct", EntityType.HUSK, new Identifier("tconstruct", "husk_head"));
        OptionalHead(consumer, "drowned", "tconstruct", EntityType.DROWNED, new Identifier("tconstruct", "drowned_head"));
        OptionalHead(consumer, "spider", "tconstruct", EntityType.SPIDER, new Identifier("tconstruct", "spider_head"));
        OptionalHead(consumer, "cave_spider", "tconstruct", EntityType.CAVE_SPIDER, new Identifier("tconstruct", "cave_spider_head"));
        OptionalHead(consumer, "piglin", "tconstruct", EntityType.PIGLIN, new Identifier("tconstruct", "piglin_head"));
        OptionalHead(consumer, "piglin_brute", "tconstruct", EntityType.PIGLIN_BRUTE, new Identifier("tconstruct", "piglin_brute_head"));
        OptionalHead(consumer, "zombified_piglin_brute", "tconstruct", EntityType.ZOMBIFIED_PIGLIN, new Identifier("tconstruct", "zombified_piglin_head"));
*/
    }

    private BeheadingRecipe HeadRecipe(EntityType<?> type, Item item) {
        return new BeheadingRecipe(type, new ItemStackTemplate(item));
    }

/*    private BeheadingRecipe HeadRecipe(EntityType<?> type, Identifier item) {
        return new BeheadingRecipe(type, item);
    }*/

    /*    private void OptionalHead(RecipeOutput consumer, String name, String modid, EntityType<?> type, Identifier item) {
            consumer.accept(RL.rl( "beheading/" + name), HeadRecipe(type, item), null,
                    new ModLoadedCondition(modid));
        }*/
    private void Head(
            RecipeOutput consumer,
            String name,
            EntityType<?> type,
            Item item
    ) {
        consumer.accept(
                recipeKey("beheading/" + name),
                HeadRecipe(type, item),
                null
        );
    }
}

