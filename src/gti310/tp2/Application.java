package gti310.tp2;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import gti310.tp2.audio.EchoAudioFilter;
import gti310.tp2.audio.AudioModel;
import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;

public class Application {

	/**
	 * Launch the application
	 * @param args This parameter is ignored
	 * @throws FileNotFoundException 
	 */
	public static void main(String args[]) throws FileNotFoundException {
		FileSource fsource = new FileSource(args[0]);
		FileSink fsink = new FileSink(args[1]);
		
		
		byte[] headerBytesArray = fsource.pop(44);
		byte[] sampleRateArr = Arrays.copyOfRange(headerBytesArray, 24, 28);
		byte[] nbChannelsArr = Arrays.copyOfRange(headerBytesArray, 22, 24);
		byte[] bytePerSampleArr = Arrays.copyOfRange(headerBytesArray, 34, 36);
		
		int sampleRate = ByteBuffer.wrap(sampleRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		short bitsPerSample = ByteBuffer.wrap(bytePerSampleArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		short nbChannels = ByteBuffer.wrap(nbChannelsArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		AudioModel audioModel = new AudioModel(headerBytesArray, nbChannels, bitsPerSample, sampleRate);
		fsink.push(headerBytesArray);
		
		EchoAudioFilter echoAudioFilter = new EchoAudioFilter(fsource, fsink, audioModel, Integer.parseInt(args[2]), Float.parseFloat(args[3]));
		echoAudioFilter.process();
	}
}
