package draylar.gofish.registry;

import draylar.gofish.GoFish;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

public class GoFishEnchantments {

    public static final ComponentType<Unit> DEEPFRY = register("deepfry", ComponentType.<Unit>builder().codec(Unit.CODEC).build());

    public static <A, T extends ComponentType<A>> T register(String name, T enchantment) {
        PolymerComponent.registerEnchantmentEffectComponent(enchantment);
        return Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, GoFish.id(name), enchantment);
    }

    public static void init() {
        // NO-OP
    }

    private GoFishEnchantments() {
        // NO-OP
    }
}
