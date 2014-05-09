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
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 0, 26, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 1, 44, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 2, 62, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 3, 80, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 4, 98, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 5, 116, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 6, 134, -20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 7, 152, -20));

        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 8, 26, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 9, 44, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 10, 62, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 11, 80, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 12, 98, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 13, 116, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 14, 134, 0));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 15, 152, 0));

        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 16, 26, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 17, 44, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 18, 62, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 19, 80, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 20, 98, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 21, 116, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 22, 134, 20));
        this.addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, 23, 152, 20));

        // -- Player inventory
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 96 + i * 18));
                //this.addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 96 + i * 18));
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


            if (slotnumber < 24) {
                // send item from the jukebox to the player
                if (!this.mergeItemStack(myStack, 0, 23, true)) return null;
            }
            else {
                // send a record to the jukebox
                if (SlotRedstoneJukeboxRecord.isRecord(myStack)) {
                    if (!this.mergeItemStack(myStack, 24, 60, false)) return null;
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
