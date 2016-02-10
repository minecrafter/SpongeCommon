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
package org.spongepowered.common.world.extent;

import static com.google.common.base.Preconditions.checkArgument;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.Functional;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.common.util.VecHelper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class ExtentViewDownsize implements DefaultedExtent {

    private final Extent extent;
    private final Vector3i blockMin;
    private final Vector3i blockMax;
    private final Vector3i blockSize;
    private final Vector2i biomeMin;
    private final Vector2i biomeMax;
    private final Vector2i biomeSize;

    public ExtentViewDownsize(Extent extent, Vector3i blockMin, Vector3i blockMax) {
        this.extent = extent;
        this.blockMin = blockMin;
        this.blockMax = blockMax;
        this.blockSize = this.blockMax.sub(this.blockMin).add(Vector3i.ONE);
        this.biomeMin = blockMin.toVector2(true);
        this.biomeMax = blockMax.toVector2(true);
        this.biomeSize = this.biomeMax.sub(this.biomeMin).add(Vector2i.ONE);
    }

    @Override
    public UUID getUniqueId() {
        return this.extent.getUniqueId();
    }

    @Override
    public boolean isLoaded() {
        return this.extent.isLoaded();
    }

    @Override
    public Vector2i getBiomeMin() {
        return this.biomeMin;
    }

    @Override
    public Vector2i getBiomeMax() {
        return this.biomeMax;
    }

    @Override
    public Vector2i getBiomeSize() {
        return this.biomeSize;
    }

    @Override
    public boolean containsBiome(int x, int z) {
        return VecHelper.inBounds(x, z, this.biomeMin, this.biomeMax);
    }

    private void checkRange(int x, int z) {
        if (!VecHelper.inBounds(x, z, this.biomeMin, this.biomeMax)) {
            throw new PositionOutOfBoundsException(new Vector2i(x, z), this.biomeMin, this.biomeMax);
        }
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        checkRange(x, z);
        return this.extent.getBiome(x, z);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        checkRange(x, z);
        this.extent.setBiome(x, z, biome);
    }

    @Override
    public Vector3i getBlockMax() {
        return this.blockMax;
    }

    @Override
    public Vector3i getBlockMin() {
        return this.blockMin;
    }

    @Override
    public Vector3i getBlockSize() {
        return this.blockSize;
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax);
    }

    private void checkRange(double x, double y, double z) {
        if (!VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax)) {
            throw new PositionOutOfBoundsException(new Vector3d(x, y, z), this.blockMin.toDouble(), this.blockMax.toDouble());
        }
    }

    private void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.blockMin, this.blockMax)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.blockMin, this.blockMax);
        }
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getBlockType(x, y, z);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getBlock(x, y, z);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        checkRange(x, y, z);
        this.extent.setBlock(x, y, z, block);
    }

    @Override
    public Location<? extends Extent> getLocation(Vector3i position) {
        return new Location<Extent>(this, position);
    }

    @Override
    public Location<? extends Extent> getLocation(Vector3d position) {
        return new Location<Extent>(this, position);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block, boolean notifyNeighbors) {
        checkRange(x, y, z);
        this.extent.setBlock(x, y, z, block, notifyNeighbors);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState blockState, boolean notifyNeighbors, Cause cause) {
        checkArgument(cause.root() instanceof PluginContainer, "PluginContainer must be at the ROOT of a cause!");
        this.extent.setBlock(x, y, z, blockState, notifyNeighbors, cause);
    }

    @Override
    public BlockSnapshot createSnapshot(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.createSnapshot(x, y, z);
    }

    @Override
    public boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        final Vector3i position = snapshot.getPosition();
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.restoreSnapshot(snapshot, force, notifyNeighbors);
    }

    @Override
    public boolean restoreSnapshot(int x, int y, int z, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        checkRange(x, y, z);
        return this.extent.restoreSnapshot(x, y, z, snapshot, force, notifyNeighbors);
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getScheduledUpdates(x, y, z);
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(int x, int y, int z, int priority, int ticks) {
        checkRange(x, y, z);
        return this.extent.addScheduledUpdate(x, y, z, priority, ticks);
    }

    @Override
    public void removeScheduledUpdate(int x, int y, int z, ScheduledBlockUpdate update) {
        checkRange(x, y, z);
        this.extent.removeScheduledUpdate(x, y, z, update);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Class<T> propertyClass) {
        checkRange(x, y, z);
        return this.extent.getProperty(x, y, z, propertyClass);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(int x, int y, int z, Direction direction, Class<T> propertyClass) {
        checkRange(x, y, z);
        return this.extent.getProperty(x, y, z, direction, propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getProperties(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getProperties(x, y, z);
    }

    @Override
    public Collection<Direction> getFacesWithProperty(int x, int y, int z, Class<? extends Property<?, ?>> propertyClass) {
        checkRange(x, y, z);
        return this.extent.getFacesWithProperty(x, y, z, propertyClass);
    }

    @Override
    public <E> Optional<E> get(int x, int y, int z, Key<? extends BaseValue<E>> key) {
        checkRange(x, y, z);
        return this.extent.get(x, y, z, key);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.get(x, y, z, manipulatorClass);
    }

    @Override
    public Set<ImmutableValue<?>> getValues(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getValues(x, y, z);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.getOrCreate(x, y, z, manipulatorClass);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        checkRange(x, y, z);
        return this.extent.getValue(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, Key<?> key) {
        checkRange(x, y, z);
        return this.extent.supports(x, y, z, key);
    }

    @Override
    public boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.supports(x, y, z, manipulatorClass);
    }

    @Override
    public Set<Key<?>> getKeys(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getKeys(x, y, z);
    }

    @Override
    public <E> DataTransactionResult offer(int x, int y, int z, Key<? extends BaseValue<E>> key, E value) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, key, value);
    }

    @Override
    public DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        checkRange(x, y, z);
        return this.extent.offer(x, y, z, manipulator, function);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        checkRange(x, y, z);
        return this.extent.remove(x, y, z, key);
    }

    @Override
    public DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        checkRange(x, y, z);
        return this.extent.remove(x, y, z, manipulatorClass);
    }

    @Override
    public DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        checkRange(x, y, z);
        return this.extent.undo(x, y, z, result);
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getManipulators(x, y, z);
    }

    @Override
    public boolean validateRawData(int x, int y, int z, DataView container) {
        checkRange(x, y, z);
        return this.extent.validateRawData(x, y, z, container);
    }

    @Override
    public void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        checkRange(x, y, z);
        this.extent.setRawData(x, y, z, container);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        checkRange(xTo, yTo, zTo);
        return this.extent.copyFrom(xTo, yTo, zTo, from);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        checkRange(xTo, yTo, zTo);
        return this.extent.copyFrom(xTo, yTo, zTo, from, function);
    }

    @Override
    public DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        checkRange(xTo, yTo, zTo);
        checkRange(xFrom, yFrom, zFrom);
        return this.extent.copyFrom(xTo, yTo, zTo, xFrom, yFrom, zFrom, function);
    }

    @Override
    public Collection<TileEntity> getTileEntities() {
        final Collection<TileEntity> tileEntities = this.extent.getTileEntities();
        for (Iterator<TileEntity> iterator = tileEntities.iterator(); iterator.hasNext(); ) {
            final TileEntity tileEntity = iterator.next();
            final Location<World> block = tileEntity.getLocation();
            if (!VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax)) {
                iterator.remove();
            }
        }
        return tileEntities;
    }

    @Override
    public Collection<TileEntity> getTileEntities(Predicate<TileEntity> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        return this.extent.getTileEntities(Functional.predicateAnd(input -> {
            final Location<World> block = input.getLocation();
            return VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax);
        }, filter));
    }

    @Override
    public Optional<TileEntity> getTileEntity(int x, int y, int z) {
        checkRange(x, y, z);
        return this.extent.getTileEntity(x, y, z);
    }

    @Override
    public boolean spawnEntity(Entity entity, Cause cause) {
        final Location<World> location = entity.getLocation();
        checkRange(location.getX(), location.getY(), location.getZ());
        return this.extent.spawnEntity(entity, cause);
    }

    @Override
    public Collection<Entity> getEntities() {
        final Collection<Entity> entities = this.extent.getEntities();
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            final Entity tileEntity = iterator.next();
            final Location<World> block = tileEntity.getLocation();
            if (!VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax)) {
                iterator.remove();
            }
        }
        return entities;
    }

    @Override
    public Collection<Entity> getEntities(Predicate<Entity> filter) {
        // Order matters! Bounds filter before the argument filter so it doesn't see out of bounds entities
        return this.extent.getEntities(Functional.predicateAnd(input -> {
            final Location<World> block = input.getLocation();
            return VecHelper.inBounds(block.getX(), block.getY(), block.getZ(), this.blockMin, this.blockMax);
        }, filter));
    }

    @Override
    public Optional<Entity> createEntity(EntityType type, Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(type, position);
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer) {
        // TODO once entity containers are implemented
        //checkRange(position.getX(), position.getY(), position.getZ());
        return Optional.empty();
    }

    @Override
    public Optional<Entity> createEntity(DataContainer entityContainer, Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.createEntity(entityContainer, position);
    }

    @Override
    public Optional<Entity> restoreSnapshot(EntitySnapshot snapshot, Vector3d position) {
        checkRange(position.getX(), position.getY(), position.getZ());
        return this.extent.restoreSnapshot(snapshot, position);
    }

    @Override
    public Extent getExtentView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new ExtentViewDownsize(this.extent, newMin, newMax);
    }

    @Override
    public Extent getExtentView(DiscreteTransform3 transform) {
        return new ExtentViewTransform(this, transform);
    }

    @Override public Optional<UUID> getCreator(int x, int y, int z) {
        return this.extent.getCreator(x, y, z);
    }

    @Override public Optional<UUID> getNotifier(int x, int y, int z) {
        return this.extent.getNotifier(x, y, z);
    }

    @Override public void setCreator(int x, int y, int z, @Nullable UUID uuid) {
        this.extent.setCreator(x, y, z, uuid);
    }

    @Override public void setNotifier(int x, int y, int z, @Nullable UUID uuid) {
        this.extent.setNotifier(x, y, z, uuid);
    }

    @Override
    public boolean hitBlock(int x, int y, int z, Direction side, Cause cause) {
        checkRange(x, y, z);
        return this.extent.hitBlock(x, y, z, side, cause);
    }

    @Override
    public boolean interactBlock(int x, int y, int z, Direction side, Cause cause) {
        checkRange(x, y, z);
        return this.extent.interactBlock(x, y, z, side, cause);
    }

    @Override
    public boolean interactBlockWith(int x, int y, int z, ItemStack itemStack, Direction side, Cause cause) {
        checkRange(x, y, z);
        return this.extent.interactBlockWith(x, y, z, itemStack, side, cause);
    }

    @Override
    public boolean placeBlock(int x, int y, int z, BlockState block, Direction side, Cause cause) {
        checkRange(x, y, z);
        return this.extent.placeBlock(x, y, z, block, side, cause);
    }

    @Override
    public boolean digBlock(int x, int y, int z, Cause cause) {
        checkRange(x, y, z);
        return this.extent.digBlock(x, y, z, cause);
    }

    @Override
    public boolean digBlockWith(int x, int y, int z, ItemStack itemStack, Cause cause) {
        checkRange(x, y, z);
        return this.extent.digBlockWith(x, y, z, itemStack, cause);
    }

    @Override
    public int getBlockDigTimeWith(int x, int y, int z, ItemStack itemStack, Cause cause) {
        checkRange(x, y, z);
        return this.extent.getBlockDigTimeWith(x, y, z, itemStack, cause);
    }

}
