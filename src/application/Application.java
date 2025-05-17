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
import service.evaluation.EvaluationrQualite;

public class Application {
    
	 /**
     * Méthode principale qui détermine le mode d'exécution en fonction des arguments.
     * Sans arguments, lance le mode console interactif.
     * Avec arguments, traite la commande en ligne.
     * @author Paul
     * @param args Arguments de la ligne de commande
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
          // Lancement Débruitage en mode de PROD
//        	lancerModeConsole();
            
        	// Lancement Débruitage en mode de TEST
            lancerModeTest();
            
        } else {
            // Mode ligne de commande (avec arguments)
            traiterArgumentsCommande(args);
        }
    }
    
    /**
     * Lance l'application en mode console interactif.
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
            EvaluationrQualite eval = new EvaluationrQualite();
            double mse = eval.mse(x0, xR);
            double psnr = eval.psnr(x0, xR);
            
            System.out.println("\n=== Résultats d'évaluation ===");
            System.out.println("MSE : " + mse);
            System.out.println("PSNR: " + psnr + " dB");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement: " + e.getMessage());
            e.printStackTrace();
        }
        
        scanner.close();
        System.out.println("\nTraitement terminé.");
    }
    
    
    private static void lancerModeTest() throws IOException {
    	 String imageName = "lena_gray.png";
         Img x0 = new Img("data/x0/" + imageName);

         DebruiteurImage dImg = new DebruiteurImage();
         EvaluationrQualite eval = new EvaluationrQualite();

         // Niveau de bruit sigma
         double sigma = 20.0;

         // Bruitage synthétique
         Img xB = BruiteurImage.noising(x0, sigma);
         xB.saveImg("data/xB/" + imageName);

         // Débruitage GLOBAL
         Img xRGlobal = dImg.imageDen(xB, "VisuShrink", "Dur", sigma, 7, false);
         xRGlobal.saveImg("data/xR/global_" + imageName);

         // Débruitage LOCAL
         Img xRLocal = dImg.imageDen(xB, "VisuShrink", "Doux", sigma, 7, true);
         xRLocal.saveImg("data/xR/local_" + imageName);

         // Évaluation qualité GLOBAL
         double mseGlobal = eval.mse(x0, xRGlobal);
         double psnrGlobal = eval.psnr(x0, xRGlobal);

         // Évaluation qualité LOCAL
         double mseLocal = eval.mse(x0, xRLocal);
         double psnrLocal = eval.psnr(x0, xRLocal);

         // Affichage des résultats
         System.out.println("===== Résultats Débruitage =====");
         System.out.println("Global - MSE : " + mseGlobal + ", PSNR : " + psnrGlobal + " dB");
         System.out.println("Local  - MSE : " + mseLocal + ", PSNR : " + psnrLocal + " dB");
         
    }
    
    
  
   /**
     * Traite les arguments de la ligne de commande pour effectuer le débruitage.
     * 
     * @param args Arguments de la ligne de commande
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
            EvaluationrQualite eval = new EvaluationrQualite();
            double mse = eval.mse(x0, xR);
            double psnr = eval.psnr(x0, xR);
            
            System.out.println("\n=== Résultats d'évaluation ===");
            System.out.println("MSE : " + mse);
            System.out.println("PSNR: " + psnr + " dB");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Demande à l'utilisateur la taille des patchs.
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
     * Formate un tableau d'entiers en chaîne lisible pour l'affichage.
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
     * Demande à l'utilisateur un nombre réel strictement supérieur à minValue.
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
     * Demande à l'utilisateur un choix parmi une liste d'options valides.
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
     * Demande à l'utilisateur de saisir un booléen ('true' ou 'false').
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
     * Récupère tous les noms de fichiers d'images dans le dossier spécifié.
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
     * Affiche l'aide pour l'utilisation en ligne de commande.
     */
    private static void afficherAide() {
        System.out.println("DenoiZe - Outil de débruitage d'images par ACP");
        System.out.println("Développé par le Groupe 7 - ING1 CY-Tech - 2024-2025");
        System.out.println();
        System.out.println("Usage: java -jar denoize.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --input, -i <chemin>       Chemin vers l'image à débruiter [obligatoire]");
        System.out.println("  --output, -o <chemin>      Chemin de destination pour l'image débruitée [facultatif]");
        System.out.println("  --global, -g               Utilise la méthode de débruitage globale [facultatif]");
        System.out.println("  --local, -l                Utilise la méthode de débruitage locale [défaut]");
        System.out.println("  --threshold, -t <type>     Type de seuillage: 'hard' ou 'soft' [défaut: 'hard']");
        System.out.println("  --shrink, -s <type>        Type de seuillage adaptatif: 'v' (VisuShrink) ou 'b' (BayesShrink) [défaut: 'v']");
        System.out.println("  --sigma, -sig <valeur>     Écart-type du bruit [défaut: 20.0]");
        System.out.println("  --patch-size, -p <taille>  Taille des patchs (entier impair) [défaut: 7]");
        System.out.println("  --help, -h                 Affiche cette aide");
        System.out.println();
        System.out.println("Exemples:");
        System.out.println("  java -jar denoize.jar -i data/xB/lena.png");
        System.out.println("  java -jar denoize.jar -i data/xB/lena.png -g -t soft -s b");
        System.out.println("  java -jar denoize.jar -i data/xB/lena.png -o data/xR/lena_denoised.png -sig 30 -p 9");
        System.out.println();
        System.out.println("Sans arguments, le mode console interactif sera lancé.");
    }
}





