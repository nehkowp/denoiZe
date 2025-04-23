package service.bruit;

import java.io.IOException;
import java.util.Random;
import model.base.Img;

public class BruiteurImage {
		
	public static void main(String[] args) {		
		String[] imageNames = {
			    "ali_gray.jpg",
			    "crocodilo_gray.jpg",
			    "darkvador_gray.jpg",
			    "gekko_gray.jpg",
			    "harrypotter_gray.png",
			    "leclerc_gray.png",
			    "lena_gray.png",
			    "mbappe_gray.jpg",
			    "moto_gray.jpeg",
			    "nyancat_gray.png",
			    "steve_gray.jpg",
			    "wemby_gray.png"
			};
		
		for(String imageName : imageNames) {
			Img x0 = null;
			try {
				x0 = new Img("data/x0/"+imageName);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			for(double sigma=10; sigma < 40; sigma+=10) {
				Img xB = noising(x0,sigma);
								
				try {
					xB.saveImg("data/xB/"+(int) sigma+"/"+imageName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Traitement de l'image "+ imageName + " terminé");
		}
			
			
		return;
	}
	
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
