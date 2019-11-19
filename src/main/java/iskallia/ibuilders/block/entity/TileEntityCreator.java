package iskallia.ibuilders.block.entity;

import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.world.storage.Schematic;
import iskallia.ibuilders.Builders;
import iskallia.ibuilders.entity.EntityBuilder;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.SchematicTracker;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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

    private int layer;
    private BlockPos pendingBlock;
    private EntityBuilder builder;
    private IBlockState expectedState;

    public TileEntityCreator() {

    }

    public BuildersSchematic getSchematic() {
        if(this.pendingBlock == null) {
            return this.schematic;
        }

        BuildersSchematic layeredSchematic = new BuildersSchematic(this.schematic.getWidth(), this.schematic.getHeight(), this.schematic.getLength());

        for(int x = 0; x < this.schematic.getWidth(); x++) {
            for(int z = 0; z < this.schematic.getLength(); z++) {
                BlockPos pos = new BlockPos(x, this.layer, z);
                layeredSchematic.setBlockState(pos, this.schematic.getBlockState(pos));
            }
        }

        return layeredSchematic;
    }

    public void setSchematic(BuildersSchematic schematic) {
        this.rawSchematic = schematic;

        if(this.schematicTracker == null) {
            this.schematicTracker = SchematicTracker.get(this.getWorld());
        }

        this.setTransform(BlockPos.ORIGIN, EnumFacing.EAST);
        this.schematicTracker.updateCreator(this);
    }

    public void setTransform(BlockPos offset, EnumFacing axis) {
        if(this.rawSchematic == null)return;
        this.offset = BlockPos.ORIGIN.add(1, 1, 1).add(offset);

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
        this.pendingBlock = null;
        Builders.LOG.warn("Set pending block to " + this.pendingBlock + "!");
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

        if(this.offset != null) {
            compound.setLong("Offset", this.offset.toLong());
        }

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
        this.updatePendingBlock();
        this.updateBuilderEntity();
    }

    private void updatePendingBlock() {
        if(this.schematic == null)return;

        if(this.pendingBlock == null) {
            for(this.layer = 0; this.layer < this.schematic.getHeight(); this.layer++) {

                int dLowX = Math.abs(this.pos.getX() + this.offset.getX() - this.builder.getPosition().getX());
                int dHighX = Math.abs(this.pos.getX() + this.offset.getX() + this.schematic.getWidth() - this.builder.getPosition().getX());
                int xSignum = dLowX < dHighX ? 1 : -1;
                int dLowZ = Math.abs(this.pos.getZ() + this.offset.getZ() - this.builder.getPosition().getZ());
                int dHighZ = Math.abs(this.pos.getZ() + this.offset.getZ() + this.schematic.getLength() - this.builder.getPosition().getZ());
                int zSignum = dLowZ < dHighZ ? 1 : -1;

                for(int x = xSignum == 1 ? 0 : this.schematic.getWidth() - 1; x < this.schematic.getWidth() && x >= 0; x += xSignum) {
                    for(int z = zSignum == 1 ? 0 : this.schematic.getLength() - 1; z < this.schematic.getLength() && z >= 0; z += zSignum) {
                        BlockPos pos = new BlockPos(x, this.layer, z);
                        BlockPos offsettedPos = pos.add(this.offset).add(this.pos);
                        IBlockState wantedState = this.schematic.getBlockState(pos);
                        IBlockState actualState = this.world.getBlockState(offsettedPos);

                        if(wantedState.getMaterial() == Material.AIR)continue;
                        if(!this.isAirOrLiquid(actualState))continue;
                        if(!wantedState.getBlock().canPlaceBlockAt(this.world, offsettedPos))continue;
                        if(wantedState.getBlock() == actualState.getBlock() || !this.isAirOrLiquid(actualState))continue;

                        this.pendingBlock = offsettedPos;
                        this.expectedState = wantedState;
                        Builders.LOG.warn("Set pending block to " + this.pendingBlock + "!");
                        this.schematicTracker.getAndSetChanged(true);
                        return;
                    }
                }
            }
        } else {
            IBlockState wantedState = this.schematic.getBlockState(this.pendingBlock.subtract(this.offset).subtract(this.pos));
            IBlockState actualState = this.world.getBlockState(this.pendingBlock);

            if(wantedState.getBlock() == actualState.getBlock() || !this.isAirOrLiquid(actualState)) {
                this.pendingBlock = null;
                Builders.LOG.warn("Set pending block to " + this.pendingBlock + "!");
                this.updatePendingBlock();
            }
        }
    }

    private void updateBuilderEntity() {
        if(!this.world.isRemote && (this.builder == null || this.builder.isDead)) {
            this.builder = new EntityBuilder(this.world);
            this.builder.setPosition(this.pos.getX() + 0.5f, this.pos.getY() + 1.0f, this.pos.getZ() + 0.5f);
            this.builder.setCreator(this);
            this.world.spawnEntity(this.builder);
        }
    }

    public boolean isAirOrLiquid(IBlockState state) {
        return state.getMaterial() == Material.AIR
                || state.getMaterial() == Material.WATER
                || state.getMaterial() == Material.LAVA;
    }

    public BlockPos getOffset() {
        return this.offset;
    }

    public BlockPos getPendingBlock() {
        return this.pendingBlock;
    }

    public IBlockState getExpectedState() {
        return this.expectedState;
    }

}
