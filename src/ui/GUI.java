package ui;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.base.Img;
import model.base.Pixel;
import service.bruit.BruiteurImage;
import service.debruitage.DebruiteurImage;
import service.evaluation.EvaluationQualite;

public class GUI extends Application {
    
    // Images
    private ImageView originalImageView;
    private ImageView noisedImageView;
    private ImageView denoisedImageView;
    
    // Conteneurs des images
    private StackPane originalImagePane;
    private StackPane noisedImagePane;
    private StackPane denoisedImagePane;
    private StackPane statsPane;
    
    // Labels pour les panneaux
    private Label originalLabel;
    private Label noisedLabel;
    private Label denoisedLabel;
    private Label statsLabel;
    
    // Stockage des versions Img pour calculs
    private Img originalImg;
    private Img noisedImg;
    private Img denoisedImg;
    
    // Paramètres
    private ComboBox<String> choixMode;
    private VBox widgetsBox;
    private Slider sigmaSlider;
    private ToggleGroup tgTypeSeuillage;
    private ToggleGroup tgFonctionSeuillage;
    private ToggleGroup tgPatchLocal;
    private ToggleGroup tgPatchGlobal;
    private double currentSigma = 10.0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Denoiℤe - Débruitage d'images par ACP");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #121212;");
        root.setPrefSize(1400, 900);

        // Grille 2x2 pour les 4 panneaux
        GridPane gridImages = createGridPane();
        
        // Panneau des paramètres
        VBox paramPane = createParamPane(primaryStage);
        
        root.setCenter(gridImages);
        root.setLeft(paramPane);

        BorderPane.setMargin(gridImages, new Insets(20));
        BorderPane.setMargin(paramPane, new Insets(20, 0, 20, 20));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Déclencher l'action du ComboBox pour afficher les bons widgets dès le départ
        choixMode.getSelectionModel().selectFirst();
        choixMode.fireEvent(new javafx.event.ActionEvent());
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        
        // Création des panneaux d'image
        originalImagePane = createImagePane("Image Originale");
        noisedImagePane = createImagePane("Image Bruitée");
        denoisedImagePane = createImagePane("Image Débruitée");
        statsPane = createStatsPane();
        
        // Récupération des labels pour les modifier plus tard
        originalLabel = (Label) originalImagePane.getChildren().get(0);
        noisedLabel = (Label) noisedImagePane.getChildren().get(0);
        denoisedLabel = (Label) denoisedImagePane.getChildren().get(0);
        statsLabel = (Label) statsPane.getChildren().get(0);
        
        // Configuration des ImageViews
        originalImageView = new ImageView();
        noisedImageView = new ImageView();
        denoisedImageView = new ImageView();
        
        configureImageView(originalImageView);
        configureImageView(noisedImageView);
        configureImageView(denoisedImageView);
        
        // Ajout des ImageViews aux panneaux
        originalImagePane.getChildren().add(originalImageView);
        noisedImagePane.getChildren().add(noisedImageView);
        denoisedImagePane.getChildren().add(denoisedImageView);
        
        // Placement dans la grille
        grid.add(originalImagePane, 0, 0);
        grid.add(denoisedImagePane, 1, 0);
        grid.add(noisedImagePane, 0, 1);
        grid.add(statsPane, 1, 1);
        
