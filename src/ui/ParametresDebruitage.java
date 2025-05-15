/**
 * @file ParametresDebruitage.java
 * @brief Classe définissant les paramètres pour le débruitage d'une image.
 */

package ui;

/**
 * @class ParametresDebruitage
 * @author Emma
 * @brief Représente les paramètres utilisés pour le débruitage d'images.
 */
public class ParametresDebruitage {
    //Taille des patches
    private int taillePatch;
    //Taille des fenêtres de recherche
    private int tailleFenetre;
    //Écart-type du bruit (sigma)
    private double sigma;
    //Type de seuil utilisé dans le seuillage
    private String typeSeuil;
    //Fonction de seuillage appliquée
    private String fonctionSeuillage;
    //Indique si le mode local est activé
    private boolean modeLocal;

    /**
     * @brief Constructeur avec initialisation des paramètres.
     * @author Emma
     * @param taillePatch taille des patches
     * @param tailleFenetre taille des fenêtres de recherche
     * @param sigma écart-type du bruit
     * @param typeSeuil type de seuil utilisé
     * @param fonctionSeuillage fonction de seuillage
     * @param modeLocal activation du mode local
     */
    public ParametresDebruitage(int taillePatch, int tailleFenetre, double sigma, String typeSeuil, String fonctionSeuillage, boolean modeLocal) {
        this.fonctionSeuillage = fonctionSeuillage;
        this.modeLocal = modeLocal;
        this.sigma = sigma;
        this.tailleFenetre = tailleFenetre;
        this.taillePatch = taillePatch;
        this.typeSeuil = typeSeuil;
    }

    /**
     * @brief Retourne la taille des patches.
     * @author Emma
     * @return taille des patches
     */
    public int getTaillePatch() {
        return taillePatch;
    }

    /**
     * @brief Retourne la taille des fenêtres de recherche.
     * @author Emma
     * @return taille des fenêtres
     */
    public int getTailleFenetre() {
        return tailleFenetre;
    }

    /**
     * @brief Retourne la valeur sigma.
     * @author Emma
     * @return sigma
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * @brief Retourne le type de seuil utilisé.
     * @author Emma
     * @return type de seuil
     */
    public String getTypeSeuil() {
        return typeSeuil;
    }

    /**
     * @brief Retourne la fonction de seuillage utilisée.
     * @author Emma
     * @return fonction de seuillage
     */
    public String getFonctionSeuillage() {
        return fonctionSeuillage;
    }

    /**
     * @brief Indique si le mode local est activé.
     * @author Emma
     * @return true si mode local activé, false sinon
     */
    public boolean isModeLocal() {
        return modeLocal;
    }
}
