/**
 * @file DebruiteurImage.java
 * @brief Classe pour le débruitage d'images utilisant l'ACP et le seuillage.
 */
package service.debruitage;

import java.util.ArrayList;
import java.util.List;

import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;
import model.base.Img;
import model.base.Pixel;
import model.base.Position;
import model.patch.Fenetre;
import model.patch.ParametresFenetre;
import model.patch.ResultatPatch;
import model.patch.ResultatPatch.PairePatchPosition;
import model.patch.ResultatVecteur;
import service.acp.ProcesseurACP;
import service.patch.GestionnairePatchs;
import service.seuillage.ProcesseurSeuillage;

/**
 * @class DebruiteurImage
 * @brief Implémente le débruitage d'images par analyse en composantes
 *        principales et seuillage.
 * @author Paul & Emma
 */
public class DebruiteurImage {

	private GestionnairePatchs gestionnairePatchs;
	private ProcesseurACP processeurACP;
	private ProcesseurSeuillage processeurSeuillage;
	private final static int TAILLE_FENETRE_DEFAUT = 250;

	/**
	 * @brief Constructeur initialisant les composants nécessaires au débruitage.
	 * @author Paul
	 */
	public DebruiteurImage() {
		this.gestionnairePatchs = new GestionnairePatchs();
		this.processeurACP = new ProcesseurACP();
		this.processeurSeuillage = new ProcesseurSeuillage();
	}

	/**
	 * @brief Effectue le débruitage global sur l'image entière.
	 * @author Paul & Emma
	 * @param xB                Image bruitée d'entrée.
	 * @param typeSeuil         Type de seuillage ("VisuShrink" ou "BayesShrink").
	 * @param fonctionSeuillage Fonction de seuillage ("Dur" ou "Doux").
	 * @param sigma             Écart-type estimé du bruit.
	 * @param taillePatch       Taille des patchs pour l'analyse.
	 * @return Image débruitée après traitement global.
	 */
	private Img debruiterGlobal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {

		System.out.println("📊 MODE GLOBAL - Traitement de l'image entière");

		// Extraction des patchs et leur transformation en vecteurs
		System.out.println("⏳ Étape 1/8 : Extraction des patchs...");
		ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(xB, taillePatch);
		System.out.println("✅ Extraction de " + resPatchs.taille() + " patchs réussie");

		List<Position> positionsOriginales = new ArrayList<>();
		for (PairePatchPosition p : resPatchs) {
			positionsOriginales.add(p.getPosition());
		}

		System.out.println("⏳ Étape 2/8 : Vectorisation des patchs...");
		ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);
		System.out.println("✅ Vectorisation réussie");

		try {
			// Étape 1: Calcul de l'ACP
			System.out.println("⏳ Étape 3/8 : Calcul de l'ACP...");
			ResultatACP resACP = processeurACP.acp(resVecteurs);
			System.out.println("✅ Analyse en composantes principales réussie");

			// Étape 2: Récupération des résultats MoyCov
			System.out.println("⏳ Étape 4/8 : Calcul des statistiques...");
			ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
			System.out.println("✅ Calcul des statistiques réussi");

			// Étape 3: Projection des vecteurs
			System.out.println("⏳ Étape 5/8 : Projection des vecteurs...");
			ResultatVecteur vecteursPropresRV = gestionnairePatchs
					.matriceToResultatVecteur(resACP.getVecteursPropres());

			ResultatVecteur vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
			System.out.println("✅ Projection réussie");

			// Étape 4: Récupération des positions
			ResultatVecteur vecteursProjRV = new ResultatVecteur();
			for (int i = 0; i < vecteursProj.taille(); i++) {
				// Utiliser les positions originales au lieu des positions de resMoyCov
				Position position = i < positionsOriginales.size() ? positionsOriginales.get(i) : new Position(0, 0);
				vecteursProjRV.ajouterVecteur(vecteursProj.getVecteurs().get(i), position);
			}

			// Étape 5: Seuillage des coefficients
			System.out.println("⏳ Étape 6/8 : Seuillage des coefficients...");
			ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProjRV, typeSeuil, fonctionSeuillage,
					sigma, xB, resMoyCov.getMatriceCovariance());
			System.out.println("✅ Seuillage réussi");

