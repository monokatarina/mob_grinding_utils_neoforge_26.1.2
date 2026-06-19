package mob_grinding_utils.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mob_grinding_utils.MobGrindingUtils;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record SolidifyRecipe(
		Ingredient mould,
		ItemStackTemplate result,
		int fluidAmount
) implements Recipe<RecipeInput> {

	public static final String NAME = "solidify";

	@Override
	public boolean matches(
			RecipeInput input,
			Level level
	) {
		return false;
	}

	public boolean matches(ItemStack input) {
		return mould.test(input);
	}

	@Override
	public ItemStack assemble(RecipeInput input) {
		return result.create();
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
	public RecipeSerializer<SolidifyRecipe> getSerializer() {
		return MobGrindingUtils.SOLIDIFIER_RECIPE.get();
	}

	@Override
	public RecipeType<SolidifyRecipe> getType() {
		return MobGrindingUtils.SOLIDIFIER_TYPE.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.create(mould);
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	public static final class Serializer {

		public static final MapCodec<SolidifyRecipe> CODEC =
				RecordCodecBuilder.mapCodec(instance ->
						instance.group(
								Ingredient.CODEC
										.fieldOf("ingredient")
										.forGetter(SolidifyRecipe::mould),

								ItemStackTemplate.CODEC
										.fieldOf("result")
										.forGetter(SolidifyRecipe::result),

								Codec.INT
										.fieldOf("fluidAmount")
										.forGetter(SolidifyRecipe::fluidAmount)
						).apply(instance, SolidifyRecipe::new)
				);

		public static final StreamCodec<
				RegistryFriendlyByteBuf,
				SolidifyRecipe
				> STREAM_CODEC = StreamCodec.of(
				Serializer::toNetwork,
				Serializer::fromNetwork
		);

		private Serializer() {
		}

		public static SolidifyRecipe fromNetwork(
				RegistryFriendlyByteBuf buffer
		) {
			Ingredient mould =
					Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

			ItemStackTemplate result =
					ItemStackTemplate.STREAM_CODEC.decode(buffer);

			int fluidAmount = buffer.readInt();

			return new SolidifyRecipe(
					mould,
					result,
					fluidAmount
			);
		}

		public static void toNetwork(
				RegistryFriendlyByteBuf buffer,
				SolidifyRecipe recipe
		) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(
					buffer,
					recipe.mould()
			);

			ItemStackTemplate.STREAM_CODEC.encode(
					buffer,
					recipe.result()
			);

			buffer.writeInt(recipe.fluidAmount());
		}
	}
}

