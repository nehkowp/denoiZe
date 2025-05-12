package model.base;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Img {
	
	private int largeur;
	private int hauteur;
	private double[][] pixels;
	
	public Img(String filename) throws IOException {
		BufferedImage image = ImageIO.read(new File(filename));
		this.hauteur = image.getHeight();
		this.largeur = image.getWidth();
		this.pixels = new double[hauteur][largeur];
		
		WritableRaster raster = image.getRaster();
		for(int i = 0; i < hauteur; i++) {
			for(int j = 0; j < largeur; j++) {
				pixels[i][j] = raster.getSampleDouble(j, i, 0);
			}	
		}
	}
	
	public Img(double generatedPixels[][]){
		this.hauteur = generatedPixels.length;
	    this.largeur = generatedPixels[0].length;
		this.pixels = new double[hauteur][largeur];
	
		for (int i = 0; i <  this.hauteur; i++) {
			for (int j = 0; j <  this.largeur; j++) {
				this.pixels[i][j] = generatedPixels[i][j];
			}
		}
	}
	
	public int getHauteur() {
		return this.hauteur;
	}
	
	public int getLargeur() {
		return this.largeur;
	}
	
	public double[][] getPixels() {
		return this.pixels;
	}
	
	public double getPixel(int x, int y) {
		return this.pixels[x][y];
	}
	
	public void setPixel(int x, int y, double valeur) {
		this.pixels[x][y] = valeur;
	}
	
	public Img clone() {
		return new Img(this.getPixels());
	}
	
	public void saveImg(String filename) throws IOException {
		BufferedImage image = new BufferedImage(this.largeur, this.hauteur, BufferedImage.TYPE_BYTE_GRAY);

		 for (int i = 0; i <  this.hauteur; i++) {
	            for (int j = 0; j <  this.largeur; j++) {
	            	int pixelValue = (int) Math.min(255, Math.max(0, Math.round(this.getPixel(i, j))));
	            	image.getRaster().setSample(j, i , 0, pixelValue);
	            }
	        }
		
		String format = filename.substring(filename.lastIndexOf('.') + 1);
        ImageIO.write(image, format, new File(filename));
	}
}
