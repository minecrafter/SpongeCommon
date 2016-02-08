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
package org.spongepowered.common.mixin.core.world.gen.populators;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.util.BlockPos;
import net.minecraft.world.gen.feature.WorldGenShrub;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.interfaces.world.gen.IMixinWorldGenPopulatorObject;

import java.util.Random;

@Mixin(WorldGenShrub.class)
public abstract class MixinWorldGenShrub implements IMixinWorldGenPopulatorObject {

    private String id;

    @Shadow
    public abstract boolean generate(net.minecraft.world.World worldIn, Random p_180709_2_, BlockPos p_180709_3_);

    @Override
    public void setId(String id) {
        this.id = checkNotNull(id, "id");
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean canPlaceAt(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public void placeObject(World world, Random random, int x, int y, int z) {
        generate((net.minecraft.world.World) world, random, new BlockPos(x, y, z));
    }

}
