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
        for (int row = 0; row < 8; row++) {
          for (int col = 0; col < 13; col++) {
            addSlotToContainer(new SlotRedstoneJukeboxRecord(this.teJukebox, col + row * 13, -36 + col * 18, -40 + row * 18));
          }
        }

        
        bindPlayerInventory(inventoryPlayer);
    }
    

    
    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                        addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, j * 18, 108 + i * 18));
                }
        }

        for (int i = 0; i < 9; i++) {
                addSlotToContainer(new Slot(inventoryPlayer, i, i * 18, 166));
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
         * 0-44 = jukebox
         * 45-71 = player inventory
         * 71-80 = player hotbar
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


            if (slotnumber < 139) {
                // send item from the jukebox to the player
                if (!this.mergeItemStack(myStack, 0, 104, true)) return null;
            }
            else {
                // send a record to the jukebox
                if (SlotRedstoneJukeboxRecord.isRecord(myStack)) {
                    if (!this.mergeItemStack(myStack, 0, 35, false)) return null;
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
