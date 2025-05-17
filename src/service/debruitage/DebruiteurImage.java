package service.debruitage;

import java.util.ArrayList;
import java.util.List;

import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;
import model.base.Img;
import model.base.Pixel;
import model.base.Position;
import model.base.Vecteur;
import model.patch.Fenetre;
import model.patch.ParametresFenetre;
import model.patch.ResultatPatch;
import model.patch.ResultatPatch.PairePatchPosition;
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
    private final static int TAILLE_FENETRE_DEFAUT = 250;

    public DebruiteurImage() {
        this.bruiteurImage = new BruiteurImage();
        this.gestionnairePatchs = new GestionnairePatchs();
        this.processeurACP = new ProcesseurACP();
        this.processeurSeuillage = new ProcesseurSeuillage();
        this.evaluationQualite = new EvaluationrQualite();
        
    }
    
//    /**
//     * Applique le débruitage sur l'image entière en une fois.
//     */
//    private Img debruiterGlobal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
//        // Extraction des patchs et leur transformation en vecteurs
//        ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(xB, taillePatch);
//        ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);
//
//        // Étape 1: Calcul de l'ACP
//        ResultatACP resACP = processeurACP.acp(resVecteurs);
//        
//        // Étape 2: Calcul de la moyenne et covariance (déjà fait dans acp, on récupère juste le résultat)
//        ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
//        
//        // Étape 3: Projection des vecteurs centrés sur les vecteurs propres
//        ResultatVecteur vecteursPropresRV = gestionnairePatchs.matriceToResultatVecteur(resACP.getVecteursPropres());
//        
//        // Projection des vecteurs centrés sur les vecteurs propres
//        List<Vecteur> vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
//        
//        // Étape 4: Conversion des résultats de projection en ResultatVecteur pour le seuillage
//        ResultatVecteur vecteursProjRV = new ResultatVecteur();
//        for (int i = 0; i < vecteursProj.size(); i++) {
//            vecteursProjRV.ajouterVecteur(vecteursProj.get(i), resMoyCov.getVecteursCenters().getPositions().get(i));
//        }
//        
//        // Étape 5: Seuillage des coefficients projetés
//        ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProjRV, typeSeuil, fonctionSeuillage, 
//                                                                    sigma, xB, resMoyCov.getMatriceCovariance());
//        
//        // Étape 6: Reconstruction des vecteurs à partir de leurs coefficients seuillés
//        ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(
//                vecteursSeuil, resACP.getVecteursPropres(), resACP.getVecteurMoyen());
//        
//        // Étape 7: Conversion des vecteurs reconstruits en patchs
//        ResultatPatch patchsReconstruits = gestionnairePatchs.transformerVecteursEnResultatPatch(vecteursReconstruits);
//        
//        // Étape 8: Reconstruction de l'image finale à partir des patchs
//        return gestionnairePatchs.reconstructionPatchs(patchsReconstruits, xB.getHauteur(), xB.getLargeur(),xB);
//    }
//    
    
    
    
