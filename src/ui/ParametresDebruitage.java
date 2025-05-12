package ui;

public class ParametresDebruitage {
	private int taillePatch;
	private int tailleFenetre;
	private double sigma;
	private String typeSeuil;
	private String fonctionSeuillage;
	private boolean modeLocal;
	
	public ParametresDebruitage(int taillePatch, int tailleFenetre, double sigma, String typeSeuil, String fonctionSeuillage, boolean modeLocal) {
		this.fonctionSeuillage = fonctionSeuillage;
		this.modeLocal = modeLocal;
		this.sigma = sigma;
		this.tailleFenetre = tailleFenetre;
		this.taillePatch = taillePatch;
		this.typeSeuil = typeSeuil;
	}

	public int getTaillePatch() {
		return taillePatch;
	}

	public int getTailleFenetre() {
		return tailleFenetre;
	}

	public double getSigma() {
		return sigma;
	}

	public String getTypeSeuil() {
		return typeSeuil;
	}

	public String getFonctionSeuillage() {
		return fonctionSeuillage;
	}

	public boolean isModeLocal() {
		return modeLocal;
	}
	
}
