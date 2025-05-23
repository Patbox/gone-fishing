package draylar.gofish.item;

import draylar.gofish.api.*;
import draylar.gofish.registry.GoFishEnchantments;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExtendedFishingRodItem extends FishingRodItem implements PolymerItem {

    private final SoundInstance retrieve;
    private final SoundInstance cast;
    private final int baseLure;
    private final int baseLOTS;
    private final int baseExperience;
    private final boolean autosmelt;
    private final boolean lavaProof;
    private final boolean nightLuck;
    private final Formatting formatting;
    private final int lines;

    public ExtendedFishingRodItem(Settings settings, SoundInstance retrieve, SoundInstance cast, int baseLure, int baseLOTS, int baseExperience, boolean autosmelt, boolean lavaProof, boolean nightLuck, Formatting formatting, int tooltipLines) {
        super(settings);
        this.retrieve = retrieve;
        this.cast = cast;
        this.baseLure = baseLure;
        this.baseLOTS = baseLOTS;
        this.baseExperience = baseExperience;
        this.autosmelt = autosmelt;
        this.lavaProof = lavaProof;
        this.nightLuck = nightLuck;
        this.formatting = formatting;
        this.lines = tooltipLines;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        Random random = world.random;

        if(user.fishHook != null) {
            // Retrieve fishing bobber and damage Fishing Rod
            if(!world.isClient) {
                int damage = user.fishHook.use(heldStack);
                heldStack.damage(damage, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), retrieve.getSound(), SoundCategory.NEUTRAL, retrieve.getVolume(random), retrieve.getPitch(random));
        } else {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), cast.getSound(), SoundCategory.NEUTRAL, cast.getVolume(random), cast.getPitch(random));

            // Summon new fishing bobber
            if(!world.isClient) {
                boolean smeltBuff = false;
                int bonusLure = 0;
                int bonusLuck = 0;
                int bonusExperience = 0;

                // Check for night luck
                if(nightLuck && user.getWorld().isNight()) {
                    bonusLuck++;
                }

                // Find buffing items in player inventory
                List<FishingBonus> found = new ArrayList<>();
                for (ItemStack stack : user.getInventory().getMainStacks()) {
                    Item item = stack.getItem();

                    if(item instanceof FishingBonus) {
                        FishingBonus bonus = (FishingBonus) item;

                        if(!found.contains(bonus)) {
                            if(bonus.shouldApply(world, user)) {
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

                // Calculate lure and luck
                int lure = Math.min((int) (EnchantmentHelper.getFishingTimeReduction((ServerWorld) world, heldStack, user) + baseLure + bonusLure), 5);
                int lots = EnchantmentHelper.getFishingLuckBonus((ServerWorld) world, heldStack, user) + baseLOTS + bonusLuck;

                // Summon bobber with stats
                FishingBobberEntity bobber = new FishingBobberEntity(user, world, lots, lure);
                world.spawnEntity(bobber);
                ((FireproofEntity) bobber).gf_setFireproof(lavaProof);
                ((SmeltingBobber) bobber).gf_setSmelts(smelts);
                ((ExperienceBobber) bobber).gf_setBaseExperience(this.baseExperience + bonusExperience);
            }

            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        return ActionResult.SUCCESS_SERVER;
    }

    @Override
    public Text getName(ItemStack stack) {
        Text name = super.getName(stack);
        if(name instanceof MutableText) {
            ((MutableText) name).formatted(formatting);
        }

        return name;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        if(lines > 0) {
            for (int i = 1; i <= lines; i++) {
                textConsumer.accept(Text.translatable(String.format("%s.tooltip_%d", getTranslationKey(), i)).formatted(Formatting.GRAY));
            }
        }
    }

    public int getBaseExperience() {
        return baseExperience;
    }

    public boolean autosmelts() {
        return autosmelt;
    }

    public boolean canFishInLava() {
        return lavaProof;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.FISHING_ROD;
    }

    public static class Builder {

        private Item.Settings settings = new Item.Settings().maxDamage(100);
        private SoundInstance retrieve = new SoundInstance(SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0F, SoundInstance.DEFAULT_PITCH);
        private SoundInstance cast = new SoundInstance(SoundEvents.ENTITY_FISHING_BOBBER_THROW, 0.5F, SoundInstance.DEFAULT_PITCH);
        private int baseLure = 0;
        private int baseLOTS = 0;
        private int experience = 1;
        private boolean autosmelt = false;
        private boolean lavaProof = false;
        private boolean nightLuck = false;
        private Formatting formatting = Formatting.WHITE;
        private int tooltipLines = 0;

        public Builder(Item.Settings settings) {
            this.settings = settings;
        }

        public Builder withSettings(Item.Settings settings) {
            this.settings = settings;
            return this;
        }

        public Builder durability(int durability) {
            this.settings.maxDamage(durability);
            return this;
        }

        public Builder color(Formatting formatting) {
            this.formatting = formatting;
            return this;
        }

        public Builder withRetrieveSound(SoundInstance sound) {
            this.retrieve = sound;
            return this;
        }

        public Builder withCastSound(SoundInstance sound) {
            this.cast = sound;
            return this;
        }

        public Builder withBaseLure(int lure) {
            this.baseLure = lure;
            return this;
        }

        public Builder withBaseLOTS(int lots) {
            this.baseLOTS = lots;
            return this;
        }

        public Builder baseExperienceGain(int experience) {
            this.experience = experience;
            return this;
        }

        public Builder autosmelt() {
            this.autosmelt = true;
            return this;
        }

        public Builder lavaProof(boolean lavaProof) {
            this.lavaProof = lavaProof;
            return this;
        }

        public Builder nightLuck() {
            this.nightLuck = true;
            return this;
        }

        public Builder tooltipLines(int tooltipLines) {
            this.tooltipLines = tooltipLines;
            return this;
        }

        public ExtendedFishingRodItem build() {
            return new ExtendedFishingRodItem(settings, retrieve, cast, baseLure, baseLOTS, experience, autosmelt, lavaProof, nightLuck, formatting, tooltipLines);
        }
    }
}
