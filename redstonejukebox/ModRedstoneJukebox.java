package sidben.redstonejukebox;


import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import sidben.redstonejukebox.client.PlayerEventHandler;
import sidben.redstonejukebox.client.SoundEventHandler;
import sidben.redstonejukebox.common.BlockRedstoneJukebox;
import sidben.redstonejukebox.common.CommandPlayBgMusic;
import sidben.redstonejukebox.common.CommandPlayRecord;
import sidben.redstonejukebox.common.CommandPlayRecordAt;
import sidben.redstonejukebox.common.CommonProxy;
import sidben.redstonejukebox.common.ItemBlankRecord;
import sidben.redstonejukebox.common.ItemCustomRecord;
import sidben.redstonejukebox.common.TileEntityRedstoneJukebox;
import sidben.redstonejukebox.helper.CustomRecordHelper;
import sidben.redstonejukebox.helper.CustomRecordObject;
import sidben.redstonejukebox.helper.MusicTickHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;



@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { Reference.Channel }, packetHandler = sidben.redstonejukebox.net.PacketHandler.class)
public class ModRedstoneJukebox {



    // The instance of your mod that Forge uses.
    @Instance(Reference.ModID)
    public static ModRedstoneJukebox instance;


    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = Reference.ClientProxyClass, serverSide = Reference.ServerProxyClass)
    public static CommonProxy        proxy;


    // Models IDs
    public static int                redstoneJukeboxModelID;


    // Textures and Icons paths
    public static String             blankRecordIcon;
    public static String             customRecordIconArray;
    public static String             jukeboxDiscIcon;
    public static String             jukeboxTopIcon;
    public static String             jukeboxBottomIcon;
    public static String             jukeboxSideOnIcon;
    public static String             jukeboxSideOffIcon;
    private static String            guiTextureJukebox;
    private static String            guiTextureTrade;
    public static ResourceLocation   redstoneJukeboxGui;
    public static ResourceLocation   recordTradeGui;


    // GUI IDs
    public static int                redstoneJukeboxGuiID = 0;
    public static int                recordTradingGuiID   = 1;


    // Blocks and Items IDs
    public static int                redstoneJukeboxIdleID;
    public static int                redstoneJukeboxActiveID;
    public static int                blankRecordItemID;
    public static int                customRecordItemID;


    // Blocks and Items
    public static Item               recordBlank;
    public static Item               customRecord;
    public static Block              redstoneJukebox;
    public static Block              redstoneJukeboxActive;


    // Enchantments
    public static Enchantment        enchantDummy;


    // Global variables
    public final static String       sourceName           = "streaming";    // music discs are called "streaming"
    public final static int          maxCustomRecordIcon  = 77;         // Limit of icon IDs for the records. This is stored on the metadata (damage value) of the item. Start at zero.
    public final static int          maxStores            = 16;         // Number of "record stores" available. Each "store" is a random selection of records for trade.
    public final static int          maxOffers            = 8;          // Maximum number of record offers a villager have
    public final static int          maxExtraVolume       = 128;        // Maximum amount of extra range for the custom jukebox
    public static String             customRecordsFolder  = "jukebox";  // Folder where this mod will look for custom records. Must be inside the 'Mods' folder.
    public static String             customRecordsPath;                 // Path of the custom records folder (used in URL creation)


    // Config variables
    public static int                customRecordOffersMin;             // Minimal of custom records a villager can offer
    public static int                customRecordOffersMax;             // Maximum of custom records a villager can offer
    public static int                customRecordPriceMin;              // Minimal value of custom records in emeralds
    public static int                customRecordPriceMax;              // Maximum value of custom records in emeralds
    public static int                maxCustomRecords;                  // Limit of custom records accepted


    // Debug mode
    public static boolean            onDebug;                           // Indicates if the MOD is on debug mode. Extra info will be tracked on the log.
    public static boolean            onDebugPackets;                    // Indicates if packets are also on debug

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Config file (ref: http://www.minecraftforge.net/wiki/How_to_make_an_advanced_configuration_file)
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        // Custom records config
        String customRecordCategory = "custom_records";

        // Custom records folder path
        ModRedstoneJukebox.customRecordsPath = "./mods/" + ModRedstoneJukebox.customRecordsFolder + "/";


        try {
            // loading the configuration from its file
            config.load();


            // Debug
            ModRedstoneJukebox.onDebug = config.get(customRecordCategory, "onDebug", false).getBoolean(false);
            ModRedstoneJukebox.onDebugPackets = config.get(customRecordCategory, "onDebugPackets", false).getBoolean(false);

            // Load blocks and items IDs
            ModRedstoneJukebox.redstoneJukeboxIdleID = config.getBlock("redstoneJukeboxIdleID", 520).getInt(520);
            ModRedstoneJukebox.redstoneJukeboxActiveID = config.getBlock("redstoneJukeboxActiveID", 521).getInt(521);
            ModRedstoneJukebox.blankRecordItemID = config.getItem("blankRecordItemID", 7200).getInt(7200);
            ModRedstoneJukebox.customRecordItemID = config.getItem("customRecordItemID", 7201).getInt(7201);

            // Amount of custom records
            ModRedstoneJukebox.maxCustomRecords = config.get(customRecordCategory, "maxCustomRecords", 32).getInt(32);

            // Extra validation on the custom records amount (min and max values)
            if (ModRedstoneJukebox.maxCustomRecords < 1) {
                ModRedstoneJukebox.maxCustomRecords = 1;
            }
            if (ModRedstoneJukebox.maxCustomRecords > 999) {
                ModRedstoneJukebox.maxCustomRecords = 999;
            }

            // Merchant config
            ModRedstoneJukebox.customRecordOffersMin = config.get(customRecordCategory, "customRecordOffersMin", 2).getInt(2);
            ModRedstoneJukebox.customRecordOffersMax = config.get(customRecordCategory, "customRecordOffersMax", 4).getInt(4);
            ModRedstoneJukebox.customRecordPriceMin = config.get(customRecordCategory, "customRecordPriceMin", 5).getInt(5);
            ModRedstoneJukebox.customRecordPriceMax = config.get(customRecordCategory, "customRecordPriceMax", 9).getInt(9);

            // Extra validation on the merchant config (min and max values)
            if (ModRedstoneJukebox.customRecordOffersMin < 1) {
                ModRedstoneJukebox.customRecordOffersMin = 1;
            }
            if (ModRedstoneJukebox.customRecordOffersMax < ModRedstoneJukebox.customRecordOffersMin) {
                ModRedstoneJukebox.customRecordOffersMax = ModRedstoneJukebox.customRecordOffersMin;
            }
            if (ModRedstoneJukebox.customRecordOffersMax > ModRedstoneJukebox.maxOffers) {
                ModRedstoneJukebox.customRecordOffersMax = ModRedstoneJukebox.maxOffers;
            }
            if (ModRedstoneJukebox.customRecordOffersMin < 1) {
                ModRedstoneJukebox.customRecordPriceMin = 1;
            }
            if (ModRedstoneJukebox.customRecordPriceMax < ModRedstoneJukebox.customRecordPriceMin) {
                ModRedstoneJukebox.customRecordPriceMax = ModRedstoneJukebox.customRecordPriceMin;
            }



            // Load the custom records
            CustomRecordHelper.LoadCustomRecordsConfig(config, customRecordCategory);


            // Custom records config help
            String br = System.getProperty("line.separator");
            String helpComment = "";

            helpComment += "How to add a custom record config" + br;
            helpComment += "------------------------------------------------------------------" + br;
            helpComment += "For each custom record, add a line below like this:" + br;
            helpComment += br;
            helpComment += "S:record###=ICON_ID;SONG_FILE;SONG_NAME" + br;
            helpComment += "    ###       = A number between 000 and " + String.format("%03d", ModRedstoneJukebox.maxCustomRecords - 1) + ". Do not repeat numbers. The numbers don't need to be in order." + br;
            helpComment += "    ICON_ID   = The icon of the this record. Must be a number between 1 and " + ModRedstoneJukebox.maxCustomRecordIcon + "." + br;
            helpComment += "    SONG_FILE = The name of the song file that should be on the 'mods/jukebox' folder. Only OGG files are accepted." + br;
            helpComment += "    SONG_NAME = The title of the song. Plain text, avoid using unicode characters. Max of 64 characters.";
            helpComment += br;
            helpComment += "Extra notes:" + br;
            helpComment += "  - if the game can't find the song file, the record won't be added;" + br;
            helpComment += "  - if the config line is incorrect, the record won't be added;" + br;
            helpComment += "  - if you still have trouble, set onDebug to 'true', restart the game, try playing a custom record and post your log on the Minecraft Forums;" + br;

            config.addCustomCategoryComment(customRecordCategory, helpComment);
        }
        catch (Exception e) {
            FMLLog.log(Level.SEVERE, "Error loading the configuration of the Redstone Jukebox Mod. Error message: " + e.getMessage() + " / " + e.toString());
        }
        finally {
            // saving the configuration to its file
            config.save();
        }


        // Textures and Icons paths
        ModRedstoneJukebox.blankRecordIcon = Reference.ResourcesNamespace + ":blank_record";
        ModRedstoneJukebox.customRecordIconArray = Reference.ResourcesNamespace + ":custom_record_";
        ModRedstoneJukebox.jukeboxDiscIcon = Reference.ResourcesNamespace + ":redstone_jukebox_disc";
        ModRedstoneJukebox.jukeboxTopIcon = Reference.ResourcesNamespace + ":redstone_jukebox_top";
        ModRedstoneJukebox.jukeboxBottomIcon = Reference.ResourcesNamespace + ":redstone_jukebox_bottom";
        ModRedstoneJukebox.jukeboxSideOnIcon = Reference.ResourcesNamespace + ":redstone_jukebox_on";
        ModRedstoneJukebox.jukeboxSideOffIcon = Reference.ResourcesNamespace + ":redstone_jukebox_off";
        ModRedstoneJukebox.guiTextureJukebox = "textures/gui/redstonejukebox-gui.png";
        ModRedstoneJukebox.guiTextureTrade = "textures/gui/recordtrading-gui.png";

        ModRedstoneJukebox.redstoneJukeboxGui = new ResourceLocation(Reference.ResourcesNamespace, ModRedstoneJukebox.guiTextureJukebox);
        ModRedstoneJukebox.recordTradeGui = new ResourceLocation(Reference.ResourcesNamespace, ModRedstoneJukebox.guiTextureTrade);


        // Blocks and Items
        ModRedstoneJukebox.recordBlank = new ItemBlankRecord(ModRedstoneJukebox.blankRecordItemID, JukeboxTab.instance, ModRedstoneJukebox.blankRecordIcon).setTextureName("record_blank");
        ModRedstoneJukebox.customRecord = new ItemCustomRecord(ModRedstoneJukebox.customRecordItemID, "customRecord").setTextureName("record_custom");
        ModRedstoneJukebox.redstoneJukebox = new BlockRedstoneJukebox(ModRedstoneJukebox.redstoneJukeboxIdleID, false).setHardness(2.0F).setResistance(10.0F).setUnlocalizedName("sidbenRedstoneJukebox").setStepSound(Block.soundStoneFootstep).setCreativeTab(JukeboxTab.instance).setTextureName("redstone_jukebox_off");
        ModRedstoneJukebox.redstoneJukeboxActive = new BlockRedstoneJukebox(ModRedstoneJukebox.redstoneJukeboxActiveID, true).setHardness(2.0F).setResistance(10.0F).setUnlocalizedName("sidbenRedstoneJukebox").setStepSound(Block.soundStoneFootstep).setLightValue(0.75F).setTextureName("redstone_jukebox_on");


        // Blocks
        GameRegistry.registerBlock(ModRedstoneJukebox.redstoneJukebox, "RedstoneJukeboxBlock");


        // Enchantments
        // enchantDummy = new EnchantmentDummy(200, 10);


        // Names
        LanguageRegistry.addName(ModRedstoneJukebox.recordBlank, "Blank Record");
        LanguageRegistry.addName(ModRedstoneJukebox.customRecord, "Music Disc");
        LanguageRegistry.addName(ModRedstoneJukebox.redstoneJukebox, "Redstone Jukebox");
        LanguageRegistry.instance().addStringLocalization("itemGroup.tabCustom", "en_US", "Redstone Jukebox");

    }

    

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {

        // Register my custom player event handler
        PlayerEventHandler playerEventHandler = new PlayerEventHandler();
        MinecraftForge.EVENT_BUS.register(playerEventHandler);

        // Register my custom sound handler
        SoundEventHandler soundEventHandler = new SoundEventHandler();
        MinecraftForge.EVENT_BUS.register(soundEventHandler);


        // Tile Entities
        GameRegistry.registerTileEntity(TileEntityRedstoneJukebox.class, "RedstoneJukeboxPlaylist");

        // GUIs
        NetworkRegistry.instance().registerGuiHandler(this, ModRedstoneJukebox.proxy);

        // Renderer
        ModRedstoneJukebox.proxy.registerRenderers();


        // Crafting Recipes
        ItemStack recordStack0 = new ItemStack(ModRedstoneJukebox.recordBlank, 1);
        ItemStack recordStack1 = new ItemStack(Item.record11);
        ItemStack recordStack2 = new ItemStack(Item.record13);
        ItemStack recordStack3 = new ItemStack(Item.recordCat);
        ItemStack recordStack4 = new ItemStack(Item.recordBlocks);
        ItemStack recordStack5 = new ItemStack(Item.recordChirp);
        ItemStack recordStack6 = new ItemStack(Item.recordFar);
        ItemStack recordStack7 = new ItemStack(Item.recordMall);
        ItemStack recordStack8 = new ItemStack(Item.recordMellohi);
        ItemStack recordStack9 = new ItemStack(Item.recordStal);
        ItemStack recordStack10 = new ItemStack(Item.recordStrad);
        ItemStack recordStack11 = new ItemStack(Item.recordWard);
        ItemStack recordStack12 = new ItemStack(Item.recordWait);

        ItemStack flintStack = new ItemStack(Item.flint);
        ItemStack redstoneStack = new ItemStack(Item.redstone);
        ItemStack redstoneTorchStack = new ItemStack(Block.torchRedstoneActive);
        ItemStack glassStack = new ItemStack(Block.glass);
        Block woodStack = Block.planks;
        ItemStack jukeboxStack = new ItemStack(Block.jukebox);


        // Recipe: Blank record
        GameRegistry.addShapelessRecipe(recordStack0, recordStack1, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack2, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack3, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack4, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack5, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack6, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack7, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack8, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack9, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack10, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack11, flintStack, redstoneStack);
        GameRegistry.addShapelessRecipe(recordStack0, recordStack12, flintStack, redstoneStack);

        // Load all current custom records as possible recipes.
        ItemStack customRecordStack;
        for (CustomRecordObject record : CustomRecordHelper.getRecordList()) {
            customRecordStack = CustomRecordHelper.getCustomRecord(record);
            if (customRecordStack != null) {
                GameRegistry.addShapelessRecipe(recordStack0, customRecordStack, flintStack, redstoneStack);
            }
        }

        // Recipe: Redstone Jukebox
        GameRegistry.addRecipe(new ItemStack(ModRedstoneJukebox.redstoneJukebox), "ggg", "tjt", "www", 'g', glassStack, 't', redstoneTorchStack, 'j', jukeboxStack, 'w', woodStack);


    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        // Custom Trades
        CustomRecordHelper.InitializeAllStores();

    }


    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // register custom commands
        event.registerServerCommand(new CommandPlayRecord());
        event.registerServerCommand(new CommandPlayRecordAt());
        event.registerServerCommand(new CommandPlayBgMusic());


        // register my custom server Tick Handler
        MusicTickHandler tickHandler = new MusicTickHandler();
        TickRegistry.registerScheduledTickHandler(tickHandler, Side.SERVER);
    }




    public static void logDebugInfo(String info) {
        ModRedstoneJukebox.logDebug(info, Level.INFO);
    }


    public static void logDebugPacket(String info) {
        if (ModRedstoneJukebox.onDebugPackets) {
            FMLLog.log("SidbenRedstoneJukebox", Level.INFO, info, "");
        }
    }


    public static void logDebug(String info, Level level) {
        if (Reference.ForceDebug) {
            System.out.println(info);
        }
        else {
            if (ModRedstoneJukebox.onDebug || level != Level.INFO) {
                FMLLog.log("SidbenRedstoneJukebox", level, "Debug: " + info, "");
            }
        }
    }


}