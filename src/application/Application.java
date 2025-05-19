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
import service.bruit.BruiteurImage;
import service.debruitage.DebruiteurImage;
import service.evaluation.EvaluationQualite;

/**
 * @class Application
 * @brief Classe principale pour le traitement d'images bruitées et leur débruitage.
 * @author Paul & Emma
 */
public class Application {
    
	 /**
     * @brief Méthode principale qui détermine le mode d'exécution en fonction des arguments.
     * Sans arguments, lance le mode console interactif.
     * Avec arguments, traite la commande en ligne.
     * @author Paul
     * @param args Arguments de la ligne de commande
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
          // Lancement Débruitage en mode de PROD
        	lancerModeConsole();
            
        	// Lancement Débruitage en mode de TEST
            //lancerModeTest();
            
        } else {
            // Mode ligne de commande (avec arguments)
            traiterArgumentsCommande(args);
        }
    }
    
    /**
     * @brief Affiche les résultats de l’évaluation de la qualité d'une image débruitée par rapport à l’image originale.
     * @author Emma
     * @param x0 Image originale (référence)
     * @param xR Image reconstruite ou débruitée
     */
    public static void afficherResultat(Img x0, Img xR) {
    	// Évaluation
        EvaluationQualite eval = new EvaluationQualite();
        double mse = eval.mse(x0, xR);
        double psnr = eval.psnr(x0, xR);
        
        System.out.println("\n📊 ÉVALUATION DE LA QUALITÉ 📊");
 		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
 		System.out.println("📉 MSE  : " + String.format("%.2f", mse));
 		System.out.println("📈 PSNR : " + String.format("%.2f", psnr) + " dB");

 		if (psnr < 20) {
 			System.out.println("🔴 Qualité faible - Débruitage limité");
 		} else if (psnr < 30) {
 			System.out.println("🟠 Qualité moyenne - Débruitage acceptable");
 		} else if (psnr < 40) {
 			System.out.println("🟢 Bonne qualité - Débruitage efficace");
 		} else {
 			System.out.println("🔵 Excellente qualité - Débruitage optimal");
 		}
 		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * @brief Lance l'application en mode console interactif.
     * @author Paul
     */
    private static void lancerModeConsole() {
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
            scanner.close();
            return;
        }
        
        double sigma = demanderDouble(scanner, "Entrez l'écart-type du bruit sigma (doit être > 0): ", 0);
        String typeSeuil = demanderChoix(scanner, "Choisissez le type de seuil (VisuShrink / BayesShrink): ", new String[]{"VisuShrink", "BayesShrink"});
        String fonctionSeuillage = demanderChoix(scanner, "Choisissez la fonction de seuillage (Dur / Doux): ", new String[]{"Dur", "Doux"});
        boolean modeLocal = demanderBool(scanner, "Activer le mode local ? (true/false): ");
        int taillePatch = 0;
        
        if(modeLocal) {
            taillePatch = demanderPatch(scanner, "Entrez la taille des patches (17, 21, 23): ", new int[]{17, 21, 23});
        } else {
            taillePatch = demanderPatch(scanner, "Entrez la taille des patches (5, 7, 9): ", new int[]{5, 7, 9});
        }

        // Processus de débruitage
        try {
            System.out.println("\n=== Début du traitement ===");
            
            // Bruitage
            System.out.println("Bruitage de l'image avec sigma = " + sigma + "...");
            new BruiteurImage();
            Img xB = BruiteurImage.noising(x0, sigma);
            
            String bruitedImagePath = "data/xB/" + imageName;
            xB.saveImg(bruitedImagePath);
            System.out.println("Image bruitée sauvegardée: " + bruitedImagePath);
            
            // Débruitage
            System.out.println("Débruitage en cours...");
            System.out.println("Paramètres: " + (modeLocal ? "Local" : "Global") + ", " + typeSeuil + ", " + fonctionSeuillage + ", taille patch: " + taillePatch);
            
            DebruiteurImage debruiteur = new DebruiteurImage();
            Img xR = debruiteur.imageDen(xB, typeSeuil, fonctionSeuillage, sigma, taillePatch, modeLocal);
            
            String methodName = modeLocal ? "local" : "global";
            String outputImagePath = "data/xR/" + methodName + "_" + imageName;
            xR.saveImg(outputImagePath);
            System.out.println("Image débruitée sauvegardée: " + outputImagePath);
            
            // Évaluation
            afficherResultat(x0, xR);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement: " + e.getMessage());
            e.printStackTrace();
        }
        
        scanner.close();
        System.out.println("\nTraitement terminé.");
    }
    
