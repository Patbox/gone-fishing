package draylar.gofish.mixin;

import draylar.gofish.api.SmeltingBobber;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberAutosmeltMixin extends Entity implements SmeltingBobber {

    private FishingBobberAutosmeltMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private boolean gf_smelts = false;

    @Override
    public boolean gf_canSmelt() {
        return gf_smelts;
    }

    @Override
    public void gf_setSmelts(boolean value) {
        this.gf_smelts = value;
    }

    @ModifyVariable(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V",
                    shift = At.Shift.AFTER
            ),
            index = 9
    )
    private ItemEntity processOutput(ItemEntity itemEntity) {
        if (this.getWorld() instanceof ServerWorld world) {
            if (gf_smelts) {
                Optional<RecipeEntry<SmeltingRecipe>> cooked = world.getRecipeManager().getFirstMatch(
                        RecipeType.SMELTING,
                        new SingleStackRecipeInput(itemEntity.getStack()),
                        getWorld()
                );

                cooked.ifPresent(smeltingRecipe -> itemEntity.setStack(smeltingRecipe.value().craft(
                        new SingleStackRecipeInput(itemEntity.getStack()), getWorld().getRegistryManager())));
            }
        }

        return itemEntity;
    }
}
