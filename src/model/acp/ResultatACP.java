/**
 * @file ResultatACP.java
 * @brief Classe représentant le résultat d'une Analyse en Composantes Principales (ACP).
 */

package model.acp;

import model.base.Matrice;
import model.base.Vecteur;

/**
 * @class ResultatACP
 * @brief Contient les résultats d'une Analyse en Composantes Principales (ACP).
 * @author Emma
 */
public class ResultatACP {

	//les valeurs propres (inerties)
    private double[] valeursPropres;
    //les vecteurs propres (axes principaux)
    private Matrice vecteursPropres;
    //le vecteur moyen des données initiales
    private Vecteur vecteurMoyen;

    /**
     * @brief Constructeur du résultat d'ACP.
     * @author Emma
     * @param valeursPropres Tableau des valeurs propres (inerties) de l'ACP.
     * @param vecteursPropres Matrice des vecteurs propres (axes principaux).
     * @param vecteurMoyen Vecteur moyen des données initiales.
     */
    public ResultatACP(double[] valeursPropres, Matrice vecteursPropres, Vecteur vecteurMoyen) {
        this.valeursPropres = valeursPropres;
        this.vecteurMoyen = vecteurMoyen;
        this.vecteursPropres = vecteursPropres;
    }

    /**
     * @brief Accède aux valeurs propres du résultat d'ACP.
     * @author Emma
     * @return Tableau des valeurs propres.
     */
    public double[] getValeursPropres() {
        return valeursPropres;
    }

    /**
     * @brief Accède aux vecteurs propres du résultat d'ACP.
     * @author Emma
     * @return Matrice des vecteurs propres.
     */
    public Matrice getVecteursPropres() {
        return vecteursPropres;
    }

    /**
     * @brief Accède au vecteur moyen des données initiales.
     * @author Emma
     * @return Le vecteur moyen.
     */
    public Vecteur getVecteurMoyen() {
        return vecteurMoyen;
    }
    
    
    /**
     * @brief Affiche une représentation lisible du résultat de l'ACP.
     * @author Paul
     * @return Une chaîne formatée contenant les résultats de l'ACP.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("======= RÉSULTAT DE L'ANALYSE EN COMPOSANTES PRINCIPALES =======\n\n");
        
        // Affichage des valeurs propres
        sb.append("-- VALEURS PROPRES (INERTIES) --\n");
        double sommeValeursPropres = 0;
        for (double vp : valeursPropres) {
            sommeValeursPropres += vp;
        }
        
        for (int i = 0; i < valeursPropres.length; i++) {
            double pourcentage = (valeursPropres[i] / sommeValeursPropres) * 100;
            sb.append(String.format("Axe %d: %.4f (%.2f%% de l'inertie totale)\n", 
                                   i + 1, valeursPropres[i], pourcentage));
        }
        sb.append(String.format("Inertie totale: %.4f\n\n", sommeValeursPropres));
        
        // Affichage du vecteur moyen
        sb.append("-- VECTEUR MOYEN --\n");
        for (int i = 0; i < vecteurMoyen.taille(); i++) {
            sb.append(String.format("Dimension %d: %.4f\n", i + 1, vecteurMoyen.getValeur(i)));
        }
        sb.append("\n");
        
        // Affichage des vecteurs propres (limité aux 3 premiers pour la lisibilité)
        sb.append("-- VECTEURS PROPRES (AXES PRINCIPAUX) --\n");
        int nbVecteursAffichés = Math.min(3, vecteursPropres.getNbLignes());
        
        for (int i = 0; i < nbVecteursAffichés; i++) {
            sb.append(String.format("Vecteur propre %d (axe principal):\n", i + 1));
            for (int j = 0; j < vecteursPropres.getNbColonnes(); j++) {
                sb.append(String.format("   Dimension %d: %.4f\n", j + 1, vecteursPropres.getValeur(i, j)));
            }
            sb.append("\n");
        }
        
        if (vecteursPropres.getNbLignes() > nbVecteursAffichés) {
            sb.append("... (affichage limité aux 3 premiers vecteurs propres)\n");
        }
        
        sb.append("=================================================================");
        
        return sb.toString();
    }
    
    
    
}

