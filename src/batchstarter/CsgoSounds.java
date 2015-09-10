package batchstarter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import static java.nio.file.StandardWatchEventKinds.*;

public class CsgoSounds {

	private static Path convertpath = null;
	protected static boolean closewanted = false;
	protected static List<String> hotkeybuttonstrings = new ArrayList<>();
	private static boolean fileexisted = false;
	private static Path savefile = Paths.get(System.getenv("APPDATA") + "/CsgoSwitcher/saves/hotkeys.svf");
	protected static Path csgomainfolder;
	protected static Path randomsong;
	protected static Path batchfile = Paths.get(System.getProperty("user.dir") + "/changefiles.bat");
	public static String newline = System.getProperty("line.separator");
	private static String defaultText = "5" + newline + "" + newline + "" + newline + "" + newline + "" + newline
			+ "1:NULL;" + newline + "2:NULL;" + newline + "3:NULL;" + newline + "4:NULL;" + newline + "5:NULL;";
	protected static boolean waitforinput = false;
	protected static String lastpressed = "";
	protected static JLabel pressedbutton = new JLabel("Waiting for Input!", SwingConstants.CENTER);
	protected static List<JButton> hotkeylist = new ArrayList<>();
	protected static JFrame frame2;
	protected static int currentlywaiting = 0;
	private static Font font = new Font("Comic Sans MS", Font.PLAIN, 15);
	private static Font heading = new Font("Comic Sans MS", Font.BOLD, 14);
	private static int returnval = 0;
	private static File selectedfile = new File("");
	// protected static List<String> wavfilenames = new ArrayList<>();
	// protected static List<String> wavfilenamesfull = new ArrayList<>();
	private static JCheckBox box;
	private static boolean disabletrue = false;
	protected static JLabel statusanzeige = new JLabel("");
	private static List<Integer> hotkeyorder = new ArrayList<>();
	private static boolean noundernull;
	private static NumberFormat nf;
	private static JFrame files;
	private static boolean nodouble;
	protected static List<JLabel> namelabels = new ArrayList<>();
	private static int hotkeysize;
	private static int hotkeysave;
	private static boolean haschanged = false;
	protected static List<String> randomwavfilenames = new ArrayList<>();
	protected static List<String> randomwavfilenamesfull = new ArrayList<>();
	protected static boolean hasrandom = false;
	private static boolean randomizedmode = false;
	protected static int currentsong = 0;
	protected static List<String> wavfilenames = new ArrayList<>();
	protected static List<String> wavfilenamesfull = new ArrayList<>();
	private static Thread windowlistenerthread = null;
	private static Thread soundlistenerthread = null;
	private static WatchService watcher = null;
	private static WatchService watcher2 = null;

	public static void main(String[] args) {

		// Sets System Default Look & Feel

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "Could not change Look & Feel", "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Creates JFrame and sets Default Close Operation
		try {
			watcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(null, "Could not create Watch Service", "Error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			watcher2 = FileSystems.getDefault().newWatchService();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		JFrame frame = new JFrame("CSGO Sound Changer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DragAndDropFactory dnd = new DragAndDropFactory();
		TransferHandler tf = dnd.getTransferHandler();
		frame.setTransferHandler(tf);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				shutDown();
			}
		});

		// Try to create hotkeys.txt if it already exists then give exception

		try {
			Files.createDirectories(savefile.getParent());
			Files.createFile(savefile);
		}

		catch (FileAlreadyExistsException e) {

			// set flag that the file could be loaded so no more info is written
			// in it.

			fileexisted = true;

		}

		catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "IO Exception bei Directory", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}

		// After here there has to be a file, with content or without
		// Check if the file was created or if it was there.

		if (!fileexisted) {
			writeToHotkey(defaultText);
		}

		// Reads the whole hotkeys.txt file and saves chars after ':' to the
		// hotkey list

		try (BufferedReader r = Files.newBufferedReader(savefile)) {

			String wholedoc = "";

			hotkeysize = Integer.parseInt(r.readLine());

			selectedfile = new File(r.readLine());
			csgomainfolder = Paths.get(r.readLine());
			randomsong = Paths.get(r.readLine());

			if (!randomsong.toString().equals("")) {
				createRandomSubfolders();
				hasrandom = true;
				createRandomWatcher();
			}

			String readhotkeyorder = r.readLine();
			if (!readhotkeyorder.equals("")) {
				String[] result = readhotkeyorder.split(",");
				Arrays.asList(result).forEach(s -> hotkeyorder.add(Integer.parseInt(s)));
			}

			else {
				for (int i = 0; i < hotkeysize; i++) {
					hotkeyorder.add(i + 1);
				}
			}

			if (!selectedfile.toString().trim().equals("")) {
				createSubFolders();
				createSoundWatcher();
			}

			while (r.ready()) {
				wholedoc += r.readLine() + "\n";
			}

			Pattern pattern = Pattern.compile(":(.*?);");
			Matcher matcher = pattern.matcher(wholedoc);
			while (matcher.find()) {
				hotkeybuttonstrings.add(matcher.group(1));
			}

		}

		catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "IOException beim File lesen", "Error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}

		// Creates the Main JFrame and fills it with information from
		// hotkeys.txt

		JButton exitknopf = new JButton("EXIT");
		frame.setSize(350, 300 + hotkeysize * 45);
		frame.setFont(font);
		frame.setLayout(new GridLayout(0, 2, 5, 5));
		JLabel curhotkey = new JLabel("Current Song : ", SwingConstants.CENTER);
		curhotkey.setFont(heading);
		frame.add(curhotkey);
		frame.add(statusanzeige);
		statusanzeige.setFont(font);
		for (int i = 0; i < hotkeysize; i++) {
			hotkeylist.add(new JButton());
		}

		JLabel l;

		for (int i = 0; i < hotkeylist.size(); i++) {
			l = new JLabel();
			l.setFont(font);
			l.setText("Undefined");
			hotkeylist.get(i).setFont(font);
			hotkeylist.get(i).setText(hotkeybuttonstrings.get(i));
			namelabels.add(l);
		}

		updateLabels();

		for (int i = 0; i < hotkeylist.size(); i++) {
			frame.add(hotkeylist.get(i));
			if (i < hotkeylist.size() - 1) {
				frame.add(namelabels.get(i));
			}
			System.out.println(namelabels.get(i).getText());
		}

		JLabel random = new JLabel("(Random)");
		random.setFont(font);

		frame.add(random);
		Font instructfont = new Font("Comic Sans MS", Font.PLAIN, 13);
		JCheckBox enablerandom = new JCheckBox("Randomized Mode");
		enablerandom.setFont(instructfont);
		enablerandom.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (randomizedmode == false) {
					randomizedmode = true;
					statusanzeige.setText("RANDOM ACTIVE");
				} else {
					randomizedmode = false;
					if (!randomwavfilenames.isEmpty()) {
						statusanzeige.setText(randomwavfilenames.get(currentsong));
					} else {
						statusanzeige.setText("Pick Random first!");
					}
				}
			}
		});

		if (!randomwavfilenames.isEmpty()) {
			statusanzeige.setText(randomwavfilenames.get(currentsong));
		} else {
			statusanzeige.setText("Pick Random first!");
		}

		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final JButton b = new JButton("Open File");
		b.setFont(font);

		box = new JCheckBox("Disable");
		box.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {

				Object source = arg0.getItemSelectable();

				if (source == box) {

					if (disabletrue == false)
						disabletrue = true;
					else {
						disabletrue = false;
					}

				}

			}

		});
		frame.add(enablerandom);
		frame.add(box);

		for (int i = 0; i < 1; i++) {
			frame.add(new Label(""));
		}

		frame.add(new JLabel());

		JButton orderChange = new JButton("Change Order");
		orderChange.setFont(font);
		frame.add(orderChange);
		JButton preferredSize = new JButton("Set Size");
		preferredSize.setFont(font);
		frame.add(preferredSize);
		preferredSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				String text = JOptionPane.showInputDialog("Bitte gib deine gew¸nschte Grˆﬂe an!");
				try {
					int hotkeysiz = Integer.parseInt(text);
					hotkeysave = hotkeysiz;
					haschanged = true;
				} catch (NumberFormatException e) {
					hotkeysave = 5;
					haschanged = true;
				}

			}

		});

		orderChange.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				createAskWindow();
				showFiles();

			}

		});

		JLabel instruct = new JLabel("Choose Sound Folder:", SwingConstants.CENTER);
		instruct.setFont(instructfont);
		frame.add(instruct);

		frame.add(b);
		JLabel csgolabel = new JLabel("Choose CSGO Folder", SwingConstants.CENTER);
		csgolabel.setFont(instructfont);
		frame.add(csgolabel);
		JButton csgofolderbutton = new JButton("Open File");
		csgofolderbutton.setFont(font);
		csgofolderbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				returnval = fc.showOpenDialog(b);
				if (returnval == JFileChooser.APPROVE_OPTION) {
					csgomainfolder = Paths.get(fc.getSelectedFile().getPath());
				}
			}

		});

		JLabel chooserandom = new JLabel("Choose Random Folder (Optional)");
		chooserandom.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));

		JButton chooserandomb = new JButton("Open File");
		chooserandomb.setFont(font);
		chooserandomb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				returnval = fc.showOpenDialog(b);
				if (returnval == JFileChooser.APPROVE_OPTION) {
					randomsong = Paths.get(fc.getSelectedFile().toString());
					createRandomSubfolders();
					if (windowlistenerthread != null) {
						windowlistenerthread.interrupt();
					}
					createRandomWatcher();
				}

			}

		});

		frame.add(csgofolderbutton);
		frame.add(chooserandom);
		frame.add(chooserandomb);
		JButton saveknopf = new JButton("SAVE");
		saveknopf.setFont(font);

		frame.add(saveknopf);
		exitknopf.setFont(heading);
		frame.add(exitknopf);

		// Registers all action listeners

		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				returnval = fc.showOpenDialog(b);
				if (returnval == JFileChooser.APPROVE_OPTION) {
					selectedfile = fc.getSelectedFile();
					createSubFolders();
					updateLabels();
					if (soundlistenerthread != null) {
						soundlistenerthread.interrupt();
					}
					createSoundWatcher();
				}
			}

		});

		addActionListeners();

		saveknopf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				boolean hotkeyorderempty = false;
				StringBuilder order = new StringBuilder();
				StringBuilder temp = new StringBuilder();

				if (!hotkeyorder.isEmpty()) {
					hotkeyorder.forEach(i -> {
						order.append(String.valueOf(i)).append(",");
					});
					temp.append(order.substring(0, order.length() - 1));
				}

				else {
					hotkeyorderempty = true;
				}

				StringBuilder text = new StringBuilder();
				if (haschanged) {
					text.append(hotkeysave + newline);
					order.setLength(0);
					hotkeyorderempty = true;
				} else {
					text.append(hotkeysize + newline);
				}

				text.append(selectedfile.toString() + newline);
				text.append(csgomainfolder.toString() + newline);
				text.append(randomsong.toString() + newline);
				System.out.println("Der Ordner heiﬂt: " + csgomainfolder.toString());

				if (hotkeyorderempty) {
					text.append("" + newline);
				} else {
					text.append(temp.toString() + newline);
				}

				if (!haschanged) {
					for (int i = 0; i < hotkeylist.size(); i++) {
						text.append(String.valueOf(i + 1) + ":" + hotkeylist.get(i).getText() + ";" + newline);
					}
				} else {
					if (hotkeysave > hotkeylist.size()) {
						for (int i = 0; i < hotkeylist.size(); i++) {
							text.append(String.valueOf(i + 1) + ":" + hotkeylist.get(i).getText() + ";" + newline);
						}
						for (int i = hotkeylist.size(); i <= hotkeysave; i++) {
							text.append(String.valueOf(i + 1) + ":NULL;" + newline);
						}

					} else {
						for (int i = 0; i < hotkeysave; i++) {
							text.append(String.valueOf(i + 1) + ":" + hotkeylist.get(i).getText() + ";" + newline);
						}
					}
				}

				writeToHotkey(text.toString());

				hotkeybuttonstrings.clear();

				for (JButton j : hotkeylist) {
					hotkeybuttonstrings.add(j.getText());
				}

				statusanzeige.setText("Erfolgreich!");

				// if (!(selectedfile.toString() == "")) {
				// StringBuilder firstbunch = new StringBuilder();
				// for (int i = 0; i < wavfilenames.size(); i++) {
				// firstbunch.append(
				// "IF /i \"%wantedkey%\"==\"" + i + "\" GOTO " +
				// wavfilenames.get(i).trim() + newline);
				// }
				//
				// StringBuilder secondbunch = new StringBuilder();
				// String maincsgofolder = selectedfile.toString().substring(0,
				// selectedfile.toString().lastIndexOf('\\'));
				// for (int i = 0; i < wavfilenames.size(); i++) {
				// secondbunch.append(":" + wavfilenames.get(i).trim() +
				// newline + "XCOPY \""
				// + wavfilenamesfull.get(i) + "\\" + "voice_input.wav\" \"" +
				// maincsgofolder + "\" /Y"
				// + newline + "GOTO end" + newline);
				// }
				// String batchtext = "@echo off" + newline + "SET wantedkey=%1"
				// + newline + firstbunch.toString()
				// + "GOTO end" + newline + secondbunch.toString() + ":end" +
				// newline + "exit 0";
				//
				// writeToBatch(batchtext);
				// actionsucceful = true;
				// }
				//
				// else {
				// actionsucceful = false;
				// }
				//
				// if (actionsucceful) {
				// statusanzeige.setText("Erfolgreich!");
				// } else {
				// statusanzeige.setText("Sound Path ausw‰hlen!");
				// }
				// try {
				// Thread.sleep(500);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }

			}

		});

		exitknopf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				shutDown();
			}

		});

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// registering the keylistener hook

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			JOptionPane.showMessageDialog(null, "Konnte Native Hook nicht laden", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new GlobalKeyListenerDemo());

		// Get the logger for "org.jnativehook" and set the level to off.

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the default logger.

		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}

		// "Main Loop" Sleeps 1 s and then repeats. Waits for Shutdown via
		// System.exit(0)

		while (!closewanted) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Make sure that everything is closed after leaving Main Loop

		shutDown();

	}

	// Write a specific String to hotkeys.txt

	private static void createAskWindow() {

		JFrame askframe = new JFrame("Enter new Hotkey Order");
		askframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		askframe.setSize(90 * hotkeysize, 200);
		askframe.setLayout(new GridLayout(0, hotkeysize, 5, 5));
		nf = NumberFormat.getIntegerInstance();
		nf.setMaximumIntegerDigits(3);
		List<JFormattedTextField> textfields = new ArrayList<>();

		for (int i = 0; i < hotkeysize; i++) {
			JFormattedTextField t = new JFormattedTextField(nf);
			t.setHorizontalAlignment(SwingConstants.CENTER);
			t.setText(String.valueOf(hotkeyorder.get(i)));
			textfields.add(t);
			JLabel l = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
			l.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			l.setFont(heading);
			askframe.add(l);
		}

		textfields.forEach(textf -> askframe.add(textf));

		JButton save2 = new JButton("Save");
		save2.setFont(font);
		askframe.add(save2);
		JLabel status = new JLabel("", SwingConstants.CENTER);
		status.setFont(font);
		askframe.add(status);
		save2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				List<String> getts = new ArrayList<>();
				textfields.forEach(t -> {
					getts.add(t.getText());
				});

				noundernull = true;
				nodouble = true;

				getts.forEach(getp -> {
					int num = Integer.parseInt(getp);
					if (getp.equals("")) {
						noundernull = false;
					} else if (num <= 0 || num > wavfilenames.size()) {
						noundernull = false;
					}
				});

				for (int f = 0; f < getts.size(); f++) {
					for (int m = 0; m < getts.size(); m++) {
						if (f != m && getts.get(m).equals(getts.get(f))) {
							nodouble = false;
						}
					}
				}

				if (noundernull && nodouble) {
					hotkeyorder.clear();
					getts.forEach(texxt -> hotkeyorder.add(Integer.parseInt(texxt.trim())));
					askframe.dispose();
					files.dispose();
					updateLabels();
				} else {
					status.setText("Ungueltig!");
				}
			}

		});
		askframe.setLocationRelativeTo(null);
		askframe.setVisible(true);

	}

	private static void openPrompt() {
		frame2 = new JFrame("Input for new Hotkey");
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setLayout(new GridLayout(0, 1, 5, 5));
		frame2.setFont(font);
		frame2.setSize(200, 200);
		JLabel text = new JLabel("Please press your liked Key now!", SwingConstants.CENTER);
		text.setFont(font);
		frame2.add(text);
		pressedbutton.setFont(heading);
		frame2.add(pressedbutton);
		waitforinput = true;
		frame2.pack();
		frame2.setResizable(false);
		frame2.setLocationRelativeTo(null);
		frame2.setVisible(true);
	}

	private static void writeToHotkey(String s) {
		try (Writer w = Files.newBufferedWriter(savefile)) {
			w.write(s);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Konnte nicht auf Hotkeys schreiben", "Error",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	// private static void writeToBatch(String s) {
	// try (Writer w = Files.newBufferedWriter(batchfile)) {
	// w.write(s);
	// } catch (IOException e) {
	// statusanzeige.setText("Fehler!");
	// e.printStackTrace();
	// }
	//
	// }

	protected static void createSubFolders() {
		// String[] directories = selectedfile.list(new FilenameFilter() {
		// @Override
		// public boolean accept(File current, String name) {
		// return new File(current, name).isDirectory();
		// }
		// });
		//
		// wavfilenames.clear();
		// wavfilenamesfull.clear();
		// wavfilenames.addAll(Arrays.asList(directories));
		// System.out.println(wavfilenames);
		//
		// // List<String> foldernames = new ArrayList<>();
		//
		// wavfilenames.forEach(s -> {
		// wavfilenamesfull.add(selectedfile.toString() + "\\" + s);
		// });
		//
		String[] wavfiles = selectedfile.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".wav");
			}
		});
		wavfilenames.clear();
		wavfilenamesfull.clear();
		List<String> longnames = new ArrayList<>(Arrays.asList(wavfiles));
		for (int i = 0; i < longnames.size(); i++) {
			String temp = longnames.get(i);
			longnames.set(i, temp.substring(0, temp.lastIndexOf('.')));
		}
		wavfilenames.addAll(longnames);
		wavfilenames.forEach(s -> {
			wavfilenamesfull.add(selectedfile.toString() + "\\" + s);
		});

	}

	protected static void createRandomSubfolders() {
		String[] directories = randomsong.toFile().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".wav");
			}
		});

		randomwavfilenames.clear();
		randomwavfilenamesfull.clear();
		List<String> longnames = new ArrayList<>(Arrays.asList(directories));
		for (int i = 0; i < longnames.size(); i++) {
			String temp = longnames.get(i);
			longnames.set(i, temp.substring(0, temp.lastIndexOf('.')));
		}

		randomwavfilenames.addAll(longnames);

		randomwavfilenames.forEach(s -> {
			randomwavfilenamesfull.add(randomsong.toString() + "\\" + s);
		});

		hasrandom = true;
		statusanzeige.setText(randomwavfilenames.get(currentsong));

	}

	// Method to unregister everything, make sure that there are no background
	// processes running

	private static void shutDown() {
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		System.out.println("Programm wird beendet");
		System.runFinalization();
		System.exit(0);
	}

	private static void replaceText(JButton hotkeyp, int hotkeynum) {
		if (!hotkeyp.getText().equals("DISABLED")) {
			hotkeyp.setText("DISABLED");
		} else {
			hotkeyp.setText(hotkeybuttonstrings.get(hotkeynum));
		}
	}

	private static void showFiles() {
		files = new JFrame("List of all Files.");
		files.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if (wavfilenames.size() != 0) {
			files.setLayout(new GridLayout(0, 4, 5, 5));
			JLabel numbas;
			JLabel texxt;
			for (int i = 0; i < wavfilenames.size(); i++) {
				numbas = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
				numbas.setFont(heading);
				numbas.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				files.add(numbas);
				texxt = new JLabel(wavfilenames.get(i), SwingConstants.CENTER);
				texxt.setFont(font);
				texxt.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				files.add(texxt);
			}

			files.setSize(new Dimension(650, 50 + 30 * (wavfilenames.size() / 2)));

		}

		else {
			files.setLayout(new GridLayout(0, 1, 5, 5));
			JLabel fail = new JLabel("No File selected!", SwingConstants.CENTER);
			fail.setFont(font);
			files.add(fail);
			files.setSize(new Dimension(300, 100));
		}

		files.setLocationRelativeTo(null);
		files.setVisible(true);
	}

	public static void addActionListeners() {

		for (int i = 0; i < hotkeylist.size(); i++) {
			JButton temp = hotkeylist.get(i);
			final int temp2 = i;
			temp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!disabletrue) {
						currentlywaiting = temp2 + 1;
						openPrompt();
					} else {
						replaceText(temp, temp2);
					}
				}
			});
		}
	}

	public static void updateLabels() {

		if (!wavfilenames.isEmpty()) {
			if (!(namelabels.size() > wavfilenames.size())) {
				for (int i = 0; i < namelabels.size(); i++) {
					namelabels.get(i).setText(wavfilenames.get(hotkeyorder.get(i) - 1));
				}
			} else {
				for (int i = 0; i < wavfilenames.size(); i++) {
					namelabels.get(i).setText(wavfilenames.get(hotkeyorder.get(i) - 1));
				}
			}
		}
	}

	private static void createRandomWatcher() {
		if (watcher != null) {
			try {
				randomsong.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			windowlistenerthread = new WindowListenerThread(watcher, 1);
			windowlistenerthread.start();
		}
	}

	private static void createSoundWatcher() {
		Path p = Paths.get(selectedfile.toURI());
		try {
			p.register(watcher2, ENTRY_CREATE, ENTRY_DELETE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		soundlistenerthread = new WindowListenerThread(watcher2, 2);
		soundlistenerthread.start();
	}

	protected static void processFiles(List<File> l) {
		ArrayList<File> tempFiles = new ArrayList<>();
		for (File f : l) {
			if (f.getName().toLowerCase().endsWith("mp3")) {
				tempFiles.add(f);
			}
		}
		;
		if (tempFiles.isEmpty())
			return;

		if (convertpath == null) {
			JLabel msgLabel = new JLabel(
					"<html>MP3 File Detected! Ready for Conversion.<br>  But first you have to choose a folder to place the converted File! Do you want to choose one now?</html>",
					SwingConstants.CENTER);
			int result = JOptionPane.showConfirmDialog(null, msgLabel, "Chose Folder now?", JOptionPane.YES_NO_OPTION);
			if(!(result == JOptionPane.YES_OPTION)){
				return;
			}
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnval = chooser.showOpenDialog(null);
			if(returnval == JFileChooser.APPROVE_OPTION){
				convertpath = chooser.getSelectedFile().toPath();
			}
			else{
				return;
			}
		}

		
		
	}

	public static File getSelectedfile() {
		return selectedfile;
	}

	public static List<Integer> getHotkeyorder() {
		return hotkeyorder;
	}

	public static boolean isRandomizedmode() {
		return randomizedmode;
	}

}