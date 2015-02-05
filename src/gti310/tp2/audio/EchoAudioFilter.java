package gti310.tp2.audio;

import java.io.FileNotFoundException;

import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;

public class EchoAudioFilter implements AudioFilter {
	private FileSource _fs;
	private FileSink _echoFile;
	private int _frequency;
	private short _bytePerSample;
	private short _numChannels;
	private int _delai;
	private float _attenuation;
	
	public EchoAudioFilter(FileSource fs, int frequency, short bytePerSample, short numChannels, String ficherSortie, String delai, String attenuation) throws FileNotFoundException{
		_fs = fs;
		_frequency = frequency; 
		_bytePerSample = bytePerSample;
		_numChannels = numChannels;
		_delai = Integer.parseInt(delai);
		_delai = Integer.parseInt(delai);
		_attenuation = Float.parseFloat(attenuation);
		_echoFile = new FileSink(ficherSortie);
	}
	
	@Override
	public void process() {
		_echoFile.push(_fs.pop(44));
		byte[] test = _fs.pop(4);
		System.out.println(test.length);
		
	}
	/*private void applyEcho(ByteToWrite, EchoByte){
		
	}*/
}
