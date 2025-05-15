/**
 * @file Patch.java
 * @brief Classe représentant un patch d'une image.
 */

package model.patch;

import model.base.Pixel;

/**
 * @class Patch
 * @brief Représente un patch carré composé de pixels extraits d'une image.
 * @author Emma
 */
public class Patch {

    //Tableau de pixels représentant le patch
    private Pixel[][] pixels;
    //Taille du patch 
    private int taille;

    /**
     * @brief Constructeur de la classe Patch.
     * @author Emma
     * @param pixels Tableau 2D de pixels constituant le patch.
     */
    public Patch(Pixel[][] pixels) {
        this.taille = pixels.length;
        this.pixels = pixels;
    }

    /**
     * @brief Récupère les pixels du patch.
     * @author Emma
     * @return Un tableau 2D de pixels.
     */
    public Pixel[][] getPixels() {
        return pixels;
    }

    /**
     * @brief Récupère la taille du patch.
     * @author Emma
     * @return La taille du patch.
     */
    public int getTaille() {
        return taille;
    }
}
