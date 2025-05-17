package service.patch;

import java.util.ArrayList;
import java.util.Collections;
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

    int h = imgPixels.length;
    int w = imgPixels[0].length;

    // Calculer le pas entre deux patchs adjacents
    int pas = Math.max(1, s / recouvrement);
    
    System.out.println("Extraction avec pas = " + pas + " pour taille patch = " + s);
    
    // Afficher les dimensions et paramètres d'extraction
    System.out.println("Dimensions de l'image: " + w + "×" + h);
    System.out.println("Dimension des patchs: " + s + "×" + s);
    System.out.println("Pas d'échantillonnage: " + pas);
    System.out.println("Positions d'extraction attendues: " + ((w-s)/pas + 1) * ((h-s)/pas + 1));

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
    
    // Validation: vérifier les positions extraites
    int totalPositions = 0;
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (positionsExtraites[i][j]) {
                totalPositions++;
            }
        }
    }
    
    System.out.println("Positions de patchs extraites: " + totalPositions);
    System.out.println("Nombre de patchs extraits: " + resPatch.taille());
    
    // Vérifier la couverture réelle
    verifierCouverture(resPatch, h, w, s);
    
    return resPatch;
}

// Méthode auxiliaire pour extraire un patch
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
private void verifierCouverture(ResultatPatch patchs, int h, int w, int s) {
    boolean[][] couverture = new boolean[h][w];
    
    // Pour chaque patch, marquer ses pixels comme couverts
    for (PairePatchPosition p : patchs) {
        Position pos = p.getPosition();
        Patch patch = p.getPatch();
        
        if (pos == null) {
            System.err.println("ERREUR: Position null détectée dans un patch");
            continue;
        }
        
        // Marquer chaque pixel du patch
        for (int i = 0; i < patch.getTaille(); i++) {
            for (int j = 0; j < patch.getTaille(); j++) {
                int y = pos.getI() + i;
                int x = pos.getJ() + j;
                
                if (y < h && x < w) {
                    couverture[y][x] = true;
                }
            }
        }
    }
    
    // Compter les pixels couverts
    int pixelsCouverts = 0;
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            if (couverture[i][j]) {
                pixelsCouverts++;
            }
        }
    }
    
    System.out.println("Vérification de la couverture:");
    System.out.println("Pixels couverts: " + pixelsCouverts + "/" + (h*w) + 
                       " (" + String.format("%.2f", 100.0*pixelsCouverts/(h*w)) + "%)");
    
    // Visualiser la couverture par blocs
    int blockSize = 50;
    for (int bi = 0; bi < h; bi += blockSize) {
        for (int bj = 0; bj < w; bj += blockSize) {
            int nonCouverts = 0;
            int total = 0;
            
            for (int i = bi; i < Math.min(bi + blockSize, h); i++) {
                for (int j = bj; j < Math.min(bj + blockSize, w); j++) {
                    total++;
                    if (!couverture[i][j]) nonCouverts++;
                }
            }
            
            if (nonCouverts > 0) {
                System.out.println("  Bloc (" + bi + "," + bj + "): " + nonCouverts + "/" + total + " pixels non couverts");
            }
        }
    }
}

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

	
    public ResultatPatch extractPatchs(Img Xs, int s) {
        // Utiliser un recouvrement de 2 (50%) par défaut
        return extractPatchsAvecRecouvrement(Xs, s, 2);
    }
