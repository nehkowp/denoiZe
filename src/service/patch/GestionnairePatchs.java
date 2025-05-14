package service.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.base.Img;
import model.base.Position;
import model.base.Vecteur;
import model.patch.Fenetre;
import model.patch.Patch;
import model.patch.ResultatPatch;
import model.patch.ResultatPatch.PairePatchPosition;
import model.patch.ResultatVecteur;



public class GestionnairePatchs {
	
 	public ResultatPatch extractPatchs(Img Xs, int s) {
 		double[][] imgPixels = Xs.getPixels();
 		ResultatPatch resPatch = new ResultatPatch();
 		
 		
 		for(int i = 0; i <= (imgPixels.length)-s; i++) {
 			for(int j = 0; j <= (imgPixels[0].length)-s; j++) {
 				double[][] patchPixels = new double[s][s];
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
 		double[][] imgReconstuitePixels = new double[l][c];
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
		 			valeurs[pos] = pPatch.getPixels()[x][y];
		 			pos++;
		 			}
		 		}
 			resVect.ajouterVecteur(new Vecteur(valeurs), pPosition);

		 	}
 			
 		
    	return resVect;
    }
    
 	public List<Fenetre> decoupageImage(Img x, int w,int n){
 		
 		
 		List<Fenetre> fenetresList = new ArrayList<Fenetre>();
		Random randomNumbers = new Random();
		
		while(n > 0) {
			
			int rL = randomNumbers.nextInt(x.getPixels().length-w);
	 		int rC = randomNumbers.nextInt(x.getPixels()[0].length-w);
			double[][] fenetrePixels = new double[w][w];

	 		for(int i = rL; i < rL+w ; i++) {
		 		for(int j = rC; j < rC+w ;j++) {
		 			fenetrePixels[i-rL][j-rC] =  x.getPixel(i, j);
		 		}
		 	}
	 		
	 		fenetresList.add(new Fenetre(new Img(fenetrePixels) , new Position(rL,rC)));
	 	
			n--;
		}
		
 		
		return fenetresList;
	}


		
}
