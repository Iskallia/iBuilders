package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.container.ContainerCreator;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.C2SCreatorAction;
import iskallia.ibuilders.net.packet.mc.C2STerminalAction;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiContainerCreator extends GuiContainerSchemaInfo {

    public GuiContainerCreator(World world, EntityPlayer player, TileEntityCreator creator) {
        super(new ContainerCreator(world, player, creator));
    }

    @Override
    public void initGui() {
        super.initGui();
        InitPacket.PIPELINE.sendToServer(new C2SCreatorAction(C2SCreatorAction.Action.GET_INFO));
    }

    @Override
    protected void updateInfoButtons() {
        super.updateInfoButtons();

        //Hide the delete buttons.
        for(int i = 0; i < this.infoButtons.length; i++) {
            this.deleteButtons[i].visible = false;
        }
    }

    @Override
    protected void onInfoButtonPressed(int index) {
        BuildersSchematic.Info info = index + this.infoOffset >= this.infoList.size() ? null : this.infoList.get(index + this.infoOffset);

        if(info != null) {
            InitPacket.PIPELINE.sendToServer(new C2SCreatorAction(C2SCreatorAction.Action.PRINT, info.getUuid(), info.getName()));
        }
    }

    @Override
    protected void onDeleteButtonPressed(int index) {

    }

}
