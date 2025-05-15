/**
 * @file ResultatVecteur.java
 * @brief Classe représentant un ensemble de vecteurs associés à des positions dans une image.
 */

package model.patch;

import java.util.ArrayList;
import java.util.List;

import model.base.Matrice;
import model.base.Position;
import model.base.Vecteur;

/**
 * @class ResultatVecteur
 * @brief Gère une collection de vecteurs et leurs positions associées.
 * @author Emma
 */
public class ResultatVecteur {

    //Liste des vecteurs
    private List<Vecteur> vecteurs;
    //Liste des positions associées à chaque vecteur
    private List<Position> positions;

    /**
     * @brief Constructeur par défaut. Initialise les listes de vecteurs et positions vides.
     * @author Emma
     */
    public ResultatVecteur() {
        this.positions = new ArrayList<>();
        this.vecteurs = new ArrayList<>();
    }
    
    public ResultatVecteur(List<Vecteur> vecteurs) {
        this.positions = new ArrayList<>();
    	this.vecteurs = vecteurs;
    }
    
    

    /**
     * @brief Ajoute un vecteur et sa position associée.
     * @author Emma
     * @param vecteur Le vecteur à ajouter.
     * @param position La position d'origine du vecteur dans l'image.
     */
    public void ajouterVecteur(Vecteur vecteur, Position position) {
        this.getPositions().add(position);
        this.getVecteurs().add(vecteur);
    }

    /**
     * @brief Retourne la liste des vecteurs.
     * @author Emma
     * @return Liste des vecteurs.
     */
    public List<Vecteur> getVecteurs() {
        return vecteurs;
    }

    /**
     * @brief Retourne la liste des positions.
     * @author Emma
     * @return Liste des positions.
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * @brief Retourne le nombre de vecteurs contenus.
     * @author Emma
     * @return Nombre de vecteurs dans la collection.
     */
    public int taille() {
        return this.getVecteurs().size();
    }

    /**
     * @brief Convertit la liste de vecteurs en une matrice de doubles.
     * @author Emma
     * @return Une matrice de taille n x M (n: taille vecteur, M: nombre de vecteurs).
     * Si la liste est vide, retourne une matrice vide [0][0].
     */
    public double[][] versMatrice() {
        if (this.getVecteurs().isEmpty()) {
            return new double[0][0];
        }

        int n = this.getVecteurs().get(0).getValeurs().length;  
        int M = this.getVecteurs().size();                    

        double[][] matrice = new double[n][M];

        for (int j = 0; j < M; j++) {
            for (int i = 0; i < n; i++) {
                matrice[i][j] = this.vecteurs.get(j).getValeurs()[i];
            }
        }

        return matrice;
    }
    
    public static ResultatVecteur transformerMatriceVecteursPropresEnResultatVecteur(Matrice vecteursPropres) {
        List<Vecteur> listeVecteurs = new ArrayList<>();
        

        int nombreVecteurs = vecteursPropres.getNbColonnes();
        
        for (int i = 0; i < nombreVecteurs; i++) {
            double[] vecteurPropre = vecteursPropres.getColonne(i); 
            
            Vecteur v = new Vecteur(vecteurPropre);
            
            listeVecteurs.add(v);
        }
        
        // Créer un ResultatVecteur sans positions
        return new ResultatVecteur(listeVecteurs);
    }
    
}
