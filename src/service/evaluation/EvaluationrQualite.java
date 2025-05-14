package service.evaluation;

import model.base.Img;

public class EvaluationrQualite {

	public double mse(Img X0, Img Xr) {
		int nbLigne=X0.getHauteur();
		int nbColonne=X0.getLargeur();
		double mse = 0;
		
		for (int i = 0; i < nbLigne ; i++) {
			for (int j = 0; j < nbColonne ; j++) {
				double diff = (X0.getPixel(i, j) - Xr.getPixel(i, j));
				mse += ( diff * diff );
			}
		}
		mse = mse / (double) (nbLigne * nbColonne) ;
		return mse;

	}
	
	public double psnr(Img X0, Img Xr) {
		double mse = mse(X0, Xr);
		double inv_mse = 255 / (double) mse; /*255^2????*/
		double psnr = 10* Math.log10(inv_mse);
		return psnr;
		
	}

}


