package service.bruit;

import java.util.Random;
import model.base.Img;
import model.base.Pixel;

public class BruiteurImage {
		
	
	
	public static Img noising(Img x0, double sigma) {
		Random r = new Random();
		Pixel xB_pixels[][] = new Pixel[x0.getHauteur()][x0.getLargeur()];
		for (int i = 0; i<x0.getHauteur();i++) {
			for (int j = 0; j<x0.getLargeur();j++) {
				xB_pixels[i][j].setValeur(x0.getPixel(i, j).getValeur() + r.nextGaussian()*sigma);
			}
		}
		Img xB = new Img(xB_pixels);
		
		
		return xB;
	}

}
