package gti310.tp2.audio;

import java.util.Arrays;

public class AudioModel {
	byte[] headerByteArray;
	short numChannels, bitsPerSample;
	int sampleRate;
	
	public AudioModel(byte[] headerByteArray, short numChannels, short bitsPerSample, int sampleRate) {
		this.headerByteArray = headerByteArray;
		this.numChannels = numChannels;
		this.bitsPerSample = bitsPerSample;
		this.sampleRate = sampleRate;
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



	@Override
	public String toString() {
		return "AudioModel [headerByteArray="
				+ Arrays.toString(headerByteArray) + ", numChannels="
				+ numChannels + ", bitsPerSample=" + bitsPerSample
				+ ", sampleRate=" + sampleRate + "]";
	}
	
	
	
	
	
}
