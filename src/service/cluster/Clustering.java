package service.cluster;

import model.base.Vecteur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * @class Cultering
 * @brief implemente la methode de clustering
 * @author Bastien
 */
public class Clustering {
	/**
     * @brief applique l'algorithme des kmeans à une liste de vecteurs.
     * @author Bastien
     * @param V la liste de vecteurs sur lequelle on applique l'algorithme.
     * @param K le nombre de centroides que l'on veut.
     * @return tableau de int où chaque cellule indique le numero du cluster auquel le vecteur de cette position appartient.
     * @throw VecteurException Si les deux vecteurs ne sont pas de même taille.
     */
	
	public static int[] kmeansClustering(List<Vecteur> V, int K) {
		int maxIter = 100;
		Random random = new Random();
		int s2 = V.get(0).taille();  // dimension des vecteurs
		int M = V.size();            // nombre de vecteurs
		List<Vecteur> centres = new ArrayList<>();

		// Initialisation aléatoire des centres (sans doublons)
		List<Integer> indicesUtilises = new ArrayList<>();
		while (centres.size() < K) {
			int idx = random.nextInt(M);
			if (!indicesUtilises.contains(idx)) {
				indicesUtilises.add(idx);
				centres.add(V.get(idx).copie());
			}
		}

		int[] labels = new int[M];
		
		// Itérations principales
		for (int iter = 0; iter < maxIter; iter++) {
			// 1. Attribution des labels
			for (int i = 0; i < M; i++) {
				Vecteur v = V.get(i);
				double minDist = Double.MAX_VALUE;
				int bestK = 0;

				for (int k = 0; k < K; k++) {
					double dist = v.distanceEuclidienne(centres.get(k));
					if (dist < minDist) {
						minDist = dist;
						bestK = k;
					}
				}
				labels[i] = bestK;
			}
			
			// Mise à jour des centres
			List<Vecteur> nouveauxCentres = new ArrayList<>();
			for (int k = 0; k < K; k++) {
				Vecteur somme = new Vecteur(s2);
				int count = 0;

				for (int i = 0; i < M; i++) {
					if (labels[i] == k) {
						somme = somme.ajouter(V.get(i));
						count++;
					}
				}
				if (count > 0) {
					Vecteur centreMoyen = somme.diviser(count);
					nouveauxCentres.add(centreMoyen);
				} else {
					// Aucun point dans ce cluster, on garde l'ancien
					nouveauxCentres.add(centres.get(k));
				}
			}
		}
		return labels;
	}
}