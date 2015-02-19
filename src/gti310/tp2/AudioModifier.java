/**
 * Date: 19 Février 2015
 * Description: Classe servant the controlleur au logiciel.
 * S'assure que le bon nombre de paramètre sont fournis et que le fichier source est trouvable 
 * @author Stéphane Lam, Philip Defoy
 */
package gti310.tp2;

import gti310.tp2.audio.AudioModel;
import gti310.tp2.audio.EchoAudioFilter;
import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioModifier {
	/**
	 * init
	 * @param args les paramètre fournis par l'utilisateur
	 * args[0]: La source du fichier sonore
	 * args[1]: La source où écrire le nouveau fichier
	 * args[2]: La valeur de délai
	 * args[3]: La valeur d'atténuation
	 * Initialise le projet en s'assurant que les arguments sont valide et décortique le header
	 */
	
	
	public void init(String args[]){
		FileSource fsource;
		
		try {
			this.validateArguments(args);
	
			
			try {
				fsource = new FileSource(args[0]);
				FileSink fsink = new FileSink(args[1]);
				
				// Lis les info du header
				byte[] headerBytesArray = fsource.pop(44);
				
				// Place les différentes valeurs du header dans des tableau
				byte[] audioFormatArr = Arrays.copyOfRange(headerBytesArray, 20, 22);
				byte[] sampleRateArr = Arrays.copyOfRange(headerBytesArray, 24, 28);
				byte[] nbChannelsArr = Arrays.copyOfRange(headerBytesArray, 22, 24);
				byte[] bytePerSampleArr = Arrays.copyOfRange(headerBytesArray, 34, 36);
				byte[] chunkSizeArr = Arrays.copyOfRange(headerBytesArray, 4, 8);
				byte[] subchunk1SizeArr = Arrays.copyOfRange(headerBytesArray, 16, 20);
				byte[] subchunk2SizeArr = Arrays.copyOfRange(headerBytesArray, 40, 44);
				
				// Transforme les tableau d'octets en short/int
				short audioFormat = ByteBuffer.wrap(audioFormatArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
				int sampleRate = ByteBuffer.wrap(sampleRateArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				short bitsPerSample = ByteBuffer.wrap(bytePerSampleArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
				short nbChannels = ByteBuffer.wrap(nbChannelsArr).order(ByteOrder.LITTLE_ENDIAN).getShort();
				int chunkSize = ByteBuffer.wrap(chunkSizeArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				int subchunk1Size = ByteBuffer.wrap(subchunk1SizeArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				int subchunk2Size = ByteBuffer.wrap(subchunk2SizeArr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				
				
				AudioModel audioModel = new AudioModel(headerBytesArray, audioFormat, nbChannels, bitsPerSample, sampleRate, chunkSize, subchunk1Size, subchunk2Size);
				
				
				try {
					audioModel.validateChunkSize();
				} catch (Exception e) {
					System.out.println(e);
				}
				
				EchoAudioFilter echoAudioFilter = new EchoAudioFilter(fsource, fsink, audioModel, Integer.parseInt(args[2]), Float.parseFloat(args[3]));
				echoAudioFilter.process();
				
			} catch (FileNotFoundException e) {
				
				System.out.println("Le fichier sp�cifi� est introuvable ou est pr�sentement ouvert par un autre programme. Veuillez r�essayer avec un autre fichier.");
				
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		

		
	}
	
	public void validateArguments(String args[]) throws Exception {
		
		if (args.length < 4)
			throw new Exception("Veuillez sp�cifier tous les param�tres requis (fichier source, destination, d�lais, att�nuation)");
		
	}
}
