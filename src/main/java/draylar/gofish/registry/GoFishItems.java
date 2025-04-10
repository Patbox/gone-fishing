package draylar.gofish.registry;

import draylar.gofish.GoFish;
import draylar.gofish.api.SoundInstance;
import draylar.gofish.item.ExtendedFishingRodItem;
import draylar.gofish.item.LureItem;
import draylar.gofish.item.SoulLureItem;
import draylar.gofish.item.TooltippedItem;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class GoFishItems {

    public static final Item BLAZE_ROD = register("blaze_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(125)
            .autosmelt()
            .lavaProof(true).build());

    public static final Item SKELETAL_ROD = register("skeletal_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(75)
            .withCastSound(new SoundInstance(SoundEvents.ENTITY_SKELETON_STEP, 1.0F, SoundInstance.DEFAULT_PITCH))
            .withRetrieveSound(new SoundInstance(SoundEvents.ENTITY_SKELETON_STEP, 0.5F, SoundInstance.DEFAULT_PITCH))
            .lavaProof(true)
            .build());

    public static final Item DIAMOND_REINFORCED_ROD = register("diamond_reinforced_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(300)
            .color(Formatting.AQUA)
            .lavaProof(true)
            .build());

    public static final Item EYE_OF_FISHING = register("ender_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(250)
            .color(Formatting.LIGHT_PURPLE)
            .build());

    public static final Item FROSTED_ROD = register("frosted_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(150)
            .build());

    public static final Item SLIME_ROD = register("slime_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(150)
            .color(Formatting.GREEN)
            .withCastSound(new SoundInstance(SoundEvents.ENTITY_SLIME_JUMP, 0.8F, SoundInstance.DEFAULT_PITCH))
            .withRetrieveSound(new SoundInstance(SoundEvents.ENTITY_SLIME_JUMP, 0.5F, SoundInstance.DEFAULT_PITCH))
            .build());

    public static final Item SOUL_ROD = register("soul_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(250)
            .color(Formatting.LIGHT_PURPLE)
            .baseExperienceGain(5)
            .lavaProof(true)
            .build());

    public static final Item MATRIX_ROD = register("matrix_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(200)
            .color(Formatting.LIGHT_PURPLE)
            .build());

    public static final Item CELESTIAL_ROD = register("celestial_rod", (settings) -> new ExtendedFishingRodItem.Builder(settings)
            .durability(150)
            .color(Formatting.LIGHT_PURPLE)
            .tooltipLines(1)
            .nightLuck()
            .build());

    // fish
    public static final Item ENDER_EEL = register("ender_eel", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build()));
    public static final Item ICICLE_FISH = register("icicle_fish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build(), ConsumableComponents.food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 0, 0), 1)).build()));
    public static final Item LILYFISH = register("lilyfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).saturationModifier(0.5f).build()));
    public static final Item MATRIX_FISH = register("matrix_fish", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.5f).build()));
    public static final Item SEAWEED = register("seaweed", new Item.Settings());
    public static final Item BAKED_SEAWEED = register("baked_seaweed", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.5f).build()));
    public static final Item SEAWEED_EEL = register("seaweed_eel", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build()));
    public static final Item SLIMEFISH = register("slimefish", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.25f).build(),
            ConsumableComponents.food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 5, 0), 1)).build()));
    public static final Item SNOWBALL_FISH = register("snowball_fish", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.25f).build()));
    public static final Item TERRAFISH = register("terrafish", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.5f).build()));
    public static final Item CARROT_CARP = register("carrot_carp", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.25f).build()));
    public static final Item BAKED_CARROT_CARP = register("baked_carrot_carp", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.5f).build()));
    public static final Item OAKFISH = register("oakfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(3).build()));
    public static final Item CHARFISH = register("charfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build(),
            ConsumableComponents.food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 5, 0), 1)).build()));

    // nether
    public static final Item SPIKERFISH = register("spikerfish", new Item.Settings().fireproof());
    public static final Item BLACKSTONE_TROUT = register("blackstone_trout", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.75f).build()).fireproof());
    public static final Item GRILLED_BLACKSTONE_TROUT = register("grilled_blackstone_trout", new Item.Settings().food(new FoodComponent.Builder().nutrition(8).saturationModifier(0.8f).build()).fireproof(), 2);
    public static final Item GRILLED_BLACKSTONE_DELUXE = register("grilled_blackstone_deluxe", new Item.Settings().food(new FoodComponent.Builder().nutrition(11).saturationModifier(0.8f).build()).fireproof(),3);
    public static final Item BONEFISH = register("bonefish", new Item.Settings().fireproof());
    public static final Item GILDED_BLACKSTONE_CARP = register("gilded_blackstone_carp", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.5f).build()).fireproof());
    public static final Item SMOKEY_SALMON = register("smokey_salmon", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.5f).build(),
            ConsumableComponents.food().consumeEffect(
                    new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 15, 0), 1)).build()
    ).fireproof());
    public static final Item SOUL_SALMON = register("soul_salmon", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.75f).build()).fireproof());
    public static final Item MAGMA_COD = register("magma_cod", new Item.Settings().food(new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.5f).build(),
            ConsumableComponents.food().consumeEffect(
                    new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 15, 0), 1)).build()
    ).fireproof());
    public static final Item BASALT_BASS = register("basalt_bass", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.25f).build()).fireproof());
    public static final Item OBSIDIAN_HALIBUT = register("obsidian_halibut", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).saturationModifier(0.25f).build()).fireproof());

    // end
    public static final Item ENDFISH = register("endfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build()));
    public static final Item BAKED_ENDFISH = register("baked_endfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.8f).build()), 2);
    public static final Item ENDFISH_AND_CHORUS = register("endfish_and_chorus", new Item.Settings().food(new FoodComponent.Builder().nutrition(8).saturationModifier(1f).build()), 2);
    public static final Item CHORUS_COD = register("chorus_cod", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).build(), ConsumableComponents.CHORUS_FRUIT).rarity(Rarity.EPIC));
    public static final Item DRAGONFISH = register("dragonfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(8).build()).rarity(Rarity.EPIC));
    public static final Item OMEGA_FLOATER = register("omega_floater", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).build()).rarity(Rarity.EPIC));
    public static final Item PORTAL_PUFFER = register("portal_puffer", new Item.Settings().food(new FoodComponent.Builder().nutrition(4).build()).rarity(Rarity.EPIC));

    // Full Moon fish
    public static final Item LUNARFISH = register("lunarfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build()));
    public static final Item GALAXY_STARFISH = register("galaxy_starfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build()));
    public static final Item STARRY_SALMON = register("starry_salmon", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build()));
    public static final Item NEBULA_SWORDFISH = register("nebula_swordfish", new Item.Settings().food(new FoodComponent.Builder().nutrition(2).build()));
    public static final Item AQUATIC_ASTRAL_STEW = register("aquatic_astral_stew", new Item.Settings().food(new FoodComponent.Builder().nutrition(9).saturationModifier(0.75f).build()), 3);

    // weather
    public static final Item RAINY_BASS = register("rainy_bass", new Item.Settings().food(new FoodComponent.Builder().nutrition(3).saturationModifier(0.5f).build()));
    public static final Item STEAMED_BASS = register("steamed_bass", new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(0.25f).build()), 2);
    public static final Item CLOUDY_CRAB = register("cloudy_crab", new Item.Settings().food(new FoodComponent.Builder().nutrition(3).saturationModifier(0.75f).build()));
    public static final Item THUNDERING_BASS = register("thundering_bass", new Item.Settings().food(new FoodComponent.Builder().nutrition(5).saturationModifier(0.75f).build(), ConsumableComponents.food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.SPEED, 15 * 20), 1)).build()));
    public static final Item SMOKED_CLOUDY_CRAB = register("smoked_cloudy_crab", new Item.Settings().food(new FoodComponent.Builder().nutrition(7).saturationModifier(0.25f).build()));
    public static final Item BLIZZARD_BASS = register("blizzard_bass", new Item.Settings().food(new FoodComponent.Builder().nutrition(3).saturationModifier(0.5f).build()));

    // accessories
    public static final Item GOLDEN_FISH = register("golden_fish", s -> new LureItem(s.maxCount(1).rarity(Rarity.EPIC), 1));
    public static final Item SIMPLE_LURE = register("simple_lure", s -> new LureItem(s.maxCount(1), 1));
    public static final Item SOUL_LURE = register("soul_lure", s -> new SoulLureItem(s.maxCount(1)));

    public static <T extends Item> T register(String name, Function<Item.Settings, T> function) {
        return register(name, new Item.Settings(), function);
    }

    public static Item register(String name, Item.Settings settings) {
        return register(name, settings, SimplePolymerItem::new);
    }

    public static Item register(String name, Item.Settings settings, int lines) {
        return register(name, settings, (s) -> new TooltippedItem(s, lines));
    }

    public static <T extends Item> T register(String name, Item.Settings settings, Function<Item.Settings, T> function) {
        var item = function.apply(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, GoFish.id(name))));
        Registry.register(Registries.ITEM, GoFish.id(name), item);
        ItemGroupEvents.modifyEntriesEvent(GoFish.ITEM_GROUP).register(entries -> entries.add(item));
        return item;
    }

    public static void init() {
        // NO-OP
    }
}
