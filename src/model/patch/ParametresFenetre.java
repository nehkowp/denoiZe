/**
 * @file ParametresFenetre.java
 * @brief Classe contenant les paramètres liés au découpage d'une image en fenêtres.
 */

package model.patch;

/**
 * @class ParametresFenetre
 * @brief Contient les paramètres calculés pour le découpage d'une image en fenêtres régulières.
 * @author Paul
 */
public class ParametresFenetre {

    // Chevauchement horizontal
    private int chevauchementCombineX;
    // Chevauchement vertical
    private int chevauchementCombineY;
    // Nombre de fenêtres sur l'axe horizontal
    private int nombreFenetresX;
    // Nombre de fenêtres sur l'axe vertical
    private int nombreFenetresY;
    // Nombre total de fenêtres
    private int nombreFenetresTotal;
    // Taille d'une fenêtre 
    private int tailleFenetreCalculee;

    /**
     * @brief Récupère le chevauchement horizontal.
     * @author Paul
     * @return Chevauchement en X (en pixels).
     */
    public int getChevauchementCombineX() { 
    	return chevauchementCombineX; 
    }

    /**
     * @brief Récupère le chevauchement vertical.
     * @author Paul
     * @return Chevauchement en Y (en pixels).
     */
    public int getChevauchementCombineY() { 
    	return chevauchementCombineY; 
    }

    /**
     * @brief Récupère le nombre de fenêtres selon l'axe horizontal.
     * @author Paul
     * @return Nombre de fenêtres sur l'axe X.
     */
    public int getNombreFenetresX() { 
    	return nombreFenetresX; 
    }

    /**
     * @brief Récupère le nombre de fenêtres selon l'axe vertical.
     * @author Paul
     * @return Nombre de fenêtres sur l'axe Y.
     */
    public int getNombreFenetresY() { 
    	return nombreFenetresY; 
    }

    /**
     * @brief Récupère le nombre total de fenêtres calculé.
     * @author Emma
     * @return Nombre total de fenêtres.
     */
    public int getNombreFenetresTotal() { 
    	return nombreFenetresTotal; 
    }

    /**
     * @brief Récupère la taille des fenêtres utilisée dans le calcul.
     * @author Paul
     * @return Taille d'une fenêtre (en pixels).
     */
    public int getTailleFenetreCalculee() { 
    	return tailleFenetreCalculee; 
    }

    /**
     * @brief Calcule les paramètres nécessaires pour découper une image en fenêtres régulières.
     * @author Paul
     * @param largeurImage Largeur de l'image à découper.
     * @param hauteurImage Hauteur de l'image à découper.
     * @param tailleFenetre Taille des fenêtres.
     * @return Un objet ParametresFenetre contenant tous les paramètres calculés.
     */
    public static ParametresFenetre calculerParametresFenetre(int largeurImage, int hauteurImage, int tailleFenetre) {
        ParametresFenetre params = new ParametresFenetre();

        params.nombreFenetresX = (int) Math.ceil(largeurImage / (double) tailleFenetre) + 1;
        params.nombreFenetresY = (int) Math.ceil(hauteurImage / (double) tailleFenetre) + 1;

        int restePixelsLargeur = largeurImage % tailleFenetre;
        int restePixelsHauteur = hauteurImage % tailleFenetre;

        params.chevauchementCombineX = tailleFenetre - restePixelsLargeur;
        params.chevauchementCombineY = tailleFenetre - restePixelsHauteur;
        params.tailleFenetreCalculee = tailleFenetre;
        params.nombreFenetresTotal = params.nombreFenetresX * params.nombreFenetresY;

        return params;
    }
}
