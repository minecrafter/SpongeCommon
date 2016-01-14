package org.spongepowered.common.item.merchant.generation;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.item.inventory.ItemStackGenerator;
import org.spongepowered.api.item.merchant.TradeOffer;
import org.spongepowered.api.item.merchant.generation.TradeOfferGenerator;
import org.spongepowered.api.util.weighted.VariableAmount;

import java.util.Random;

import javax.annotation.Nullable;

public final class SpongeTradeOfferGenerator implements TradeOfferGenerator {

    public static Builder builder() {
        return new Builder();
    }

    private final ItemStackGenerator firstItemGenerator;
    private final ItemStackGenerator sellingItemGenerator;
    @Nullable private final ItemStackGenerator secondItemGenerator;
    private final double experience;
    private final VariableAmount baseUses;
    private final VariableAmount maxUses;

    private SpongeTradeOfferGenerator(Builder builder) {
        this.firstItemGenerator = builder.firstGenerator;
        this.secondItemGenerator = builder.secondGenerator;
        this.sellingItemGenerator = builder.sellingGenerator;
        this.experience = builder.experience;
        this.baseUses = builder.baseUses;
        this.maxUses = builder.maxUses;
    }

    @Override
    public TradeOffer apply(Random random) {
        checkNotNull(random, "Random cannot be null!");
        final TradeOffer.Builder builder = TradeOffer.builder();
        builder.firstBuyingItem(this.firstItemGenerator.apply(random));
        if (this.secondItemGenerator != null) {
            builder.secondBuyingItem(this.secondItemGenerator.apply(random));
        }
        builder.sellingItem(this.sellingItemGenerator.apply(random));
        builder.canGrantExperience(random.nextDouble() < this.experience);
        builder.uses(this.baseUses.getFlooredAmount(random));
        builder.maxUses(this.maxUses.getFlooredAmount(random));
        return builder.build();
    }

    // basically, should be able to just prattle on with BiConsumers
    public static final class Builder implements TradeOfferGenerator.Builder {

        private ItemStackGenerator firstGenerator;
        @Nullable private ItemStackGenerator secondGenerator;
        private ItemStackGenerator sellingGenerator;
        private double experience;
        private VariableAmount baseUses;
        private VariableAmount maxUses;

        @Override
        public TradeOfferGenerator.Builder setItemGenerator(ItemStackGenerator generator) {
            this.firstGenerator = checkNotNull(generator, "ItemStackGenerator cannot be null!");
            return this;
        }

        @Override
        public TradeOfferGenerator.Builder setSecondGenerator(@Nullable ItemStackGenerator generator) {
            this.secondGenerator = generator;
            return this;
        }

        @Override
        public TradeOfferGenerator.Builder setSellingGenerator(ItemStackGenerator sellingGenerator) {
            this.sellingGenerator = checkNotNull(sellingGenerator, "ItemStackGenerator cannot be null!");
            return this;
        }

        @Override
        public TradeOfferGenerator.Builder experienceChance(double experience) {
            this.experience = experience;
            return this;
        }

        @Override
        public TradeOfferGenerator.Builder startingUses(VariableAmount amount) {
            this.baseUses = checkNotNull(amount, "Variable amount cannot be null!");
            return this;
        }

        @Override
        public TradeOfferGenerator.Builder maxUses(VariableAmount amount) {
            this.maxUses = checkNotNull(amount, "Variable amount cannot be null!");
            return this;
        }

        @Override
        public TradeOfferGenerator build() {
            checkState(this.firstGenerator != null, "First item populators cannot be empty! Populate with some BiConsumers!");
            checkState(this.sellingGenerator != null, "Selling item populators cannot be empty! Populate with some BiConsumers!");
            checkState(this.baseUses != null);
            checkState(this.maxUses != null);
            return new SpongeTradeOfferGenerator(this);
        }

        @Override
        public TradeOfferGenerator.Builder from(TradeOfferGenerator value) {
            return null;
        }

        @Override
        public TradeOfferGenerator.Builder reset() {
            this.firstGenerator = null;
            this.secondGenerator = null;
            this.sellingGenerator = null;
            this.experience = 0.5D;
            this.baseUses = null;
            this.maxUses = null;
            return this;
        }
    }

}
