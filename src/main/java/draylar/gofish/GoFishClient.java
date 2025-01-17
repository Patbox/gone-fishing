package draylar.gofish;

import draylar.gofish.client.be.AstralCrateRenderer;
import draylar.gofish.client.item.AstralCrateItemRenderer;
import draylar.gofish.registry.GoFishBlocks;
import draylar.gofish.registry.GoFishEntities;
import draylar.gofish.registry.GoFishItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public class GoFishClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(GoFishBlocks.ASTRAL_CRATE, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(GoFishEntities.ASTRAL_CRATE, AstralCrateRenderer::new);
        //BuiltinItemRendererRegistry.INSTANCE.register(GoFishBlocks.ASTRAL_CRATE.asItem(), new AstralCrateItemRenderer());

        registerFishingRodPredicates(GoFishItems.BLAZE_ROD);
        registerFishingRodPredicates(GoFishItems.CELESTIAL_ROD);
        registerFishingRodPredicates(GoFishItems.FROSTED_ROD);
        registerFishingRodPredicates(GoFishItems.SOUL_ROD);
        registerFishingRodPredicates(GoFishItems.MATRIX_ROD);
        registerFishingRodPredicates(GoFishItems.SLIME_ROD);
        registerFishingRodPredicates(GoFishItems.DIAMOND_REINFORCED_ROD);
        registerFishingRodPredicates(GoFishItems.SKELETAL_ROD);
        registerFishingRodPredicates(GoFishItems.EYE_OF_FISHING);
    }

    public void registerFishingRodPredicates(Item item) {

    }
}
