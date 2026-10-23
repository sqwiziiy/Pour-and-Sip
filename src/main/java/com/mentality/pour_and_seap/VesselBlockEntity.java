package com.mentality.pour_and_seap;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class VesselBlockEntity extends BlockEntity {
    private ItemStack wine = ItemStack.EMPTY;

    public VesselBlockEntity(BlockPos pos, BlockState state) {
        super(PourAndSeap.VESSEL_ENTITY_TYPE, pos, state);
    }

    public ItemStack wine() {
        return wine.isEmpty() ? ItemStack.EMPTY : wine.copyWithCount(1);
    }

    public void setWine(ItemStack stack) {
        this.wine = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        markDirty();
        if (world != null && !world.isClient)
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!wine.isEmpty()) nbt.put(WineContents.SOURCE_KEY, wine.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        wine = WineContents.fromNbt(nbt);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
