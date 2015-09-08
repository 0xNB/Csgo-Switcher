package batchstarter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JOptionPane;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import static batchstarter.CsgoSounds.*;

public class GlobalKeyListenerDemo implements NativeKeyListener {
	;

	private static boolean randomEnabled = false;

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {

		// Change hotkey String on main window from hotkeysetup window

		if (waitforinput) {
			lastpressed = NativeKeyEvent.getKeyText(e.getKeyCode());
			waitforinput = false;
			pressedbutton.setText(lastpressed);
			int realNumber;
			if (currentlywaiting > 0) {
				realNumber = currentlywaiting - 1;
				hotkeylist.get(realNumber).setText(lastpressed);
				hotkeybuttonstrings.set(realNumber, lastpressed);
			} else {
				JOptionPane.showMessageDialog(null, "Schwerer Fehler beim Zuordnen von 'currentlywaiting'", "Error",
						JOptionPane.ERROR_MESSAGE);
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			CsgoSounds.frame2.dispose();
			pressedbutton.setText("Waiting for Input!");
		}

		else {
			String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
			int size = wavfilenames.size();
			int randomsize = randomwavfilenames.size();
			boolean b = size != 0;
			boolean b2 = randomsize != 0;

			if (b || b2) {
				if(b){
				for (int i = 0; i < hotkeybuttonstrings.size() - 1; i++) {
					String s = hotkeybuttonstrings.get(i);
					if (s.equals(keyText)) {
						moveFiles(getHotkeyorder().get(i) - 1);
						break;
					}
					}
				}

				String w = hotkeybuttonstrings.get(hotkeybuttonstrings.size() - 1);
				
				if (w.equals(keyText)) {
					int random = (int) (Math.round(Math.random() * (randomwavfilenames.size() - 1)));
					System.out.println(random);
					if(b2){
					randomEnabled = true;
					moveFiles(random);
					}
					else if (b){
						random = (int) (Math.round(Math.random() * (wavfilenames.size() - 1)));
						randomEnabled = false;
						moveFiles(random);
					}
				}
				
				if(!CsgoSounds.isRandomizedmode() && b2){
					if("Links".equals(keyText)){
						currentsong--;
						if(currentsong < 0){
							currentsong = randomwavfilenames.size()-1;
						}
						randomEnabled = true;
						moveFiles(currentsong);
						statusanzeige.setText(randomwavfilenames.get(currentsong));
					}
					else if("Rechts".equals(keyText)){
						currentsong++;
						if(currentsong >= randomwavfilenames.size()){
							currentsong = 0;
						}
						randomEnabled = true;
						moveFiles(currentsong);
						statusanzeige.setText(randomwavfilenames.get(currentsong));
					}
				}
				
			}

		}
	}

	// Open the cmd with the given number at the end for start property

	// private void openCMD(int number) {
	// try {
	// System.out.println(CsgoSounds.batchfile.toString()+"\\");
	// //, "\""+String.valueOf(number)+"\""
	// String[] commands = {"cmd.exe", "/c", "start",
	// "/min",CsgoSounds.batchfile.toString()};
	// String[] params = {String.valueOf(number)};
	// //"cmd /c start \""+CsgoSounds.batchfile.toString()+"\"
	// \""+String.valueOf(number)+"\""
	// Runtime rt = Runtime.getRuntime();
	//// ProcessBuilder pb = new ProcessBuilder(commands);
	//// pb.start();
	// System.out.println(Arrays.toString(commands));
	// Process p =rt.exec(commands);
	// Thread.sleep(250);
	// System.out.println("Prozess zertört");
	// } catch (IOException | InterruptedException e1) {
	// e1.printStackTrace();
	// }
	//
	// }

	private void moveFiles(int number) {
		try {
			 if (!csgomainfolder.toString().equals("")) {
				 if(!randomEnabled){
					Files.copy(Paths.get(wavfilenamesfull.get(number) + ".wav"),
							Paths.get(csgomainfolder.toString() + "\\voice_input.wav"), StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Ersetzen erfolgreich!");
				}

				else if(!randomsong.toString().equals("")){
						Files.copy(Paths.get(randomwavfilenamesfull.get(number) + ".wav"),
								Paths.get(csgomainfolder.toString() + "\\voice_input.wav"), StandardCopyOption.REPLACE_EXISTING);
						System.out.println("Ersetzen erfolgreich! (Random)");
						
				}
				 
				else{
					JOptionPane.showMessageDialog(null, "Select Random Folder or Music Folder first!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				 
			 }
			 
			 else {
					JOptionPane.showMessageDialog(null, "Select CSGO Main Folder Path first!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			
		randomEnabled = false;
		} catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Konnte Datei nicht kopieren!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {

	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {

	}

}
