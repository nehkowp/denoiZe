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

 


   
    
    public static void main(String[] args) throws IOException {
        // Nom de l'image à traiter
        String imageName = "lena_gray.png";
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
        Img xRGlobal = dImg.imageDen(xB, "BayesShrink", "Doux", sigma, 7, false);
        xRGlobal.saveImg("data/xR/global_" + imageName);

        // Débruitage LOCAL
        Img xRLocal = dImg.imageDen(xB, "BayesShrink", "Doux", sigma, 7, true);
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