//    /**
//     * Applique le débruitage en découpant l'image en fenêtres locales.
//     */
//    private Img debruiterLocal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
//        // Calcul des paramètres de fenêtrage
//        ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(
//                xB.getLargeur(), xB.getHauteur(), TAILLE_FENETRE_DEFAUT);
//        
//        // Découpage de l'image en fenêtres
//        List<Fenetre> imagettesList = gestionnairePatchs.decoupageImage(xB, pF);
//
//        // Initialisation de l'image résultat avec des pixels à 0
//        Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
//        for (int i = 0; i < xB.getHauteur(); i++) {
//            for (int j = 0; j < xB.getLargeur(); j++) {
//                xRPixels[i][j] = new Pixel(0);
//            }
//        }
//
//        // Traitement de chaque fenêtre
//        for (Fenetre f : imagettesList) {
//            // Extraction des patchs et conversion en vecteurs pour la fenêtre courante
//            ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
//            ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);
//
//            // Vérification qu'il y a suffisamment de patchs pour l'ACP
//            if (resVecteurs.taille() == 0) {
//                continue; // Passer à la fenêtre suivante si pas de patchs
//            }
//
//            // Analyse ACP sur la fenêtre
//            ResultatACP resACP = processeurACP.acp(resVecteurs);
//            ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
//            
//            // Conversion correcte des vecteurs propres pour la projection
//            ResultatVecteur vecteursPropresRV = gestionnairePatchs.matriceToResultatVecteur(resACP.getVecteursPropres());
//            
//            // Projection, seuillage et reconstruction similaires au mode global
//            List<Vecteur> vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
//            
//            // Conversion en ResultatVecteur avec positions conservées
//            ResultatVecteur vecteursProjRV = new ResultatVecteur();
//            for (int i = 0; i < vecteursProj.size(); i++) {
//                vecteursProjRV.ajouterVecteur(vecteursProj.get(i), resMoyCov.getVecteursCenters().getPositions().get(i));
//            }
//            
//            ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(
//                vecteursProjRV, typeSeuil, fonctionSeuillage, sigma, f.getImage(), resMoyCov.getMatriceCovariance());
//            
//            ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(
//                vecteursSeuil, resACP.getVecteursPropres(), resACP.getVecteurMoyen());
//            
//            ResultatPatch patchsReconstruits = gestionnairePatchs.transformerVecteursEnResultatPatch(vecteursReconstruits);
//
//            // Reconstruction de l'image pour cette fenêtre
//            Img nfImg = gestionnairePatchs.reconstructionPatchs(
//                patchsReconstruits, f.getImage().getHauteur(), f.getImage().getLargeur(),xB);
//            
//            // Fusion des résultats dans l'image globale avec gestion des chevauchements
//            Pixel[][] nfPixels = nfImg.getPixels();
//            for (int i = 0; i < nfPixels.length; i++) {
//                for (int j = 0; j < nfPixels[0].length; j++) {
//                    int posY = i + f.getPosition().getI();
//                    int posX = j + f.getPosition().getJ();
//                    
//                    if (posY < xB.getHauteur() && posX < xB.getLargeur()) {
//                        Pixel pixelGlobal = xRPixels[posY][posX];
//                        pixelGlobal.setValeur(pixelGlobal.getValeur() + nfPixels[i][j].getValeur());
//                        pixelGlobal.setNbChevauchement(pixelGlobal.getNbChevauchement() + 1);
//                    }
//                }
//            }
//        }
//
//        // Calcul de la moyenne pour les pixels qui se chevauchent
//        for (int i = 0; i < xRPixels.length; i++) {
//            for (int j = 0; j < xRPixels[0].length; j++) {
//                if (xRPixels[i][j].getNbChevauchement() > 0) {
//                    double valeurNormalisee = xRPixels[i][j].getValeur() / (double) xRPixels[i][j].getNbChevauchement();
//                    int valeurFinale = (int) Math.min(255, Math.max(0, Math.round(valeurNormalisee)));
//                    xRPixels[i][j].setValeur(valeurFinale);
//                }
//            }
//        }
//
//        return new Img(xRPixels);
//    }
//    
    
    private Img debruiterGlobal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
        System.out.println("\n=== DEBUGGING MODE - GLOBAL DENOISING ===");
        
        // Extraction des patchs et leur transformation en vecteurs
        ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(xB, taillePatch);
        System.out.println("Étape 1: Extraction des patchs - OK");
        System.out.println("Nombre de patchs extraits: " + resPatchs.taille());

        List<Position> positionsOriginales = new ArrayList<>();
        for (PairePatchPosition p : resPatchs) {
            positionsOriginales.add(p.getPosition());
        }
        
        System.out.println("Sauvegarde de " + positionsOriginales.size() + " positions originales");
        
        ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);
        System.out.println("Étape 2: Vectorisation des patchs - OK");
        System.out.println("Nombre de vecteurs: " + resVecteurs.taille() + ", Dimension: " + resVecteurs.getVecteurs().get(0).taille());
        
        // Vérifier des échantillons de vecteurs
        System.out.println("Échantillon de valeurs du premier vecteur:");
        double[] premierVecteur = resVecteurs.getVecteurs().get(0).getValeurs();
        for (int i = 0; i < Math.min(5, premierVecteur.length); i++) {
            System.out.println("  Valeur " + i + ": " + premierVecteur[i]);
        }

        // Étape 1: Calcul de l'ACP
        System.out.println("\nCalcul de l'ACP et MoyCov...");
        try {
            ResultatACP resACP = processeurACP.acp(resVecteurs);
            System.out.println("Étape 3: ACP complétée - OK");
            
            // Afficher quelques valeurs propres
            double[] valeursPropres = resACP.getValeursPropres();
            System.out.println("Valeurs propres (premières et dernières):");
            for (int i = 0; i < Math.min(3, valeursPropres.length); i++) {
                System.out.println("  VP " + i + ": " + valeursPropres[i]);
            }
            
            // Étape 2: Récupération des résultats MoyCov
            ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
            System.out.println("Étape 4: Calcul MoyCov - OK");
            
            // Vérifier le vecteur moyen
            Vecteur moyenneVect = resMoyCov.getVecteurMoyen();
            System.out.println("Échantillon du vecteur moyen:");
            for (int i = 0; i < Math.min(5, moyenneVect.taille()); i++) {
                System.out.println("  Valeur " + i + ": " + moyenneVect.getValeur(i));
            }
            
            // Étape 3: Projection des vecteurs
            ResultatVecteur vecteursPropresRV = gestionnairePatchs.matriceToResultatVecteur(resACP.getVecteursPropres());

            ResultatVecteur vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
            
            ResultatVecteur vecteursProjRV = new ResultatVecteur();
            for (int i = 0; i < vecteursProj.taille(); i++) {
                // Utiliser les positions originales au lieu des positions de resMoyCov
                Position position = i < positionsOriginales.size() ? positionsOriginales.get(i) : new Position(0, 0);
                vecteursProjRV.ajouterVecteur(vecteursProj.getVecteurs().get(i), position);
            }
            
            System.out.println("Étape 5: Projection des vecteurs - OK");
            
            // Étape 5: Seuillage des coefficients
            System.out.println("\nApplication du seuillage...");
            System.out.println("Type de seuil: " + typeSeuil + ", Fonction: " + fonctionSeuillage + ", Sigma: " + sigma);
            
            ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProjRV, typeSeuil, fonctionSeuillage, 
                                                                         sigma, xB, resMoyCov.getMatriceCovariance());
            System.out.println("Étape 7: Seuillage des coefficients - OK");
            
            // Vérifier les valeurs seuillées
            if (!vecteursSeuil.getVecteurs().isEmpty()) {
                double[] vecteurSeuilSample = vecteursSeuil.getVecteurs().get(0).getValeurs();
                int nonZeroCount = 0;
                for (double val : vecteurSeuilSample) {
                    if (val != 0) nonZeroCount++;
                }
                System.out.println("Premier vecteur seuillé: " + nonZeroCount + "/" + vecteurSeuilSample.length + " coefficients non nuls");
                System.out.println("Échantillon du vecteur seuillé:");
                for (int i = 0; i < Math.min(5, vecteurSeuilSample.length); i++) {
                    System.out.println("  Valeur seuillée " + i + ": " + vecteurSeuilSample[i]);
                }
            }
            
            // Étape 6: Reconstruction
            ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(
                    vecteursSeuil, resACP.getVecteursPropres(), resACP.getVecteurMoyen());
            System.out.println("Étape 8: Reconstruction des vecteurs - OK");
            
            // Vérifier un échantillon de reconstruction
            if (!vecteursReconstruits.getVecteurs().isEmpty()) {
                System.out.println("Échantillon de reconstruction du premier vecteur:");
                for (int i = 0; i < Math.min(5, vecteursReconstruits.getVecteurs().get(0).taille()); i++) {
                    System.out.println("  Recons " + i + ": " + vecteursReconstruits.getVecteurs().get(0).getValeur(i));
                }
            }
            
            // Étape 7: Conversion en patchs
            ResultatPatch patchsReconstruits = gestionnairePatchs.transformerVecteursEnResultatPatch(vecteursReconstruits);
            System.out.println("Étape 9: Conversion en patchs - OK");
            System.out.println("Nombre de patchs reconstruits: " + patchsReconstruits.taille());
            
            // Étape 8: Reconstruction finale
            System.out.println("\nReconstruction de l'image finale...");
            Img imgReconstruite = gestionnairePatchs.reconstructionPatchs(patchsReconstruits, xB.getHauteur(), xB.getLargeur(), xB);
            System.out.println("Étape 10: Reconstruction finale de l'image - OK");
            System.out.println("Dimensions de l'image reconstruite: " + imgReconstruite.getLargeur() + "×" + imgReconstruite.getHauteur());
            
            return imgReconstruite;
        } catch (Exception e) {
            System.err.println("ERREUR lors du débruitage: " + e.getMessage());
            e.printStackTrace();
            return xB; // En cas d'erreur, retourne l'image bruitée
        }
    }
    
    
    
    private Img debruiterLocal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
        System.out.println("\n=== DEBUGGING MODE - LOCAL DENOISING ===");
        
        // Calcul des paramètres de fenêtrage
        ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(
                xB.getLargeur(), xB.getHauteur(), TAILLE_FENETRE_DEFAUT);
        
        System.out.println("Paramètres de fenêtrage:");
        System.out.println("  Dimensions de l'image: " + xB.getLargeur() + "×" + xB.getHauteur());
        System.out.println("  Taille de fenêtre: " + pF.getTailleFenetreCalculee());
        System.out.println("  Grille: " + pF.getNombreFenetresX() + "×" + pF.getNombreFenetresY() + " fenêtres");
        System.out.println("  Chevauchement: X=" + pF.getChevauchementCombineX() + ", Y=" + pF.getChevauchementCombineY());
        
        // Découpage de l'image en fenêtres
        List<Fenetre> imagettesList = gestionnairePatchs.decoupageImage(xB, pF);
        System.out.println("Étape 1: Découpage en fenêtres - OK");
        System.out.println("Nombre de fenêtres: " + imagettesList.size());
        
        // Vérifier les positions des premières fenêtres
        if (!imagettesList.isEmpty()) {
            System.out.println("Positions des 3 premières fenêtres:");
            for (int i = 0; i < Math.min(3, imagettesList.size()); i++) {
                Position pos = imagettesList.get(i).getPosition();
                System.out.println("  Fenêtre " + i + ": (" + pos.getI() + ", " + pos.getJ() + ")");
            }
        }

        // Initialisation de l'image résultat
        Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
        for (int i = 0; i < xB.getHauteur(); i++) {
            for (int j = 0; j < xB.getLargeur(); j++) {
                xRPixels[i][j] = new Pixel(0);
            }
        }
        System.out.println("Étape 2: Initialisation de l'image résultat - OK");

        // Compteur de progression
        int fenetreTraitee = 0;
        int totalFenetres = imagettesList.size();
        
        // Traitement de chaque fenêtre
        for (Fenetre f : imagettesList) {
            fenetreTraitee++;
            if (fenetreTraitee % 5 == 0 || fenetreTraitee == 1 || fenetreTraitee == totalFenetres) {
                System.out.println("\nTraitement de la fenêtre " + fenetreTraitee + "/" + totalFenetres);
                System.out.println("Position: (" + f.getPosition().getI() + ", " + f.getPosition().getJ() + ")");
                System.out.println("Dimensions: " + f.getImage().getLargeur() + "×" + f.getImage().getHauteur());
            }
            
            // Extraction des patchs et conversion en vecteurs
            ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
            
            List<Position> positionsOriginales = new ArrayList<>();
            for (PairePatchPosition p : resPatchs) {
                positionsOriginales.add(p.getPosition());
            }
            
            System.out.println("Sauvegarde de " + positionsOriginales.size() + " positions originales");
            
            
            
            if (fenetreTraitee % 5 == 0 || fenetreTraitee == 1 || fenetreTraitee == totalFenetres) {
                System.out.println("  Nombre de patchs extraits: " + resPatchs.taille());
            }
            
            ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);

            // Vérification qu'il y a des patchs
            if (resVecteurs.taille() == 0) {
                System.out.println("  AVERTISSEMENT: Aucun patch extrait pour cette fenêtre, passage à la suivante");
                continue;
            }

            try {
                // Analyse ACP sur la fenêtre
                ResultatACP resACP = processeurACP.acp(resVecteurs);
                ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
                
                // Conversion des vecteurs propres
                ResultatVecteur vecteursPropresRV = gestionnairePatchs.matriceToResultatVecteur(resACP.getVecteursPropres());
                
                
                
                // Projection, seuillage et reconstruction
                ResultatVecteur vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
                
               
                ResultatVecteur vecteursProjRV = new ResultatVecteur();
                for (int i = 0; i < vecteursProj.taille(); i++) {
                    // Utiliser les positions originales au lieu des positions de resMoyCov
                    Position position = i < positionsOriginales.size() ? positionsOriginales.get(i) : new Position(0, 0);
                    vecteursProjRV.ajouterVecteur(vecteursProj.getVecteurs().get(i), position);
                }
                
                ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(
                    vecteursProjRV, typeSeuil, fonctionSeuillage, sigma, f.getImage(), resMoyCov.getMatriceCovariance());
                
                ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(
                    vecteursSeuil, resACP.getVecteursPropres(), resACP.getVecteurMoyen());
                
                ResultatPatch patchsReconstruits = gestionnairePatchs.transformerVecteursEnResultatPatch(vecteursReconstruits);

                // Reconstruction de l'image pour cette fenêtre
                Img nfImg = gestionnairePatchs.reconstructionPatchs(
                    patchsReconstruits, f.getImage().getHauteur(), f.getImage().getLargeur(), xB);
                
                if (fenetreTraitee % 5 == 0 || fenetreTraitee == 1 || fenetreTraitee == totalFenetres) {
                    System.out.println("  Fenêtre traitée avec succès");
                }
                
                // Fusion des résultats dans l'image globale
                Pixel[][] nfPixels = nfImg.getPixels();
                int pixelsAjoutes = 0;
                
                for (int i = 0; i < nfPixels.length; i++) {
                    for (int j = 0; j < nfPixels[0].length; j++) {
                        int posY = i + f.getPosition().getI();
                        int posX = j + f.getPosition().getJ();
                        
                        if (posY < xB.getHauteur() && posX < xB.getLargeur()) {
                            Pixel pixelGlobal = xRPixels[posY][posX];
                            pixelGlobal.setValeur(pixelGlobal.getValeur() + nfPixels[i][j].getValeur());
                            pixelGlobal.setNbChevauchement(pixelGlobal.getNbChevauchement() + 1);
                            pixelsAjoutes++;
                        }
                    }
                }
                
                if (fenetreTraitee % 5 == 0 || fenetreTraitee == 1 || fenetreTraitee == totalFenetres) {
                    System.out.println("  Fusion dans l'image globale - OK (" + pixelsAjoutes + " pixels ajoutés)");
                }
                
            } catch (Exception e) {
                System.err.println("  ERREUR lors du traitement de la fenêtre " + fenetreTraitee + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Calcul de la moyenne pour les pixels qui se chevauchent
        System.out.println("\nFinalisation de l'image...");
        int pixelsTraites = 0;
        int pixelsNonVisites = 0;
        
        for (int i = 0; i < xRPixels.length; i++) {
            for (int j = 0; j < xRPixels[0].length; j++) {
                if (xRPixels[i][j].getNbChevauchement() > 0) {
                    double valeurNormalisee = xRPixels[i][j].getValeur() / (double) xRPixels[i][j].getNbChevauchement();
                    int valeurFinale = (int) Math.min(255, Math.max(0, Math.round(valeurNormalisee)));
                    xRPixels[i][j].setValeur(valeurFinale);
                    pixelsTraites++;
                } else {
                    pixelsNonVisites++;
                    // Utiliser une valeur par défaut pour les pixels jamais traités
                    xRPixels[i][j].setValeur(xB.getPixel(i, j).getValeur());
                }
            }
        }
        
        System.out.println("Normalisation des chevauchements - OK");
        System.out.println("Pixels traités: " + pixelsTraites);
        
        if (pixelsNonVisites > 0) {
            System.out.println("AVERTISSEMENT: " + pixelsNonVisites + " pixels n'ont jamais été visités!");
        }

        System.out.println("\nDébruitage local terminé");
        return new Img(xRPixels);
    }
    
    
    /**
     * Pipeline principal débruitage ACP + seuillage (mode global ou local).
     * @param xB Image bruitée à débruiter
     * @param typeSeuil Type de seuil ("VisuShrink" ou "BayesShrink")
     * @param fonctionSeuillage Fonction de seuillage ("Dur" ou "Doux")
     * @param sigma Écart-type estimé du bruit
     * @param taillePatch Taille des patchs pour l'analyse
     * @param modeLocal Si true, traitement par fenêtres locales; sinon traitement global
     * @return Image débruitée
     */
    public Img imageDen(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch, boolean modeLocal) {
        if (modeLocal) {
            return debruiterLocal(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch);
        } else {
            return debruiterGlobal(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch);
        }
    }

}
    
    
    //modeLocal True --> Local & False --> Global
