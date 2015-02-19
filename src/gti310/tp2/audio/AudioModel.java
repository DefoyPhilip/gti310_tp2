/**
 * Date: 19 Février 2015
 * Description: Classe servant de modèle pour le fichier sonore. Les 
 * différente méthode servent principalement à traité ou recevoir facilement
 * les différentes méthode du header 
 * @author Stéphane Lam, Philip Defoy
 */
package gti310.tp2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioModel {
	byte[] headerByteArray;
	short numChannels, bitsPerSample;
	int sampleRate, chunkSize, subchunk1Size, subchunk2Size, byteRate;
	short audioFormat;

	public AudioModel(byte[] headerByteArray, short audioFormat, short numChannels, short bitsPerSample, int sampleRate, int chunkSize, int subchunk1Size, int subchunk2Size) {
		this.headerByteArray = headerByteArray;
		this.numChannels = numChannels;
		this.bitsPerSample = bitsPerSample;
		this.sampleRate = sampleRate;
		this.chunkSize = chunkSize;
		this.subchunk1Size = subchunk1Size;
		this.subchunk2Size = subchunk2Size;
		byte[] byteRateArr = Arrays.copyOfRange(headerByteArray, 28, 32);
		this.byteRate = ByteBuffer.wrap(byteRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
		this.audioFormat = audioFormat;
	}
	
	/**
	 * validateChunkSize()
	 * This method validates the chunksizes is defined in the header
	 */
	public boolean validateChunkSize() throws Exception{
		
		// validate if PCM format
		if (this.getChunkSize() <= 0 || this.getSubchunk1Size() <= 0 || this.getSubchunk2Size() <= 0)
			throw new Exception("Le fichier audio contient des erreurs dans l'ent�te.");
		
		return true;
	}

	/**
	 * getHeaderByteArray()
	 * Return the header
	 */
	
	public byte[] getHeaderByteArray() {
		return headerByteArray;
	}

	/**
	 * setHeaderByteArray
	 * Set the header
	 * @param headerByteArray An array representing the header in byte
	 */

	public void setHeaderByteArray(byte[] headerByteArray) {
		this.headerByteArray = headerByteArray;
	}

	/**
	 * getNumChannels
	 * return the number of chanel
	 */
	public short getNumChannels() {
		return numChannels;
	}

	/**
	 * setNumChannels
	 * set the number of channel
	 * @param numChannels the number of channel
	 */

	public void setNumChannels(short numChannels) {
		this.numChannels = numChannels;
	}

	/**
	 * getBitsPerSample
	 * return the bits per sample
	 */

	public short getBitsPerSample() {
		return bitsPerSample;
	}

	/**
	 * setBitsPerSample
	 * @param bitsPerSample the bit per sample
	 * Set the bit per sample
	 */

	public void setBitsPerSample(short bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}

	/**
	 * getSampleRate
	 * get the sample rate
	 */

	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * setSampleRate
	 * @param sampleRate the sample rate
	 */

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	/**
	 * getSubchunk1Size
	 * return the sub chunk size 1
	 */
	
	public int getSubchunk1Size() {
		return subchunk1Size;
	}


	/**
	 * getChunkSize
	 * return the chunk size
	 */

	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * setChunkSize
	 * @param chunkSize la valeur du chunck size en integer
	 * Méthode utiliser pour écrire la section chunk size du header.
	 */

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
		byte[] chunk1SizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(chunkSize).array();
		for (int i = 0; i < chunk1SizeBytes.length; i++){
			headerByteArray[4+i] = chunk1SizeBytes[i];
		}
		
	}

	/**
	 * setSubchunk1Size
	 * @param subchunk1Size la valeur du sub chunck size 1 en integer
	 * Méthode utiliser pour écrire la section subchunk size 1 du header.
	 */

	public void setSubchunk1Size(int subchunk1Size) {
		this.subchunk1Size = subchunk1Size;
		byte[] subchunk1SizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(subchunk1Size).array();
		for (int i = 0; i < subchunk1SizeBytes.length; i++){
			headerByteArray[16+i] = subchunk1SizeBytes[i];
		}
		
	}

	/**
	 * getSubchunk2Size
	 * return the sub chunk size 2
	 */

	public int getSubchunk2Size() {
		return subchunk2Size;
	}

	
	/**
	 * setSubchunk2Size
	 * @param subchunk2Size la valeur du sub chunck size 2 en integer
	 * Méthode utiliser pour écrire la section subchunk size 2 du header.
	 */
	public void setSubchunk2Size(int subchunk2Size) {
		this.subchunk2Size = subchunk2Size;
		byte[] subchunk2SizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(subchunk2Size).array();
		for (int i = 0; i < subchunk2SizeBytes.length; i++){
			headerByteArray[40+i] = subchunk2SizeBytes[i];
		}
	}

	
	public short getAudioFormat() {
		return audioFormat;
	}



	public void setAudioFormat(short audioFormat) {
		this.audioFormat = audioFormat;
	}



	public void setChunksSize(int dataSize){
		setSubchunk2Size(dataSize);
		//setSubchunk1Size(24 + dataSize);
		setChunkSize(36 + getSubchunk2Size());
	}

	
	public int getByteRate(){
		return byteRate;
	}


	@Override
	public String toString() {
		return "AudioModel [headerByteArray="
				+ Arrays.toString(headerByteArray) + ", numChannels="
				+ numChannels + ", bitsPerSample=" + bitsPerSample
				+ ", sampleRate=" + sampleRate + "]";
	}
	
	
	
	
	
}
