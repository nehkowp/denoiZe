# denoiℤe - Débruitage d'Images par Analyse en Composantes Principales

## À propos

Ce projet implémente une méthode de débruitage d'images basée sur l'Analyse en Composantes Principales (ACP). Cette approche utilise la décomposition en patchs et la projection dans un espace de dimension réduite pour éliminer le bruit gaussien additif.

Développé dans le cadre d'une SAÉ (Situation d'Apprentissage et d'Évaluation) à CY Tech Pau par le Groupe 7 - ING1.

## Prérequis

- Java JDK 17 ou supérieur
- Eclipse IDE (pour le build du .jar)

## Structure du projet

```
denoiZe/
├── src/             # Code source
├── data/
│   ├── x0/          # Images originales
│   ├── xB/          # Images bruitées (générées par le programme)
│   └── xR/          # Images débruitées (résultats)
├── denoize.jar      # Exécutable (généré à la compilation)
└── README.md
```

## Installation et compilation

### Compilation avec Eclipse

1. **Cloner le projet**

   ```bash
   git clone https://github.com/nehkowp/denoiZe.git
   ```

2. **Importer dans Eclipse**

   - Ouvrez Eclipse
   - Allez dans `File > Import`
   - Sélectionnez `Maven > Existing Maven Projects`
   - Naviguez jusqu'au dossier cloné
   - Sélectionnez le projet et cliquez sur `Finish`

3. **Compiler et créer le JAR avec Eclipse**
   - Clic droit sur le projet > `Run As > Maven Build...`
   - Dans le champ `Goals`, saisissez `clean package`
   - Cliquez sur `Run`
   - Le fichier JAR sera généré à la racine du projet : `denoize.jar`

## Utilisation

DenoiZe propose un pipeline complet de traitement d'image qui:

1. **Prend** une image originale (non bruitée)
2. **Applique** un bruit gaussien à cette image
3. **Débruite** l'image en utilisant l'ACP
4. **Évalue** la qualité du débruitage (MSE, PSNR)

### Préparation

**Important**: Placez vos images originales dans le dossier `data/x0/`

Les dossiers `data/xB/` (images bruitées) et `data/xR/` (images débruitées) seront créés automatiquement.

### Mode console interactif (sans arguments)

Double-cliquez sur le fichier JAR ou exécutez :

```bash
java -jar denoize.jar
```

L'application vous guidera à travers les étapes suivantes :

1. Choix de l'image originale parmi celles présentes dans `data/x0/`
2. Configuration des paramètres de bruitage et débruitage
3. Exécution du processus
4. Affichage des résultats d'évaluation

### Mode ligne de commande (avec arguments)

```bash
java -jar denoize.jar [options]
```

Options disponibles :

| Option                  | Format court    | Description                                                        | Statut                        |
| ----------------------- | --------------- | ------------------------------------------------------------------ | ----------------------------- |
| `--image <nom>`         | `-i <nom>`      | Nom de l'image (dans data/x0/)                                     | **Obligatoire**               |
| `--global`              | `-g`            | Utilise la méthode de débruitage globale                           | _Facultatif_                  |
| `--local`               | `-l`            | Utilise la méthode de débruitage locale                            | _Facultatif_ (défaut)         |
| `--threshold <type>`    | `-t <type>`     | Type de seuillage: 'hard' ou 'soft'                                | _Facultatif_ (défaut: 'hard') |
| `--shrink <type>`       | `-s <type>`     | Type de seuillage adaptatif: 'v' (VisuShrink) ou 'b' (BayesShrink) | _Facultatif_ (défaut: 'v')    |
| `--sigma <valeur>`      | `-sig <valeur>` | Écart-type du bruit                                                | _Facultatif_ (défaut: 20.0)   |
| `--patch-size <taille>` | `-p <taille>`   | Taille des patchs (entier impair)                                  | _Facultatif_ (défaut: 7)      |
| `--help`                | `-h`            | Affiche l'aide de la commande                                      | _Facultatif_                  |

### Exemples d'utilisation

1. Débruiter une image avec les paramètres par défaut :

   ```bash
   java -jar denoize.jar --image lena_gray.png
   ```

2. Débruiter avec la méthode globale et seuillage soft :

   ```bash
   java -jar denoize.jar -i lena_gray.png -g -t soft
   ```

3. Paramètres personnalisés pour le bruit et les patchs :
   ```bash
   java -jar denoize.jar -i lena_gray.png -sig 30 -p 9 -s b
   ```

## Méthodes de débruitage

### Méthode globale

- Analyse l'image entière avec une approche globale par ACP
- Moins gourmande en calculs
- Meilleure préservation des structures globales de l'image

### Méthode locale

- Analyse des patches locaux dans l'image
- Plus précise pour préserver les détails
- Plus gourmande en calculs

## Types de seuillage

### Dur (Hard)

- Les composantes inférieures au seuil sont mises à zéro
- Les autres restent inchangées

### Doux (Soft)

- Les composantes inférieures au seuil sont mises à zéro
- Les autres sont réduites par la valeur du seuil

## Types de seuillage adaptatif

### VisuShrink (v)

- Méthode basée sur le principe de réduction du bruit minimal
- Seuil universel déterminé à partir de l'énergie estimée du bruit

### BayesShrink (b)

- Méthode basée sur l'estimation bayésienne
- Généralement plus efficace pour les bruits variables

## Évaluation de la qualité

Le programme calcule deux métriques pour évaluer la qualité du débruitage :

### MSE (Mean Square Error)

- Mesure la différence moyenne des carrés des erreurs entre pixels
- Plus la valeur est basse, plus les images sont similaires

### PSNR (Peak Signal-to-Noise Ratio)

- Évalue la qualité de reconstruction d'une image
- Plus la valeur est élevée, meilleure est la qualité
- Valeurs typiques : >30 dB (bon), >40 dB (excellent)

## Résolution des problèmes courants

### Erreur "L'image n'existe pas dans le dossier data/x0"

Vérifiez que vous avez bien placé votre image originale dans le dossier `data/x0/` et que le nom spécifié est correct, y compris l'extension du fichier (par exemple, `.png`, `.jpg`).

### Messages "Aucun fichier trouvé dans le dossier data/x0"

Créez le dossier `data/x0/` à la racine du projet et placez-y au moins une image.

### Problème de chemin lors de l'exécution du JAR

Assurez-vous d'exécuter le JAR depuis la racine du projet, où se trouvent les dossiers `data` et `src`. En d'autres termes, le JAR et le dossier `data` doivent être au même niveau.

## Membres du Groupe 7 - ING1 CY-Tech - 2024-2025

- BRECHENMACHER Paul
- CHAPUIS Lucas
- DAVID Bastien
- LESBARRERES Emma
- POIRIER Alexis

---

Ce projet utilise la bibliothèque Apache Commons Math pour les calculs matriciels.
