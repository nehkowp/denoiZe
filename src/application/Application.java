/**
 * @file Application.java
 * @brief Application console pour configurer et simuler les paramètres de débruitage d'une image par ACP.
 */

package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.base.Img;
import ui.ParametresDebruitage;

/**
 * @class Application
 * @brief Point d'entrée de l'application console permettant à l'utilisateur de configurer le débruitage d'image par ACP.
 * @author Emma
 */
public class Application {

    /**
     * @brief Méthode principale qui interagit avec l'utilisateur pour configurer les paramètres de débruitage.
     * @author Emma
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Configuration des paramètres de débruitage par ACP ===");
        
        String[] imagesDisponibles = listerFichiersImages("data/x0");

	     // Si aucune image n'est trouvée
	     if (imagesDisponibles.length == 0) {
	         System.out.println("Aucune image disponible dans le dossier. Le programme va s'arrêter.");
	         scanner.close();
	         return; 
	     }
     
        // Afficher les images disponibles :
	    for(String imageN : imagesDisponibles) {
	    	System.out.print(imageN + " / ");
	    }
	    System.out.println();
	     
        String imageName = demanderChoix(scanner, "Choisissez une image non bruitée x0 présente dans le dossier x0 : ", imagesDisponibles);
        
        
        Img x0 = null;
        try {
            x0 = new Img("data/x0/" + imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        double sigma = demanderDouble(scanner, "Entrez l'écart-type du bruit sigma (doit être > 0): ", 0);
        String typeSeuil = demanderChoix(scanner, "Choisissez le type de seuil (VisuShrink / BayesShrink): ", new String[]{"VisuShrink", "BayesShrink"});
        String fonctionSeuillage = demanderChoix(scanner, "Choisissez la fonction de seuillage (Dur / Doux): ", new String[]{"Dur", "Doux"});
        boolean modeLocal = demanderBool(scanner, "Activer le mode local ? (true/false): ");
        int taillePatch = 0;
        int tailleFenetre = 0;
        
        if(modeLocal) {
            taillePatch = demanderPatch(scanner, "Entrez la taille des patches (17, 21, 23): ", new int[]{17, 21, 23});
            tailleFenetre = demanderEntier(scanner, "Entrez la taille de la fenêtre de recherche (doit être inférieure à "+Math.max(x0.getHauteur(), x0.getLargeur())/2+": ", Math.max(x0.getHauteur(), x0.getLargeur())/2);
        }else {
        	  taillePatch = demanderPatch(scanner, "Entrez la taille des patches (5, 7, 9): ", new int[]{5, 7, 9});
        }

        // Création de l'objet ParametresDebruitage
        ParametresDebruitage params = new ParametresDebruitage(taillePatch, tailleFenetre, sigma, typeSeuil, fonctionSeuillage, modeLocal);

        System.out.println("\n=== Paramètres configurés ===");
        afficherParametres(params);

        scanner.close();
    }

    /**
     * @brief Demande à l'utilisateur la taille des patchs.
     * @author Emma
     * @param scanner Scanner pour lire l'entrée utilisateur.
     * @param message Message affiché à l'utilisateur.
     * @param choixValides Tableau contenant les options valides.
     * @return L'entier saisi.
     */
    private static int demanderPatch(Scanner scanner, String message, int[] choixValides) {
    	int choix;
        while (true) {
            System.out.print(message);
            choix = scanner.nextInt();
            for (int option : choixValides) {
                if (choix == option) {
                    return option;
                }
            }
            System.out.println("Erreur: choix invalide. Options valides: 17, 21, 23");
        }
    }
    
