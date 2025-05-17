/**
 * @file Matrice.java
 * @brief Classe représentant une matrice de nombres réels.
 */

package model.base;

import exception.MatriceException;
import exception.VecteurException;

/**
 * @class Matrice
 * @brief Classe pour manipuler des matrices réelles.
 * @author Emma
 */
public class Matrice {

    //Valeurs numériques de la matrice
    private double[][] valeurs;
    ///Nombre de lignes de la matrice
    private int lignes;
    //Nombre de colonnes de la matrice
    private int colonnes;

    /**
     * @brief Constructeur à partir d'un tableau 2D de valeurs.
     * @author Emma
     * @param valeurs Tableau de valeurs représentant la matrice.
     */
    public Matrice(double[][] valeurs) {
        this.valeurs = valeurs;
        this.lignes = valeurs.length;
        this.colonnes = valeurs[0].length;
    }

    /**
     * @brief Constructeur d'une matrice vide donnée en dimensions.
     * @author Emma
     * @param lignes Nombre de lignes.
     * @param colonnes Nombre de colonnes.
     */
    public Matrice(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.valeurs = new double[lignes][colonnes];
    }

    /**
     * @brief Récupère la valeur d'un élément de la matrice.
     * @author Emma
     * @param ligne Indice de la ligne.
     * @param colonne Indice de la colonne.
     * @return Valeur de l'élément.
     */
    public double getValeur(int ligne, int colonne) {
        return this.valeurs[ligne][colonne];
    }

    /**
     * @brief Modifie la valeur d'un élément de la matrice.
     * @author Emma
     * @param ligne Indice de la ligne.
     * @param colonne Indice de la colonne.
     * @param valeur Nouvelle valeur à affecter.
     */
    public void setValeur(int ligne, int colonne, double valeur) {
        this.valeurs[ligne][colonne] = valeur;
    }

    /**
     * @brief Récupère le nombre de lignes de la matrice.
     * @author Emma
     * @return Nombre de lignes.
     */
    public int getNbLignes() {
        return lignes;
    }

    /**
     * @brief Récupère le nombre de colonnes de la matrice.
     * @author Emma
     * @return Nombre de colonnes.
     */
    public int getNbColonnes() {
        return colonnes;
    }
    
    /**
     * @brief Récupère la colonne.
     * @author Paul
     * @param indexC Index de la colonne
     * @return La colonne dans un tableau de double.
     */
    public double[] getColonne(int indexC) {
        double[] colonne= new double[colonnes];
    	
        for(int i = 0; i < lignes;i++) {
        	colonne[i] = valeurs[i][indexC];
        }
        
        return colonne;
    }
    
    /**
     * @brief Multiplie cette matrice par une autre matrice.
     * @author Emma
     * @param autre Matrice à multiplier.
     * @return Résultat de la multiplication matricielle.
     * @throws MatriceException Si les dimensions ne sont pas compatibles.
     */
    public Matrice multiplier(Matrice autre) {
        if (this.valeurs[0].length == autre.valeurs.length) {
            Matrice res = new Matrice(this.valeurs.length, autre.valeurs[0].length);
            double somme;
            for (int i = 0; i < this.valeurs.length; i++) {
                for (int j = 0; j < autre.valeurs[0].length; j++) {
                    somme = 0;
                    for (int k = 0; k < this.valeurs[0].length; k++) {
                        somme += this.valeurs[i][k] * autre.valeurs[k][j];
                    }
                    res.setValeur(i, j, somme);
                }
            }
            return res;
        } else {
            throw new MatriceException("Le nombre de colonnes de la première matrice doit être égal au nombre de lignes de la seconde matrice.");
        }
    }

    /**
     * @brief Multiplie cette matrice par un vecteur.
     * @author Emma
     * @param vecteur Vecteur à multiplier.
     * @return Résultat sous forme de vecteur.
     * @throws MatriceException Si les dimensions ne sont pas compatibles.
     */
    public Vecteur multiplier(Vecteur vecteur) {
        if (this.valeurs[0].length == vecteur.getValeurs().length) {
            Vecteur res = new Vecteur(this.valeurs.length);
            double somme;
            for (int i = 0; i < this.valeurs.length; i++) {
                somme = 0;
                for (int j = 0; j < this.valeurs[0].length; j++) {
                    somme += this.valeurs[i][j] * vecteur.getValeur(j);
                }
                res.setValeur(i, somme);
            }
            return res;
        } else {
            throw new MatriceException("Le nombre de colonnes de la matrice doit être égal au nombre d'éléments du vecteur.");
        }
    }

    /**
     * @brief Transpose la matrice.
     * @author Emma
     * @return Nouvelle matrice transposée.
     */
    public Matrice transposer() {
        Matrice res = new Matrice(this.colonnes, this.lignes);
        for (int i = 0; i < this.lignes; i++) {
            for (int j = 0; j < this.colonnes; j++) {
                res.setValeur(j, i, this.getValeur(i, j));
            }
        }
        return res;
    }

    /**
     * @brief Additionne cette matrice avec une autre.
     * @author Emma
     * @param autre Matrice à additionner.
     * @return Résultat de l'addition.
     * @throws VecteurException Si les matrices ne sont pas de la même taille.
     */
    public Matrice ajouter(Matrice autre) {
        if (this.lignes == autre.lignes && this.colonnes == autre.colonnes) {
            Matrice res = new Matrice(this.lignes, this.colonnes);
            for (int i = 0; i < this.lignes; i++) {
                for (int j = 0; j < this.colonnes; j++) {
                    res.setValeur(i, j, this.getValeur(i, j) + autre.getValeur(i, j));
                }
            }
            return res;
        } else {
            throw new VecteurException("Les deux matrices ne sont pas de taille égale.");
        }
    }

    /**
     * @brief Multiplie la matrice par un scalaire.
     * @author Emma
     * @param scalaire Valeur à multiplier.
     * @return Nouvelle matrice résultante.
     */
    public Matrice multiplierParScalaire(double scalaire) {
        Matrice res = new Matrice(this.lignes, this.colonnes);
        for (int i = 0; i < this.lignes; i++) {
            for (int j = 0; j < this.colonnes; j++) {
                res.setValeur(i, j, this.getValeur(i, j) * scalaire);
            }
        }
        return res;
    }

    /**
     * @brief Calcule la somme des éléments de la diagonale principale.
     * @author Emma
     * @return Somme des éléments diagonaux.
     * @throws MatriceException Si la matrice n'est pas carrée.
     */
    public double SommeDiagonale() {
        if (this.lignes == this.colonnes) {
            double res = 0;
            for (int i = 0; i < this.lignes; i++) {
                res += this.getValeur(i, i);
            }
            return res;
        } else {
            throw new MatriceException("La matrice n'est pas carrée.");
        }
    }
}

