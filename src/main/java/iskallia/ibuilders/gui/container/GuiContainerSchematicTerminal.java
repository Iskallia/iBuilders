package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.C2SUploadSchematic;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiContainerSchematicTerminal extends GuiContainer {

    protected int centerX;
    protected int centerY;

    protected GuiButton uploadButton;
    private GuiButton[] infoButtons = new GuiButton[4];
    private List<BuildersSchematic.Info> infoList = new ArrayList<>();
    private int infoOffset;

    public GuiContainerSchematicTerminal(World world, EntityPlayer player, TileEntitySchematicTerminal schematicTerminal) {
        super(new ContainerSchematicTerminal(world, player, schematicTerminal));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.addButton(this.uploadButton = new GuiButton(0, this.centerX - 65, this.centerY - 85, 60, 20, "UPLOAD"));
        this.addButton(this.infoButtons[0] =  new GuiButton(1, this.centerX + 100, this.centerY - 40, ""));
        this.addButton(this.infoButtons[1] =  new GuiButton(2, this.centerX + 100, this.centerY - 20, ""));
        this.addButton(this.infoButtons[2] =  new GuiButton(3, this.centerX + 100, this.centerY, ""));
        this.addButton(this.infoButtons[3] =  new GuiButton(4, this.centerX + 100, this.centerY + 20, ""));
        this.updateUploadButton();
        this.updateInfoButtons();
        //TODO: fix this.
        InitPacket.PIPELINE.sendToServer(new C2SUploadSchematic());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.updateUploadButton();
        this.updateInfoButtons();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void updateUploadButton() {
        ContainerSchematicTerminal schematicTerminal = (ContainerSchematicTerminal)this.inventorySlots;
        ItemStack blueprint = schematicTerminal.inventorySlots.get(0).getStack();
        this.uploadButton.enabled = !blueprint.isEmpty();
    }

    protected void updateInfoButtons() {
        int scroll = Mouse.getDWheel();

        while(scroll >= 120) {
            scroll -= 120;
            this.infoOffset--;
        }

        while(scroll <= -120) {
            scroll += 120;
            this.infoOffset++;
        }

        if(this.infoOffset > this.infoList.size() - this.infoButtons.length) {
            this.infoOffset = this.infoList.size() - this.infoButtons.length;
        }

        if(this.infoOffset < 0) {
            this.infoOffset = 0;
        }

        for(int i = 0; i < this.infoButtons.length; i++) {
            BuildersSchematic.Info info = i + this.infoOffset >= this.infoList.size() ?
                            null : this.infoList.get(i + this.infoOffset);

            if(info == null) {
                this.infoButtons[i].visible = false;
            } else {
                this.infoButtons[i].visible = true;
                this.infoButtons[i].displayString = info.toString();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button == this.uploadButton) {
            InitPacket.PIPELINE.sendToServer(new C2SUploadSchematic());
        }

        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
    }

    public void setInfo(List<BuildersSchematic.Info> infoList) {
        this.infoList = infoList;
    }

}
