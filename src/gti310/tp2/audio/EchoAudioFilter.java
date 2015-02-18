package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
			System.out.println(sampleSize);
			byte[] sampleArray;
			boolean finishedProcessing = false;
			int n = 0;
			byte[] storedSampleArray = new byte[sampleSize];
			byte[][] sampleBuffer = new byte[delay][sampleSize];
			int sampleBufferHead = 0;
			audioModel.setChunksSize(audioModel.getSubchunk2Size() + audioModel.getSampleRate() * delay / 1000);
			int bytePerSample = audioModel.getBitsPerSample()/8;
			System.out.println(bytePerSample);
			
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
					for (int j = 0; j < sampleSize; j = j + bytePerSample) {
						short currentSample;
						short echo;
						if(bytePerSample == 1){
							currentSample = this.getSample(sampleArray[j]);
							echo = this.getSample(sampleBuffer[sampleBufferHead][j]);
						}	
						else{
							currentSample = this.getSample(sampleArray[j],sampleArray[j+1]);
							echo = this.getSample(sampleBuffer[sampleBufferHead][j],sampleBuffer[sampleBufferHead][j+1]);
						}
						short resultSampleValue = (short) (currentSample + echo * attenuation);
						sampleArray[j] = (byte) (resultSampleValue);
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
	private short getSample(byte sampleByte){
		short sample = 0;
		sample = (short) (sampleByte & 0xFF);
		return sample;
	}
	private short getSample(byte sampleByte, byte secondSampleByte){
		short sample = 0;
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(sampleByte);
		bb.put(secondSampleByte);
		sample = bb.getShort(0);
		return sample;
	}

}
