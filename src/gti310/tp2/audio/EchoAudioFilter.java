package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;

import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;



public class EchoAudioFilter implements AudioFilter {
	
	FileSource fsource;
	FileSink fsink;
	AudioModel audioModel;
	int delay;
	float attenuation;
	

	public EchoAudioFilter(FileSource fsource, FileSink fsink, AudioModel audioModel, int delay, float attenuation){
		
		this.fsource = fsource;
		this.fsink = fsink;
		this.audioModel = audioModel;
		this.delay = delay;
		this.attenuation = attenuation;
	}
	
	@Override
	/* y[n] = x[n] + a*x[n-M] 
	*
	* n : current sample
	* x : original signal
	* M : delay (ms)
	* a : attenuation factor
	* y : output signal
	* 
	*/
	public void process() {
		if (validateData()){
			// for testing purposes, we assume that the audio file is 8 bits

			
			/* getting samples */
			int sampleSize = audioModel.getByteRate() / 1000;
			byte[] sampleArray;
			boolean finishedProcessing = false;
			int n = 0;
			byte[] storedSampleArray = new byte[sampleSize];
			byte[][] sampleBuffer = new byte[delay][sampleSize];
			int sampleBufferHead = 0;
			audioModel.setChunksSize(audioModel.getSubchunk2Size() + audioModel.getSampleRate() * delay / 1000);
			
			fsink.push(audioModel.getHeaderByteArray());
			while (!finishedProcessing) {
				sampleArray = fsource.pop(sampleSize);
				storedSampleArray = sampleArray.clone();
				if (n >= audioModel.getSubchunk2Size()){
					finishedProcessing = true;
					break;
				}
				if (n < delay) {
					sampleBuffer[sampleBufferHead] = storedSampleArray;
					sampleBufferHead++;
					if(sampleBufferHead == delay){
						sampleBufferHead = 0;
					}
				}
				else{
					for (int j = 0; j < sampleSize; j++) {
						byte echoSampleByte = sampleBuffer[sampleBufferHead][j];
						short currentSampleShort = (short) (sampleArray[j] & 0xFF); // 0xFF converts to unsigned for arithmetic operations
						short echoShort = (short) (echoSampleByte & 0xFF);
						short resultSampleShortValue = (short) (currentSampleShort + echoShort * attenuation);
						sampleArray[j] = (byte) (resultSampleShortValue);
					}
					sampleBuffer[sampleBufferHead] = storedSampleArray;
					sampleBufferHead++;
					if(sampleBufferHead == delay){
						sampleBufferHead = 0;
					}
				}
				n++;
				fsink.push(sampleArray);
			}
			
			System.out.println(n + " samples");
			fsink.close();
		}
		
	}
	
	private boolean validateData(){
		// what validation should we be doing?
		return true;
	}

}
