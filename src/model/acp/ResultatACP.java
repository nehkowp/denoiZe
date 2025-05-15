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
}

