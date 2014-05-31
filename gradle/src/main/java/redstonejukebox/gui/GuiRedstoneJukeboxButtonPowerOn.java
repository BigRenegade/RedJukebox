package redstonejukebox.gui;

import org.lwjgl.opengl.GL11;

import redstonejukebox.ModRedstoneJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiRedstoneJukeboxButtonPowerOn extends GuiButton {
	
    protected static int myWidth  = 10;
    protected static int myHeight = 10;



    public GuiRedstoneJukeboxButtonPowerOn(int index, int x, int y) {
        super(index, x, y, GuiRedstoneJukeboxButtonPowerOn.myWidth, GuiRedstoneJukeboxButtonPowerOn.myHeight, "");
    }



    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft par1Minecraft, int mouseX, int mouseY) {
        if (this.drawButton) {
            boolean isMouseOver = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.field_82253_i = isMouseOver;

            if (isMouseOver) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                par1Minecraft.getTextureManager().bindTexture(ModRedstoneJukebox.redstoneJukeboxGui);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 200, 245, GuiRedstoneJukeboxButtonPowerOn.myWidth, GuiRedstoneJukeboxButtonPowerOn.myHeight);
            }
        }
    }



    // OBS: Mouseover
    @Override
    public boolean func_82252_a() {
        return this.field_82253_i;
    }



}
