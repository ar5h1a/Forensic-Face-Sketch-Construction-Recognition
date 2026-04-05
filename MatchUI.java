import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class MatchUI {

    public static Scene createMatchScene(Stage stage, javafx.scene.image.WritableImage sketchSnapshot) {

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

        Button homeBtn = makeTopBtn("⌂ Home");
        Button backBtn = makeTopBtn("← Dashboard");

        topBar.getChildren().addAll(title, topSpacer, homeBtn, backBtn);
        topBar.setStyle(
            "-fx-background-color: #222830;" +
            "-fx-padding: 9 16 9 16;" +
            "-fx-border-color: #3e4451; -fx-border-width: 0 0 1 0;"
        );

        homeBtn.setOnAction(e -> stage.setScene(SplashScreen.createSplash(stage)));
        backBtn.setOnAction(e -> stage.setScene(DashboardUI.createDashboardScene(stage)));

        // ── PAGE TITLE ──
        Label pageTitle = new Label("FACE MATCH ANALYSIS");
        pageTitle.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 16px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 3px;"
        );

        Label pageSubtitle = new Label("Upload a sketch to find the closest match in the database");
        pageSubtitle.setStyle(
            "-fx-text-fill: #a8b0c0; -fx-font-size: 12px; -fx-font-family: 'Courier New';"
        );

        VBox pageTitleBox = new VBox(4, pageTitle, pageSubtitle);
        pageTitleBox.setAlignment(Pos.CENTER);
        pageTitleBox.setPadding(new Insets(18, 0, 14, 0));

        // ================================================================
        // BOX 1 — Upload Sketch
        // ================================================================
        Label box1Label = makeBoxLabel("UPLOAD SKETCH");

        // Upload area — click to browse
        Label uploadIcon = new Label("⬆");
        uploadIcon.setStyle(
            "-fx-font-size: 28px; -fx-text-fill: #8a9bb0;"
        );
        Label uploadHint = new Label("Click to browse");
        uploadHint.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
        );

        ImageView uploadedView = new ImageView();
        uploadedView.setFitWidth(180);
        uploadedView.setFitHeight(180);
        uploadedView.setPreserveRatio(true);
        uploadedView.setVisible(false);

        VBox uploadPlaceholder = new VBox(8, uploadIcon, uploadHint);
        uploadPlaceholder.setAlignment(Pos.CENTER);

        StackPane box1Inner = new StackPane(uploadPlaceholder, uploadedView);
        box1Inner.setPrefSize(220, 200);
        box1Inner.setStyle(
            "-fx-background-color: #1e232c;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1;" +
            "-fx-border-style: dashed; -fx-background-radius: 4; -fx-border-radius: 4;"
        );
        box1Inner.setCursor(javafx.scene.Cursor.HAND);

        // Status label below box1
        Label uploadStatus = new Label("");
        uploadStatus.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
        );
        uploadStatus.setWrapText(true);
        uploadStatus.setMaxWidth(220);
        uploadStatus.setAlignment(Pos.CENTER);

        // Find Match button — hidden until upload done
        Button findMatchBtn = makeActionBtn("🔍  Find Match");
        findMatchBtn.setVisible(false);
        findMatchBtn.setManaged(false);

        VBox box1 = new VBox(8, box1Label, box1Inner, uploadStatus, findMatchBtn);
        box1.setAlignment(Pos.TOP_CENTER);

        // Track uploaded image
        final Image[] uploadedImage = {null};

        box1Inner.setOnMouseClicked(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Sketch Image");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                    "Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                Image img = new Image(file.toURI().toString());
                uploadedImage[0] = img;
                uploadedView.setImage(img);
                uploadedView.setVisible(true);
                uploadPlaceholder.setVisible(false);

                // Style box as filled
                box1Inner.setStyle(
                    "-fx-background-color: #1e232c;" +
                    "-fx-border-color: #8a9bb0; -fx-border-width: 1;" +
                    "-fx-background-radius: 4; -fx-border-radius: 4;"
                );

                // Success message
                uploadStatus.setStyle(
                    "-fx-text-fill: #7aab7a; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
                );
                uploadStatus.setText("✓  Upload successful — " + file.getName());

                // Show find match button
                findMatchBtn.setVisible(true);
                findMatchBtn.setManaged(true);
            }
        });

        // Hover effect on upload box
        box1Inner.setOnMouseEntered(ev -> {
            if (uploadedImage[0] == null)
                box1Inner.setStyle(
                    "-fx-background-color: #252a34;" +
                    "-fx-border-color: #8a9bb0; -fx-border-width: 1;" +
                    "-fx-border-style: dashed; -fx-background-radius: 4; -fx-border-radius: 4;"
                );
        });
        box1Inner.setOnMouseExited(ev -> {
            if (uploadedImage[0] == null)
                box1Inner.setStyle(
                    "-fx-background-color: #1e232c;" +
                    "-fx-border-color: #3e4451; -fx-border-width: 1;" +
                    "-fx-border-style: dashed; -fx-background-radius: 4; -fx-border-radius: 4;"
                );
        });

        // ================================================================
        // BOX 2 — Matched Result
        // ================================================================
        Label box2Label = makeBoxLabel("MATCHED RESULT");

        Label matchPlaceholderIcon = new Label("◎");
        matchPlaceholderIcon.setStyle("-fx-font-size: 28px; -fx-text-fill: #3e4451;");
        Label matchPlaceholderText = new Label("Awaiting match...");
        matchPlaceholderText.setStyle(
            "-fx-text-fill: #3e4451; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
        );
        VBox matchPlaceholder = new VBox(8, matchPlaceholderIcon, matchPlaceholderText);
        matchPlaceholder.setAlignment(Pos.CENTER);

        ImageView matchedView = new ImageView();
        matchedView.setFitWidth(180);
        matchedView.setFitHeight(180);
        matchedView.setPreserveRatio(true);
        matchedView.setVisible(false);

        StackPane box2Inner = new StackPane(matchPlaceholder, matchedView);
        box2Inner.setPrefSize(220, 200);
        box2Inner.setStyle(
            "-fx-background-color: #1e232c;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );

        Label matchStatus = new Label("");
        matchStatus.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
        );
        matchStatus.setWrapText(true);
        matchStatus.setMaxWidth(220);
        matchStatus.setAlignment(Pos.CENTER);

        VBox box2 = new VBox(8, box2Label, box2Inner, matchStatus);
        box2.setAlignment(Pos.TOP_CENTER);

        // ================================================================
        // BOX 3 — Analysis Results
        // ================================================================
        Label box3Label = makeBoxLabel("ANALYSIS");

        // Similarity bar
        Label simLabel     = makeStatLabel("MATCHED NAME");
        Label simValue     = makeStatValue("—");
        Rectangle simBarBg = makeBar();
        Rectangle simBarFg = makeBarFill(0, "#7aab7a");

        // Confidence bar
        Label confLabel     = makeStatLabel("CONFIDENCE");
        Label confValue     = makeStatValue("—");
        Rectangle confBarBg = makeBar();
        Rectangle confBarFg = makeBarFill(0, "#7a9aab");

        // Match status badge
        Label matchBadge = new Label("PENDING");
        matchBadge.setStyle(
            "-fx-background-color: #2b303a; -fx-text-fill: #8a9bb0;" +
            "-fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-font-weight: bold;" +
            "-fx-padding: 5 12 5 12; -fx-background-radius: 3;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;" +
            "-fx-letter-spacing: 2px;"
        );

        // Database name row
        Label dbNameLabel = makeStatLabel("DATABASE MATCH");
        Label dbNameValue = new Label("—");
        dbNameValue.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 12px; -fx-font-family: 'Courier New';"
        );
        dbNameValue.setWrapText(true);
        dbNameValue.setMaxWidth(200);

        Separator statSep = new Separator();
        statSep.setStyle("-fx-background-color: #3e4451;");

        VBox box3Inner = new VBox(10,
            matchBadge,
            statSep,
            simLabel,
            new StackPane(simBarBg, simBarFg),
            simValue,
            confLabel,
            new StackPane(confBarBg, confBarFg),
            confValue,
            dbNameLabel,
            dbNameValue
        );
        box3Inner.setPrefSize(220, 200);
        box3Inner.setAlignment(Pos.TOP_CENTER);
        box3Inner.setPadding(new Insets(14));
        box3Inner.setStyle(
            "-fx-background-color: #1e232c;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );

        VBox box3 = new VBox(8, box3Label, box3Inner);
        box3.setAlignment(Pos.TOP_CENTER);

        final String[] lastMatchStatus = {""};
        final String[] lastMatchedRecord = {""};
        final double[] lastSimilarity = {0};
        final double[] lastConfidence = {0};
        
        
        Button exportBtn = new Button("📥 Export as PDF");
        exportBtn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 7 16 7 16;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;"
        );
        exportBtn.setVisible(false);
        exportBtn.setManaged(false);
        // ================================================================
        // FIND MATCH ACTION
        // ================================================================
        findMatchBtn.setOnAction(e -> {
            if (uploadedImage[0] == null) return;

            // Simulate searching state
            matchStatus.setStyle(
                "-fx-text-fill: #7a9aab; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
            );
            matchStatus.setText("Searching database...");
            matchBadge.setText("SEARCHING");
            matchBadge.setStyle(
                "-fx-background-color: #2b303a; -fx-text-fill: #7a9aab;" +
                "-fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-font-weight: bold;" +
                "-fx-padding: 5 12 5 12; -fx-background-radius: 3;" +
                "-fx-border-color: #7a9aab; -fx-border-width: 1; -fx-border-radius: 3;" +
                "-fx-letter-spacing: 2px;"
            );

            // Simulate a delay then show placeholder result
            // Replace this block with real DB/Python call when backend is ready
            javafx.animation.PauseTransition delay =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
            delay.setOnFinished(ev -> {
                // ── PLACEHOLDER RESULT ──
                // When backend is connected, replace below with real values:
                //   matchedView.setImage(new Image("file:resources/match/result.jpg"));
                //   simValue.setText("95.97%");  confValue.setText("99.99%");
                //   dbNameValue.setText("John Doe — ID #4821");

                boolean matchFound = false; // ← flip to true when DB connected

                if (matchFound) {
                    matchedView.setImage(
                        new Image("file:resources/match/sample.jpg"));
                    matchedView.setVisible(true);
                    matchPlaceholder.setVisible(false);
                    box2Inner.setStyle(
                        "-fx-background-color: #1e232c;" +
                        "-fx-border-color: #7aab7a; -fx-border-width: 1;" +
                        "-fx-background-radius: 4; -fx-border-radius: 4;"
                    );

                    simValue.setText("95.97%");
                    confValue.setText("99.99%");
                    dbNameValue.setText("sample.jpg");
                    simBarFg.setWidth(200 * 0.9597);
                    confBarFg.setWidth(200 * 0.9999);

                    lastMatchStatus[0] = "MATCH FOUND";
                    lastMatchedRecord[0] = "sample.jpg";
                    lastSimilarity[0] = 0.9597;
                    lastConfidence[0] = 0.9999;
                    
                    exportBtn.setVisible(true);
                    exportBtn.setManaged(true);

                    matchBadge.setText("MATCH FOUND");
                    matchBadge.setStyle(
                        "-fx-background-color: #1a2e1a; -fx-text-fill: #7aab7a;" +
                        "-fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-font-weight: bold;" +
                        "-fx-padding: 5 12 5 12; -fx-background-radius: 3;" +
                        "-fx-border-color: #7aab7a; -fx-border-width: 1; -fx-border-radius: 3;" +
                        "-fx-letter-spacing: 2px;"
                    );
                    matchStatus.setStyle(
                        "-fx-text-fill: #7aab7a; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
                    );
                    matchStatus.setText("✓  Match found successful — Database record located");
                    
                    // Add to history
                    MatchHistoryUI.addMatch("sketch.png", "sample.jpg", 0.9597, "MATCH FOUND");

                } else {
                    // No match
                    matchPlaceholderIcon.setText("✕");
                    matchPlaceholderText.setText("No match found");
                    matchPlaceholderIcon.setStyle(
                        "-fx-font-size: 28px; -fx-text-fill: #c07a7a;");
                    matchPlaceholderText.setStyle(
                        "-fx-text-fill: #c07a7a; -fx-font-size: 11px; -fx-font-family: 'Courier New';");

                    lastMatchStatus[0] = "NO MATCH";
                    lastMatchedRecord[0] = "—";
                    lastSimilarity[0] = 0;
                    lastConfidence[0] = 0;
                    
                    exportBtn.setVisible(true);
                    exportBtn.setManaged(true);

                    matchBadge.setText("NO MATCH");
                    matchBadge.setStyle(
                        "-fx-background-color: #2e1a1a; -fx-text-fill: #c07a7a;" +
                        "-fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-font-weight: bold;" +
                        "-fx-padding: 5 12 5 12; -fx-background-radius: 3;" +
                        "-fx-border-color: #c07a7a; -fx-border-width: 1; -fx-border-radius: 3;" +
                        "-fx-letter-spacing: 2px;"
                    );
                    matchStatus.setStyle(
                        "-fx-text-fill: #c07a7a; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
                    );
                    matchStatus.setText("✗  No matching record found — Try uploading a different sketch");
                    simValue.setText("—"); confValue.setText("—");
                    dbNameValue.setText("—");
                    
                    // Add to history
                    MatchHistoryUI.addMatch("sketch.png", "—", 0, "NO MATCH");
                }
            });
            delay.play();
        });

        // ================================================================
        // MAIN LAYOUT
        // ================================================================
        HBox boxRow = new HBox(24, box1, box2, box3);
        boxRow.setAlignment(Pos.TOP_CENTER);
        boxRow.setPadding(new Insets(0, 30, 0, 30));

        // Info strip at bottom
        Label infoStrip = new Label(
            "ℹ  Connect backend API to enable live database matching. " +
            "Double-click the matched result to view full record. Use Export to save results."
        );
        infoStrip.setStyle(
            "-fx-text-fill: #3e4451; -fx-font-size: 11px; -fx-font-family: 'Courier New';"
        );
        infoStrip.setWrapText(true);
        infoStrip.setMaxWidth(700);
        infoStrip.setAlignment(Pos.CENTER);

    

        exportBtn.setOnAction(e -> {
            String filename = "match_report_" + System.currentTimeMillis() + ".txt";
            String filepath = PDFExportService.getDefaultDownloadPath(filename);
            PDFExportService.exportMatchReport(
                "sketch.png",
                lastMatchedRecord[0],
                lastSimilarity[0],
                lastConfidence[0],
                lastMatchStatus[0],
                filepath
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText("Match report exported");
            alert.setContentText("Saved to: " + filepath);
            alert.showAndWait();
        });

        VBox content = new VBox(0, pageTitleBox, boxRow, exportBtn, infoStrip);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(0, 0, 20, 0));
        VBox.setVgrow(boxRow, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #2b303a;");

        return new Scene(root, 900, 620);
    }

    // ── Style helpers — matches DashboardUI palette ──
    private static Button makeTopBtn(String text) {
        Button b = new Button(text);
        String n = "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
                   "-fx-font-size: 11px; -fx-padding: 5 12 5 12;" +
                   "-fx-background-radius: 3; -fx-cursor: hand;" +
                   "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;";
        String h = "-fx-background-color: #4a5260; -fx-text-fill: #ffffff;" +
                   "-fx-font-size: 11px; -fx-padding: 5 12 5 12;" +
                   "-fx-background-radius: 3; -fx-cursor: hand;" +
                   "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;";
        b.setStyle(n);
        b.setOnMouseEntered(e -> b.setStyle(h));
        b.setOnMouseExited(e  -> b.setStyle(n));
        return b;
    }

    private static Button makeActionBtn(String text) {
        Button b = new Button(text);
        String n = "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
                   "-fx-font-size: 11px; -fx-padding: 7 16 7 16;" +
                   "-fx-background-radius: 3; -fx-cursor: hand;" +
                   "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;";
        String h = "-fx-background-color: #4a5260; -fx-text-fill: #ffffff;" +
                   "-fx-font-size: 11px; -fx-padding: 7 16 7 16;" +
                   "-fx-background-radius: 3; -fx-cursor: hand;" +
                   "-fx-border-color: #c8cdd6; -fx-border-width: 1; -fx-border-radius: 3;";
        b.setStyle(n);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle(h));
        b.setOnMouseExited(e  -> b.setStyle(n));
        return b;
    }

    private static Label makeBoxLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 2px;"
        );
        return l;
    }

    private static Label makeStatLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill: #a8b0c0; -fx-font-size: 9px;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 1px;"
        );
        return l;
    }

    private static Label makeStatValue(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );
        return l;
    }

    private static Rectangle makeBar() {
        Rectangle r = new Rectangle(200, 5);
        r.setFill(Color.web("#2b303a"));
        r.setArcWidth(4); r.setArcHeight(4);
        return r;
    }

    private static Rectangle makeBarFill(double width, String color) {
        Rectangle r = new Rectangle(width, 5);
        r.setFill(Color.web(color));
        r.setArcWidth(4); r.setArcHeight(4);
        StackPane.setAlignment(r, Pos.CENTER_LEFT);
        return r;
    }
}