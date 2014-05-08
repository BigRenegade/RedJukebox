package sidben.redstonejukebox.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.common.ContainerRedstoneJukebox;
import sidben.redstonejukebox.common.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.net.PacketHelper;



public class GuiRedstoneJukebox extends GuiContainer {

    private TileEntityRedstoneJukebox jukeboxInventory;

    // Auxiliary info for the dancing blue note that indicates the current record playing
    private static int				danceNoteSpeed	= 2;
    private static int[]			danceNoteArrayX	= { 0, 1, 2, 1, 0, -1, -2, -1 };
    private static int[]			danceNoteArrayY	= { 0, 0, 1, 0, 0, 0, 1, 0 };
    private int						danceNoteFrame	= 0;
    private int						danceNoteCount	= 0;
	public float 					value			= 0.5F;




    public GuiRedstoneJukebox(InventoryPlayer inventory, TileEntityRedstoneJukebox teJukebox) {
        super(new ContainerRedstoneJukebox(inventory, teJukebox));
        this.jukeboxInventory = teJukebox;
    }




    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(0, this.guiLeft + 5, this.guiTop + 51));
        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(1, this.guiLeft + 28, this.guiTop + 51));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPlayMode(2, this.guiLeft + 79, this.guiTop + 51));
        this.buttonList.add(new GuiRedstoneJukeboxButtonVolumeUp(3, this.guiLeft + 51, this.guiTop + 51));
        this.buttonList.add(new GuiRedstoneJukeboxButtonVolumeDown(4, this.guiLeft + 51, this.guiTop + 64));
    }




    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        super.updateScreen();

        ++this.danceNoteCount;
        if (this.danceNoteCount > GuiRedstoneJukebox.danceNoteSpeed) {
            ++this.danceNoteFrame;
            this.danceNoteCount = 0;
        }
        if (this.danceNoteFrame >= GuiRedstoneJukebox.danceNoteArrayX.length) {
            this.danceNoteFrame = 0;
        }
    }


    /*
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {

            switch (par1GuiButton.id) {
            case 0:
                // Loop command: no loop
                this.jukeboxInventory.isLoop = false;
                break;


            case 1:
                // Loop command: with loop
                this.jukeboxInventory.isLoop = true;
                break;


            case 2:
                // Swap play mode (shuffle / normal)
                if (this.jukeboxInventory.playMode == 0) {
                    this.jukeboxInventory.playMode = 1;
                }
                else {
                    this.jukeboxInventory.playMode = 0;
                }
                break;

            case 3:
                // Volume up command
            	if (value <= 1.0F) {
            		value += 0.05F;
           	    	}
            	this.jukeboxInventory.isVolume = value;
            	Minecraft.getMinecraft().gameSettings.setOptionFloatValue(EnumOptions.MUSIC, value);
            	break;

            case 4:
                // Volume down command
            	if (value > 0) {
            		value -= 0.05F;
           	    	}
            	this.jukeboxInventory.isVolume = value;
            	Minecraft.getMinecraft().gameSettings.setOptionFloatValue(EnumOptions.MUSIC, value);
            }


            // Packet code here
            // This is need to inform the server that changes where made.
            PacketHelper.sendJukeboxGUIPacket(this.jukeboxInventory.isLoop, this.jukeboxInventory.playMode);

        }


    }




    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 84, 4210752);

        // Tooltips
        GuiButton btPlayOnce = (GuiButton) this.buttonList.get(0);
        GuiButton btPlayLoop = (GuiButton) this.buttonList.get(1);
        GuiButton btPlaymode = (GuiButton) this.buttonList.get(2);
        GuiButton btVolumeUp = (GuiButton) this.buttonList.get(3);
        GuiButton btVolumeDown = (GuiButton) this.buttonList.get(4);

        if (btPlayOnce.func_82252_a()) {
            this.drawCreativeTabHoveringText("Play records only once", x - this.guiLeft, y - this.guiTop + 31);
        }
        else if (btPlayLoop.func_82252_a()) {
            this.drawCreativeTabHoveringText("Play records in loop", x - this.guiLeft, y - this.guiTop + 31);
        }
        else if (btVolumeUp.func_82252_a()) {
            this.drawCreativeTabHoveringText("Adjust volume of music up", x - this.guiLeft, y - this.guiTop + 31);
        }
        else if (btVolumeDown.func_82252_a()) {
            this.drawCreativeTabHoveringText("Adjust volume of music down", x - this.guiLeft, y - this.guiTop + 31);
        }
        else if (btPlaymode.func_82252_a()) {
            switch (this.jukeboxInventory.playMode) {
            case 0:
                this.drawCreativeTabHoveringText("Play mode: In order", x - this.guiLeft, y - this.guiTop + 31);
                break;
            case 1:
                this.drawCreativeTabHoveringText("Play mode: Shuffle", x - this.guiLeft, y - this.guiTop + 31);
                break;
            }
        }

    }



    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

        /*
         * -----------------------------------
         * default GUI size:
         * -----------------------------------
         * width: 176
         * height: 166
         * 
         * 
         * method Signature
         * -----------------------------------
         * drawTexturedModalRect(drawingStartX, drawingStartY, textureStartX, textureStartY, width, height)
         * Args: x, y, u, v, width, height
         */


        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ModRedstoneJukebox.redstoneJukeboxGui);
        int j = (this.width - this.xSize) / 2;
        int k = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(j, k - 28, 0, 0, this.xSize, this.ySize + 40);




        // -- current record indicator (blue note)
        if (this.jukeboxInventory.isActive()) {

            switch (this.jukeboxInventory.getCurrentJukeboxPlaySlot()) {
            case 0:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 27, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 1:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 46, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 2:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 64, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 3:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 82, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 4:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 100, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 5:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 118, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 6:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 136, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 7:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 154, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 8:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 27, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 9:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 46, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 10:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 64, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 11:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 82, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 12:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 100, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 13:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 118, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 14:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 136, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 15:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 154, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 16:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 27, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 17:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 46, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 18:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 64, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 19:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 82, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 20:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 100, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 21:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 118, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 22:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 136, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            case 23:
                this.drawTexturedModalRect(j + GuiRedstoneJukebox.danceNoteArrayX[this.danceNoteFrame] + 154, k + GuiRedstoneJukebox.danceNoteArrayY[this.danceNoteFrame] + 38, 176, 1, 12, 10);
                break;
            }

        }


        // -- loop indicator
        if (this.jukeboxInventory.isLoop) {
            // play loop
            this.drawTexturedModalRect(j + 31, k + 54, 176, 21, 18, 21);
        }
        else {
            // play once
            this.drawTexturedModalRect(j + 9, k + 61, 176, 12, 16, 9);
        }




        // -- play mode indicator
        int spacer = 18;
        int pStartX = 80;
        int pStartY = 57;
        GuiContainer.itemRenderer.zLevel = 100.0F;


        switch (this.jukeboxInventory.playMode) {
        case 0:
            // normal
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeWood), j + pStartX + spacer * 0, k + pStartY);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeStone), j + pStartX + spacer * 1, k + pStartY);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeIron), j + pStartX + spacer * 2, k + pStartY);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeGold), j + pStartX + spacer * 3, k + pStartY);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeDiamond), j + pStartX + spacer * 4, k + pStartY);
            break;

        case 1:
            // shuffle
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.shovelIron), j + pStartX + spacer * 0, k + pStartY - 1);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeDiamond), j + pStartX + spacer * 1, k + pStartY + 4);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.axeGold), j + pStartX + spacer * 2, k + pStartY - 3);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeWood), j + pStartX + spacer * 3, k + pStartY + 1);
            GuiContainer.itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.pickaxeStone), j + pStartX + spacer * 4, k + pStartY - 2);
            break;

     }


        
        GuiContainer.itemRenderer.zLevel = 0.0F;

    }




}
