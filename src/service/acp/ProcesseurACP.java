package service.acp;

public class ProcesseurACP {

public void moyCov(ResultatVecteur v) { // on initialise v la matrice des patchs
		int s2 = v.length ; // dimension de patchs (s^2)
		int m = v[0].length ; // nombre de patchs
		
		// initialisations
        Vecteur mV = new double[s2][1];
        ResultatVecteur vc = new double[s2][m];
        Matrice gamma = new double[s2][s2];
        
        // calcul vect moyen mV
        for (int i = 0; i < s2; i++) {
            double sum = 0;
            for (int k = 0; k < m; k++) {
                sum += v[i][k];
            }
            mV[i][0] = sum / m;
        }
        
        // calcul vecteurs centrés vc
        for (int i = 0; i < s2; i++) {
            for (int k = 0; k < m; k++) {
                vc[i][k] = v[i][k] - mV[i][0];
            }
        }
        
        // calcul matrice covariance gamma
        for (int i = 0; i < s2; i++) {
            for (int j = 0; j < s2; j++) {
                double sum = 0;
                for (int k = 0; k < m; k++) {
                    sum += vc[i][k] * vc[j][k];
                }
                gamma[i][j] = sum / m;
            }
        }
	}
	
	public void acp(ResultatVecteur v) {
		
	}
}
