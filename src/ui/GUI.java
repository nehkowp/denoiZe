package ui;

import java.io.File;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.base.Img;
import model.base.Pixel;
import service.bruit.BruiteurImage;
import service.debruitage.DebruiteurImage;

public class GUI extends Application {
	
	private ImageView imageView;

	@Override
	public void start(Stage primaryStage) throws Exception {
	    primaryStage.setTitle("Denoiℤe");

	    BorderPane root = new BorderPane();
	    root.setStyle("-fx-background-color: black;");
	    root.setPrefSize(1400, 1000);

	    VBox centerBox = new VBox();
	    centerBox.setAlignment(Pos.CENTER);
	    centerBox.setMaxWidth(700);

	    StackPane imagePane = creerCentre();
	    centerBox.getChildren().add(imagePane);

	    BorderPane paramPane = creerLeft(primaryStage, imageView);

	    root.setRight(centerBox);
	    root.setLeft(paramPane);

	    BorderPane.setMargin(centerBox, new Insets(30));
	    BorderPane.setMargin(paramPane, new Insets(30));

	    Scene scene = new Scene(root);
	    scene.getStylesheets().add("style.css");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}


	public StackPane creerCentre() {
	    StackPane container = new StackPane();
	    container.setStyle("-fx-background-color: #4a4848; -fx-background-radius: 20px;");
	    container.setPadding(new Insets(120));

	    Image img = new Image("file:data/Fx/lena.png");
	    imageView = new ImageView(img);
	    imageView.setFitWidth(700);
	    imageView.setFitHeight(700);
	    imageView.setPreserveRatio(true);

	    container.getChildren().add(imageView);
	    return container;
	}


