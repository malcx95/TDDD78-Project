package se.liu.ida.malvi108.tddd78.project.gui.main_gui;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.calendar_import.TimeEditImporter;
import se.liu.ida.malvi108.tddd78.project.databases.ToDoListDatabase;
import se.liu.ida.malvi108.tddd78.project.exceptions.FileCorruptedException;
import se.liu.ida.malvi108.tddd78.project.gui.calendar_list.RemoveCalendarAction;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.CalendarDialog;
import se.liu.ida.malvi108.tddd78.project.gui.miscellaneous.DateSelector;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_classes.MonthView;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_classes.View;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarDatabaseListener;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.gui.calendar_list.CalendarListPanel;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_classes.DayView;
import se.liu.ida.malvi108.tddd78.project.listeners.DatabaseSaveErrorListener;
import se.liu.ida.malvi108.tddd78.project.listeners.ViewListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.Year;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main window of the program. If this window is closed, the program quits.
 */
public final class MainFrame extends JFrame implements CalendarDatabaseListener, ViewListener, DatabaseSaveErrorListener
{
    private final static Logger LOGGER = Logger.getLogger(MainFrame.class.getName());
    private final static double FRAME_TO_SCREEN_RATIO = 0.7;
    /**
     * The font of every single component in the program.
     */
    public final static String GLOBAL_FONT = "Helvetica";
    /**
     * The main icon for the program.
     */
    public final static Icon PLAN_IT_ICON =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/plan_it_icon.png"));
    /**
     * The PlanIt error icon for dialogs.
     */
    public final static ImageIcon PLAN_IT_ERROR =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/plan_it_error.png"));
    /**
     * The PlantIt warning icon for dialogs.
     */
    public final static ImageIcon PLAN_IT_WARNING =
    	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/plan_it_warning.png"));
    /**
     * A larger version of PLAN_IT_ICON.
     */
    public final static ImageIcon PLAN_IT_ICON_LARGE =
    	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/plan_it_icon_large.png"));
    private final static Icon TIME_EDIT_INSTRUCTION =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/time_edit_instruction.png"));
    private final static Icon TIME_EDIT_ICON =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/time_edit_import.png"));
    private final static Icon REMOVE_APP_ICON =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/remove_app.png"));
    private final static Icon REMOVE_CAL_ICON =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/remove_cal.png"));
    private static final Icon NEW_APP_ICON =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/new_app.png"));
    private static final Icon NEW_CALENDAR_ICON =
	    new ImageIcon(MainFrame.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/new_cal.png"));
    private JMenuItem newAppointment;
    private JMenuItem removeCalendars;
    private JTabbedPane tabbedViewPane;
    private JMenuItem removeAppointment;
    /**
     * The panel which all components are ancestors of.
     */
    private JPanel mainPanel;

    public MainFrame(){
	super("PlanIt");
	makeSureCorrectTimeIsSet();
	loadDatabases();
	setIconImage(PLAN_IT_ICON_LARGE.getImage());
	JPanel viewPane = createViewPane();
	CalendarListPanel calListPanel = new CalendarListPanel();
	CalendarDatabase db = CalendarDatabase.getInstance();
	db.addDatabaseListener(calListPanel);
	JScrollPane calendarList = new JScrollPane(calListPanel);
	calendarList.setBorder(new TitledBorder(new SoftBevelBorder(BevelBorder.LOWERED), "Kalendrar"));
	calendarList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	createMenus();
	mainPanel = new JPanel(new BorderLayout());
	mainPanel.add(calendarList, BorderLayout.WEST);
	mainPanel.add(viewPane, BorderLayout.CENTER);
	add(mainPanel);
	setCorrectSizeAndPosition();
	db.addDatabaseListener(this);
	db.addSaveErrorListener(this);
	ToDoListDatabase.getInstance().addSaveErrorListener(this);
	//to check if there are calendars in the database so that the MenuItems are correctly enabled
	databaseChanged();
	addBindings();
    }

    private static void loadDatabases() {
        try {
            CalendarDatabase.getInstance().loadDatabase();
        } catch (FileCorruptedException ex) {
            JOptionPane.showMessageDialog(null, "Kunde inte läsa in kalenderdatabasen! Databasfilen är skadad!",
                                          "Databasfil skadad", JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
	    LOGGER.log(Level.WARNING, "Couldn't load the CalendarDatabase! " + ex.getMessage() + " Cause:" + ex.getCause());
        } catch (FileNotFoundException ignored){
            JOptionPane.showMessageDialog(null, "Hittade ingen kalenderdatabasfil, en ny databasfil har att skapats.",
					  "Ingen databasfil hittades", JOptionPane.INFORMATION_MESSAGE, PLAN_IT_ICON);
        } catch (EOFException ignored){
            //This means that the database is empty, and nothing needs to be done.
            LOGGER.log(Level.INFO, "CalendarDatabase is empty.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "<html><body>Kunde inte läsa in kalenderdatabasen! Fel: <br>" +
                                                ex.getMessage() + "</body></html>", "Inläsningsfel" , JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
	    LOGGER.log(Level.INFO, "Couldn't load the CalendarDatabase! A " + ex.getClass().getName() + " occured: " + ex.getMessage());
        }
        //Separate catch-blocks for different messages.
        try{
            ToDoListDatabase.getInstance().loadDatabase();
        } catch (FileCorruptedException ex){
            JOptionPane.showMessageDialog(null, "Kunde inte läsa in databasen för att-göra-listorna! Databasfilen är skadad!",
                                          "Databasfil skadad", JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
	    LOGGER.log(Level.WARNING, "Couldn't load the ToDoListDatabase! " + ex.getMessage() + " Cause:" + ex.getCause());
        } catch (FileNotFoundException ignored){
            JOptionPane.showMessageDialog(null, "Hittade ingen att-göra-databasfil, en ny databasfil har att skapats.",
                                                      "Ingen databasfil hittades", JOptionPane.INFORMATION_MESSAGE, PLAN_IT_ICON);
        } catch (EOFException ignored){
            //This means that the database is empty, and nothing needs to be done.
            LOGGER.log(Level.INFO, "ToDoListDatabase is empty.");
        } catch (IOException ex){
            JOptionPane.showMessageDialog(null, "Kunde inte läsa in databasen för att-göra-listorna! Fel:" +
                                                ex.getMessage(), "Inläsningsfel" , JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
        }
    }

    /**
     * Checks whether the system clock is set incorrectly to being a year that precedes
     * the starting year, in which case it should alert the user and shut down the program.
     */
    private static void makeSureCorrectTimeIsSet() {
        int todaysYear = LocalDate.now().getYear();
        if (todaysYear < Year.STARTING_YEAR){
            LOGGER.log(Level.SEVERE, "System clock is set to a year that precedes the " +
                                     "STARTING_YEAR, shutting the program down...");
            JOptionPane.showMessageDialog(null, "Fel! Systemklockan är inställd på ett " +
                                                "år som inte stöds av PlanIt: " + todaysYear +
                                                " Vänligen ställ om klockan till rätt tid och starta om PlanIt.",
                                          "Felinställd systemklocka", JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
            System.exit(0);
        }
        LOGGER.log(Level.INFO, "System clock OK");
    }

    /**
     * Informs the user through a dialog if a save failed.
     * @param ex The exception that caused the save to fail.
     */
    @Override public void calendarDatabaseSaveFailed(final IOException ex) {
	JOptionPane.showMessageDialog(this, "<html><body>Databasen för kalendrar kunde inte sparas! Fel: <br>" +
		ex.getMessage() + "</body></html>", "Sparande misslyckades", JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
    }

    @Override public void toDoListDatabaseSaveFailed(final IOException ex) {
	JOptionPane.showMessageDialog(this, "<html><body>Databasen för att-göra-listorna kunde inte sparas! Fel: <br>" +
			ex.getMessage() + "</body></html>", "Sparande misslyckades", JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
    }

    private enum ActionKeys {DELETE, NEXT, PREVIOUS, NEW_APP, NEW_CAL, QUIT, GO_TO_DATE, GO_TO_TODAY}

    private void addBindings() {
 	ActionMap actionMap = mainPanel.getActionMap();
 	actionMap.put(ActionKeys.DELETE, new DeleteAppointmentAction());
 	actionMap.put(ActionKeys.NEXT, new GoToNextAction());
 	actionMap.put(ActionKeys.PREVIOUS, new GoToPreviousAction());
	actionMap.put(ActionKeys.NEW_APP, new NewAppointmentAction());
	actionMap.put(ActionKeys.NEW_CAL, new NewCalendarAction());
	actionMap.put(ActionKeys.QUIT, new QuitAction());
	actionMap.put(ActionKeys.GO_TO_DATE, new GoToDateAction());
	actionMap.put(ActionKeys.GO_TO_TODAY, new GoToTodayAction());

 	InputMap map = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
 	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK), ActionKeys.NEXT);
 	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK), ActionKeys.PREVIOUS);
 	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), ActionKeys.DELETE);
	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), ActionKeys.DELETE);
	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), ActionKeys.NEW_APP);
	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK), ActionKeys.NEW_CAL);
	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK), ActionKeys.QUIT);
	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK), ActionKeys.GO_TO_DATE);
	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK), ActionKeys.GO_TO_TODAY);
    }

    /**
     * Creates a <code>JPanel</code> containing the <code>JTabbedPane</code> that holds
     * the different views. Each tab in the tabbed pane must contain a component implementing
     * the gui.views.View interface.
     */
    private JPanel createViewPane() {
	JPanel viewPane = new JPanel(new BorderLayout());
	tabbedViewPane = new JTabbedPane();
	tabbedViewPane.addTab("Dagsvy", new DayView(Date.getToday()));
	tabbedViewPane.addTab("Månadsvy", new MonthView(Date.getToday()));
	//add this to the list of ViewListeners to each view.
	for (Component component : tabbedViewPane.getComponents()) {
	    View view = (View) component;
	    view.addViewListener(this);
	}
	viewPane.setBackground(Color.WHITE);
	viewPane.add(tabbedViewPane, BorderLayout.CENTER);
	return viewPane;
    }

    private void createMenus(){
	final JMenu file = createFileMenu();

	final JMenu edit = createEditMenu();

	final JMenu navigate = createNavigateMenu();

	final JMenuBar menuBar = new JMenuBar();

	menuBar.add(file);
	menuBar.add(edit);
	menuBar.add(navigate);
	menuBar.add(Box.createHorizontalGlue());
	setJMenuBar(menuBar);
    }

    private JMenu createNavigateMenu() {
	JMenu navigate = new JMenu("Navigering");
	navigate.setMnemonic('N');
	JMenuItem goToDate = new JMenuItem("Gå till datum...");
	navigate.add(goToDate);
	goToDate.addActionListener(new GoToDateAction());
	return navigate;
    }

    private void navigateToDate() {
	View currentView = (View) tabbedViewPane.getSelectedComponent();
	DateSelector selector = new DateSelector();
	JComponent[] inputs = {new JLabel("Välj ett datum att gå till"), selector};
	String[] options = {"Gå", "Avbryt"};
	int option = JOptionPane.showOptionDialog(this, inputs, "Navigera till datum",
						  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
						  null, options, options[0]);
	if (option == JOptionPane.OK_OPTION){
	    currentView.setDate(selector.getSelectedDate());
	}
    }

    private JMenu createEditMenu() {
	final JMenu edit = new JMenu("Redigera");
	edit.setMnemonic('R');
	removeCalendars = new JMenuItem("Ta bort kalendrar...", 'b');
	removeCalendars.setIcon(REMOVE_CAL_ICON);
	removeAppointment = new JMenuItem("Ta bort aktivitet", 'a');
	removeAppointment.setEnabled(false);
	removeAppointment.setIcon(REMOVE_APP_ICON);
	edit.add(removeCalendars);
	edit.add(removeAppointment);
	addRemoveAppointmentListener(removeAppointment);
	removeCalendars.addActionListener(new RemoveCalendarAction());
	return edit;
    }

    private void addRemoveAppointmentListener(final JMenuItem removeAppointment) {
	ActionListener listener = new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		View currentView = (View) tabbedViewPane.getSelectedComponent();
		currentView.deleteSelectedAppointment();
	    }
	};
	removeAppointment.addActionListener(listener);
    }

    private JMenu createFileMenu() {
	final JMenu file = new JMenu("Arkiv");
	file.setMnemonic('A');
	newAppointment = new JMenuItem("Ny aktivitet", 'a');
	final JMenuItem newCalendar = new JMenuItem("Ny kalender", 'k');
	final JMenuItem importFromTimeEdit = new JMenuItem("Importera schema from TimeEdit", 's');
	final JMenuItem quit = new JMenuItem("Avsluta PlanIt", 'v');
	file.add(newAppointment);
	file.add(newCalendar);
	file.add(importFromTimeEdit);
	file.addSeparator();
	file.add(quit);

	newAppointment.setIcon(NEW_APP_ICON);

	newCalendar.setIcon(NEW_CALENDAR_ICON);
	newAppointment.addActionListener(new NewAppointmentAction());
	newCalendar.addActionListener(new NewCalendarAction());
	addImportTimeEditListener(importFromTimeEdit);
	importFromTimeEdit.setIcon(TIME_EDIT_ICON);
	quit.addActionListener(new QuitAction());
	return file;
    }

    private void addImportTimeEditListener(final JMenuItem importFromTimeEdit) {
	ActionListener listener = new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		importFromTimeEdit();
	    }
	};
	importFromTimeEdit.addActionListener(listener);
    }

    private void importFromTimeEdit() {
	showImportInstruction();
	JFileChooser chooser = new JFileChooser();
	chooser.setFileFilter(new FileNameExtensionFilter("Textfiler", "txt"));
	int option = chooser.showOpenDialog(null);
	if (option == JFileChooser.APPROVE_OPTION){
	    try {
		LOGGER.log(Level.INFO, "Importing from TimeEdit file...");
		TimeEditImporter.importFromTimeEdit(chooser.getSelectedFile());
		LOGGER.log(Level.INFO, "TimeEdit schedule successfully imported.");
	    } catch (FileCorruptedException ex){
		LOGGER.log(Level.WARNING, "Couldn't import from TimeEdit! File is corrupted, cause: " + ex.getCause());
		JOptionPane.showMessageDialog(this,
					      "<html><body>Kunde inte importera schemat! Filen är av fel typ eller skadad.</body></html>", "Fel",
					      JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
	    } catch (IOException ex) {
		String message = ex.getMessage();
		LOGGER.log(Level.WARNING, "Couldn't import from TimeEdit! An IO error occured: " + message);
		JOptionPane.showMessageDialog(this, "<html><body>Kunde inte importera schemat! Ett fel inträffade när filen skulle läsas: <br>" +
						message + "</body></html>", "Fel", JOptionPane.ERROR_MESSAGE, PLAN_IT_ERROR);
	    }
	}
	//instead of the TimeEditImporter notifying the calendar listeners the MainFrame manually reloads the views.
	updateViews();
    }

    private void updateViews() {
	for (Component component : tabbedViewPane.getComponents()) {
	    View view = (View) component;
	    view.reloadAppointments();
	}
    }

    /**
     * Shows a dialog instructing the user how to import their TimeEdit schedule.
     */
    private void showImportInstruction() {
	JOptionPane.showMessageDialog(null, "<html><body>I TimeEdit i din webbläsare, välj <b><i>Anpassa</i></b>, och <br>" +
					    "sedan välj <b><i>Export</i></b>. Klicka på <b><i>Text</i></b>, spara filen <br>" +
					    "på din dator och välj den i nästa steg.</body></html>", "Importera från TimeEdit",
				      JOptionPane.PLAIN_MESSAGE, TIME_EDIT_INSTRUCTION);
    }

    /**
     * Sets an appropriate size of the frame depending on the screen size, as well as
     * positions the frame in the center of the display.
     */
    private void setCorrectSizeAndPosition() {
	int screenHeight = this.getToolkit().getScreenSize().height;
	int screenWidth = this.getToolkit().getScreenSize().width;
	this.setSize(new Dimension((int) (screenWidth * FRAME_TO_SCREEN_RATIO), (int) (screenHeight * FRAME_TO_SCREEN_RATIO)));
	this.setLocation(screenWidth / 2 - this.getWidth() / 2, screenHeight / 2 - this.getHeight() / 2);
    }

    /**
     * Called when a calendar is added or removed to the database.
     */
    @Override public void databaseChanged() {
	if (CalendarDatabase.getInstance().isEmpty()){
	    newAppointment.setEnabled(false);
	    removeCalendars.setEnabled(false);
	} else {
	    newAppointment.setEnabled(true);
	    removeCalendars.setEnabled(true);
	}
	appointmentSelectedOrDeselected(); //if a calendar with a selected app is removed
    }

    /**
     * Invoked when someone selects or deselects an appointment in a view.
     */
    @Override public void appointmentSelectedOrDeselected() {
	View currentView = (View) tabbedViewPane.getSelectedComponent();
	Appointment selectedApp = currentView.getSelectedAppointment();
	//selectedApp not used since the method's only job is to toggle the enabling of the menu item for removing an appointment.
	if (selectedApp == null){
	    removeAppointment.setEnabled(false);
	} else {
	    removeAppointment.setEnabled(true);
	}
    }

    private class DeleteAppointmentAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    View view = (View) tabbedViewPane.getSelectedComponent();
	    view.deleteSelectedAppointment();
	}
    }

    private class GoToNextAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    View view = (View) tabbedViewPane.getSelectedComponent();
	    view.goToNext();
	}
    }

    private class GoToPreviousAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    View view = (View) tabbedViewPane.getSelectedComponent();
	    view.goToPrevious();
	}
    }

    private class GoToTodayAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    View view = (View) tabbedViewPane.getSelectedComponent();
	    view.goToToday();
	}
    }

    private class NewAppointmentAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    View view = (View) tabbedViewPane.getSelectedComponent();
	    view.inputNewAppointment();
	}
    }

    private class NewCalendarAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    CalendarDialog dialog = new CalendarDialog();
	    dialog.inputCalendar(CalendarDialog.NEW);
	}
    }

    private class QuitAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    System.exit(0);
	}
    }

    private class GoToDateAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    navigateToDate();
	}
    }
}
