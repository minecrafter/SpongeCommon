/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.spongepowered.common.mixin.core.world.gen;

import com.google.common.collect.Lists;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.gen.structure.Village;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.interfaces.world.gen.IFlaggedPopulator;
import org.spongepowered.common.world.gen.WorldGenConstants;

import java.util.List;
import java.util.Random;

/**
 * The placement of villages within a chunk has impact can have impact on other
 * populators. Possibly this should be added as a concept in the API in order to
 * allow better access and control over the function of these populators while
 * preserving vanilla functionality.
 */
@Mixin(MapGenVillage.class)
public abstract class MixinMapGenVillage extends MapGenStructure implements IFlaggedPopulator, Village {

    @Shadow private int terrainType;
    @Shadow private int field_82665_g; // distance
    
    @SuppressWarnings("unchecked")
    private List<BiomeType> validBiomes = Lists.newArrayList(MapGenVillage.villageSpawnBiomes);
    
    @Override
    public void populate(IChunkProvider provider, Chunk chunk, Random rand, List<String> flags) {
        World world = (World) chunk.getWorld();
        boolean flag = generateStructure(world, rand, new ChunkCoordIntPair(chunk.getPosition().getX(), chunk.getPosition().getZ()));
        if (flag) {
            flags.add(WorldGenConstants.VILLAGE_FLAG);
        }
    }
    
    @Override
    public int getSize() {
        return this.terrainType;
    }
    
    @Override
    public void setSize(int range) {
        this.terrainType = range;
    }
    
    @Override
    public int getDistance() {
        return this.field_82665_g;
    }
    
    @Override
    public void setDistance(int range) {
        this.field_82665_g = range;
    }
    
    @Override
    public List<BiomeType> getValidBiomes() {
        return this.validBiomes;
    }

    @Redirect(method = "canSpawnStructureAtCoords", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/biome/WorldChunkManager;areBiomesViable(IIILjava/util/List;)Z") )
    private boolean onAreBiomesViable(WorldChunkManager wcm, int x, int y, int z, List types) {
        //Replace the list with our own
        return wcm.areBiomesViable(x, y, z, this.validBiomes);
    }
    
}
