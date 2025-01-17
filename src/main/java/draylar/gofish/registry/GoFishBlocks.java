package draylar.gofish.registry;

import draylar.gofish.GoFish;
import draylar.gofish.block.AstralCrateBlock;
import draylar.gofish.block.CrateBlock;
import draylar.gofish.item.CrateItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class GoFishBlocks {

    // The Wooden crate is padded with junk and cobwebs, but will often contain minimal resources, and a rare special item drop.
    //   Junk: Cobwebs, String, Kelp, Sticks, Planks, Seaweed
    //   Resources: coal, iron ore, iron nuggets, gold nuggets, flint, gold ingots
    //   Food: carrots, wheat, potatoes, beetroots
    //   Special: enchanting bottle, low-level enchanted book, emerald, bucket / bucket with fish, more materials
    //   Weapons: damaged crossbows, arrows, bows, stone tools
    //   Fish: all types of vanilla fish
    public static Block WOODEN_CRATE = registerCrate("wooden_crate", Block.Settings.copy(Blocks.OAK_WOOD), CrateBlock::new, new Item.Settings().maxCount(8), GoFish.id("gameplay/fishing/wooden_crate"));

    // The Iron Crate provides less junk, a chance for iron tools, and better rare loot.
    //   Junk: Oak Planks, sticks, Oak Logs, String, Seaweed, Kelp, Bones
    //   Resources: coal, iron ore, iron nuggets, gold nuggets, gold ingots, lapis, redstone
    //   Food: carrots, wheat, potatoes, beetroots, cooked potatoes
    //   Special: mid-level enchanted book, emerald, more materials
    //   Weapons: damaged crossbows, arrows, bows, stone tools
    //   Fish: all types of vanilla fish
    public static Block IRON_CRATE = registerCrate("iron_crate", Block.Settings.copy(Blocks.IRON_BLOCK), CrateBlock::new, new Item.Settings().maxCount(8), GoFish.id("gameplay/fishing/iron_crate"));

    // The Gold Crate is a rare crate that drops gold items and materials.
    public static Block GOLDEN_CRATE = registerCrate("golden_crate", Block.Settings.copy(Blocks.GOLD_BLOCK), CrateBlock::new, new Item.Settings().maxCount(8).rarity(Rarity.UNCOMMON), GoFish.id("gameplay/fishing/golden_crate"));

    // The Diamond Crate provides good materials
    public static Block DIAMOND_CRATE = registerCrate("diamond_crate", Block.Settings.copy(Blocks.DIAMOND_BLOCK), CrateBlock::new, new Item.Settings().maxCount(8).rarity(Rarity.RARE), GoFish.id("gameplay/fishing/diamond_crate"));
    public static Block FROSTED_CRATE = registerCrate("frosted_crate", Block.Settings.copy(Blocks.BLUE_ICE), CrateBlock::new, new Item.Settings().maxCount(8).rarity(Rarity.RARE), GoFish.id("gameplay/fishing/frosted_crate"));
    public static Block SLIMEY_CRATE = registerCrate("slimey_crate", Block.Settings.copy(Blocks.SLIME_BLOCK), CrateBlock::new, new Item.Settings().maxCount(8), GoFish.id("gameplay/fishing/slimey_crate"));
    public static Block SUPPLY_CRATE = registerCrate("supply_crate", Block.Settings.copy(Blocks.OAK_WOOD), CrateBlock::new, new Item.Settings().maxCount(8), GoFish.id("gameplay/fishing/supply_crate"));
    public static Block FIERY_CRATE = registerCrate("fiery_crate", Block.Settings.copy(Blocks.NETHER_BRICKS), CrateBlock::new, new Item.Settings().fireproof().maxCount(8), GoFish.id("gameplay/fishing/fiery_crate"));
    public static Block SOUL_CRATE = registerCrate("soul_crate", Block.Settings.copy(Blocks.STONE), CrateBlock::new, new Item.Settings().fireproof().maxCount(8).rarity(Rarity.RARE), GoFish.id("gameplay/fishing/soul_crate"));
    public static Block GILDED_BLACKSTONE_CRATE = registerCrate("gilded_blackstone_crate", Block.Settings.copy(Blocks.GILDED_BLACKSTONE), CrateBlock::new, new Item.Settings().fireproof().maxCount(8).rarity(Rarity.UNCOMMON), GoFish.id("gameplay/fishing/gilded_blackstone_crate"));
    public static Block ASTRAL_CRATE = registerCrate("astral_crate", Block.Settings.copy(Blocks.END_STONE).nonOpaque(), AstralCrateBlock::new, new Item.Settings().fireproof().maxCount(8).rarity(Rarity.EPIC), GoFish.id("gameplay/fishing/astral_crate"));
    public static Block END_CRATE = registerCrate("end_crate", Block.Settings.copy(Blocks.END_STONE), AstralCrateBlock::new, new Item.Settings().fireproof().maxCount(8).rarity(Rarity.EPIC), GoFish.id("gameplay/fishing/end_crate"));

    public static <T extends Block> T registerCrate(String name, AbstractBlock.Settings blockSettings, Function<AbstractBlock.Settings, T> blockFunc, Item.Settings settings, Identifier id) {
        var block = blockFunc.apply(blockSettings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, GoFish.id(name))));
        T registeredBlock = Registry.register(Registries.BLOCK, GoFish.id(name), block);
        Registry.register(Registries.ITEM, GoFish.id(name), new CrateItem(block, settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, GoFish.id(name))).useBlockPrefixedTranslationKey(), id));
        ItemGroupEvents.modifyEntriesEvent(GoFish.ITEM_GROUP).register(entries -> entries.add(registeredBlock));
        return registeredBlock;
    }

    public static void init() {
        // NO-OP
    }
}
