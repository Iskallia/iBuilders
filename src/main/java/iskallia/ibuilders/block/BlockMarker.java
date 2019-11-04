package iskallia.ibuilders.block;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.entity.TileEntityMarker;
import iskallia.ibuilders.init.InitBlock;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMarker extends BlockDirectional {

    public static final int Z_LIMIT = 10;
    public static final int X_LIMIT = 10;
    public static final int Y_LIMIT = 10;

    protected static final AxisAlignedBB VERTICAL_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);
    protected static final AxisAlignedBB NS_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 1.0D);
    protected static final AxisAlignedBB EW_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);

    public BlockMarker(String name) {
        super(Material.ROCK);

        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));

        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (((EnumFacing) state.getValue(FACING)).getAxis()) {
            case X:
            default:
                return EW_AABB;
            case Z:
                return NS_AABB;
            case Y:
                return VERTICAL_AABB;
        }
    }

    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror((EnumFacing) state.getValue(FACING)));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND && player.getHeldItem(hand).isEmpty()) {
            TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, pos);

            if (markerTileEntity == null)
                return false;

            if (player.isSneaking() && markerTileEntity.isUnset()) {
                int closestX = BlockMarker.closestMarkerX(world, pos);
                int closestY = BlockMarker.closestMarkerY(world, pos);
                int closestZ = BlockMarker.closestMarkerZ(world, pos);

                if (closestX == pos.getX()) {
                    player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.invalid_pos", "X"), true);
                    return false;
                }

                if (closestY == pos.getY()) {
                    player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.invalid_pos", "Y"), true);
                    return false;
                }

                if (closestZ == pos.getZ()) {
                    player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.invalid_pos", "Z"), true);
                    return false;
                }

                boolean success = markerTileEntity.setExtensions(closestX, closestY, closestZ);

                player.sendStatusMessage(new TextComponentTranslation(
                        success ? "use.block_marker.success" : "use.block_marker.failure"), true);


            } else if (player.isSneaking() && markerTileEntity.isMaster()) {
                markerTileEntity.destroyNetwork();
                player.sendStatusMessage(new TextComponentTranslation("use.block_marker.disconnected"), true);

            } else if (!player.isSneaking()) {
                // TODO: Remove. Here only for DEBUG purposes
                System.out.printf("\n%s\nisMaster:%s, isExtension:%s, isUnset:%s\n",
                        markerTileEntity,
                        markerTileEntity.isMaster(),
                        markerTileEntity.isExtension(),
                        markerTileEntity.isUnset());
            }
        }

        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!world.isRemote) {
            TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, pos);

            if (markerTileEntity != null)
                markerTileEntity.destroyNetwork();
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return true;
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = worldIn.getBlockState(pos.offset(facing.getOpposite()));

        if (iblockstate.getBlock() == Blocks.END_ROD) {
            EnumFacing enumfacing = (EnumFacing) iblockstate.getValue(FACING);

            if (enumfacing == facing) {
                return this.getDefaultState().withProperty(FACING, facing.getOpposite());
            }
        }

        return this.getDefaultState().withProperty(FACING, facing);
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState();
        iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(meta));
        return iblockstate;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityMarker();
    }

    public static BuildersSchematic getSchematic(World world, BlockPos pos, int closestX, int closestY, int closestZ) {
        BlockPos minPos = new BlockPos(
                Math.min(pos.getX(), closestX),
                Math.min(pos.getY(), closestY),
                Math.min(pos.getZ(), closestZ));

        BlockPos maxPos = new BlockPos(
                Math.max(pos.getX(), closestX),
                Math.max(pos.getY(), closestY),
                Math.max(pos.getZ(), closestZ));

        int width = maxPos.getX() - minPos.getX() - 1;
        int height = maxPos.getY() - minPos.getY() + 1;
        int length = maxPos.getZ() - minPos.getZ() - 1;

        BuildersSchematic schematic = new BuildersSchematic(width, height, length);

        // Add Blocks
        for (int x = minPos.getX() + 1; x < maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ() + 1; z < maxPos.getZ(); z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    BlockPos schemaPos = new BlockPos(
                            x - minPos.getX() - 1,
                            y - minPos.getY(),
                            z - minPos.getZ() - 1);
                    IBlockState blockState = world.getBlockState(blockPos);
                    TileEntity tileEntity = world.getTileEntity(blockPos);
                    schematic.setBlockState(schemaPos, blockState);
                    schematic.setTileEntity(schemaPos, tileEntity);
                }
            }
        }

        // Add Entities
        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(minPos, maxPos))) {
            if (entity instanceof EntityPlayer || entity.isCreatureType(EnumCreatureType.MONSTER, false))
                continue;

            // TODO update the entity's pos here
            // Then add into the schematic
//            schematic.addEntity(entity);
        }

        return schematic;
    }

    public static int closestMarkerY(World world, BlockPos pos) {
        for (int i = 1; i <= Y_LIMIT; i++) {
            BlockPos positivePos = pos.add(0, i, 0);
            BlockPos negativePos = pos.add(0, -i, 0);

            if (world.getBlockState(positivePos).getBlock() == InitBlock.MARKER) {
                TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, positivePos);
                if (markerTileEntity != null && markerTileEntity.isUnset())
                    return positivePos.getY();
            }

            if (world.getBlockState(negativePos).getBlock() == InitBlock.MARKER) {
                TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, negativePos);
                if (markerTileEntity != null && markerTileEntity.isUnset())
                    return negativePos.getY();
            }
        }

        return pos.getY();
    }

    public static int closestMarkerX(World world, BlockPos pos) {
        for (int i = 1; i <= X_LIMIT; i++) {
            BlockPos positivePos = pos.add(i, 0, 0);
            BlockPos negativePos = pos.add(-i, 0, 0);

            if (world.getBlockState(positivePos).getBlock() == InitBlock.MARKER) {
                TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, positivePos);
                if (markerTileEntity != null && markerTileEntity.isUnset())
                    return positivePos.getX();
            }

            if (world.getBlockState(negativePos).getBlock() == InitBlock.MARKER) {
                TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, negativePos);
                if (markerTileEntity != null && markerTileEntity.isUnset())
                    return negativePos.getX();
            }
        }

        return pos.getX();
    }

    public static int closestMarkerZ(World world, BlockPos pos) {
        for (int i = 1; i <= Z_LIMIT; i++) {
            BlockPos positivePos = pos.add(0, 0, i);
            BlockPos negativePos = pos.add(0, 0, -i);

            if (world.getBlockState(positivePos).getBlock() == InitBlock.MARKER) {
                TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, positivePos);
                if (markerTileEntity != null && markerTileEntity.isUnset())
                    return positivePos.getZ();
            }

            if (world.getBlockState(negativePos).getBlock() == InitBlock.MARKER) {
                TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, negativePos);
                if (markerTileEntity != null && markerTileEntity.isUnset())
                    return negativePos.getZ();
            }
        }

        return pos.getZ();
    }

}
