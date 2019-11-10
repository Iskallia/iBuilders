package iskallia.ibuilders.block.entity;

import iskallia.ibuilders.init.InitItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCreator extends TileEntity {

    protected ItemStackHandler inventory = new ItemStackHandler(3) {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if(!this.isItemValid(slot, stack)) {
                return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if(slot == 0) {
                return stack.getItem() == iskallia.itraders.init.InitItem.SPAWN_EGG_FIGHTER;
            } else if(slot == 1) {
                return stack.getItem() == Items.PAPER;
            }

            return false;
        }
    };

    public TileEntityCreator() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Inventory", this.inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        super.readFromNBT(compound);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory);
        }

        return super.getCapability(capability, facing);
    }

}
