package draylar.gofish;

import com.google.common.hash.HashCode;
import draylar.gofish.command.FishCommand;
import draylar.gofish.registry.*;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class GoFish implements ModInitializer {

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("group"));
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(GoFishItems.GOLDEN_FISH))
                .displayName(Text.translatable("itemGroup.gofish.group"))
                .build());

        GoFishBlocks.init();
        GoFishItems.init();
        GoFishEnchantments.init();
        GoFishLoot.init();
        GoFishLootHandler.init();
        GoFishEntities.init();

        FishCommand.register();

        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(GoFishItems.OAKFISH, 3 * context.baseSmeltTime() / 2);
            builder.add(GoFishItems.CHARFISH, 8 * context.baseSmeltTime());
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register(this::registerBrewingRecipes);

        PolymerResourcePackUtils.addModAssets("go-fish");

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            //PatboxLazyModelGen.run();
        }
    }

    public static Identifier id(String name) {
        return Identifier.of("gofish", name);
    }

    public void registerBrewingRecipes(BrewingRecipeRegistry.Builder builder) {
        builder.registerPotionRecipe(Potions.AWKWARD, GoFishItems.CLOUDY_CRAB, Potions.SLOW_FALLING);
        builder.registerPotionRecipe(Potions.AWKWARD, GoFishItems.CHARFISH, Potions.WEAKNESS);
        builder.registerPotionRecipe(Potions.AWKWARD, GoFishItems.RAINY_BASS, Potions.WATER_BREATHING);
        builder.registerPotionRecipe(Potions.AWKWARD, GoFishItems.MAGMA_COD, Potions.FIRE_RESISTANCE);
    }

}