    /**
     * @brief Demande à l'utilisateur un entier supérieur ou égal à minValue.
     * @author Emma
     * @param scanner Scanner pour lire l'entrée utilisateur.
     * @param message Message affiché à l'utilisateur.
     * @param minValue Valeur minimale acceptable.
     * @return L'entier saisi.
     */
    private static int demanderEntier(Scanner scanner, String message, int minValue) {
        int value = -1;
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value >= minValue) {
                    break;
                }
            } else {
                scanner.next();
            }
            System.out.println("Erreur: veuillez entrer un entier >= " + minValue + ".");
        }
        return value;
    }

    /**
     * @brief Demande à l'utilisateur un nombre réel strictement supérieur à minValue.
     * @author Emma
     * @param scanner Scanner pour lire l'entrée utilisateur.
     * @param message Message affiché à l'utilisateur.
     * @param minValue Valeur minimale (exclusive).
     * @return Le nombre réel saisi.
     */
    private static double demanderDouble(Scanner scanner, String message, double minValue) {
        double value = -1;
        while (true) {
            System.out.print(message);
            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                if (value > minValue) {
                    break;
                }
            } else {
                scanner.next();
            }
            System.out.println("Erreur: veuillez entrer un nombre > " + minValue + ".");
        }
        scanner.nextLine();
        return value;
    }

    /**
     * @brief Demande à l'utilisateur un choix parmi une liste d'options valides.
     * @author Emma
     * @param scanner Scanner pour lire l'entrée utilisateur.
     * @param message Message affiché à l'utilisateur.
     * @param choixValides Tableau contenant les options valides.
     * @return Le choix de l'utilisateur.
     */
    private static String demanderChoix(Scanner scanner, String message, String[] choixValides) {
        String choix = "";
        while (true) {
            System.out.print(message);
            choix = scanner.nextLine().trim();
            for (String option : choixValides) {
                if (choix.equalsIgnoreCase(option)) {
                    return option;
                }
            }
            System.out.println("Erreur: choix invalide. Options valides: " + String.join(" / ", choixValides));
        }
    }

    /**
     * @brief Demande à l'utilisateur de saisir un booléen ('true' ou 'false').
     * @author Emma
     * @param scanner Scanner pour lire l'entrée utilisateur.
     * @param message Message affiché à l'utilisateur.
     * @return true ou false selon la saisie.
     */
    private static boolean demanderBool(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.next();
            if (input.equalsIgnoreCase("true")) {
                return true;
            } else if (input.equalsIgnoreCase("false")) {
                return false;
            }
            System.out.println("Erreur: veuillez entrer 'true' ou 'false'.");
        }
    }

    /**
     * @brief Affiche les paramètres de débruitage configurés.
     * @author Emma
     * @param params Objet ParametresDebruitage à afficher.
     */
    private static void afficherParametres(ParametresDebruitage params) {
        System.out.println("Taille des patches: " + params.getTaillePatch());
        System.out.println("Taille de la fenêtre de recherche: " + params.getTailleFenetre());
        System.out.println("Sigma (écart-type du bruit): " + params.getSigma());
        System.out.println("Type de seuil: " + params.getTypeSeuil());
        System.out.println("Fonction de seuillage: " + params.getFonctionSeuillage());
        System.out.println("Mode local activé: " + (params.isModeLocal() ? "Oui" : "Non"));
    }
    
    /**
     * Récupère tous les noms de fichiers d'images dans le dossier spécifié.
     * @param folderPath Le chemin du dossier à parcourir
     * @return Un tableau de String contenant les noms des fichiers (sans le chemin)
     */
    private static String[] listerFichiersImages(String folderPath) {
        File folder = new File(folderPath);
        
        // Vérifier si le dossier existe et si c'est bien un dossier
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Erreur: Le dossier " + folderPath + " n'existe pas ou n'est pas un dossier.");
            return new String[0];
        }
        
        // Récupérer tous les fichiers du dossier
        File[] files = folder.listFiles();
        
        if (files == null || files.length == 0) {
            System.out.println("Aucun fichier trouvé dans le dossier " + folderPath);
            return new String[0];
        }
        
        // Filtrer pour ne garder que les fichiers (pas les sous-dossiers)
        // et potentiellement uniquement les images (en fonction de l'extension)
        List<String> imageNames = new ArrayList<>();
        
        for (File file : files) {
            if (file.isFile()) {
                
                 String name = file.getName().toLowerCase();
                 if (name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                     name.endsWith(".png") || name.endsWith(".gif") || 
                     name.endsWith(".bmp") || name.endsWith(".tiff")) {
                     imageNames.add(file.getName());
                 }
            }
        }
        
        return imageNames.toArray(new String[0]);
    }
    
    
    
}





