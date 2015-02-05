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
		byte[] byteArr = fs.pop(44);
		byte[] sampleRateArr = Arrays.copyOfRange(byteArr, 24, 28);
		int frequency = ByteBuffer.wrap(sampleRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		byte[] bytePerSampleArr = Arrays.copyOfRange(byteArr, 34, 36);
		short bytePerSample = ByteBuffer.wrap(bytePerSampleArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		byte[] numChannelsArr = Arrays.copyOfRange(byteArr, 22, 24);
		short numChannels = ByteBuffer.wrap(numChannelsArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		EchoAudioFilter EAF = new EchoAudioFilter(fs,frequency,bytePerSample,numChannels,args[1],args[2],args[3]);
		EAF.process();
	}
}
