package main;

import java.io.File;

import wav.WavFile;

public class Test {
	public static void main(String[] args) {
		double[] window = readWindow("primer.wav", 1, 1);
		for (int i = 0; i < window.length; i++) {
			System.out.println(window[i]);
		}
		
		System.out.println("--- AFTER DFT ----");
		double[] img = imgArray(window);
		Fft.getFourier(window, img);
		for (int i = 0; i < window.length; i++) {
			System.out.println(window[i]);
		}
		
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
