package service.debruitage;

import java.io.IOException;
import java.util.List;

import model.acp.ResultatACP;
import model.base.Img;
import model.base.Pixel;
import model.patch.Fenetre;
import model.patch.ParametresFenetre;
import model.patch.ResultatPatch;
import model.patch.ResultatVecteur;
import service.acp.ProcesseurACP;
import service.bruit.BruiteurImage;
import service.evaluation.EvaluationrQualite;
import service.patch.GestionnairePatchs;
import service.seuillage.ProcesseurSeuillage;

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

    public Img imageDen(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch, boolean modeLocal) {
        if (modeLocal) {
            return debruiterLocal(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch);
        } else {
            return debruiterGlobal(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch);
        }
    }

    private Img debruiterGlobal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
        ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(xB, taillePatch);
        ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);

        // ACP
        ResultatACP resACP = processeurACP.acp(resVecteurs);

        // Projection
        ResultatVecteur vecteursProj = processeurACP.proj(resACP.getVecteursPropres(), resACP.getVecteurMoyen());

        // Seuillage
        ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProj, typeSeuil, fonctionSeuillage, sigma, xB, resACP.getMatriceCovariance());

        // Reconstruction
        return gestionnairePatchs.reconstructionPatchs(resPatchs, vecteursSeuil, xB.getHauteur(), xB.getLargeur());
    }

    private Img debruiterLocal(Img xB, String typeSeuil, String fonctionSeuillage, double sigma, int taillePatch) {
        ParametresFenetre pF = ParametresFenetre.calculerParametresFenetre(xB.getLargeur(), xB.getHauteur(), TAILLE_FENETRE_DEFAUT);
        List<Fenetre> imagettesList = gestionnairePatchs.decoupageImage(xB, pF);

        Pixel[][] xRPixels = new Pixel[xB.getHauteur()][xB.getLargeur()];
        for (int i = 0; i < xB.getHauteur(); i++) {
            for (int j = 0; j < xB.getLargeur(); j++) {
                xRPixels[i][j] = new Pixel(0);
            }
        }

        for (Fenetre f : imagettesList) {
            ResultatPatch resPatchs = gestionnairePatchs.extractPatchs(f.getImage(), taillePatch);
            ResultatVecteur resVecteurs = gestionnairePatchs.vectorPatchs(resPatchs);

            // ACP
            ResultatACP resACP = processeurACP.acp(resVecteurs);

            // Projection
            ResultatVecteur vecteursProj = processeurACP.proj(resACP.getVecteursPropres(), resACP.getValeursPropres());

            // Seuillage
            ResultatVecteur vecteursSeuil = processeurSeuillage.seuillage(vecteursProj, typeSeuil, fonctionSeuillage, sigma, f.getImage(), resACP.getMatriceCovariance());

            // Reconstruction locale
            Img nfImg = gestionnairePatchs.reconstructionPatchs(resPatchs, vecteursSeuil, f.getImage().getHauteur(), f.getImage().getLargeur());
            Pixel[][] nfPixels = nfImg.getPixels();

            for (int i = 0; i < nfPixels.length; i++) {
                for (int j = 0; j < nfPixels[0].length; j++) {
                    Pixel pixelGlobal = xRPixels[i + f.getPosition().getI()][j + f.getPosition().getJ()];
                    pixelGlobal.setValeur(pixelGlobal.getValeur() + nfPixels[i][j].getValeur());
                    pixelGlobal.setNbChevauchement(pixelGlobal.getNbChevauchement() + 1);
                }
            }
        }

        // Moyenne sur chevauchement
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
        String imageName = "ali_gray.jpg";
        Img x0 = new Img("data/x0/" + imageName);

        DebruiteurImage dImg = new DebruiteurImage();
        BruiteurImage bruiteur = new BruiteurImage();

        double sigma = 20.0;
        Img xB = bruiteur.noising(x0, sigma);
        xB.saveImg("data/xB/" + imageName);

        // Débruitage global
        Img xRGlobal = dImg.imageDen(xB, "VisuShrink", "Dur", sigma, 7, false);
        xRGlobal.saveImg("data/xR/global_" + imageName);

        // Débruitage local
        Img xRLocal = dImg.imageDen(xB, "VisuShrink", "Dur", sigma, 7, true);
        xRLocal.saveImg("data/xR/local_" + imageName);

        // Évaluation
        EvaluationrQualite eval = new EvaluationrQualite();

        double mseGlobal = eval.calculMSE(x0, xRGlobal);
        double psnrGlobal = eval.calculPSNR(x0, xRGlobal);

        double mseLocal = eval.calculMSE(x0, xRLocal);
        double psnrLocal = eval.calculPSNR(x0, xRLocal);

        System.out.println("========== ÉVALUATION ==========");
        System.out.println("GLOBAL - MSE: " + mseGlobal + " / PSNR: " + psnrGlobal + " dB");
        System.out.println("LOCAL  - MSE: " + mseLocal + " / PSNR: " + psnrLocal + " dB");
        System.out.println("================================");
    }

}
