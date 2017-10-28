package new_hmwrk;

public class Pair {
	double frequency;
	double magnitude;
	
	public Pair(double frequency, double magnitude) {
		this.frequency = frequency;
		this.magnitude = magnitude;
	}
	
	public int toColor(double min, double max) {
		return (int) ((magnitude * 255 - min) / (max - min));
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.frequency + "";
	}
}
