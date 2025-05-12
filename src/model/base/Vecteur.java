package model.base;

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
}
