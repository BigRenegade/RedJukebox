package redstonejukebox.gui;


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
import redstonejukebox.ModRedstoneJukebox;
import redstonejukebox.common.ContainerRedstoneJukebox;
import redstonejukebox.network.PacketHelper;
import redstonejukebox.tileentites.TileEntityRedstoneJukebox;



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

        this.buttonList.add(new GuiRedstoneJukeboxButtonLoop(0, this.guiLeft + 63, this.guiTop + 79));
        this.buttonList.add(new GuiRedstoneJukeboxButtonVolumeUp(1, this.guiLeft + 89, this.guiTop + 79));
        this.buttonList.add(new GuiRedstoneJukeboxButtonVolumeDown(2, this.guiLeft + 99, this.guiTop + 79));
        this.buttonList.add(new GuiRedstoneJukeboxButtonPlayMode(3, this.guiLeft + 116, this.guiTop + 79));
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
        GuiButton btPlayLoop = (GuiButton) this.buttonList.get(0);
        GuiButton btVolumeUp = (GuiButton) this.buttonList.get(1);
        GuiButton btVolumeDown = (GuiButton) this.buttonList.get(2);
        GuiButton btPlaymode = (GuiButton) this.buttonList.get(3);

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
        this.drawTexturedModalRect(j, k - 28, 0, 0, this.xSize, this.ySize + 40);


        // -- loop indicator
        if (this.jukeboxInventory.isLoop) {
            // play loop
            this.drawTexturedModalRect(j + 63, k + 81, 0, 216, 18, 9);
        }
        else {
            // play once
            this.drawTexturedModalRect(j + 63, k + 81, 0, 206, 18, 9);
        }




        // -- play mode indicator
        int spacer = 18;
        int pStartX = 80;
        int pStartY = 57;
        GuiContainer.itemRenderer.zLevel = 100.0F;


        switch (this.jukeboxInventory.playMode) {
        case 0:
            // normal
            this.drawTexturedModalRect(j + 116, k + 81, 39, 217, 52, 10);
            break;

        case 1:
            // shuffle
            this.drawTexturedModalRect(j + 116, k + 81, 92, 217, 52, 10);
            break;

     }


        
        GuiContainer.itemRenderer.zLevel = 0.0F;

    }




}
