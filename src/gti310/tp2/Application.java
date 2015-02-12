package gti310.tp2;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import gti310.tp2.audio.EchoAudioFilter;
import gti310.tp2.io.FileSource;

public class Application {

	/**
	 * Launch the application
	 * @param args This parameter is ignored
	 * @throws FileNotFoundException 
	 */
	public static void main(String args[]) throws FileNotFoundException {
		System.out.println("Audio Resample project!");
		FileSource fs = new FileSource(args[0]);
		EchoAudioFilter EAF = new EchoAudioFilter(fs,args[1],args[2],args[3]);
		EAF.process();
	}
}
