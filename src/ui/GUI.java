/**
 * @file GUI.java
 * @brief Interface graphique principale de l'application de traitement d'images.
 */

package ui;

import java.io.File;
import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.base.Img;
import model.base.Pixel;
import service.bruit.BruiteurImage;
import service.debruitage.DebruiteurImage;
import service.evaluation.EvaluationQualite;

/**
 * @class GUI
 * @brief Interface graphique principale de l'application de traitement
 *        d'images.
 * @author Emma & Alexis
 */
public class GUI extends Application {
	// Images
	private ImageView vueImageOriginale;
	private ImageView vueImageBruitee;
	private ImageView vueImageDebruitee;

	// Conteneurs zoomable
	private ScrollPane panneauZoomOriginale;
	private ScrollPane panneauZoomBruitee;
	private ScrollPane panneauZoomDebruitee;

	// Conteneurs des images
	private StackPane panneauImageOriginale;
	private StackPane panneauImageBruitee;
	private StackPane panneauImageDebruitee;
	private StackPane panneauStatistiques;

	// Labels pour les panneaux
	private Label labelStats;

	// Stockage des versions Img pour calculs
	private Img imgOriginale;
	private Img imgBruitee;
	private Img imgDebruitee;

	// Paramètres
	private ComboBox<String> choixMode;
	private VBox widgetsBox;
	private Slider sigmaSlider;
	private ToggleGroup tgTypeSeuillage;
	private ToggleGroup tgFonctionSeuillage;
	private ToggleGroup tgPatchLocal;
	private ToggleGroup tgPatchGlobal;
	private double sigmaActuel = 10.0;

	// Barre de progression
	private ProgressBar progressBar;
	private Label progressLabel;

	/**
	 * @brief Point d'entrée de l'interface graphique JavaFX.
	 * @author Alexis
	 * @param primaryStage La fenêtre principale de l'application JavaFX.
	 * @throws Exception En cas d'erreur lors de l'initialisation de l'interface.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Denoiℤe - Débruitage d'images par ACP");

		BorderPane mainLayout = new BorderPane();

		mainLayout.setStyle("-fx-background-color: #121212;");
		mainLayout.setPrefSize(1400, 900);

		// Grille 2x2 pour les 4 panneaux
		GridPane grilleImages = creerGridPane();

		// Panneau des paramètres
		VBox paramPane = creerParamPane(primaryStage);

		mainLayout.setCenter(grilleImages);
		mainLayout.setLeft(paramPane);

		BorderPane.setMargin(grilleImages, new Insets(20));
		BorderPane.setMargin(paramPane, new Insets(20, 0, 20, 20));

		StackPane root = new StackPane(mainLayout); // racine superposable
		Scene scene = new Scene(root);
		scene.getStylesheets().add("style.css");
		primaryStage.setScene(scene);
		primaryStage.show();

		// Déclencher l'action du ComboBox pour afficher les bons widgets dès le départ
		choixMode.getSelectionModel().selectFirst();
		choixMode.fireEvent(new ActionEvent());
	}

	/**
	 * @brief Crée la grille d'affichage principale pour les images et les
	 *        statistiques.
	 * @author Alexis & Emma
	 * @return La grille configurée prête à être ajoutée à la scène principale.
	 */
	private GridPane creerGridPane() {
		GridPane grille = new GridPane();
		grille.setHgap(15);
		grille.setVgap(15);
		grille.setAlignment(Pos.CENTER);

		// Barre de progression
		progressLabel = new Label("Débruitage en cours...");
		progressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
		progressLabel.setVisible(false);

		progressBar = new ProgressBar();
		progressBar.setPrefWidth(400);
		progressBar.setVisible(false);

		VBox progressBox = new VBox(5, progressLabel, progressBar);
		progressBox.setAlignment(Pos.CENTER);
		grille.add(progressBox, 0, 0, 2, 1);

		// Panneaux statiques
		panneauImageOriginale = creerImagePane("Image Originale");
		panneauImageBruitee = creerImagePane("Image Bruitée");
		panneauImageDebruitee = creerImagePane("Image Débruitée");
		panneauStatistiques = creerPanneauStatistiques();
		labelStats = (Label) panneauStatistiques.getChildren().get(0);

		// ImageView
		vueImageOriginale = new ImageView();
		vueImageBruitee = new ImageView();
		vueImageDebruitee = new ImageView();

		configureImageView(vueImageOriginale);
		configureImageView(vueImageBruitee);
		configureImageView(vueImageDebruitee);

		// Ajout panneau image originale
		if (vueImageOriginale.getImage() != null) {
			panneauZoomOriginale = creerPanneauImageZoomable(vueImageOriginale);
			grille.add(panneauZoomOriginale, 0, 1);
		} else {
			grille.add(panneauImageOriginale, 0, 1);
		}

		// Ajout panneau image bruitée
		if (vueImageBruitee.getImage() != null) {
			panneauZoomBruitee = creerPanneauImageZoomable(vueImageBruitee);
			grille.add(panneauZoomBruitee, 0, 2);
		} else {
			grille.add(panneauImageBruitee, 0, 2);
		}

		// Ajout panneau image débruitée
		if (vueImageDebruitee.getImage() != null) {
			panneauZoomDebruitee = creerPanneauImageZoomable(vueImageDebruitee);
			grille.add(panneauZoomDebruitee, 1, 1);
		} else {
			grille.add(panneauImageDebruitee, 1, 1);
		}

		// Ajout panneau statistiques
		grille.add(panneauStatistiques, 1, 2);

		return grille;
	}

