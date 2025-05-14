package service.acp;
import model.base.Matrice;
import model.base.Vecteur;
import model.patch.ResultatVecteur;
import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;

import model.acp.ResultatMoyCov;
import model.base.Matrice;
import model.base.Vecteur;
import model.patch.ResultatVecteur;

public class ProcesseurACP {

public ResultatMoyCov moyCov(ResultatVecteur v) { // on initialise v la matrice des patchs
		int s2 = v.getVecteurs().get(0).taille() ; // dimension de patchs (s^2)
		int M = v.taille() ; // nombre de patchs
		
		// initialisations
        Vecteur mV = new Vecteur(s2);
        Matrice gamma = new Matrice(M,M);
        ResultatVecteur vc = new ResultatVecteur();
        
        
        // calcul vect moyen mV
        for (int i = 0; i < M; i++) { 
            mV = mV.ajouter(v.getVecteurs().get(i));
        }
        mV = mV.diviser(M);
        
        // calcul vecteurs centrés vc
        for (int i = 0; i < s2; i++) {
            for (int k = 0; k < m; k++) {
                vc[i][k] = v[i][k] - mV[i][0];
            }
        }
        
        // calcul matrice covariance gamma
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double sum = 0;
                for (int k = 0; k < s2; k++) {
                    sum += vc.get(k).getValeur(i) * vc.get(k).getValeur(j);
                }
                gamma.setValeur(i, j, sum / m);
            }
        }
	}
	
	public void acp(ResultatVecteur v) {
		
	}
	
	
	
	public List<Vecteur> proj(ResultatVecteur u,ResultatVecteur Vc) {
		
		
		
	}
}


































