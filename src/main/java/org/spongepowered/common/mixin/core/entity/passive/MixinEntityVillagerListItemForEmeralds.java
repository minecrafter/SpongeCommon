package org.spongepowered.common.mixin.core.entity.passive;

import static com.google.common.base.Preconditions.checkNotNull;

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
@Mixin(EntityVillager.ListItemForEmeralds.class)
public class MixinEntityVillagerListItemForEmeralds implements TradeOfferGenerator {

    @Shadow public ItemStack field_179403_a;
    @Shadow public EntityVillager.PriceInfo field_179402_b;

    @Override
    public TradeOffer apply(Random random) {
        checkNotNull(random, "Random cannot be null!");
        int amount = 1;

        if (this.field_179402_b != null) {
            amount = this.field_179402_b.getPrice(random);
        }

        ItemStack itemstack;
        ItemStack itemstack1;

        if (amount < 0) {
            itemstack = new ItemStack(Items.emerald, 1, 0);
            itemstack1 = new ItemStack(this.field_179403_a.getItem(), -amount, this.field_179403_a.getMetadata());
        } else {
            itemstack = new ItemStack(Items.emerald, amount, 0);
            itemstack1 = new ItemStack(this.field_179403_a.getItem(), 1, this.field_179403_a.getMetadata());
        }

        return (TradeOffer) new MerchantRecipe(itemstack, itemstack1);
    }
}
