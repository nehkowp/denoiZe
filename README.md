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

DenoiZe:

1. **Prend** une image originale (non bruitée)
2. **Applique** un bruit gaussien à cette image
3. **Débruite** l'image en utilisant l'ACP
4. **Évalue** la qualité du débruitage (MSE, PSNR)

### Préparation

**Important**: Placez vos images originales dans le dossier `data/x0/`

Les dossiers `data/xB/` (images bruitées) et `data/xR/` (images débruitées) seront créés automatiquement.

### Mode GUI (sans arguments)

Double-cliquez sur le fichier JAR ou exécutez :

```bash
java -jar denoize.jar
```

L'application vous guidera à travers les étapes suivantes :

1. Choix de l'image originale parmi celles présentes dans `data/x0/`
2. Configuration des paramètres de bruitage et débruitage
3. Exécution du processus
4. Affichage des résultats d'évaluation

Il est aussi possible de zoomer sur chacune des images avec "Control + molette".

### Mode CLI (avec arguments)

```bash
java -jar denoize.jar [options]
```

Options disponibles :

| Option                    | Format court    | Description                                                        | Statut                        |
| ------------------------- | --------------- | ------------------------------------------------------------------ | ----------------------------- |
| `--image <nom>`           | `-i <nom>`      | Nom de l'image (dans data/x0/)                                     | **Obligatoire**               |
| `--global`                | `-g`            | Utilise la méthode de débruitage globale                           | _Facultatif_                  |
| `--local`                 | `-l`            | Utilise la méthode de débruitage locale                            | _Facultatif_ (défaut)         |
| `--threshold <type>`      | `-t <type>`     | Type de seuillage: 'hard' ou 'soft'                                | _Facultatif_ (défaut: 'hard') |
| `--shrink <type>`         | `-s <type>`     | Type de seuillage adaptatif: 'v' (VisuShrink) ou 'b' (BayesShrink) | _Facultatif_ (défaut: 'v')    |
| `--sigma <valeur>`        | `-sig <valeur>` | Écart-type du bruit                                                | _Facultatif_ (défaut: 20.0)   |
| `--patch-size <taille>`   | `-p <taille>`   | Taille des patchs (entier impair)                                  | _Facultatif_ (défaut: 7)      |
| `--fenetre-size <taille>` | `-f <taille>`   | Taille des fenêtres locales (50-1000, mode local seulement)        | _Facultatif_ (défaut: 250)    |
| `--help`                  | `-h`            | Affiche l'aide de la commande                                      | _Facultatif_                  |

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

4. Méthode locale avec taille de fenêtre personnalisée :
   ```bash
   java -jar denoize.jar -i lena_gray.png -l -f 150 -p 5
   ```

## Méthodes de débruitage

### Méthode globale

- Analyse l'image entière avec une approche globale par ACP
- Moins gourmande en calculs
- Meilleure préservation des structures globales de l'image
- **Note**: La taille de fenêtre n'est pas utilisée en mode global

### Méthode locale

- Analyse des patches locaux dans l'image en la découpant en fenêtres
- Plus précise pour préserver les détails
- Plus gourmande en calculs
- **Taille de fenêtre**: Contrôle la taille des fenêtres locales (entre 50 et 1000 pixels)
  - Petites fenêtres (50-150) : Plus de détails locaux, mais plus de calculs
  - Fenêtres moyennes (150-400) : Bon compromis entre détails et performance
  - Grandes fenêtres (400-1000) : Moins de calculs, mais analyse moins fine

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

## Exemples pratiques

### Images avec bruit σ = 30

