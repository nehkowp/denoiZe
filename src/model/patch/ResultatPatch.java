/**
 * @file ResultatPatch.java
 * @brief Classe représentant le résultat d'une extraction de patches d'une image.
 */

package model.patch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import model.base.Position;

/**
 * @class ResultatPatch
 * @brief Contient une liste de patches et leurs positions associées dans une image.
 * Implémente Iterable pour parcourir les paires (Patch, Position).
 * @author Emma & Paul
 */
public class ResultatPatch implements Iterable<model.patch.ResultatPatch.PairePatchPosition> {

    //Liste des patches extraits
    private List<Patch> patches;
    //Liste des positions associées aux patches
    private List<Position> positions;

    /**
     * @brief Constructeur par défaut. Initialise les listes vides.
     * @author Emma
     */
    public ResultatPatch() {
        this.patches = new ArrayList<>();
        this.positions = new ArrayList<>();
    }

    /**
     * @brief Ajoute un patch et sa position associée.
     * @author Emma
     * @param patch Le patch à ajouter.
     * @param position La position d'origine du patch dans l'image.
     */
    public void ajouterPatch(Patch patch, Position position) {
        this.getPatches().add(patch);
        this.getPositions().add(position);
    }

    /**
     * @brief Retourne la liste des patches.
     * @author Emma
     * @return Liste des patches.
     */
    public List<Patch> getPatches() {
        return patches;
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
     * @brief Retourne le nombre de paires (Patch, Position).
     * @author Emma
     * @return Taille de la liste de patches (et positions).
     */
    public int taille() {
        return patches.size();
    }

    /**
     * @brief Retourne un itérateur sur les paires (Patch, Position).
     * @author Paul
     * @return Un itérateur de type PairePatchPosition.
     */
    @Override
    public Iterator<PairePatchPosition> iterator() {
        return new PatchPositionIterator();
    }

    /**
     * @class PairePatchPosition
     * @author Paul
     * @brief Structure contenant un patch et sa position.
     */
    public static class PairePatchPosition {
        //La position du patch dans l'image d'origine
        private Position position;
        //Le patch extrait de l'image
        private Patch patch;

        /**
         * @brief Constructeur d'une paire Patch-Position.
         * @author Paul
         * @param patch Le patch à stocker.
         * @param position La position associée au patch.
         */
        public PairePatchPosition(Patch patch, Position position) {
            this.patch = patch;
            this.position = position;
        }

        /**
         * @brief Retourne le patch de la paire.
         * @author Paul
         * @return Le patch.
         */
        public Patch getPatch() {
            return patch;
        }

        /**
         * @brief Retourne la position de la paire.
         * @author Paul
         * @return La position.
         */
        public Position getPosition() {
            return position;
        }
    }
    
    /**
     * @class PatchPositionIterator
     * @author Paul
     * @brief Itérateur sur les paires Patch-Position.
     */
    private class PatchPositionIterator implements Iterator<PairePatchPosition> {

        //Indice courant de l'itérateur
        private int currentIndex = 0;

        /**
         * @brief Indique s'il reste des éléments à parcourir.
         * @author Paul
         * @return true si un prochain élément existe, false sinon.
         */
        @Override
        public boolean hasNext() {
            return currentIndex < patches.size() && currentIndex < positions.size();
        }

        /**
         * @brief Retourne la prochaine paire Patch-Position.
         * @author Paul
         * @return La prochaine PairePatchPosition.
         * @throws NoSuchElementException Si aucun élément restant.
         */
        @Override
        public PairePatchPosition next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            PairePatchPosition pair = new PairePatchPosition(
                patches.get(currentIndex),
                positions.get(currentIndex)
            );

            currentIndex++;

            return pair;
        }
    }
}
