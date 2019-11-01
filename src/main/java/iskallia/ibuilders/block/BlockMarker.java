package iskallia.ibuilders.block;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitBlock;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            BlockPos closestX = closestMarkerX(world, pos);
            BlockPos closestY = closestMarkerY(world, pos);
            BlockPos closestZ = closestMarkerZ(world, pos);

            if (closestX == null || closestY == null || closestZ == null)
                return true;

            System.out.printf("X:%s Y:%s Z:%s\n", closestX, closestY, closestZ);
        }

        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    private BlockPos closestMarkerY(World world, BlockPos pos) {
        for (int i = 1; i < Y_LIMIT; i++) {
            BlockPos positivePos = pos.add(0, i, 0);
            BlockPos negativePos = pos.add(0, -i, 0);

            if (world.getBlockState(positivePos).getBlock() == InitBlock.MARKER) {
                return positivePos;
            }

            if (world.getBlockState(negativePos).getBlock() == InitBlock.MARKER) {
                return negativePos;
            }
        }

        return null;
    }

    private BlockPos closestMarkerX(World world, BlockPos pos) {
        for (int i = 1; i < X_LIMIT; i++) {
            BlockPos positivePos = pos.add(i, 0, 0);
            BlockPos negativePos = pos.add(-i, 0, 0);

            if (world.getBlockState(positivePos).getBlock() == InitBlock.MARKER) {
                return positivePos;
            }

            if (world.getBlockState(negativePos).getBlock() == InitBlock.MARKER) {
                return negativePos;
            }
        }

        return null;
    }

    private BlockPos closestMarkerZ(World world, BlockPos pos) {
        for (int i = 1; i < Z_LIMIT; i++) {
            BlockPos positivePos = pos.add(0, 0, i);
            BlockPos negativePos = pos.add(0, 0, -i);

            if (world.getBlockState(positivePos).getBlock() == InitBlock.MARKER) {
                return positivePos;
            }

            if (world.getBlockState(negativePos).getBlock() == InitBlock.MARKER) {
                return negativePos;
            }
        }

        return null;
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

}
