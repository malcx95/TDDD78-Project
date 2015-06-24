package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import java.awt.Color;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.List;

/**
 * Dialog for removing elements of a given type from a set.
 * @param <T> The type of elements you want to remove
 */
public final class RemoveDialog<T>
{
    private T[] elements;

    public RemoveDialog(T[] elements){
        this.elements = elements;
    }

    /**
     * Shows a dialog for removing items from a collections of entries specified by the <code>T</code>
     * parameter provided during the construction of the <code>EditDialog</code> object. Returns
     * a list of the selected values, or null if the user clicked the Cancel-button.
     */
    public List<T> showRemoveDialog(){
        JList<T> list = new JList<>(elements);
        list.setBorder(new LineBorder(Color.BLACK));
        JComponent[] message = {
                new JLabel("Välj vilken/vilka du vill ta bort:"),
                list
        };
        int option = JOptionPane.showOptionDialog(null, message, "Ta bort", JOptionPane.OK_CANCEL_OPTION,
                                     JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (option == JOptionPane.OK_OPTION){
            return list.getSelectedValuesList();
        } else {
            return null;
        }
    }
}