<table>
  <tr>
    <th>Image bruitée (σ=30)</th>
    <th>Image débruitée</th>
  </tr>
  <tr>
    <td><img src="data/xB/30/lena_gray.png" alt="Lena bruitée" width="300"/></td>
    <td><img src="data/xR/lena_gray_local_hard_v.png" alt="Lena débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/mbappe_gray.jpg" alt="Mbappe bruitée" width="300"/></td>
    <td><img src="data/xR/mbappe_gray_local_hard_v.jpg" alt="Mbappe débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/harrypotter_gray.png" alt="Harry Potter bruitée" width="300"/></td>
    <td><img src="data/xR/harrypotter_gray_local_hard_v.png" alt="Harry Potter débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/wemby_gray.png" alt="Wemby bruitée" width="300"/></td>
    <td><img src="data/xR/wemby_gray_local_hard_v.png" alt="Wemby débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/ali_gray.jpg" alt="Ali bruitée" width="300"/></td>
    <td><img src="data/xR/ali_gray_local_hard_v.jpg" alt="Ali débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/crocodilo_gray.jpg" alt="Crocodilo bruitée" width="300"/></td>
    <td><img src="data/xR/crocodilo_gray_local_hard_v.jpg" alt="Crocodilo débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/darkvador_gray.jpg" alt="Dark Vador bruitée" width="300"/></td>
    <td><img src="data/xR/darkvador_gray_local_hard_v.jpg" alt="Dark Vador débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/gekko_gray.jpg" alt="Gekko bruitée" width="300"/></td>
    <td><img src="data/xR/gekko_gray_local_hard_v.jpg" alt="Gekko débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/leclerc_gray.png" alt="Leclerc bruitée" width="300"/></td>
    <td><img src="data/xR/leclerc_gray_local_hard_v.png" alt="Leclerc débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/moto_gray.jpeg" alt="Moto bruitée" width="300"/></td>
    <td><img src="data/xR/moto_gray_local_hard_v.jpeg" alt="Moto débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/nyancat_gray.png" alt="Nyan Cat bruitée" width="300"/></td>
    <td><img src="data/xR/nyancat_gray_local_hard_v.png" alt="Nyan Cat débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/30/steve_gray.jpg" alt="Steve bruitée" width="300"/></td>
    <td><img src="data/xR/steve_gray_local_hard_v.jpg" alt="Steve débruitée" width="300"/></td>
  </tr>
</table>

### Images avec bruit σ = 20

<table>
  <tr>
    <th>Image bruitée (σ=20)</th>
    <th>Image débruitée</th>
  </tr>
  <tr>
    <td><img src="data/xB/20/lena_gray.png" alt="Lena bruitée" width="300"/></td>
    <td><img src="data/xR/lena_gray_local_hard_v.png" alt="Lena débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/mbappe_gray.jpg" alt="Mbappe bruitée" width="300"/></td>
    <td><img src="data/xR/mbappe_gray_local_hard_v.jpg" alt="Mbappe débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/harrypotter_gray.png" alt="Harry Potter bruitée" width="300"/></td>
    <td><img src="data/xR/harrypotter_gray_local_hard_v.png" alt="Harry Potter débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/wemby_gray.png" alt="Wemby bruitée" width="300"/></td>
    <td><img src="data/xR/wemby_gray_local_hard_v.png" alt="Wemby débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/ali_gray.jpg" alt="Ali bruitée" width="300"/></td>
    <td><img src="data/xR/ali_gray_local_hard_v.jpg" alt="Ali débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/crocodilo_gray.jpg" alt="Crocodilo bruitée" width="300"/></td>
    <td><img src="data/xR/crocodilo_gray_local_hard_v.jpg" alt="Crocodilo débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/darkvador_gray.jpg" alt="Dark Vador bruitée" width="300"/></td>
    <td><img src="data/xR/darkvador_gray_local_hard_v.jpg" alt="Dark Vador débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/gekko_gray.jpg" alt="Gekko bruitée" width="300"/></td>
    <td><img src="data/xR/gekko_gray_local_hard_v.jpg" alt="Gekko débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/leclerc_gray.png" alt="Leclerc bruitée" width="300"/></td>
    <td><img src="data/xR/leclerc_gray_local_hard_v.png" alt="Leclerc débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/moto_gray.jpeg" alt="Moto bruitée" width="300"/></td>
    <td><img src="data/xR/moto_gray_local_hard_v.jpeg" alt="Moto débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/nyancat_gray.png" alt="Nyan Cat bruitée" width="300"/></td>
    <td><img src="data/xR/nyancat_gray_local_hard_v.png" alt="Nyan Cat débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/20/steve_gray.jpg" alt="Steve bruitée" width="300"/></td>
    <td><img src="data/xR/steve_gray_local_hard_v.jpg" alt="Steve débruitée" width="300"/></td>
  </tr>
