package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.C2STerminalAction;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiContainerSchemaInfo extends GuiContainer {

    protected int centerX;
    protected int centerY;

    protected GuiButton[] infoButtons = new GuiButton[8];
    protected GuiButton[] deleteButtons = new GuiButton[infoButtons.length];
    protected List<BuildersSchematic.Info> infoList = new ArrayList<>();
    protected int infoOffset;

    public GuiContainerSchemaInfo(Container container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.guiLeft = this.centerX - 200;

        for(int i = 0; i < this.infoButtons.length; i++) {
            this.addButton(this.infoButtons[i] =  new GuiButton(1, this.centerX, this.centerY + (i * 20) - 100, ""));
            this.addButton(this.deleteButtons[i] =  new GuiButton(1, this.centerX + 200, this.centerY + (i * 20) - 100, 20, 20, "[X]"));
        }

        this.updateInfoButtons();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.updateInfoButtons();
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
        for(int i = 0; i < this.infoButtons.length; i++) {
            if(button == this.infoButtons[i]) {
                this.onInfoButtonPressed(i);
            } else if(button == this.deleteButtons[i]) {
                this.onDeleteButtonPressed(i);
            }
        }

        super.actionPerformed(button);
    }

    protected abstract void onInfoButtonPressed(int index);
    protected abstract void onDeleteButtonPressed(int index);

    public void setInfoList(List<BuildersSchematic.Info> infoList) {
        this.infoList = infoList;
    }

    public List<BuildersSchematic.Info> getInfoList() {
        return this.infoList;
    }

}
