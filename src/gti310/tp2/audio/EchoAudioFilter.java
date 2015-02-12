package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Map;

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
			// for testing purposes, we assume that the audio file is 8 bits mono with 44.1k sample rate

			
			/* getting samples */
			int sampleSize = audioModel.getBitsPerSample() / 8;
			byte[] sampleArray;
			boolean finishedReading = false;
			int n = 0;
			int modifiedSampleIndex, modifiedSampleSignalValue;
			LinkedList<LinkedList<Integer>> modificationsList = new LinkedList<LinkedList<Integer>>();
			

			while (!finishedReading) {
				
				// TODO : check what kind of array/list/collection is the most efficient for this kind of use
				sampleArray = fsource.pop(sampleSize);
				
				// check if there are still any bytes left to read
				// TODO : consider the echo that persists after the end of the file
				if (sampleArray[0] == 0){
					finishedReading = true;
					break;
				}

				
				int sampleSignalValue = sampleArray[0];
				
				
				LinkedList<Integer> modification = new LinkedList<Integer>();
				modifiedSampleIndex = n + (audioModel.getSampleRate() * delay / 1000);
				modifiedSampleSignalValue = sampleSignalValue; // no attenuation for now
				modification.push(modifiedSampleIndex);
				modification.push(modifiedSampleSignalValue);
				modificationsList.push(modification);
				
				// check if this sample needs to be modified
				if (modificationsList.size() > 0) { // performance ?
					LinkedList<Integer> nextModification = modificationsList.getLast();
					if (nextModification.getLast() == n) {
						
						nextModification.removeLast();
						Byte newByteValue = nextModification.removeLast().byteValue();
						//System.out.println(sampleArray[0] + " => " + newByteValue);
						sampleArray[0] = (byte) (newByteValue + sampleArray[0]);
						modificationsList.removeLast();
					}
					
				}

				fsink.push(sampleArray);
				
				

				
				
				n++;
				
				
			}
			System.out.println(n);
			
			fsink.close();
			
			
		}
		
	}
	
	private boolean validateData(){
		// what validation should we be doing?
		return true;
	}

}
