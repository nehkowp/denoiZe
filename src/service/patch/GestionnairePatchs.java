package service.patch;

import java.util.ArrayList;
import java.util.List;

import model.base.Img;
import model.base.Matrice;
import model.base.Pixel;
import model.base.Position;
import model.base.Vecteur;
import model.patch.Fenetre;
import model.patch.ParametresFenetre;
import model.patch.Patch;
import model.patch.ResultatPatch;
import model.patch.ResultatPatch.PairePatchPosition;
import model.patch.ResultatVecteur;



public class GestionnairePatchs {
	
	/**
     * Extrait des patchs de l'image avec une option de recouvrement.
     * @param Xs Image source
     * @param s Taille des patchs 
     * @param recouvrement Niveau de recouvrement (1 = pas de recouvrement, 2 = 50%, 4 = 75%, etc.)
     * @return ResultatPatch contenant les patchs extraits et leurs positions
     */
    public ResultatPatch extractPatchsAvecRecouvrement(Img Xs, int s, int recouvrement) {
        Pixel[][] imgPixels = Xs.getPixels();
        ResultatPatch resPatch = new ResultatPatch();
        
        // Calcul du pas selon le niveau de recouvrement
        int pas = Math.max(1, s / recouvrement);
        
        for(int i = 0; i <= (imgPixels.length)-s; i = i + pas) {
            for(int j = 0; j <= (imgPixels[0].length)-s; j = j + pas) {
                Pixel[][] patchPixels = new Pixel[s][s];
                Patch patch = new Patch(patchPixels);
                for(int x = 0; x < s; x++) {
                    for(int y = 0; y < s; y++) {
                        patch.getPixels()[x][y] = imgPixels[i+x][j+y];
                    }
                }
                resPatch.ajouterPatch(patch, new Position(i, j));
            }
        }
        
        return resPatch;
    }
	
    public ResultatPatch extractPatchs(Img Xs, int s) {
        // Utiliser un recouvrement de 2 (50%) par défaut
        return extractPatchsAvecRecouvrement(Xs, s, 2);
    }

 	public Img reconstructionPatchs(ResultatPatch yPatchs, int l, int c) {
 		Pixel[][] imgReconstuitePixels = new Pixel[l][c];
 		
 		for (int i = 0; i < l; i++) {
 		    for (int j = 0; j < c; j++) {
 		    	imgReconstuitePixels[i][j] = new Pixel(0); // Valeur par défaut (noir)
 		    }
 		}
 		
 		 for(PairePatchPosition p : yPatchs) {
 	        Position pPosition = p.getPosition();
 	        Patch pPatch = p.getPatch();
 	        
 	        for (int i = 0; i < pPatch.getTaille(); i++) {
 	            for (int j = 0; j < pPatch.getTaille(); j++) {
 	                int posY = pPosition.getI() + i;
 	                int posX = pPosition.getJ() + j;
 	                
 	                if (posY < l && posX < c) {
 	                    imgReconstuitePixels[posY][posX] = pPatch.getPixels()[i][j];
 	                }
 	            }
 	        }
 	    }
 		 
 		Img imgReconstruite = new Img(imgReconstuitePixels);
    	return imgReconstruite;

    }

 	public ResultatVecteur vectorPatchs(ResultatPatch yPatchs) {
 		ResultatVecteur resVect = new ResultatVecteur();

		
 		for(PairePatchPosition p : yPatchs) {
 			Position pPosition = p.getPosition();
 			Patch pPatch = p.getPatch();
 			double[] valeurs = new double[pPatch.getTaille()*pPatch.getTaille()];
 			int pos = 0;
 			for(int x = 0; x < pPatch.getTaille(); x++) {
		 		for(int y = 0; y < pPatch.getTaille(); y++) {
		 			valeurs[pos] = pPatch.getPixels()[x][y].getValeur();
		 			pos++;
		 			}
		 		}
 			resVect.ajouterVecteur(new Vecteur(valeurs), pPosition);

		 	}
 			
 		
    	return resVect;
    }
    
