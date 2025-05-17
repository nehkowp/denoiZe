#!/bin/bash

# Script pour tester différentes combinaisons de paramètres de débruitage
# pour le projet denoiZe (débruitage d'images par ACP)

# Répertoire contenant le JAR
JAR_FILE="denoize.jar"

# Répertoires
INPUT_DIR="data/x0"
NOISY_DIR="data/xB"
OUTPUT_DIR="data/xR/results"

# Créer les dossiers nécessaires s'ils n'existent pas
mkdir -p "$OUTPUT_DIR"
mkdir -p "$NOISY_DIR"

# Paramètres à tester
METHODS=("local" "global")              # Méthodes: locale ou globale
THRESHOLD_TYPES=("hard" "soft")         # Types de seuillage: dur ou doux
SHRINK_TYPES=("v" "b")                  # VisuShrink ou BayesShrink
PATCH_SIZES=(5 7 9)                     # Tailles de patchs
SIGMAS=(10 20 30)                       # Écarts-types du bruit

# Écrit un en-tête pour le rapport CSV
echo "image,method,threshold,shrink,patch_size,sigma,mse,psnr" > "$OUTPUT_DIR/resultats.csv"

# Fonction pour exécuter le débruitage avec une combinaison de paramètres
run_denoizing() {
    local image_name=$(basename "$1")
    local method=$2
    local threshold=$3
    local shrink=$4
    local patch_size=$5
    local sigma=$6
    
    # Préparer les arguments
    local method_arg=""
    if [ "$method" == "global" ]; then
        method_arg="-g"
    else
        method_arg="-l"
    fi
    
    echo "📊 Traitement: $image_name"
    echo "   Paramètres: method=$method, threshold=$threshold, shrink=$shrink, patch=$patch_size, sigma=$sigma"
    
    # Construire la commande complète
    local cmd="java -jar $JAR_FILE -i $image_name $method_arg -t $threshold -s $shrink -p $patch_size -sig $sigma"
    
    # Exécuter la commande et capturer la sortie
    local output=$(eval "$cmd" 2>&1)
    
    # Extraire MSE et PSNR des résultats
    local mse=$(echo "$output" | grep -o "MSE[^0-9]*[0-9.]*" | grep -o "[0-9.]*")
    local psnr=$(echo "$output" | grep -o "PSNR[^0-9]*[0-9.]*" | grep -o "[0-9.]*")
    
    # Si les valeurs sont vides, mettre N/A
    [ -z "$mse" ] && mse="N/A"
    [ -z "$psnr" ] && psnr="N/A"
    
    # Enregistrer les résultats dans le CSV
    echo "$image_name,$method,$threshold,$shrink,$patch_size,$sigma,$mse,$psnr" >> "$OUTPUT_DIR/resultats.csv"
    
    echo "   ✅ Traitement terminé"
    echo "   📈 Qualité: MSE=$mse, PSNR=$psnr dB"
    echo ""
}

# Vérifier si le JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Erreur: Le fichier $JAR_FILE n'a pas été trouvé."
    echo "   Assurez-vous d'avoir compilé le projet et que le JAR est dans le répertoire courant."
    exit 1
fi

# Récupérer toutes les images du dossier d'entrée
shopt -s nullglob
image_files=("$INPUT_DIR"/*.{png,jpg,jpeg,bmp})
shopt -u nullglob

# Vérifier qu'il y a des images
if [ ${#image_files[@]} -eq 0 ]; then
    echo "❌ Erreur: Aucune image trouvée dans le dossier $INPUT_DIR"
    echo "   Veuillez placer vos images originales dans le dossier data/x0/"
    exit 1
fi

echo "🔍 Démarrage des tests de débruitage sur ${#image_files[@]} images"
echo "📁 Dossier de sortie: $OUTPUT_DIR"
echo "📊 Fichier de résultats: $OUTPUT_DIR/resultats.csv"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Calculer le nombre total de tests
total_tests=$((${#image_files[@]} * ${#METHODS[@]} * ${#THRESHOLD_TYPES[@]} * ${#SHRINK_TYPES[@]} * ${#PATCH_SIZES[@]} * ${#SIGMAS[@]}))
current_test=0

# Boucle principale
for image_file in "${image_files[@]}"; do
    image_name=$(basename "$image_file")
    echo "🖼️  Image: $image_name"
    
    for method in "${METHODS[@]}"; do
        for threshold in "${THRESHOLD_TYPES[@]}"; do
            for shrink in "${SHRINK_TYPES[@]}"; do
                for patch_size in "${PATCH_SIZES[@]}"; do
                    for sigma in "${SIGMAS[@]}"; do
                        ((current_test++))
                        echo "Test $current_test/$total_tests ($(printf "%.1f" $(echo "scale=1; $current_test*100/$total_tests" | bc))%)"
                        run_denoizing "$image_name" "$method" "$threshold" "$shrink" "$patch_size" "$sigma"
                    done
                done
            done
        done
    done
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
done

echo "🎉 Tests terminés! Tous les résultats sont dans le dossier $OUTPUT_DIR"

# Analyse des résultats
if [ -f "$OUTPUT_DIR/resultats.csv" ]; then
    echo "📈 Analyse des meilleurs résultats selon PSNR:"
    # Ignorer l'en-tête, trier par PSNR décroissant (colonne 8)
    sort -t',' -k8,8nr "$OUTPUT_DIR/resultats.csv" | head -6 | tail -5 | while IFS=, read -r img method threshold shrink patch sigma mse psnr; do
        if [ "$psnr" != "N/A" ]; then
            echo "   ⭐ PSNR=$psnr, MSE=$mse : $img (method=$method, threshold=$threshold, shrink=$shrink, patch=$patch, sigma=$sigma)"
        fi
    done
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "💡 Conseil: Pour visualiser les résultats, ouvrez le fichier CSV dans Excel ou utilisez:"
echo "    $ column -t -s, $OUTPUT_DIR/resultats.csv | less -S"