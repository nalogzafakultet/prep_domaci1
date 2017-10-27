package util;

import java.io.File;

import wav.WavFile;

public class Util {
	
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
	
	public static double[] scaleAmps(double[] arr) {
		double[] copy = new double[arr.length];
		double maxVal = Double.MAX_VALUE;
		double minVal = Double.MIN_VALUE;
		
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] < minVal) {
				minVal = arr[i];
			}
			if (arr[i] > maxVal) {
				maxVal = arr[i];
			}
		}
		
		System.out.println(maxVal + ", " + minVal);
		
		double delta = maxVal - minVal;
		System.out.println("DELTA: " + delta);
		// diff : delta = x : 100
		// x = diff * 100 / delta
		
		for (int i = 0; i < arr.length; i++) {
			copy[i] = (((arr[i] - minVal) * 100.0) / delta);
		}
		
		return copy;
	}

}
