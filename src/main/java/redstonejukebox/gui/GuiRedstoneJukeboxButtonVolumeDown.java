package redstonejukebox.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import redstonejukebox.ModRedstoneJukebox;



public class GuiRedstoneJukeboxButtonVolumeDown extends GuiButton {


    protected static int myWidth  = 9;
    protected static int myHeight = 9;



    public GuiRedstoneJukeboxButtonVolumeDown(int index, int x, int y) {
        super(index, x, y, GuiRedstoneJukeboxButtonVolumeDown.myWidth, GuiRedstoneJukeboxButtonVolumeDown.myHeight, "");
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
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 136, 239, GuiRedstoneJukeboxButtonVolumeDown.myWidth, GuiRedstoneJukeboxButtonVolumeDown.myHeight);
            }
        }
    }



    // OBS: Mouseover
    @Override
    public boolean func_82252_a() {
        return this.field_82253_i;
    }

}
