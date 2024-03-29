package iskallia.ibuilders.block;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.gui.GuiHandler;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.Block;
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

public class BlockSchematicTerminal extends Block {

    public BlockSchematicTerminal(String name) {
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
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySchematicTerminal();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)  {
        TileEntity tileentity = world.getTileEntity(pos);

        if(tileentity instanceof TileEntitySchematicTerminal) {
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
        if(!player.isSneaking()) {
            player.openGui(Builders.getInstance(), GuiHandler.SCHEMATIC_TERMINAL, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

}