//    public Img imageDen(Img xB ,String typeSeuil, String fonctionSeuillage,  double sigma ,int taillePatch, boolean modeLocal) {
//    	Img xR = null;
//    	if(modeLocal) {
//    		//LOCAL
//    		ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(xB.getLargeur(), xB.getHauteur(), TAILLE_FENETRE_DEFAUT); 
//
//    		 // Affichage détaillé de tous les paramètres
//    	    System.out.println("---------- DÉTAILS DES PARAMÈTRES DE FENÊTRES ----------");
//    	    System.out.println("Dimensions de l'image : " + xB.getLargeur() + "×" + xB.getHauteur() + " pixels");
//    	    System.out.println();
//    	    System.out.println("Taille de fenêtre : " + pF.getTailleFenetreCalculee() + "×" + pF.getTailleFenetreCalculee() + " pixels");
//
//    	    
//    	    System.out.println("Nombre de fenêtres horizontales : " + pF.getNombreFenetresX());
//    	    System.out.println("Nombre de fenêtres verticales : " + pF.getNombreFenetresY());
//    	    System.out.println("Nombre total de fenêtres : " + pF.getNombreFenetresTotal());
//    	    System.out.println();
//    	    
//    	    System.out.println("Chevauchement combiné en X : " + pF.getChevauchementCombineX() + " pixels");
//    	    System.out.println("Chevauchement combiné en Y : " + pF.getChevauchementCombineY() + " pixels");
//    	    System.out.println();
//    		
//    		
//    		List<Fenetre> imagettesList = this.gestionnairePatchs.decoupageImage(xB,pF);
//    		
//    		List<Fenetre> newFenetresList = new ArrayList<Fenetre>();
//    		
//    		for(Fenetre f : imagettesList) {
//    			
//    			ResultatPatch resPatchs = this.gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
//        		ResultatVecteur resVecteur = this.gestionnairePatchs.vectorPatchs(resPatchs);
//        		
//        		// Traitement ACP + Seuillage 
//        		//System.out.println("Début ACP");
//        		//ResultatACP resACP= this.processeurACP.acp(resVecteur);
//        		//System.out.println(resACP);
////        		
////
////        		List<Vecteur> listeVecteurCoefs = this.processeurACP.proj(ResultatVecteur.transformerMatriceVecteursPropresEnResultatVecteur(resACP.getVecteursPropres()),resMoyCov.getVecteursCenters());
////        		
////        		
////
////        		//ResultatPatch resPatchReconstruits= this.gestionnairePatchs.deVectorPatchs(resPatchs);
//
//        		
//        		Fenetre nf = new Fenetre(this.gestionnairePatchs.reconstructionPatchs(resPatchs, f.getImage().getHauteur(), f.getImage().getLargeur(), xB), f.getPosition());
//                newFenetresList.add(nf);
//    		}
//    		
//     		Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
//     		
//     		for (int i = 0; i < xB.getHauteur(); i++) {
//     		    for (int j = 0; j < xB.getLargeur(); j++) {
//     		        xRPixels[i][j] = new Pixel(0); // Valeur par défaut
//     		    }
//     		}
//     		
//     		
//    		for(Fenetre nf : newFenetresList) {
//    			Pixel[][] nfPixels = nf.getImage().getPixels();
//    			for(int i = 0; i < nfPixels.length ;i++) {
//        			for(int j = 0; j < nfPixels[0].length;j++) {
//        				xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].setValeur(
//        						xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].getValeur() 
//        						+
//        						nfPixels[i][j].getValeur()
//        				);
//        				xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].setNbChevauchement(
//        					    xRPixels[i+nf.getPosition().getI()][j+nf.getPosition().getJ()].getNbChevauchement() + 1);
//        			}
//    			}
//    		}
//    		
//    		for(int i = 0; i < xRPixels.length ;i++) {
//    			for(int j = 0; j < xRPixels[0].length;j++) {
//    				
//    				if (xRPixels[i][j].getNbChevauchement() > 0) {
//    					
//    					double valeurAvant = xRPixels[i][j].getValeur();
//    		            int nbChevauchement = xRPixels[i][j].getNbChevauchement();
//    		            
//    		            double valeurNormalisee = valeurAvant / (double) nbChevauchement;
//    		            int valeurFinale = (int) Math.min(255, Math.max(0, Math.round(valeurNormalisee)));
//    		            
//    		            xRPixels[i][j].setValeur(valeurFinale);
//    		        }
//    				
//    			}
//    			
//			}
//    		
//    		
//    		
//    		xR = new Img(xRPixels);
//    		
//    		
//    		
//    		
//    	}else {
//    		//GLOBAL
//    		ResultatPatch resPatchs = this.gestionnairePatchs.extractPatchs(xB, taillePatch);
//    		ResultatVecteur resVecteur = this.gestionnairePatchs.vectorPatchs(resPatchs);
//    		
//    		// Traitement ACP + Seuillage 
//    		
//    		xR = this.gestionnairePatchs.reconstructionPatchs(resPatchs, xB.getHauteur(), xB.getLargeur(), xB);
//
//    	}
//    	
//    	return xR;   	
//    }
    
