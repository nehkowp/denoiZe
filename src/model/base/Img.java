/**
 * @file Img.java
 * @brief Classe représentant une image en niveaux de gris.
 */

package model.base;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @class Img
 * @author Paul & Alexis
 * @brief Représentation d'une image en niveaux de gris sous forme de matrice de pixels.
 */
public class Img {

    //Largeur de l'image (nombre de colonnes de pixels)
    private int largeur;
    //Hauteur de l'image (nombre de lignes de pixels)
    private int hauteur;
    //Matrice des pixels de l'image
    private Pixel[][] pixels;

    /**
     * @brief Constructeur à partir d'un fichier image.
     * @author Paul & Alexis
     * @param filename Chemin du fichier image à charger.
     * @throws IOException En cas d'erreur de lecture du fichier.
     */
    public Img(String filename) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));
        this.hauteur = image.getHeight();
        this.largeur = image.getWidth();
        this.pixels = new Pixel[hauteur][largeur];

        WritableRaster raster = image.getRaster();
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                pixels[i][j] = new Pixel(raster.getSampleDouble(j, i, 0));
            }
        }
    }

    /**
     * @brief Constructeur à partir d'une matrice de pixels.
     * @author Paul & Alexis
     * @param generatedPixels Matrice de pixels représentant l'image.
     */
    public Img(Pixel generatedPixels[][]) {
        this.hauteur = generatedPixels.length;
        this.largeur = generatedPixels[0].length;
        this.pixels = new Pixel[hauteur][largeur];

        for (int i = 0; i < this.hauteur; i++) {
            for (int j = 0; j < this.largeur; j++) {
                this.pixels[i][j] = generatedPixels[i][j];
            }
        }
    }

    /**
     * @brief Récupère la hauteur de l'image.
     * @author Alexis
     * @return Nombre de lignes de pixels.
     */
    public int getHauteur() {
        return this.hauteur;
    }

    /**
     * @brief Récupère la largeur de l'image.
     * @author Alexis
     * @return Nombre de colonnes de pixels.
     */
    public int getLargeur() {
        return this.largeur;
    }

    /**
     * @brief Récupère la matrice de pixels.
     * @author Alexis
     * @return Tableau 2D des pixels de l'image.
     */
    public Pixel[][] getPixels() {
        return this.pixels;
    }

    /**
     * @brief Récupère un pixel aux coordonnées (x, y).
     * @author Alexis
     * @param x Ligne du pixel.
     * @param y Colonne du pixel.
     * @return Le pixel correspondant.
     */
    public Pixel getPixel(int x, int y) {
        return this.pixels[x][y];
    }

    /**
     * @brief Modifie la valeur d'un pixel aux coordonnées (x, y).
     * @author Alexis
     * @param x Ligne du pixel.
     * @param y Colonne du pixel.
     * @param valeur Nouvelle valeur à affecter au pixel.
     */
    public void setPixel(int x, int y, double valeur) {
        this.pixels[x][y].setValeur(valeur);
    }

    /**
     * @brief Clone l'image en créant une nouvelle instance.
     * @author Emma
     * @return Une copie de l'image actuelle.
     */
    public Img clone() {
        return new Img(this.getPixels());
    }

    /**
     * @brief Sauvegarde l'image dans un fichier.
     * @author Paul & Alexis
     * @param filename Chemin du fichier de sortie (incluant l'extension).
     * @throws IOException En cas d'erreur d'écriture du fichier.
     */
    public void saveImg(String filename) throws IOException {
        BufferedImage image = new BufferedImage(this.largeur, this.hauteur, BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < this.hauteur; i++) {
            for (int j = 0; j < this.largeur; j++) {
                int pixelValue = (int) Math.min(255, Math.max(0, Math.round(this.getPixel(i, j).getValeur())));
                image.getRaster().setSample(j, i, 0, pixelValue);
            }
        }

        String format = filename.substring(filename.lastIndexOf('.') + 1);
        ImageIO.write(image, format, new File(filename));
    }
}

