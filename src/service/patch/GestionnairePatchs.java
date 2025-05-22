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

/**
 * @class GestionnairePatchs
 * @brief Gère l'extraction, la transformation et la reconstruction des patchs
 *        d'une image.
 * @author Paul & Alexis & Emma
 */
public class GestionnairePatchs {

	/**
	 * @brief Extrait des patchs de l'image avec une option de recouvrement.
	 * @author Emma
	 * @param Xs           Image source
	 * @param s            Taille des patchs
	 * @param recouvrement Niveau de recouvrement
	 * @return ResultatPatch contenant les patchs extraits et leurs positions
	 */
	public ResultatPatch extractPatchsAvecRecouvrement(Img Xs, int s, int recouvrement) {
		Pixel[][] imgPixels = Xs.getPixels();
		ResultatPatch resPatch = new ResultatPatch();

		int h = imgPixels.length;
		int w = imgPixels[0].length;

		// Calculer le pas entre deux patchs adjacents
		int pas = Math.max(1, s / recouvrement);

		// Créer une matrice pour suivre les positions extraites
		boolean[][] positionsExtraites = new boolean[h][w];

		// Pour chaque position possible de patch selon le pas
		for (int i = 0; i <= h - s; i += pas) {
			for (int j = 0; j <= w - s; j += pas) {
				// Marquer cette position
				positionsExtraites[i][j] = true;

				// Créer et ajouter le patch
				Patch patch = extrairePatch(imgPixels, i, j, s);

				resPatch.ajouterPatch(patch, new Position(i, j));

			}
		}

		// Couvrir les bordures si nécessaire
		if ((h - s) % pas != 0) {
			int i = h - s;
			for (int j = 0; j <= w - s; j += pas) {
				positionsExtraites[i][j] = true;
				Patch patch = extrairePatch(imgPixels, i, j, s);
				resPatch.ajouterPatch(patch, new Position(i, j));
			}
		}

		if ((w - s) % pas != 0) {
			int j = w - s;
			for (int i = 0; i <= h - s; i += pas) {
				positionsExtraites[i][j] = true;
				Patch patch = extrairePatch(imgPixels, i, j, s);
				resPatch.ajouterPatch(patch, new Position(i, j));
			}
		}

		// Coin inférieur droit
		if ((h - s) % pas != 0 && (w - s) % pas != 0) {
			positionsExtraites[h - s][w - s] = true;
			Patch patch = extrairePatch(imgPixels, h - s, w - s, s);
			resPatch.ajouterPatch(patch, new Position(h - s, w - s));
		}

		return resPatch;
	}

	/**
	 * @brief Méthode auxiliaire privée pour extraire un patch de taille s à partir
	 *        d'une position.
	 * @author Paul & Alexis
	 * @param imgPixels Matrice de pixels de l'image.
	 * @param i         Coordonnée verticale du patch.
	 * @param j         Coordonnée horizontale du patch.
	 * @param s         Taille du patch.
	 * @return Patch extrait.
	 */
	private Patch extrairePatch(Pixel[][] imgPixels, int i, int j, int s) {
		Pixel[][] patchPixels = new Pixel[s][s];
		for (int x = 0; x < s; x++) {
			for (int y = 0; y < s; y++) {
				patchPixels[x][y] = imgPixels[i + x][j + y];
			}
		}
		return new Patch(patchPixels);
	}

	// Méthode pour vérifier la couverture spatiale

	private void ajouterPatch(ResultatPatch resPatch, Pixel[][] imgPixels, int i, int j, int s) {
		Pixel[][] patchPixels = new Pixel[s][s];
		for (int x = 0; x < s; x++) {
			for (int y = 0; y < s; y++) {
				patchPixels[x][y] = imgPixels[i + x][j + y];
			}
		}
		Patch patch = new Patch(patchPixels);
		resPatch.ajouterPatch(patch, new Position(i, j));
	}

	/**
	 * @brief Extrait des patchs avec un recouvrement par défaut de 50%.
	 * @author Emma
	 * @param Xs Image source.
	 * @param s  Taille des patchs.
	 * @return ResultatPatch contenant les patchs extraits.
	 */
	public ResultatPatch extractPatchs(Img Xs, int s) {
		// Utiliser un recouvrement de 2 (50%) par défaut
		return extractPatchsAvecRecouvrement(Xs, s, 2);
	}

