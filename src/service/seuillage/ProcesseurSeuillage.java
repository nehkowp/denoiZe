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
        double sigmaX = Math.max(0.001, Math.sqrt(Math.abs(sigmaXb - sigmaCarre)));
        return sigmaCarre / (double) sigmaX;
    }
    
    //@author Emma proposition de correction ???
    /*public double seuilB(Img Xb, double sigma, Matrice gamma) {
        double sigmaXb = gamma.SommeDiagonale() / gamma.getNbLignes();  // Variance moyenne des coefficients
        double sigmaCarre = sigma * sigma;
        
        // S'assurer que la variance du signal est positive
        double sigmaX = Math.max(0.001, Math.sqrt(Math.max(0, sigmaXb - sigmaCarre)));
        
        return sigmaCarre / sigmaX;
    }*/

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
    
//    public ResultatVecteur seuillage(ResultatVecteur alphaProj, String typeSeuil, String fonctionSeuillage, double sigma, Img xB, Matrice gamma) {
//        double lambda = 0.0;
//
//        // Calcul du seuil selon le type
//        if (typeSeuil.equalsIgnoreCase("VisuShrink")) {
//            lambda = seuilV(xB, sigma);
//        } else if (typeSeuil.equalsIgnoreCase("BayesShrink")) {
//            lambda = seuilB(xB, sigma, gamma);
//        } else {
//            throw new IllegalArgumentException("Type de seuil non reconnu : " + typeSeuil);
//        }
//
//        // Appliquer le seuillage sur tous les vecteurs alpha
//        ResultatVecteur resSeuil = new ResultatVecteur();
//
//        for (int i = 0; i < alphaProj.taille(); i++) {
//            double[] alphaValues = alphaProj.getVecteurs().get(i).getValeurs();
//
//            // Appliquer la fonction de seuillage choisie
//            if (fonctionSeuillage.equalsIgnoreCase("Dur")) {
//                alphaValues = seuillageDur(lambda, alphaValues.clone()); // clone pour ne pas modifier l'original
//            } else if (fonctionSeuillage.equalsIgnoreCase("Doux")) {
//                alphaValues = seuillageDoux(lambda, alphaValues.clone());
//            } else {
//                throw new IllegalArgumentException("Fonction de seuillage non reconnue : " + fonctionSeuillage);
//            }
//
//            // Ajout du vecteur seuillé dans le résultat
//            resSeuil.ajouterVecteur(new Vecteur(alphaValues), alphaProj.getPositions().get(i));
//        }
//
//        return resSeuil;
//    }

    public ResultatVecteur seuillage(ResultatVecteur alphaProj, String typeSeuil, String fonctionSeuillage, double sigma, Img xB, Matrice gamma) {
        double lambda = 0.0;

        // Calcul du seuil selon le type
        if (typeSeuil.equalsIgnoreCase("VisuShrink")) {
            lambda = seuilV(xB, sigma);
            System.out.println("Seuillage VisuShrink: lambda = " + lambda);
        } else if (typeSeuil.equalsIgnoreCase("BayesShrink")) {
            lambda = seuilB(xB, sigma, gamma);
            System.out.println("Seuillage BayesShrink: lambda = " + lambda);
            
            // Diagnostic pour BayesShrink
            double sigmaXb = gamma.SommeDiagonale();
            System.out.println("  Diagnostics BayesShrink:");
            System.out.println("  - Somme diagonale gamma: " + sigmaXb);
            System.out.println("  - sigma²: " + (sigma * sigma));
            System.out.println("  - sigmaX estimé: " + Math.max(0.001, Math.sqrt(Math.abs(sigmaXb - (sigma * sigma)))));
        } else {
            throw new IllegalArgumentException("Type de seuil non reconnu : " + typeSeuil);
        }

        // Statistiques sur les vecteurs
        if (!alphaProj.getVecteurs().isEmpty()) {
            double[] premierVecteur = alphaProj.getVecteurs().get(0).getValeurs();
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double sum = 0;
            
            for (double val : premierVecteur) {
                min = Math.min(min, val);
                max = Math.max(max, val);
                sum += Math.abs(val);
            }
            
            System.out.println("Statistiques sur le premier vecteur avant seuillage:");
            System.out.println("  - Min: " + min);
            System.out.println("  - Max: " + max);
            System.out.println("  - Moyenne abs: " + (sum / premierVecteur.length));
        }

        // Appliquer le seuillage sur tous les vecteurs alpha
        ResultatVecteur resSeuil = new ResultatVecteur();
        int totalCoefs = 0;
        int coefsAnnules = 0;

        System.out.println("POSITIONS DEBUG - seuillage:");
        
        
        for (int i = 0; i < alphaProj.taille(); i++) {
            double[] alphaValues = alphaProj.getVecteurs().get(i).getValeurs();
            totalCoefs += alphaValues.length;

            // Appliquer la fonction de seuillage choisie
            double[] seuilValues;
            if (fonctionSeuillage.equalsIgnoreCase("Dur")) {
                seuilValues = seuillageDur(lambda, alphaValues.clone());
            } else if (fonctionSeuillage.equalsIgnoreCase("Doux")) {
                seuilValues = seuillageDoux(lambda, alphaValues.clone());
            } else {
                throw new IllegalArgumentException("Fonction de seuillage non reconnue : " + fonctionSeuillage);
            }
            
            // Compter les coefficients annulés
            for (double val : seuilValues) {
                if (val == 0) coefsAnnules++;
            }
            
            if (i % 250 == 0) {
                if (alphaProj.getPositions().get(i) != null) {
                    System.out.println("Position alphaProj[" + i + "]: (" + alphaProj.getPositions().get(i).getI() + "," + alphaProj.getPositions().get(i).getJ() + ")");
                } else {
                    System.out.println("Position alphaProj[" + i + "]: null");
                }
            }
            

            // Ajout du vecteur seuillé dans le résultat
            resSeuil.ajouterVecteur(new Vecteur(seuilValues), alphaProj.getPositions().get(i));
        }
        
        
        double pourcentageAnnule = (double)coefsAnnules / totalCoefs * 100.0;
        System.out.println("Résultat du seuillage " + fonctionSeuillage + ":");
        System.out.println("  - Coefficients annulés: " + coefsAnnules + "/" + totalCoefs + " (" + String.format("%.2f", pourcentageAnnule) + "%)");

        return resSeuil;
    }
    
}
