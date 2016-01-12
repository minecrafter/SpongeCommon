/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.world.gen.structure;

import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.PopulatorType;
import org.spongepowered.api.world.gen.structure.JungleTemple;
import org.spongepowered.common.interfaces.world.gen.IMixinMapGenScatteredFeature;
import org.spongepowered.common.util.gen.ChunkBufferPrimer;
import org.spongepowered.common.world.gen.InternalPopulatorTypes;

import java.util.Random;

public class JungleTempleStructure implements JungleTemple {

    private final MapGenScatteredFeature gen;
    private final net.minecraft.world.World world;

    public JungleTempleStructure(net.minecraft.world.World world) {
        this.world = world;
        this.gen = new MapGenScatteredFeature();
        IMixinMapGenScatteredFeature igen = (IMixinMapGenScatteredFeature) this.gen;
        igen.setStructureStart((x, z) -> new Start(this.world, igen.getRandom(), x, z));
    }

    @Override
    public PopulatorType getType() {
        return InternalPopulatorTypes.STRUCTURE;
    }

    @Override
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeArea biomes) {
        int x = buffer.getBlockMin().getX() / 16;
        int z = buffer.getBlockMin().getZ() / 16;
        this.gen.generate(((net.minecraft.world.World) world).getChunkProvider(), (net.minecraft.world.World) world, x, z,
                new ChunkBufferPrimer(buffer));
    }

    @Override
    public void populate(Chunk chunk, Random random) {
        net.minecraft.world.World world = (net.minecraft.world.World) chunk.getWorld();
        this.gen.generateStructure(world, random, new ChunkCoordIntPair(chunk.getBlockMin().getX() / 16, chunk.getBlockMin().getZ() / 16));
    }

    public static class Start extends StructureStart {

        public Start(net.minecraft.world.World world, Random rand, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            BiomeGenBase biomegenbase = world.getBiomeGenForCoords(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8));

            if (biomegenbase == BiomeGenBase.jungle || biomegenbase == BiomeGenBase.jungleHills) {
                ComponentScatteredFeaturePieces.JunglePyramid comp =
                        new ComponentScatteredFeaturePieces.JunglePyramid(rand, chunkX * 16, chunkZ * 16);
                this.components.add(comp);
            }

            this.updateBoundingBox();
        }
    }

}
