package gti310.tp2.audio;

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
			int sampleSize = audioModel.getBitsPerSample() / 8;
			byte[] sampleArray;
			boolean finishedProcessing = false;
			int n = 0;
			int modificationSampleIndex, modificationSampleSignalValue;
			LinkedList<LinkedList<Integer>> modificationsList = new LinkedList<LinkedList<Integer>>();
			
			audioModel.setDataSize(audioModel.getSubchunk2Size() + audioModel.getSampleRate() * delay / 1000);
			
			fsink.push(audioModel.getHeaderByteArray());

			while (!finishedProcessing) {
				
				sampleArray = fsource.pop(sampleSize);
				int sampleSignalValue = sampleArray[0];
				
				// check if there are still any bytes left to read
				// TODO : consider the echo that persists after the end of the file => modify Subchunk2Size in the header
				if (sampleSignalValue == 0 && modificationsList.size() == 0){
					finishedProcessing = true;
					break;
				}

				
				if (sampleSignalValue != 0){
					LinkedList<Integer> modification = new LinkedList<Integer>();
					modificationSampleIndex = n + (audioModel.getSampleRate() * delay / 1000);
					modificationSampleSignalValue = sampleSignalValue;
					modification.push(modificationSampleIndex);
					modification.push(modificationSampleSignalValue);
					modificationsList.push(modification);
				}
				
				// check if this sample needs to be modified
				if (modificationsList.size() > 0) { // performance ?
					LinkedList<Integer> nextModification = modificationsList.getLast();
					if (nextModification.getLast() == n) {
						
						nextModification.removeLast();
						Byte echoSampleByte = nextModification.removeLast().byteValue();
						short currentSampleShort = (short) (sampleSignalValue & 0xFF); // 0xFF converts to unsigned for arithmetic operations
						short echoShort = (short) (echoSampleByte & 0xFF);
						short resultSampleShortValue = (short) (currentSampleShort + echoShort * attenuation);
						

						sampleArray[0] = (byte) (resultSampleShortValue);
						modificationsList.removeLast();
					}
					
				}

				fsink.push(sampleArray);
				
				n++;
				
				
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
