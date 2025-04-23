package model.base;

import javafx.scene.image.Image;

public class Img {
	
	private double[][] pixels;
	private Image image;
	
	public Img(double[][] pixels) {
		this.pixels = pixels;
	}
	
	public int getHauteur() {
		return (int) image.getHeight();
	}
	
	public int getLargeur() {
		return (int) image.getWidth();
	}
	
	public double getPixel(int x, int y) {
		return this.pixels[x][y];
	}
}
