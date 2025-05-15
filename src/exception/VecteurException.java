/**
 * @file VecteurException.java
 * @brief Définition de l'exception spécifique pour les erreurs liées aux vecteurs.
 * 
 * Cette exception est levée lorsqu'une opération sur un vecteur rencontre une erreur.
 */

package exception;

/**
 * @class VecteurException
 * @brief Exception spécifique pour les erreurs liées aux vecteurs.
 * @author Emma
 * @extends RuntimeException
 * Il s'agit d'une exception non vérifiée.
 */
public class VecteurException extends RuntimeException {
    
    /**
     * @brief Constructeur de l'exception VecteurException.
     * @author Emma
     * @param msg Le message décrivant l'erreur rencontrée.
     */
    public VecteurException(String msg) {
        super(msg);
    }
}

