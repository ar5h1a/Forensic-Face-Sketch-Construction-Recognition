import java.io.File;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class DashboardUI {

    private static String drawMode = "pen";
    private static ColorPicker colorPicker;
    private static Slider brushSlider;

    // Two-canvas architecture:
    //   bgCanvas   — holds uploaded image (eraser never touches this)
    //   drawCanvas — holds freehand strokes only
    private static Canvas bgCanvas;
    private static Canvas drawCanvas;

    public static Scene createDashboardScene(Stage stage) {
        return buildScene(stage, null);
    }

    public static Scene createDashboardSceneWithImage(Stage stage, File imageFile) {
        return buildScene(stage, imageFile);
    }

    // ================================================================
    private static Scene buildScene(Stage stage, File uploadedFile) {

        // ── Palette — warm slate, not black ──
        // Panel bg:   #2b303a  (dark blue-grey)
        // Canvas bg:  #f5f0ea  (warm off-white paper)
        // Borders:    #3e4451
        // Text:       #c8cdd6

        // ── TOP BAR ──
        HBox topBar = new HBox(8);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("OCULUS");
        title.setStyle(
            "-fx-text-fill: #c8cdd6; -fx-font-size: 17px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button backBtn    = makeTopBtn("← Back");
        Button undoBtn    = makeTopBtn("↩ Undo");
        Button redoBtn    = makeTopBtn("↪ Redo");
        Button saveBtn    = makeTopBtn("💾 Save");
        Button resetBtn   = makeTopBtn("⟳ Reset");
        Button compareBtn = makeTopBtn("⇄ Compare");
        Button profileBtn = makeTopBtn("👤 Profile");
        Button historyBtn = makeTopBtn("📋 History");

        topBar.getChildren().addAll(
            title, topSpacer, profileBtn, historyBtn, backBtn, undoBtn, redoBtn, saveBtn, resetBtn, compareBtn
        );
        topBar.setStyle(
            "-fx-background-color: #222830;" +
            "-fx-padding: 9 16 9 16;" +
            "-fx-border-color: #3e4451; -fx-border-width: 0 0 1 0;"
        );

        // ── CANVAS STACK ──
        bgCanvas   = new Canvas(560, 540);
        drawCanvas = new Canvas(560, 540);

        Pane imageLayer = new Pane();
        imageLayer.setPrefSize(560, 540);
        imageLayer.setPickOnBounds(false);

        // Warm paper canvas background — no black, no cream from eraser
        StackPane canvasWrapper = new StackPane(bgCanvas, drawCanvas, imageLayer);
        canvasWrapper.setStyle("-fx-background-color: #f5f0ea;");
        canvasWrapper.setPadding(new Insets(12));

        GraphicsContext bgGc   = bgCanvas.getGraphicsContext2D();
        GraphicsContext drawGc = drawCanvas.getGraphicsContext2D();

        // Load uploaded image as editable feature
        final javafx.scene.image.Image[] uploadedImage = {null};
        if (uploadedFile != null) {
            javafx.application.Platform.runLater(() -> {
                try {
                    uploadedImage[0] = new javafx.scene.image.Image(uploadedFile.toURI().toString());
                    // Add the uploaded image as an editable feature
                    CanvasManager.addImageFeature(imageLayer, uploadedImage[0], "Uploaded");
                } catch (Exception ex) { ex.printStackTrace(); }
            });
        }

        // ── DRAW TOOLS ──
        colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.setMaxWidth(Double.MAX_VALUE);
        brushSlider = new Slider(1, 40, 6);
        brushSlider.setMaxWidth(Double.MAX_VALUE);
        brushSlider.setShowTickLabels(true);
        brushSlider.setMajorTickUnit(10);

        // ── DRAW EVENTS — only on drawCanvas ──
        final boolean[] drawing = {false};

        drawCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            drawing[0] = true;
            handleDraw(drawGc, e.getX(), e.getY());
            e.consume();
        });
        drawCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (drawing[0]) handleDraw(drawGc, e.getX(), e.getY());
            e.consume();
        });
        drawCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> drawing[0] = false);

        canvasWrapper.setOnMousePressed(e -> {
            if (e.getTarget() == canvasWrapper || e.getTarget() == drawCanvas
                    || e.getTarget() == bgCanvas)
                CanvasManager.deselectAll(imageLayer);
        });

        // ── LEFT PANEL ──
        VBox leftPanel = new VBox(3);
        leftPanel.setPrefWidth(74); leftPanel.setMinWidth(74); leftPanel.setMaxWidth(74);
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setStyle(
            "-fx-background-color: #222830;" +
            "-fx-border-color: #3e4451; -fx-border-width: 0 1 0 0;" +
            "-fx-padding: 10 4 10 4;"
        );

        Button headBtn     = makeIconBtn("👤", "Head");
        Button hairBtn     = makeIconBtn("💇", "Hair");
        Button eyesBtn     = makeIconBtn("👁",  "Eyes");
        Button eyebrowBtn  = makeIconBtn("〰",  "Eyebrows");
        Button noseBtn     = makeIconBtn("👃", "Nose");
        Button lipsBtn     = makeIconBtn("👄", "Lips");
        Button mustacheBtn = makeIconBtn("🥸", "Mustache");
        Separator leftSep  = new Separator();
        leftSep.setStyle("-fx-background-color: #3e4451;");
        Button moreBtn     = makeIconBtn("⋯", "More");

        leftPanel.getChildren().addAll(
            headBtn, hairBtn, eyesBtn, eyebrowBtn,
            noseBtn, lipsBtn, mustacheBtn, leftSep, moreBtn
        );

        // ── MORE POPUP ──
        ContextMenu moreMenu = new ContextMenu();
        moreMenu.setAutoHide(true);

        VBox moreContent = new VBox(11);
        moreContent.setPadding(new Insets(14));
        moreContent.setPrefWidth(235);
        moreContent.setStyle("-fx-background-color: #2b303a;");

        Label toolLabel = makeMenuLabel("DRAW TOOL");

        ToggleGroup toolGroup = new ToggleGroup();
        ToggleButton penBtn    = makeToolToggle("✏  Pen",    toolGroup);
        ToggleButton pencilBtn = makeToolToggle("🖊  Pencil", toolGroup);
        ToggleButton eraserBtn = makeToolToggle("⬜  Eraser", toolGroup);
        penBtn.setSelected(true);
        drawMode = "pen";

        penBtn.setOnAction(e    -> { drawMode = "pen";    moreMenu.hide(); });
        pencilBtn.setOnAction(e -> { drawMode = "pencil"; moreMenu.hide(); });
        eraserBtn.setOnAction(e -> { drawMode = "eraser"; moreMenu.hide(); });

        HBox toolRow = new HBox(7, penBtn, pencilBtn, eraserBtn);

        Button clearDrawBtn = new Button("🗑  Clear Drawing");
        clearDrawBtn.setMaxWidth(Double.MAX_VALUE);
        clearDrawBtn.setStyle(neutralBtn());
        clearDrawBtn.setOnMouseEntered(e -> clearDrawBtn.setStyle(neutralBtnHover()));
        clearDrawBtn.setOnMouseExited(e  -> clearDrawBtn.setStyle(neutralBtn()));
        clearDrawBtn.setOnAction(e -> {
            drawGc.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
            moreMenu.hide();
        });

        moreContent.getChildren().addAll(
            toolLabel, toolRow,
            makeMenuLabel("COLOR"), colorPicker,
            makeMenuLabel("BRUSH SIZE"), brushSlider,
            new Separator(), clearDrawBtn
        );

        CustomMenuItem moreItem = new CustomMenuItem(moreContent, false);
        moreMenu.getItems().add(moreItem);
        moreBtn.setOnAction(e -> {
            if (moreMenu.isShowing()) moreMenu.hide();
            else moreMenu.show(moreBtn, Side.RIGHT, 0, 0);
        });

        // ── RIGHT PANEL ──
        Label rightTitle = new Label("HEAD SHAPES");
        rightTitle.setStyle(
            "-fx-text-fill: #c8cdd6; -fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        // Delete button — subtle, no red border lines
        Button deleteBtn = new Button("✕ Delete");
        deleteBtn.setStyle(neutralBtn());
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(neutralBtnHover()));
        deleteBtn.setOnMouseExited(e  -> deleteBtn.setStyle(neutralBtn()));

        // Lock/Unlock button — single button that toggles label
        Button lockBtn = new Button("🔓 Lock");
        lockBtn.setStyle(neutralBtn());
        lockBtn.setOnMouseEntered(e -> lockBtn.setStyle(neutralBtnHover()));
        lockBtn.setOnMouseExited(e  -> lockBtn.setStyle(neutralBtn()));
        lockBtn.setOnAction(e -> {
            if (CanvasManager.selected == null) return;
            CanvasManager.toggleLock(imageLayer);
            boolean locked = CanvasManager.isSelectedLocked();
            // Toggle button text: Lock ↔ Unlock
            lockBtn.setText(locked ? "🔒 Unlock" : "🔓 Lock");
        });

        Region rhSpacer = new Region();
        HBox.setHgrow(rhSpacer, Priority.ALWAYS);

        HBox rightHeader = new HBox(6, rightTitle, rhSpacer, lockBtn, deleteBtn);
        rightHeader.setAlignment(Pos.CENTER_LEFT);
        rightHeader.setPadding(new Insets(0, 0, 10, 0));

        Separator rightSep = new Separator();
        rightSep.setStyle("-fx-background-color: #3e4451;");

        TilePane featureTiles = new TilePane();
        featureTiles.setPrefColumns(3);
        featureTiles.setHgap(8); featureTiles.setVgap(8);
        featureTiles.setStyle("-fx-background-color: transparent;");

        ScrollPane featureScroll = new ScrollPane(featureTiles);
        featureScroll.setFitToWidth(true);
        featureScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        featureScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        featureScroll.setStyle(
            "-fx-background-color: transparent; -fx-background: transparent;" +
            "-fx-border-color: transparent;"
        );
        VBox.setVgrow(featureScroll, Priority.ALWAYS);

        VBox rightPanel = new VBox(0, rightHeader, rightSep, featureScroll);
        rightPanel.setPrefWidth(280); rightPanel.setMinWidth(280);
        rightPanel.setStyle(
            "-fx-background-color: #222830;" +
            "-fx-border-color: #3e4451; -fx-border-width: 0 0 0 1;" +
            "-fx-padding: 12;"
        );

        // ── Z-ORDER TOOLBAR ──
        Label zLabel = new Label("LAYER:");
        zLabel.setStyle("-fx-text-fill: #666c78; -fx-font-size: 10px; -fx-font-family: 'Courier New';");
        Button btnFront = makeZBtn("⬆⬆ Front");
        Button btnFwd   = makeZBtn("↑ Forward");
        Button btnBwd   = makeZBtn("↓ Backward");
        Button btnBack  = makeZBtn("⬇⬇ Back");

        HBox zToolbar = new HBox(6, zLabel, btnFront, btnFwd, btnBwd, btnBack);
        zToolbar.setAlignment(Pos.CENTER_LEFT);
        zToolbar.setStyle(
            "-fx-background-color: #222830; -fx-padding: 6 12 6 12;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1 0 0 0;"
        );

        VBox centerArea = new VBox(0, canvasWrapper, zToolbar);
        VBox.setVgrow(canvasWrapper, Priority.ALWAYS);

        // ── ACTIONS ──
        backBtn.setOnAction(e -> stage.setScene(ChoiceUI.createChoiceScene(stage)));

        undoBtn.setOnAction(e -> {
            if (!CanvasManager.undoStack.isEmpty()) {
                Pane last = CanvasManager.undoStack.pop();
                imageLayer.getChildren().remove(last);
                CanvasManager.redoStack.push(last);
                if (CanvasManager.selected == last) CanvasManager.selected = null;
            }
        });
        redoBtn.setOnAction(e -> {
            if (!CanvasManager.redoStack.isEmpty()) {
                Pane node = CanvasManager.redoStack.pop();
                imageLayer.getChildren().add(node);
                CanvasManager.undoStack.push(node);
            }
        });

        resetBtn.setOnAction(e -> {
            drawGc.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
            bgGc.clearRect(0, 0, bgCanvas.getWidth(), bgCanvas.getHeight());
            imageLayer.getChildren().clear();
            CanvasManager.undoStack.clear();
            CanvasManager.redoStack.clear();
            CanvasManager.selected = null;
            lockBtn.setText("🔓 Lock");
        });

        deleteBtn.setOnAction(e -> {
            if (CanvasManager.selected != null && !CanvasManager.isSelectedLocked()) {
                imageLayer.getChildren().remove(CanvasManager.selected);
                CanvasManager.undoStack.remove(CanvasManager.selected);
                CanvasManager.selected = null;
                lockBtn.setText("🔓 Lock");
            }
        });

        saveBtn.setOnAction(e -> {
            try {
                javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
                fc.setTitle("Save Sketch");
                fc.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("PNG", "*.png"));
                fc.setInitialFileName("sketch.png");
                File file = fc.showSaveDialog(stage);
                if (file != null) {
                    WritableImage img = canvasWrapper.snapshot(null, null);
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        compareBtn.setOnAction(e -> {

            // Take snapshot of full sketch
            WritableImage snapshot = canvasWrapper.snapshot(null, null);

            // Move to match screen
            stage.setScene(MatchUI.createMatchScene(stage, snapshot));
        });

        profileBtn.setOnAction(e -> stage.setScene(ProfileUI.createProfileScene(stage)));
        historyBtn.setOnAction(e -> stage.setScene(MatchHistoryUI.createMatchHistoryScene(stage)));

        btnFront.setOnAction(e -> CanvasManager.bringToFront(imageLayer));
        btnFwd.setOnAction(e   -> CanvasManager.bringForward(imageLayer));
        btnBwd.setOnAction(e   -> CanvasManager.sendBackward(imageLayer));
        btnBack.setOnAction(e  -> CanvasManager.sendToBack(imageLayer));

        // Reset lock button when selection is cleared from the canvas side
        imageLayer.getChildren().addListener(
            (javafx.collections.ListChangeListener<javafx.scene.Node>) c -> {
                if (CanvasManager.selected == null) lockBtn.setText("🔓 Lock");
            });

        // Feature sidebar
        headBtn.setOnAction(e -> { rightTitle.setText("HEAD SHAPES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Head"); });
        hairBtn.setOnAction(e -> { rightTitle.setText("HAIR STYLES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Hair"); });
        eyesBtn.setOnAction(e -> { rightTitle.setText("EYE SHAPES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Eyes"); });
        eyebrowBtn.setOnAction(e -> { rightTitle.setText("EYEBROW STYLES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Eyebrows"); });
        noseBtn.setOnAction(e -> { rightTitle.setText("NOSE SHAPES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Nose"); });
        lipsBtn.setOnAction(e -> { rightTitle.setText("LIP SHAPES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Lips"); });
        mustacheBtn.setOnAction(e -> { rightTitle.setText("MUSTACHE STYLES");
            FeatureLoader.loadFeatures(featureTiles, imageLayer, "Mustache"); });

        FeatureLoader.loadFeatures(featureTiles, imageLayer, "Head");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(leftPanel);
        root.setRight(rightPanel);
        root.setCenter(centerArea);
        root.setStyle("-fx-background-color: #2b303a;");

        return new Scene(root, 1060, 680);
    }

    // ================================================================
    // Draw — eraser clears drawCanvas only (bgCanvas untouched)
    // ================================================================
    private static void handleDraw(GraphicsContext gc, double x, double y) {
        double size = brushSlider.getValue();
        switch (drawMode) {
            case "eraser":
                gc.clearRect(x - size, y - size, size * 2, size * 2);
                break;
            case "pencil": {
                Color base = colorPicker.getValue();
                gc.setFill(new Color(base.getRed(), base.getGreen(), base.getBlue(), 0.38));
                double ps = Math.max(1.5, size * 0.55);
                gc.fillOval(x, y, ps, ps);
                gc.setFill(new Color(base.getRed(), base.getGreen(), base.getBlue(), 0.18));
                gc.fillOval(x + 1.3, y + 0.7, ps * 0.6, ps * 0.6);
                gc.fillOval(x - 0.8, y + 1.1, ps * 0.45, ps * 0.45);
                break;
            }
            default:
                gc.setFill(colorPicker.getValue());
                gc.fillOval(x, y, size, size);
        }
    }

    // ================================================================
    // Style helpers — all neutral slate, no red lines anywhere
    // ================================================================
    private static Button makeTopBtn(String text) {
        Button b = new Button(text);
        b.setStyle(topNormal());
        b.setOnMouseEntered(e -> b.setStyle(topHover()));
        b.setOnMouseExited(e  -> b.setStyle(topNormal()));
        return b;
    }

    private static String topNormal() {
        return "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
               "-fx-font-size: 11px; -fx-padding: 5 12 5 12;" +
               "-fx-background-radius: 3; -fx-cursor: hand;" +
               "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;";
    }
    private static String topHover() {
        return "-fx-background-color: #4a5260; -fx-text-fill: #ffffff;" +
               "-fx-font-size: 11px; -fx-padding: 5 12 5 12;" +
               "-fx-background-radius: 3; -fx-cursor: hand;" +
               "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;";
    }

    private static Button makeIconBtn(String icon, String label) {
        Label il = new Label(icon);
        il.setStyle("-fx-text-fill: #c8cdd6; -fx-font-size: 20px;");
        Label nl = new Label(label);
        nl.setStyle("-fx-text-fill: #666c78; -fx-font-size: 8px; -fx-font-family: 'Courier New';");
        VBox box = new VBox(2, il, nl);
        box.setAlignment(Pos.CENTER);
        Button btn = new Button();
        btn.setGraphic(box);
        btn.setPrefWidth(66); btn.setPrefHeight(54);
        String n = "-fx-background-color: transparent; -fx-cursor: hand;" +
                   "-fx-border-color: transparent; -fx-background-radius: 5;";
        String h = "-fx-background-color: #333a47; -fx-cursor: hand;" +
                   "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-background-radius: 5;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e  -> btn.setStyle(n));
        return btn;
    }

    private static ToggleButton makeToolToggle(String text, ToggleGroup group) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(group);
        String n = "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
                   "-fx-font-size: 11px; -fx-padding: 5 10 5 10;" +
                   "-fx-background-radius: 3; -fx-cursor: hand;" +
                   "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;";
        String s = "-fx-background-color: #4a5260; -fx-text-fill: #ffffff;" +
                   "-fx-font-size: 11px; -fx-padding: 5 10 5 10;" +
                   "-fx-background-radius: 3; -fx-cursor: hand;" +
                   "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;";
        tb.setStyle(n);
        tb.selectedProperty().addListener((obs, o, nv) -> tb.setStyle(nv ? s : n));
        return tb;
    }

    private static Button makeZBtn(String text) {
        Button b = new Button(text);
        b.setStyle(topNormal());
        b.setOnMouseEntered(e -> b.setStyle(topHover()));
        b.setOnMouseExited(e  -> b.setStyle(topNormal()));
        return b;
    }

    // Neutral slate button — no red, no danger colours
    private static String neutralBtn() {
        return "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
               "-fx-font-size: 10px; -fx-padding: 4 10 4 10;" +
               "-fx-background-radius: 3; -fx-cursor: hand;" +
               "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;";
    }
    private static String neutralBtnHover() {
        return "-fx-background-color: #4a5260; -fx-text-fill: #ffffff;" +
               "-fx-font-size: 10px; -fx-padding: 4 10 4 10;" +
               "-fx-background-radius: 3; -fx-cursor: hand;" +
               "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;";
    }

    private static Label makeMenuLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #8a9bb0; -fx-font-size: 10px;" +
                   "-fx-font-weight: bold; -fx-font-family: 'Courier New';");
        return l;
    }
}