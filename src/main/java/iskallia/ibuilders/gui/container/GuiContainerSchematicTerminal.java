package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.C2STerminalAction;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.io.IOException;

public class GuiContainerSchematicTerminal extends GuiContainerSchemaInfo {

    private GuiButton uploadButton;

    public GuiContainerSchematicTerminal(World world, EntityPlayer player, TileEntitySchematicTerminal schematicTerminal) {
        super(new ContainerSchematicTerminal(world, player, schematicTerminal));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(this.uploadButton = new GuiButton(0, this.centerX - 180, this.centerY - 85, 60, 20, "UPLOAD"));
        this.updateUploadButton();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.updateUploadButton();
    }

    protected void updateUploadButton() {
        ContainerSchematicTerminal schematicTerminal = (ContainerSchematicTerminal)this.inventorySlots;
        ItemStack blueprint = schematicTerminal.inventorySlots.get(0).getStack();
        this.uploadButton.enabled = !blueprint.isEmpty();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button == this.uploadButton) {
            InitPacket.PIPELINE.sendToServer(new C2STerminalAction(C2STerminalAction.Action.UPLOAD));
        }

        super.actionPerformed(button);
    }

}
