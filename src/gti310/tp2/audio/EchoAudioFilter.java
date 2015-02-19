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
				int sampleSize = audioModel.getByteRate() / 1000;																//1
				byte[] sampleArray;																								//1
				int n = 0;																										//1
				byte[] storedSampleArray = new byte[sampleSize];																//1
				byte[][] sampleBuffer = new byte[delay][sampleSize];															//1
				int sampleBufferHead = 0;																						//1
				int bytePerSample = audioModel.getBitsPerSample()/8;															//1
				
				audioModel.setChunksSize(audioModel.getSubchunk2Size() 															//1
						+ audioModel.getSampleRate() 
						* bytePerSample 
						* audioModel.getNumChannels() 
						* Math.abs(delay) / 1000);	
				
				fsink.push(audioModel.getHeaderByteArray());																	//1
				float correctionFactor = 1 / (1 + Math.abs(attenuation));
				
				while (n <= (audioModel.getSubchunk2Size())/sampleSize) {														//N 
					sampleArray = fsource.pop(sampleSize);																		//N
					storedSampleArray = sampleArray.clone();																	//1
					if (n >= delay) {																							//N
						for (int j = 0; j < sampleSize; j = j + bytePerSample) {												//N
							short currentSample;																				//N
							short echo;																							//N
							
							// for 8 bits
							if(bytePerSample == 1){																				//N
								currentSample = this.getSample(sampleArray[j]);											//N
								echo = this.getSample(sampleBuffer[sampleBufferHead][j]);								//N
								short resultSampleValue = (short) ((currentSample + echo * Math.abs(attenuation)) * correctionFactor);							//N
								sampleArray[j] = (byte) (resultSampleValue);													//N
							}	
							
							// for 16 bits
							else{																								//N
								currentSample = (short) this.getSample(sampleArray[j],sampleArray[j+1]);						//N
								echo = (short) this.getSample(sampleBuffer[sampleBufferHead][j],sampleBuffer[sampleBufferHead][j+1]);	//N
								short resultSampleValue = (short) (currentSample + echo * Math.abs(attenuation));							//N
								byte[] tempSampleArray = ByteBuffer.allocate(2).putShort(resultSampleValue).array();			//N
								sampleArray[j] = tempSampleArray[0];
								sampleArray[j+1] = tempSampleArray[0];
							}
							
							
						}
					}
					sampleBuffer[sampleBufferHead] = storedSampleArray;															//N
					sampleBufferHead++;																							//N
					if(sampleBufferHead == delay){																				//N
						sampleBufferHead = 0;																					//N
					}
					n++;																										//N
					fsink.push(sampleArray);																					//N
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
	
	
	private short getSample(byte sampleByte){
		short sample = 0;
		sample = (short) (sampleByte & 0xFF);
		return sample;
	}
	
	
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
