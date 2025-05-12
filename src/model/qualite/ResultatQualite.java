package model.qualite;

public class ResultatQualite {
	private double mse;
	private double psnr;
	
	public ResultatQualite(double mse, double psnr) {
		this.mse = mse;
		this.psnr = psnr;
	}

	public double getMSE() {
		return mse;
	}

	public double getPSNR() {
		return psnr;
	}
	
}
