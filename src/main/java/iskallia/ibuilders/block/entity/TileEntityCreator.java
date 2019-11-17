package iskallia.ibuilders.block.entity;

import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.world.storage.Schematic;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.SchematicTracker;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCreator extends TileEntity implements ITickable {

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
            } else if(slot == 2) {
                return stack.getItem() == InitItem.BLUEPRINT;
            }

            return false;
        }
    };

    protected BuildersSchematic schematic = null;
    private BlockPos offset;
    private SchematicTracker schematicTracker;
    private ItemStack lastBlueprint;
    private BuildersSchematic rawSchematic;

    public TileEntityCreator() {

    }

    public BuildersSchematic getSchematic() {
        return this.schematic;
    }

    public void setSchematic(BuildersSchematic schematic) {
        this.rawSchematic = schematic;
        this.schematic = schematic;

        if(this.schematicTracker == null) {
            this.schematicTracker = SchematicTracker.get(this.getWorld());
        }

        if(this.schematic == null) {
            this.offset = BlockPos.ORIGIN.add(0, 1, 0);
        }

        this.schematicTracker.updateCreator(this);
    }

    public void setTransform(BlockPos offset, EnumFacing axis) {
        if(this.rawSchematic == null)return;
        this.offset = BlockPos.ORIGIN.add(0, 1, 0).add(offset);

        if(axis == EnumFacing.EAST) {
            this.schematic = this.rawSchematic;
        } else if(axis == EnumFacing.WEST) {
            this.schematic = this.rotateSchematic(this.rawSchematic, EnumFacing.DOWN);
            this.schematic = this.rotateSchematic(this.schematic, EnumFacing.DOWN);
            this.offset = this.offset.add(-this.rawSchematic.getWidth() + 1,0,-this.rawSchematic.getLength() + 1);
        } else if(axis == EnumFacing.SOUTH) {
            this.schematic = this.rotateSchematic(this.rawSchematic, EnumFacing.UP);
            this.offset = this.offset.add(-this.rawSchematic.getLength() + 1,0,0);
        } else if(axis == EnumFacing.NORTH) {
            this.schematic = this.rotateSchematic(this.rawSchematic, EnumFacing.DOWN);
            this.offset = this.offset.add(0,0, -this.rawSchematic.getWidth() + 1);
        }

        this.schematicTracker.getAndSetChanged(true);
    }

    private BuildersSchematic rotateSchematic(BuildersSchematic schematic, EnumFacing axis) {
        if(axis != null) {
            try {
                Schematic rotatedSchematic = RotationHelper.INSTANCE.rotate(schematic, axis, true);
                BuildersSchematic newSchematic = BuildersSchematic.fromDefaultSchematic(rotatedSchematic);
                newSchematic.setInfo(schematic.getInfo());
                return newSchematic;
            } catch (RotationHelper.RotationException e) {
                e.printStackTrace();
            }
        }

        return this.rawSchematic;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Inventory", this.inventory.serializeNBT());
        compound.setLong("Offset", this.offset.toLong());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        this.offset = compound.hasKey("Offset", Constants.NBT.TAG_LONG) ?
                BlockPos.fromLong(compound.getLong("Offset")) :
                BlockPos.ORIGIN.add(1, 1, 1);
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

    @Override
    public void update() {
        ItemStack blueprint = this.inventory.getStackInSlot(2);

        if(blueprint != this.lastBlueprint) {
            if(blueprint.getItem() == InitItem.BLUEPRINT && !blueprint.isEmpty()) {
                this.setSchematic(BuildersFormat.INSTANCE.readFromNBT(blueprint.getOrCreateSubCompound("Schematic")));
            } else {
                this.setSchematic(null);
            }
        }

        this.inventory.setStackInSlot(2, blueprint);
        this.lastBlueprint = blueprint;
    }

    public BlockPos getOffset() {
        return this.offset;
    }

}
