package iskallia.ibuilders.block.entity;

import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.world.storage.Schematic;
import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.BlockFacing;
import iskallia.ibuilders.entity.EntityBuilder;
import iskallia.ibuilders.init.InitBlock;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.util.MaterialList;
import iskallia.ibuilders.util.Pair;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TileEntityCreator extends TileEntity implements ITickable {

    protected ItemStackHandler inventory = new ItemStackHandler(3 + 54) {
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

            return stack.getItem() == iskallia.itraders.init.InitItem.SPAWN_EGG_FIGHTER;
        }
    };

    protected BuildersSchematic schematic = null;
    private BlockPos offset = BlockPos.ORIGIN.add(1, 1, 1);
    private EnumFacing axis = EnumFacing.EAST;
    private SchematicTracker schematicTracker;
    private ItemStack lastBlueprint;
    private BuildersSchematic rawSchematic;

    private int layer;
    private List<EntityBuilder> builders = new ArrayList<>();
    private List<Pair<BlockPos, IBlockState>> pendingBlocks = new ArrayList<>();
    private int builderCount = 5;


    public TileEntityCreator() {

    }

    public BuildersSchematic getSchematic() {
        int pendingBlocksCount = (int)this.pendingBlocks.stream().filter(pendingBlock -> pendingBlock.getKey() != null).count();

        if(pendingBlocksCount == 0) {
            return this.schematic;
        }

        BuildersSchematic layeredSchematic = new BuildersSchematic(this.schematic.getWidth(), 1, this.schematic.getLength());

        for(int x = 0; x < this.schematic.getWidth(); x++) {
            for(int z = 0; z < this.schematic.getLength(); z++) {
                BlockPos pos = new BlockPos(x, 0, z);
                layeredSchematic.setBlockState(pos, this.schematic.getBlockState(pos.offset(EnumFacing.UP, this.layer)));
            }
        }

        return layeredSchematic;
    }

    public void setSchematic(BuildersSchematic schematic) {
        this.rawSchematic = schematic;

        if(this.schematicTracker == null) {
            this.schematicTracker = SchematicTracker.get(this.getWorld());
        }

        this.setTransform(this.offset, this.axis, false);
        this.schematicTracker.getAndSetChanged(true);
        this.markDirty();
    }

    public void setTransform(BlockPos offset, EnumFacing axis, boolean align) {
        if(this.rawSchematic == null)return;
        this.offset = offset;
        this.axis = axis;

        if(axis == EnumFacing.EAST) {
            this.schematic = this.rawSchematic;
        } else if(axis == EnumFacing.WEST) {
            this.schematic = this.rotateSchematic(this.rawSchematic, EnumFacing.DOWN);
            this.schematic = this.rotateSchematic(this.schematic, EnumFacing.DOWN);
            if(align)this.offset = this.offset.add(-this.rawSchematic.getWidth() + 1,0,-this.rawSchematic.getLength() + 1);
        } else if(axis == EnumFacing.SOUTH) {
            this.schematic = this.rotateSchematic(this.rawSchematic, EnumFacing.UP);
            if(align)this.offset = this.offset.add(-this.rawSchematic.getLength() + 1,0,0);
        } else if(axis == EnumFacing.NORTH) {
            this.schematic = this.rotateSchematic(this.rawSchematic, EnumFacing.DOWN);
            if(align)this.offset = this.offset.add(0,0, -this.rawSchematic.getWidth() + 1);
        }

        this.schematicTracker.getAndSetChanged(true);
        this.pendingBlocks.clear();
        this.markDirty();
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

        if(this.axis != null) {
            compound.setInteger("Axis", this.axis.ordinal());
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));

        this.offset = compound.hasKey("Offset", Constants.NBT.TAG_LONG) ?
                BlockPos.fromLong(compound.getLong("Offset")) :
                BlockPos.ORIGIN.add(1, 1, 1);

        this.axis = compound.hasKey("Axis", Constants.NBT.TAG_INT) ?
                EnumFacing.values()[compound.getInteger("Axis")] :
                EnumFacing.EAST;

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
        if(this.world.isRemote)return;
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
        this.builderCount = 20;

        this.updatePendingBlock();
        this.updateBuilders();
    }

    private void updatePendingBlock() {
        if(this.schematic == null || this.offset == null)return;

        int pendingBlocksCount = (int)this.pendingBlocks.stream().filter(pendingBlock -> pendingBlock.getKey() != null).count();

        if(pendingBlocksCount < this.builderCount) {
            List<Pair<BlockPos, IBlockState>> blocksNeeded = new ArrayList<>();
            boolean placed = false;
            int startLayer = this.layer;

            for(this.layer = 0; this.layer < this.schematic.getHeight() && !placed; this.layer++) {
                for(int x = 0; x < this.schematic.getWidth(); x += 1) {
                    for(int z = 0; z < this.schematic.getLength(); z += 1) {
                        BlockPos pos = new BlockPos(x, this.layer, z);
                        BlockPos offsettedPos = pos.add(this.offset).add(this.pos);
                        IBlockState wantedState = this.schematic.getBlockState(pos);
                        IBlockState actualState = this.world.getBlockState(offsettedPos);

                        if(wantedState.getMaterial() == Material.AIR)continue;
                        if(!this.isAirOrLiquid(actualState))continue;
                        if(!wantedState.getBlock().canPlaceBlockAt(this.world, offsettedPos))continue;
                        if(wantedState.getBlock() == actualState.getBlock() || !this.isAirOrLiquid(actualState))continue;
                        ItemStack material = MaterialList.getItem(this.world, wantedState, offsettedPos);
                        if(this.getBuildingStack(material, true).isEmpty())continue;

                        blocksNeeded.add(new Pair<>(offsettedPos, wantedState));
                        placed = true;
                    }
                }
            }

            if(placed) {
                this.layer--;
                blocksNeeded.removeIf(this.pendingBlocks::contains);

                while(blocksNeeded.size() > 0 && this.pendingBlocks.size() < this.builderCount) {
                    int i = 0;
                    this.pendingBlocks.add(blocksNeeded.get(i));
                    blocksNeeded.remove(i);
                }

                for(int i = 0; i < this.pendingBlocks.size() && i < this.builders.size(); i++) {
                    Pair<BlockPos, IBlockState> pendingBlock = this.pendingBlocks.get(i);

                    if(blocksNeeded.size() > 0 && pendingBlock.getKey() == null) {
                        EntityBuilder builder = this.builders.get(i);

                        Pair<BlockPos, IBlockState> blockNeeded = null;
                        int index = -1;

                        for (int j = 0; j < blocksNeeded.size(); j++) {
                            Pair<BlockPos, IBlockState> c = blocksNeeded.get(j);

                            if(blockNeeded == null || c.getKey().distanceSq(builder.getPosition()) < blockNeeded.getKey().distanceSq(builder.getPosition())) {
                                blockNeeded = c;
                                index = j;
                            }
                        }

                        pendingBlock.setKey(blockNeeded.getKey());
                        pendingBlock.setValue(blockNeeded.getValue());
                        blocksNeeded.remove(index);
                    }
                }

                this.pendingBlocks = this.pendingBlocks.stream().limit(this.builderCount).collect(Collectors.toList());
            }

            if(startLayer != this.layer) {
                this.schematicTracker.getAndSetChanged(true);
            }

        }

        this.pendingBlocks.forEach(pendingBlock -> {
            BlockPos pendingPos = pendingBlock.getKey();

            if(pendingPos != null) {
                IBlockState wantedState = this.schematic.getBlockState(pendingPos.subtract(this.offset).subtract(this.pos));
                IBlockState actualState = this.world.getBlockState(pendingPos);

                if(wantedState.getBlock() == actualState.getBlock() || !this.isAirOrLiquid(actualState)) {
                    pendingBlock.setKey(null);
                }
            }

            if(pendingBlock.getKey() != null) {
                ItemStack material = MaterialList.getItem(this.world, pendingBlock.getValue(), pendingBlock.getKey());

                if (this.getBuildingStack(material, true).isEmpty()) {
                    pendingBlock.setKey(null);
                }
            }
        });
    }

    public ItemStack getBuildingStack(ItemStack wantedStack, boolean simulate) {
        IBlockState state = this.world.getBlockState(this.pos);
        if(state.getBlock() != InitBlock.CREATOR)return ItemStack.EMPTY;
        EnumFacing facing = state.getValue(BlockFacing.FACING);

        EnumFacing[] directions = new EnumFacing[] {
                facing.getOpposite(),
                facing.getOpposite().rotateYCCW(),
                facing.getOpposite().rotateYCCW().getOpposite()
        };

        ItemStack completedStack = wantedStack.copy();
        int neededCount = wantedStack.getCount();

        for(EnumFacing direction: directions) {
            TileEntity tileEntity = world.getTileEntity(this.pos.offset(direction));
            if(tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction)) {
                IItemHandler itemHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction);

                for(int i = 0; i < itemHandler.getSlots() && neededCount > 0; i++) {
                    ItemStack stackInSlot = itemHandler.getStackInSlot(i);
                    if(stackInSlot.isEmpty())continue;
                    if(!canItemStacksStackRelaxed(wantedStack, stackInSlot))continue;
                    ItemStack extractedStack = itemHandler.extractItem(i, neededCount, simulate);
                    neededCount -= extractedStack.getCount();
                }
            }
        }

        completedStack.shrink(neededCount);
        return completedStack;
    }

    public static boolean canItemStacksStackRelaxed(@Nonnull ItemStack a, @Nonnull ItemStack b)  {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem())
            return false;

        //if (!a.isStackable())
        //    return false;

        // Metadata value only matters when the item has subtypes
        // Vanilla stacks non-subtype items with different metadata together
        // e.g. a stick with metadata 0 and a stick with metadata 1 stack
        if (a.getHasSubtypes() && a.getMetadata() != b.getMetadata())
            return false;

        if (a.hasTagCompound() != b.hasTagCompound())
            return false;

        return (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound())) && a.areCapsCompatible(b);
    }

    private void updateBuilders() {
        if(this.world.isRemote)return;

        this.builders.removeIf(builder -> builder.isDead);

        while(this.builders.size() < this.builderCount) {
            ItemStack subStack = this.getAndRemoveSub();
            if(subStack.isEmpty())break;

            EntityBuilder builder = new EntityBuilder(this.world);
            builder.setPosition(this.pos.getX() + 0.5f, this.pos.getY() + 1.0f, this.pos.getZ() + 0.5f);
            builder.setCreator(this);

            int months = iskallia.itraders.init.InitItem.SPAWN_EGG_FIGHTER.getMonths(subStack);
            months = Math.max(months, 1);
            builder.setBlocksLeft(months * 20 + 10);
            if(subStack.hasDisplayName())builder.setCustomNameTag(subStack.getDisplayName());

            this.world.spawnEntity(builder);
            this.builders.add(builder);
        }
    }

    public ItemStack getAndRemoveSub() {
        for(int i = 3; i < this.inventory.getSlots(); i++) {
            ItemStack stack = this.inventory.getStackInSlot(i).copy();
            if(stack.isEmpty())continue;
            if(stack.getItem() != iskallia.itraders.init.InitItem.SPAWN_EGG_FIGHTER)continue;
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
            return stack;
        }

        return ItemStack.EMPTY;
    }

    public boolean isAirOrLiquid(IBlockState state) {
        return state.getMaterial() == Material.AIR
                || state.getMaterial() == Material.WATER
                || state.getMaterial() == Material.LAVA;
    }

    public BlockPos getOffset() {
        int pendingBlocksCount = (int)this.pendingBlocks.stream().filter(pendingBlock -> pendingBlock.getKey() != null).count();

        if(pendingBlocksCount == 0) {
            return this.offset;
        }

        return this.offset.offset(EnumFacing.UP, this.layer);
    }

    public BlockPos getPendingBlock(EntityBuilder builder) {
        int i = this.builders.indexOf(builder);

        if(i < 0 || i >= this.pendingBlocks.size()) {
            return null;
        }

        return this.pendingBlocks.get(i).getKey();
    }

    public IBlockState getExpectedState(EntityBuilder builder) {
        int i = this.builders.indexOf(builder);

        if(i < 0 || i >= this.pendingBlocks.size()) {
            return null;
        }

        return this.pendingBlocks.get(i).getValue();
    }

}
