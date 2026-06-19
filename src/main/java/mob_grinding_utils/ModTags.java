package mob_grinding_utils;

import mob_grinding_utils.util.RL;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> ENDER_EYES = TagKey.create(Registries.ITEM, RL.rl("c", "ender_eyes"));
        public static final TagKey<Item> ENDER_PEARLS = TagKey.create(Registries.ITEM, RL.rl("c", "ender_pearls"));
        public static final TagKey<Item> OBSIDIAN = TagKey.create(Registries.ITEM, RL.rl("c", "obsidian"));
        public static final TagKey<Item> HOPPERS = TagKey.create(Registries.ITEM, RL.rl("c", "hoppers"));
        public static final TagKey<Item> INGOTS_IRON = TagKey.create(Registries.ITEM, RL.rl("c", "ingots/iron"));
        public static final TagKey<Item> IRON_BARS = TagKey.create(Registries.ITEM, RL.rl("c", "iron_bars"));
        public static final TagKey<Item> STORAGE_BLOCKS_IRON = TagKey.create(Registries.ITEM, RL.rl("c", "storage_blocks/iron"));
        public static final TagKey<Item> STORAGE_BLOCKS_REDSTONE = TagKey.create(Registries.ITEM, RL.rl("c", "storage_blocks/redstone"));
        public static final TagKey<Item> GLASS_BLOCKS = TagKey.create(Registries.ITEM, RL.rl("c", "glass_blocks"));
        public static final TagKey<Item> DUSTS_REDSTONE = TagKey.create(Registries.ITEM, RL.rl("c", "dusts/redstone"));
        public static final TagKey<Item> FEATHERS = TagKey.create(Registries.ITEM, RL.rl("c", "feathers"));
        public static final TagKey<Item> NUGGETS_GOLD = TagKey.create(Registries.ITEM, RL.rl("c", "nuggets/gold"));
        public static final TagKey<Item> GEMS_DIAMOND = TagKey.create(Registries.ITEM, RL.rl("c", "gems/diamond"));
        public static final TagKey<Item> BLAZE_POWDERS = TagKey.create(Registries.ITEM, RL.rl("c", "blaze_powders"));
        public static final TagKey<Item> ROTTEN_FLESH = TagKey.create(Registries.ITEM, RL.rl("c", "rotten_flesh"));
        public static final TagKey<Item> SPIDER_EYES = TagKey.create(Registries.ITEM, RL.rl("c", "spider_eyes"));
        public static final TagKey<Item> SLIME_BALLS = TagKey.create(Registries.ITEM, RL.rl("c", "slime_balls"));
        public static final TagKey<Item> GLOWSTONE_DUSTS = TagKey.create(Registries.ITEM, RL.rl("c", "dusts/glowstone"));
        public static final TagKey<Item> GUNPOWDERS = TagKey.create(Registries.ITEM, RL.rl("c", "gunpowder"));
        public static final TagKey<Item> BONES = TagKey.create(Registries.ITEM, RL.rl("c", "bones"));
        public static final TagKey<Item> SEEDS = TagKey.create(Registries.ITEM, RL.rl("c", "seeds"));
        public static final TagKey<Item> CROPS = TagKey.create(Registries.ITEM, RL.rl("c", "crops"));
        public static final TagKey<Item> EGGS = TagKey.create(Registries.ITEM, RL.rl("c", "eggs"));
        public static final TagKey<Item> SUGARS = TagKey.create(Registries.ITEM, RL.rl("c", "sugars"));
    }
    public static class Fluids {
        public static final TagKey<Fluid> EXPERIENCE = TagKey.create(Registries.FLUID, RL.rl("c", "experience"));
        public static final TagKey<Fluid> XPJUICE = TagKey.create(Registries.FLUID, RL.rl("c", "xpjuice"));
    }
    public static class Entities {
        public static final TagKey<EntityType<?>> NO_SWAB = TagKey.create(Registries.ENTITY_TYPE, RL.mgu("no_swab"));
        public static final TagKey<EntityType<?>> NO_SPAWN = TagKey.create(Registries.ENTITY_TYPE, RL.mgu("no_spawn"));
        public static final TagKey<EntityType<?>> NO_DIRT_SPAWN = TagKey.create(Registries.ENTITY_TYPE, RL.mgu("no_dirt_spawn"));
        public static final TagKey<EntityType<?>> NO_DREADFUL_SPAWN = TagKey.create(Registries.ENTITY_TYPE, RL.mgu("no_dreadful_spawn"));
        public static final TagKey<EntityType<?>> NO_DELIGHTFUL_SPAWN = TagKey.create(Registries.ENTITY_TYPE, RL.mgu("no_delightful_spawn"));
    }
    public static class Biomes {
        public static final TagKey<Biome> PASSIVE_OVERRIDE = TagKey.create(Registries.BIOME, RL.mgu("passive_override"));
        public static final TagKey<Biome> HOSTILE_OVERRIDE = TagKey.create(Registries.BIOME, RL.mgu("hostile_override"));
    }
}

