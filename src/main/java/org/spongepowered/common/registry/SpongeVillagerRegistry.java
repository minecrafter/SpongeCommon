package org.spongepowered.common.registry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityVillager.EmeraldForItems;
import net.minecraft.entity.passive.EntityVillager.ItemAndEmeraldToItem;
import net.minecraft.entity.passive.EntityVillager.ListEnchantedBookForEmeralds;
import net.minecraft.entity.passive.EntityVillager.ListEnchantedItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.ListItemForEmeralds;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.item.merchant.VillagerRegistry;
import org.spongepowered.api.item.merchant.generation.TradeOfferGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Basically, until Forge figures out their VillagerRegistry stuff, we can only hope to
 * make this useful by enforcing generic villager registrations ourselves.
 * The related Forge PR: https://github.com/MinecraftForge/MinecraftForge/pull/2337
 *
 * Note: This registry is being used by MixinVillager in common as Forge doesn't
 * currently change it.
 */
public final class SpongeVillagerRegistry implements VillagerRegistry {

    public static SpongeVillagerRegistry getInstance() {
        return Holder.INSTANCE;
    }

    private final Map<Career, Multimap<Integer, TradeOfferGenerator>> careerGeneratorMap = new HashMap<>();

    private SpongeVillagerRegistry() {
    }

    @Override
    public Multimap<Integer, TradeOfferGenerator> getTradeOfferLevelMap(Career career) {
        final Multimap<Integer, TradeOfferGenerator> multimap = this.careerGeneratorMap.get(checkNotNull(career, "Career cannot be null!"));
        if (multimap == null) {
            return ImmutableMultimap.of();
        } else {
            return ImmutableMultimap.copyOf(multimap);
        }
    }

    @Override
    public VillagerRegistry addGenerator(Career career, int level, TradeOfferGenerator generator) {
        checkArgument(level > 0, "Career level must be at least greater than zero!");
        checkNotNull(career, "Career cannot be null!");
        checkNotNull(generator, "Generator cannot be null!");
        Multimap<Integer, TradeOfferGenerator> multimap = this.careerGeneratorMap.get(career);
        if (multimap == null) {
            multimap = ArrayListMultimap.create(3, 3);
            this.careerGeneratorMap.put(career, multimap);
        }
        multimap.put(level, generator);
        return this;
    }

    @Override
    public VillagerRegistry addGenerators(Career career, int level, TradeOfferGenerator generator, TradeOfferGenerator... generators) {
        checkArgument(level > 0, "Career level must be at least greater than zero!");
        checkNotNull(career, "Career cannot be null!");
        checkNotNull(generator, "Generator cannot be null!");
        checkNotNull(generators, "Generators cannot be null!");
        Multimap<Integer, TradeOfferGenerator> multimap = this.careerGeneratorMap.get(career);
        List<TradeOfferGenerator> list = new ArrayList<>();
        list.add(generator);
        for (TradeOfferGenerator element : generators) {
            list.add(checkNotNull(element, "TradeOfferGenerator cannot be null!"));
        }
        if (multimap == null) {
            multimap = ArrayListMultimap.create(3, list.size());
            this.careerGeneratorMap.put(career, multimap);
        }
        multimap.putAll(level, list);
        return this;
    }

    @Override
    public VillagerRegistry setGenerators(Career career, int level, List<TradeOfferGenerator> generators) {
        checkArgument(level > 0, "Career level must be at least greater than zero!");
        checkNotNull(career, "Career cannot be null!");
        checkNotNull(generators, "Generators cannot be null!");
        Multimap<Integer, TradeOfferGenerator> multimap = this.careerGeneratorMap.get(career);
        if (multimap == null) {
            multimap = ArrayListMultimap.create(3, generators.size());
            this.careerGeneratorMap.put(career, multimap);
        }
        multimap.replaceValues(level, generators);
        return this;
    }

    @Override
    public VillagerRegistry setGenerators(Career career, Multimap<Integer, TradeOfferGenerator> generatorMap) {
        checkNotNull(career, "Career cannot be null!");
        checkNotNull(generatorMap, "Generators cannot be null!");
        Multimap<Integer, TradeOfferGenerator> multimap = this.careerGeneratorMap.get(career);
        if (multimap != null) {
            multimap.clear();
        }
        multimap = ArrayListMultimap.create(generatorMap);
        this.careerGeneratorMap.put(career, multimap);
        return this;
    }

