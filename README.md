# denoiℤe - Débruitage d'Images par Analyse en Composantes Principales

## À propos

Ce projet implémente une méthode de débruitage d'images basée sur l'Analyse en Composantes Principales (ACP). Cette approche utilise la décomposition en patchs et la projection dans un espace de dimension réduite pour éliminer le bruit gaussien additif.

## Prérequis

- Eclipse avec Maven
- Git

## Installation et configuration

### 1. Cloner le projet Git

```bash
git clone https://github.com/nehkowp/denoiZe.git
cd denoiZe
```

### 2. Importer le projet dans Eclipse

1. Ouvrez Eclipse
2. Allez dans `File > Import`
3. Sélectionnez `Maven > Existing Maven Projects`
4. Cliquez sur `Next`
5. Naviguez jusqu'au dossier du projet que vous venez de cloner
6. Assurez-vous que le fichier `pom.xml` est détecté et sélectionné
7. Cliquez sur `Finish`

### 3. Configuration Maven

Le projet est configuré avec un fichier `pom.xml` qui définit les dépendances et la structure du projet. Si vous rencontrez des problèmes avec la configuration Maven, suivez ces étapes :

1. Clic droit sur le projet > `Maven > Update Project...`
2. Cochez l'option "Force Update of Snapshots/Releases"
3. Cliquez sur `OK`

Si vous voyez l'erreur "Cannot nest 'denoiZe/src/main/resources' inside 'denoiZe/src'", suivez ces étapes supplémentaires :

1. Ouvrez le fichier `pom.xml`
2. Vérifiez que la section `<build>` est correctement configurée comme ci-dessous :

```xml
<build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>17</source>
                <target>17</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

3. Sauvegardez le fichier et mettez à jour le projet Maven à nouveau

### 4. Dépendances

Le projet utilise la bibliothèque Apache Commons Math pour les calculs matriciels. Si cette dépendance n'est pas correctement chargée, vérifiez qu'elle est bien présente dans le fichier `pom.xml` :

```xml
<dependencies>
    <!-- Dépendance pour Apache Commons Math -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-math3</artifactId>
        <version>3.6.1</version>
    </dependency>
</dependencies>
```

Si vous rencontrez une erreur `ClassNotFoundException` pour `org.apache.commons.math3.linear.RealMatrix`, essayez de nettoyer et reconstruire le projet :

1. Projet > `Clean...`
2. Clic droit sur le projet > `Maven > Clean`
3. Clic droit sur le projet > `Maven > Install`

## Structure du projet

```
debruitage-image-acp/
├── src/
│   ├── exception/
│   │   ├── MatriceException.java
│   │   └── VecteurException.java
│   ├── model/
│   │   ├── base/
│   │   │   ├── Matrice.java
│   │   │   ├── Vecteur.java
│   │   │   └── Position.java
│   │   └── resultat/
│   │       ├── ResultatACP.java
│   │       ├── ResultatMoyCov.java
│   │       ├── ResultatPatch.java
│   │       └── ResultatVecteur.java
│   ├── service/
│   │   ├── debruitage/
│   │   │   └── DebruiteurImage.java
│   │   ├── gestion/
│   │   │   ├── GestionnaireImages.java
│   │   │   └── GestionnairePatchs.java
│   │   └── traitement/
│   │       ├── ProcesseurACP.java
│   │       └── ProcesseurBruit.java
│   └── ui/
│       └── InterfaceUtilisateur.java
├── data/
│   ├── images/
│   │   ├── originales/
│   │   └── bruitees/
│   └── resultats/
├── pom.xml
└── README.md
```

## Exécution du projet

Pour exécuter le projet, localisez la classe principale `DebruiteurImage.java` ou `InterfaceUtilisateur.java` et exécutez-la comme une application Java :

1. Clic droit sur la classe principale
2. Sélectionnez `Run As > Java Application`

## Utilisation

1. Chargez une image à débruiter
2. Sélectionnez les paramètres de débruitage (taille des patchs, méthode de seuillage, etc.)
3. Exécutez le processus de débruitage
4. Visualisez et comparez les résultats

## Résolution des problèmes courants

### Erreur "Cannot nest 'denoiZe/src/main/resources' inside 'denoiZe/src'"

Cette erreur est liée à la structure du projet Maven. Elle peut être résolue en modifiant la configuration dans le fichier `pom.xml` comme indiqué dans la section "Configuration Maven".

### Erreur "NoClassDefFoundError: org/apache/commons/math3/linear/RealMatrix"

Cette erreur indique que la dépendance Apache Commons Math n'est pas correctement chargée. Suivez les étapes de la section "Dépendances" pour résoudre ce problème.

---

Groupe 7 - ING1 CY-Tech - 2024-2025

BRECHENMACHER Paul
CHAPUIS Lucas
DAVID Bastien
LESBARRERES Emma  
POIRIER Alexis
