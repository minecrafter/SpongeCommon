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
package org.spongepowered.common.registry.type.block;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockQuartz;
import org.spongepowered.api.data.type.QuartzType;
import org.spongepowered.api.data.type.QuartzTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class QuartzTypeRegistryModule implements CatalogRegistryModule<QuartzType> {

    @RegisterCatalog(QuartzTypes.class)
    private final Map<String, QuartzType> quartzTypeMappings = new ImmutableMap.Builder<String, QuartzType>()
        .put("default", (QuartzType) (Object) BlockQuartz.EnumType.DEFAULT)
        .put("chiseled", (QuartzType) (Object) BlockQuartz.EnumType.CHISELED)
        .put("lines_x", (QuartzType) (Object) BlockQuartz.EnumType.LINES_X)
        .put("lines_y", (QuartzType) (Object) BlockQuartz.EnumType.LINES_Y)
        .put("lines_z", (QuartzType) (Object) BlockQuartz.EnumType.LINES_Z)
        .build();

    @Override
    public Optional<QuartzType> getById(String id) {
        return Optional.ofNullable(this.quartzTypeMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<QuartzType> getAll() {
        return ImmutableList.copyOf(this.quartzTypeMappings.values());
    }

}
