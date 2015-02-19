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
		
		try {
			if (validateData()){
				// for testing purposes, we assume that the audio file is 8 bits

				
				/* getting samples */
				int sampleSize = audioModel.getBitsPerSample() / 8;
				byte[] sampleArray;
				boolean finishedProcessing = false;
				int n = 0;
				int modificationSampleIndex, modificationSampleSignalValue;
				LinkedList<LinkedList<Integer>> modificationsList = new LinkedList<LinkedList<Integer>>();
				
				audioModel.setChunksSize(audioModel.getSubchunk2Size() + audioModel.getSampleRate() * delay / 1000);
				
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		
	}
	
	
	
	private boolean validateData() throws Exception{
		
		
		// validate if PCM format
		if (audioModel.getAudioFormat() != 1)
			throw new Exception("Ce filtre ne peut être appliqué que sur les fichiers au format PCM (sans compression).");
		
		// validate bits per sample 8 bits or 16 bits per sample
		if (audioModel.getBitsPerSample() != 8 && audioModel.getBitsPerSample() != 16)
			throw new Exception("Ce filtre ne peut être appliqué que sur les fichiers audios de 8 ou 16 bits par échantillon.");
		
		// validate sample rate (44.1k or 8k)
		if (audioModel.getSampleRate() != 44100 && audioModel.getSampleRate() != 8000)
			throw new Exception("Ce filtre ne peut être appliqué que sur les fichiers audios dont le taux d'échantillonnage est de 44.1kHz ou de 8kHz.");
		
		
		
		return true;
	}

}
