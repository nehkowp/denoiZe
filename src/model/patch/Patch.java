package model.patch;

import model.base.Pixel;

public class Patch {
	private Pixel[][] pixels;
	private int taille;
	
	public Patch(Pixel[][] pixels) {
		this.taille = pixels.length;
		this.pixels = pixels;
	}

	public Pixel[][] getPixels() {
		return pixels;
	}

	public int getTaille() {
		return taille;
	}
		
}