</table>

### Images avec bruit σ = 10

<table>
  <tr>
    <th>Image bruitée (σ=10)</th>
    <th>Image débruitée</th>
  </tr>
  <tr>
    <td><img src="data/xB/10/lena_gray.png" alt="Lena bruitée" width="300"/></td>
    <td><img src="data/xR/lena_gray_local_hard_v.png" alt="Lena débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/mbappe_gray.jpg" alt="Mbappe bruitée" width="300"/></td>
    <td><img src="data/xR/mbappe_gray_local_hard_v.jpg" alt="Mbappe débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/harrypotter_gray.png" alt="Harry Potter bruitée" width="300"/></td>
    <td><img src="data/xR/harrypotter_gray_local_hard_v.png" alt="Harry Potter débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/wemby_gray.png" alt="Wemby bruitée" width="300"/></td>
    <td><img src="data/xR/wemby_gray_local_hard_v.png" alt="Wemby débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/ali_gray.jpg" alt="Ali bruitée" width="300"/></td>
    <td><img src="data/xR/ali_gray_local_hard_v.jpg" alt="Ali débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/crocodilo_gray.jpg" alt="Crocodilo bruitée" width="300"/></td>
    <td><img src="data/xR/crocodilo_gray_local_hard_v.jpg" alt="Crocodilo débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/darkvador_gray.jpg" alt="Dark Vador bruitée" width="300"/></td>
    <td><img src="data/xR/darkvador_gray_local_hard_v.jpg" alt="Dark Vador débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/gekko_gray.jpg" alt="Gekko bruitée" width="300"/></td>
    <td><img src="data/xR/gekko_gray_local_hard_v.jpg" alt="Gekko débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/leclerc_gray.png" alt="Leclerc bruitée" width="300"/></td>
    <td><img src="data/xR/leclerc_gray_local_hard_v.png" alt="Leclerc débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/moto_gray.jpeg" alt="Moto bruitée" width="300"/></td>
    <td><img src="data/xR/moto_gray_local_hard_v.jpeg" alt="Moto débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/nyancat_gray.png" alt="Nyan Cat bruitée" width="300"/></td>
    <td><img src="data/xR/nyancat_gray_local_hard_v.png" alt="Nyan Cat débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/10/steve_gray.jpg" alt="Steve bruitée" width="300"/></td>
    <td><img src="data/xR/steve_gray_local_hard_v.jpg" alt="Steve débruitée" width="300"/></td>
  </tr>
</table>

### Autres Images

