package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

class RingtoneSelector extends JComboBox<RingToneOption>
{
    private AudioClip customOption;

    RingtoneSelector(){
	super(RingToneOption.values());
	customOption = null;
	addActionListener(new ActionListener()
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
	});
    }

    private void handleInvalidFile() {
	JOptionPane.showMessageDialog(null, "Filen du valt stöds inte av PlanIt!", "Fel", JOptionPane.ERROR_MESSAGE, MainFrame.PLAN_IT_ERROR);
    }

    private void loadAudioClip(File audioFile){
	try{
	    URL url = audioFile.toURI().toURL();
	    customOption = Applet.newAudioClip(url);
	} catch (MalformedURLException ignored){
	    JOptionPane.showMessageDialog(null, "Ljudklippet kunde inte läsas in", "Fel", JOptionPane.ERROR_MESSAGE, MainFrame.PLAN_IT_ERROR);
	    setSelectedItem(RingToneOption.NONE);
	}
    }

    AudioClip getSelectedRingtone(){
	switch ((RingToneOption) getSelectedItem()){
	    case NONE:
		return null;
	    case CUSTOM:
		return customOption;
	    case BLIP:
		return Reminder.BLIP;
	    case PLING:
		return Reminder.PLING;
	    case WHISTLE:
		return Reminder.WHISTLE;
	    case TIME_HAS_COME:
		return Reminder.TIME_HAS_COME;
	    default:
		return null;
	}
    }
}
