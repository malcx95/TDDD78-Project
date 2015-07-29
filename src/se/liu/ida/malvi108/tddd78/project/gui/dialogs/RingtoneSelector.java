package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import javax.swing.*;

class RingtoneSelector extends JComboBox<Ringtone>
{

    RingtoneSelector(){
	super(Ringtone.values());
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


}
