package iskallia.ibuilders.util;

import iskallia.ibuilders.Builders;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MaterialList {

    public static final List<Behaviour> OVERRIDES = new ArrayList<>();

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

        OVERRIDES.add(new Behaviour() {
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

        OVERRIDES.add(new Behaviour() {
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

        OVERRIDES.add(new Behaviour() {
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
