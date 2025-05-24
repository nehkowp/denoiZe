/**
 * @file Main.java
 * @brief Classe principale qui détermine le mode d'exécution de l'application (graphique ou console).
 */
package app;

import java.io.IOException;
import javafx.application.Application;

/**
 * @class Main
 * @brief Classe de démarrage principale qui détermine le mode d'exécution
 * @author Paul
 */
public class Main {

    /**
     * @brief Méthode principale exécutée au lancement du programme.
     * @author Paul
     * @param args Arguments passés en ligne de commande.
     */
    public static void main(String[] args) {
        System.setProperty("javafx.platform.tracing", "false");
        
        if (args.length == 0) {
            System.out.println("Lancement de l'interface graphique...");
            try {
                Application.launch(ui.GUI.class, args);
            } catch (Exception e) {
                System.err.println("Erreur lors du lancement de l'interface graphique: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.println("Lancement en mode console...");
            try {
                CLI.main(args);
            } catch (IOException e) {
                System.err.println("Erreur lors du lancement en mode console: " + e.getMessage());
                System.exit(1);
            }
        }
    }
}	