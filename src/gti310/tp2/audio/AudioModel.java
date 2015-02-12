package gti310.tp2.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioModel {
	byte[] headerByteArray;
	short numChannels, bitsPerSample;
	int sampleRate, chunkSize, subchunk1Size, subchunk2Size, byteRate, testo;
	
	public AudioModel(byte[] headerByteArray, short numChannels, short bitsPerSample, int sampleRate, int chunksize, int subchunk1Size, int subchunk2Size) {
		this.headerByteArray = headerByteArray;
		this.numChannels = numChannels;
		this.bitsPerSample = bitsPerSample;
		this.sampleRate = sampleRate;
		this.subchunk2Size = subchunk2Size;
		byte[] byteRateArr = Arrays.copyOfRange(headerByteArray, 28, 32);
		this.byteRate = ByteBuffer.wrap(byteRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	
	
	public byte[] getHeaderByteArray() {
		return headerByteArray;
	}



	public void setHeaderByteArray(byte[] headerByteArray) {
		this.headerByteArray = headerByteArray;
	}



	public short getNumChannels() {
		return numChannels;
	}



	public void setNumChannels(short numChannels) {
		this.numChannels = numChannels;
	}



	public short getBitsPerSample() {
		return bitsPerSample;
	}



	public void setBitsPerSample(short bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
	}



	public int getSampleRate() {
		return sampleRate;
	}



	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	
	public int getSubchunk1Size() {
		return subchunk1Size;
	}


	

	public int getChunkSize() {
		return chunkSize;
	}



	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
		byte[] chunk1SizeBytes = ByteBuffer.allocate(4).putInt(chunkSize).array();
		for (int i = 0; i < chunk1SizeBytes.length; i++){
			headerByteArray[4+i] = chunk1SizeBytes[i];
		}
		
		System.out.println(getChunkSize());
	}



	public void setSubchunk1Size(int subchunk1Size) {
		this.subchunk1Size = subchunk1Size;
		byte[] subchunk1SizeBytes = ByteBuffer.allocate(4).putInt(subchunk1Size).array();
		for (int i = 0; i < subchunk1SizeBytes.length; i++){
			headerByteArray[16+i] = subchunk1SizeBytes[i];
		}
		
		System.out.println("new 1size = " + getSubchunk1Size());
	}



	public int getSubchunk2Size() {
		return subchunk2Size;
	}

	
	/*
	 * Also modifies the value in the header
	 */
	public void setSubchunk2Size(int subchunk2Size) {
		this.subchunk2Size = subchunk2Size;
		byte[] subchunk2SizeBytes = ByteBuffer.allocate(4).putInt(subchunk2Size).array();
		for (int i = 0; i < subchunk2SizeBytes.length; i++){
			headerByteArray[40+i] = subchunk2SizeBytes[i];
		}
	}
	
	
	public void setDataSize(int dataSize){
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
