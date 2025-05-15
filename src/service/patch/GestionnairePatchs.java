package service.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.base.Img;
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
	
 	public ResultatPatch extractPatchs(Img Xs, int s) {
 		Pixel[][] imgPixels = Xs.getPixels();
 		ResultatPatch resPatch = new ResultatPatch();
 		
 		
 		for(int i = 0; i <= (imgPixels.length)-s; i++) {
 			for(int j = 0; j <= (imgPixels[0].length)-s; j++) {
 				Pixel[][] patchPixels = new Pixel[s][s];
 				Patch patch = new Patch(patchPixels);
 				for(int x = 0; x < s; x++) {
 		 			for(int y = 0; y < s; y++) {
 		 				patch.getPixels()[x][y] = imgPixels[i+x][j+y];
 		 			}
 		 		}
 				resPatch.ajouterPatch(patch,new Position(i, j));
 	 		}
 		}
 		
 		return resPatch;
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
 			imgReconstuitePixels[pPosition.getI()][pPosition.getJ()] = pPatch.getPixels()[0][0];
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
 		
 		//indexFenetre commence à 1 /!\
 		for(int indexFenetreX = 0; indexFenetreX < pF.getNombreFenetresX(); indexFenetreX++) {
			
 			
 			if(indexFenetreX+1 == pF.getNombreFenetresX() ){ // Si dernière fenêtre horizontale ajustez posX
 				System.out.println("Dernière Fenêtre X");
				posX = pF.getChevauchementCombineX() - (chevauchementX * (indexFenetreX));
			}else{
				posX = (pF.getTailleFenetreCalculee()*indexFenetreX)-chevauchementXAcc;
			}
 				
 			
 			int chevauchementYAcc = 0;
				
 			for(int indexFenetreY = 0; indexFenetreY < pF.getNombreFenetresY(); indexFenetreY++) {
 				
 				if(indexFenetreY+1 == pF.getNombreFenetresY()){ // Si dernière fenêtre horizontale ajustez posY
 					System.out.println("Dernière Fenêtre Y");
 					posY = pF.getChevauchementCombineY() - (chevauchementY * (indexFenetreY));
 				}else{
 					posY = (pF.getTailleFenetreCalculee()*indexFenetreY)-chevauchementYAcc;
 				}
 				
 				Pixel[][] fPixels = new Pixel[pF.getTailleFenetreCalculee()][pF.getTailleFenetreCalculee()];
 				
 				
 				System.out.println(posX);
 				System.out.println(posY);		
 				
 	     		for (int i = 0; i < pF.getTailleFenetreCalculee(); i++) {
 	     		    for (int j = 0; j < pF.getTailleFenetreCalculee(); j++) {
 	     		    	fPixels[i][j] = x.getPixel(posX+i,posY+j);
 	     		    }
 	     		}
 				
 	     		
 	     		Img fImage = new Img(fPixels);
 				Fenetre f = new Fenetre(fImage, new Position(posX,posY));
 				
 	     		chevauchementYAcc = chevauchementYAcc + chevauchementY;
 				
 				fenetresList.add(f);
 				
 			}
	     	chevauchementXAcc = chevauchementXAcc + chevauchementX;

 		}
 				
 			
		
 		
		return fenetresList;
	}


		
}
