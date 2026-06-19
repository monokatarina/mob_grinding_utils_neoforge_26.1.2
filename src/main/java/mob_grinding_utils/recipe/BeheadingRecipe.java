package mob_grinding_utils.recipe;

import com.google.gson.JsonParseException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mob_grinding_utils.MobGrindingUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.NullMarked;
import net.minecraft.world.level.Level;

import java.util.Optional;
@NullMarked
public class BeheadingRecipe implements Recipe<EmptyInput> {

    public static final String NAME = "beheading";

    private final EntityType<?> entityType;
    private final ItemStackTemplate result;

    public BeheadingRecipe(
            EntityType<?> entityType,
            ItemStackTemplate result
    ) {
        this.entityType = entityType;
        this.result = result;
    }

    @Override
    public boolean matches(
            EmptyInput input,
            Level level
    ) {
        return false;
    }

    public boolean matches(EntityType<?> entityType) {
        return this.entityType == entityType;
    }

    @Override
    public ItemStack assemble(EmptyInput input) {
        return result.create();
    }

    public ItemStackTemplate result() {
        return result;
    }

    public EntityType<?> entityType() {
        return entityType;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<BeheadingRecipe> getSerializer() {
        return MobGrindingUtils.BEHEADING_RECIPE.get();
    }

    @Override
    public RecipeType<BeheadingRecipe> getType() {
        return MobGrindingUtils.BEHEADING_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static final class Serializer {

        public static final MapCodec<BeheadingRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        instance.group(
                                BuiltInRegistries.ENTITY_TYPE
                                        .byNameCodec()
                                        .fieldOf("entity")
                                        .forGetter(BeheadingRecipe::entityType),

                                ItemStackTemplate.CODEC
                                        .fieldOf("result")
                                        .forGetter(recipe -> recipe.result)
                        ).apply(instance, BeheadingRecipe::new)
                );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                BeheadingRecipe
                > STREAM_CODEC = StreamCodec.of(
                Serializer::toNetwork,
                Serializer::fromNetwork
        );

        private Serializer() {
        }

        public static BeheadingRecipe fromNetwork(
                RegistryFriendlyByteBuf buffer
        ) {
            Identifier entityId =
                    Identifier.parse(buffer.readUtf());

            Optional<EntityType<?>> entityType =
                    BuiltInRegistries.ENTITY_TYPE
                            .getOptional(entityId);

            if (entityType.isEmpty()) {
                throw new JsonParseException(
                        "Unknown entity type: " + entityId
                );
            }

            ItemStackTemplate result =
                    ItemStackTemplate.STREAM_CODEC.decode(buffer);

            return new BeheadingRecipe(
                    entityType.get(),
                    result
            );
        }

        public static void toNetwork(
                RegistryFriendlyByteBuf buffer,
                BeheadingRecipe recipe
        ) {
            Identifier entityId =
                    BuiltInRegistries.ENTITY_TYPE
                            .getKey(recipe.entityType);

            buffer.writeUtf(entityId.toString());

            ItemStackTemplate.STREAM_CODEC.encode(
                    buffer,
                    recipe.result
            );
        }
    }
}

