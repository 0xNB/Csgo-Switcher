package batchstarter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javazoom.jl.converter.Converter;
import javazoom.jl.decoder.JavaLayerException;

import static java.nio.file.StandardCopyOption.*;

public class MyConverter {

	private AudioInputStream audioInputStream = null;
	private String wavfilepath;
	private File f;
	private String mp3FilePath;
	private String appdatapath = System.getProperty("user.home")+"\\temporarywavconv.wav"; 
	
	public MyConverter(Path wavfilepath, Path mp3FilePath){
		this.wavfilepath = wavfilepath.toString();
		this.f = new File(this.wavfilepath);
		this.mp3FilePath = mp3FilePath.toString();
	}
	
	public MyConverter(){
		
	}

	public void convertMp3toWav(String sourcefilename, String targetfilename) {
		Converter con = new Converter();
		System.out.println(sourcefilename+" "+targetfilename);
		try {
			con.convert(sourcefilename, targetfilename);
			System.out.println("Wav File converted!");
		} catch (JavaLayerException e) {
			this.showOptionPane("Could not convert by using JLayer!");
		}
	}
	
	public void convertMp3toWav(){
		convertMp3toWav(mp3FilePath, wavfilepath);
	}

	public void compressWavFile() {

		try {
			InputStream musicfile = new BufferedInputStream(new FileInputStream(wavfilepath));
			audioInputStream = AudioSystem.getAudioInputStream(musicfile);
			System.out.println(audioInputStream.getFormat());
			AudioFormat format = audioInputStream.getFormat();
			AudioFormat outDataFormat = new AudioFormat((float) 22050.0, (int) 16, (int) 1, true, false);
			if (AudioSystem.isConversionSupported(outDataFormat, format)) {
				System.out.println("Conversion Supported!");
				AudioInputStream lowResAis = AudioSystem.getAudioInputStream(outDataFormat, audioInputStream);
				System.out.println(lowResAis.getFormat().toString());
				writeToSoundFile(new File(appdatapath),lowResAis);
			}
			System.out.println("low res created!");
		} catch (UnsupportedAudioFileException e) {
			this.showOptionPane("Audio File not Supported!");
			try {
				audioInputStream.close();
			} catch (IOException e1) {
				this.showOptionPane("Could not close Audio Stream on catch block! Fatal Error!!!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setWavFilePath(Path filepath){
		this.wavfilepath = filepath.toString();
	}

	public void setMp3FilePath(Path givenpath){
		this.mp3FilePath = givenpath.toString();
	}
	
	public void writeToSoundFile(File out,AudioInputStream lowResAis) {
		if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, lowResAis)) {
			try {
//				if(lowResAis.markSupported()){
//					lowResAis.reset();
//				}
				AudioSystem.write(lowResAis, AudioFileFormat.Type.WAVE, out);
				System.out.println("New wav file written!");
				lowResAis.close();
				audioInputStream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				this.showOptionPane("Could not write to new Audio File!");
			}
			finally{
				try {
					lowResAis.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					audioInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				Files.move(Paths.get(appdatapath), Paths.get(wavfilepath), REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void showOptionPane(String msg){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, msg, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			
		});
	}

}
