package iskallia.ibuilders.block;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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

}
