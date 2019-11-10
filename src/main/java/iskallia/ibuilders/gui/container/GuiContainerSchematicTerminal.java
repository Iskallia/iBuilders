package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntitySchematicTerminal;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.C2STerminalAction;
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

public class GuiContainerSchematicTerminal extends GuiContainer implements ISchemaInfo {

    protected int centerX;
    protected int centerY;

    protected GuiButton uploadButton;
    private GuiButton[] infoButtons = new GuiButton[8];
    private GuiButton[] deleteButtons = new GuiButton[infoButtons.length];
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
        this.guiLeft = this.centerX - 200;

        this.addButton(this.uploadButton = new GuiButton(0, this.centerX - 180, this.centerY - 85, 60, 20, "UPLOAD"));

        for(int i = 0; i < this.infoButtons.length; i++) {
            this.addButton(this.infoButtons[i] =  new GuiButton(1, this.centerX, this.centerY + (i * 20) - 100, ""));
            this.addButton(this.deleteButtons[i] =  new GuiButton(1, this.centerX + 200, this.centerY + (i * 20) - 100, 20, 20, "[X]"));
        }

        this.updateUploadButton();
        this.updateInfoButtons();

        InitPacket.PIPELINE.sendToServer(new C2STerminalAction(C2STerminalAction.Action.GET_INFO));
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
                this.deleteButtons[i].visible = false;
            } else {
                this.infoButtons[i].visible = true;
                this.deleteButtons[i].visible = true;
                this.infoButtons[i].displayString = info.toString();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button == this.uploadButton) {
            InitPacket.PIPELINE.sendToServer(new C2STerminalAction(C2STerminalAction.Action.UPLOAD));
        }

        for(int i = 0; i < this.infoButtons.length; i++) {
            if(button == this.infoButtons[i]) {

            } else if(button == this.deleteButtons[i]) {
                BuildersSchematic.Info info = i + this.infoOffset >= this.infoList.size() ? null : this.infoList.get(i + this.infoOffset);

                if(info != null) {
                    InitPacket.PIPELINE.sendToServer(new C2STerminalAction(C2STerminalAction.Action.DELETE, info.getName()));
                }
            }
        }

        super.actionPerformed(button);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
    }

    @Override
    public void setInfoList(List<BuildersSchematic.Info> infoList) {
        this.infoList = infoList;
    }

    @Override
    public List<BuildersSchematic.Info> getInfoList() {
        return this.infoList;
    }

}
