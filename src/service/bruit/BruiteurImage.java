package service.bruit;

import java.util.Random;
import model.base.Img;

public class BruiteurImage {
		
	
	
	public static Img noising(Img x0, double sigma) {
		Random r = new Random();
		double xB_pixels[][] = new double[x0.getHauteur()][x0.getLargeur()];
		for (int i = 0; i<x0.getHauteur();i++) {
			for (int j = 0; j<x0.getLargeur();j++) {
				xB_pixels[i][j] = x0.getPixel(i, j) + r.nextGaussian()*sigma;
			}
		}
		Img xB = new Img(xB_pixels);
		
		
		return xB;
	}

}
