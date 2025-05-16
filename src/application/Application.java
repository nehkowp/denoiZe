/**
 * @file Application.java
 * @brief Application console pour configurer et simuler les paramètres de débruitage d'une image par ACP.
 */

package application;

import java.util.Scanner;
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

        int taillePatch = demanderPatch(scanner, "Entrez la taille des patches (17, 21, 23): ", new int[]{17, 21, 23});
        int tailleFenetre = demanderEntier(scanner, "Entrez la taille de la fenêtre de recherche (doit être > 200): ", 200);
        double sigma = demanderDouble(scanner, "Entrez l'écart-type du bruit sigma (doit être > 0): ", 0);
        String typeSeuil = demanderChoix(scanner, "Choisissez le type de seuil (VisuShrink / BayesShrink): ", new String[]{"VisuShrink", "BayesShrink"});
        String fonctionSeuillage = demanderChoix(scanner, "Choisissez la fonction de seuillage (Dur / Doux): ", new String[]{"Dur", "Doux"});
        boolean modeLocal = demanderBool(scanner, "Activer le mode local ? (true/false): ");

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
}
