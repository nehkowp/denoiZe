package service.seuillage;

import model.base.Img;
import model.base.Matrice;

public class ProcesseurSeuillage {
	
	public double seuilV(Img Xb , double sigma) {
		int h = Xb.getHauteur();
		int l = Xb.getLargeur();
		return sigma * Math.sqrt(2 * Math.log( h*l )) ;
	}
	
	
	public double seuilB(Img Xb , double sigma, Matrice gamma) {
		double sigmaXb = gamma.SommeDiagonale(); //on recupere la variance de l'image Xb sans tout recalculé
		double sigmaCarre = sigma * sigma ;
		double sigmaXbCarre = sigmaXb * sigmaXb ;
		double sigmaX = Math.sqrt( Math.abs(sigmaXbCarre - sigmaCarre));
		return sigmaCarre / (double) sigmaX ;
	}
	
	
	public double[] seuillageDur(double lambda, double[] alpha) {
		for (int i=0; i<alpha.length; i++) {
			if ( Math.abs(alpha[i]) <= lambda ) {
				alpha[i] = 0;
			}
		}
		return alpha;
	}
	
	
	public double[] seuillageDoux(double lambda, double[] alpha) {
				
		for (int i=0; i<alpha.length; i++) {
			if ( alpha[i] > lambda ) {
				alpha[i] -= lambda;
			} else if ( alpha[i] <= -lambda) {
				alpha[i] += lambda;
			} else {
				alpha[i] = 0;
			}
		}
		return alpha;
	}

	
}
