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
@Mixin(EntityVillager.ItemAndEmeraldToItem.class)
public class MixinEntityVillagerItemAndEmeraldToItem implements TradeOfferGenerator {

    @Shadow public ItemStack field_179411_a;
    @Shadow public EntityVillager.PriceInfo field_179409_b;
    @Shadow public ItemStack field_179410_c;
    @Shadow public EntityVillager.PriceInfo field_179408_d;

    @Override
    public TradeOffer apply(Random random) {
        checkNotNull(random, "Random cannot be null!");
        int buyingCount = 1;

        if (this.field_179409_b != null) {
            buyingCount = this.field_179409_b.getPrice(random);
        }

        int sellingCount = 1;

        if (this.field_179408_d != null) {
            sellingCount = this.field_179408_d.getPrice(random);
        }

        final ItemStack itemStackBuying = new ItemStack(this.field_179411_a.getItem(), buyingCount, this.field_179411_a.getMetadata());
        final ItemStack emeraldStack = new ItemStack(Items.emerald);
        final ItemStack itemStackSelling = new ItemStack(this.field_179410_c.getItem(), sellingCount, this.field_179410_c.getMetadata());
        return (TradeOffer) new MerchantRecipe(itemStackBuying, emeraldStack, itemStackSelling);
    }
}
