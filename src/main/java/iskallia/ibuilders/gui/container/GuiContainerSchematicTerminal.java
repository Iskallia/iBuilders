package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiContainerSchematicTerminal extends GuiContainer {

    public GuiContainerSchematicTerminal(World world, EntityPlayer player, TileEntitySchematicTerminal schematicTerminal) {
        super(new ContainerSchematicTerminal(world, player, schematicTerminal));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
    }

}
