package draylar.gofish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import draylar.gofish.api.ExperienceBobber;
import draylar.gofish.api.FireproofEntity;
import draylar.gofish.api.FishingBonus;
import draylar.gofish.api.SmeltingBobber;
import draylar.gofish.item.ExtendedFishingRodItem;
import draylar.gofish.registry.GoFishEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(FishingRodItem.class)
public class FishingRodPropertyMixin {

    @Unique private PlayerEntity player;
    @Unique private ItemStack heldStack;

    @Inject(method = "use", at = @At("HEAD"))
    private void storeContext(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.heldStack = user.getStackInHand(hand);
        this.player = user;
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawn(Lnet/minecraft/entity/projectile/ProjectileEntity;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/projectile/ProjectileEntity;"))
    private ProjectileEntity modifyBobber(ProjectileEntity entity, ServerWorld world, ItemStack projectileStack, Operation<ProjectileEntity> operation) {
        if(entity instanceof FishingBobberEntity bobber) {
            modifyBobber(world, bobber);
        }

        return operation.call(entity, world, projectileStack);
    }

    @Unique
    private void modifyBobber(World world, FishingBobberEntity bobber) {
        boolean smeltBuff = false;
        int bonusLure = 0;
        int bonusLuck = 0;
        int bonusExperience = 0;

        // Find buffing items in player inventory
        List<FishingBonus> found = new ArrayList<>();
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            Item item = stack.getItem();

            if (item instanceof FishingBonus bonus) {
                if (!found.contains(bonus)) {
                    if(bonus.shouldApply(world, player)) {
                        found.add(bonus);
                        smeltBuff = bonus.providesAutosmelt() || smeltBuff;
                        bonusLure += bonus.getLure();
                        bonusLuck += bonus.getLuckOfTheSea();
                        bonusExperience += bonus.getBaseExperience();
                    }
                }
            }
        }

        // Check if this rod autosmelts
        boolean hasDeepfryEnchantment = EnchantmentHelper.hasAnyEnchantmentsWith(heldStack, GoFishEnchantments.DEEPFRY);
        boolean rodAutosmelts = heldStack.getItem() instanceof ExtendedFishingRodItem && ((ExtendedFishingRodItem) heldStack.getItem()).autosmelts();
        boolean smelts = hasDeepfryEnchantment || rodAutosmelts || smeltBuff;

        // Modify bobber statistics
        ((FireproofEntity) bobber).gf_setFireproof(false);
        ((SmeltingBobber) bobber).gf_setSmelts(smelts);
        ((ExperienceBobber) bobber).gf_setBaseExperience(1 + bonusExperience);
        FishingBobberEntityAccessor accessor = (FishingBobberEntityAccessor) bobber;
        accessor.setWaitTimeReductionTicks(Math.min((accessor.getWaitTimeReductionTicks() + bonusLure),5));
        accessor.setLuckBonus(accessor.getLuckBonus() + bonusLuck);
    }
}
