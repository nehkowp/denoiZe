/**
 * @file ResultatMoyCov.java
 * @brief Classe représentant le résultat du calcul du vecteur moyen et de la matrice de covariance.
 */

package model.acp;

import model.base.Matrice;
import model.base.Vecteur;
import model.patch.ResultatVecteur;

/**
 * @class ResultatMoyCov
 * @brief Contient les résultats d'un calcul de vecteur moyen et de matrice de covariance.
 * @author Emma
 */
public class ResultatMoyCov {

    //le vecteur moyen des données
    private Vecteur mV;
    //la matrice de covariance
    private Matrice gamma;
    //les résultats liés au centrage des vecteurs
    private ResultatVecteur vc;

    /**
     * @brief Constructeur du résultat moyen-covariance.
     * @author Emma
     * @param mV Le vecteur moyen calculé.
     * @param gamma La matrice de covariance calculée.
     * @param vc Les résultats liés au centrage des vecteurs.
     */
    public ResultatMoyCov(Vecteur mV, Matrice gamma, ResultatVecteur vc) {
        this.gamma = gamma;
        this.mV = mV;
        this.vc = vc;
    }

    /**
     * @brief Accède au vecteur moyen.
     * @author Emma
     * @return Le vecteur moyen des données.
     */
    public Vecteur getVecteurMoyen() {
        return mV;
    }

    /**
     * @brief Accède à la matrice de covariance.
     * @author Emma
     * @return La matrice de covariance.
     */
    public Matrice getMatriceCovariance() {
        return gamma;
    }

    /**
     * @brief Accède aux résultats liés au centrage des vecteurs.
     * @author Emma
     * @return L'objet contenant les vecteurs centrés et leurs résultats associés.
     */
    public ResultatVecteur getVecteursCenters() {
        return vc;
    }
}

