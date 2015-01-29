package gti310.tp2;

import java.io.FileNotFoundException;

import gti310.tp2.io.FileSource;

public class Application {

	/**
	 * Launch the application
	 * @param args This parameter is ignored
	 */
	public static void main(String args[]) {
		System.out.println("Audio Resample project!");
		try {
			FileSource fs = new FileSource(args[0]);
			byte[] byteArr = fs.pop(1);
			System.out.println(byteArr[0]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