	/**
	 * @brief Reconstruit une image à partir d'un ensemble de patchs.
	 * @author Paul & Alexis
	 * @param yPatchs Ensemble des patchs à reconstituer.
	 * @param l       Hauteur de l'image finale.
	 * @param c       Largeur de l'image finale.
	 * @param xB      Image bruitée originale pour les pixels non couverts.
	 * @return Image reconstruite.
	 */
	public Img reconstructionPatchs(ResultatPatch yPatchs, int l, int c, Img xB) {

		// Dimension des matrices pour l'image reconstruite
		Pixel[][] imgReconstitueePixels = new Pixel[l][c];
		double[][][] sommePixels = new double[l][c][3];
		int[][] compteur = new int[l][c];

		// Obtenir la taille des patchs
		int taillePatch = -1;
		for (PairePatchPosition p : yPatchs) {
			taillePatch = p.getPatch().getTaille();
			break;
		}

		if (taillePatch == -1) {
			System.err.println("Erreur: impossible de déterminer la taille des patchs");
			return xB;
		}

		// Matrice pour vérifier la couverture
		boolean[][] pixelsCovered = new boolean[l][c];

		if (xB.isEstRGB()) {

			// Pour chaque patch
			for (PairePatchPosition p : yPatchs) {
				Position pPosition = p.getPosition();
				Patch pPatch = p.getPatch();

				// Pour chaque pixel du patch
				for (int i = 0; i < pPatch.getTaille(); i++) {
					for (int j = 0; j < pPatch.getTaille(); j++) {
						int posY = pPosition.getI() + i;
						int posX = pPosition.getJ() + j;

						// Vérifier que le pixel est dans les limites de l'image
						if (posY < l && posX < c) {
							// Récupérer et cliper la valeur
							double[] valeurs = pPatch.getPixels()[i][j].getValeurs();
							for (int k = 0; k < 3; k++) {
								double valeur = Math.min(255, Math.max(0, valeurs[k]));
								sommePixels[posY][posX][k] += valeur;
							}

							// Accumuler la valeur et incrémenter le compteur
							compteur[posY][posX]++;
							pixelsCovered[posY][posX] = true;
						}
					}
				}
			}
			// Créer l'image finale en moyennant les valeurs
			for (int i = 0; i < l; i++) {
				for (int j = 0; j < c; j++) {
					if (compteur[i][j] > 0) {
						int[] valeursFinales = new int[3];
						// Calculer la moyenne et limiter aux valeurs valides
						for (int k = 0; k < 3; k++) {
							double valeur = sommePixels[i][j][k] / compteur[i][j];
							valeur = Math.min(255, Math.max(0, Math.round(valeur)));
							valeursFinales[k] = (int) valeur;
						}

					} else {
						// Pour les pixels non couverts, utiliser l'image originale
						imgReconstitueePixels[i][j] = new Pixel(xB.getPixel(i, j).getValeurs());
					}
				}
			}

		} else {
			// Pour chaque patch
			for (PairePatchPosition p : yPatchs) {
				Position pPosition = p.getPosition();
				Patch pPatch = p.getPatch();

				// Pour chaque pixel du patch
				for (int i = 0; i < pPatch.getTaille(); i++) {
					for (int j = 0; j < pPatch.getTaille(); j++) {
						int posY = pPosition.getI() + i;
						int posX = pPosition.getJ() + j;

						// Vérifier que le pixel est dans les limites de l'image
						if (posY < l && posX < c) {
							// Récupérer et cliper la valeur
							double valeur = pPatch.getPixels()[i][j].getValeur();
							valeur = Math.min(255, Math.max(0, valeur));

							// Accumuler la valeur et incrémenter le compteur
							sommePixels[posY][posX][0] += valeur;
							compteur[posY][posX]++;
							pixelsCovered[posY][posX] = true;
						}
					}
				}
			}
			// Créer l'image finale en moyennant les valeurs
			for (int i = 0; i < l; i++) {
				for (int j = 0; j < c; j++) {
					if (compteur[i][j] > 0) {
						// Calculer la moyenne et limiter aux valeurs valides
						double valeur = sommePixels[i][j][0] / compteur[i][j];
						valeur = Math.min(255, Math.max(0, Math.round(valeur)));
						imgReconstitueePixels[i][j] = new Pixel(valeur);
					} else {
						// Pour les pixels non couverts, utiliser l'image originale
						imgReconstitueePixels[i][j] = new Pixel(xB.getPixel(i, j).getValeur());
					}
				}
			}

		}

		return new Img(imgReconstitueePixels, xB.isEstRGB());
	}

	/**
	 * @brief Convertit un ResultatPatch en ResultatVecteur.
	 * @author Paul & Alexis
	 * @param yPatchs Ensemble des patchs à vectoriser.
	 * @return ResultatVecteur contenant les vecteurs associés aux positions.
	 */
	public ResultatVecteur vectorPatchs(ResultatPatch yPatchs) {
		ResultatVecteur resVect = new ResultatVecteur();

		for (PairePatchPosition p : yPatchs) {
			Position pPosition = p.getPosition();
			Patch pPatch = p.getPatch();
			if (pPosition != null) {
				double[] valeurs = new double[pPatch.getTaille() * pPatch.getTaille()];
				int pos = 0;

				for (int x = 0; x < pPatch.getTaille(); x++) {
					for (int y = 0; y < pPatch.getTaille(); y++) {
						valeurs[pos++] = pPatch.getPixels()[x][y].getValeur();
					}
				}
				resVect.ajouterVecteur(new Vecteur(valeurs), pPosition);
			}
		}

		return resVect;
	}

