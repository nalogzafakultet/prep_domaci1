package util;

public class WindowFunctions {
	
	public static double hanning(double i, int n) {
		return 0.5 * (1.0 - Math.cos((2 * Math.PI * i) / (n - 1)));
	}
	
	public static double hamming(double i, int n) {
		return 0.54 - 0.46 * Math.cos((2 * Math.PI * i) / (n - 1));
 	}
}