	/**
	 * @brief Configure une vue d'image avec des dimensions, un lissage et un
	 *        positionnement adaptés à l'affichage.
	 * @author Alexis
	 * @param imageView L'objet ImageView à configurer.
	 */
	private void configureImageView(ImageView imageView) {
		imageView.setFitWidth(550);
		imageView.setFitHeight(350);
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		imageView.setCache(true);

		StackPane.setAlignment(imageView, Pos.CENTER);
		StackPane.setMargin(imageView, new Insets(30, 10, 10, 10));
	}

	/**
	 * @brief Crée un panneau d'image stylisé avec un titre, un espace pour l'image
	 *        et un message de statut.
	 * @author Alexis
	 * @param titre Le titre à afficher en haut du panneau.
	 * @return Un StackPane prêt à afficher une image.
	 */
	private StackPane creerImagePane(String title) {
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

		// Rectangle pour les images non chargées
		Rectangle rectangle = new Rectangle(250, 250);
		rectangle.setFill(Color.web("#2A2A2A"));
		rectangle.setArcWidth(20);
		rectangle.setArcHeight(20);
		pane.getChildren().add(rectangle);

		// Message de statut
		Label statusLabel = new Label("Aucune image chargée");
		statusLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px;");
		pane.getChildren().add(statusLabel);

		return pane;
	}

	/**
	 * @brief Crée un panneau pour l'affichage des statistiques de qualité après le
	 *        débruitage.
	 * @author Alexis & Emma
	 * @return Un StackPane prêt à afficher les statistiques de qualité.
	 */
	private StackPane creerPanneauStatistiques() {
		StackPane pane = new StackPane();
		pane.setStyle("-fx-background-color: #1E1E1E; -fx-background-radius: 15px;");
		pane.setPrefSize(550, 350);
		pane.setMinSize(550, 350);

		// Label en haut du panneau
		Label titleLabel = new Label("Analyse de la Qualité");
		titleLabel.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 16px; -fx-font-weight: bold;");
		titleLabel.setPadding(new Insets(10, 0, 0, 0));

		// Contenu initial
		VBox contenuStats = new VBox(10);
		contenuStats.setAlignment(Pos.CENTER);
		contenuStats.setPadding(new Insets(40, 20, 20, 20));

		Text texteAttente = new Text("Les statistiques apparaîtront ici après débruitage");
		texteAttente.setFill(Color.LIGHTGRAY);
		texteAttente.setFont(Font.font("System", FontWeight.NORMAL, 14));

		contenuStats.getChildren().add(texteAttente);

		// Ajouter les éléments à la pile
		pane.getChildren().addAll(titleLabel, contenuStats);
		StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);

