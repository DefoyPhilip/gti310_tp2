/**
 * Date: 19 Février 2015
 * Description: Classe servant a appliquer un filtre d'echo a un fichier sonore 
 * @author Stéphane Lam, Philip Defoy
 */
package gti310.tp2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;



public class EchoAudioFilter implements AudioFilter {
	
	FileSource fsource;
	FileSink fsink;
	AudioModel audioModel;
	int delay;
	float attenuation;
	
	public static final Integer[] SUPPORTED_SAMPLE_RATES = new Integer[] {8000, 44100};
	public static final Short[] SUPPORTED_BITS_PER_SAMPLE = new Short[] {8, 16};
	public static final short MIN_DELAY = 0;
	public static final short MAX_DELAY = 10000;
	public static final float MIN_ATTENUATION = -1;
	public static final float MAX_ATTENUATION = 1;
	

	public EchoAudioFilter(FileSource fsource, FileSink fsink, AudioModel audioModel, int delay, float attenuation){
		
		this.fsource = fsource;
		this.fsink = fsink;
		this.audioModel = audioModel;
		this.delay = delay;
		this.attenuation = attenuation;
	}
	
	@Override
	/**
	* process
	* Function to call when we want to apply the echo to the file
	* 
	* y[n] = x[n] + a*x[n-M] 
	*
	* n : current sample
	* x : original signal
	* M : delay (ms)
	* a : attenuation factor
	* y : output signal
	* 
	* 
	* analyse asymptotique
	* n: longueur de bande sonore en octet
	* m: nombre d'échantillion par miliseconde (fréquence / 1000)
	* 
	* Groupe O(N)
	*/
	public void process() {

		try {
			// Return true if everything is fine
			if (validateData()){
				
				// number of sample played in one milisecond
				int sampleSize = audioModel.getByteRate() / 1000;																//1
				byte[] sampleArray;																								//1
				int n = 0;																										//1
				
				// Array that store the samples for the echo buffer
				byte[] storedSampleArray = new byte[sampleSize];																//1
				
				// Echo buffer: 2 dimensional array.
				// The first depth is de delai (in milisecond). Since we populated
				// this array with group of sample per milisecond, we can use the logic
				// "first in first out" to apply the echo.
				byte[][] sampleBuffer = new byte[delay][sampleSize];															//1
				int sampleBufferHead = 0;																						//1
				// Numbre of Byte per sample
				
				int bytePerSample = audioModel.getBitsPerSample()/8;															//1
				
				// Set the new audio file length in the header
				audioModel.setChunksSize(audioModel.getSubchunk2Size() 															//1
						+ audioModel.getSampleRate() 
						* bytePerSample 
						* audioModel.getNumChannels() 
						* Math.abs(delay) / 1000);	
				
				// Write the header in the output file
				fsink.push(audioModel.getHeaderByteArray());																	//1
				
				// Factor for a cleaner sound
				float correctionFactor = 1 / (1 + Math.abs(attenuation));														//1
				
				// Iterate through the audio file divided by the number of sample per milisecond
				while (n <= (audioModel.getSubchunk2Size())/sampleSize) {														//N / M 
					
					// read the group of sample per milisecond
					sampleArray = fsource.pop(sampleSize);																		//1
					
					// Clone the value to put original samples in the buffer
					storedSampleArray = sampleArray.clone();																	//1
					
					// if we didn't iterated a number of time bigger then the delay,
					// then we just want to store the data
					if (n >= delay) {																							//1
						
						//iterate through each sample to apply an echo to the byte
						for (int j = 0; j < sampleSize; j = j + bytePerSample) {												//M
							short currentSample;																				//1
							short echo;																							//1
							
							// for 8 bits
							if(bytePerSample == 1){																				//1
								currentSample = this.getSample(sampleArray[j]);													//1
								echo = this.getSample(sampleBuffer[sampleBufferHead][j]);										//1
								short resultSampleValue = 
										(short) ((currentSample + echo * Math.abs(attenuation)) * correctionFactor);			//1
								sampleArray[j] = (byte) (resultSampleValue);													//1
							}	
							
							// for 16 bits
							else{																								//1
								currentSample = (short) this.getSample(sampleArray[j],sampleArray[j+1]);						//1
								echo = (short) this.getSample(sampleBuffer[sampleBufferHead][j],								//1
																sampleBuffer[sampleBufferHead][j+1]);	
								short resultSampleValue = (short) (currentSample + echo * Math.abs(attenuation));				//1
								byte[] tempSampleArray = ByteBuffer.allocate(2).putShort(resultSampleValue).array();			//1
								sampleArray[j] = tempSampleArray[0];
								sampleArray[j+1] = tempSampleArray[0];
							}
							
							
						}
					}
					// store the unmodified group of sample in the buffer
					sampleBuffer[sampleBufferHead] = storedSampleArray;															//1
					sampleBufferHead++;																							//1
					if(sampleBufferHead == delay){																				//1
						sampleBufferHead = 0;																					//1
					}
					n++;																										//1
					//write the sample to the final file
					fsink.push(sampleArray);																					//1
				}
				System.out.println("Filtre appliqu�");
				fsink.close();																									//1
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);

		}

		
	}
	
	
	
	private boolean validateData() throws Exception{
		
		
		// validate if PCM format
		if (audioModel.getAudioFormat() != 1)
			throw new Exception("Ce filtre ne peut �tre appliqu� que sur les fichiers au format PCM (sans compression).");
		
		// validate bits per sample 8 bits or 16 bits per sample
		if (!Arrays.asList(SUPPORTED_BITS_PER_SAMPLE).contains(audioModel.getBitsPerSample()))
			throw new Exception("Ce filtre ne peut �tre appliqu� que sur les fichiers audios de 8 ou 16 bits par �chantillon.");
		
		// validate sample rate (44.1k or 8k)
		if (!Arrays.asList(SUPPORTED_SAMPLE_RATES).contains(audioModel.getSampleRate()))
			throw new Exception("Ce filtre ne peut �tre appliqu� que sur les fichiers audios dont le taux d'�chantillonnage est de 44.1kHz ou de 8kHz.");
		
		// validate delay value
		if (delay <= MIN_DELAY || delay > MAX_DELAY)
			throw new Exception("Veuillez entrer une valeur comprise entre 1 et 10 000 pour le d�lais.");
		
		// validate attenuation value
		if (attenuation < MIN_ATTENUATION || attenuation > MAX_ATTENUATION)
			throw new Exception("Veuillez entrer une valeur comprise entre -1 et 1 pour l'att�nuation.");
		
		return true;
	}
	/**
	 * getSample(byte sampleByte)
	 * Return the short value of the 8 bit sample
	 * @param sampleByte the byte to convert to short
	 */
	private short getSample(byte sampleByte){
		short sample = 0;
		sample = (short) (sampleByte & 0xFF);
		return sample;
	}
	
	/**
	 * getSample(byte sampleByte, byte secondSampleByte)
	 * Return the short value of the  16 bit sample
	 * @param sampleByte the first byte to convert to short
	 * @param secondSampleByte the second byte to convert to short
	 */
	private short getSample(byte sampleByte, byte secondSampleByte){
		short sample = 0;
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put(sampleByte);
		bb.put(secondSampleByte);
		sample = bb.getShort(0);
		return sample;
	}

}
