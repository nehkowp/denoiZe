/**
 * @file Vecteur.java
 * @brief Classe représentant un vecteur mathématique et ses opérations associées.
 */

package model.base;

import exception.MatriceException;
import exception.VecteurException;

/**
 * @class Vecteur
 * @brief Représente un vecteur et fournit des opérations de base.
 * @author Emma & Bastien
 */
public class Vecteur {

    //Tableau contenant les valeurs du vecteur
    private double[] valeurs;

    /**
     * @brief Constructeur de la classe Vecteur à partir d'un tableau de valeurs.
     * @author Emma
     * @param valeurs Tableau de valeurs représentant le vecteur.
     */
    public Vecteur(double[] valeurs) {
        this.valeurs = valeurs;
    }

    /**
     * @brief Constructeur de la classe Vecteur avec initialisation à 0.
     * @author Emma
     * @param taille Taille du vecteur.
     */
    public Vecteur(int taille) {
        this.valeurs = new double[taille];
    }

    /**
     * @brief Récupère la valeur à un indice donné.
     * @author Emma
     * @param index L'indice de la valeur à récupérer.
     * @return La valeur à l'indice spécifié.
     */
    public double getValeur(int index) {
        return valeurs[index];
    }

    /**
     * @brief Modifie la valeur à un indice donné.
     * @author Emma
     * @param index L'indice de la valeur à modifier.
     * @param valeur La nouvelle valeur à assigner.
     */
    public void setValeur(int index, double valeur) {
        this.valeurs[index] = valeur;
    }

    /**
     * @brief Récupère le tableau des valeurs du vecteur.
     * @author Emma
     * @return Le tableau des valeurs.
     */
    public double[] getValeurs() {
        return this.valeurs;
    }

    /**
     * @brief Récupère la taille (dimension) du vecteur.
     * @author Emma
     * @return La taille du vecteur.
     */
    public int taille() {
        return this.valeurs.length;
    }

    /**
     * @brief Effectue la soustraction de ce vecteur avec un autre.
     * @author Emma
     * @param autre Le vecteur à soustraire.
     * @return Un nouveau vecteur résultant de la soustraction.
     * @throw VecteurException Si les deux vecteurs ne sont pas de même taille.
     */
    public Vecteur soustraire(Vecteur autre) {
        if (this.taille() == autre.taille()) {
            Vecteur res = new Vecteur(this.taille());
            for(int i = 0; i < this.taille(); i++) {
                res.getValeurs()[i] = this.getValeur(i) - autre.getValeur(i);
            }
            return res;
        } else {
            throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
        }
    }

    /**
     * @brief Effectue l'addition de ce vecteur avec un autre.
     * @author Emma
     * @param autre Le vecteur à additionner.
     * @return Un nouveau vecteur résultant de l'addition.
     * @throw VecteurException Si les deux vecteurs ne sont pas de même taille.
     */
    public Vecteur ajouter(Vecteur autre) {
        if (this.taille() == autre.taille()) {
            Vecteur res = new Vecteur(this.taille());
            for(int i = 0; i < this.taille(); i++) {
                res.getValeurs()[i] = this.getValeur(i) + autre.getValeur(i);
            }
            return res;
        } else {
            throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
        }
    }

    /**
     * @brief Divise chaque élément du vecteur par un scalaire.
     * @author Emma
     * @param scalaire La valeur par laquelle diviser.
     * @return Un nouveau vecteur résultant de la division.
     * @throw VecteurException Si le scalaire est égal à 0.
     */
    public Vecteur diviser(double scalaire) {
        if (scalaire == 0) {
            throw new VecteurException("Division par 0 impossible");
        } else {
            Vecteur res = new Vecteur(this.taille());
            for(int i = 0; i < this.taille(); i++) {
                res.getValeurs()[i] = this.getValeur(i) / scalaire;
            }
            return res;
        }
    }

    /**
     * @brief Calcule le produit extérieur de ce vecteur avec un autre, sous forme de matrice.
     * @author Emma
     * @param autre Le vecteur à multiplier.
     * @return Une matrice représentant le produit extérieur des deux vecteurs.
     * @throw MatriceException Si les deux vecteurs ne sont pas de même taille.
     */
    public Matrice multiplier(Vecteur autre) {
        if (this.taille() == autre.taille()) {
            Matrice res = new Matrice(this.taille(), this.taille());
            double somme;
            for (int i = 0; i < this.taille(); i++) {
                for (int j = 0; j < this.taille(); j++) {
                    somme = this.getValeur(i) * autre.getValeur(j);
                    res.setValeur(i, j, somme);
                }
            }
            return res;
        } else {
            throw new MatriceException("Les deux vecteurs ne sont pas de taille éguale");
        }
    }

    /**
     * @brief Calcule le produit scalaire de ce vecteur avec un autre.
     * @author Emma
     * @param autre Le vecteur à multiplier.
     * @return Le résultat du produit scalaire.
     * @throw VecteurException Si les deux vecteurs ne sont pas de même taille.
     */
    public double produitscalaire(Vecteur autre) {
        if (this.taille() == autre.taille()) {
            double res = 0;
            for(int i = 0; i < this.taille(); i++) {
                res = res + this.getValeur(i) * autre.getValeur(i);
            }
            return res;
        } else {
            throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
        }
    }
    
    /**
     * @brief Calcule la distance eucliedienne entre deux vecteurs.
     * @author Bastien
     * @param autre Le vecteur dont on calcule la distance avec.
     * @return la valeur de la distance entre les deux vecteurs.
     * @throw VecteurException Si les deux vecteurs ne sont pas de même taille.
     */
    public double distanceEuclidienne(Vecteur autre) {
    	if (this.taille() != autre.taille()) {
    		throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
    	}
    	else {
    		double somme = 0 ;
    		for (int i = 0; i<this.taille();i++) {
    			double diff = this.getValeur(i) - autre.getValeur(i);
    			somme += diff * diff;
    		}
    		return Math.sqrt(somme);
    	}
    }
    
    /**
     * @brief Copie d'un vecteur.
     * @author Bastien
     * @return La copie du vecteur.
     */
    public Vecteur copie() {
    	Vecteur copie = new Vecteur(this.taille());
    	for ( int i = 0; i< this.taille(); i++) {
    		copie.setValeur(i, this.getValeur(i));
    	}
    	return copie;
    }
}