		return pane;
	}

	/**
	 * @brief Crée le panneau latéral contenant les paramètres et actions de
	 *        débruitage.
	 * @author Alexis
	 * @param stage La fenêtre principale JavaFX, utilisée pour certaines actions
	 *              comme l'import d'image.
	 * @return Un VBox prêt à être intégré à l'interface.
	 */
	private VBox creerParamPane(Stage stage) {
		VBox conteneur = new VBox(15);
		conteneur.setStyle("-fx-background-color: #1E1E1E; -fx-background-radius: 15px;");
		conteneur.setPadding(new Insets(20));
		conteneur.setPrefWidth(300);
		conteneur.setAlignment(Pos.TOP_CENTER);

		// Titre
		Label titleLabel = new Label("Paramètres de Débruitage");
		titleLabel.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 18px; -fx-font-weight: bold;");
		titleLabel.setPadding(new Insets(0, 0, 10, 0));

		// Bouton d'ajout d'image
		Button boutonAjouterImage = new Button("+ AJOUTER IMAGE");
		boutonAjouterImage.getStyleClass().add("button-action");
		boutonAjouterImage.setMaxWidth(Double.MAX_VALUE);

		// Ligne de séparation
		Rectangle separateur = new Rectangle(conteneur.getPrefWidth() - 40, 1);
		separateur.setFill(Color.web("#333333"));

		// Section de paramètres de débruitage
		VBox sectionParametres = creerSectionParametresDebruitage();

		// Boutons d'action
		Button boutonBruiter = new Button("BRUITER L'IMAGE");
		boutonBruiter.getStyleClass().add("button-noize");
		boutonBruiter.setMaxWidth(Double.MAX_VALUE);

		Button boutonDebruiter = new Button("DÉBRUITER L'IMAGE");
		boutonDebruiter.getStyleClass().add("button-denoize");
		boutonDebruiter.setMaxWidth(Double.MAX_VALUE);

		// Actions des boutons
		configurerActionsDesBoutons(boutonAjouterImage, boutonBruiter, boutonDebruiter, stage);

		// Assemblage
		conteneur.getChildren().addAll(titleLabel, boutonAjouterImage, separateur, sectionParametres, boutonBruiter,
				boutonDebruiter);

		VBox.setMargin(sectionParametres, new Insets(10, 0, 20, 0));

		return conteneur;
	}

	/**
	 * @brief Crée la section contenant les paramètres de débruitage ajustables par
	 *        l'utilisateur.
	 * @author Alexis
	 * @return Un VBox contenant tous les éléments de paramétrage.
	 */
	private VBox creerSectionParametresDebruitage() {
		VBox paramsBox = new VBox(15);
		paramsBox.setAlignment(Pos.TOP_LEFT);

		// ComboBox Local/Global
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
		choixMode.setOnAction(e -> mettreAJourWidgetsSelonMode());

		// Slider Sigma
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
			sigmaActuel = newVal.doubleValue();
			sliderLabel.setText(String.format("Niveau de bruit (Sigma): %.0f", sigmaActuel));
		});

		// Type de seuillage
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

		// Fonction de seuillage
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
		paramsBox.getChildren().addAll(modeLabel, choixMode, widgetsBox, sliderLabel, sigmaSlider, labelTypeSeuillage,
				typeSeuillageBox, labelFonctionSeuillage, fonctionSeuillageBox);

		return paramsBox;
	}

	/**
	 * @brief Met à jour dynamiquement les paramètres affichés selon le mode de
	 *        débruitage sélectionné.
	 * @author Alexis
	 */
	private void mettreAJourWidgetsSelonMode() {
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
			rb7.setSelected(true);
			styleRadioButtons(rb5, rb7, rb9);

			HBox patchOptionsBox = new HBox(15, rb5, rb7, rb9);
			patchOptionsBox.setAlignment(Pos.CENTER_LEFT);

			// Taille de la fenêtre
			Label labelFenetre = new Label("Taille de la fenêtre:");
			labelFenetre.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

			TextField tfFenetre = new TextField("250");
			tfFenetre.setPromptText("Taille de la fenêtre...");
			tfFenetre.getStyleClass().add("text-field-custom");
			tfFenetre.setPrefWidth(100);

			HBox fenetreBox = new HBox(10, tfFenetre);
			fenetreBox.setAlignment(Pos.CENTER_LEFT);

			widgetsBox.getChildren().addAll(labelPatchs, patchOptionsBox, labelFenetre, fenetreBox);
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
			rb21.setSelected(true);
			styleRadioButtons(rb17, rb21, rb23);

			HBox patchOptionsBox = new HBox(15, rb17, rb21, rb23);
			patchOptionsBox.setAlignment(Pos.CENTER_LEFT);

			widgetsBox.getChildren().addAll(labelPatchGlobal, patchOptionsBox);
		}
	}

	/**
	 * @brief Applique un style commun aux boutons radio passés en paramètre.
	 * @author Alexis
	 * @param buttons Liste variable de RadioButton à styliser.
	 */
	private void styleRadioButtons(RadioButton... buttons) {
		for (RadioButton rb : buttons) {
			rb.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
		}
	}

	/**
	 * @brief Configure les actions des boutons d'ajout d'image, de bruitage et de
	 *        débruitage.
	 * @author Alexis & Emma
	 * @param boutonAjouterImage Bouton pour sélectionner et charger une nouvelle
	 *                           image.
	 * @param boutonBruiter      Bouton pour appliquer du bruit sur l'image
	 *                           originale.
	 * @param boutonDebruiter    Bouton pour lancer le débruitage de l'image
	 *                           bruitée.
	 * @param stage              La fenêtre principale de l'application, utilisée
	 *                           pour les dialogues et alertes.
	 */
	private void configurerActionsDesBoutons(Button boutonAjouterImage, Button boutonBruiter, Button boutonDebruiter,
			Stage stage) {
		// Action pour ajouter une image (corrigée)
		boutonAjouterImage.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choisir une image");
			fileChooser.getExtensionFilters()
					.addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
			File file = fileChooser.showOpenDialog(stage);
			if (file != null) {
				try {
					// Reset des images bruitées et débruitées
					vueImageBruitee.setImage(null);
					vueImageDebruitee.setImage(null);
					imgBruitee = null;
					imgDebruitee = null;

					// Forcer le chargement de l'image en mode synchrone
					Image image = new Image(file.toURI().toString());

					// S'assurer que l'image est chargée correctement
					if (image.isError()) {
						throw new Exception("Erreur de chargement de l'image");
					}

					// Mettre à jour l'image dans la vue
					vueImageOriginale.setImage(image);

					// Convertir en format interne
					imgOriginale = convertirImageEnImg(image);

					// Récupérer la grille d'images
					GridPane grilleImages = (GridPane) ((BorderPane) ((StackPane) stage.getScene().getRoot())
							.getChildren().get(0)).getCenter();

					// Vider complètement la grille
					grilleImages.getChildren().clear();

					// Réinitialiser les panneaux statiques
					reinitialiserPanneau(panneauImageOriginale);
					reinitialiserPanneau(panneauImageBruitee);
					reinitialiserPanneau(panneauImageDebruitee);
					reinitialiserPanneauStatistiques();

					// Réinitialiser la barre de progression
					progressBar.setVisible(false);
					progressLabel.setVisible(false);

					// Ajouter la barre de progression
					VBox progressBox = new VBox(5, progressLabel, progressBar);
					progressBox.setAlignment(Pos.CENTER);
					grilleImages.add(progressBox, 0, 0, 2, 1);

					// Ajouter les panneaux statiques à la grille
					grilleImages.add(panneauImageOriginale, 0, 1);
					grilleImages.add(panneauImageBruitee, 0, 2);
					grilleImages.add(panneauImageDebruitee, 1, 1);
					grilleImages.add(panneauStatistiques, 1, 2);

					// Nettoyer le panneau de l'image originale
					panneauImageOriginale.getChildren().removeIf(node -> node instanceof Rectangle
							|| (node instanceof Label && !((Label) node).getText().equals("Image Originale")));

					// Ajouter l'image originale au panneau
					panneauImageOriginale.getChildren().add(vueImageOriginale);
					StackPane.setAlignment(vueImageOriginale, Pos.CENTER);

					// Créer et ajouter le panneau zoomable pour l'image originale
					panneauZoomOriginale = creerPanneauImageZoomable(vueImageOriginale);
					grilleImages.getChildren().remove(panneauImageOriginale);
					grilleImages.add(panneauZoomOriginale, 0, 1);

				} catch (Exception ex) {
					ex.printStackTrace();
					// Afficher un message d'erreur
					afficherAlerte(stage, "Erreur lors du chargement de l'image : " + ex.getMessage());
				}
			}
		});

		// Action pour bruiter l'image
		boutonBruiter.setOnAction(e -> {
			if (vueImageOriginale.getImage() != null) {
				try {
					// Bruitage de l'image
					double sigma = sigmaSlider.getValue();
					imgBruitee = BruiteurImage.noising(imgOriginale, sigma);

					// Affichage de l'image bruitée
					Image noisedImageFX = convertirImgEnImage(imgBruitee);
					vueImageBruitee.setImage(noisedImageFX);

					// Récupérer la grille d'images
					GridPane grilleImages = (GridPane) ((BorderPane) ((StackPane) stage.getScene().getRoot())
							.getChildren().get(0)).getCenter();

					// Supprimer l'ancien panneau zoomable s'il existe
					grilleImages.getChildren().remove(panneauZoomBruitee);

					// Créer un nouveau panneau zoomable pour l'image bruitée
					panneauZoomBruitee = creerPanneauImageZoomable(vueImageBruitee);

					// Ajouter le nouveau panneau zoomable à la grille
					grilleImages.add(panneauZoomBruitee, 0, 2);

					// Réinitialiser les panneaux suivants
					reinitialiserPanneau(panneauImageDebruitee);
					reinitialiserPanneauStatistiques();

				} catch (Exception ex) {
					ex.printStackTrace();
					afficherAlerte(stage, "Erreur lors du bruitage de l'image : " + ex.getMessage());
				}
			} else {
				afficherAlerte(stage, "Aucune image originale n'a été chargée.");
			}
		});

		// Action pour débruiter l'image
		boutonDebruiter.setOnAction(e -> {
			if (vueImageBruitee.getImage() != null) {
				// Afficher la barre de progression
				progressBar.setVisible(true);
				progressLabel.setVisible(true);
				progressBar.setProgress(-1);

				Task<Void> task = new Task<>() {
					@Override
					protected Void call() {
						try {
							// Récupération des paramètres
							String typeSeuil = ((RadioButton) tgTypeSeuillage.getSelectedToggle()).getText();
							String fonctionSeuillage = ((RadioButton) tgFonctionSeuillage.getSelectedToggle())
									.getText();
							boolean modeLocal = "Local".equals(choixMode.getValue());
							int taillePatch = getPatchSize(modeLocal);

							DebruiteurImage debruiteur = new DebruiteurImage();
							imgDebruitee = debruiteur.imageDen(imgBruitee, typeSeuil, fonctionSeuillage, sigmaActuel,
									taillePatch, modeLocal);
						} catch (Exception ex) {
							ex.printStackTrace();
							Platform.runLater(
									() -> afficherAlerte(stage, "Erreur lors du débruitage: " + ex.getMessage()));
						}
						return null;
					}

					@Override
					protected void succeeded() {
						Platform.runLater(() -> {
							try {
								Image imageFXDebruite = convertirImgEnImage(imgDebruitee);
								vueImageDebruitee.setImage(imageFXDebruite);

								// Récupérer la grille d'images
								GridPane grilleImages = (GridPane) ((BorderPane) ((StackPane) stage.getScene()
										.getRoot()).getChildren().get(0)).getCenter();

								// Supprimer l'ancien panneau zoomable s'il existe
								grilleImages.getChildren().remove(panneauZoomDebruitee);

								// Créer un nouveau panneau zoomable pour l'image débruitée
								panneauZoomDebruitee = creerPanneauImageZoomable(vueImageDebruitee);

								// Ajouter le nouveau panneau zoomable à la grille
								grilleImages.add(panneauZoomDebruitee, 1, 1);

								mettreAJourPanneauStatistiques();

								progressBar.setVisible(false);
								progressLabel.setVisible(false);
							} catch (Exception ex) {
								ex.printStackTrace();
								afficherAlerte(stage,
										"Erreur lors de l'affichage de l'image débruitée : " + ex.getMessage());
							}
						});
					}

					@Override
					protected void failed() {
						Platform.runLater(() -> {
							progressBar.setVisible(false);
							progressLabel.setVisible(false);
							afficherAlerte(stage, "Le débruitage a échoué.");
						});
					}
				};

				// Lancer le thread
				new Thread(task).start();
			} else {
				afficherAlerte(stage, "Veuillez d'abord bruiter l'image originale.");
			}
		});

	}

	/**
	 * @brief Récupère la taille du patch sélectionné selon le mode choisi.
	 * @author Alexis
	 * @param modeLocal Indique si le mode local est actif ou global.
	 * @return La taille du patch sélectionné.
	 */
	private int getPatchSize(boolean modeLocal) {
		if (modeLocal) {
			RadioButton selectRB = (RadioButton) tgPatchLocal.getSelectedToggle();
			if (selectRB != null) {
				return Integer.parseInt(selectRB.getText().split("x")[0]);
			}
			return 7;
		} else {
			RadioButton selectRB = (RadioButton) tgPatchGlobal.getSelectedToggle();
			if (selectRB != null) {
				return Integer.parseInt(selectRB.getText().split("x")[0]);
			}
			return 21;
		}
	}

	/**
	 * @brief Réinitialise un panneau d'image à son état initial.
	 * @author Alexis
	 * @param panel Le StackPane représentant le panneau à réinitialiser.
	 */
	private void reinitialiserPanneau(StackPane panel) {
		ImageView imageView = null;
		Slider zoomSlider = null;

		for (Node node : panel.getChildren()) {
			if (node instanceof ImageView) {
				imageView = (ImageView) node;
			} else if (node instanceof Slider) {
				zoomSlider = (Slider) node;
			}
		}

		if (imageView != null) {
			imageView.setImage(null);
		}

		panel.getChildren().removeIf(node -> !(node instanceof ImageView || node instanceof Slider));

		Rectangle rectangle = new Rectangle(250, 250);
		rectangle.setFill(Color.web("#2A2A2A"));
		rectangle.setArcWidth(20);
		rectangle.setArcHeight(20);
		panel.getChildren().add(rectangle);

		// Label de statut
		Label statusLabel = new Label("En attente...");
		statusLabel.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px;");
		StackPane.setAlignment(statusLabel, Pos.BOTTOM_CENTER);
		StackPane.setMargin(statusLabel, new Insets(0, 0, 5, 0));
		panel.getChildren().add(statusLabel);

		// Cacher le slider s'il existe
		if (zoomSlider != null) {
			zoomSlider.setVisible(false);
		}
	}

	/**
	 * @brief Réinitialise le panneau des statistiques à son état initial.
	 * @author Alexis
	 */
	private void reinitialiserPanneauStatistiques() {
		// Conserver uniquement le titre
		Label titleLabel = (Label) panneauStatistiques.getChildren().get(0);
		panneauStatistiques.getChildren().clear();
		panneauStatistiques.getChildren().add(titleLabel);

		// Contenu initial
		VBox contenuStats = new VBox(10);
		contenuStats.setAlignment(Pos.CENTER);
		contenuStats.setPadding(new Insets(40, 20, 20, 20));

		Text texteAttente = new Text("Les statistiques apparaîtront ici après débruitage");
		texteAttente.setFill(Color.LIGHTGRAY);
		texteAttente.setFont(Font.font("System", FontWeight.NORMAL, 14));

		contenuStats.getChildren().add(texteAttente);
		panneauStatistiques.getChildren().add(contenuStats);
	}

	/**
	 * @brief Met à jour le panneau des statistiques après le débruitage.
	 * @author Alexis
	 */
	private void mettreAJourPanneauStatistiques() {
		if (imgOriginale != null && imgDebruitee != null) {
			// Calcul des métriques
			EvaluationQualite eval = new EvaluationQualite();
			double mse = eval.mse(imgOriginale, imgDebruitee);
			double psnr = eval.psnr(imgOriginale, imgDebruitee);

			// Formatage des valeurs
			DecimalFormat df = new DecimalFormat("#.##");

			// Création du contenu
			VBox contenuStats = new VBox(15);
			contenuStats.setAlignment(Pos.CENTER);
			contenuStats.setPadding(new Insets(20));

			// MSE
			HBox mseBox = creerLigneStat("MSE", df.format(mse));

			// PSNR
			HBox psnrBox = creerLigneStat("PSNR", df.format(psnr) + " dB");

			// Paramètres utilisés
			VBox paramsBox = new VBox(5);
			paramsBox.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 5px; -fx-padding: 10px;");

			Label paramsTitle = new Label("Paramètres utilisés");
			paramsTitle.setStyle("-fx-text-fill: #BBBBBB; -fx-font-size: 14px; -fx-font-weight: bold;");

			Label sigmaValue = new Label("Sigma: " + (int) sigmaActuel);
			sigmaValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

			Label modeValue = new Label("Mode: " + choixMode.getValue());
			modeValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

			Label patchValue = new Label("Taille patch: " + getPatchSize(choixMode.getValue().equals("Local")) + "x"
					+ getPatchSize(choixMode.getValue().equals("Local")));
			patchValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

			Label seuilValue = new Label(
					"Type seuil: " + ((RadioButton) tgTypeSeuillage.getSelectedToggle()).getText());
			seuilValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

			Label fonctionValue = new Label(
					"Fonction: " + ((RadioButton) tgFonctionSeuillage.getSelectedToggle()).getText());
			fonctionValue.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

			paramsBox.getChildren().addAll(paramsTitle, sigmaValue, modeValue, patchValue, seuilValue, fonctionValue);

			// Assemblage
			contenuStats.getChildren().addAll(mseBox, psnrBox, paramsBox);

			// Mise à jour du panneau de statistiques
			panneauStatistiques.getChildren().clear();
			panneauStatistiques.getChildren().addAll(labelStats, contenuStats);
		}
	}

	/**
	 * @brief Crée une ligne d'affichage pour une statistique avec un libellé.
	 * @author Alexis
	 * @param label Le nom de la statistique.
	 * @param value La valeur calculée à afficher.
	 * @return Un HBox contenant la ligne de statistique stylisée.
	 */
	private HBox creerLigneStat(String label, String value) {
		HBox row = new HBox(10);
		row.setAlignment(Pos.CENTER);
		row.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 5px; -fx-padding: 10px;");

		Label titleLabel = new Label(label + ":");
		titleLabel.setStyle("-fx-text-fill: #BBBBBB; -fx-font-size: 14px; -fx-font-weight: bold;");

		Label valueLabel = new Label(value);
		valueLabel.setStyle("-fx-text-fill: #00bcd4; -fx-font-size: 16px; -fx-font-weight: bold;");

		row.getChildren().addAll(titleLabel, valueLabel);
		HBox.setHgrow(valueLabel, Priority.ALWAYS);

		return row;
	}

	/**
	 * @brief Affiche une alerte personnalisée en superposition sur la scène.
	 * @author Emma
	 * @param fenetre La fenêtre principale dans laquelle afficher l'alerte.
	 * @param message Le message à afficher dans l'alerte.
	 */
	private void afficherAlerte(Stage fenetre, String message) {
		// Vérification que la fenêtre et sa scène existent, et que la racine est un
		// StackPane
		if (fenetre == null || fenetre.getScene() == null || !(fenetre.getScene().getRoot() instanceof StackPane)) {
			System.err.println("Erreur : impossible d'afficher l'alerte (scène ou racine invalide).");
			return;
		}

		// Création de la superposition semi-transparente
		StackPane superpositionAlerte = new StackPane();
		superpositionAlerte.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20px;");
		superpositionAlerte.setPickOnBounds(true);

		// Boîte contenant le contenu de l'alerte
		VBox boiteAlerte = new VBox(5);
		boiteAlerte.setStyle("-fx-background-color: #333333; -fx-padding: 10 15 10 15; -fx-background-radius: 10px;");
		boiteAlerte.setMaxWidth(250);
		boiteAlerte.setMaxHeight(150);
		boiteAlerte.setAlignment(Pos.CENTER);

		// Titre de l'alerte
		Label titreAlerte = new Label("Attention");
		titreAlerte.setStyle("-fx-text-fill: #F44336; -fx-font-size: 18px; -fx-font-weight: bold;");

		// Message de l'alerte
		Label messageAlerte = new Label(message);
		messageAlerte.setWrapText(true);
		messageAlerte.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
		messageAlerte.setAlignment(Pos.CENTER);

		// Bouton pour fermer l'alerte
		Button boutonOK = new Button("OK");
		boutonOK.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
		boutonOK.setPrefWidth(100);

		// Ajouter les éléments dans la boîte d'alerte
		boiteAlerte.getChildren().addAll(titreAlerte, messageAlerte, boutonOK);
		superpositionAlerte.getChildren().add(boiteAlerte);

		// Récupérer la racine de la scène et y ajouter la superposition
		StackPane racine = (StackPane) fenetre.getScene().getRoot();
		racine.getChildren().add(superpositionAlerte);

		// Définir l'action du bouton OK : retirer la superposition et donc fermer
		// l'alerte
		boutonOK.setOnAction(event -> racine.getChildren().remove(superpositionAlerte));
	}


	/**
	 * @brief Convertit une Image JavaFX en objet Img avec détection automatique RGB/Grayscale.
	 * @author Alexis & Paul
	 * @param imageFx L'Image JavaFX à convertir.
	 * @return Un objet Img (RGB ou niveaux de gris selon le contenu).
	 */
	public static Img convertirImageEnImg(Image imageFx) {
	    int largeur = (int) imageFx.getWidth();
	    int hauteur = (int) imageFx.getHeight();
	    PixelReader lecteurPixels = imageFx.getPixelReader();

	    // Détection automatique si l'image est en couleur ou niveaux de gris
	    boolean estRGB = detecterSiImageEstRGB(imageFx, lecteurPixels, largeur, hauteur);
	    
	    Pixel[][] pixels = new Pixel[hauteur][largeur];

	    if (estRGB) {
	        // Traitement pour image RGB
	        System.out.println("DEBUG: Conversion JavaFX → Img en mode RGB");
	        for (int y = 0; y < hauteur; y++) {
	            for (int x = 0; x < largeur; x++) {
	                Color couleur = lecteurPixels.getColor(x, y);
	                
	                // Récupérer les composantes RGB et les convertir en échelle 0-255
	                double r = couleur.getRed() * 255.0;
	                double g = couleur.getGreen() * 255.0;
	                double b = couleur.getBlue() * 255.0;
	                
	                // Limiter les valeurs entre 0 et 255
	                r = Math.max(0, Math.min(255, r));
	                g = Math.max(0, Math.min(255, g));
	                b = Math.max(0, Math.min(255, b));
	                
	                pixels[y][x] = new Pixel(r, g, b);
	            }
	        }
	    } else {
	        // Traitement pour image en niveaux de gris
	        System.out.println("DEBUG: Conversion JavaFX → Img en mode niveaux de gris");
	        for (int y = 0; y < hauteur; y++) {
	            for (int x = 0; x < largeur; x++) {
	                Color couleur = lecteurPixels.getColor(x, y);
	                
	                // Calcul du niveau de gris avec pondération RGB classique
	                double niveauGris = 0.299 * couleur.getRed() + 0.587 * couleur.getGreen() + 0.114 * couleur.getBlue();
	                
	                // Conversion en échelle 0-255
	                niveauGris *= 255.0;
	                niveauGris = Math.max(0, Math.min(255, niveauGris));
	                
	                pixels[y][x] = new Pixel(niveauGris);
	            }
	        }
	    }

	    return new Img(pixels, estRGB);
	}

	/**
	 * @brief Détecte si une image JavaFX est en couleur ou en niveaux de gris.
	 * @param imageFx L'image JavaFX
	 * @author Paul
	 * @param lecteurPixels Le lecteur de pixels
	 * @param largeur Largeur de l'image
	 * @param hauteur Hauteur de l'image
	 * @return true si l'image est en couleur, false si en niveaux de gris
	 */
	private static boolean detecterSiImageEstRGB(Image imageFx, PixelReader lecteurPixels, int largeur, int hauteur) {
	    // Échantillonner quelques pixels pour détecter la couleur
	    int echantillons = Math.min(100, largeur * hauteur); // Maximum 100 pixels
	    int pasX = Math.max(1, largeur / 10);
	    int pasY = Math.max(1, hauteur / 10);
	    
	    for (int y = 0; y < hauteur; y += pasY) {
	        for (int x = 0; x < largeur; x += pasX) {
	            Color couleur = lecteurPixels.getColor(x, y);
	            
	            // Si les composantes RGB diffèrent significativement, c'est une image couleur
	            double tolerance = 0.01; // Tolérance pour les erreurs d'arrondi
	            if (Math.abs(couleur.getRed() - couleur.getGreen()) > tolerance || 
	                Math.abs(couleur.getRed() - couleur.getBlue()) > tolerance || 
	                Math.abs(couleur.getGreen() - couleur.getBlue()) > tolerance) {
	                return true;
	            }
	            
	            echantillons--;
	            if (echantillons <= 0) break;
	        }
	        if (echantillons <= 0) break;
	    }
	    
	    return false; // Aucune couleur détectée, c'est du niveaux de gris
	}
	

	/**
	 * @brief Convertit un objet Img en Image JavaFX (compatible RGB et niveaux de
	 *        gris).
	 * @author Alexis & Paul
	 * @param img L'objet Img à convertir.
	 * @return Une Image JavaFX représentant l'image.
	 */
	public static Image convertirImgEnImage(Img img) {
		int largeur = img.getLargeur();
		int hauteur = img.getHauteur();
		WritableImage imageFx = new WritableImage(largeur, hauteur);
		PixelWriter ecrivainPixels = imageFx.getPixelWriter();

		if (img.isEstRGB()) {
			// Traitement pour image RGB
			System.out.println("DEBUG: Conversion Img → JavaFX en mode RGB");
			for (int y = 0; y < hauteur; y++) {
				for (int x = 0; x < largeur; x++) {
					Pixel pixel = img.getPixel(y, x);

					// Récupération des composantes RGB et normalisation entre 0 et 1
					double r = pixel.getValeur(0) / 255.0;
					double g = pixel.getValeur(1) / 255.0;
					double b = pixel.getValeur(2) / 255.0;

					// Limiter les valeurs entre 0 et 1
					r = Math.max(0, Math.min(1, r));
					g = Math.max(0, Math.min(1, g));
					b = Math.max(0, Math.min(1, b));

					Color couleur = new Color(r, g, b, 1.0);
					ecrivainPixels.setColor(x, y, couleur);
				}
			}
		} else {
			// Traitement pour image en niveaux de gris
			System.out.println("DEBUG: Conversion Img → JavaFX en mode niveaux de gris");
			for (int y = 0; y < hauteur; y++) {
				for (int x = 0; x < largeur; x++) {
					Pixel pixel = img.getPixel(y, x);

					// Récupération de la valeur du pixel et normalisation entre 0 et 1
					double valeur = pixel.getValeur() / 255.0;
					valeur = Math.max(0, Math.min(1, valeur));

					Color couleur = new Color(valeur, valeur, valeur, 1.0);
					ecrivainPixels.setColor(x, y, couleur);
				}
			}
		}

		return imageFx;
	}

	/**
	 * @brief Crée un panneau zoomable contenant une image.
	 * @author Emma
	 * @param imageView L'objet ImageView à afficher et à rendre zoomable.
	 * @return Un ScrollPane contenant l'image avec les fonctionnalités de zoom et
	 *         déplacement.
	 */
	private ScrollPane creerPanneauImageZoomable(ImageView imageView) {
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);

		// Créer un conteneur pour l'image qui permettra le déplacement
		StackPane imageContainer = new StackPane(imageView);
		imageContainer.setStyle("-fx-background-color: #1E1E1E;");

		ScrollPane scroll = new ScrollPane(imageContainer);
		scroll.setPannable(true);
		scroll.setStyle("-fx-background: #1E1E1E; -fx-background-color: #1E1E1E;");
		scroll.setFitToWidth(false);
		scroll.setFitToHeight(false);

		// Création d'une échelle pour le zoom
		Scale scaleTransform = new Scale(1, 1);
		imageView.getTransforms().add(scaleTransform);

		// Zoom avec Ctrl + molette
		scroll.setOnScroll(event -> {
			if (event.isControlDown()) {
				event.consume();

				double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
				double oldScale = scaleTransform.getX();
				double newScale = oldScale * zoomFactor;

				// Limiter le zoom entre 0.1x et 20x
				if (newScale < 0.1)
					newScale = 0.1;
				if (newScale > 20)
					newScale = 20;

				scaleTransform.setX(newScale);
				scaleTransform.setY(newScale);

				// Ajuster la taille du conteneur pour permettre le déplacement
				imageContainer.setMinWidth(imageView.getFitWidth() * newScale);
				imageContainer.setMinHeight(imageView.getFitHeight() * newScale);
			}
		});

		// Ajouter des informations de zoom en bas du panneau
		Label zoomInfo = new Label("Zoom: 100%");
		zoomInfo.setStyle("-fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5px;");

		// Mettre à jour l'information de zoom quand le facteur change
		scaleTransform.xProperty().addListener((obs, oldVal, newVal) -> {
			int zoomPercent = (int) (newVal.doubleValue() * 100);
			zoomInfo.setText("Zoom: " + zoomPercent + "%");
		});

		StackPane.setAlignment(zoomInfo, Pos.BOTTOM_RIGHT);
		StackPane.setMargin(zoomInfo, new Insets(0, 10, 10, 0));

		StackPane stackPane = new StackPane(scroll, zoomInfo);
		stackPane.setStyle("-fx-background-color: #1E1E1E; -fx-padding: 10px;");

		return scroll;
	}

	/**
	 * @brief Méthode principale pour lancer l'application.
	 * @author Alexis
	 * @param args Arguments de la ligne de commande.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
