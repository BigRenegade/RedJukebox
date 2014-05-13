package redstonejukebox.network;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import redstonejukebox.ModRedstoneJukebox;
import redstonejukebox.common.ContainerRecordTrading;
import redstonejukebox.common.ContainerRedstoneJukebox;
import redstonejukebox.config.Reference;
import redstonejukebox.helper.PlayMusicHelper;
import redstonejukebox.tileentites.TileEntityRedstoneJukebox;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;




public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {

        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (payload.channel.equals(Reference.Channel)) {
            try {
                DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload.data));
                byte packetType = data.readByte();



                if (side == Side.SERVER) {
                    EntityPlayer sender = (EntityPlayer) player;


                    // ----------------------------------------------------------------------------
                    // Jukebox GUI Packet
                    // ----------------------------------------------------------------------------
                    if (packetType == PacketHelper.JukeboxGUIUpdate && sender.openContainer instanceof ContainerRedstoneJukebox) {

                        // Load data
                        boolean isLoop = data.readBoolean();
                        int playMode = data.readInt();



                        // Process data
                        ContainerRedstoneJukebox myJuke = (ContainerRedstoneJukebox) sender.openContainer;
                        TileEntityRedstoneJukebox teJukebox = myJuke.GetTileEntity();

                        teJukebox.isLoop = isLoop;
                        teJukebox.playMode = playMode;
                        teJukebox.onInventoryChanged();

                        // Sync Server and Client TileEntities (markBlockForUpdate method)
                        teJukebox.resync();
                    }


                    // ----------------------------------------------------------------------------
                    // Record Trading GUI Packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.RecordTradingGUIUpdate && sender.openContainer instanceof ContainerRecordTrading) {

                        // Load data
                        int currentRecipe = data.readInt();



                        // Process data
                        ContainerRecordTrading myTrade = (ContainerRecordTrading) sender.openContainer;
                        myTrade.setCurrentRecipeIndex(currentRecipe);
                    }


                    // ----------------------------------------------------------------------------
                    // Response if is playing packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.IsPlayingAnswer) {

                        // Load data
                        byte questionId = data.readByte();
                        String playerName = data.readUTF();
                        int playX = data.readInt();
                        int playY = data.readInt();
                        int playZ = data.readInt();
                        int playDim = data.readInt();


                        // Process data
                        // Only stores TRUE values, and only of the right question ID
                        if (questionId == PacketHelper.isPlayingQuestionCode) {
                            PlayMusicHelper.AddResponse(playerName, playX, playY, playZ, playDim);
                        }

                    }

                }
                else if (side == Side.CLIENT) {
                    // ----------------------------------------------------------------------------
                    // Play Record At Packet
                    // ----------------------------------------------------------------------------
                    if (packetType == PacketHelper.PlayRecordAt) {

                        // Load data
                        String songID = data.readUTF();
                        int sourceX = data.readInt();
                        int sourceY = data.readInt();
                        int sourceZ = data.readInt();
                        boolean showName = data.readBoolean();
                        float volumeExtra = data.readFloat();



                        // Process data
                        if (songID.equals("-")) {
                            songID = null;
                        }
                        PlayMusicHelper.playAnyRecordAt(songID, sourceX, sourceY, sourceZ, showName, volumeExtra);
                    }


                    // ----------------------------------------------------------------------------
                    // Play Record Packet (as background music)
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.PlayBgRecord) {

                        // Load data
                        String songName = data.readUTF();
                        boolean showName = data.readBoolean();



                        // Process data
                        PlayMusicHelper.playBgMusic(songName, true, showName);
                    }


                    // ----------------------------------------------------------------------------
                    // Play BgMusic Packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.PlayBgMusic) {

                        // Load data
                        String songName = data.readUTF();



                        // Process data
                        PlayMusicHelper.playBgMusic(songName, false, false);
                    }


                    // ----------------------------------------------------------------------------
                    // Request if is playing packet
                    // ----------------------------------------------------------------------------
                    else if (packetType == PacketHelper.IsPlayingQuestion) {

                        // Load data
                        byte questionId = data.readByte();


                        // Prepare data
                        Minecraft myMC = ModLoader.getMinecraftInstance();
                        EntityPlayer myself = (EntityPlayer) player;
                        String myName = myself.username;
                        boolean amIPlaying = false;
                        amIPlaying = myMC.sndManager.sndSystem.playing(ModRedstoneJukebox.sourceName);
                        int playX = PlayMusicHelper.lastSoundSourceClient.x;
                        int playY = PlayMusicHelper.lastSoundSourceClient.y;
                        int playZ = PlayMusicHelper.lastSoundSourceClient.z;
                        int myDim = myself.dimension;

                        // Send response
                        PacketHelper.sendIsPlayingAnswerPacket(questionId, myName, amIPlaying, playX, playY, playZ, myDim);
                    }

                }

            }
            catch (Exception e) {
                ModRedstoneJukebox.logDebug("Error: " + e.getMessage() + " / " + e.toString(), Level.SEVERE);

            }

        }



    }

}
