package model.base;

import exception.MatriceException;

public class Matrice {
	private double[][] valeurs;
	private int lignes;
	private int colonnes;
	
	public Matrice(double[][] valeurs) {
		this.valeurs = valeurs;
		this.lignes = valeurs.length;
		this.colonnes = valeurs[0].length;
	}
	
	public Matrice(int lignes, int colonnes) {
		this.lignes = lignes;
		this.colonnes = colonnes;
		this.valeurs = new double[lignes][colonnes];
	}

	public double getValeur(int ligne, int colonne) {
		return this.valeurs[ligne][colonne];
	}

	public void setValeur(int ligne, int colonne, double valeur) {
		this.valeurs[ligne][colonne] = valeur;
	}

	public int getLignes() {
		return lignes;
	}

	public int getColonnes() {
		return colonnes;
	}
	
	public Matrice multiplier(Matrice autre) {
	    if (this.valeurs[0].length == autre.valeurs.length) {
	       Matrice res = new Matrice(this.valeurs.length, autre.valeurs[0].length);
	       double somme;
	       for (int i = 0; i < this.valeurs.length; i++) {
	    	   for (int j = 0; j < autre.valeurs[0].length; j++) {
	    		   somme = 0;
	    		   for (int k = 0; k < this.valeurs[0].length; k++) {
	    			   somme  = somme + this.valeurs[i][k] * autre.valeurs[k][j];
	    		   }
	    		   res.setValeur(i, j, somme);
	    	   }
	       }
	       return res;
	    } else {
	    	throw new MatriceException("Le nombre de colonnes de la première matrice doit être égal au nombre de lignes de la seconde matrice.");
	    }   
	}

	public Vecteur multiplier(Vecteur vecteur) { 
		if (this.valeurs[0].length == vecteur.getValeurs().length) {
			Vecteur res = new Vecteur(this.valeurs.length);
			double somme;
			for (int i = 0; i < this.valeurs.length; i++) {
				somme = 0;
				for (int j = 0; j < this.valeurs[0].length; j++) {
					somme  = somme + this.valeurs[i][j] * vecteur.getValeur(j);
				}
				res.setValeur(i, somme);
			}
			return res;
		} else {
		    throw new MatriceException("Le nombre de colonnes de la matrice doit être égal au nombre d'éléments du vecteur.");
		} 
	}
	
	public Matrice transposer() { 
		Matrice res = new Matrice(this.valeurs.length, this.valeurs[0].length);
	    for (int i = 0; i < this.valeurs.length; i++) {
	   	   for (int j = 0; j < this.valeurs[0].length; j++) {
	   		   res.setValeur(j, i, this.getValeur(i, j));
	   	   }
	    }
	    return res;
	}
}
