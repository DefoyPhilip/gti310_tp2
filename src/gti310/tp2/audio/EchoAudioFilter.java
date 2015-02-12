package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;

public class EchoAudioFilter implements AudioFilter {
	private FileSource _fs;
	private FileSink _echoFile;
	private int _delai;
	private float _attenuation;
	
	public EchoAudioFilter(FileSource fs, String ficherSortie, String delai, String attenuation) throws FileNotFoundException{
		_fs = fs;
		_delai = Integer.parseInt(delai);
		_attenuation = Float.parseFloat(attenuation);
		_echoFile = new FileSink(ficherSortie);
		
		//Process the header
		byte[] byteArr = fs.pop(44);
		byte[] sampleRateArr = Arrays.copyOfRange(byteArr, 24, 28);
		int frequency = ByteBuffer.wrap(sampleRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		byte[] byteRateArr = Arrays.copyOfRange(byteArr, 28, 32);
		int BytePerSec = ByteBuffer.wrap(byteRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		byte[] bytePerSampleArr = Arrays.copyOfRange(byteArr, 34, 36);
		short bytePerSample = ByteBuffer.wrap(bytePerSampleArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		byte[] numChannelsArr = Arrays.copyOfRange(byteArr, 22, 24);
		short numChannels = ByteBuffer.wrap(numChannelsArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		System.out.println("frequency:"+frequency);
		System.out.println("BytePerSec:"+BytePerSec);
		System.out.println("BytePerMiliSec:"+BytePerSec/1000);
		System.out.println("bytePerSample:"+bytePerSample);
		System.out.println("numChannels:"+numChannels);
		
		//rewrite the header	
		_echoFile.push(byteArr);

	}
	
	@Override
	public void process() {
		byte[] test =_fs.pop(88);
		int i = 0;
		
		while( i < ((_fs.fileSize() - 43)/88)){
			i++;
			_echoFile.push(test);
			test = _fs.pop(88);
		}
		System.out.println("end:"+i);
		
	}
	/*private void applyEcho(ByteToWrite, EchoByte){
		
	}*/
}