	public BorderPane creerLeft(Stage stage, ImageView imageView) {
	    BorderPane container = new BorderPane();
	    container.setStyle("-fx-background-color: #4a4848; -fx-background-radius: 20px;");
	    container.setPadding(new Insets(100));

	    Button btnAddImage = new Button("+ ADD IMAGE");
	    Button btnDenoize = new Button("DENOIZE IMAGE");
	    Button btnNoize = new Button("NOIZE IMAGE");

	    btnAddImage.setPadding(new Insets(10, 25, 10, 25));
	    btnDenoize.setPadding(new Insets(10));
	    btnNoize.setPadding(new Insets(10, 18, 10, 18));

	    btnAddImage.getStyleClass().add("idBtnAddImage");
	    btnDenoize.getStyleClass().add("idBtnDenoize");
	    btnNoize.getStyleClass().add("idBtnNoize");

	    // === ComboBox Local/Global ===
	    ComboBox<String> choixMode = new ComboBox<>();
	    choixMode.getItems().addAll("Local", "Global");
	    choixMode.setValue("Local");

	    VBox widgetsBox = new VBox(10);
	    widgetsBox.setAlignment(Pos.CENTER);
	    widgetsBox.setPadding(new Insets(20));

	    // Widgets à afficher dynamiquement
	    Button boutonLocal = new Button("Bouton Local");
	    Slider sliderGlobal = new Slider(10, 30, 10);
	    sliderGlobal.setMajorTickUnit(10);
	    sliderGlobal.setMinorTickCount(0);
	    sliderGlobal.setShowTickLabels(true);
	    sliderGlobal.setShowTickMarks(true);
	    sliderGlobal.setSnapToTicks(true);
	    sliderGlobal.setPrefWidth(150);

	    // Mise à jour widgets en fonction du choix dans le ComboBox
	    choixMode.setOnAction(e -> {
	        widgetsBox.getChildren().clear();
	        String selection = choixMode.getValue();

	        if ("Local".equals(selection)) {
	            Label labelPatchs = new Label("Taille Patchs :");
	            labelPatchs.setTextFill(Color.WHITE);

	            ToggleGroup tgPatchsLocal = new ToggleGroup();
	            RadioButton rb19= new RadioButton("5x5");
	            RadioButton rb21 = new RadioButton("7x7");
	            RadioButton rb23 = new RadioButton("9x9");
	            rb19.setToggleGroup(tgPatchsLocal);
	            rb21.setToggleGroup(tgPatchsLocal);
	            rb23.setToggleGroup(tgPatchsLocal);
	            rb19.setTextFill(Color.WHITE);
	            rb21.setTextFill(Color.WHITE);
	            rb23.setTextFill(Color.WHITE);

	            VBox radioPatchsBox = new VBox(5, rb19, rb21, rb23);
	            radioPatchsBox.setAlignment(Pos.CENTER_LEFT);

	            // Label + TextField pour Taille Fenetre
	            Label labelFenetre = new Label("Taille Fenêtre :");
	            labelFenetre.setTextFill(Color.WHITE);
	            TextField tfFenetre = new TextField();
	            tfFenetre.setPromptText("Entrer une taille...");
	            tfFenetre.setMaxWidth(120);

	            widgetsBox.getChildren().addAll(
	                labelPatchs,
	                radioPatchsBox,
	                labelFenetre,
	                tfFenetre
	            );

	        } else if ("Global".equals(selection)) {
	            Label labelPatchGlobal = new Label("Taille Patch :");
	            labelPatchGlobal.setTextFill(Color.WHITE);

	            ToggleGroup tgPatchGlobal = new ToggleGroup();
	            RadioButton rb19 = new RadioButton("19x19");
	            RadioButton rb21 = new RadioButton("21x21");
	            RadioButton rb23 = new RadioButton("23x23");
	            rb19.setToggleGroup(tgPatchGlobal);
	            rb21.setToggleGroup(tgPatchGlobal);
	            rb23.setToggleGroup(tgPatchGlobal);
	            rb19.setTextFill(Color.WHITE);
	            rb21.setTextFill(Color.WHITE);
	            rb23.setTextFill(Color.WHITE);

	            VBox radioGlobalBox = new VBox(5, rb19, rb21, rb23);
	            radioGlobalBox.setAlignment(Pos.CENTER_LEFT);

	            widgetsBox.getChildren().addAll(labelPatchGlobal, radioGlobalBox);
	        }
	    });
	    
	    choixMode.getStyleClass().add("idComboMode");

	    // === Slider Sigma ===
	    Label sliderLabel = new Label("Sigma: 10");
	    sliderLabel.setTextFill(Color.WHITE);

	    Slider sigmaSlider = new Slider(10, 30, 10);
	    sigmaSlider.setMajorTickUnit(10);
	    sigmaSlider.setMinorTickCount(0);
	    sigmaSlider.setShowTickLabels(true);
	    sigmaSlider.setShowTickMarks(true);
	    sigmaSlider.setSnapToTicks(true);
	    sigmaSlider.setPrefWidth(200);
	    sigmaSlider.getStyleClass().add("idSigmaSlider");

	    sigmaSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
	        sliderLabel.setText("Sigma: " + newVal.intValue());
	    });

	    VBox sliderBox = new VBox(5, sliderLabel, sigmaSlider);
	    sliderBox.setAlignment(Pos.CENTER);
	    sliderBox.setPadding(new Insets(20, 0, 20, 0));
	    
	 // === RadioButton Type de Seuillage ===
	    Label labelTypeSeuillage = new Label("Type de seuillage :");
	    labelTypeSeuillage.setTextFill(Color.WHITE);

	    ToggleGroup tgTypeSeuillage = new ToggleGroup();
	    RadioButton rbVisuShrink = new RadioButton("VisuShrink");
	    RadioButton rbBayesShrink = new RadioButton("BayesShrink");
	    rbVisuShrink.setToggleGroup(tgTypeSeuillage);
	    rbBayesShrink.setToggleGroup(tgTypeSeuillage);
	    rbVisuShrink.setTextFill(Color.WHITE);
	    rbBayesShrink.setTextFill(Color.WHITE);
	    rbVisuShrink.setSelected(true); // option par défaut

	    VBox typeSeuillageBox = new VBox(5, labelTypeSeuillage, rbVisuShrink, rbBayesShrink);
	    typeSeuillageBox.setAlignment(Pos.CENTER_LEFT);

	    // === RadioButton Fonction de Seuillage ===
	    Label labelFonctionSeuillage = new Label("Fonction de seuillage :");
	    labelFonctionSeuillage.setTextFill(Color.WHITE);

	    ToggleGroup tgFonctionSeuillage = new ToggleGroup();
	    RadioButton rbDur = new RadioButton("Dur");
	    RadioButton rbDoux = new RadioButton("Doux");
	    rbDur.setToggleGroup(tgFonctionSeuillage);
	    rbDoux.setToggleGroup(tgFonctionSeuillage);
	    rbDur.setTextFill(Color.WHITE);
	    rbDoux.setTextFill(Color.WHITE);
	    rbDur.setSelected(true); // option par défaut

	    VBox fonctionSeuillageBox = new VBox(5, labelFonctionSeuillage, rbDur, rbDoux);
	    fonctionSeuillageBox.setAlignment(Pos.CENTER_LEFT);


	    // === Actions boutons ===
	    btnAddImage.setOnAction(e -> {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Choisir une image");
	        fileChooser.getExtensionFilters().addAll(
	            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp")
	        );
	        File file = fileChooser.showOpenDialog(stage);
	        if (file != null) {
	            imageView.setImage(new Image(file.toURI().toString()));
	        }
	    });

	    btnNoize.setOnAction(e -> {
	        Image current = imageView.getImage();
	        if (current != null) {
	            Img imgOriginale = imageToImg(current);
	            double sigma = sigmaSlider.getValue();
	            Img imgBruitée = BruiteurImage.noising(imgOriginale, sigma);
	            Image imgFX = imgToImage(imgBruitée);
	            imageView.setImage(imgFX);
	        }
	    });
	    
	    btnDenoize.setOnAction(e -> {
	        Image current = imageView.getImage();
	        DebruiteurImage den = new DebruiteurImage();
	        if (current != null) {
	            Img imgOriginale = imageToImg(current);

	            // Paramètre sigma
	            double sigma = sigmaSlider.getValue();

	            // Type de seuillage
	            String typeSeuil = ((RadioButton) tgTypeSeuillage.getSelectedToggle()).getText();

	            // Fonction de seuillage
	            String fonctionSeuillage = ((RadioButton) tgFonctionSeuillage.getSelectedToggle()).getText();

	            // Mode local/global
	            boolean modeLocal = choixMode.getValue().equals("Local");

	            int taillePatch = 0;

	            if (modeLocal) {
	                // Récupération du RadioButton sélectionné pour taille patch local
	                for (Node node : widgetsBox.getChildren()) {
	                    if (node instanceof VBox) {
	                        VBox vbox = (VBox) node;
	                        for (Node rbNode : vbox.getChildren()) {
	                            if (rbNode instanceof RadioButton rb && rb.isSelected()) {
	                                String text = rb.getText(); // e.g. "5x5"
	                                taillePatch = Integer.parseInt(text.split("x")[0]);
	                            }
	                        }
	                    }
	                }
	            } else {
	                // Mode global : valeurs "19x19", "21x21", "23x23" 
	                for (Node node : widgetsBox.getChildren()) {
	                    if (node instanceof VBox) {
	                        VBox vbox = (VBox) node;
	                        for (Node rbNode : vbox.getChildren()) {
	                            if (rbNode instanceof RadioButton rb && rb.isSelected()) {
	                                String text = rb.getText(); 
	                                taillePatch = Integer.parseInt(text.split("x")[0]);
	                            }
	                        }
	                    }
	                }
	            }

	            // Appel à la méthode de débruitage
	            Img imgDenoised = den.imageDen(imgOriginale, typeSeuil, fonctionSeuillage, sigma, taillePatch, modeLocal);

	            // Affichage du résultat
	            imageView.setImage(imgToImage(imgDenoised));
	        }
	    });


	    VBox topBox = new VBox(10, btnAddImage, choixMode, widgetsBox);
	    topBox.setAlignment(Pos.TOP_CENTER);

	    VBox bottomBox = new VBox(15, sliderBox, typeSeuillageBox, fonctionSeuillageBox, btnNoize, btnDenoize);
	    bottomBox.setAlignment(Pos.BOTTOM_CENTER);
	    bottomBox.setPadding(new Insets(20));

	    container.setTop(topBox);
	    BorderPane.setAlignment(topBox, Pos.TOP_CENTER);

	    container.setBottom(bottomBox);
	    BorderPane.setAlignment(bottomBox, Pos.BOTTOM_CENTER);

	    return container;
	}

	
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


    // Convertit un Img vers Image JavaFX
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


	public static void main(String[] args) {
		launch(args);
	}
}