 	public List<Fenetre> decoupageImage(Img x, ParametresFenetre pF){
 		
 		List<Fenetre> fenetresList = new ArrayList<Fenetre>();
 		
 		
 		int chevauchementX = (int) Math.ceil(pF.getChevauchementCombineX() / (pF.getNombreFenetresX()-1));
 		int chevauchementY = (int) Math.ceil(pF.getChevauchementCombineY() / (pF.getNombreFenetresY()-1));
 		
 		int chevauchementXAcc = 0;
 		
 		int posX=0;
 		int posY=0;
 		
 	    int dernierePositionX = x.getLargeur() - pF.getTailleFenetreCalculee();
 	    int dernierePositionY = x.getHauteur() - pF.getTailleFenetreCalculee();
 	    
 		//indexFenetre commence à 1 /!\
 		for(int indexFenetreX = 0; indexFenetreX < pF.getNombreFenetresX(); indexFenetreX++) {
			
 			
 			if(indexFenetreX+1 == pF.getNombreFenetresX() ){ // Si dernière fenêtre horizontale ajustez posX
 				posX = dernierePositionX;
			}else{
				posX = (pF.getTailleFenetreCalculee()*indexFenetreX+1)-chevauchementXAcc;
			}
 				
 			
 			int chevauchementYAcc = 0;
				
 			for(int indexFenetreY = 0; indexFenetreY < pF.getNombreFenetresY(); indexFenetreY++) {
 				
 				if(indexFenetreY+1 == pF.getNombreFenetresY()){ // Si dernière fenêtre horizontale ajustez posY
 					posY = dernierePositionY;
 				}else{
 					posY = (pF.getTailleFenetreCalculee()*indexFenetreY+1)-chevauchementYAcc;
 				}
 				
 				Pixel[][] fPixels = new Pixel[pF.getTailleFenetreCalculee()][pF.getTailleFenetreCalculee()];
 				
 				
 				
 	     		for (int i = 0; i < pF.getTailleFenetreCalculee(); i++) {
 	     		    for (int j = 0; j < pF.getTailleFenetreCalculee(); j++) {
 	     		    	
 	     		    	fPixels[i][j] = x.getPixel(posY+i,posX+j);
 	     		    	fPixels[i][j].setNbChevauchement(fPixels[i][j].getNbChevauchement() + 1);
 	     		    	
 	     		    }
 	     		}
 				
 	     		
 	     		Img fImage = new Img(fPixels);
 				Fenetre f = new Fenetre(fImage, new Position(posY,posX));
 				
 	     		chevauchementYAcc = chevauchementYAcc + chevauchementY;
 				
 				fenetresList.add(f);
 				
 			}
	     	chevauchementXAcc = chevauchementXAcc + chevauchementX;

 		}
 				
 			
		
 		
		return fenetresList;
	}

 	public ResultatPatch transformerVecteursEnResultatPatch(ResultatVecteur vecteursReconstruits) {
 	    ResultatPatch resultatPatch = new ResultatPatch();
 	    int taillePatch = (int) Math.sqrt(vecteursReconstruits.getVecteurs().get(0).taille());

 	    for (int k = 0; k < vecteursReconstruits.taille(); k++) {
 	        double[] valeurs = vecteursReconstruits.getVecteurs().get(k).getValeurs();
 	        Pixel[][] patchPixels = new Pixel[taillePatch][taillePatch];

 	        for (int i = 0; i < taillePatch; i++) {
 	            for (int j = 0; j < taillePatch; j++) {
 	                int index = i * taillePatch + j;
 	                int valeur = (int) Math.min(255, Math.max(0, Math.round(valeurs[index])));
 	                patchPixels[i][j] = new Pixel(valeur);
 	            }
 	        }

 	        Patch patch = new Patch(patchPixels);
 	        resultatPatch.ajouterPatch(patch, vecteursReconstruits.getPositions().get(k));
 	    }

 	    return resultatPatch;
 	}

 	public ResultatVecteur matriceToResultatVecteur(Matrice matrice) {
 	    ResultatVecteur resultat = new ResultatVecteur();
 	    int nbColonnes = matrice.getNbColonnes();

 	    for (int j = 0; j < nbColonnes; j++) {
 	        double[] valeurs = new double[matrice.getNbLignes()];
 	        for (int i = 0; i < matrice.getNbLignes(); i++) {
 	            valeurs[i] = matrice.getValeur(i, j);
 	        }
 	        resultat.ajouterVecteur(new Vecteur(valeurs), null);  // position peut être null ici
 	    }
 	    return resultat;
 	}


		
}