//
//    public Img reconstructionPatchs(ResultatPatch yPatchs, int l, int c, Img xB) {
//        double[][] sommePixels = new double[l][c];
//        int[][] compteur = new int[l][c];
//
//        for (PairePatchPosition p : yPatchs) {
//            Position pPosition = p.getPosition();
//            Patch pPatch = p.getPatch();
//
//            for (int i = 0; i < pPatch.getTaille(); i++) {
//                for (int j = 0; j < pPatch.getTaille(); j++) {
//                    int posY = pPosition.getI() + i;
//                    int posX = pPosition.getJ() + j;
//
//                    if (posY < l && posX < c) {
//                        double valPatch = pPatch.getPixels()[i][j].getValeur();
//                        valPatch = Math.min(240, Math.max(15, valPatch));
//                        sommePixels[posY][posX] += valPatch;
//                        compteur[posY][posX]++;
//                    }
//                }
//            }
//        }
//
//        Pixel[][] imgReconstitueePixels = new Pixel[l][c];
//        for (int i = 0; i < l; i++) {
//            for (int j = 0; j < c; j++) {
//                double val;
//                if (compteur[i][j] > 0) {
//                    val = (int) Math.round(sommePixels[i][j] / compteur[i][j]);
//                } else {
//                    val = xB.getPixel(i, j).getValeur();  // fallback si jamais couvert
//                	//val = 255;
//                }
//                imgReconstitueePixels[i][j] = new Pixel(val);
//            }
//        }
//
//        return new Img(imgReconstitueePixels);
//    }

public Img reconstructionPatchs(ResultatPatch yPatchs, int l, int c, Img xB) {
    System.out.println("Reconstruction de l'image à partir des patchs");
    System.out.println("  - Dimensions cibles: " + l + "×" + c);
    System.out.println("  - Nombre de patchs: " + yPatchs.taille());
    
    if (yPatchs.taille() == 0) {
        System.err.println("ATTENTION: Aucun patch à reconstruire!");
        return xB;
    }
    
    // Dimension des matrices pour l'image reconstruite
    double[][] sommePixels = new double[l][c];
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
    
    System.out.println("  - Taille des patchs: " + taillePatch + "×" + taillePatch);
    
    // Statistiques pour le debugging
    int patchsTraites = 0;
    int pixelsPlaces = 0;
    double valeurMin = Double.MAX_VALUE;
    double valeurMax = Double.MIN_VALUE;
    
    // Matrice pour vérifier la couverture
    boolean[][] pixelsCovered = new boolean[l][c];
    
    // Pour chaque patch
    for (PairePatchPosition p : yPatchs) {
        patchsTraites++;
        Position pPosition = p.getPosition();
        Patch pPatch = p.getPatch();
        
        // Déboguer les 50 premiers patchs
        if (patchsTraites <= 50) {
            System.out.println("DEBUG: Patch at position (" + pPosition.getI() + "," + pPosition.getJ() + ")");
        }
        
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
                    
                    // Mettre à jour les statistiques
                    valeurMin = Math.min(valeurMin, valeur);
                    valeurMax = Math.max(valeurMax, valeur);
                    
                    // Accumuler la valeur et incrémenter le compteur
                    sommePixels[posY][posX] += valeur;
                    compteur[posY][posX]++;
                    pixelsCovered[posY][posX] = true;
                    pixelsPlaces++;
                }
            }
        }
    }
    
    // Compter les pixels couverts
    int pixelsCouverts = 0;
    for (int i = 0; i < l; i++) {
        for (int j = 0; j < c; j++) {
            if (pixelsCovered[i][j]) {
                pixelsCouverts++;
            }
        }
    }
    
    System.out.println("  - Patchs traités: " + patchsTraites);
    System.out.println("  - Pixels placés: " + pixelsPlaces);
    System.out.println("  - Pixels couverts: " + pixelsCouverts + "/" + (l*c) + 
                      " (" + String.format("%.2f", 100.0*pixelsCouverts/(l*c)) + "%)");
    System.out.println("  - Valeur minimale: " + valeurMin);
    System.out.println("  - Valeur maximale: " + valeurMax);
    
    // Créer l'image finale en moyennant les valeurs
    Pixel[][] imgReconstitueePixels = new Pixel[l][c];
    int pixelsNonCouverts = 0;
    
    for (int i = 0; i < l; i++) {
        for (int j = 0; j < c; j++) {
            if (compteur[i][j] > 0) {
                // Calculer la moyenne et limiter aux valeurs valides
                double valeur = sommePixels[i][j] / compteur[i][j];
                valeur = Math.min(255, Math.max(0, Math.round(valeur)));
                imgReconstitueePixels[i][j] = new Pixel(valeur);
            } else {
                // Pour les pixels non couverts, utiliser l'image originale
                pixelsNonCouverts++;
                imgReconstitueePixels[i][j] = new Pixel(xB.getPixel(i, j).getValeur());
            }
        }
    }
    
    // Analyser la couverture par zones de 50×50 pixels
    analyserCouverture(pixelsCovered, l, c);
    
    System.out.println("Reconstruction terminée avec succès");
    return new Img(imgReconstitueePixels);
}

