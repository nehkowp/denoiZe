/**
 * @file ProcesseurACP.java
 * @brief Classe implémentant l'Analyse en Composantes Principales (ACP) pour le traitement d'images.
 */

package service.acp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import model.acp.ResultatACP;
import model.acp.ResultatMoyCov;
import model.base.Matrice;
import model.base.Position;
import model.base.Vecteur;
import model.patch.ResultatVecteur;

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

        // Conversion vers format Apache Commons Math pour calcul optimisé
        double[][] dataArray = new double[M][s2];
        for (int i = 0; i < M; i++) {
            Vecteur vect = v.getVecteurs().get(i);
            for (int j = 0; j < s2; j++) {
                dataArray[i][j] = vect.getValeur(j);
            }
        }
        RealMatrix data = new Array2DRowRealMatrix(dataArray);
        
        // Calcul du vecteur moyen
        Vecteur mV = new Vecteur(s2);
        for (int j = 0; j < s2; j++) {
            double meanValue = 0;
            for (int i = 0; i < M; i++) {
                meanValue += data.getEntry(i, j);
            }
            mV.setValeur(j, meanValue / M);
        }

        // Création des vecteurs centrés et calcul optimisé de la covariance
        ResultatVecteur vc = new ResultatVecteur();
        RealMatrix centeredData = new Array2DRowRealMatrix(M, s2);
        
        for (int i = 0; i < M; i++) {
            Vecteur V_k = v.getVecteurs().get(i);
            Vecteur centre = V_k.soustraire(mV);
            Position coordonne = new Position(i, i);
            vc.ajouterVecteur(centre, coordonne);
            
            // Remplir la matrice centrée pour le calcul de covariance
            for (int j = 0; j < s2; j++) {
                centeredData.setEntry(i, j, centre.getValeur(j));
            }
        }
        
        // Calcul optimisé de la covariance avec Apache Commons Math
        RealMatrix covMatrix = centeredData.transpose().multiply(centeredData);
        covMatrix = covMatrix.scalarMultiply(1.0 / M);
        
        // Conversion du résultat vers Matrice
        Matrice gamma = new Matrice(s2, s2);
        for (int i = 0; i < s2; i++) {
            for (int j = 0; j < s2; j++) {
                gamma.setValeur(i, j, covMatrix.getEntry(i, j));
            }
        }

        return new ResultatMoyCov(mV, gamma, vc);
    }

    /**
     * @brief Effectue l'Analyse en Composantes Principales à partir d'un ensemble de vecteurs.
     * @author Lucas 
     * @param v RésultatVecteur contenant les vecteurs des patches d'image.
     * @return Un objet ResultatACP contenant les valeurs propres, les vecteurs propres et le vecteur moyen.
     */
    public ResultatACP acp(ResultatVecteur v) {
    	System.out.println("1");
    	ResultatMoyCov res = moyCov(v);
        Vecteur mV = res.getVecteurMoyen();
        Matrice gamma = res.getMatriceCovariance();

        int s2 = mV.taille();
        System.out.println("2");
        // Conversion de la matrice de covariance vers RealMatrix (Apache Commons Math)
        double[][] gammaData = new double[s2][s2];
        for (int i = 0; i < s2; i++) {
            for (int j = 0; j < s2; j++) {
                gammaData[i][j] = gamma.getValeur(i, j);
            }
        }
        System.out.println("3");
        RealMatrix gammaMatrix = new Array2DRowRealMatrix(gammaData);

        // Décomposition pour obtenir valeurs propres et vecteurs propres
        EigenDecomposition eig = new EigenDecomposition(gammaMatrix);

        // Récupération et tri des valeurs propres et vecteurs propres
        double[] valeursPropres = eig.getRealEigenvalues();
        RealMatrix vecteursPropresMatrix = eig.getV();
        
        // Tri des valeurs propres et réorganisation des vecteurs propres
        double[][] vecteursPropresData = new double[s2][s2];
        System.out.println("4");
        // Créer des paires (valeur propre, indice) pour le tri
        ArrayList<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < s2; i++) {
            pairs.add(new Pair(valeursPropres[i], i));
        }
        
        // Tri par ordre décroissant des valeurs propres
        pairs.sort((a, b) -> -Double.compare((double)a.first, (double)b.first));
        System.out.println("5");
        // Réorganisation des données
        double[] sortedEigenValues = new double[s2];
        for (int i = 0; i < s2; i++) {
            Pair pair = pairs.get(i);
            int originalIndex = (int)pair.second;
            sortedEigenValues[i] = (double)pair.first;
            
            // Copie du vecteur propre dans l'ordre trié
            for (int j = 0; j < s2; j++) {
                vecteursPropresData[j][i] = vecteursPropresMatrix.getEntry(j, originalIndex);
            }
        }
        System.out.println("6");
        Matrice vecteursPropres = new Matrice(vecteursPropresData);

        return new ResultatACP(sortedEigenValues, vecteursPropres, mV);
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
        
        double[][] uData = new double[s2][s2];
        double[][] vcData = new double[M][s2];
        
        for (int i = 0; i < s2; i++) {
            Vecteur ui = U.getVecteurs().get(i);
            for (int j = 0; j < s2; j++) {
                uData[j][i] = ui.getValeur(j);
            }
        }
        
        for (int i = 0; i < M; i++) {
            Vecteur vci = Vc.getVecteurs().get(i);
            for (int j = 0; j < s2; j++) {
                vcData[i][j] = vci.getValeur(j);
            }
        }
        
        RealMatrix uMatrix = new Array2DRowRealMatrix(uData);
        RealMatrix vcMatrix = new Array2DRowRealMatrix(vcData);
        
        RealMatrix projections = vcMatrix.multiply(uMatrix);
        
        List<Vecteur> alpha = new ArrayList<>();
        for (int k = 0; k < M; k++) {
            Vecteur alpha_k = new Vecteur(s2);
            for (int i = 0; i < s2; i++) {
                alpha_k.setValeur(i, projections.getEntry(k, i));
            }
            alpha.add(alpha_k);
        }
        
        return alpha;
    }
    
    /**
     * @class Pair
     * @brief Classe utilitaire pour le tri des valeurs propres
     * @author Lucas 
     */
    private class Pair {
        public Object first;
        public Object second;
        
        public Pair(Object first, Object second) {
            this.first = first;
            this.second = second;
        }
    }
    
    /**
     * @brief Reconstruit les vecteurs débruités dans l'espace original à partir des coefficients seuillés.
     * @param alphaSeuil RésultatVecteur contenant les vecteurs de coefficients seuillés.
     * @param U Matrice des vecteurs propres (colonnes).
     * @param mV Vecteur moyen calculé lors de l'ACP.
     * @return ResultatVecteur contenant les vecteurs reconstruits dans l'espace d'origine.
     */
    public ResultatVecteur reconstructionDepuisCoefficients(ResultatVecteur alphaSeuil, Matrice U, Vecteur mV) {
        ResultatVecteur resultatReconstruit = new ResultatVecteur();
        int s2 = U.getNbColonnes();

        for (int k = 0; k < alphaSeuil.taille(); k++) {
            double[] alpha_k = alphaSeuil.getVecteurs().get(k).getValeurs();
            double[] reconstruit = new double[s2];

            for (int i = 0; i < s2; i++) {
                double somme = 0;
                for (int j = 0; j < s2; j++) {
                    somme += U.getValeur(i, j) * alpha_k[j];
                }
                reconstruit[i] = somme + mV.getValeur(i);
            }

            resultatReconstruit.ajouterVecteur(new model.base.Vecteur(reconstruit), alphaSeuil.getPositions().get(k));
        }

        return resultatReconstruit;
    }

}