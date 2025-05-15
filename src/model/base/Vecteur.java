package model.base;

import exception.MatriceException;
import exception.VecteurException;

public class Vecteur {
	private double[] valeurs;
	
	public Vecteur(double[] valeurs) {
		this.valeurs = valeurs;
	}
	
	public Vecteur(int taille) {
		this.valeurs = new double[taille];
	}

	public double getValeur(int index) {
		return valeurs[index];
	}

	public void setValeur(int index, double valeur) {
		this.valeurs[index] = valeur;
	}
	
	public double[] getValeurs() {
		return this.valeurs;
	}
	
	public int taille() {
		return this.valeurs.length;
	}
	
	public Vecteur soustraire(Vecteur autre) { 
		if (this.taille() == autre.taille()) {
			Vecteur res = new Vecteur(this.taille());
			for(int i = 0; i<this.taille(); i++) {
				res.getValeurs()[i] = this.getValeur(i) - autre.getValeur(i);
			}
			return res;
		} else {
			throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
		}
	}
	
	public Vecteur ajouter(Vecteur autre) { 
		if (this.taille() == autre.taille()) {
			Vecteur res = new Vecteur(this.taille());
			for(int i = 0; i<this.taille(); i++) {
				res.getValeurs()[i] = this.getValeur(i) + autre.getValeur(i);
			}
			return res;
		} else {
			throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
		}
	}
	
	public Vecteur diviser(double scalaire) {
		if (scalaire == 0) {
			throw new VecteurException("Division par 0 impossible");
		} else {
			Vecteur res = new Vecteur(this.taille());
			for(int i = 0; i<this.taille(); i++) {
				res.getValeurs()[i] = this.getValeur(i) / scalaire;
			}
			return res;
		}
	}
	
	
	public Matrice multiplier(Vecteur autre) {
		if (this.taille() == autre.taille()) {
			Matrice res = new Matrice(this.taille(), this.taille());
			double somme;
			for (int i = 0; i < this.taille(); i++) {
				for (int j = 0; j < this.taille(); j++) {
					somme  = this.getValeur(i) * autre.getValeur(j);
					res.setValeur(i, j, somme);
				}
			}
		    return res;
		} else {
			throw new MatriceException("Les deux vecteurs ne sont pas de taille éguale");
		} 
	}
	
	public double produitscalaire(Vecteur autre) {
		if (this.taille() == autre.taille()) {
			double res = 0;
			for(int i = 0; i<this.taille(); i++) {
				res = res + this.getValeur(i) * autre.getValeur(i);
			}
			return res;
		} else {
			throw new VecteurException("Les deux vecteurs ne sont pas de taille éguale");
		}
	}
}
