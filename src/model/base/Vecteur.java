package model.base;

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
	
	public Vecteur soustraire(Vecteur autre) { //a faire
		return new Vecteur(autre.getValeurs());
	}
	
	public Vecteur ajouter(Vecteur autre) { // a faire
		return new Vecteur(autre.getValeurs());
	}
}
