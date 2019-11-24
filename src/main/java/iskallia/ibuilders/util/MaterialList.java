package iskallia.ibuilders.util;

import iskallia.ibuilders.Builders;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.ArrayList;
import java.util.List;

public class MaterialList {

    public static final List<Behaviour> OVERRIDES = new ArrayList<>();

    //Convert fluids to the associated bucket.
    public static Behaviour FLUIDS;
    //Convert farmland and path blocks to dirt.
    public static Behaviour FARMLAND_AND_PATH;
    //Double slab blocks require 2 of the associated slab items.
    public static Behaviour DOUBLE_SLAB;
    //Runs the pick block routine.
    public static Behaviour PICK_BLOCK;
    //Converts the block to it's associated item block.
    public static Behaviour ITEM_BLOCK;

    public static ItemStack getItem(World world, IBlockState state, BlockPos pos) {
        initializeOverrides();

        for(Behaviour override: OVERRIDES) {
            try {
                if (override.matches(state)) {
                    ItemStack stack = override.getItem(world, state, pos);
                    //Builders.LOG.error(stack);
                    if (!stack.isEmpty()) return stack;
                }
            } catch(Exception e) {
                //Builders.LOG.error("BlockState [" + state + "] threw an exception when fetching it's item.");
            }
        }

        return ItemStack.EMPTY;
    }

    public static void initializeOverrides() {
        OVERRIDES.clear();

        OVERRIDES.add(FLUIDS = new Behaviour() {
            @Override
            public boolean matches(IBlockState state) {
                return state.getMaterial().isLiquid();
            }

            @Override
            public ItemStack getItem(World world, IBlockState state, BlockPos pos) {
                Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());

                if(fluid != null) {
                    return FluidUtil.getFilledBucket(new FluidStack(fluid, 0));
                }

                return ItemStack.EMPTY;
            }
        });

        OVERRIDES.add(FARMLAND_AND_PATH = new Behaviour() {
            @Override
            public boolean matches(IBlockState state) {
                return state.getBlock() == Blocks.FARMLAND || state.getBlock() == Blocks.GRASS_PATH;
            }

            @Override
            public ItemStack getItem(World world, IBlockState state, BlockPos pos) {
                return new ItemStack(Blocks.DIRT, 1);
            }
        });

        OVERRIDES.add(DOUBLE_SLAB = new Behaviour() {
            @Override
            public boolean matches(IBlockState state) {
                return state.getBlock() instanceof BlockSlab && ((BlockSlab)state.getBlock()).isDouble();
            }

            @Override
            public ItemStack getItem(World world, IBlockState state, BlockPos pos) {
                ItemStack stack = state.getBlock().getItem(world, pos, state);
                stack.setCount(2);
                return stack;
            }
        });

        OVERRIDES.add(PICK_BLOCK = new Behaviour() {
            @Override
            public boolean matches(IBlockState state) {
                return true;
            }

            @Override
            public ItemStack getItem(World world, IBlockState state, BlockPos pos) {
                //Why would one need anything else?
                return state.getBlock().getPickBlock(state, null, world, pos, null);
            }
        });

        OVERRIDES.add(ITEM_BLOCK = new Behaviour() {
            @Override
            public boolean matches(IBlockState state) {
                return true;
            }

            @Override
            public ItemStack getItem(World world, IBlockState state, BlockPos pos) {
                return state.getBlock().getItem(world, pos, state);
            }
        });
    }

    public static abstract class Behaviour {
        public abstract boolean matches(IBlockState state);
        public abstract ItemStack getItem(World world, IBlockState state, BlockPos pos);
    }

    static {
        initializeOverrides();
    }

}
