package draylar.gofish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import draylar.gofish.GoFish;
import draylar.gofish.item.ExtendedFishingRodItem;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberLavaFishingMixin extends Entity {

    @Shadow public abstract PlayerEntity getPlayerOwner();
    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Shadow private FishingBobberEntity.State state;

    private FishingBobberLavaFishingMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private ElementHolder holder;
    private EntityAttachment attachment;
    private ItemDisplayElement bobber;

    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        if (tag == FluidTags.LAVA) {
            return super.updateMovementInFluid(tag, 0.014 * 2);
        }
        return super.updateMovementInFluid(tag, speed);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;II)V",
        at = @At("RETURN")
    )
    public void onInit(EntityType<? extends FishingBobberEntity> type, World world, int luckBonus, int waitTimeReductionTicks, CallbackInfo ci) {
        this.holder = new ElementHolder();
        var stack = Items.TROPICAL_FISH.getDefaultStack();
        stack.set(DataComponentTypes.ITEM_MODEL, GoFish.id("fishing_hook"));
        this.bobber = new ItemDisplayElement(stack);
        this.bobber.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        this.bobber.setScale(new Vector3f(0.5f));
        this.holder.addElement(bobber);
        this.attachment = new EntityAttachment(this.holder, this, true);
    }

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
        )
    )
    public void onTick(CallbackInfo ci, @Local FluidState fluidState) {
        if (fluidState.isIn(FluidTags.LAVA)) {
            if (this.state == FishingBobberEntity.State.BOBBING) {
                if (this.bobber.getHolder() == null) {
                    this.holder.addElement(bobber);
                }
            }
        }
        if (this.state != FishingBobberEntity.State.BOBBING) {
            if (this.bobber.getHolder() != null) {
                this.holder.removeElement(bobber);
            }
        }

        this.bobber.setOnFire(this.isOnFire());
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

        return 0;
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 1)
    )
    private boolean fallOutsideLiquid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        return original.call(instance, tag) || instance.isIn(FluidTags.LAVA);
    }

    @WrapOperation(method = "tickFishingLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean replaceLava(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || instance.isOf(Blocks.LAVA);
    }

    @ModifyArg(method = "tickFishingLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private ParticleEffect replaceLavaParticle(ParticleEffect particle, @Local ServerWorld world, @Local(argsOnly = true) BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getFluidState().isIn(FluidTags.LAVA)) {
            return ParticleTypes.LAVA;
        }
        return particle;
    }

    @WrapOperation(
        method = "tickFishingLogic",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;isSkyVisible(Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    public boolean isSkyVisible(World instance, BlockPos pos, Operation<Boolean> original) {
        // The sky is never visible, dont punish players for not fishing in a sky visible spot
        if (!instance.getDimension().hasSkyLight()) {
            return true;
        }
        return original.call(instance, pos);
    }

    @WrapOperation(
            method = "getPositionType(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/entity/projectile/FishingBobberEntity$PositionType;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z")
    )
    private boolean isInValidLiquid(FluidState instance, TagKey<Fluid> tag, Operation<Boolean> original) {
        return instance.isIn(FluidTags.LAVA) || original.call(instance, tag);
    }
}
