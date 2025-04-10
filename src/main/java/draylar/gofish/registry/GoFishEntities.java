package draylar.gofish.registry;

import draylar.gofish.GoFish;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GoFishEntities {


    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> entity) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, GoFish.id(name), entity);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> entity) {
        return Registry.register(Registries.ENTITY_TYPE, GoFish.id(name), entity);
    }

    public static void init() {
        // NO-OP
    }

    private GoFishEntities() {
        // NO-OP
    }
}
