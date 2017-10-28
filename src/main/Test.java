package main;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import wav.WavFile;
import wav.WavFileException;

public class Test {
	public static void main(String[] args) throws IOException, WavFileException {
//		PrintWriter pw = new PrintWriter(new File("output.log"));
//		WavFile wav = WavFile.openWavFile(new File("primer.wav"));
//		int numOfChannels = wav.getNumChannels();
//		int sampleRate = (int) wav.getSampleRate();
//		int total_chunks = (int) wav.getNumFrames();
//		int windowSize = 5;
//		int samples_per_window = (sampleRate / 1000 * windowSize * numOfChannels); 
//		
//		int total_windows = (int) Math.ceil((double) total_chunks / (sampleRate / 1000 * windowSize));
//		double[] buffer = new double[samples_per_window];
//		for (int i = 0; i < total_windows; i++) {
//			buffer = Util.readWindow("primer.wav", i, 5);
//		}
//		pw.close();
		
		Point p = new Point(5, 7);
		System.out.println(p);
		
//		System.out.println("Num of channels: " + numOfChannels);
//		System.out.println("Sample Rate: " + sampleRate);
//		System.out.println("Total Chunks: " + total_chunks);
//		System.out.println("Total windows: " + total_windows);
//		System.out.println("Buffer len: " + buffer.length);
	}
	
	public static double[] imgArray(double[] buffer) {
		double array[] = new double[buffer.length];
		for (int i = 0; i < buffer.length; i++) {
			array[i] = 0;
		}
		return array;
	}
	
	public static double[] readWindow(String fileName, int redniBroj, int miliseconds) {
		try {
			WavFile wavFile = WavFile.openWavFile(new File(fileName));
			int samplingRate = (int) wavFile.getSampleRate();
			int numberOfChannels = wavFile.getNumChannels();
			
			int requiredWindowSize = (int) Math.ceil((samplingRate * 1.0 / (1000/miliseconds)));
			
			double buffer[] = new double[requiredWindowSize * numberOfChannels];
			
			for (int i = 0; i < redniBroj; i++) {
				wavFile.readFrames(buffer, requiredWindowSize);
			}
			return buffer;
			
		} catch (Exception e) {
			System.out.println("Ne radi citanje prozora.");
			e.printStackTrace();
			return null;
		}
		
		
	}
}
