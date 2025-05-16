package service.debruitage;

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

import java.io.IOException;
import java.util.List;

public class Test {

    private BruiteurImage bruiteurImage;
    private GestionnairePatchs gestionnairePatchs;
    private ProcesseurACP processeurACP;
    private ProcesseurSeuillage processeurSeuillage;
    private EvaluationrQualite evaluationQualite;
    private final static int TAILLE_FENETRE_DEFAUT = 250;

    public Test() {
        this.bruiteurImage = new BruiteurImage();
        this.gestionnairePatchs = new GestionnairePatchs();
        this.processeurACP = new ProcesseurACP();
        this.processeurSeuillage = new ProcesseurSeuillage();
        this.evaluationQualite = new EvaluationrQualite();
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

    /**
     * Applique le débruitage sur l'image entière en une fois.
     */
    private Img debruiterGlobal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
        // Extraction des patchs et leur transformation en vecteurs
        ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(xB, taillePatch);
        ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);

        // Étape 1: Calcul de l'ACP
        ResultatACP resACP = processeurACP.acp(resVecteurs);
        
        // Étape 2: Calcul de la moyenne et covariance (déjà fait dans acp, on récupère juste le résultat)
        ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
        
        // Étape 3: Projection des vecteurs centrés sur les vecteurs propres
        // Correct: On convertit directement la matrice des vecteurs propres en ResultatVecteur
        ResultatVecteur vecteursPropresRV = gestionnairePatchs.matriceToResultatVecteur(resACP.getVecteursPropres());
        
        // Projection des vecteurs centrés sur les vecteurs propres
        List<Vecteur> vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
        
        // Étape 4: Conversion des résultats de projection en ResultatVecteur pour le seuillage
        ResultatVecteur vecteursProjRV = new ResultatVecteur();
        for (int i = 0; i < vecteursProj.size(); i++) {
            vecteursProjRV.ajouterVecteur(vecteursProj.get(i), resMoyCov.getVecteursCenters().getPositions().get(i));
        }
        
