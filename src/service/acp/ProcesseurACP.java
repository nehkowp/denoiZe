package service.acp;
import model.base.Matrice;
import model.base.Position;
import model.base.Vecteur;
import model.patch.ResultatVecteur;
import java.util.ArrayList;
import java.util.List;

import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;
import org.apache.commons.math3.linear.*;

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
        	Vecteur V_k = v.getVecteurs().get(k);
        	Vecteur centre = V_k.soustraire(mV);
        	Position coordonne = new Position(k,k);
        	vc.ajouterVecteur(centre, coordonne);
        }
        
        // calcul matrice covariance gamma
        for (int k = 0; k<M; k++) {
        	gamma.ajouter(vc.getVecteurs().get(k).multiplier(vc.getVecteurs().get(k)));
        }
        double invM = 1 / (double) M;
        gamma.multiplierParScalaire(invM);
        
        
        return new ResultatMoyCov(mV, gamma, vc);
	}
	
	public ResultatACP acp(ResultatVecteur v) {
		
		 // vecteur moyen et matrice de covariance
	    ResultatMoyCov res = moyCov(v);
	    Vecteur mV = res.getVecteurMoyen();
	    Matrice gamma = res.getMatriceCovariance();

	    int M = gamma.getLignes();

	    //  Conversion matrice (privée) de covariance en RealMatrix (Apache Commons Math)
	    double[][] gammaData = new double[M][M];
	    for (int i = 0; i < M; i++) {
	        for (int j = 0; j < M; j++) {
	            gammaData[i][j] = gamma.getValeur(i, j);
	        }
	    }
	    RealMatrix gammaMatrix = new Array2DRowRealMatrix(gammaData);

	    // Décomposition pour trouver les val propres et vect propres
	    EigenDecomposition eig = new EigenDecomposition(gammaMatrix);

	    // 4. Extraction des valeurs propres et des vecteurs propres
	    double[] valeursPropres = eig.getRealEigenvalues();
	    double[][] vecteursPropresData = new double[M][M];
	    for (int i = 0; i < M; i++) {
	        double[] vPropre = eig.getEigenvector(i).toArray();
	        for (int j = 0; j < M; j++) {
	            vecteursPropresData[j][i] = vPropre[j]; // vecteur propre i dans la colonne i
	        }
	    }

	    // Ordonner les valeurs propres et vecteurs propres dans l’ordre décroissant
	    for (int i = 0; i < M - 1; i++) {
	        for (int j = i + 1; j < M; j++) {
	            if (valeursPropres[j] > valeursPropres[i]) {
	                // Échanger valeurs propres
	                double tempVal = valeursPropres[i];
	                valeursPropres[i] = valeursPropres[j];
	                valeursPropres[j] = tempVal;
	                // Échanger vecteurs propres
	                for (int k = 0; k < M; k++) {
	                    double tempVec = vecteursPropresData[k][i];
	                    vecteursPropresData[k][i] = vecteursPropresData[k][j];
	                    vecteursPropresData[k][j] = tempVec;
	                }
	            }
	        }
	    }

	    // Création de la matrice des vecteurs propres dans notre classe Matrice (privée)
	    Matrice vecteursPropres = new Matrice(vecteursPropresData);

	    return new ResultatACP(valeursPropres, vecteursPropres, mV);
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
				double contribution = uiPrime.produitscalaire(Vci);
				alpha_k.setValeur(i,contribution);
			}
			
			alpha.add(alpha_k);
		}
		return alpha;
	}

}




















