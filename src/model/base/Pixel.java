/**
 * @file Pixel.java
 * @brief Classe représentant un pixel avec une valeur et un nombre de chevauchements.
 */

package model.base;

/**
 * @class Pixel
 * @brief Représente un pixel utilisé dans des calculs ou modélisations, avec gestion des chevauchements.
 * @author Paul
 */
public class Pixel {

    //Valeur numérique associée au pixel
    private double valeur;
    //Nombre de fois que ce pixel est chevauché
    private int nbChevauchement;

    /**
     * @brief Constructeur du pixel avec une valeur initiale.
     * @author Paul
     * @param valeur Valeur numérique du pixel.
     */
    public Pixel(double valeur) {
        this.valeur = valeur;
        this.nbChevauchement = 0;
    }

    /**
     * @brief Récupère la valeur du pixel.
     * @author Paul
     * @return Valeur numérique du pixel.
     */
    public double getValeur() {
        return valeur;
    }

    /**
     * @brief Modifie la valeur du pixel.
     * @author Paul
     * @param valeur Nouvelle valeur numérique à affecter.
     */
    public void setValeur(double valeur) {
        this.valeur = valeur;
    }

    /**
     * @brief Modifie le nombre de chevauchements du pixel.
     * @author Paul
     * @param nbChevauchement Nombre de chevauchements.
     */
    public void setNbChevauchement(int nbChevauchement) {
        this.nbChevauchement = nbChevauchement;
    }

    /**
     * @brief Récupère le nombre de chevauchements du pixel.
     * @author Paul
     * @return Nombre de chevauchements.
     */
    public int getNbChevauchement() {
        return nbChevauchement;
    }

}
