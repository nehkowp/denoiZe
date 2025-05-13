package application;

import java.io.IOException;

import model.base.Img;
import model.patch.ResultatPatch;
import model.patch.ResultatVecteur;
import service.patch.GestionnairePatchs;

public class Application {

	private final static int TAILLE_PATCH = 7;

	
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
		
		for(String imageName : imageNames) {
			Img x0 = null;
			try {
				x0 = new Img("data/x0/"+imageName);
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
			
			GestionnairePatchs gestionnaire = new GestionnairePatchs();
			
			ResultatPatch resPatchs = gestionnaire.extractPatchs(x0,TAILLE_PATCH);
			ResultatVecteur resVecteur = gestionnaire.vectorPatchs(resPatchs);
			System.out.println(resVecteur.getVecteurs().getFirst().getValeur(TAILLE_PATCH));
			Img xR = gestionnaire.reconstructionPatchs(resPatchs, x0.getHauteur(), x0.getLargeur());
			
			xR.saveImg("data/xR/"+imageName);

			System.out.println("Traitement de l'image "+ imageName + " terminé");
		}
			
			
		return;
	}
	
	
}
