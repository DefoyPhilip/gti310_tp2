package gti310.tp2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


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
				int sampleSize = audioModel.getByteRate() / 1000;																//1
				byte[] sampleArray;																								//1																			//1
				int n = 0;																										//1
				byte[] storedSampleArray = new byte[sampleSize];																//1
				byte[][] sampleBuffer = new byte[delay][sampleSize];															//1
				int sampleBufferHead = 0;																						//1
				audioModel.setChunksSize(audioModel.getSubchunk2Size() + audioModel.getSampleRate() * delay / 1000);			//1
				int bytePerSample = audioModel.getBitsPerSample()/8;															//1
				fsink.push(audioModel.getHeaderByteArray());																	//1

				while (n >= (audioModel.getSubchunk2Size())/sampleSize) {														//N 
					sampleArray = fsource.pop(sampleSize);																		//N
					storedSampleArray = sampleArray.clone();																	//1
					if (n >= delay) {																							//N
						for (int j = 0; j < sampleSize; j = j + bytePerSample) {												//N
							short currentSample;																				//N
							short echo;																							//N
							if(bytePerSample == 1){																				//N
								currentSample = this.getSample(sampleArray[j]);													//N
								echo = this.getSample(sampleBuffer[sampleBufferHead][j]);										//N
							}	
							else{																								//N
								currentSample = this.getSample(sampleArray[j],sampleArray[j+1]);								//N
								echo = this.getSample(sampleBuffer[sampleBufferHead][j],sampleBuffer[sampleBufferHead][j+1]);	//N
							}
							short resultSampleValue = (short) (currentSample + echo * attenuation);								//N
							sampleArray[j] = (byte) (resultSampleValue);														//N
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
				
				System.out.println("Filtre appliqué");
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
			throw new Exception("Ce filtre ne peut être appliqué que sur les fichiers au format PCM (sans compression).");
		
		// validate bits per sample 8 bits or 16 bits per sample
		if (audioModel.getBitsPerSample() != 8 && audioModel.getBitsPerSample() != 16)
			throw new Exception("Ce filtre ne peut être appliqué que sur les fichiers audios de 8 ou 16 bits par échantillon.");
		
		// validate sample rate (44.1k or 8k)
		if (audioModel.getSampleRate() != 44100 && audioModel.getSampleRate() != 8000)
			throw new Exception("Ce filtre ne peut être appliqué que sur les fichiers audios dont le taux d'échantillonnage est de 44.1kHz ou de 8kHz.");
		
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
