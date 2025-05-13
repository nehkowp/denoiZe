package service.seuillage;

import model.base.Img;

public class ProcesseurSeuillage {
	
	/*les private ca va degager*/
	private double sigma ; /*ecart type du bruit*/
	private int L ; /*nombre de pixels de l'image*/
	private double sigmaX; /*ecart type estimé du signal*/
	private double sigmaXb; /*ecart type estimée du signal observé*/
	private double[] alpha; /*coef de la representation du patch vextorisé bruité Vk*/
	private double lambda; /*parametre de seuillage*/
	
	/* qu'est ce que j'ai besoin de recuperer ?*/
	/* sigma L  alpha*/
	/* qu'est ce que je dois calculer ? */
	/* sigmaX sigmaXb sont des ecart type*/
	/* on calcule alpha il faut recup moyenne et vecteurs de la base orthonormé*/
	
	public double seuilV(Img Xb , double sigma) {
		return sigma * Math.sqrt(2 * Math.log(L)) ;
	}
	
	
	public double seuilB(Img Xb , double sigma) {
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