	/**
	 * @brief Découpe une image en fenêtres selon les paramètres spécifiés.
	 * @author Paul & Alexis
	 * @param x  Image à découper.
	 * @param pF Paramètres définissant la taille et le chevauchement des fenêtres.
	 * @return Liste des fenêtres extraites.
	 */
	public List<Fenetre> decoupageImage(Img x, ParametresFenetre pF) {
		List<Fenetre> fenetresList = new ArrayList<>();

		int tailleFenetre = pF.getTailleFenetreCalculee();
		int imgLargeur = x.getLargeur();
		int imgHauteur = x.getHauteur();

		int chevauchementX = (int) Math.ceil((double) pF.getChevauchementCombineX() / (pF.getNombreFenetresX() - 1));
		int chevauchementY = (int) Math.ceil((double) pF.getChevauchementCombineY() / (pF.getNombreFenetresY() - 1));

		int dernierePositionX = imgLargeur - tailleFenetre;
		int dernierePositionY = imgHauteur - tailleFenetre;

		for (int indexFenetreX = 0, chevauchementXAcc = 0; indexFenetreX < pF
				.getNombreFenetresX(); indexFenetreX++, chevauchementXAcc += chevauchementX) {
			int posX = (indexFenetreX + 1 == pF.getNombreFenetresX()) ? dernierePositionX
					: (tailleFenetre * indexFenetreX + 1) - chevauchementXAcc;

			// Sécurité sur les bornes
			if (posX < 0)
				posX = 0;
			if (posX + tailleFenetre > imgLargeur)
				continue;

			for (int indexFenetreY = 0, chevauchementYAcc = 0; indexFenetreY < pF
					.getNombreFenetresY(); indexFenetreY++, chevauchementYAcc += chevauchementY) {
				int posY = (indexFenetreY + 1 == pF.getNombreFenetresY()) ? dernierePositionY
						: (tailleFenetre * indexFenetreY + 1) - chevauchementYAcc;

				// Sécurité sur les bornes
				if (posY < 0)
					posY = 0;
				if (posY + tailleFenetre > imgHauteur)
					continue;

				Pixel[][] fPixels = new Pixel[tailleFenetre][tailleFenetre];
				for (int i = 0; i < tailleFenetre; i++) {
					for (int j = 0; j < tailleFenetre; j++) {
						Pixel px = x.getPixel(posY + i, posX + j);

						if (x.isEstRGB()) {
							// Pour les pixels RGB
							fPixels[i][j] = new Pixel(px.getValeur(0), px.getValeur(1), px.getValeur(2));
						} else {
							// Pour les pixels en niveaux de gris
							fPixels[i][j] = new Pixel(px.getValeur(0));
						}

						fPixels[i][j].setNbChevauchement(px.getNbChevauchement() + 1);
					}
				}

				Img fImage = new Img(fPixels, x.isEstRGB());
				Fenetre f = new Fenetre(fImage, new Position(posY, posX));
				fenetresList.add(f);
			}
		}

		return fenetresList;
	}

	/**
	 * @brief Transforme un ResultatVecteur en ResultatPatch.
	 * @author Emma
	 * @param vecteursReconstruits ResultatVecteur à transformer.
	 * @return ResultatPatch correspondant.
	 */
	public ResultatPatch transformerVecteursEnResultatPatch(ResultatVecteur vecteursReconstruits) {

		ResultatPatch resultatPatch = new ResultatPatch();

		int taillePatch = (int) Math.sqrt(vecteursReconstruits.getVecteurs().get(0).taille());

		try {
			for (int k = 0; k < vecteursReconstruits.taille(); k++) {
				double[] valeurs = vecteursReconstruits.getVecteurs().get(k).getValeurs();
				Position position = vecteursReconstruits.getPositions().get(k);

				
				Pixel[][] patchPixels = new Pixel[taillePatch][taillePatch];

				for (int i = 0; i < taillePatch; i++) {
					for (int j = 0; j < taillePatch; j++) {
						int index = i * taillePatch + j;
						int valeur = (int) Math.min(255, Math.max(0, Math.round(valeurs[index])));
						patchPixels[i][j] = new Pixel(valeur);
					}
				}

				Patch patch = new Patch(patchPixels);
				resultatPatch.ajouterPatch(patch, position);

			}
			return resultatPatch;

		} catch (Exception e) {
			System.err.println("ERREUR lors de la transformation en patchs: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @brief Convertit une matrice en ResultatVecteur.
	 * @author Emma
	 * @param matrice Matrice source.
	 * @return ResultatVecteur résultant.
	 */
	public ResultatVecteur matriceToResultatVecteur(Matrice matrice) {
		ResultatVecteur resultat = new ResultatVecteur();
		int nbColonnes = matrice.getNbColonnes();

		for (int j = 0; j < nbColonnes; j++) {
			double[] valeurs = new double[matrice.getNbLignes()];
			for (int i = 0; i < matrice.getNbLignes(); i++) {
				valeurs[i] = matrice.getValeur(i, j);
			}
			resultat.ajouterVecteur(new Vecteur(valeurs), null); // position peut être null ici
		}
		return resultat;
	}
}
