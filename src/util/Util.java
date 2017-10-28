package util;

import java.io.File;
import java.io.IOException;

import wav.WavFile;
import wav.WavFileException;

public class Util {
	
	public static double[] imgArray(double[] buffer) {
		double array[] = new double[buffer.length];
		for (int i = 0; i < buffer.length; i++) {
			array[i] = 0;
		}
		return array;
	}
	
	public static double getMin(String fileName) {
		
		
		double min = Double.MAX_VALUE;
		

		WavFile wavFile;
		try {
			wavFile = WavFile.openWavFile(new File(fileName));
			int framesRead = 0;
			int numChannels = wavFile.getNumChannels();
			double[] buffer = new double[100 * numChannels];
			do {
				framesRead = wavFile.readFrames(buffer, 100);
				for (int s=0 ; s<framesRead * numChannels ; s++)
				{
					if (buffer[s] < min) min = buffer[s];
				}

			} while (framesRead != 0);
			
			wavFile.close();
			return min;
			
		} catch (IOException | WavFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		return min;
	}
	
public static double getMax(String fileName) {
		
		
		double max = Double.MIN_VALUE;
		

		WavFile wavFile;
		try {
			wavFile = WavFile.openWavFile(new File(fileName));
			int framesRead = 0;
			int numChannels = wavFile.getNumChannels();
			double[] buffer = new double[100 * numChannels];
			do {
				framesRead = wavFile.readFrames(buffer, 100);
				for (int s=0 ; s<framesRead * numChannels ; s++)
				{
					if (buffer[s] > max) max = buffer[s];
				}

			} while (framesRead != 0);
			
			wavFile.close();
			return max;
			
		} catch (IOException | WavFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		return max;
	}
	
	public static double[] readWindow(String fileName, int redniBroj, int miliseconds) {
		try {
			WavFile wavFile = WavFile.openWavFile(new File(fileName));
			int samplingRate = (int) wavFile.getSampleRate();
			int numberOfChannels = wavFile.getNumChannels();
			
			int requiredWindowSize = (int)((samplingRate / (1000.0/miliseconds)));
			System.out.println("Samp/R: " + samplingRate);
			System.out.println("win size: " + (1000.0/miliseconds));
			System.out.println(requiredWindowSize * numberOfChannels);
			
			double buffer[] = new double[requiredWindowSize * numberOfChannels];
			
			for (int i = 0; i < redniBroj; i++) {
				wavFile.readFrames(buffer, requiredWindowSize);
			}
			wavFile.close();
			return buffer;
			
		} catch (Exception e) {
			System.out.println("Ne radi citanje prozora.");
			e.printStackTrace();
			return null;
		}
	}
	
	public static double getMinScore(double[] arr, int n) {
		double min = Double.MAX_VALUE;
		for (int i = 1; i < n; i++) {
			if (arr[i] < min) min = arr[i];
		}
		return min;
	}
	
	public static double getMaxScore(double[] arr, int n) {
		double max = Double.MAX_VALUE;
		for (int i = 1; i < n; i++) {
			if (arr[i] > max) max = arr[i];
		}
		return max;
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
