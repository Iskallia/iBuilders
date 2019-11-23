package iskallia.ibuilders.block;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.gui.GuiHandler;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class BlockCreator extends BlockFacing implements ITileEntityProvider {

    public BlockCreator(String name) {
        super(Material.IRON);

        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));

        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCreator();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)  {
        TileEntity tileentity = world.getTileEntity(pos);

        if(tileentity instanceof TileEntityCreator) {
            IItemHandler inventory = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

            if(inventory != null) {
                for (int i = 0; i < inventory.getSlots(); ++i) {
                    ItemStack itemstack = inventory.getStackInSlot(i);

                    if (!itemstack.isEmpty()) {
                        Block.spawnAsEntity(world, pos, itemstack);
                    }
                }
            }
        }

        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(hand == EnumHand.MAIN_HAND && player.getHeldItem(hand).getItem() != InitItem.SCHEMATIC_RELOCATOR) {
            if(!player.isSneaking()) {
                player.openGui(Builders.getInstance(), GuiHandler.CREATOR, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                player.openGui(Builders.getInstance(), GuiHandler.CREATOR_WORKERS, world, pos.getX(), pos.getY(), pos.getZ());
            }

            return true;
        }

        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

}
