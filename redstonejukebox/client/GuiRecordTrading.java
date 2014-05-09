package sidben.redstonejukebox.client;


import java.util.Random;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sidben.redstonejukebox.ModRedstoneJukebox;
import sidben.redstonejukebox.common.ContainerRecordTrading;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.net.PacketHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



//-- Based on GuiMerchant
@SideOnly(Side.CLIENT)
public class GuiRecordTrading extends GuiContainer {

    protected Random           rand               = new Random();


    /** Instance of IMerchant interface. */
    private IMerchant          theIMerchant;
    private GuiButtonMerchant  nextRecipeButtonIndex;
    private GuiButtonMerchant  previousRecipeButtonIndex;
    private int                currentRecipeIndex = 0;
    private int                storeId            = 0;           // Every merchant shares access to one of the available stores.

    private MerchantRecipeList recordList;                       // Record trading uses a special trades list, shared by some merchants




    public GuiRecordTrading(InventoryPlayer player, IMerchant merchant, World world) {
        super(new ContainerRecordTrading(player, merchant, world));
        this.theIMerchant = merchant;
        this.storeId = CustomRecordHelper.getStoreID(((Entity) merchant).entityId);
        this.recordList = CustomRecordHelper.getStoreCatalog(this.storeId);
    }


    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        int var1 = (this.width - this.xSize) / 2;
        int var2 = (this.height - this.ySize) / 2;
        this.buttonList.add(this.nextRecipeButtonIndex = new GuiButtonMerchant(1, var1 + 120 + 27, var2 + 24 - 1, true));
        this.buttonList.add(this.previousRecipeButtonIndex = new GuiButtonMerchant(2, var1 + 36 - 19, var2 + 24 - 1, false));
        this.nextRecipeButtonIndex.enabled = false;
        this.previousRecipeButtonIndex.enabled = false;
    }




    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        // Shows the name "Villager" and "Inventory"
        this.fontRenderer.drawString(StatCollector.translateToLocal("entity.Villager.name"), 56, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }


    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        super.updateScreen();
        MerchantRecipeList var1 = this.recordList;

        if (var1 != null) {
            this.nextRecipeButtonIndex.enabled = this.currentRecipeIndex < var1.size() - 1;
            this.previousRecipeButtonIndex.enabled = this.currentRecipeIndex > 0;
        }
    }



    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        boolean updateServer = false;

        // Action = Move to the previous offer
        if (par1GuiButton == this.nextRecipeButtonIndex) {
            ++this.currentRecipeIndex;
            updateServer = true;
        }

        // Action = Move to the next offer
        else if (par1GuiButton == this.previousRecipeButtonIndex) {
            --this.currentRecipeIndex;
            updateServer = true;
        }



        if (updateServer) {
            ((ContainerRecordTrading) this.inventorySlots).setCurrentRecipeIndex(this.currentRecipeIndex);

            // Packets
            PacketHelper.sendRecordTradeGUIPacket(this.currentRecipeIndex);
        }
    }


    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ModRedstoneJukebox.recordTradeGui);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);


        MerchantRecipeList var7 = this.recordList;


        // Draws a X on the locked trades
        if (var7 != null && !var7.isEmpty()) {
            int var8 = this.currentRecipeIndex;
            MerchantRecipe var9 = (MerchantRecipe) var7.get(var8);

            if (var9.func_82784_g()) {
                this.mc.getTextureManager().bindTexture(ModRedstoneJukebox.recordTradeGui);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }

    }


    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3) {

        super.drawScreen(par1, par2, par3);
        MerchantRecipeList var4 = this.recordList;

        if (var4 != null && !var4.isEmpty()) {
            int var5 = (this.width - this.xSize) / 2;
            int var6 = (this.height - this.ySize) / 2;
            int var7 = this.currentRecipeIndex;
            MerchantRecipe var8 = (MerchantRecipe) var4.get(var7);
            GL11.glPushMatrix();
            ItemStack var9 = var8.getItemToBuy();
            ItemStack var10 = var8.getSecondItemToBuy();
            ItemStack var11 = var8.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            GuiContainer.itemRenderer.zLevel = 100.0F;
            GuiContainer.itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var9, var5 + 36, var6 + 24);
            GuiContainer.itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var9, var5 + 36, var6 + 24);

            if (var10 != null) {
                GuiContainer.itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
                GuiContainer.itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var10, var5 + 62, var6 + 24);
            }

            GuiContainer.itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
            GuiContainer.itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var11, var5 + 120, var6 + 24);
            GuiContainer.itemRenderer.zLevel = 0.0F;
            GL11.glDisable(GL11.GL_LIGHTING);

            if (this.isPointInRegion(36, 24, 16, 16, par1, par2)) {
                this.drawItemStackTooltip(var9, par1, par2);
            }
            else if (var10 != null && this.isPointInRegion(62, 24, 16, 16, par1, par2)) {
                this.drawItemStackTooltip(var10, par1, par2);
            }
            else if (this.isPointInRegion(120, 24, 16, 16, par1, par2)) {
                this.drawItemStackTooltip(var11, par1, par2);
            }

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }



    /**
     * Gets the Instance of IMerchant interface.
     */
    public IMerchant getIMerchant() {
        return this.theIMerchant;
    }



    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed() {
        boolean madeTrade = false;

        // Check if a trade was made
        ContainerRecordTrading auxContainer = (ContainerRecordTrading) this.inventorySlots;
        if (auxContainer != null) {
            madeTrade = auxContainer.madeAnyTrade();
        }


        super.onGuiClosed();


        // Spawns particles if a trade was made.
        // TODO: better particle effects
        if (this.mc.thePlayer != null) {
            Entity auxVillager = (Entity) this.theIMerchant;
            if (auxVillager != null && madeTrade) {
                CustomRecordHelper.spawnTradeParticles(this.mc.thePlayer.worldObj, auxVillager, this.rand);
            }
        }
    }

}
