package draylar.gofish.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import draylar.gofish.registry.GoFishLoot;
import net.minecraft.entity.Entity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public record WeatherCondition(Optional<Boolean> raining, Optional<Boolean> thundering, Optional<Boolean> snowing) implements LootCondition {

    public static final MapCodec<WeatherCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.BOOL.optionalFieldOf("raining").forGetter(WeatherCondition::raining),
                            Codec.BOOL.optionalFieldOf("thundering").forGetter(WeatherCondition::thundering),
                            Codec.BOOL.optionalFieldOf("snowing").forGetter(WeatherCondition::snowing)
                    )
                    .apply(instance, WeatherCondition::new)
    );

    @Override
    public LootConditionType getType() {
        return GoFishLoot.WEATHER;
    }


    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return ImmutableSet.of(LootContextParameters.THIS_ENTITY, LootContextParameters.ORIGIN);
    }

    @Override
    public boolean test(LootContext lootContext) {
        @Nullable Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);
        @Nullable Vec3d pos = lootContext.get(LootContextParameters.ORIGIN);

        if(entity != null && pos != null) {
            World world = entity.getWorld();

            // If raining is required and the world is not raining, return false.
            if (raining.isPresent() && raining.get() && !world.isRaining()) {
                return false;
            }

            // If thundering is required and the world is not raining, return false.
            if (thundering.isPresent() && thundering.get() && !world.isThundering()) {
                return false;
            }

            // same check for snowing
            if (snowing.isPresent() && snowing.get()) {
                // >= .15 = no snow
                if(world.getBiome(entity.getBlockPos()).value().doesNotSnow(new BlockPos((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z)), world.getSeaLevel())) {
                    return false;
                }

                return world.isRaining();
            }

            // Both conditions match, return true.
            return true;
        }

        return false;
    }

    public static LootCondition.Builder builder(boolean raining, boolean thundering, boolean snowing) {
        return () -> new WeatherCondition(Optional.of(raining), Optional.of(thundering), Optional.of(snowing));
    }
}
