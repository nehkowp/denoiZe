package service.debruitage;

import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import model.base.Img;
import model.patch.Fenetre;
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
    

    public DebruiteurImage() {
        this.bruiteurImage = new BruiteurImage();
        this.gestionnairePatchs = new GestionnairePatchs();
        this.processeurACP = new ProcesseurACP();
        this.processeurSeuillage = new ProcesseurSeuillage();
        this.evaluationQualite = new EvaluationrQualite();
    }
    
    
    //modeLocal True --> Local & False --> Global
    public Img imageDen(Img xB, String typeSeuil, String fonctionSeuillage, int taillePatch, boolean modeLocal) {
    	Img xR = null;
    	if(modeLocal) {
    		//LOCAL
    		

    		List<Fenetre> imagettesList = this.gestionnairePatchs.decoupageImage(xB, xB.getLargeur()/2, 4);
    		
    		List<Fenetre> newFenetresList = new ArrayList<Fenetre>();
    		
    		for(Fenetre f : imagettesList) {
    			ResultatPatch resPatchs = this.gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
        		ResultatVecteur resVecteur = this.gestionnairePatchs.vectorPatchs(resPatchs);
        		
        		// Traitement ACP + Seuillage 

        		Fenetre nf = new Fenetre(this.gestionnairePatchs.reconstructionPatchs(resPatchs, f.getImage().getHauteur(), f.getImage().getLargeur()), f.getPosition());
                newFenetresList.add(nf);
    		}
    		
     		double[][] xRPixels = new double[xB.getHauteur()][xB.getLargeur()];
    		for(Fenetre nf : newFenetresList) {
    			double[][] nfPixels = nf.getImage().getPixels();
    			for(int i = 0; i < nfPixels.length ;i++) {
        			for(int j = 0; j < nfPixels[0].length;j++) {
        				xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()] = nfPixels[i][j];
        			}
    			}
    		}
    		
    		xR = new Img(xRPixels);
    		
    		
    		
    		
    	}else {
    		//GLOBAL
    		ResultatPatch resPatchs = this.gestionnairePatchs.extractPatchs(xB, taillePatch);
    		ResultatVecteur resVecteur = this.gestionnairePatchs.vectorPatchs(resPatchs);
    		
    		// Traitement ACP + Seuillage 
    		
            xR = this.gestionnairePatchs.reconstructionPatchs(resPatchs, xB.getHauteur(), xB.getLargeur());

    	}
    	
    	
    	return xR;   	
    }
    

    public static void main(String[] args) throws IOException {
        String[] imageNames = {
            "ali_gray.jpg",
            "crocodilo_gray.jpg",
            "darkvador_gray.jpg",
            "gekko_gray.jpg",
            "harrypotter_gray.png",
            "leclerc_gray.png",
            "lena_gray.png",
            "mbappe_gray.jpg",
            "moto_gray.jpeg",
            "nyancat_gray.png",
            "steve_gray.jpg",
            "wemby_gray.png"
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
            Img xR1 = dImg.imageDen(x0, null, null, TAILLE_PATCH_GLOBAL, false);
//            Img xR2 = dImg.imageDen(x0, null, null, TAILLE_PATCH_LOCAL, true);

            xR1.saveImg("data/xR/" + imageName);
//            xR2.saveImg("data/xR/" + imageName);



            System.out.println("Traitement de l'image " + imageName + " terminé");
        }

        return;
    }

}
