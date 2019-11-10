package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.container.ContainerCreator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiContainerCreator extends GuiContainer {

    protected int centerX;
    protected int centerY;

    public GuiContainerCreator(World world, EntityPlayer player, TileEntityCreator creator) {
        super(new ContainerCreator(world, player, creator));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
    }

}
