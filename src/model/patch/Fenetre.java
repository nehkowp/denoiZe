/**
 * @file Fenetre.java
 * @brief Classe représentant une fenêtre d'une image à une position donnée.
 */

package model.patch;

import model.base.Img;
import model.base.Position;

/**
 * @class Fenetre
 * @brief Représente une fenêtre (imagette) associée à une position dans une image.
 * @author Emma
 */
public class Fenetre {

    //Image correspondant à la fenêtre
    private Img image;
    //Position de la fenêtre dans l'image d'origine
    private Position position;

    /**
     * @brief Constructeur de la classe Fenetre.
     * @author Emma
     * @param image L'image correspondant à la fenêtre.
     * @param position La position de la fenêtre dans l'image d'origine.
     */
    public Fenetre(Img image, Position position) {
        this.image = image;
        this.position = position;
    }

    /**
     * @brief Récupère l'image associée à la fenêtre.
     * @author Emma
     * @return L'image de la fenêtre.
     */
    public Img getImage() {
        return image;
    }

    /**
     * @brief Récupère la position de la fenêtre dans l'image d'origine.
     * @author Emma
     * @return La position de la fenêtre.
     */
    public Position getPosition() {
        return position;
    }
}

