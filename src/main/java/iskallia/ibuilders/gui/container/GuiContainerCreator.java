package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.container.ContainerCreator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiContainerCreator extends GuiContainerSchemaInfo {

    public GuiContainerCreator(World world, EntityPlayer player, TileEntityCreator creator) {
        super(new ContainerCreator(world, player, creator));
    }

    @Override
    protected void updateInfoButtons() {
        super.updateInfoButtons();

        //Hide the delete buttons.
        for(int i = 0; i < this.infoButtons.length; i++) {
            this.deleteButtons[i].visible = false;
        }
    }

}