<table>
  <tr>
    <th>Image bruitée</th>
    <th>Image débruitée</th>
  </tr>
  <tr>
    <td><img src="data/xB/lena_gray.png" alt="Lena bruitée" width="300"/></td>
    <td><img src="data/xR/lena_gray_local_hard_v.png" alt="Lena débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/lena.png" alt="Lena couleur bruitée" width="300"/></td>
    <td><img src="data/xR/lena_local_hard_v.png" alt="Lena couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/Lenna.png" alt="Lenna bruitée" width="300"/></td>
    <td><img src="data/xR/Lenna_local_hard_v.png" alt="Lenna débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/mbappe_gray.jpg" alt="Mbappe bruitée" width="300"/></td>
    <td><img src="data/xR/mbappe_gray_local_hard_v.jpg" alt="Mbappe débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/mbappe.jpg" alt="Mbappe couleur bruitée" width="300"/></td>
    <td><img src="data/xR/mbappe_local_hard_v.jpg" alt="Mbappe couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/harrypotter_gray.png" alt="Harry Potter bruitée" width="300"/></td>
    <td><img src="data/xR/harrypotter_gray_local_hard_v.png" alt="Harry Potter débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/harrypotter.png" alt="Harry Potter couleur bruitée" width="300"/></td>
    <td><img src="data/xR/harrypotter_local_hard_v.png" alt="Harry Potter couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/wemby_gray.png" alt="Wemby bruitée" width="300"/></td>
    <td><img src="data/xR/wemby_gray_local_hard_v.png" alt="Wemby débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/ali_gray.jpg" alt="Ali bruitée" width="300"/></td>
    <td><img src="data/xR/ali_gray_local_hard_v.jpg" alt="Ali débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/ali.jpg" alt="Ali couleur bruitée" width="300"/></td>
    <td><img src="data/xR/ali_local_hard_v.jpg" alt="Ali couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/crocodilo_gray.jpg" alt="Crocodilo bruitée" width="300"/></td>
    <td><img src="data/xR/crocodilo_gray_local_hard_v.jpg" alt="Crocodilo débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/crocodilo.jpg" alt="Crocodilo couleur bruitée" width="300"/></td>
    <td><img src="data/xR/crocodilo_local_hard_v.jpg" alt="Crocodilo couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/darkvador_gray.jpg" alt="Dark Vador bruitée" width="300"/></td>
    <td><img src="data/xR/darkvador_gray_local_hard_v.jpg" alt="Dark Vador débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/darkvador.jpg" alt="Dark Vador couleur bruitée" width="300"/></td>
    <td><img src="data/xR/darkvador_local_hard_v.jpg" alt="Dark Vador couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/gekko_gray.jpg" alt="Gekko bruitée" width="300"/></td>
    <td><img src="data/xR/gekko_gray_local_hard_v.jpg" alt="Gekko débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/gekko.jpg" alt="Gekko couleur bruitée" width="300"/></td>
    <td><img src="data/xR/gekko_local_hard_v.jpg" alt="Gekko couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/leclerc_gray.png" alt="Leclerc bruitée" width="300"/></td>
    <td><img src="data/xR/leclerc_gray_local_hard_v.png" alt="Leclerc débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/moto_gray.jpeg" alt="Moto bruitée" width="300"/></td>
    <td><img src="data/xR/moto_gray_local_hard_v.jpeg" alt="Moto débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/moto.jpeg" alt="Moto couleur bruitée" width="300"/></td>
    <td><img src="data/xR/moto_local_hard_v.jpeg" alt="Moto couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/nyancat_gray.png" alt="Nyan Cat bruitée" width="300"/></td>
    <td><img src="data/xR/nyancat_gray_local_hard_v.png" alt="Nyan Cat débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/nyancat.png" alt="Nyan Cat couleur bruitée" width="300"/></td>
    <td><img src="data/xR/nyancat_local_hard_v.png" alt="Nyan Cat couleur débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/steve_gray.jpg" alt="Steve bruitée" width="300"/></td>
    <td><img src="data/xR/steve_gray_local_hard_v.jpg" alt="Steve débruitée" width="300"/></td>
  </tr>
  <tr>
    <td><img src="data/xB/steve.jpg" alt="Steve couleur bruitée" width="300"/></td>
    <td><img src="data/xR/steve_local_hard_v.jpg" alt="Steve couleur débruitée" width="300"/></td>
  </tr>
</table>

> **Note :** Toutes les images débruitées utilisent la méthode locale avec seuillage dur et VisuShrink (`local_hard_v`).

## Résolution des problèmes courants

### Erreur "L'image n'existe pas dans le dossier data/x0"

Vérifiez que vous avez bien placé votre image originale dans le dossier `data/x0/` et que le nom spécifié est correct, y compris l'extension du fichier (par exemple, `.png`, `.jpg`).

### Messages "Aucun fichier trouvé dans le dossier data/x0"

Créez le dossier `data/x0/` à la racine du projet et placez-y au moins une image.

### Problème de chemin lors de l'exécution du JAR

Assurez-vous d'exécuter le JAR depuis la racine du projet, où se trouvent les dossiers `data` et `src`. En d'autres termes, le JAR et le dossier `data` doivent être au même niveau.

### Erreur de taille de fenêtre

En mode local, la taille de fenêtre doit être comprise entre 50 et 1000 pixels. Si l'erreur persiste, vérifiez que votre image est suffisamment grande par rapport à la taille de fenêtre choisie.

## Membres du Groupe 7 - ING1 CY-Tech - 2024-2025

- BRECHENMACHER Paul
- CHAPUIS Lucas
- DAVID Bastien
- LESBARRERES Emma
- POIRIER Alexis

---

Ce projet utilise la bibliothèque Apache Commons Math pour les calculs matriciels.
