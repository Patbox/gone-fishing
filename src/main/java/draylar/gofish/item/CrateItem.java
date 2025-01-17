package draylar.gofish.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrateItem extends BlockItem {

    private final RegistryKey<LootTable> loot;

    public CrateItem(Block block, Settings settings) {
        super(block, settings);
        loot = LootTables.PILLAGER_OUTPOST_CHEST;
    }

    public CrateItem(Block block, Settings settings, Identifier loot) {
        super(block, settings);
        this.loot = RegistryKey.of(RegistryKeys.LOOT_TABLE, loot);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();

        if(player != null && player.isSneaking()) {
            return ActionResult.FAIL;
        }

        return super.useOnBlock(context);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // only open crate if user is sneaking
        if(user.isSneaking()) {
            if(!world.isClient) {
                // drop loot
                getDrops((ServerWorld) world, loot, user.getPos()).forEach(stack -> {
                    ItemScatterer.spawn(world, user.getX(), user.getY(), user.getZ(), stack);
                });
            }

            // remove 1x from inventory for non-creative players
            if(!user.isCreative()) {
                user.getStackInHand(hand).decrement(1);
            }

            return ActionResult.SUCCESS_SERVER;
        }

        return super.use(world, user, hand);
    }

    /**
     * Retrieves the loot table drops for the given Identifier.
     * If no loot table exists at the given Identifier, an empty list is returned.
     * @param identifier loot table Identifier
     * @return list of drops generated from the loot table
     */
    private List<ItemStack> getDrops(ServerWorld world, RegistryKey<LootTable> identifier, Vec3d pos) {
        List<ItemStack> output = new ArrayList<>();

        if (world != null && !world.isClient) {
            // set up loot objects
            LootTable supplier = Objects.requireNonNull(world.getServer()).getReloadableRegistries().getLootTable(identifier);
            LootWorldContext.Builder builder =
                    new LootWorldContext.Builder(world)
                            .add(LootContextParameters.ORIGIN, pos);

            // build & add loot to output
            List<ItemStack> stacks = supplier.generateLoot(builder.build(LootContextTypes.CHEST));
            output.addAll(stacks);
        }

        return output;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("gofish.crate_tooltip").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
    }
}