// Méthode pour analyser la couverture par zones
private void analyserCouverture(boolean[][] pixelsCovered, int h, int w) {
    int zoneSize = 50;
    int zonesToShow = 10; // Limit the number of zones in the output
    
    System.out.println("\nAnalyse de la couverture par zones de " + zoneSize + "×" + zoneSize + " pixels:");
    System.out.println("Format: Zone(i,j): couverture moyenne (pixels couverts / total)");
    
    for (int zi = 0; zi < Math.min((h + zoneSize - 1) / zoneSize, zonesToShow); zi++) {
        for (int zj = 0; zj < Math.min((w + zoneSize - 1) / zoneSize, zonesToShow); zj++) {
            int startI = zi * zoneSize;
            int endI = Math.min(startI + zoneSize, h);
            int startJ = zj * zoneSize;
            int endJ = Math.min(startJ + zoneSize, w);
            
            int coveredPixels = 0;
            int totalPixels = 0;
            
            for (int i = startI; i < endI; i++) {
                for (int j = startJ; j < endJ; j++) {
                    if (pixelsCovered[i][j]) {
                        coveredPixels++;
                    }
                    totalPixels++;
                }
            }
            
            double coverage = 100.0 * coveredPixels / totalPixels;
            System.out.println("Zone(" + zi + "," + zj + "): " + 
                               String.format("%.2f", coverage) + "% (" + 
                               coveredPixels + "/" + totalPixels + ")");
        }
    }
    
    System.out.println();
}
// Méthode auxiliaire pour analyser la couverture des patchs
private void analyserCouverture(double[][] densityMap, int height, int width) {
    // Analyser par zones de 50x50 pixels
    int zoneSize = 50;
    int zonesH = (height + zoneSize - 1) / zoneSize;
    int zonesW = (width + zoneSize - 1) / zoneSize;
    
    System.out.println("\nAnalyse de la couverture par zones de " + zoneSize + "x" + zoneSize + " pixels:");
    System.out.println("Format: Zone(i,j): couverture moyenne (pixels couverts / total)");
    
    for (int zi = 0; zi < zonesH; zi++) {
        for (int zj = 0; zj < zonesW; zj++) {
            int startI = zi * zoneSize;
            int endI = Math.min(startI + zoneSize, height);
            int startJ = zj * zoneSize;
            int endJ = Math.min(startJ + zoneSize, width);
            
            double totalDensity = 0.0;
            int coveredPixels = 0;
            int totalPixels = 0;
            
            for (int i = startI; i < endI; i++) {
                for (int j = startJ; j < endJ; j++) {
                    totalDensity += densityMap[i][j];
                    if (densityMap[i][j] > 0.0) {
                        coveredPixels++;
                    }
                    totalPixels++;
                }
            }
            
            double avgDensity = totalDensity / totalPixels;
            System.out.println("Zone(" + zi + "," + zj + "): " + 
                               String.format("%.2f", avgDensity * 100) + "% (" + 
                               coveredPixels + "/" + totalPixels + ")");
        }
    }
    
    System.out.println();
}
    public ResultatVecteur vectorPatchs(ResultatPatch yPatchs) {
        ResultatVecteur resVect = new ResultatVecteur();
        
        System.out.println("DEBUG vectorPatchs: nombre de patchs = " + yPatchs.taille());
        
        // Crée un tableau pour visualiser les positions utilisées
        int[][] positionsMap = new int[512][512]; // Ajuster la taille selon votre image
        
        for(PairePatchPosition p : yPatchs) {
            Position pPosition = p.getPosition();
            Patch pPatch = p.getPatch();
            double[] valeurs = new double[pPatch.getTaille()*pPatch.getTaille()];
            int pos = 0;
            
            // Déboguer les positions
            if (pPosition != null) {
//                System.out.println("  Position patch: (" + pPosition.getI() + "," + pPosition.getJ() + ")");
                
                // Marquer cette position sur notre carte
                positionsMap[pPosition.getI()][pPosition.getJ()]++;
                
                for(int x = 0; x < pPatch.getTaille(); x++) {
                    for(int y = 0; y < pPatch.getTaille(); y++) {
                        valeurs[pos] = pPatch.getPixels()[x][y].getValeur();
                        pos++;
                    }
                }
                resVect.ajouterVecteur(new Vecteur(valeurs), pPosition);
            } else {
                System.err.println("ERREUR: Position de patch null!");
            }
        }
        
        // Vérifier la distribution des positions
        int couvertes = 0;
        for (int i = 0; i < positionsMap.length; i++) {
            for (int j = 0; j < positionsMap[0].length; j++) {
                if (positionsMap[i][j] > 0) couvertes++;
            }
        }
        System.out.println("  Positions de patch distinctes: " + couvertes);
        
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

// 	public ResultatPatch transformerVecteursEnResultatPatch(ResultatVecteur vecteursReconstruits) {
// 	    ResultatPatch resultatPatch = new ResultatPatch();
// 	    int taillePatch = (int) Math.sqrt(vecteursReconstruits.getVecteurs().get(0).taille());
//
// 	    for (int k = 0; k < vecteursReconstruits.taille(); k++) {
// 	        double[] valeurs = vecteursReconstruits.getVecteurs().get(k).getValeurs();
// 	        Pixel[][] patchPixels = new Pixel[taillePatch][taillePatch];
//
// 	        for (int i = 0; i < taillePatch; i++) {
// 	            for (int j = 0; j < taillePatch; j++) {
// 	                int index = i * taillePatch + j;
// 	                int valeur = (int) Math.min(255, Math.max(0, Math.round(valeurs[index])));
// 	                patchPixels[i][j] = new Pixel(valeur);
// 	            }
// 	        }
//
// 	        Patch patch = new Patch(patchPixels);
// 	        resultatPatch.ajouterPatch(patch, vecteursReconstruits.getPositions().get(k));
// 	    }
//
// 	    return resultatPatch;
// 	}

	public ResultatPatch transformerVecteursEnResultatPatch(ResultatVecteur vecteursReconstruits) {
    System.out.println("Transformation des vecteurs en patchs");
    System.out.println("  - Nombre de vecteurs: " + vecteursReconstruits.taille());
    
    ResultatPatch resultatPatch = new ResultatPatch();
    
    if (vecteursReconstruits.taille() == 0) {
        System.err.println("ATTENTION: Aucun vecteur à transformer!");
        return resultatPatch;
    }
    
    int taillePatch = (int) Math.sqrt(vecteursReconstruits.getVecteurs().get(0).taille());
    System.out.println("  - Taille de patch calculée: " + taillePatch + "×" + taillePatch);

    // Vérifier combien de positions sont nulles
    int positionsNulles = 0;
    for (int k = 0; k < vecteursReconstruits.taille(); k++) {
        if (vecteursReconstruits.getPositions().get(k) == null) {
            positionsNulles++;
        }
    }
    
    if (positionsNulles > 0) {
        System.err.println("ATTENTION: " + positionsNulles + " positions nulles détectées dans les vecteurs!");
    }

    try {
        for (int k = 0; k < vecteursReconstruits.taille(); k++) {
            double[] valeurs = vecteursReconstruits.getVecteurs().get(k).getValeurs();
            Position position = vecteursReconstruits.getPositions().get(k);
//            System.out.println("DEBUG Reconstruct :"+  position.getI() + " - "+ position.getJ());

        
            
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
        
        System.out.println("Transformation terminée - " + resultatPatch.taille() + " patchs créés");
        return resultatPatch;
        
    } catch (Exception e) {
        System.err.println("ERREUR lors de la transformation en patchs: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
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
