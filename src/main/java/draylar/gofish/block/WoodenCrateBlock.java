package draylar.gofish.block;

import draylar.gofish.GoFish;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class WoodenCrateBlock extends CrateBlock {

    public WoodenCrateBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new Model(pos, initialBlockState);
    }


    public static final class Model extends BlockModel {
        private static final List<Pair<ItemStack, Float>> MODELS = List.of(
                entry(GoFish.id("wooden_crate"), 0f),
                entry(GoFish.id("wooden_crate"), 90f),
                entry(GoFish.id("wooden_crate_0"), 0f),
                entry(GoFish.id("wooden_crate_1"), 0f)
        );

        private static Pair<ItemStack, Float> entry(Identifier id, float yaw) {
            var stack = new ItemStack(Items.TRIAL_KEY);
            stack.set(DataComponentTypes.ITEM_MODEL, id);
            return new Pair<>(stack, yaw);
        }

        public Model(BlockPos pos, BlockState state) {
            var model = Util.getRandom(MODELS, Random.create(pos.asLong()));
            var main = ItemDisplayElementUtil.createSimple(model.getLeft());
            main.setYaw(model.getRight());
            main.setScale(new Vector3f(2));
            this.addElement(main);
        }
    }
}