			// Étape 6: Reconstruction
			System.out.println("⏳ Étape 7/8 : Reconstruction des vecteurs...");
			ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(vecteursSeuil,
					resACP.getVecteursPropres(), resACP.getVecteurMoyen());
			System.out.println("✅ Reconstruction des vecteurs réussie");

			// Étape 7: Conversion en patchs
			System.out.println("⏳ Étape 8/8 : Reconstruction de l'image...");
			ResultatPatch patchsReconstruits = gestionnairePatchs
					.transformerVecteursEnResultatPatch(vecteursReconstruits);

			// Étape 8: Reconstruction finale
			Img imgReconstruite = gestionnairePatchs.reconstructionPatchs(patchsReconstruits, xB.getHauteur(),
					xB.getLargeur(), xB);
			System.out.println("\n🎉 DÉBRUITAGE GLOBAL TERMINÉ AVEC SUCCÈS 🎉");

			return imgReconstruite;
		} catch (Exception e) {
			System.err.println("ERREUR lors du débruitage: " + e.getMessage());
			e.printStackTrace();
			return xB; // En cas d'erreur, retourne l'image bruitée
		}
	}

	/**
	 * @brief Effectue le débruitage local en traitant l'image par fenêtres.
	 * @author Emma & Paul
	 * @param xB                Image bruitée d'entrée.
	 * @param typeSeuil         Type de seuillage ("VisuShrink" ou "BayesShrink").
	 * @param fonctionSeuillage Fonction de seuillage ("Dur" ou "Doux").
	 * @param sigma             Écart-type estimé du bruit.
	 * @param taillePatch       Taille des patchs pour l'analyse.
	 * @return Image débruitée après traitement local par fenêtres.
	 */
	private Img debruiterLocal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {

		System.out.println("🧩 MODE LOCAL - Traitement par fenêtres");
		System.out.println("⏳ Étape 1/5 : Calcul des paramètres de fenêtrage...");
		// Calcul des paramètres de fenêtrage
		ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(xB.getLargeur(), xB.getHauteur(),
				TAILLE_FENETRE_DEFAUT);

		System.out.println("\n ⚙️  Paramètres de fenêtrage: ⚙️ ");
		System.out.println("  Dimensions de l'image: " + xB.getLargeur() + "×" + xB.getHauteur());
		System.out.println("  Taille de fenêtre: " + pF.getTailleFenetreCalculee());
		System.out.println("  Grille: " + pF.getNombreFenetresX() + "×" + pF.getNombreFenetresY() + " fenêtres");
		System.out.println(
				"  Chevauchement: X=" + pF.getChevauchementCombineX() + ", Y=" + pF.getChevauchementCombineY());

		System.out.println("⏳ Étape 2/5 : Découpage de l'image en fenêtres...");
		// Découpage de l'image en fenêtres
		List<Fenetre> imagettesList = gestionnairePatchs.decoupageImage(xB, pF);
		System.out.println("✅ Découpage en " + imagettesList.size() + " fenêtres réussi");

		System.out.println("⏳ Étape 3/5 : Initialisation de l'image résultat...");
		// Initialisation de l'image résultat
		Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
		for (int i = 0; i < xB.getHauteur(); i++) {
			for (int j = 0; j < xB.getLargeur(); j++) {
				xRPixels[i][j] = new Pixel(0.0);

			}
		}
		System.out.println("✅ Initialisation réussie");

		// Compteur de progression
		int fenetreTraitee = 0;
		System.out.println("⏳ Étape 4/5 : Traitement de chaque fenêtre...");
		// Traitement de chaque fenêtre
		for (Fenetre f : imagettesList) {
			fenetreTraitee++;

			// Extraction des patchs et conversion en vecteurs
			ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);

			List<Position> positionsOriginales = new ArrayList<>();
			for (PairePatchPosition p : resPatchs) {
				positionsOriginales.add(p.getPosition());
			}

			ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);

			try {
				// Analyse ACP sur la fenêtre
				ResultatACP resACP = processeurACP.acp(resVecteurs);
				ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);

				// Conversion des vecteurs propres
				ResultatVecteur vecteursPropresRV = gestionnairePatchs
						.matriceToResultatVecteur(resACP.getVecteursPropres());

				// Projection, seuillage et reconstruction
				ResultatVecteur vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());

				ResultatVecteur vecteursProjRV = new ResultatVecteur();
				for (int i = 0; i < vecteursProj.taille(); i++) {
					Position position = i < positionsOriginales.size() ? positionsOriginales.get(i)
							: new Position(0, 0);
					vecteursProjRV.ajouterVecteur(vecteursProj.getVecteurs().get(i), position);
				}

				ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProjRV, typeSeuil,
						fonctionSeuillage, sigma, f.getImage(), resMoyCov.getMatriceCovariance());

				ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(vecteursSeuil,
						resACP.getVecteursPropres(), resACP.getVecteurMoyen());

				ResultatPatch patchsReconstruits = gestionnairePatchs
						.transformerVecteursEnResultatPatch(vecteursReconstruits);

				// Reconstruction de l'image pour cette fenêtre
				Img nfImg = gestionnairePatchs.reconstructionPatchs(patchsReconstruits, f.getImage().getHauteur(),
						f.getImage().getLargeur(), xB);

				// Fusion des résultats dans l'image globale
				Pixel[][] nfPixels = nfImg.getPixels();

				for (int i = 0; i < nfPixels.length; i++) {
					for (int j = 0; j < nfPixels[0].length; j++) {
						int posY = i + f.getPosition().getI();
						int posX = j + f.getPosition().getJ();

						if (posY < xB.getHauteur() && posX < xB.getLargeur()) {
							Pixel pixelGlobal = xRPixels[posY][posX];
							pixelGlobal.setValeur(pixelGlobal.getValeur() + nfPixels[i][j].getValeur());
							pixelGlobal.setNbChevauchement(pixelGlobal.getNbChevauchement() + 1);
						}
					}
				}

			} catch (Exception e) {
				System.err
						.println("  ERREUR lors du traitement de la fenêtre " + fenetreTraitee + ": " + e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println("\n✅ Traitement de toutes les fenêtres réussi");

		System.out.println("⏳ Étape 5/5 : Normalisation et finalisation...");

		for (int i = 0; i < xRPixels.length; i++) {
			for (int j = 0; j < xRPixels[0].length; j++) {
				if (xRPixels[i][j].getNbChevauchement() > 0) {
					double valeurNormalisee = xRPixels[i][j].getValeur() / (double) xRPixels[i][j].getNbChevauchement();
					int valeurFinale = (int) Math.min(255, Math.max(0, Math.round(valeurNormalisee)));
					xRPixels[i][j].setValeur(valeurFinale);
				} else {
					xRPixels[i][j].setValeur(xB.getPixel(i, j).getValeur());
				}
			}
		}

		System.out.println("✅ Normalisation réussie");
		System.out.println("\n🎉 DÉBRUITAGE LOCAL TERMINÉ AVEC SUCCÈS 🎉");

		return new Img(xRPixels, xB.isEstRGB());
	}

	
	/**
	 * @brief Effectue le débruitage d'une image RGB en traitant chaque canal séparément.
	 * @author Paul
	 * @param xB Image RGB bruitée
	 * @param typeSeuil Type de seuillage
	 * @param fonctionSeuillage Fonction de seuillage
	 * @param sigma Écart-type du bruit
	 * @param taillePatch Taille des patchs
	 * @param modeLocal Mode de traitement (local ou global)
	 * @return Image RGB débruitée
	 */
	private Img debruiterRGB(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch, boolean modeLocal) {
	    System.out.println("🌈 DÉBRUITAGE RGB - Traitement par canaux séparés");
	    
	    // Séparer les canaux RGB
	    System.out.println("⏳ Séparation des canaux RGB...");
	    Img canalR = extraireCanal(xB, 0); // Canal Rouge
	    Img canalG = extraireCanal(xB, 1); // Canal Vert  
	    Img canalB = extraireCanal(xB, 2); // Canal Bleu
	    System.out.println("✅ Séparation réussie");
	    
	    // Débruiter chaque canal indépendamment
	    System.out.println("🔴 Débruitage du canal Rouge...");
	    Img canalR_debruite = modeLocal ? 
	        debruiterLocal(canalR, typeSeuil, fonctionSeuillage, sigma, taillePatch) :
	        debruiterGlobal(canalR, typeSeuil, fonctionSeuillage, sigma, taillePatch);
	    
	    System.out.println("🟢 Débruitage du canal Vert...");
	    Img canalG_debruite = modeLocal ? 
	        debruiterLocal(canalG, typeSeuil, fonctionSeuillage, sigma, taillePatch) :
	        debruiterGlobal(canalG, typeSeuil, fonctionSeuillage, sigma, taillePatch);
	    
	    System.out.println("🔵 Débruitage du canal Bleu...");
	    Img canalB_debruite = modeLocal ? 
	        debruiterLocal(canalB, typeSeuil, fonctionSeuillage, sigma, taillePatch) :
	        debruiterGlobal(canalB, typeSeuil, fonctionSeuillage, sigma, taillePatch);
	    
	    // Recombiner les canaux
	    System.out.println("⏳ Recombinaison des canaux RGB...");
	    Img imageRecombinee = combinerCanaux(canalR_debruite, canalG_debruite, canalB_debruite);
	    System.out.println("✅ Recombinaison réussie");
	    
	    System.out.println("🎉 DÉBRUITAGE RGB TERMINÉ AVEC SUCCÈS 🎉");
	    return imageRecombinee;
	}
	
	
	/**
	 * @brief Extrait un canal spécifique d'une image RGB et le convertit en image niveaux de gris.
	 * @author Paul
	 * @param imageRGB Image RGB source
	 * @param canal Index du canal (0=Rouge, 1=Vert, 2=Bleu)
	 * @return Image en niveaux de gris correspondant au canal
	 */
	private Img extraireCanal(Img imageRGB, int canal) {
	    int hauteur = imageRGB.getHauteur();
	    int largeur = imageRGB.getLargeur();
	    Pixel[][] pixelsCanal = new Pixel[hauteur][largeur];
	    
	    for (int i = 0; i < hauteur; i++) {
	        for (int j = 0; j < largeur; j++) {
	            double valeurCanal = imageRGB.getPixel(i, j).getValeur(canal);
	            pixelsCanal[i][j] = new Pixel(valeurCanal);
	        }
	    }
	    
	    return new Img(pixelsCanal, false); // false = niveaux de gris
	}

	/**
	 * @brief Combine trois canaux en niveaux de gris en une image RGB.
	 * @author Paul
	 * @param canalR Canal rouge (image niveaux de gris)
	 * @param canalG Canal vert (image niveaux de gris) 
	 * @param canalB Canal bleu (image niveaux de gris)
	 * @return Image RGB combinée
	 */
	private Img combinerCanaux(Img canalR, Img canalG, Img canalB) {
	    int hauteur = canalR.getHauteur();
	    int largeur = canalR.getLargeur();
	    Pixel[][] pixelsRGB = new Pixel[hauteur][largeur];
	    
	    for (int i = 0; i < hauteur; i++) {
	        for (int j = 0; j < largeur; j++) {
	            double r = canalR.getPixel(i, j).getValeur();
	            double g = canalG.getPixel(i, j).getValeur();
	            double b = canalB.getPixel(i, j).getValeur();
	            
	            pixelsRGB[i][j] = new Pixel(r, g, b);
	        }
	    }
	    
	    return new Img(pixelsRGB, true); // true = RGB
	}
	
	/**
	 * @brief Méthode principale pour débruiter une image via ACP + seuillage.
	 * @author Paul & Emma
	 * @param xB                Image bruitée à débruiter.
	 * @param typeSeuil         Type de seuillage ("VisuShrink" ou "BayesShrink").
	 * @param fonctionSeuillage Fonction de seuillage ("Dur" ou "Doux").
	 * @param sigma             Écart-type estimé du bruit.
	 * @param taillePatch       Taille des patchs pour le traitement.
	 * @param modeLocal         Si vrai, applique un traitement local par fenêtres,
	 *                          sinon global.
	 * @return Image débruitée.
	 */
	public Img imageDen(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch,
			boolean modeLocal) {
		System.out.println("\n🔍 DÉMARRAGE DU DÉBRUITAGE D'IMAGE 🔍");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("🛠️  Mode: " + (modeLocal ? "LOCAL" : "GLOBAL"));
		System.out.println("🛠️  Type de seuil: " + typeSeuil);
		System.out.println("🛠️  Fonction: " + fonctionSeuillage);
		System.out.println("🛠️  Sigma: " + sigma);
		System.out.println("🛠️  Taille des patchs: " + taillePatch + "×" + taillePatch);
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

		  if (xB.isEstRGB()) {
			  	System.out.println("DEBUG RGB CHOSI");
		        return debruiterRGB(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch, modeLocal);
		    } else {
		        return modeLocal ? 
		            debruiterLocal(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch) :
		            debruiterGlobal(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch);
		    }
		  
	}

}
