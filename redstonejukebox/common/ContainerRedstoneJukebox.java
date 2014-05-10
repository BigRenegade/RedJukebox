package redstonejukebox.common;


import redstonejukebox.tileentites.TileEntityRedstoneJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;




public class ContainerRedstoneJukebox extends Container {

    private TileEntityRedstoneJukebox teJukebox;



    public ContainerRedstoneJukebox(InventoryPlayer inventoryPlayer, TileEntityRedstoneJukebox tileEntity) {
        this.teJukebox = tileEntity;



        // --- Slots of the Jukebox

        for (int i = 0; i < 5; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, k + i * 9 + 9, 8 + k * 18, -20 + i * 20));
            }
        }
        
        

        // -- Player inventory
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 96 + i * 18));
            }
        }

        // -- Player hotbar
        for (int j = 0; j < 9; j++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, j, 8 + j * 18, 154));
        }

    }



    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return this.teJukebox.isUseableByPlayer(par1EntityPlayer);
    }


    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotnumber) {
        /*
         * slotnumber:
         * 0-23 = jukebox
         * 24-50 = player inventory
         * 51-60 = player hotbar
         * 
         * 
         * mergeItemStack(i, a, b, r)
         * i = itemStack
         * a = first position of the check
         * b = last position of the check
         * r = order (true = reverse, last to first)
         * 
         * return TRUE if successful
         */




        ItemStack returnStack = null;
        Slot slot = (Slot) this.inventorySlots.get(slotnumber);

        if (slot != null && slot.getHasStack()) {
            ItemStack myStack = slot.getStack();
            returnStack = myStack.copy();


            if (slotnumber < 45) {
                // send item from the jukebox to the player
                if (!this.mergeItemStack(myStack, 0, 44, true)) return null;
            }
            else {
                // send a record to the jukebox
                if (SlotRedstoneJukeboxRecord.isRecord(myStack)) {
                    if (!this.mergeItemStack(myStack, 0, 9, false)) return null;
                }
                else
                    return null;
            }


            if (myStack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            }
            else {
                slot.onSlotChanged();
            }

        }


        return returnStack;

    }




    public TileEntityRedstoneJukebox GetTileEntity() {
        return this.teJukebox;
    }


}
