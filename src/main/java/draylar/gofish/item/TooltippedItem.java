package draylar.gofish.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public class TooltippedItem extends Item implements PolymerItem {

    private final int lines;

    public TooltippedItem(Settings settings, int lines) {
        super(settings);
        this.lines = lines;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        if(lines > 0) {
            for (int i = 1; i <= lines; i++) {
                textConsumer.accept(Text.translatable(String.format("%s.tooltip_%d", getTranslationKey(), i)).formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }
}
