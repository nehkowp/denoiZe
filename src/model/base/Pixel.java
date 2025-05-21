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
    private double[] valeurs;
    
	//true si l'image le pixel en couleur , false si byte_gray 
    private boolean estRGB;
    
    
    //Nombre de fois que ce pixel est chevauché
    private int nbChevauchement;

    /**
     * @brief Constructeur du pixel avec une valeur initiale pour GRAYSCALE
     * @author Paul
     * @param valeur Valeur numérique du pixel.
     */
    public Pixel(double valeur) {
        this.valeurs = new double[1];
        this.valeurs[0] = valeur;
        this.estRGB = false;
        this.nbChevauchement = 0;
    }
    
    /**
     * @brief Constructeur du pixel avec une valeur initiale pour RGB
     * @author Paul
     * @param r Valeur numérique du pixel du rouge.
     * @param g Valeur numérique du pixel du vert.
     * @param b Valeur numérique du pixel du bleu.
     */
    
    public Pixel(double r, double g, double b) {
        this.valeurs = new double[3];
    	this.estRGB = true;
        this.valeurs[0] = r ;
        this.valeurs[1] = g ;
        this.valeurs[2] = b ;
        this.estRGB = true;
        this.nbChevauchement = 0;
    }
    
    
    
    /**
     * @brief Constructeur du pixel avec une valeur initiale pour RGB
     * @author Paul
     * @param r Valeur numérique du pixel du rouge.
     * @param g Valeur numérique du pixel du vert.
     * @param b Valeur numérique du pixel du bleu.
     */
    
    public Pixel(double[] rgb) {
        this.valeurs = new double[3];
    	this.estRGB = true;
        this.valeurs[0] = rgb[0] ;
        this.valeurs[1] = rgb[1] ;
        this.valeurs[2] = rgb[2] ;
        this.estRGB = true;
        this.nbChevauchement = 0;
    }
    
    

    /**
     * @brief Récupère les valeurs du pixel RGB.
     * @author Paul
     * @return Valeur numérique du pixel.
     */
    public double[] getValeurs() {
        return valeurs;
    }
    
    /**
     * @brief Récupère la valeur du pixel.
     * @author Paul
     * @return Valeur numérique du pixel.
     */
    public double getValeur() {
        return valeurs[0];
    }

    /**
     * @brief Récupère la valeur du pixel RDB.
     * @author Paul
     * @return Valeur numérique du pixel.
     */
    public double getValeur(int k ) {
        return valeurs[k];
    }

    
    /**
     * @brief Modifie la valeur du pixel.
     * @author Paul
     * @param valeur Nouvelle valeur numérique à affecter.
     */
    public void setValeur(double valeur) {
        this.valeurs[0] = valeur;
    }
    
    

    /**
     * @brief Modifie la valeur du pixel RGB pour les doubles.
     * @author Paul
     * @param valeur Nouvelle valeur numérique à affecter.
     */
   
    public void setValeurs(double[] valeurs) {
        this.valeurs[0] = valeurs[0];
        this.valeurs[1] = valeurs[1];
        this.valeurs[2] = valeurs[2];
    }
    
    /**
     * @brief Modifie la valeur du pixel RGB pour les entiers.
     * @author Paul
     * @param valeur Nouvelle valeur numérique à affecter.
     */
   
    public void setValeurs(int[] valeurs) {
        this.valeurs[0] = valeurs[0];
        this.valeurs[1] = valeurs[1];
        this.valeurs[2] = valeurs[2];
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
    
    public boolean estRGB() {
    	return this.estRGB;
    }

}
