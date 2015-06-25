package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

class RingtoneSelector extends JComboBox<RingToneOption>
{
    /**
     * Sound similar to that of a watch-alarm.
     */
    public final static AudioClip BLIP = Applet.newAudioClip(
	    RingtoneSelector.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/blipblip.wav"));
    /**
     * The sound of a glass being striked.
     */
    public final static AudioClip PLING = Applet.newAudioClip(
	    RingtoneSelector.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/pling.wav"));
    /**
     * The sound of a simple whistle.
     */
    public final static AudioClip WHISTLE = Applet.newAudioClip(
	    RingtoneSelector.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/whistle.wav"));
    /**
     * The sound of someone saying 'The time has come, to perform the task you set out to perform'.
     */
    public final static AudioClip TIME_HAS_COME = Applet.newAudioClip(
	    RingtoneSelector.class.getResource("/se/liu/ida/malvi108/tddd78/project/sounds/timehascome.wav"));

    RingtoneSelector(){
	super(RingToneOption.values());
	/*addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		if (getSelectedItem() == RingToneOption.CUSTOM){
		    JFileChooser fileChooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("Ljudfiler", "wav");
		    fileChooser.setFileFilter(filter);
		    int option = fileChooser.showOpenDialog(null);
		    if (option == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			if (filter.accept(file)){
			    loadAudioClip(file);
			} else {
			    handleInvalidFile();
			    actionPerformed(e);
			}
		    } else {
			setSelectedItem(RingToneOption.NONE);
		    }
		}
	    }
	});*/
    }

    /*private void handleInvalidFile() {
	JOptionPane.showMessageDialog(null, "Filen du valt stöds inte av PlanIt!", "Fel", JOptionPane.ERROR_MESSAGE, MainFrame.PLAN_IT_ERROR);
    }*/

    /*private void loadAudioClip(File audioFile){
	try{
	    URL url = audioFile.toURI().toURL();
	    customOption = Applet.newAudioClip(url);
	} catch (MalformedURLException ignored){
	    JOptionPane.showMessageDialog(null, "Ljudklippet kunde inte läsas in", "Fel", JOptionPane.ERROR_MESSAGE, MainFrame.PLAN_IT_ERROR);
	    setSelectedItem(RingToneOption.NONE);
	}
    }*/

    AudioClip getSelectedRingtone(){
	switch ((RingToneOption) getSelectedItem()){
	    case NONE:
		return null;
	    case BLIP:
		return BLIP;
	    case PLING:
		return PLING;
	    case WHISTLE:
		return WHISTLE;
	    case TIME_HAS_COME:
		return TIME_HAS_COME;
	    default:
		return null;
	}
    }
}