        return grid;
    }

    private void configureImageView(ImageView imageView) {
        imageView.setFitWidth(550);
        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        
        // Placement de l'ImageView au-dessus du label mais en-dessous des autres éléments
        StackPane.setAlignment(imageView, Pos.CENTER);
        StackPane.setMargin(imageView, new Insets(30, 10, 10, 10));
    }
    
    private StackPane createImagePane(String title) {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: #1E1E1E; -fx-background-radius: 15px;");
        pane.setPrefSize(550, 350);
        pane.setMinSize(550, 350);
        
        // Label en haut du panneau
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setPadding(new Insets(10, 0, 0, 0));
        
        // Ajouter le label à la pile
        pane.getChildren().add(titleLabel);
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        
        // Placeholder pour les images non chargées
        Rectangle placeholder = new Rectangle(250, 250);
        placeholder.setFill(Color.web("#2A2A2A"));
        placeholder.setArcWidth(20);
        placeholder.setArcHeight(20);
        pane.getChildren().add(placeholder);
        
        // Message de statut
        Label statusLabel = new Label("Aucune image chargée");
        statusLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px;");
        pane.getChildren().add(statusLabel);
        
        return pane;
    }
    
    private StackPane createStatsPane() {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: #1E1E1E; -fx-background-radius: 15px;");
        pane.setPrefSize(550, 350);
        pane.setMinSize(550, 350);
        
        // Label en haut du panneau
        Label titleLabel = new Label("Analyse de la Qualité");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setPadding(new Insets(10, 0, 0, 0));
        
        // Contenu initial
        VBox statsContent = new VBox(10);
        statsContent.setAlignment(Pos.CENTER);
        statsContent.setPadding(new Insets(40, 20, 20, 20));
        
        Text waitingText = new Text("Les statistiques apparaîtront ici après débruitage");
        waitingText.setFill(Color.LIGHTGRAY);
        waitingText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        
        statsContent.getChildren().add(waitingText);
        
        // Ajouter les éléments à la pile
        pane.getChildren().addAll(titleLabel, statsContent);
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        
        return pane;
    }
    
    private VBox createParamPane(Stage stage) {
        VBox container = new VBox(15);
        container.setStyle("-fx-background-color: #1E1E1E; -fx-background-radius: 15px;");
        container.setPadding(new Insets(20));
        container.setPrefWidth(300);
        container.setAlignment(Pos.TOP_CENTER);
        
        // Titre
        Label titleLabel = new Label("Paramètres de Débruitage");
        titleLabel.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 18px; -fx-font-weight: bold;");
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        
        // Bouton d'ajout d'image
        Button btnAddImage = new Button("+ AJOUTER IMAGE");
        btnAddImage.getStyleClass().add("button-action");
        btnAddImage.setMaxWidth(Double.MAX_VALUE);
        
        // Ligne de séparation
        Rectangle separator = new Rectangle(container.getPrefWidth() - 40, 1);
        separator.setFill(Color.web("#333333"));
        
        // Section de paramètres de débruitage
        VBox denoiseParams = createDenoiseParamsSection();
        
        // Boutons d'action
        Button btnNoize = new Button("BRUITER L'IMAGE");
        btnNoize.getStyleClass().add("button-noize");
        btnNoize.setMaxWidth(Double.MAX_VALUE);
        
        Button btnDenoize = new Button("DÉBRUITER L'IMAGE");
        btnDenoize.getStyleClass().add("button-denoize");
        btnDenoize.setMaxWidth(Double.MAX_VALUE);
        
        // Actions des boutons
        setupButtonActions(btnAddImage, btnNoize, btnDenoize, stage);
        
        // Assemblage
        container.getChildren().addAll(
            titleLabel,
            btnAddImage,
            separator,
            denoiseParams,
            btnNoize,
            btnDenoize
        );
        
        VBox.setMargin(denoiseParams, new Insets(10, 0, 20, 0));
        
        return container;
    }
    
    private VBox createDenoiseParamsSection() {
        VBox paramsBox = new VBox(15);
        paramsBox.setAlignment(Pos.TOP_LEFT);
        
        // === ComboBox Local/Global ===
        Label modeLabel = new Label("Mode de Débruitage:");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        choixMode = new ComboBox<>();
        choixMode.getItems().addAll("Local", "Global");
        choixMode.setMaxWidth(Double.MAX_VALUE);
        choixMode.getStyleClass().add("combo-box-custom");
        
        // Conteneur pour les widgets dynamiques
        widgetsBox = new VBox(10);
        widgetsBox.setAlignment(Pos.TOP_LEFT);
        
        // Configuration des ToggleGroups pour les paramètres de patch
        tgPatchLocal = new ToggleGroup();
        tgPatchGlobal = new ToggleGroup();
        
        // Gestionnaire d'événements pour le changement de mode
        choixMode.setOnAction(e -> updateWidgetsForMode());
        
        // === Slider Sigma ===
        Label sliderLabel = new Label("Niveau de bruit (Sigma): 10");
        sliderLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        sigmaSlider = new Slider(10, 30, 10);
        sigmaSlider.setMajorTickUnit(10);
        sigmaSlider.setMinorTickCount(0);
        sigmaSlider.setShowTickLabels(true);
        sigmaSlider.setShowTickMarks(true);
        sigmaSlider.setSnapToTicks(true);
        sigmaSlider.getStyleClass().add("slider-custom");
        sigmaSlider.setMaxWidth(Double.MAX_VALUE);
        
        sigmaSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentSigma = newVal.doubleValue();
            sliderLabel.setText(String.format("Niveau de bruit (Sigma): %.0f", currentSigma));
        });
        
        // === Type de seuillage ===
        Label labelTypeSeuillage = new Label("Type de seuillage:");
        labelTypeSeuillage.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        tgTypeSeuillage = new ToggleGroup();
        RadioButton rbVisuShrink = new RadioButton("VisuShrink");
        RadioButton rbBayesShrink = new RadioButton("BayesShrink");
        rbVisuShrink.setToggleGroup(tgTypeSeuillage);
        rbBayesShrink.setToggleGroup(tgTypeSeuillage);
        rbVisuShrink.setSelected(true);
        styleRadioButtons(rbVisuShrink, rbBayesShrink);
        
        HBox typeSeuillageBox = new HBox(20, rbVisuShrink, rbBayesShrink);
        typeSeuillageBox.setAlignment(Pos.CENTER_LEFT);
        
        // === Fonction de seuillage ===
        Label labelFonctionSeuillage = new Label("Fonction de seuillage:");
        labelFonctionSeuillage.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        tgFonctionSeuillage = new ToggleGroup();
        RadioButton rbDur = new RadioButton("Dur");
        RadioButton rbDoux = new RadioButton("Doux");
        rbDur.setToggleGroup(tgFonctionSeuillage);
        rbDoux.setToggleGroup(tgFonctionSeuillage);
        rbDur.setSelected(true);
        styleRadioButtons(rbDur, rbDoux);
        
        HBox fonctionSeuillageBox = new HBox(20, rbDur, rbDoux);
        fonctionSeuillageBox.setAlignment(Pos.CENTER_LEFT);
        
        // Assemblage
        paramsBox.getChildren().addAll(
            modeLabel,
            choixMode,
            widgetsBox,
            sliderLabel,
            sigmaSlider,
            labelTypeSeuillage,
            typeSeuillageBox,
            labelFonctionSeuillage,
            fonctionSeuillageBox
        );
        
        return paramsBox;
    }
    
    private void updateWidgetsForMode() {
        widgetsBox.getChildren().clear();
        String selection = choixMode.getValue();
        
        if ("Local".equals(selection)) {
            // Interface pour le mode Local
            Label labelPatchs = new Label("Taille des patchs:");
            labelPatchs.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            
            RadioButton rb5 = new RadioButton("5x5");
            RadioButton rb7 = new RadioButton("7x7");
            RadioButton rb9 = new RadioButton("9x9");
            rb5.setToggleGroup(tgPatchLocal);
            rb7.setToggleGroup(tgPatchLocal);
            rb9.setToggleGroup(tgPatchLocal);
            rb7.setSelected(true); // Option par défaut
            styleRadioButtons(rb5, rb7, rb9);
            
            HBox patchOptionsBox = new HBox(15, rb5, rb7, rb9);
            patchOptionsBox.setAlignment(Pos.CENTER_LEFT);
            
            // Taille de la fenêtre (optionnel)
            Label labelFenetre = new Label("Taille de la fenêtre:");
            labelFenetre.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            
            TextField tfFenetre = new TextField("250");
            tfFenetre.setPromptText("Taille de la fenêtre...");
            tfFenetre.getStyleClass().add("text-field-custom");
            tfFenetre.setPrefWidth(100);
            
            HBox fenetreBox = new HBox(10, tfFenetre);
            fenetreBox.setAlignment(Pos.CENTER_LEFT);
            
            widgetsBox.getChildren().addAll(
                labelPatchs,
                patchOptionsBox,
                labelFenetre,
                fenetreBox
            );
        } else {
            // Interface pour le mode Global
            Label labelPatchGlobal = new Label("Taille des patchs:");
            labelPatchGlobal.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            
            RadioButton rb17 = new RadioButton("17x17");
            RadioButton rb21 = new RadioButton("21x21");
            RadioButton rb23 = new RadioButton("23x23");
            rb17.setToggleGroup(tgPatchGlobal);
            rb21.setToggleGroup(tgPatchGlobal);
            rb23.setToggleGroup(tgPatchGlobal);
            rb21.setSelected(true); // Option par défaut
            styleRadioButtons(rb17, rb21, rb23);
            
            HBox patchOptionsBox = new HBox(15, rb17, rb21, rb23);
            patchOptionsBox.setAlignment(Pos.CENTER_LEFT);
            
            widgetsBox.getChildren().addAll(
                labelPatchGlobal,
                patchOptionsBox
            );
        }
    }
    
    private void styleRadioButtons(RadioButton... buttons) {
        for (RadioButton rb : buttons) {
            rb.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        }
    }
    
    private void setupButtonActions(Button btnAddImage, Button btnNoize, Button btnDenoize, Stage stage) {
        // Action pour ajouter une image
        btnAddImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Image image = new Image(file.toURI().toString());
                originalImageView.setImage(image);
                originalImg = imageToImg(image);
                updatePanelStatus(originalImagePane, "Image originale chargée", true);
                
                // Réinitialiser les autres panneaux
                resetPanel(noisedImagePane, "Image bruitée");
                resetPanel(denoisedImagePane, "Image débruitée");
                resetStatsPanel();
            }
        });

        // Action pour bruiter l'image
        btnNoize.setOnAction(e -> {
            if (originalImageView.getImage() != null) {
                // Bruitage de l'image
                double sigma = sigmaSlider.getValue();
                noisedImg = BruiteurImage.noising(originalImg, sigma);
                
                // Affichage de l'image bruitée
                Image noisedImageFX = imgToImage(noisedImg);
                noisedImageView.setImage(noisedImageFX);
                updatePanelStatus(noisedImagePane, "Image bruitée avec σ = " + (int)sigma, true);
                
                // Réinitialiser les panneaux suivants
                resetPanel(denoisedImagePane, "Image débruitée");
                resetStatsPanel();
            } else {
                showAlert(stage, "Aucune image originale n'a été chargée.");
            }
        });
        
        // Action pour débruiter l'image
        btnDenoize.setOnAction(e -> {
            if (noisedImageView.getImage() != null) {
                // Récupération des paramètres
                String typeSeuil = ((RadioButton) tgTypeSeuillage.getSelectedToggle()).getText();
                String fonctionSeuillage = ((RadioButton) tgFonctionSeuillage.getSelectedToggle()).getText();
                boolean modeLocal = "Local".equals(choixMode.getValue());
                
                // Taille du patch
                int taillePatch = getPatchSize(modeLocal);
                
                // Débruitage
                DebruiteurImage debruiteur = new DebruiteurImage();
                try {
                    denoisedImg = debruiteur.imageDen(
                        noisedImg, 
                        typeSeuil, 
                        fonctionSeuillage, 
                        currentSigma, 
                        taillePatch, 
                        modeLocal
                    );
                    
                    // Affichage de l'image débruitée
                    Image denoisedImageFX = imgToImage(denoisedImg);
                    denoisedImageView.setImage(denoisedImageFX);
                    updatePanelStatus(denoisedImagePane, "Image débruitée", true);
                    
                    // Mise à jour des statistiques
                    updateStatsPanel();
                    
                } catch (Exception ex) {
                    showAlert(stage, "Erreur lors du débruitage: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                showAlert(stage, "Veuillez d'abord bruiter l'image originale.");
            }
        });
    }
    
    private int getPatchSize(boolean modeLocal) {
        if (modeLocal) {
            RadioButton selectedRB = (RadioButton) tgPatchLocal.getSelectedToggle();
            if (selectedRB != null) {
                return Integer.parseInt(selectedRB.getText().split("x")[0]);
            }
            return 7; // Valeur par défaut
        } else {
            RadioButton selectedRB = (RadioButton) tgPatchGlobal.getSelectedToggle();
            if (selectedRB != null) {
                return Integer.parseInt(selectedRB.getText().split("x")[0]);
            }
            return 21; // Valeur par défaut
        }
    }
    
    private void updatePanelStatus(StackPane panel, String status, boolean success) {
        // Supprimer le placeholder et le message de statut s'ils existent
        panel.getChildren().removeIf(node -> 
            node instanceof Rectangle || 
            (node instanceof Label && ((Label)node).getText().contains("Aucune") || ((Label)node).getText().contains("Image")));
        
        // Ajouter un nouveau message de statut
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: " + (success ? "#4CAF50" : "#F44336") + "; -fx-font-size: 12px;");
        panel.getChildren().add(statusLabel);
        StackPane.setAlignment(statusLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(statusLabel, new Insets(0, 0, 5, 0));
    }
    
    private void resetPanel(StackPane panel, String title) {
        // Conserver uniquement le titre
        Label titleLabel = (Label) panel.getChildren().get(0);
        panel.getChildren().clear();
        panel.getChildren().add(titleLabel);
        
        // Ajouter un placeholder
        Rectangle placeholder = new Rectangle(250, 250);
        placeholder.setFill(Color.web("#2A2A2A"));
        placeholder.setArcWidth(20);
        placeholder.setArcHeight(20);
        panel.getChildren().add(placeholder);
        
        // Ajouter un message de statut
        Label statusLabel = new Label("En attente...");
        statusLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px;");
        panel.getChildren().add(statusLabel);
    }
    
    private void resetStatsPanel() {
        // Conserver uniquement le titre
        Label titleLabel = (Label) statsPane.getChildren().get(0);
        statsPane.getChildren().clear();
        statsPane.getChildren().add(titleLabel);
        
        // Contenu initial
        VBox statsContent = new VBox(10);
        statsContent.setAlignment(Pos.CENTER);
        statsContent.setPadding(new Insets(40, 20, 20, 20));
        
        Text waitingText = new Text("Les statistiques apparaîtront ici après débruitage");
        waitingText.setFill(Color.LIGHTGRAY);
        waitingText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        
        statsContent.getChildren().add(waitingText);
        statsPane.getChildren().add(statsContent);
    }
    
    private void updateStatsPanel() {
        if (originalImg != null && denoisedImg != null) {
            // Calcul des métriques
            EvaluationQualite eval = new EvaluationQualite();
            double mse = eval.mse(originalImg, denoisedImg);
            double psnr = eval.psnr(originalImg, denoisedImg);
            
            // Formatage des valeurs
            DecimalFormat df = new DecimalFormat("#.##");
            
            // Création du contenu
            VBox statsContent = new VBox(15);
            statsContent.setAlignment(Pos.CENTER);
            statsContent.setPadding(new Insets(20));
            
            // Titre
            Label titleStats = new Label("Résultats de l'Évaluation");
            titleStats.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 16px; -fx-font-weight: bold;");
            
            // MSE
            HBox mseBox = createStatsRow("MSE", df.format(mse), 
                    "Erreur Quadratique Moyenne\nPlus la valeur est petite, meilleur est le débruitage");
            
            // PSNR
            HBox psnrBox = createStatsRow("PSNR", df.format(psnr) + " dB", 
                    "Rapport Signal/Bruit de Crête\nPlus la valeur est grande, meilleur est le débruitage");
            
            // Paramètres utilisés
            VBox paramsBox = new VBox(5);
            paramsBox.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 5px; -fx-padding: 10px;");
            
            Label paramsTitle = new Label("Paramètres utilisés");
            paramsTitle.setStyle("-fx-text-fill: #BBBBBB; -fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label sigmaValue = new Label("Sigma: " + (int)currentSigma);
            sigmaValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            
            Label modeValue = new Label("Mode: " + choixMode.getValue());
            modeValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            
            Label patchValue = new Label("Taille patch: " + getPatchSize(choixMode.getValue().equals("Local")) + "x" + getPatchSize(choixMode.getValue().equals("Local")));
            patchValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            
            Label seuilValue = new Label("Type seuil: " + ((RadioButton) tgTypeSeuillage.getSelectedToggle()).getText());
            seuilValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            
            Label fonctionValue = new Label("Fonction: " + ((RadioButton) tgFonctionSeuillage.getSelectedToggle()).getText());
            fonctionValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            
            paramsBox.getChildren().addAll(paramsTitle, sigmaValue, modeValue, patchValue, seuilValue, fonctionValue);
            
            // Assemblage
            statsContent.getChildren().addAll(titleStats, mseBox, psnrBox, paramsBox);
            
            // Mise à jour du panneau de statistiques
            statsPane.getChildren().clear();
            statsPane.getChildren().addAll(statsLabel, statsContent);
        }
    }
    
    private HBox createStatsRow(String label, String value, String tooltip) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 5px; -fx-padding: 10px;");
        
        Label titleLabel = new Label(label + ":");
        titleLabel.setStyle("-fx-text-fill: #BBBBBB; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Icône d'info-bulle
        Label infoIcon = new Label(" ⓘ ");
        infoIcon.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px; -fx-cursor: hand;");
        
        // Info-bulle personnalisée
        VBox tooltipContent = new VBox(5);
        Label tooltipLabel = new Label(tooltip);
        tooltipLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-wrap-text: true;");
        tooltipContent.getChildren().add(tooltipLabel);
        
        // Action au survol
        infoIcon.setOnMouseEntered(e -> {
            javafx.scene.control.Tooltip tip = new javafx.scene.control.Tooltip(tooltip);
            tip.setStyle("-fx-font-size: 12px; -fx-background-color: #333333;");
            javafx.scene.control.Tooltip.install(infoIcon, tip);
        });
        
        row.getChildren().addAll(titleLabel, valueLabel, infoIcon);
        HBox.setHgrow(valueLabel, Priority.ALWAYS);
        
        return row;
    }
    
    private void showAlert(Stage stage, String message) {
        // Simple alerte avec overlay
        StackPane alertOverlay = new StackPane();
        alertOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20px;");
        
        VBox alertBox = new VBox(15);
        alertBox.setStyle("-fx-background-color: #333333; -fx-padding: 20px; -fx-background-radius: 10px;");
        alertBox.setMaxWidth(300);
        alertBox.setAlignment(Pos.CENTER);
        
        Label alertTitle = new Label("Attention");
        alertTitle.setStyle("-fx-text-fill: #F44336; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label alertMessage = new Label(message);
        alertMessage.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-wrap-text: true;");
        alertMessage.setAlignment(Pos.CENTER);
        
        Button closeButton = new Button("OK");
        closeButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setPrefWidth(100);
        
        alertBox.getChildren().addAll(alertTitle, alertMessage, closeButton);
        alertOverlay.getChildren().add(alertBox);
        
        // Ajouter l'alerte à la scène
        Scene scene = stage.getScene();
        BorderPane root = (BorderPane) scene.getRoot();
        root.getChildren().add(alertOverlay);
        
        // Action pour fermer l'alerte
        closeButton.setOnAction(event -> root.getChildren().remove(alertOverlay));
    }
    
    /**
     * Convertit une Image JavaFX en objet Img du modèle
     */
    public static Img imageToImg(Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        PixelReader reader = fxImage.getPixelReader();

        Pixel[][] pixels = new Pixel[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double gray = color.getRed() * 255.0;
                pixels[y][x] = new Pixel(gray);
            }
        }

        return new Img(pixels);
    }

    /**
     * Convertit un Img du modèle en Image JavaFX
     */
    public static Image imgToImage(Img img) {
        int width = img.getLargeur();
        int height = img.getHauteur();
        WritableImage fxImage = new WritableImage(width, height);
        PixelWriter writer = fxImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = img.getPixel(y, x).getValeur() / 255.0;
                val = Math.max(0, Math.min(1, val));
                Color c = new Color(val, val, val, 1.0);
                writer.setColor(x, y, c);
            }
        }

        return fxImage;
    }
    
    /**
     * Point d'entrée principal de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}