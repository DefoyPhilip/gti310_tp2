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
		byte[] chunkSizeArr = Arrays.copyOfRange(headerBytesArray, 4, 8);
		byte[] subchunk1SizeArr = Arrays.copyOfRange(headerBytesArray, 16, 20);
		byte[] subchunk2SizeArr = Arrays.copyOfRange(headerBytesArray, 40, 44);
		
		int sampleRate = ByteBuffer.wrap(sampleRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		short bitsPerSample = ByteBuffer.wrap(bytePerSampleArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		short nbChannels = ByteBuffer.wrap(nbChannelsArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		int chunkSize = ByteBuffer.wrap(chunkSizeArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		int subchunk1Size = ByteBuffer.wrap(subchunk1SizeArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		int subchunk2Size = ByteBuffer.wrap(subchunk2SizeArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		AudioModel audioModel = new AudioModel(headerBytesArray, nbChannels, bitsPerSample, sampleRate, chunkSize, subchunk1Size, subchunk2Size);
		
		EchoAudioFilter echoAudioFilter = new EchoAudioFilter(fsource, fsink, audioModel, Integer.parseInt(args[2]), Float.parseFloat(args[3]));
		echoAudioFilter.process();
	}
}
