package sidben.redstonejukebox;


/*
 * Inspired by EE3 from Pahimar
 */
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class JukeboxTab extends CreativeTabs {
public static final JukeboxTab instance = new JukeboxTab();

public JukeboxTab(){
	super("Redstone Jukebox");
	}

	@Override
	public String getTranslatedTabLabel(){
		return "Redstone Jukebox";
		}
	
	public ItemStack getIconItemStack() {
		return new ItemStack(ModRedstoneJukebox.redstoneJukebox);
	}

}

