package redstonejukebox.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.EnumOptions;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import redstonejukebox.ModRedstoneJukebox;
import redstonejukebox.common.ContainerRedstoneJukebox;
import redstonejukebox.network.PacketHelper;
import redstonejukebox.tileentites.TileEntityRedstoneJukebox;



public class GuiRedstoneJukebox extends GuiContainer {

    public TileEntityRedstoneJukebox jukeboxInventory;

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

        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(0, this.guiLeft + 174, this.guiTop + 147));
        this.buttonList.add(new GuiRedstoneJukeboxButtonVolumeUp(1, this.guiLeft - 34, this.guiTop + 130));
        this.buttonList.add(new GuiRedstoneJukeboxButtonVolumeDown(2, this.guiLeft - 24, this.guiTop + 130));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPlayMode(3, this.guiLeft + 154, this.guiTop + 126));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPowerOn(4, this.guiLeft - 29, this.guiTop + 146));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPrevious(5, this.guiLeft - 29, this.guiTop + 167));
        this.buttonList.add(new GuiRedstoneJukeboxButtonNext(6, this.guiLeft + 180, this.guiTop + 167));
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
                if (!this.jukeboxInventory.isLoop) {
                    this.jukeboxInventory.isLoop = true;
                }
                else {
                    this.jukeboxInventory.isLoop = false;
                }
                break;

            case 1:
                // Volume up command
            	if (value <= 1.0F) {
            		value += 0.05F;
           	    	}
            	this.jukeboxInventory.isVolume = value;
            	Minecraft.getMinecraft().gameSettings.setOptionFloatValue(EnumOptions.MUSIC, value);
            	break;

            case 2:
                // Volume down command
            	if (value > 0) {
            		value -= 0.05F;
           	    	}
            	this.jukeboxInventory.isVolume = value;
            	Minecraft.getMinecraft().gameSettings.setOptionFloatValue(EnumOptions.MUSIC, value);
            	break;
            	
            case 3:
                // Swap play mode (shuffle / normal)
                if (this.jukeboxInventory.playMode == 0) {
                    this.jukeboxInventory.playMode = 1;
                }
                else {
                    this.jukeboxInventory.playMode = 0;
                }
                break;


            case 4:
                // Swap power mode (off / on)
                if (!this.jukeboxInventory.isActive) {
                    this.jukeboxInventory.isActive = true;

                }
                else {
                    this.jukeboxInventory.isActive = false;
                }
                break;

            case 5:
                // Play previous record
            	int prevSlot = this.jukeboxInventory.getCurrentJukeboxPlaySlot();
                if (prevSlot > -1) {
                	prevSlot--;
                	this.jukeboxInventory.playRecord(prevSlot);
                }
            	break;
            	
            case 6:
                // Play next record
            	int nextSlot = this.jukeboxInventory.getCurrentJukeboxPlaySlot();
                if (nextSlot < this.jukeboxInventory.maxAmount) {
                	this.jukeboxInventory.playNextRecord();
                }
            	break;
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
        this.fontRenderer.drawString(StatCollector.translateToLocal(""), 8, this.ySize - 84, 4210752);

        // Tooltips
        GuiButton btPlayLoop = (GuiButton) this.buttonList.get(0);
        GuiButton btVolumeUp = (GuiButton) this.buttonList.get(1);
        GuiButton btVolumeDown = (GuiButton) this.buttonList.get(2);
        GuiButton btPlaymode = (GuiButton) this.buttonList.get(3);
        GuiButton btPower = (GuiButton) this.buttonList.get(4);
        GuiButton btPrevious = (GuiButton) this.buttonList.get(5);
        GuiButton btNext = (GuiButton) this.buttonList.get(6);

        if (btPlayLoop.func_82252_a()) {
        	if (!jukeboxInventory.isLoop)
        	{
            this.drawCreativeTabHoveringText("Play order: only once", x - this.guiLeft, y - this.guiTop + 31);
        	}
        	else {
        		this.drawCreativeTabHoveringText("Play order: in a loop", x - this.guiLeft, y - this.guiTop + 31);
        	}
        }
        else if (btVolumeUp.func_82252_a()) {
            this.drawCreativeTabHoveringText("Adjust volume up", x - this.guiLeft, y - this.guiTop + 31);
        }
        else if (btVolumeDown.func_82252_a()) {
            this.drawCreativeTabHoveringText("Adjust volume down", x - this.guiLeft, y - this.guiTop + 31);
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
        else if (btPower.func_82252_a()) {
        	if (!jukeboxInventory.isActive)
        	{
        		this.drawCreativeTabHoveringText("Power: Off", x - this.guiLeft, y - this.guiTop + 31);
        	}
        	else {
        		this.drawCreativeTabHoveringText("Power: On", x - this.guiLeft, y - this.guiTop + 31);
        	}
        }
        else if (btPrevious.func_82252_a()) {
                this.drawCreativeTabHoveringText("Previous record", x - this.guiLeft, y - this.guiTop + 31);
        }
        else if (btNext.func_82252_a()) {
                this.drawCreativeTabHoveringText("Next record", x - this.guiLeft, y - this.guiTop + 31);
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
         * height: 206
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
        this.drawTexturedModalRect(j - 48, k - 48, 0, 0, this.xSize + 80, this.ySize + 72);


        // -- loop indicator
        if (this.jukeboxInventory.isLoop) {
            // play loop
            this.drawTexturedModalRect(j + 174, k + 147, 19, 239, 18, 9);
        }
        else {
            // play once
            this.drawTexturedModalRect(j + 174, k + 147, 0, 239, 18, 9);
        }


        // -- power indicator
        if (!jukeboxInventory.isActive) {
            // power off
            this.drawTexturedModalRect(j - 28, k + 145, 146, 239, 10, 10);
        }
        else {
            // power on
            this.drawTexturedModalRect(j - 28, k + 145, 157, 239, 10, 10);
        }


        // -- play mode indicator
        int spacer = 18;
        int pStartX = 80;
        int pStartY = 57;
        GuiContainer.itemRenderer.zLevel = 100.0F;


        switch (this.jukeboxInventory.playMode) {
        case 0:
            // normal
            this.drawTexturedModalRect(j + 162, k + 127, 39, 239, 40, 10);
            break;

        case 1:
            // shuffle
            this.drawTexturedModalRect(j + 162, k + 127, 82, 239, 40, 10);
            break;

     }


        
        GuiContainer.itemRenderer.zLevel = 0.0F;

    }




}
