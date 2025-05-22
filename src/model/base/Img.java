/**
 * @file Img.java
 * @brief Classe représentant une image en niveaux de gris.
 */

package model.base;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @class Img
 * @author Paul & Alexis
 * @brief Représentation d'une image en niveaux de gris sous forme de matrice de
 *        pixels.
 */
public class Img {

	// true si l'image est en couleur , false si byte_gray
	private boolean estRGB;
	// Largeur de l'image (nombre de colonnes de pixels)
	private int largeur;
	// Hauteur de l'image (nombre de lignes de pixels)
	private int hauteur;
	// Matrice des pixels de l'image
	private Pixel[][] pixels;

	/**
	 * @brief Constructeur à partir d'un fichier image.
	 * @author Paul & Alexis
	 * @param filename Chemin du fichier image à charger.
	 * @throws IOException En cas d'erreur de lecture du fichier.
	 */
	public Img(String filename) throws IOException {
		 File file = new File(filename);
		    
	    // Vérifier existant dans x0
	    if (!file.exists()) {
	        throw new IOException("Le fichier n'existe pas : " + file.getAbsolutePath());
	    }
		    
		BufferedImage image = ImageIO.read(file);
		
	    if (image == null) {
	        throw new IOException("Impossible de lire l'image. Format non pris en charge ou fichier corrompu : " + filename);
	    }
	    
		this.hauteur = image.getHeight();
		this.largeur = image.getWidth();
		this.pixels = new Pixel[hauteur][largeur];

		 // Détection intelligente du type d'image
	    ColorModel colorModel = image.getColorModel();
	    int numComponents = colorModel.getNumComponents();
	    boolean hasAlpha = colorModel.hasAlpha();
	    
	    System.out.println("DEBUG: Type d'image détecté - " + getImageTypeString(image.getType()));
	    System.out.println("DEBUG: Nombre de composantes: " + numComponents + (hasAlpha ? " (avec alpha)" : ""));
	    
	    // Déterminer si l'image doit être traitée comme RGB ou niveaux de gris
	    this.estRGB = determinerSiRGB(image, colorModel, numComponents);
	   
	    
	    if (this.estRGB) {
	        System.out.println("DEBUG: Traitement en mode RGB");
	        chargerImageRGB(image);
	    } else {
	        System.out.println("DEBUG: Traitement en mode niveaux de gris");
	        chargerImageGrayscale(image);
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
	 * @param x      Ligne du pixel.
	 * @param y      Colonne du pixel.
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
		return new Img(this.getPixels(), this.estRGB);
	}

	/**
	 * @brief Sauvegarde l'image dans un fichier.
	 * @author Paul & Alexis
	 * @param filename Chemin du fichier de sortie (incluant l'extension).
	 * @throws IOException En cas d'erreur d'écriture du fichier.
	 */
	public void saveImg(String filename, boolean estRGB) throws IOException {

		if (estRGB) {
			BufferedImage image = new BufferedImage(this.largeur, this.hauteur, BufferedImage.TYPE_INT_RGB);

			for (int i = 0; i < this.hauteur; i++) {
				for (int j = 0; j < this.largeur; j++) {
					double[] rgb = this.getPixel(i, j).getValeurs();
					image.getRaster().setSample(j, i, 0, Math.max(0, Math.min(Math.round(rgb[0]), 255)));
					image.getRaster().setSample(j, i, 1, Math.max(0, Math.min(Math.round(rgb[1]), 255)));
					image.getRaster().setSample(j, i, 2, Math.max(0, Math.min(Math.round(rgb[2]), 255)));
				}
			}

			String format = filename.substring(filename.lastIndexOf('.') + 1);
			ImageIO.write(image, format, new File(filename));

		} else {
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
	
	/**
	 * @brief Charge une image en mode RGB.
	 */
	private void chargerImageRGB(BufferedImage image) {
	    // Utiliser getRGB() qui fonctionne pour tous les types d'images
	    for (int i = 0; i < hauteur; i++) {
	        for (int j = 0; j < largeur; j++) {
	            int rgb = image.getRGB(j, i);
	            
	            // Extraire les composantes RGB
	            int r = (rgb >> 16) & 0xFF;
	            int g = (rgb >> 8) & 0xFF;
	            int b = rgb & 0xFF;
	            
	            pixels[i][j] = new Pixel(r, g, b);
	        }
	    }
	}

	/**
	 * @brief Charge une image en mode niveaux de gris.
	 */
	private void chargerImageGrayscale(BufferedImage image) {
	    for (int i = 0; i < hauteur; i++) {
	        for (int j = 0; j < largeur; j++) {
	            int rgb = image.getRGB(j, i);
	            
	            // Convertir en niveaux de gris en utilisant la luminance
	            int r = (rgb >> 16) & 0xFF;
	            int g = (rgb >> 8) & 0xFF;
	            int b = rgb & 0xFF;
	            
	            // Formule de luminance standard : 0.299*R + 0.587*G + 0.114*B
	            double gray = 0.299 * r + 0.587 * g + 0.114 * b;
	            
	            pixels[i][j] = new Pixel(gray);
	        }
	    }
	}
	
	/**
	 * @brief Détermine si une image doit être traitée comme RGB ou niveaux de gris.
	 */
	private boolean determinerSiRGB(BufferedImage image, ColorModel colorModel, int numComponents) {
	    // Images explicitement en niveaux de gris
	    if (image.getType() == BufferedImage.TYPE_BYTE_GRAY || 
	        image.getType() == BufferedImage.TYPE_USHORT_GRAY) {
	        return false;
	    }
	    
	    // Images avec un seul canal (sans alpha)
	    if (numComponents == 1) {
	        return false;
	    }
	    
	    // Images avec 2 canaux = grayscale + alpha (on ignore l'alpha)
	    if (numComponents == 2 && colorModel.hasAlpha()) {
	        return false;
	    }
	    
	    // Toutes les autres images (3+ canaux) sont traitées comme RGB
	    return true;
	}
	
	/**
	 * @brief Convertit le type d'image en chaîne lisible pour le débogage.
	 */
	private String getImageTypeString(int type) {
	    return switch (type) {
	        case BufferedImage.TYPE_INT_ARGB -> "INT_ARGB";
	        case BufferedImage.TYPE_INT_RGB -> "INT_RGB";
	        case BufferedImage.TYPE_INT_ARGB_PRE -> "INT_ARGB_PRE";
	        case BufferedImage.TYPE_INT_BGR -> "INT_BGR";
	        case BufferedImage.TYPE_3BYTE_BGR -> "3BYTE_BGR";
	        case BufferedImage.TYPE_4BYTE_ABGR -> "4BYTE_ABGR";
	        case BufferedImage.TYPE_4BYTE_ABGR_PRE -> "4BYTE_ABGR_PRE";
	        case BufferedImage.TYPE_BYTE_GRAY -> "BYTE_GRAY";
	        case BufferedImage.TYPE_USHORT_GRAY -> "USHORT_GRAY";
	        case BufferedImage.TYPE_BYTE_BINARY -> "BYTE_BINARY";
	        case BufferedImage.TYPE_BYTE_INDEXED -> "BYTE_INDEXED";
	        case BufferedImage.TYPE_USHORT_565_RGB -> "USHORT_565_RGB";
	        case BufferedImage.TYPE_USHORT_555_RGB -> "USHORT_555_RGB";
	        case BufferedImage.TYPE_CUSTOM -> "CUSTOM";
	        default -> "UNKNOWN(" + type + ")";
	    };
	}
	
	
	

}
