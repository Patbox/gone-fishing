package draylar.gofish.item;

import draylar.gofish.api.FishingBonus;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public class SoulLureItem extends Item implements FishingBonus, PolymerItem {

    public SoulLureItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getLuckOfTheSea() {
        return 1;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        textConsumer.accept(Text.translatable(String.format("gofish.lure.tooltip_%d", 1)).formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable(String.format("gofish.lots.tooltip_%d", 2), 1, " in Soul Sand Valley").formatted(Formatting.GRAY));
    }

    @Override
    public boolean shouldApply(World world, PlayerEntity player) {
        return world.getBiome(player.getBlockPos()).matchesKey(BiomeKeys.SOUL_SAND_VALLEY);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }
}
