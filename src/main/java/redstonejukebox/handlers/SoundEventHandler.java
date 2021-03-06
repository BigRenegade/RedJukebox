package redstonejukebox.handlers;


import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.event.ForgeSubscribe;
import redstonejukebox.ModRedstoneJukebox;
import redstonejukebox.helper.MusicCoords;
import redstonejukebox.helper.PlayMusicHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



//initial ref: http://www.minecraftforum.net/topic/1058091-tutorial-custom-sounds-and-more/
public class SoundEventHandler {


    // -- Is called right before any streaming sound is played.
//    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void onPlayStreamingSourceEvent(PlayStreamingSourceEvent event) {

        // Updates the sound source on the client
        // OBS: dimension doesn't matter, the player will complete when sending the packet
        PlayMusicHelper.lastSoundSourceClient = new MusicCoords((int) event.x, (int) event.y, (int) event.z, 0);

    }


}
