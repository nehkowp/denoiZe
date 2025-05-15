/**
 * @file ResultatQualite.java
 * @brief Classe représentantle resultat de la qualite d'une image.
 */
package model.qualite;

/**
 * @class ResultatQualite
 * @brief Représente le résultat des mesures de qualité d'une image.
 * @author Emma
 */
public class ResultatQualite {
    //Erreur quadratique moyenne (Mean Squared Error)
    private double mse;
    //Rapport signal sur bruit par pic (Peak Signal-to-Noise Ratio)
    private double psnr;

    /**
     * @brief Constructeur du résultat de qualité.
     * @author Emma
     * @param mse  Erreur quadratique moyenne.
     * @param psnr Rapport signal sur bruit par pic.
     */
    public ResultatQualite(double mse, double psnr) {
        this.mse = mse;
        this.psnr = psnr;
    }

    /**
     * @brief Retourne l'erreur quadratique moyenne (MSE).
     * @author Emma
     * @return Valeur du MSE.
     */
    public double getMSE() {
        return mse;
    }

    /**
     * @brief Retourne le rapport signal sur bruit par pic (PSNR).
     * @author Emma
     * @return Valeur du PSNR.
     */
    public double getPSNR() {
        return psnr;
    }
}

