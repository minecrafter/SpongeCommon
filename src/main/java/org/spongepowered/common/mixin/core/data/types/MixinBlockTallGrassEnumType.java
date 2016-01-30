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
package org.spongepowered.common.mixin.core.data.types;

import net.minecraft.block.BlockTallGrass;
import org.spongepowered.api.data.type.ShrubType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.text.translation.SpongeTranslation;

@Mixin(BlockTallGrass.EnumType.class)
@Implements(@Interface(iface = ShrubType.class, prefix = "shadow$"))
public abstract class MixinBlockTallGrassEnumType {

    @Shadow @Final private String name;

    private Translation translation;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstructed(String internalName, int internalOrdinal, int meta, String name, CallbackInfo ci) {
        final String translationId;
        if ("dead_bush".equals(name)) {
            translationId = "tile.tallgrass.shrub.name";
        } else if ("tall_grass".equals(name)) {
            translationId = "tile.tallgrass.tallgrass.name";
        } else if ("fern".equals(name)) {
            translationId = "tile.tallgrass.fern.name";
        } else {
            translationId = "tile.tallgrass.name";
        }
        this.translation = new SpongeTranslation(translationId);
    }

    public String shadow$getId() {
        return this.name;
    }

    @Intrinsic
    public String shadow$getName() {
        return this.translation.get();
    }

    public Translation shadow$getTranslation() {
        return this.translation;
    }

}
