/**
 * @file EvaluationrQualite.java
 * @brief Classe fournissant des méthodes pour évaluer la qualité d'une image reconstruite par rapport à l'originale.
 */

package service.evaluation;

import model.base.Img;

/**
 * @class EvaluationrQualite
 * @brief Contient des méthodes pour calculer des mesures de qualité d'image, telles que le MSE et le PSNR.
 * @author Bastien
 */
public class EvaluationrQualite {

    /**
     * @brief Calcule l'erreur quadratique moyenne (MSE) entre deux images.
     * @author Bastien
     * @param X0 Image originale.
     * @param Xr Image reconstruite ou bruitée.
     * @return La valeur du MSE.
     */
    public double mse(Img X0, Img Xr) {
        int nbLigne = X0.getHauteur();
        int nbColonne = X0.getLargeur();
        double mse = 0;
        
        for (int i = 0; i < nbLigne; i++) {
            for (int j = 0; j < nbColonne; j++) {
                double diff = (X0.getPixel(i, j).getValeur() - Xr.getPixel(i, j).getValeur());
                mse += (diff * diff);
            }
        }
        mse = mse / (double) (nbLigne * nbColonne);
        return mse;
    }
    
    /**
     * @brief Calcule le rapport signal-bruit par pic (PSNR) entre deux images.
     * @author Bastien
     * @param X0 Image originale.
     * @param Xr Image reconstruite ou bruitée.
     * @return La valeur du PSNR (en décibels).
     */
    public double psnr(Img X0, Img Xr) {
        double mse = mse(X0, Xr);
        double inv_mse = 255 * 255 / mse; 
        double psnr = 10 * Math.log10(inv_mse);
        return psnr;
    }
}



