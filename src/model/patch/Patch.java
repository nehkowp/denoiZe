package model.patch;

public class Patch {
	private double[][] pixels;
	private int taille;
	
	public Patch(double[][] pixels) {
		this.taille = pixels.length;
		this.pixels = pixels;
	}

	public double[][] getPixels() {
		return pixels;
	}

	public int getTaille() {
		return taille;
	}
		
}
