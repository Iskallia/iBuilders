package iskallia.ibuilders.gui.container;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.container.ContainerCreatorWorkers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiContainerCreatorWorkers extends GuiContainer {

    public static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public GuiContainerCreatorWorkers(EntityPlayer player,  TileEntityCreator creator) {
        super(new ContainerCreatorWorkers(player, creator));
        this.xSize = 176;
        this.ySize = 222;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(INVENTORY_TEXTURE);
        this.zLevel = 100;
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

}
