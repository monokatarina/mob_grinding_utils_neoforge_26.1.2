package mob_grinding_utils.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mob_grinding_utils.MobGrindingUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.access.ItemAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FluidIngredient implements ICustomIngredient {

    public static final MapCodec<FluidIngredient> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            FluidValue.CODEC
                                    .fieldOf("value")
                                    .forGetter(ingredient -> ingredient.value),

                            Codec.BOOL
                                    .fieldOf("advanced")
                                    .forGetter(ingredient -> ingredient.advanced)
                    ).apply(instance, FluidIngredient::new)
            );

    private final boolean advanced;
    private final List<Fluid> matchingFluids = new ArrayList<>();

    public final FluidValue value;

    public FluidIngredient(
            TagKey<Fluid> tag,
            int amount,
            boolean advanced
    ) {
        this.advanced = advanced;
        this.value = new FluidTagValue(tag, amount);
    }

    public FluidIngredient(TagKey<Fluid> tag) {
        this(tag, 1000, false);
    }

    public FluidIngredient(
            Fluid fluid,
            int amount,
            boolean advanced
    ) {
        this.value = new SpecificFluidValue(
                new FluidStack(fluid, amount)
        );

        this.advanced = advanced;
        this.matchingFluids.add(fluid);
    }

    public FluidIngredient(Fluid fluid) {
        this(fluid, 1000, false);
    }

    public FluidIngredient(
            FluidValue value,
            boolean advanced
    ) {
        this.value = value;
        this.advanced = advanced;
    }

    private List<Fluid> getMatchingFluids() {
        if (!matchingFluids.isEmpty()) {
            return matchingFluids;
        }

        if (value instanceof FluidTagValue tagValue) {
            BuiltInRegistries.FLUID
                    .getTagOrEmpty(tagValue.tag())
                    .forEach(holder ->
                            matchingFluids.add(holder.value())
                    );
        } else if (value instanceof SpecificFluidValue specificValue) {
            matchingFluids.add(
                    specificValue.fluidStack().getFluid()
            );
        }

        return matchingFluids;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return MobGrindingUtils.FLUID_INGREDIENT.get();
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!advanced && !(stack.getItem() instanceof BucketItem)) {
            return false;
        }

        ResourceHandler<FluidResource> handler =
                ItemAccess.forStack(stack)
                        .oneByOne()
                        .getCapability(Capabilities.Fluid.ITEM);

        if (handler == null || handler.size() == 0) {
            return false;
        }

        FluidResource storedResource = handler.getResource(0);
        int storedAmount = handler.getAmountAsInt(0);

        if (storedResource.isEmpty()) {
            return false;
        }

        boolean matchesFluid = getMatchingFluids()
                .stream()
                .map(FluidResource::of)
                .anyMatch(storedResource::equals);

        return matchesFluid
                && storedAmount >= value.getAmount();
    }

    @Override
    public Stream<Holder<Item>> items() {
        return getMatchingFluids()
                .stream()
                .map(Fluid::getBucket)
                .filter(item -> item != Items.AIR)
                .map(BuiltInRegistries.ITEM::wrapAsHolder)
                .distinct();
    }

    public interface FluidValue {

        Codec<FluidValue> CODEC =
                Codec.xor(
                        SpecificFluidValue.CODEC,
                        FluidTagValue.CODEC
                ).xmap(
                        either -> either.map(
                                specific -> specific,
                                tag -> tag
                        ),
                        fluidValue -> {
                            if (fluidValue instanceof FluidTagValue tagValue) {
                                return Either.right(tagValue);
                            }

                            if (fluidValue instanceof SpecificFluidValue specificValue) {
                                return Either.left(specificValue);
                            }

                            throw new UnsupportedOperationException(
                                    "FluidValue inválido."
                            );
                        }
                );

        int getAmount();

        Collection<FluidStack> getFluids();
    }

    public record SpecificFluidValue(
            FluidStack fluidStack
    ) implements FluidValue {

        public static final Codec<SpecificFluidValue> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                FluidStack.CODEC
                                        .fieldOf("fluid")
                                        .forGetter(
                                                SpecificFluidValue::fluidStack
                                        )
                        ).apply(
                                instance,
                                SpecificFluidValue::new
                        )
                );

        @Override
        public int getAmount() {
            return fluidStack.getAmount();
        }

        @Override
        public Collection<FluidStack> getFluids() {
            return Collections.singleton(fluidStack);
        }
    }

    public record FluidTagValue(
            TagKey<Fluid> tag,
            int amount
    ) implements FluidValue {

        public static final Codec<FluidTagValue> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                TagKey.codec(Registries.FLUID)
                                        .fieldOf("Tag")
                                        .forGetter(FluidTagValue::tag),

                                Codec.INT
                                        .fieldOf("Amount")
                                        .forGetter(FluidTagValue::amount)
                        ).apply(
                                instance,
                                FluidTagValue::new
                        )
                );

        @Override
        public int getAmount() {
            return amount;
        }

        @Override
        public Collection<FluidStack> getFluids() {
            List<FluidStack> fluids = new ArrayList<>();

            for (Holder<Fluid> holder :
                    BuiltInRegistries.FLUID.getTagOrEmpty(tag)) {

                fluids.add(
                        new FluidStack(
                                holder.value(),
                                amount
                        )
                );
            }

            if (fluids.isEmpty()) {
                fluids.add(FluidStack.EMPTY);
            }

            return fluids;
        }
    }
}
