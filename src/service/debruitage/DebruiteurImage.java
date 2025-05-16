package service.debruitage;

import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;
import model.base.Img;
import model.base.Pixel;
import model.base.Vecteur;
import model.patch.Fenetre;
import model.patch.ParametresFenetre;
import model.patch.ResultatPatch;
import model.patch.ResultatVecteur;
import service.acp.ProcesseurACP;
import service.bruit.BruiteurImage;
import service.evaluation.EvaluationrQualite;
import service.patch.GestionnairePatchs;
import service.seuillage.ProcesseurSeuillage;

public class DebruiteurImage {

    private BruiteurImage bruiteurImage;
    private GestionnairePatchs gestionnairePatchs;
    private ProcesseurACP processeurACP;
    private ProcesseurSeuillage processeurSeuillage;
    private EvaluationrQualite evaluationQualite;
    private final static int TAILLE_PATCH_GLOBAL = 7;
    private final static int TAILLE_PATCH_LOCAL = 17;
    private final static int TAILLE_FENETRE_DEFAUT = 250;

    public DebruiteurImage() {
        this.bruiteurImage = new BruiteurImage();
        this.gestionnairePatchs = new GestionnairePatchs();
        this.processeurACP = new ProcesseurACP();
        this.processeurSeuillage = new ProcesseurSeuillage();
        this.evaluationQualite = new EvaluationrQualite();
        
    }
    
    
    //modeLocal True --> Local & False --> Global
    public Img imageDen(Img xB ,String typeSeuil, String fonctionSeuillage,  double sigma ,int taillePatch, boolean modeLocal) {
    	Img xR = null;
    	if(modeLocal) {
    		//LOCAL
    		ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(xB.getLargeur(), xB.getHauteur(), TAILLE_FENETRE_DEFAUT); 

    		 // Affichage détaillé de tous les paramètres
    	    System.out.println("---------- DÉTAILS DES PARAMÈTRES DE FENÊTRES ----------");
    	    System.out.println("Dimensions de l'image : " + xB.getLargeur() + "×" + xB.getHauteur() + " pixels");
    	    System.out.println();
    	    System.out.println("Taille de fenêtre : " + pF.getTailleFenetreCalculee() + "×" + pF.getTailleFenetreCalculee() + " pixels");

    	    
    	    System.out.println("Nombre de fenêtres horizontales : " + pF.getNombreFenetresX());
    	    System.out.println("Nombre de fenêtres verticales : " + pF.getNombreFenetresY());
    	    System.out.println("Nombre total de fenêtres : " + pF.getNombreFenetresTotal());
    	    System.out.println();
    	    
    	    System.out.println("Chevauchement combiné en X : " + pF.getChevauchementCombineX() + " pixels");
    	    System.out.println("Chevauchement combiné en Y : " + pF.getChevauchementCombineY() + " pixels");
    	    System.out.println();
    		
    		
    		List<Fenetre> imagettesList = this.gestionnairePatchs.decoupageImage(xB,pF);
    		
    		List<Fenetre> newFenetresList = new ArrayList<Fenetre>();
    		
    		for(Fenetre f : imagettesList) {
    			
    			ResultatPatch resPatchs = this.gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
        		ResultatVecteur resVecteur = this.gestionnairePatchs.vectorPatchs(resPatchs);
        		
        		// Traitement ACP + Seuillage 
        		//System.out.println("Début ACP");
        		//ResultatACP resACP= this.processeurACP.acp(resVecteur);
        		//System.out.println(resACP);
//        		
//
//        		List<Vecteur> listeVecteurCoefs = this.processeurACP.proj(ResultatVecteur.transformerMatriceVecteursPropresEnResultatVecteur(resACP.getVecteursPropres()),resMoyCov.getVecteursCenters());
//        		
//        		
//
//        		//ResultatPatch resPatchReconstruits= this.gestionnairePatchs.deVectorPatchs(resPatchs);

        		
        		Fenetre nf = new Fenetre(this.gestionnairePatchs.reconstructionPatchs(resPatchs, f.getImage().getHauteur(), f.getImage().getLargeur(), xB), f.getPosition());
                newFenetresList.add(nf);
    		}
    		
     		Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
     		
     		for (int i = 0; i < xB.getHauteur(); i++) {
     		    for (int j = 0; j < xB.getLargeur(); j++) {
     		        xRPixels[i][j] = new Pixel(0); // Valeur par défaut
     		    }
     		}
     		
     		
    		for(Fenetre nf : newFenetresList) {
    			Pixel[][] nfPixels = nf.getImage().getPixels();
    			for(int i = 0; i < nfPixels.length ;i++) {
        			for(int j = 0; j < nfPixels[0].length;j++) {
        				xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].setValeur(
        						xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].getValeur() 
        						+
        						nfPixels[i][j].getValeur()
        				);
        				xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].setNbChevauchement(
        					    xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].getNbChevauchement() + 1);
        			}
    			}
    		}
    		
    		for(int i = 0; i < xRPixels.length ;i++) {
    			for(int j = 0; j < xRPixels[0].length;j++) {
    				
    				if (xRPixels[i][j].getNbChevauchement() > 0) {
    					
    					double valeurAvant = xRPixels[i][j].getValeur();
    		            int nbChevauchement = xRPixels[i][j].getNbChevauchement();
    		            
    		            double valeurNormalisee = valeurAvant / (double) nbChevauchement;
    		            int valeurFinale = (int) Math.min(255, Math.max(0, Math.round(valeurNormalisee)));
    		            
    		            xRPixels[i][j].setValeur(valeurFinale);
    		        }
    				
    			}
    			
			}
    		
    		
    		
    		xR = new Img(xRPixels);
    		
    		
    		
    		
    	}else {
    		//GLOBAL
    		ResultatPatch resPatchs = this.gestionnairePatchs.extractPatchs(xB, taillePatch);
    		ResultatVecteur resVecteur = this.gestionnairePatchs.vectorPatchs(resPatchs);
    		
    		// Traitement ACP + Seuillage 
    		
    		xR = this.gestionnairePatchs.reconstructionPatchs(resPatchs, xB.getHauteur(), xB.getLargeur(), xB);

    	}
    	
    	return xR;   	
    }
    

    public static void main(String[] args) throws IOException {
        String[] imageNames = {
           "ali_gray.jpg"
//            "crocodilo_gray.jpg",
//            "darkvador_gray.jpg",
//            "gekko_gray.jpg",
//        	  "harrypotter_gray.png"
//            "leclerc_gray.png",
//            "lena_gray.png",
//            "mbappe_gray.jpg",
//            "moto_gray.jpeg",
//            "nyancat_gray.png",
//            "steve_gray.jpg",
//            "wemby_gray.png"
        };

        for (String imageName : imageNames) {
            Img x0 = null;
            try {
                x0 = new Img("data/x0/" + imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
//			for(double sigma=10; sigma < 40; sigma+=10) {
//				Img xB = noising(x0,sigma);
//								
//				try {
//					xB.saveImg("data/xB/"+(int) sigma+"/"+imageName);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}

            DebruiteurImage dImg = new DebruiteurImage();
//            Img xR1 = dImg.imageDen(x0, null, null, 20.0 ,TAILLE_PATCH_GLOBAL, false);
            Img xR2 = dImg.imageDen(x0, null, null, 20.0 , TAILLE_PATCH_LOCAL, true);

//            xR1.saveImg("data/xR/" + imageName);
            xR2.saveImg("data/xR/" + imageName);



            System.out.println("Traitement de l'image " + imageName + " terminé");
        }

        return;
    }

}
