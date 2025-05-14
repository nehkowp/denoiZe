package service.acp;
import model.base.Matrice;
import model.base.Vecteur;
import model.patch.ResultatVecteur;

import java.util.ArrayList;
import java.util.List;
import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;


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
        for (int k = 0; k < M; k++) {
            Vecteur centre = v.get(k).soustraire(mV);
            vc.set(k, centre);
        }
        
        // calcul matrice covariance gamma
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                double sum = 0;
                for (int k = 0; k < s2; k++) {
                    sum += vc.get(k).getValeur(i) * vc.get(k).getValeur(j);
                }
                gamma.setValeur(i, j, sum / M);
            }
        }
        return new ResultatMoyCov (mV, gamma, vc);
	}
	
	public void acp(ResultatVecteur v) {
		
	}
	
	
	
	public List<Vecteur> proj(ResultatVecteur U,ResultatVecteur Vc) {
		int s2 = Vc.getVecteurs().get(0).taille();
		int M = Vc.taille();
		List<Vecteur> alpha = new ArrayList<>();
		
		for (int k=0; k < M; k++) {
			Vecteur alpha_k = new Vecteur(s2);
			Vecteur Vci = Vc.getVecteurs().get(k);
			
			for (int i=0; i<s2 ; i++) {
				Vecteur uiPrime = U.getVecteurs().get(i);
				double contribution = uiPrime.multiplier(Vci);
				alpha_k.setValeur(i,contribution);
			}
			
			alpha.add(alpha_k);
		}
		return alpha;
	}

}




















