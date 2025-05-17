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
    public double seuilB(double[] alpha, double sigma) {
        double varianceBruit = sigma * sigma;

        double mean = 0.0;
        for (double v : alpha) mean += v;
        mean /= alpha.length;

        double varY = 0.0;
        for (double v : alpha) varY += (v - mean) * (v - mean);
        varY /= alpha.length;

        double varianceSignal = Math.max(varY - varianceBruit, 1e-6);

        return varianceBruit / Math.sqrt(varianceSignal);
    }

    

    /**
     * @brief Applique la fonction de seuillage dur sur un vecteur de coefficients.
     * @author Bastien
     * @param lambda Le seuil à appliquer.
     * @param alpha Le tableau des coefficients à seuiller.
     * @return Le tableau de coefficients après seuillage dur.
     */
    public double[] seuillageDur(double lambda, double[] alpha) {
        double[] resultat = new double[alpha.length];

        for (int i = 0; i < alpha.length; i++) {
            if (Math.abs(alpha[i]) <= lambda) {
                resultat[i] = 0;
            } else {
                resultat[i] = alpha[i]; 
            }
        }
        return resultat;
    }

    /**
     * @brief Applique la fonction de seuillage doux sur un vecteur de coefficients.
     * @author Bastien
     * @param lambda Le seuil à appliquer.
     * @param alpha Le tableau des coefficients à seuiller.
     * @return Le tableau de coefficients après seuillage doux.
     */
    public double[] seuillageDoux(double lambda, double[] alpha) {
        double[] resultat = new double[alpha.length];
        
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] > lambda) {
                resultat[i] = alpha[i] - lambda; 
            } else if (alpha[i] < -lambda) { 
                resultat[i] = alpha[i] + lambda; 
            } else {
                resultat[i] = 0;
            }
        }
        return resultat;
    }
    
    public ResultatVecteur seuillage(ResultatVecteur alphaProj, String typeSeuil, String fonctionSeuillage, double sigma, Img xB, Matrice gamma) {
        double lambdaGlobal = 0.0;
        boolean isBayes = false;

        if (typeSeuil.equalsIgnoreCase("VisuShrink")) {
            lambdaGlobal = seuilV(xB, sigma);
            System.out.println("💠 Seuillage VisuShrink: λ = " + String.format("%.4f", lambdaGlobal));
        } else if (typeSeuil.equalsIgnoreCase("BayesShrink")) {
            isBayes = true; // on utilisera seuilB(alpha_i, sigma) dans la boucle
            System.out.println("💠 Seuillage BayesShrink adaptatif par vecteur");
        } else {
            throw new IllegalArgumentException("Type de seuil non reconnu : " + typeSeuil);
        }

        ResultatVecteur resSeuil = new ResultatVecteur();

        for (int i = 0; i < alphaProj.taille(); i++) {
            double[] alphaValues = alphaProj.getVecteurs().get(i).getValeurs();
            double lambda = isBayes ? seuilB(alphaValues, sigma) : lambdaGlobal;

            if (fonctionSeuillage.equalsIgnoreCase("Dur")) {
                alphaValues = seuillageDur(lambda, alphaValues.clone());
            } else if (fonctionSeuillage.equalsIgnoreCase("Doux")) {
                alphaValues = seuillageDoux(lambda, alphaValues.clone());
            } else {
                throw new IllegalArgumentException("Fonction de seuillage non reconnue : " + fonctionSeuillage);
            }

            resSeuil.ajouterVecteur(new Vecteur(alphaValues), alphaProj.getPositions().get(i));
        }

        return resSeuil;
    }


   
    
}
