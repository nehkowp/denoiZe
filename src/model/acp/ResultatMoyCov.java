package model.acp;
import model.base.Matrice;
import model.base.Vecteur;
import model.patch.ResultatVecteur;

public class ResultatMoyCov {
	private Vecteur mV;
	private Matrice gamma;
	private ResultatVecteur vc;
	
	public ResultatMoyCov(Vecteur mV, Matrice gamma, ResultatVecteur vc) {
		this.gamma = gamma;
		this.mV = mV;
		this.vc = vc;
	}

	public Vecteur getVecteurMoyen() {
		return mV;
	}

	public Matrice getMatriceCovariance() {
		return gamma;
	}

	public ResultatVecteur getVecteursCenters() {
		return vc;
	}
}
