package org.spongepowered.common.mixin.core.entity.passive;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.item.merchant.generation.TradeOfferGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

// Note that these mixins will not have to exist once mixing into interfaces is
// added as the only thing needing to be done is a simple default implementation
// with an empty MerchantRecipeList and diff the list with an empty one and
// provide the resulting diff'ed MerchantRecipe (TradeOffer) as the result.
@Mixin(EntityVillager.ListEnchantedItemForEmeralds.class)
public class MixinEntityVillagerListEnchantedItemForEmeralds implements TradeOfferGenerator {

    @Shadow public ItemStack field_179407_a;
    @Shadow public EntityVillager.PriceInfo field_179406_b;

    @Override
    public TradeOffer apply(Random random) {
        checkNotNull(random, "Random cannot be null!");
        int emeraldCount = 1;

        if (this.field_179406_b != null) {
            emeraldCount = this.field_179406_b.getPrice(random);
        }

        ItemStack itemstack = new ItemStack(Items.emerald, emeraldCount, 0);
        ItemStack itemstack1 = new ItemStack(this.field_179407_a.getItem(), 1, this.field_179407_a.getMetadata());
        itemstack1 = EnchantmentHelper.addRandomEnchantment(random, itemstack1, 5 + random.nextInt(15));
        return (TradeOffer) new MerchantRecipe(itemstack, itemstack1);
    }
}