        // Étape 5: Seuillage des coefficients projetés
        ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProjRV, typeSeuil, fonctionSeuillage, 
                                                                    sigma, xB, resACP.getVecteursPropres());
        
        // Étape 6: Reconstruction des vecteurs à partir de leurs coefficients seuillés
        ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(
                vecteursSeuil, resACP.getVecteursPropres(), resACP.getVecteurMoyen());
        
        // Étape 7: Conversion des vecteurs reconstruits en patchs
        ResultatPatch patchsReconstruits = gestionnairePatchs.transformerVecteursEnResultatPatch(vecteursReconstruits);
        
        // Étape 8: Reconstruction de l'image finale à partir des patchs
        return gestionnairePatchs.reconstructionPatchs(patchsReconstruits, xB.getHauteur(), xB.getLargeur());
    }

    /**
     * Applique le débruitage en découpant l'image en fenêtres locales.
     */
    private Img debruiterLocal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
        // Calcul des paramètres de fenêtrage
        ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(
                xB.getLargeur(), xB.getHauteur(), TAILLE_FENETRE_DEFAUT);
        
        // Découpage de l'image en fenêtres
        List<Fenetre> imagettesList = gestionnairePatchs.decoupageImage(xB, pF);

        // Initialisation de l'image résultat avec des pixels à 0
        Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
        for (int i = 0; i < xB.getHauteur(); i++) {
            for (int j = 0; j < xB.getLargeur(); j++) {
                xRPixels[i][j] = new Pixel(0);
            }
        }

        // Traitement de chaque fenêtre
        for (Fenetre f : imagettesList) {
            // Extraction des patchs et conversion en vecteurs pour la fenêtre courante
            ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
            ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);

            // Vérification qu'il y a suffisamment de patchs pour l'ACP
            if (resVecteurs.taille() == 0) {
                continue; // Passer à la fenêtre suivante si pas de patchs
            }

            // Analyse ACP sur la fenêtre
            ResultatACP resACP = processeurACP.acp(resVecteurs);
            ResultatMoyCov resMoyCov = processeurACP.moyCov(resVecteurs);
            
            // Conversion correcte des vecteurs propres pour la projection
            ResultatVecteur vecteursPropresRV = gestionnairePatchs.matriceToResultatVecteur(resACP.getVecteursPropres());
            
            // Projection, seuillage et reconstruction similaires au mode global
            List<Vecteur> vecteursProj = processeurACP.proj(vecteursPropresRV, resMoyCov.getVecteursCenters());
            
            // Conversion en ResultatVecteur avec positions conservées
            ResultatVecteur vecteursProjRV = new ResultatVecteur();
            for (int i = 0; i < vecteursProj.size(); i++) {
                vecteursProjRV.ajouterVecteur(vecteursProj.get(i), resMoyCov.getVecteursCenters().getPositions().get(i));
            }
            
            ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(
                vecteursProjRV, typeSeuil, fonctionSeuillage, sigma, f.getImage(), resACP.getVecteursPropres());
            
            ResultatVecteur vecteursReconstruits = processeurACP.reconstructionDepuisCoefficients(
                vecteursSeuil, resACP.getVecteursPropres(), resACP.getVecteurMoyen());
            
            ResultatPatch patchsReconstruits = gestionnairePatchs.transformerVecteursEnResultatPatch(vecteursReconstruits);

            // Reconstruction de l'image pour cette fenêtre
            Img nfImg = gestionnairePatchs.reconstructionPatchs(
                patchsReconstruits, f.getImage().getHauteur(), f.getImage().getLargeur());
            
            // Fusion des résultats dans l'image globale avec gestion des chevauchements
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
        }

        // Calcul de la moyenne pour les pixels qui se chevauchent
        for (int i = 0; i < xRPixels.length; i++) {
            for (int j = 0; j < xRPixels[0].length; j++) {
                if (xRPixels[i][j].getNbChevauchement() > 0) {
                    double valeurNormalisee = xRPixels[i][j].getValeur() / (double) xRPixels[i][j].getNbChevauchement();
                    int valeurFinale = (int) Math.min(255, Math.max(0, Math.round(valeurNormalisee)));
                    xRPixels[i][j].setValeur(valeurFinale);
                }
            }
        }

        return new Img(xRPixels);
    }
    
    public static void main(String[] args) throws IOException {
        // Nom de l'image à traiter
        String imageName = "ali_gray.jpg";
        Img x0 = new Img("data/x0/" + imageName);

        DebruiteurImage dImg = new DebruiteurImage();
        BruiteurImage bruiteur = new BruiteurImage();
        EvaluationrQualite eval = new EvaluationrQualite();

        // Niveau de bruit sigma
        double sigma = 20.0;

        // Bruitage synthétique
        Img xB = bruiteur.noising(x0, sigma);
        xB.saveImg("data/xB/" + imageName);

        // Débruitage GLOBAL
        Img xRGlobal = dImg.imageDen(xB, "VisuShrink", "Dur", sigma, 7, false);
        xRGlobal.saveImg("data/xR/global_" + imageName);

        // Débruitage LOCAL
        Img xRLocal = dImg.imageDen(xB, "VisuShrink", "Dur", sigma, 7, true);
        xRLocal.saveImg("data/xR/local_" + imageName);

        // Évaluation qualité GLOBAL
        double mseGlobal = eval.mse(x0, xRGlobal);
        double psnrGlobal = eval.psnr(x0, xRGlobal);

        // Évaluation qualité LOCAL
        double mseLocal = eval.mse(x0, xRLocal);
        double psnrLocal = eval.psnr(x0, xRLocal);

        // Affichage des résultats
        System.out.println("===== Résultats Débruitage =====");
        System.out.println("Global - MSE : " + mseGlobal + ", PSNR : " + psnrGlobal + " dB");
        System.out.println("Local  - MSE : " + mseLocal + ", PSNR : " + psnrLocal + " dB");
    }

}
