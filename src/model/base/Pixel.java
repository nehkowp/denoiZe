package model.base;

public class Pixel {

	private double valeur;
	private int nbChevauchement;
	
	
	public Pixel(double valeur) {
		this.valeur= valeur;
		this.nbChevauchement = 0;
	}

	public double getValeur() {return valeur;}
	
	public void setValeur(double valeur) {
		this.valeur = valeur;
	}

	public void setNbChevauchement(int nbChevauchement) {
		this.nbChevauchement = nbChevauchement;
	}

	public int getNbChevauchement() {return nbChevauchement;}

	
	
}
