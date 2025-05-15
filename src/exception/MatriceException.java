/**
 * @file MatriceException.java
 * @brief Définition de l'exception spécifique pour les erreurs liées aux matrices.
 * 
 * Cette exception est levée lorsqu'une opération sur une matrice rencontre une erreur.
 */

package exception;

/**
 * @class MatriceException
 * @brief Exception spécifique pour les erreurs liées aux matrices.
 * @author Emma
 * @extends RuntimeException
 * Il s'agit d'une exception non vérifiée.
 */
public class MatriceException extends RuntimeException {
	
    /**
     * @brief Constructeur de l'exception MatriceException.
     * @author Emma
     * @param msg Le message décrivant l'erreur rencontrée.
     */
	public MatriceException(String msg) {
		super(msg);
	}
}
