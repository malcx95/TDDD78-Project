package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Class for ringtones. Enumerates a number of ringtones (as well as a 'NONE'-option)
 * and plays them with the <code>play()</code>-method.
 */
public enum Ringtone
{
    /**
     * No ringtone.
     */
    NONE,
    /**
     * Sound similar to that of a watch-alarm.
     */
    BLIP,
    /**
     * The sound of a glass being striked.
     */
    PLING,
    /**
     * The sound of a simple whistle.
     */
    WHISTLE,
    /**
     * The sound of someone saying 'The time has come, to perform the task you set out to perform'.
     */
    TIME_HAS_COME;

    private transient final static AudioClip BLIP_SOUND = Applet
		.newAudioClip(Ringtone.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/blipblip.wav"));

    private transient final static AudioClip PLING_SOUND = Applet.newAudioClip(
            Ringtone.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/pling.wav"));

    private transient final static AudioClip WHISTLE_SOUND = Applet.newAudioClip(
            Ringtone.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/whistle.wav"));

    private transient final static AudioClip TIME_HAS_COME_SOUND = Applet.newAudioClip(
            Ringtone.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/timehascome.wav"));

    @Override public String toString() {
	switch (this){
	    case NONE:
		return "Ingen ringsignal";
	    case BLIP:
		return "Armbandsur";
	    case PLING:
		return "Glas";
	    case WHISTLE:
		return "Vissling";
	    case TIME_HAS_COME:
		return "\"Tiden är kommen\"";
	    default:
		return "";
	}
    }

    public void play(){
	switch (this){
	    case BLIP:
		BLIP_SOUND.play();
		break;
	    case PLING:
		PLING_SOUND.play();
		break;
	    case WHISTLE:
		WHISTLE_SOUND.play();
		break;
	    case TIME_HAS_COME:
		TIME_HAS_COME_SOUND.play();
		break;
	    case NONE:
	    default:
		break;
	}
    }
}
