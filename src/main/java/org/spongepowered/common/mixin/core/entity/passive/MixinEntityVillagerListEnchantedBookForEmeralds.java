package org.spongepowered.common.mixin.core.entity.passive;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.item.merchant.generation.TradeOfferGenerator;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

// Note that these mixins will not have to exist once mixing into interfaces is
// added as the only thing needing to be done is a simple default implementation
// with an empty MerchantRecipeList and diff the list with an empty one and
// provide the resulting diff'ed MerchantRecipe (TradeOffer) as the result.
@Mixin(EntityVillager.ListEnchantedBookForEmeralds.class)
public class MixinEntityVillagerListEnchantedBookForEmeralds implements TradeOfferGenerator {

    @Override
    public TradeOffer apply(Random random) {
        checkNotNull(random, "Random cannot be null!");
        Enchantment enchantment = Enchantment.enchantmentsBookList[random.nextInt(Enchantment.enchantmentsBookList.length)];
        int enchantmentLevel = MathHelper.getRandomIntegerInRange(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        ItemStack itemstack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, enchantmentLevel));
        int emeraldCount = 2 + random.nextInt(5 + enchantmentLevel * 10) + 3 * enchantmentLevel;

        if (emeraldCount > 64) {
            emeraldCount = 64;
        }

        return (TradeOffer) new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, emeraldCount), itemstack);
    }
}
