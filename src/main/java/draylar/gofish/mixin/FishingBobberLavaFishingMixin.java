package draylar.gofish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import draylar.gofish.item.ExtendedFishingRodItem;
import draylar.gofish.registry.GoFishParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberLavaFishingMixin extends Entity {

    @Shadow public abstract PlayerEntity getPlayerOwner();
    @Shadow public abstract void remove(Entity.RemovalReason reason);
    private FishingBobberLavaFishingMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // this mixin is used to determine whether a bobber is actually bobbing for fish
    @ModifyVariable(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 0),
            index = 2
    )
    private float bobberInLava(float value) {
        BlockPos blockPos = this.getBlockPos();
        FluidState fluidState = this.getWorld().getFluidState(blockPos);

        if (!fluidState.isIn(FluidTags.LAVA)) {
            return value;
        }
        // Fishing rod doesn't set active hand, and can be used in either, so we check both
        Item mainHandItem = getPlayerOwner().getMainHandStack().getItem();
        Item offHandItem = getPlayerOwner().getOffHandStack().getItem();

        // Player is holding extended fishing rod, check if it can be in lava.
        // Otherwise, fallback to default behavior.
        if (mainHandItem instanceof ExtendedFishingRodItem) {
            ExtendedFishingRodItem usedRod = (ExtendedFishingRodItem) mainHandItem;

            if (usedRod.canFishInLava()) {
                return fluidState.getHeight(this.getWorld(), blockPos);
            }
        } else if (offHandItem instanceof ExtendedFishingRodItem) {
            ExtendedFishingRodItem usedRod = (ExtendedFishingRodItem) offHandItem;

            if (usedRod.canFishInLava()) {
                return fluidState.getHeight(this.getWorld(), blockPos);
            }
        }

        if (!getPlayerOwner().isCreative()) {
            getPlayerOwner().getStackInHand(Hand.MAIN_HAND).damage(5, getPlayerOwner(), EquipmentSlot.MAINHAND);
        }

        if (getWorld() instanceof ServerWorld) {
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.LAVA, getX(), getY(), getZ(), 5, 0, 1, 0, 0);
        }

        getPlayerOwner().playSound(SoundEvents.ENTITY_GENERIC_BURN, .5f, 1f);
        remove(RemovalReason.KILLED);

        return value;
    }

    // Original check is used to determine whether the bobber should free-fall.
    // Bobbers shouldn't free-fall through liquid anyways, so we return true for all liquids.
    // note that the original method call is inversed so we also inverse ours
    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 1)
    )
    private boolean fallOutsideLiquid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        return !instance.isIn(FluidTags.LAVA) && original.call(instance, tag);
    }

    @WrapOperation(method = "tickFishingLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean replaceLava(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || instance.isOf(Blocks.LAVA);
    }

    @ModifyArg(method = "tickFishingLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private ParticleEffect replaceLavaParticle(ParticleEffect particle, @Local ServerWorld world, @Local(argsOnly = true) BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getFluidState().isIn(FluidTags.LAVA)) {
            if (particle == ParticleTypes.FISHING) {
                return GoFishParticles.LAVA_FISHING;
            } else {
                return ParticleTypes.LAVA;
            }
        }
        return particle;
    }

    @WrapOperation(
            method = "getPositionType(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/projectile/FishingBobberEntity$PositionType;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    private boolean isInValidLiquid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        return instance.isIn(FluidTags.LAVA) || original.call(instance, tag);
    }
}
