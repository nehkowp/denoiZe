/**
 * @file Position.java
 * @brief Classe représentant une position dans une matrice ou une image, via ses coordonnées.
 */

package model.base;

/**
 * @class Position
 * @brief Représente une position en deux dimensions avec les coordonnées i et j.
 * @author Emma
 */
public class Position {

    //Coordonnée de la ligne
    private int i;
    //Coordonnée de la colonne
    private int j;

    /**
     * @brief Constructeur de la classe Position.
     * @author Emma
     * @param i Coordonnée de la ligne.
     * @param j Coordonnée de la colonne.
     */
    public Position(int i, int j) {
        this.i = i;
        this.j = j;
    }

    /**
     * @brief Récupère la coordonnée i.
     * @author Emma
     * @return La valeur de i.
     */
    public int getI() {
        return i;
    }

    /**
     * @brief Récupère la coordonnée j.
     * @author Emma
     * @return La valeur de j.
     */
    public int getJ() {
        return j;
    }
}
