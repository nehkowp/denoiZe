/**
 * @file ProcesseurACP.java
 * @brief Classe implémentant l'Analyse en Composantes Principales (ACP) pour le traitement d'images.
 */

package service.acp;

import model.base.Matrice;
import model.base.Position;
import model.base.Vecteur;
import model.patch.ResultatVecteur;
import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;
import org.apache.commons.math3.linear.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @class ProcesseurACP
 * @brief Fournit des méthodes pour calculer la moyenne, la covariance, appliquer l'ACP et projeter des vecteurs.
 * @author Lucas 
 */
public class ProcesseurACP {

    /**
     * @brief Calcule le vecteur moyen, la matrice de covariance et les vecteurs centrés.
     * @author Lucas 
     * @param v RésultatVecteur contenant les vecteurs des patches d'image.
     * @return Un objet ResultatMoyCov contenant : vecteur moyen, matrice de covariance et vecteurs centrés.
     */
    public ResultatMoyCov moyCov(ResultatVecteur v) {
        int s2 = v.getVecteurs().get(0).taille(); // Dimension des vecteurs
        int M = v.taille(); // Nombre de vecteurs

        Vecteur mV = new Vecteur(s2);
        Matrice gamma = new Matrice(M, M);
        ResultatVecteur vc = new ResultatVecteur();

        // Calcul du vecteur moyen
        for (int i = 0; i < M; i++) {
            mV = mV.ajouter(v.getVecteurs().get(i));
        }
        mV = mV.diviser(M);

        // Calcul des vecteurs centrés
        for (int k = 0; k < M; k++) {
            Vecteur V_k = v.getVecteurs().get(k);
            Vecteur centre = V_k.soustraire(mV);
            Position coordonne = new Position(k, k);
            vc.ajouterVecteur(centre, coordonne);
        }

        // Calcul de la matrice de covariance
        for (int k = 0; k < M; k++) {
            gamma.ajouter(vc.getVecteurs().get(k).multiplier(vc.getVecteurs().get(k)));
        }
        double invM = 1 / (double) M;
        gamma.multiplierParScalaire(invM);

        return new ResultatMoyCov(mV, gamma, vc);
    }

    /**
     * @brief Effectue l'Analyse en Composantes Principales à partir d'un ensemble de vecteurs.
     * @author Lucas 
     * @param v RésultatVecteur contenant les vecteurs des patches d'image.
     * @return Un objet ResultatACP contenant les valeurs propres, les vecteurs propres et le vecteur moyen.
     */
    public ResultatACP acp(ResultatVecteur v) {
        ResultatMoyCov res = moyCov(v);
        Vecteur mV = res.getVecteurMoyen();
        Matrice gamma = res.getMatriceCovariance();

        int M = gamma.getLignes();

        // Conversion de la matrice de covariance vers RealMatrix (Apache Commons Math)
        double[][] gammaData = new double[M][M];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                gammaData[i][j] = gamma.getValeur(i, j);
            }
        }
        RealMatrix gammaMatrix = new Array2DRowRealMatrix(gammaData);

        // Décomposition pour obtenir valeurs propres et vecteurs propres
        EigenDecomposition eig = new EigenDecomposition(gammaMatrix);

        double[] valeursPropres = eig.getRealEigenvalues();
        double[][] vecteursPropresData = new double[M][M];
        for (int i = 0; i < M; i++) {
            double[] vPropre = eig.getEigenvector(i).toArray();
            for (int j = 0; j < M; j++) {
                vecteursPropresData[j][i] = vPropre[j];
            }
        }

        // Tri décroissant des valeurs propres et réorganisation des vecteurs propres
        for (int i = 0; i < M - 1; i++) {
            for (int j = i + 1; j < M; j++) {
                if (valeursPropres[j] > valeursPropres[i]) {
                    // Échange des valeurs propres
                    double tempVal = valeursPropres[i];
                    valeursPropres[i] = valeursPropres[j];
                    valeursPropres[j] = tempVal;
                    // Échange des vecteurs propres correspondants
                    for (int k = 0; k < M; k++) {
                        double tempVec = vecteursPropresData[k][i];
                        vecteursPropresData[k][i] = vecteursPropresData[k][j];
                        vecteursPropresData[k][j] = tempVec;
                    }
                }
            }
        }

        Matrice vecteursPropres = new Matrice(vecteursPropresData);

        return new ResultatACP(valeursPropres, vecteursPropres, mV);
    }

    /**
     * @brief Projette les vecteurs centrés sur les vecteurs propres pour obtenir les coefficients projetés.
     * @author Lucas
     * @param U RésultatVecteur contenant les vecteurs propres.
     * @param Vc RésultatVecteur contenant les vecteurs centrés.
     * @return Liste des vecteurs alpha correspondant aux coefficients projetés.
     */
    public List<Vecteur> proj(ResultatVecteur U, ResultatVecteur Vc) {
        int s2 = Vc.getVecteurs().get(0).taille();
        int M = Vc.taille();
        List<Vecteur> alpha = new ArrayList<>();

        for (int k = 0; k < M; k++) {
            Vecteur alpha_k = new Vecteur(s2);
            Vecteur Vci = Vc.getVecteurs().get(k);

            for (int i = 0; i < s2; i++) {
                Vecteur uiPrime = U.getVecteurs().get(i);
                double contribution = uiPrime.produitscalaire(Vci);
                alpha_k.setValeur(i, contribution);
            }

            alpha.add(alpha_k);
        }
        return alpha;
    }
}
