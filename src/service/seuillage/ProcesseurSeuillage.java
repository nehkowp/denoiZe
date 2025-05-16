/**
 * @file ProcesseurSeuillage.java
 * @brief Classe gérant les opérations de seuillage utilisées pour la réduction de bruit dans les images.
 */

package service.seuillage;

import model.base.Img;
import model.base.Matrice;
import model.base.Vecteur;
import model.patch.ResultatVecteur;

/**
 * @class ProcesseurSeuillage
 * @brief Fournit différentes méthodes pour calculer des seuils et appliquer des fonctions de seuillage.
 * @author Bastien
 */
public class ProcesseurSeuillage {

    /**
     * @brief Calcule le seuil global basé sur le bruit pour une image (seuil V).
     * @author Bastien
     * @param Xb L'image bruitée.
     * @param sigma L'écart-type du bruit.
     * @return La valeur du seuil.
     */
    public double seuilV(Img Xb , double sigma) {
        int h = Xb.getHauteur();
        int l = Xb.getLargeur();
        return sigma * Math.sqrt(2 * Math.log(h * l));
    }

    /**
     * @brief Calcule un seuil basé sur la variance de l'image et du bruit (seuil B).
     * @author Bastien
     * @param Xb L'image bruitée.
     * @param sigma L'écart-type du bruit.
     * @param gamma Matrice contenant des informations sur la covariance des coefficients.
     * @return La valeur du seuil calculée.
     */
    public double seuilB(Img Xb , double sigma, Matrice gamma) {
        double sigmaXb = gamma.SommeDiagonale();  //on recupere la variance de l'image Xb sans tout recalculé
        double sigmaCarre = sigma * sigma;
        double sigmaXbCarre = sigmaXb * sigmaXb;
        double sigmaX = Math.sqrt(Math.abs(sigmaXbCarre - sigmaCarre));
        return sigmaCarre / (double) sigmaX;
    }

    /**
     * @brief Applique la fonction de seuillage dur sur un vecteur de coefficients.
     * @author Bastien
     * @param lambda Le seuil à appliquer.
     * @param alpha Le tableau des coefficients à seuiller.
     * @return Le tableau de coefficients après seuillage dur.
     */
    public double[] seuillageDur(double lambda, double[] alpha) {
        for (int i = 0; i < alpha.length; i++) {
            if (Math.abs(alpha[i]) <= lambda) {
                alpha[i] = 0;
            }
        }
        return alpha;
    }

    /**
     * @brief Applique la fonction de seuillage doux sur un vecteur de coefficients.
     * @author Bastien
     * @param lambda Le seuil à appliquer.
     * @param alpha Le tableau des coefficients à seuiller.
     * @return Le tableau de coefficients après seuillage doux.
     */
    public double[] seuillageDoux(double lambda, double[] alpha) {
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] > lambda) {
                alpha[i] -= lambda;
            } else if (alpha[i] <= -lambda) {
                alpha[i] += lambda;
            } else {
                alpha[i] = 0;
            }
        }
        return alpha;
    }
    
    public ResultatVecteur seuillage(ResultatVecteur alphaProj, String typeSeuil, String fonctionSeuillage, double sigma, Img xB, Matrice gamma) {
        double lambda = 0.0;

        // Calcul du seuil selon le type
        if (typeSeuil.equalsIgnoreCase("VisuShrink")) {
            lambda = seuilV(xB, sigma);
        } else if (typeSeuil.equalsIgnoreCase("BayesShrink")) {
            lambda = seuilB(xB, sigma, gamma);
        } else {
            throw new IllegalArgumentException("Type de seuil non reconnu : " + typeSeuil);
        }

        // Appliquer le seuillage sur tous les vecteurs alpha
        ResultatVecteur resSeuil = new ResultatVecteur();

        for (int i = 0; i < alphaProj.taille(); i++) {
            double[] alphaValues = alphaProj.getVecteurs().get(i).getValeurs();

            // Appliquer la fonction de seuillage choisie
            if (fonctionSeuillage.equalsIgnoreCase("Dur")) {
                alphaValues = seuillageDur(lambda, alphaValues.clone()); // clone pour ne pas modifier l'original
            } else if (fonctionSeuillage.equalsIgnoreCase("Doux")) {
                alphaValues = seuillageDoux(lambda, alphaValues.clone());
            } else {
                throw new IllegalArgumentException("Fonction de seuillage non reconnue : " + fonctionSeuillage);
            }

            // Ajout du vecteur seuillé dans le résultat
            resSeuil.ajouterVecteur(new Vecteur(alphaValues), alphaProj.getPositions().get(i));
        }

        return resSeuil;
    }

}