    /**
     * @brief Lance une exécution automatisée de test avec des paramètres prédéfinis.
     * @author Paul
     * @throws IOException En cas de problème lors de la lecture ou de l'écriture d'image
     */
    private static void lancerModeTest() throws IOException {
    	 String imageName = "lena_gray.png";
         Img x0 = new Img("data/x0/" + imageName);

         DebruiteurImage dImg = new DebruiteurImage();
         EvaluationQualite eval = new EvaluationQualite();

         // Niveau de bruit sigma
         double sigma = 20.0;

         // Bruitage synthétique
         Img xB = BruiteurImage.noising(x0, sigma);
         xB.saveImg("data/xB/" + imageName);

         // Débruitage GLOBAL
         Img xRGlobal = dImg.imageDen(xB, "BayesShrink", "Doux", sigma, 7, false);
         xRGlobal.saveImg("data/xR/global_" + imageName);
         
      // Évaluation
         afficherResultat(x0, xRGlobal);

         // Débruitage LOCAL
         Img xRLocal = dImg.imageDen(xB, "BayesShrink", "Doux", sigma, 7, true);
         xRLocal.saveImg("data/xR/local_" + imageName);

      // Évaluation
         afficherResultat(x0, xRLocal);
    }
    
    
  
    /**
     * @brief Traite les arguments de la ligne de commande pour configurer un débruitage.
     * @author Paul
     * @param args Arguments en ligne de commande
     */
    private static void traiterArgumentsCommande(String[] args) {
        // Paramètres par défaut
        String imageName = null;
        boolean isGlobal = false;
        String thresholdType = "Dur";
        String shrinkType = "VisuShrink";
        double sigma = 20.0;
        int taillePatch = 7;
        
        // Traitement des arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--image", "-i" -> {
                    if (i + 1 < args.length) imageName = args[++i];
                    else {
                        System.err.println("Erreur: Nom d'image manquant pour --image");
                        afficherAide();
                        return;
                    }
                }
                case "--global", "-g" -> {
                    isGlobal = true;
                     }
                case "--local", "-l" -> {
                    isGlobal = false;
                }
                case "--threshold", "-t" -> {
                    if (i + 1 < args.length) {
                        String type = args[++i].toLowerCase();
                        if (type.equals("hard")) thresholdType = "Dur";
                        else if (type.equals("soft")) thresholdType = "Doux";
                        else {
                            System.err.println("Erreur: Type de seuillage non reconnu: " + type);
                            afficherAide();
                            return;
                        }
                    } else {
                        System.err.println("Erreur: Type de seuillage manquant pour --threshold");
                        afficherAide();
                        return;
                    }
                }
                case "--shrink", "-s" -> {
                    if (i + 1 < args.length) {
                        String type = args[++i].toLowerCase();
                        if (type.equals("v")) shrinkType = "VisuShrink";
                        else if (type.equals("b")) shrinkType = "BayesShrink";
                        else {
                            System.err.println("Erreur: Type de seuillage adaptatif non reconnu: " + type);
                            afficherAide();
                            return;
                        }
                    } else {
                        System.err.println("Erreur: Type de seuillage adaptatif manquant pour --shrink");
                        afficherAide();
                        return;
                    }
                }
                case "--sigma", "-sig" -> {
                    if (i + 1 < args.length) {
                        try {
                            sigma = Double.parseDouble(args[++i]);
                            if (sigma <= 0) {
                                System.err.println("Erreur: Sigma doit être supérieur à 0");
                                afficherAide();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Erreur: Valeur sigma non valide: " + args[i]);
                            afficherAide();
                            return;
                        }
                    } else {
                        System.err.println("Erreur: Valeur sigma manquante pour --sigma");
                        afficherAide();
                        return;
                    }
                }
                case "--patch-size", "-p" -> {
                    if (i + 1 < args.length) {
                        try {
                            taillePatch = Integer.parseInt(args[++i]);
                            if (taillePatch <= 0 || taillePatch % 2 == 0) {
                                System.err.println("Erreur: Taille du patch doit être un entier impair positif");
                                afficherAide();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Erreur: Valeur de taille de patch non valide: " + args[i]);
                            afficherAide();
                            return;
                        }
                    } else {
                        System.err.println("Erreur: Taille du patch manquante pour --patch-size");
                        afficherAide();
                        return;
                    }
                }
                case "--help", "-h" -> {
                    afficherAide();
                    return;
                }
                default -> {
                    System.err.println("Option non reconnue: " + args[i]);
                    afficherAide();
                    return;
                }
            }
        }
        
