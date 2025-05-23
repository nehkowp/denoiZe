/**
 * @file BruiteurImage.java
 * @brief Classe utilitaire pour ajouter du bruit gaussien à une image.
 */

package service.bruit;

import java.util.Random;
import model.base.Img;
import model.base.Pixel;

/**
 * @class BruiteurImage
 * @brief Fournit des méthodes pour appliquer du bruit aléatoire à une image.
 * @author Paul & Alexis
 */
public class BruiteurImage {

	/**
     * @brief Applique un bruit gaussien d'écart-type sigma sur une image.
     * @author Paul & Alexis
     * @param x0 Image d'entrée.
     * @param sigma Ecart-type du bruit gaussien à appliquer.
     * @return Une nouvelle image bruitée.
     */
    public static Img noising(Img x0, double sigma) {
        Random rand = new Random();
        Pixel[][] xB_pixels = new Pixel[x0.getHauteur()][x0.getLargeur()];
        
        if(x0.isEstRGB()) {
        	for (int i = 0; i < x0.getHauteur(); i++) {
                for (int j = 0; j < x0.getLargeur(); j++) {
                    double[] rgb = x0.getPixel(i, j).getValeurs();
                    double[] valeurBruitees = new double[3];
                    for(int k = 0; k < 3 ; k++) {
                        double bruit = rand.nextGaussian() * sigma;
                        valeurBruitees[k]= rgb[k] + bruit;
                    }

                    xB_pixels[i][j] = new Pixel(valeurBruitees[0],valeurBruitees[1],valeurBruitees[2]);
                }
            }	
        }else {
        	for (int i = 0; i < x0.getHauteur(); i++) {
                for (int j = 0; j < x0.getLargeur(); j++) {
                    double valeurOriginale = x0.getPixel(i, j).getValeur();
                    double bruit = rand.nextGaussian() * sigma;
                    double valeurBruitee = valeurOriginale + bruit;

                    xB_pixels[i][j] = new Pixel(valeurBruitee);
                }
            }	
        }
        
        return new Img(xB_pixels,x0.isEstRGB());
    }
}
