package org.spongepowered.common.mixin.core.entity.passive;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
@Mixin(EntityVillager.EmeraldForItems.class)
public class MixinEntityVillagerEmeraldForItems implements TradeOfferGenerator {

    @Shadow public Item sellItem; // Not actually the selling item, it's the buying item: https://github.com/ModCoderPack/MCPBot-Issues/issues/66
    @Shadow public EntityVillager.PriceInfo price;

    @Override
    public TradeOffer apply(Random random) {
        checkNotNull(random, "Random cannot be null!");
        int buyingCount = 1;

        if (this.price != null) {
            buyingCount = this.price.getPrice(random);
        }

        final ItemStack buyingItem = new ItemStack(this.sellItem, buyingCount, 0);
        return (TradeOffer) new MerchantRecipe(buyingItem, Items.emerald);
    }
}