        // Vérification que le nom de l'image est spécifié
        if (imageName == null) {
            System.err.println("Erreur: Le nom de l'image (--image) est obligatoire");
            afficherAide();
            return;
        }
        
        
        // Vérification que l'image existe dans le dossier x0
        String inputPath = "data/x0/" + imageName;
        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("Erreur: L'image '" + imageName + "' n'existe pas dans le dossier data/x0");
            System.err.println("Veuillez placer l'image dans le dossier data/x0 et réessayer.");
            return;
        }
        
        // Exécuter le processus complet (bruitage + débruitage)
        try {
            System.out.println("=== Traitement de l'image " + imageName + " ===");
            
            // Chargement de l'image originale
            Img x0 = new Img(inputPath);
            System.out.println("Image originale chargée: " + inputPath);
            
            // Bruitage
            System.out.println("Application du bruit (sigma = " + sigma + ")...");
            new BruiteurImage();
            Img xB = BruiteurImage.noising(x0, sigma);
            
            String bruitedImagePath = "data/xB/" + imageName;
            xB.saveImg(bruitedImagePath);
            System.out.println("Image bruitée sauvegardée: " + bruitedImagePath);
            
            // Débruitage
            System.out.println("Débruitage en cours...");
            String methodeTexte = isGlobal ? "Globale" : "Locale";
            System.out.println("Paramètres: méthode " + methodeTexte + ", seuil " + 
                              (thresholdType.equals("Dur") ? "Hard" : "Soft") + ", " + 
                              (shrinkType.equals("VisuShrink") ? "VisuShrink" : "BayesShrink") + 
                              ", taille patch: " + taillePatch);
            
            DebruiteurImage debruiteur = new DebruiteurImage();
            Img xR = debruiteur.imageDen(xB, shrinkType, thresholdType, sigma, taillePatch, !isGlobal);
            
            // Sauvegarde de l'image débruitée
            String methodName = isGlobal ? "global" : "local";
            String threshold = thresholdType.equals("Dur") ? "hard" : "soft";
            String shrink = shrinkType.equals("VisuShrink") ? "v" : "b";
            
            // Extraire le nom de base et l'extension
            int dotIndex = imageName.lastIndexOf('.');
            String baseName = (dotIndex != -1) ? imageName.substring(0, dotIndex) : imageName;
            String extension = (dotIndex != -1) ? imageName.substring(dotIndex) : ".png";
            
            String outputImagePath = "data/xR/" + baseName + "_" + methodName + "_" + threshold + "_" + shrink + extension;
            xR.saveImg(outputImagePath);
            System.out.println("Image débruitée sauvegardée: " + outputImagePath);
            
         // Évaluation
            afficherResultat(x0, xR);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * @brief Demande à l'utilisateur de saisir une taille de patch parmi des options valides.
     * @author Emma
     * @param scanner Scanner actif
     * @param message Message à afficher à l'utilisateur
     * @param choixValides Tableau d'entiers représentant les tailles autorisées
     * @return La taille choisie
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
            System.out.println("Erreur: choix invalide. Options valides: " + formatOptionsValides(choixValides));
        }
    }
    
    /**
     * @brief Formate un tableau d'entiers pour affichage en texte lisible.
     * @author Paul
     * @param options Tableau d'options
     * @return Chaîne formatée des options
     */
    private static String formatOptionsValides(int[] options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            sb.append(options[i]);
            if (i < options.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    /**
     * @brief Demande un nombre réel supérieur à une valeur minimale.
     * @author Emma
     * @param scanner Scanner actif
     * @param message Message à afficher
     * @param minValue Valeur minimale 
     * @return Valeur saisie par l'utilisateur
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
     * @brief Demande à l'utilisateur de choisir une option parmi des choix donnés.
     * @author Emma
     * @param scanner Scanner actif
     * @param message Message à afficher
     * @param choixValides Liste des chaînes valides
     * @return Choix validé
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
     * @brief Demande un booléen à l'utilisateur (true/false).
     * @author Emma
     * @param scanner Scanner actif
     * @param message Message à afficher
     * @return Valeur booléenne saisie
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
     * @brief Récupère tous les noms de fichiers d'images dans le dossier spécifié.
     * @author Paul
     * @param folderPath Le chemin absolu ou relatif vers le dossier contenant les fichiers.
     * @return Un tableau de chaînes de caractères contenant les noms des fichiers image trouvés.
     *         Retourne un tableau vide si le dossier est vide, invalide, ou ne contient aucune image.
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
        
        // Filtrer pour ne garder que les fichiers images
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
    
    /**
     * @brief Affiche un message d'aide pour l'utilisation en ligne de commande.
     * @author Paul
     */
    private static void afficherAide() {
        System.out.println("DenoiZe - Outil de débruitage d'images par ACP");
        System.out.println("Développé par le Groupe 7 - ING1 CY-Tech - 2024-2025");
        System.out.println();
        System.out.println("Usage: java -jar denoize.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --image, -i <nom>         Nom de l'image (dans data/x0/)                     [Obligatoire]");
        System.out.println("  --global, -g              Utilise la méthode de débruitage globale           [Facultatif]");
        System.out.println("  --local, -l               Utilise la méthode de débruitage locale            [Défaut]");
        System.out.println("  --threshold, -t <type>    Type de seuillage: 'hard' ou 'soft'                [Défaut: 'hard']");
        System.out.println("  --shrink, -s <type>       Type de seuillage adaptatif: 'v' ou 'b'            [Défaut: 'v']");
        System.out.println("                            ('v' = VisuShrink, 'b' = BayesShrink)");
        System.out.println("  --sigma, -sig <valeur>    Écart-type du bruit                                [Défaut: 20.0]");
        System.out.println("  --patch-size, -p <taille> Taille des patchs (entier impair)                  [Défaut: 7]");
        System.out.println("  --help, -h                Affiche cette aide");
        System.out.println();
        System.out.println("Exemples:");
        System.out.println("  java -jar denoize.jar -i lena_gray.png");
        System.out.println("  java -jar denoize.jar -i lena_gray.png -g -t soft");
        System.out.println("  java -jar denoize.jar -i lena_gray.png -sig 30 -p 9 -s b");
        System.out.println();
        System.out.println("Sans arguments, le mode console interactif sera lancé.");
    }
}





