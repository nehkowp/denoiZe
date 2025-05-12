package model.base;

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
	
	public Matrice multiplier(Matrice autre) { //a faire
		return new Matrice(autre.valeurs);
	}
	
	public Vecteur multiplier(Vecteur vecteur) { //a faire
		return new Vecteur(vecteur.getValeurs());
	}
	
	public Matrice transposer() { //a faire
		return new Matrice(this.valeurs);
	}
}
