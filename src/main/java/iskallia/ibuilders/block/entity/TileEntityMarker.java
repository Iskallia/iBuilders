package iskallia.ibuilders.block.entity;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import iskallia.ibuilders.init.InitBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class TileEntityMarker extends TileEntitySynchronized {

    public static TileEntityMarker getMarkerTileEntity(World world, BlockPos position) {
        TileEntity tileEntity = world.getTileEntity(position);

        if (!(tileEntity instanceof TileEntityMarker))
            return null;

        return (TileEntityMarker) tileEntity;
    }

    @Nullable // - only non-null when is an extension
    private BlockPos masterPos;

    @Nullable // - only non-null when is a master
    private BlockPos extensionX, extensionY, extensionZ;

    public TileEntityMarker() { super(); }

    public boolean isMaster() {
        return extensionX != null && extensionY != null
                && extensionZ != null && masterPos == null;
    }

    public boolean isExtension() {
        if (masterPos == null) return false;

        if (extensionX != null || extensionY != null || extensionZ != null)
            return false;

        return world.getBlockState(masterPos).getBlock() == InitBlock.MARKER
                && !masterPos.equals(this.pos);
    }

    public boolean isUnset() {
        return !isMaster() && !isExtension();
    }

    public TileEntityMarker getMaster() {
        if (isMaster()) return getMarkerTileEntity(world, this.pos);
        if (isExtension()) return getMarkerTileEntity(world, masterPos);
        return null;
    }

    @Nullable
    public BlockPos getExtensionX() {
        return extensionX;
    }

    @Nullable
    public BlockPos getExtensionY() {
        return extensionY;
    }

    @Nullable
    public BlockPos getExtensionZ() {
        return extensionZ;
    }

    /**
     * Sets master position for an extension
     */
    public void setMasterPos(@Nonnull BlockPos masterPos) {
        this.masterPos = masterPos;

        // Reset extensions
        this.extensionX = null;
        this.extensionY = null;
        this.extensionZ = null;
    }

    private boolean setExtensionX(BlockPos extensionX) {
        IBlockState extensionState = world.getBlockState(extensionX);
        TileEntityMarker extensionTileEntity = getMarkerTileEntity(world, extensionX);

        if (extensionTileEntity == null || !extensionTileEntity.isUnset())
            return false;

        if (extensionState.getBlock() != InitBlock.MARKER)
            return false;

        if (pos.getY() != extensionX.getY() || pos.getZ() != extensionX.getZ())
            return false;

        this.extensionX = extensionX;
        return true;
    }

    private boolean setExtensionY(BlockPos extensionY) {
        IBlockState extensionState = world.getBlockState(extensionY);
        TileEntityMarker extensionTileEntity = getMarkerTileEntity(world, extensionY);

        if (extensionTileEntity == null || !extensionTileEntity.isUnset())
            return false;

        if (extensionState.getBlock() != InitBlock.MARKER)
            return false;

        if (pos.getX() != extensionY.getX() || pos.getZ() != extensionY.getZ())
            return false;

        this.extensionY = extensionY;
        return true;
    }

    private boolean setExtensionZ(BlockPos extensionZ) {
        IBlockState extensionState = world.getBlockState(extensionZ);
        TileEntityMarker extensionTileEntity = getMarkerTileEntity(world, extensionZ);

        if (extensionTileEntity == null || !extensionTileEntity.isUnset())
            return false;

        if (extensionState.getBlock() != InitBlock.MARKER)
            return false;

        if (pos.getX() != extensionZ.getX() || pos.getY() != extensionZ.getY())
            return false;

        this.extensionZ = extensionZ;
        return true;
    }

    public boolean setExtensions(int closestX, int closestY, int closestZ) {
        boolean extensionsSet = setExtensionX(new BlockPos(closestX, pos.getY(), pos.getZ()))
                && setExtensionY(new BlockPos(pos.getX(), closestY, pos.getZ()))
                && setExtensionZ(new BlockPos(pos.getX(), pos.getY(), closestZ));

        if (!extensionsSet) { // Reset them if at least 1 failed to set
            reset();

        } else { // Otherwise set master of extensions
            Objects.requireNonNull(getMarkerTileEntity(world, extensionX)).setMasterPos(pos);
            Objects.requireNonNull(getMarkerTileEntity(world, extensionY)).setMasterPos(pos);
            Objects.requireNonNull(getMarkerTileEntity(world, extensionZ)).setMasterPos(pos);
        }

        markForUpdate();
        return extensionsSet;
    }

    public boolean destroyNetwork() {
        if (isMaster()) {
            Objects.requireNonNull(getMarkerTileEntity(world, extensionX)).reset();
            Objects.requireNonNull(getMarkerTileEntity(world, extensionY)).reset();
            Objects.requireNonNull(getMarkerTileEntity(world, extensionZ)).reset();

            this.reset();
            return true;

        } else if (isExtension()) {
            Objects.requireNonNull(getMarkerTileEntity(world, masterPos)).destroyNetwork();
            this.reset();
            return true;
        }

        return false;
    }

    private void reset() {
        masterPos = null;
        extensionX = null;
        extensionY = null;
        extensionZ = null;
        markForUpdate();
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound); // Keeping just in case

        NBTTagCompound masterPosNBT = null;
        NBTTagCompound extensionXNBT = null;
        NBTTagCompound extensionYNBT = null;
        NBTTagCompound extensionZNBT = null;

        // Fetch NBT compounds
        if (compound.hasKey("MasterPos", Constants.NBT.TAG_COMPOUND))
            masterPosNBT = compound.getCompoundTag("MasterPos");

        if (compound.hasKey("ExtensionX", Constants.NBT.TAG_COMPOUND))
            extensionXNBT = compound.getCompoundTag("ExtensionX");

        if (compound.hasKey("ExtensionY", Constants.NBT.TAG_COMPOUND))
            extensionYNBT = compound.getCompoundTag("ExtensionY");

        if (compound.hasKey("ExtensionZ", Constants.NBT.TAG_COMPOUND))
            extensionZNBT = compound.getCompoundTag("ExtensionZ");

        // Assign fields
        this.masterPos = blockPosFrom(masterPosNBT);
        this.extensionX = blockPosFrom(extensionXNBT);
        this.extensionY = blockPosFrom(extensionYNBT);
        this.extensionZ = blockPosFrom(extensionZNBT);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound); // Keeping just in case

        if (masterPos != null)
            compound.setTag("MasterPos", nbtFrom(masterPos));

        if (extensionX != null)
            compound.setTag("ExtensionX", nbtFrom(extensionX));

        if (extensionY != null)
            compound.setTag("ExtensionY", nbtFrom(extensionY));

        if (extensionZ != null)
            compound.setTag("ExtensionZ", nbtFrom(extensionZ));
    }

    @Override
    public String toString() {
        return String.format("Master:%s, X:%s, Y:%s, Z:%s",
                masterPos, extensionX, extensionY, extensionZ);
    }

    // TODO: Replace below converters with a Helper if present. Extract otherwise
    private NBTTagCompound nbtFrom(BlockPos position) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", position.getX());
        nbt.setInteger("y", position.getY());
        nbt.setInteger("z", position.getZ());
        return nbt;
    }

    private BlockPos blockPosFrom(NBTTagCompound nbt) {
        if (nbt == null) return null;

        if (!nbt.hasKey("x", Constants.NBT.TAG_INT)
                || !nbt.hasKey("y", Constants.NBT.TAG_INT)
                || !nbt.hasKey("z", Constants.NBT.TAG_INT))
            return null;

        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");
        return new BlockPos(x, y, z);
    }

}
