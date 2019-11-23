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
            if(override.matches(state)) {
                ItemStack stack = override.getItem(world, state, pos);
                //Builders.LOG.error(stack);
                if(!stack.isEmpty())return stack;
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
                //TODO: Not hardcoding this...
                if(state.getMaterial() == Material.WATER) {
                    return new ItemStack(Items.WATER_BUCKET, 1);
                } else if(state.getMaterial() == Material.LAVA) {
                    return new ItemStack(Items.LAVA_BUCKET, 1);
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
                try {
                    //Why would one need anything else?
                    return state.getBlock().getPickBlock(state, null, world, pos, null);
                } catch(Exception e) {
                    return ItemStack.EMPTY;
                }
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
