#!/bin/bash

# Script de test complet pour le projet denoiZe (débruitage d'images par ACP)
# Ce script effectue une analyse exhaustive avec tous les paramètres possibles

# Répertoire contenant le JAR
JAR_FILE="denoize.jar"

# Répertoires
INPUT_DIR="data/x0"
NOISY_DIR="data/xB"
OUTPUT_DIR="data/xR/results"

# Créer les dossiers nécessaires s'ils n'existent pas
mkdir -p "$OUTPUT_DIR"
mkdir -p "$NOISY_DIR"

# === PARAMÈTRES DE TEST ===
# Méthodes de débruitage
METHODS=("local" "global")

# Types de seuillage (Hard/Soft Thresholding)
THRESHOLD_TYPES=("hard" "soft")

# Types de seuillage adaptatif (VisuShrink/BayesShrink)
SHRINK_TYPES=("v" "b")

# Tailles de patchs selon la méthode
PATCH_SIZES_LOCAL=(5 7 9)              # Pour le mode local
PATCH_SIZES_GLOBAL=(17 19 21 23 25)    # Pour le mode global

# Tailles de fenêtres pour le mode local (entre 50 et 1000)
FENETRE_SIZES=(50 75 100 150 200 250 300 400 500 750 1000)

# Écarts-types du bruit pour analyser la robustesse
SIGMAS=(5 10 15 20 25 30 35 40)

# === FONCTIONS UTILITAIRES ===

# Écrit l'en-tête du CSV
write_csv_header() {
    echo "image,method,threshold,shrink,patch_size,fenetre_size,sigma,mse,psnr,execution_time,success" > "$OUTPUT_DIR/resultats_complets.csv"
}

# Fonction pour obtenir les dimensions d'une image
get_image_dimensions() {
    local image_path="$1"
    # Utiliser file ou identify (ImageMagick) si disponible
    if command -v identify >/dev/null 2>&1; then
        identify -format "%w %h" "$image_path" 2>/dev/null
    else
        echo "512 512"  # Valeurs par défaut si impossible de détecter
    fi
}

# Vérifie si la taille de fenêtre est compatible avec l'image
is_fenetre_compatible() {
    local image_path="$1"
    local fenetre_size="$2"
    
    read -r width height <<< $(get_image_dimensions "$image_path")
    local min_dimension=$((width < height ? width : height))
    
    [ "$fenetre_size" -le "$min_dimension" ]
}

# Fonction principale pour exécuter le débruitage
run_denoizing() {
    local image_name="$1"
    local method="$2"
    local threshold="$3"
    local shrink="$4"
    local patch_size="$5"
    local fenetre_size="$6"
    local sigma="$7"
    
    # Préparer les arguments de méthode
    local method_arg=""
    if [ "$method" == "global" ]; then
        method_arg="-g"
    else
        method_arg="-l"
    fi
    
    echo "📊 Test: $image_name | $method | $threshold | $shrink | patch:$patch_size | fenetre:$fenetre_size | sigma:$sigma"
    
    # Construire la commande
    local cmd="java -jar $JAR_FILE -i $image_name $method_arg -t $threshold -s $shrink -p $patch_size -sig $sigma"
    
    # Ajouter la taille de fenêtre seulement en mode local
    if [ "$method" == "local" ]; then
        cmd="$cmd -f $fenetre_size"
    fi
    
    # Mesurer le temps d'exécution
    local start_time=$(date +%s.%N)
    
    # Exécuter la commande et capturer la sortie
    local output=$(eval "$cmd" 2>&1)
    local exit_code=$?
    
    local end_time=$(date +%s.%N)
    local execution_time=$(echo "$end_time - $start_time" | bc -l)
    execution_time=$(printf "%.2f" "$execution_time")
    
    # Déterminer le succès
    local success="true"
    if [ $exit_code -ne 0 ]; then
        success="false"
    fi
    
    # Extraire MSE et PSNR
    local mse=$(echo "$output" | grep -o "MSE[^0-9]*[0-9.]*" | grep -o "[0-9.]*" | head -1)
    local psnr=$(echo "$output" | grep -o "PSNR[^0-9]*[0-9.]*" | grep -o "[0-9.]*" | head -1)
    
    # Si les valeurs sont vides, mettre N/A
    [ -z "$mse" ] && mse="N/A"
    [ -z "$psnr" ] && psnr="N/A"
    
    # Enregistrer dans le CSV
    local fenetre_display="$fenetre_size"
    if [ "$method" == "global" ]; then
        fenetre_display="N/A"
    fi
    
    echo "$image_name,$method,$threshold,$shrink,$patch_size,$fenetre_display,$sigma,$mse,$psnr,$execution_time,$success" >> "$OUTPUT_DIR/resultats_complets.csv"
    
    # Affichage du résultat
    if [ "$success" == "true" ]; then
        echo "   ✅ MSE=$mse, PSNR=$psnr dB (${execution_time}s)"
    else
        echo "   ❌ Échec du traitement"
    fi
}