    static void registerVanillaTrades() {
        VillagerRegistry instance = getInstance();

        { // Farmers
            { // Farmer
                instance.setGenerators(Careers.FARMER, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.wheat, new PriceInfo(18, 22))),
                        generatorFor(new EmeraldForItems(Items.potato, new PriceInfo(15, 19))),
                        generatorFor(new EmeraldForItems(Items.carrot, new PriceInfo(15, 19))),
                        generatorFor(new ListItemForEmeralds(Items.bread, new PriceInfo(-4, -2)))
                ))
                        .setGenerators(Careers.FARMER, 2, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Item.getItemFromBlock(Blocks.pumpkin), new PriceInfo(8, 13))),
                                generatorFor(new ListItemForEmeralds(Items.pumpkin_pie, new PriceInfo(-3, -2)))
                        ))
                        .setGenerators(Careers.FARMER, 3, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Item.getItemFromBlock(Blocks.melon_block), new PriceInfo(7, 12))),
                                generatorFor(new ListItemForEmeralds(Items.apple, new PriceInfo(-5, -7)))
                        ))
                        .setGenerators(Careers.FARMER, 4, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.cookie, new PriceInfo(-6, -10))),
                                generatorFor(new ListItemForEmeralds(Items.cake, new PriceInfo(1, 1)))
                        ));
            }
            { // Fisherman
                instance.setGenerators(Careers.FISHERMAN, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.string, new PriceInfo(15, 20))),
                        generatorFor(new EmeraldForItems(Items.coal, new PriceInfo(16, 24))),
                        generatorFor(new ItemAndEmeraldToItem(Items.fish, new PriceInfo(6, 6), Items.cooked_fish, new PriceInfo(6, 6)))
                ))
                        .setGenerators(Careers.FISHERMAN, 2, ImmutableList.of(
                                generatorFor(new ListEnchantedItemForEmeralds(Items.fishing_rod, new PriceInfo(7, 8)))
                        ));
            }
            { // Shepherd
                instance.setGenerators(Careers.SHEPHERD, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Item.getItemFromBlock(Blocks.wool), new PriceInfo(16, 22))),
                        generatorFor(new ListItemForEmeralds(Items.shears, new PriceInfo(3, 4)))
                ))
                        .setGenerators(Careers.SHEPHERD, 2, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 0), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 1), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 2), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 3), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 4), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 5), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 6), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 7), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 8), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 9), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 10), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 11), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 12), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 13), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 14), new PriceInfo(1, 2))),
                                generatorFor(new ListItemForEmeralds(new ItemStack(Blocks.wool, 1, 15), new PriceInfo(1, 2)))
                        ));
            }
            { // Fletcher
                instance.setGenerators(Careers.FLETCHER, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.string, new PriceInfo(15, 20))),
                        generatorFor(new ListItemForEmeralds(Items.arrow, new PriceInfo(-12, -8)))
                ))
                        .setGenerators(Careers.FLETCHER, 2, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.bow, new PriceInfo(2, 3))),
                                generatorFor(new ItemAndEmeraldToItem(Item.getItemFromBlock(Blocks.gravel), new PriceInfo(10, 10), Items.flint,
                                        new PriceInfo(6, 10)))
                        ));
            }
        }
        { // Librarian
            { // Librarian
                instance.setGenerators(Careers.LIBRARIAN, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.paper, new PriceInfo(24, 36))),
                        generatorFor(new ListEnchantedBookForEmeralds())
                ))
                        .setGenerators(Careers.LIBRARIAN, 2, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.book, new PriceInfo(8, 10))),
                                generatorFor(new ListItemForEmeralds(Items.compass, new PriceInfo(10, 12))),
                                generatorFor(new ListItemForEmeralds(Item.getItemFromBlock(Blocks.bookshelf), new PriceInfo(3, 4)))
                        ))
                        .setGenerators(Careers.LIBRARIAN, 3, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.written_book, new PriceInfo(2, 2))),
                                generatorFor(new ListItemForEmeralds(Items.clock, new PriceInfo(10, 12))),
                                generatorFor(new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glass), new PriceInfo(-5, -3)))
                        ))
                        .setGenerators(Careers.LIBRARIAN, 4, ImmutableList.of(
                                generatorFor(new ListEnchantedBookForEmeralds())
                        ))
                        .setGenerators(Careers.LIBRARIAN, 5, ImmutableList.of(
                                generatorFor(new ListEnchantedBookForEmeralds())
                        ))
                        .setGenerators(Careers.LIBRARIAN, 6, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.name_tag, new PriceInfo(20, 22)))
                        ));
            }
        }
        { // Priest
            { // Cleric
                instance.setGenerators(Careers.CLERIC, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.rotten_flesh, new PriceInfo(36, 40))),
                        generatorFor(new EmeraldForItems(Items.gold_ingot, new PriceInfo(8, 10)))
                ))
                        .setGenerators(Careers.CLERIC, 2, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.redstone, new PriceInfo(-4, -1))),
                                generatorFor(
                                        new ListItemForEmeralds(new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new PriceInfo(-2, -1)))
                        ))
                        .setGenerators(Careers.CLERIC, 3, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.ender_eye, new PriceInfo(7, 11))),
                                generatorFor(new ListItemForEmeralds(Item.getItemFromBlock(Blocks.glowstone), new PriceInfo(-3, -1)))
                        ))
                        .setGenerators(Careers.CLERIC, 4, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.experience_bottle, new PriceInfo(3, 11)))
                        ));
            }
        }
        { // Blacksmith
            { // Armorer
                instance.setGenerators(Careers.ARMORER, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.coal, new PriceInfo(16, 24))),
                        generatorFor(new ListItemForEmeralds(Items.iron_helmet, new PriceInfo(4, 6)))
                ))
                        .setGenerators(Careers.ARMORER, 2, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9))),
                                generatorFor(new ListItemForEmeralds(Items.iron_chestplate, new PriceInfo(10, 14)))
                        ))
                        .setGenerators(Careers.ARMORER, 3, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.diamond, new PriceInfo(3, 4))),
                                generatorFor(new ListEnchantedItemForEmeralds(Items.diamond_chestplate, new PriceInfo(16, 19)))
                        ))
                        .setGenerators(Careers.ARMORER, 4, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.chainmail_boots, new PriceInfo(5, 7))),
                                generatorFor(new ListItemForEmeralds(Items.chainmail_leggings, new PriceInfo(9, 11))),
                                generatorFor(new ListItemForEmeralds(Items.chainmail_helmet, new PriceInfo(5, 7))),
                                generatorFor(new ListItemForEmeralds(Items.chainmail_chestplate, new PriceInfo(11, 15)))
                        ));
            }
            { // Weapon Smith
                instance.setGenerators(Careers.WEAPON_SMITH, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.coal, new PriceInfo(16, 24))),
                        generatorFor(new ListItemForEmeralds(Items.iron_axe, new PriceInfo(6, 8)))
                ))
                        .setGenerators(Careers.WEAPON_SMITH, 2, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9))),
                                generatorFor(new ListEnchantedItemForEmeralds(Items.iron_sword, new PriceInfo(9, 10)))
                        ))
                        .setGenerators(Careers.WEAPON_SMITH, 3, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.diamond, new PriceInfo(3, 4))),
                                generatorFor(new ListEnchantedItemForEmeralds(Items.diamond_sword, new PriceInfo(12, 15))),
                                generatorFor(new ListEnchantedItemForEmeralds(Items.diamond_axe, new PriceInfo(9, 12)))
                        ));
            }
            { // Tool Smith
                instance.setGenerators(Careers.TOOL_SMITH, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.coal, new PriceInfo(16, 24))),
                        generatorFor(new ListEnchantedItemForEmeralds(Items.iron_shovel, new PriceInfo(5, 7)))
                ))
                        .setGenerators(Careers.TOOL_SMITH, 2, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.iron_ingot, new PriceInfo(7, 9))),
                                generatorFor(new ListEnchantedItemForEmeralds(Items.iron_pickaxe, new PriceInfo(9, 11)))
                        ))
                        .setGenerators(Careers.TOOL_SMITH, 3, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.diamond, new PriceInfo(3, 4))),
                                generatorFor(new ListEnchantedItemForEmeralds(Items.diamond_pickaxe, new PriceInfo(12, 15)))
                        ));
            }
        }
        { // Butcher
            { // Butcher
                instance.setGenerators(Careers.BUTCHER, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.porkchop, new PriceInfo(14, 18))),
                        generatorFor(new EmeraldForItems(Items.chicken, new PriceInfo(14, 18)))
                ))
                        .setGenerators(Careers.BUTCHER, 2, ImmutableList.of(
                                generatorFor(new EmeraldForItems(Items.coal, new PriceInfo(16, 24))),
                                generatorFor(new ListItemForEmeralds(Items.cooked_porkchop, new PriceInfo(-7, -5))),
                                generatorFor(new ListItemForEmeralds(Items.cooked_chicken, new PriceInfo(-8, -6)))
                        ));
            }
            { // Leather Worker
                instance.setGenerators(Careers.LEATHERWORKER, 1, ImmutableList.of(
                        generatorFor(new EmeraldForItems(Items.leather, new PriceInfo(9, 12))),
                        generatorFor(new ListItemForEmeralds(Items.leather_leggings, new PriceInfo(2, 4)))
                ))
                        .setGenerators(Careers.LEATHERWORKER, 2, ImmutableList.of(
                                generatorFor(new ListEnchantedItemForEmeralds(Items.leather_chestplate, new PriceInfo(7, 12)))
                        ))
                        .setGenerators(Careers.LEATHERWORKER, 3, ImmutableList.of(
                                generatorFor(new ListItemForEmeralds(Items.saddle, new PriceInfo(8, 10)))
                        ));
            }
        }
    }

    private static TradeOfferGenerator generatorFor(EntityVillager.ITradeList iTradeList) {
        return (TradeOfferGenerator) iTradeList;
    }

    private static final class Holder {
        private static final SpongeVillagerRegistry INSTANCE = new SpongeVillagerRegistry();
    }
}
