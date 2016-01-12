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
package org.spongepowered.common.mixin.core.world.gen;

import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.interfaces.world.gen.IMixinMapGenScatteredFeature;

import java.util.Random;
import java.util.function.BiFunction;

@Mixin(MapGenScatteredFeature.class)
public abstract class MixinMapGenScatteredFeature extends MapGenStructure implements IMixinMapGenScatteredFeature {

    private BiFunction<Integer, Integer, StructureStart> provider;

    @Inject(method = "<init>()V", at = @At("RETURN") )
    public void onConstructed(CallbackInfo ci) {
        this.provider = (x, z) -> new MapGenScatteredFeature.Start(this.worldObj, this.rand, x, z);
    }
    
    @Override
    public void setStructureStart(BiFunction<Integer, Integer, StructureStart> provider) {
        this.provider = provider;
    }
    
    @Override
    public Random getRandom() {
        return this.rand;
    }
    
    /*
     * @author Deamon
     * 
     * @purpose This method is overwritten to instead return our provider
     */
    @Overwrite
    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return this.provider.apply(chunkX, chunkZ);
    }
    
}
