package iskallia.ibuilders.gui;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.container.ContainerCreator;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import iskallia.ibuilders.gui.container.GuiContainerCreator;
import iskallia.ibuilders.gui.container.GuiContainerSchematicTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final int SCHEMATIC_TERMINAL = 0;
    public static final int CREATOR = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(id == SCHEMATIC_TERMINAL) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity instanceof TileEntitySchematicTerminal) {
                return new ContainerSchematicTerminal(world, player, (TileEntitySchematicTerminal)tileEntity);
            }
        } else if(id == CREATOR) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity instanceof TileEntityCreator) {
                return new ContainerCreator(world, player, (TileEntityCreator)tileEntity);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(id == SCHEMATIC_TERMINAL) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity instanceof TileEntitySchematicTerminal) {
                return new GuiContainerSchematicTerminal(world, player, (TileEntitySchematicTerminal)tileEntity);
            }
        } else if(id == CREATOR) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity instanceof TileEntityCreator) {
                return new GuiContainerCreator(world, player, (TileEntityCreator)tileEntity);
            }
        }

        return null;
    }

}
