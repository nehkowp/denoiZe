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

	//true si l'image est en couleur , false si byte_gray 
	private boolean estRGB;
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
        if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
        	this.estRGB =false;
        	for (int i = 0; i < hauteur; i++) {
                for (int j = 0; j < largeur; j++) {
                    pixels[i][j] = new Pixel(raster.getSampleDouble(j, i, 0));
                }
            }
        }else {
        	this.estRGB =true;
        	for (int i = 0; i < hauteur; i++) {
                for (int j = 0; j < largeur; j++) {
                    double r = (raster.getSampleDouble(j, i, 0));
                    double g = (raster.getSampleDouble(j, i, 1));
                    double b = (raster.getSampleDouble(j, i, 2));
                    pixels[i][j] = new Pixel(r,g,b); 
                }
            }
        	
        }
        
        
    }

    /**
     * @brief Constructeur à partir d'une matrice de pixels.
     * @author Paul & Alexis
     * @param generatedPixels Matrice de pixels représentant l'image.
     */
    public Img(Pixel generatedPixels[][], boolean estRGB) {
    	this.estRGB = estRGB;
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
        return new Img(this.getPixels(),this.estRGB);
    }

    /**
     * @brief Sauvegarde l'image dans un fichier.
     * @author Paul & Alexis
     * @param filename Chemin du fichier de sortie (incluant l'extension).
     * @throws IOException En cas d'erreur d'écriture du fichier.
     */
    public void saveImg(String filename, boolean estRGB) throws IOException {
    	
    	if(estRGB) {
    		BufferedImage image = new BufferedImage(this.largeur, this.hauteur, BufferedImage.TYPE_INT_RGB);
    		
	        for (int i = 0; i < this.hauteur; i++) {
	            for (int j = 0; j < this.largeur; j++) {
	                double[] rgb = this.getPixel(i, j).getValeurs();
	                image.getRaster().setSample(j, i, 0, Math.max(0, Math.min(Math.round(rgb[0]),255)));
	                image.getRaster().setSample(j, i, 1, Math.max(0, Math.min(Math.round(rgb[1]),255)));
	                image.getRaster().setSample(j, i, 2, Math.max(0, Math.min(Math.round(rgb[2]),255)));
	            }
	        }

	        String format = filename.substring(filename.lastIndexOf('.') + 1);
	        ImageIO.write(image, format, new File(filename));
	        
    	}else {
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

	public boolean isEstRGB() {
		return estRGB;
	}

}