# Fonction pour calculer le nombre total de tests
calculate_total_tests() {
    local num_images=$1
    local total=0
    
    # Tests en mode local
    local local_tests=$((${#THRESHOLD_TYPES[@]} * ${#SHRINK_TYPES[@]} * ${#PATCH_SIZES_LOCAL[@]} * ${#FENETRE_SIZES[@]} * ${#SIGMAS[@]}))
    
    # Tests en mode global
    local global_tests=$((${#THRESHOLD_TYPES[@]} * ${#SHRINK_TYPES[@]} * ${#PATCH_SIZES_GLOBAL[@]} * ${#SIGMAS[@]}))
    
    total=$(((local_tests + global_tests) * num_images))
    echo $total
}

# === VÉRIFICATIONS PRÉLIMINAIRES ===

# Vérifier si le JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ Erreur: Le fichier $JAR_FILE n'a pas été trouvé."
    echo "   Assurez-vous d'avoir compilé le projet et que le JAR est dans le répertoire courant."
    exit 1
fi

# Vérifier la disponibilité de bc pour les calculs
if ! command -v bc >/dev/null 2>&1; then
    echo "❌ Erreur: 'bc' est requis pour les calculs. Installez-le avec:"
    echo "   Ubuntu/Debian: sudo apt-get install bc"
    echo "   macOS: brew install bc"
    exit 1
fi

# Récupérer toutes les images
shopt -s nullglob
image_files=("$INPUT_DIR"/*.{png,jpg,jpeg,bmp,PNG,JPG,JPEG,BMP})
shopt -u nullglob

if [ ${#image_files[@]} -eq 0 ]; then
    echo "❌ Erreur: Aucune image trouvée dans le dossier $INPUT_DIR"
    echo "   Veuillez placer vos images originales dans le dossier data/x0/"
    exit 1
fi

# === INITIALISATION ===

echo "🚀 DÉMARRAGE DES TESTS EXHAUSTIFS DE DÉBRUITAGE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 Configuration des tests:"
echo "   🖼️  Nombre d'images: ${#image_files[@]}"
echo "   🎛️  Méthodes: ${METHODS[*]}"
echo "   🔧 Types de seuillage: ${THRESHOLD_TYPES[*]}"
echo "   📈 Seuillage adaptatif: ${SHRINK_TYPES[*]}"
echo "   📐 Patchs (local): ${PATCH_SIZES_LOCAL[*]}"
echo "   📐 Patchs (global): ${PATCH_SIZES_GLOBAL[*]}"
echo "   🪟 Tailles de fenêtre: ${FENETRE_SIZES[*]}"
echo "   📊 Sigmas: ${SIGMAS[*]}"

# Calculer le nombre total de tests
total_tests=$(calculate_total_tests ${#image_files[@]})
echo "   🎯 Total de tests prévus: $total_tests"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Initialiser le CSV
write_csv_header

# Variables de progression
current_test=0
start_global_time=$(date +%s)

# === BOUCLE PRINCIPALE ===

for image_file in "${image_files[@]}"; do
    image_name=$(basename "$image_file")
    echo ""
    echo "🖼️  === TRAITEMENT DE L'IMAGE: $image_name ==="
    
    # Obtenir les dimensions de l'image
    read -r img_width img_height <<< $(get_image_dimensions "$image_file")
    min_dimension=$((img_width < img_height ? img_width : img_height))
    echo "   📏 Dimensions: ${img_width}×${img_height} (min: $min_dimension)"
    
    # Filtrer les tailles de fenêtre compatibles
    compatible_fenetre_sizes=()
    for size in "${FENETRE_SIZES[@]}"; do
        if [ "$size" -le "$min_dimension" ]; then
            compatible_fenetre_sizes+=("$size")
        fi
    done
    
    if [ ${#compatible_fenetre_sizes[@]} -eq 0 ]; then
        echo "   ⚠️  Aucune taille de fenêtre compatible, utilisation de la taille minimale"
        compatible_fenetre_sizes=(50)
    fi
    
    echo "   🪟 Fenêtres compatibles: ${compatible_fenetre_sizes[*]}"
    echo ""
    
    # Tests en MODE LOCAL
    echo "   🧩 === TESTS MODE LOCAL ==="
    for threshold in "${THRESHOLD_TYPES[@]}"; do
        for shrink in "${SHRINK_TYPES[@]}"; do
            for patch_size in "${PATCH_SIZES_LOCAL[@]}"; do
                for fenetre_size in "${compatible_fenetre_sizes[@]}"; do
                    for sigma in "${SIGMAS[@]}"; do
                        ((current_test++))
                        
                        # Affichage de progression
                        local progress=$(echo "scale=1; $current_test*100/$total_tests" | bc)
                        echo "   🎯 Test $current_test/$total_tests (${progress}%)"
                        
                        run_denoizing "$image_name" "local" "$threshold" "$shrink" "$patch_size" "$fenetre_size" "$sigma"
                    done
                done
            done
        done
    done
    
    # Tests en MODE GLOBAL
    echo ""
    echo "   📊 === TESTS MODE GLOBAL ==="
    for threshold in "${THRESHOLD_TYPES[@]}"; do
        for shrink in "${SHRINK_TYPES[@]}"; do
            for patch_size in "${PATCH_SIZES_GLOBAL[@]}"; do
                for sigma in "${SIGMAS[@]}"; do
                    ((current_test++))
                    
                    # Affichage de progression
                    local progress=$(echo "scale=1; $current_test*100/$total_tests" | bc)
                    echo "   🎯 Test $current_test/$total_tests (${progress}%)"
                    
                    run_denoizing "$image_name" "global" "$threshold" "$shrink" "$patch_size" "250" "$sigma"
                done
            done
        done
    done
    
    echo "   ✅ Image $image_name terminée"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
done

# === ANALYSE DES RÉSULTATS ===

end_global_time=$(date +%s)
total_duration=$((end_global_time - start_global_time))
total_duration_formatted=$(printf "%02d:%02d:%02d" $((total_duration/3600)) $((total_duration%3600/60)) $((total_duration%60)))

echo ""
echo "🎉 === TESTS TERMINÉS ==="
echo "⏱️  Durée totale: $total_duration_formatted"
echo "📊 Tests effectués: $current_test"
echo "📁 Résultats sauvegardés dans: $OUTPUT_DIR/resultats_complets.csv"

# Analyser les résultats
if [ -f "$OUTPUT_DIR/resultats_complets.csv" ]; then
    echo ""
    echo "📈 === ANALYSE DES MEILLEURS RÉSULTATS ==="
    
    # Top 10 des meilleurs PSNR
    echo "🏆 TOP 10 - MEILLEURS PSNR:"
    echo "Rang | PSNR  | MSE   | Image | Méthode | Seuillage | Shrink | Patch | Fenêtre | Sigma"
    echo "-----|-------|-------|-------|---------|-----------|--------|-------|---------|-------"
    
    # Trier par PSNR décroissant, ignorer l'en-tête et les N/A
    awk -F',' 'NR>1 && $9!="N/A" {print $0}' "$OUTPUT_DIR/resultats_complets.csv" | \
    sort -t',' -k9,9nr | \
    head -10 | \
    nl | \
    while IFS=$'\t' read -r rank line; do
        IFS=',' read -r img method threshold shrink patch fenetre sigma mse psnr time success <<< "$line"
        printf "%4s | %5s | %5s | %-8s | %-7s | %-9s | %-6s | %-5s | %-7s | %s\n" \
               "$rank" "$psnr" "$mse" "${img:0:8}" "$method" "$threshold" "$shrink" "$patch" "$fenetre" "$sigma"
    done
    
    echo ""
    echo "📊 === STATISTIQUES GLOBALES ==="
    
    # Compter les succès/échecs
    local total_lines=$(awk 'NR>1' "$OUTPUT_DIR/resultats_complets.csv" | wc -l)
    local success_count=$(awk -F',' 'NR>1 && $11=="true"' "$OUTPUT_DIR/resultats_complets.csv" | wc -l)
    local failure_count=$((total_lines - success_count))
    local success_rate=$(echo "scale=1; $success_count*100/$total_lines" | bc)
    
    echo "✅ Tests réussis: $success_count/$total_lines (${success_rate}%)"
    echo "❌ Tests échoués: $failure_count"
    
    # Statistiques par méthode
    echo ""
    echo "📈 Moyennes PSNR par méthode:"
    awk -F',' 'NR>1 && $9!="N/A" && $11=="true" {method[$2] += $9; count[$2]++} END {for (m in method) printf "   %s: %.2f dB\n", m, method[m]/count[m]}' "$OUTPUT_DIR/resultats_complets.csv"
    
    # Meilleur paramétrage par sigma
    echo ""
    echo "🎯 Meilleures configurations par niveau de bruit:"
    for sigma in "${SIGMAS[@]}"; do
        echo "   Sigma $sigma:"
        awk -F',' -v s="$sigma" 'NR>1 && $7==s && $9!="N/A" && $11=="true"' "$OUTPUT_DIR/resultats_complets.csv" | \
        sort -t',' -k9,9nr | \
        head -1 | \
        awk -F',' '{printf "     PSNR=%.2f | %s | %s-%s | patch=%s | fenetre=%s\n", $9, $2, $3, $4, $5, $6}'
    done
fi

echo ""
echo "💡 === CONSEILS D'UTILISATION ==="
echo "📋 Pour analyser les résultats:"
echo "   • Ouvrir le CSV dans Excel ou LibreOffice"
echo "   • Utiliser des filtres pour analyser par paramètre"
echo "   • Créer des graphiques PSNR vs paramètres"
echo ""
echo "🔍 Commandes utiles:"
echo "   • Afficher en colonnes: column -t -s, $OUTPUT_DIR/resultats_complets.csv | less -S"
echo "   • Filtrer par méthode: awk -F',' '\$2==\"local\"' $OUTPUT_DIR/resultats_complets.csv"
echo "   • Statistiques rapides: sort -t',' -k9,9nr $OUTPUT_DIR/resultats_complets.csv | head -20"
echo ""
echo "🎯 Le fichier de résultats contient toutes les métriques pour une analyse approfondie!"
