package draylar.gofish;

import com.google.common.hash.HashCode;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.FishingRodCastProperty;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.FishingRodItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.function.BiConsumer;

public class PatboxLazyModelGen {

    public static void run() {
        BiConsumer<Identifier, String> assetWriter = (path, data) -> {
            try {
                Files.writeString(FabricLoader.getInstance().getGameDir().resolve("../src/main/resources/").resolve(AssetPaths.itemAsset(path)), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        for (var item : Registries.ITEM) {
            var id = Registries.ITEM.getId(item);
            if (!id.getNamespace().equals("gofish")) {
                continue;
            }

            if (item instanceof FishingRodItem) {
                assetWriter.accept(id, new ItemAsset(new ConditionItemModel(new FishingRodCastProperty(),
                        new BasicItemModel(id.withPrefixedPath("item/").withSuffixedPath("_cast")), new BasicItemModel(id.withPrefixedPath("item/"))),
                        ItemAsset.Properties.DEFAULT).toJson());
            } else {
                assetWriter.accept(id, new ItemAsset(new BasicItemModel(id.withPrefixedPath("item/")), ItemAsset.Properties.DEFAULT).toJson());
            }
        }
    }
}
