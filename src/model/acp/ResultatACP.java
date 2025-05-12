package model.acp;
import model.base.Matrice;
import model.base.Vecteur;

public class ResultatACP {
	private double[] valeursPropres;
	private Matrice vecteursPropres;
	private Vecteur vecteurMoyen;
	
	public ResultatACP(double[] valeursPropres, Matrice vecteursPropres, Vecteur vecteurMoyen) {
		this.valeursPropres = valeursPropres;
		this.vecteurMoyen = vecteurMoyen;
		this.vecteursPropres = vecteursPropres;
	}

	public double[] getValeursPropres() {
		return valeursPropres;
	}

	public Matrice getVecteursPropres() {
		return vecteursPropres;
	}

	public Vecteur getVecteurMoyen() {
		return vecteurMoyen;
	}
}
