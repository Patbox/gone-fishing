package draylar.gofish.item;

import draylar.gofish.api.FishingBonus;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public class LureItem extends Item implements FishingBonus, PolymerItem {

    private final int lure;

    public LureItem(Settings settings, int lure) {
        super(settings);
        this.lure = lure;
    }

    @Override
    public int getLure() {
        return lure;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        for(int i = 1; i <= 2; i++) {
            textConsumer.accept(Text.translatable(String.format("gofish.lure.tooltip_%d", i), lure).formatted(Formatting.GRAY));
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }
}
