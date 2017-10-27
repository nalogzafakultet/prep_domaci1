package main;

import util.WindowFunctions;

public class Main {
	
	public static void main(String[] args) {
//		MainFrame.getInstance();
		
		double real[] = new double[30];
		double imag[] = new double[30];
		
		for (int i = 0; i < 30; i++) {
			real[i] = Math.sin(2.0 * Math.PI * (i / 30.0));
			real[i] += Math.sin(2.0 * Math.PI * (i / 15.0));
			real[i] += Math.sin(2.0 * Math.PI * (i / 10.0));
			imag[i] = 0;
		}
		
		for (int i = 0; i < 30; i++) {
			real[i] = WindowFunctions.hamming(real[i], real.length);
			imag[i] = 0;
		}
		
		
		System.out.println("---- BEFORE FFT");
		for (int i = 0; i < 30; i++) {
			System.out.println("x: " + real[i] + ", y: " + imag[i]);
		}
		
		System.out.println("---- AFTER FFT");
		Fft.transform(real, imag);
//		Fft.getFourier(real, imag);
		
		for (int i = 0; i < 30; i++) {
			System.out.println("x: " + real[i] + ", y: " + imag[i]);
		}
		
		System.out.println();
		
	}
}
