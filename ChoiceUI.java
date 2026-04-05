import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ChoiceUI {

    public static Scene createChoiceScene(Stage stage) {

        // ── Title area ──
        Label title = new Label("SELECT MODE");
        title.setStyle(
            "-fx-font-size: 13px; -fx-text-fill: #666666;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 4px;"
        );

        Label appName = new Label("OCULUS");
        appName.setStyle(
            "-fx-font-size: 34px; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 6px;"
        );

        Rectangle rule = new Rectangle(300, 1);
        rule.setFill(Color.web("#333333"));

        Label sub = new Label("Choose how you want to proceed");
        sub.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-font-family: 'Courier New';");

        VBox header = new VBox(6, title, appName, rule, sub);
        header.setAlignment(Pos.CENTER);

        // ── Card: Make a Sketch ──
        VBox makeCard = buildCard(
            "✎",
            "MAKE A SKETCH",
            "Build a composite face\nfrom scratch using feature\ncomponents and drawing tools.",
            "START SKETCHING"
        );

        // ── Card: Upload a Sketch ──
        VBox uploadCard = buildCard(
            "⬆",
            "UPLOAD A SKETCH",
            "Load an existing image file\nand work with it inside\nthe Oculus editor.",
            "UPLOAD FILE"
        );

        // Actions
        Button makeBtn   = (Button) ((VBox) makeCard.getChildren().get(3)).getChildren().get(0);
        Button uploadBtn = (Button) ((VBox) uploadCard.getChildren().get(3)).getChildren().get(0);

        makeBtn.setOnAction(e -> stage.setScene(DashboardUI.createDashboardScene(stage)));

        uploadBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select a Sketch Image");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
            );
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                // Pass uploaded file path to DashboardUI
                stage.setScene(DashboardUI.createDashboardSceneWithImage(stage, file));
            }
        });

        HBox cards = new HBox(24, makeCard, uploadCard);
        cards.setAlignment(Pos.CENTER);

        VBox centerContent = new VBox(40, header, cards);
        centerContent.setAlignment(Pos.CENTER);

        // ── Background ──
        Canvas bgCanvas = new Canvas(900, 600);
        GraphicsContext gc = bgCanvas.getGraphicsContext2D();
        gc.setFill(Color.web("#0a0a0a"));
        gc.fillRect(0, 0, 900, 600);
        gc.setStroke(Color.web("#141414"));
        gc.setLineWidth(0.5);
        for (int x = 0; x < 900; x += 30) gc.strokeLine(x, 0, x, 600);
        for (int y = 0; y < 600; y += 30) gc.strokeLine(0, y, 900, y);

        StackPane root = new StackPane(bgCanvas, centerContent);
        return new Scene(root, 900, 600);
    }

    private static VBox buildCard(String iconText, String cardTitle, String desc, String btnText) {
        Label icon = new Label(iconText);
        icon.setStyle(
            "-fx-font-size: 32px; -fx-text-fill: #888888;" +
            "-fx-font-family: 'Courier New';"
        );

        Label titleLbl = new Label(cardTitle);
        titleLbl.setStyle(
            "-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 2px;"
        );

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px; -fx-font-family: 'Courier New';");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(200);

        Button btn = new Button(btnText);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(LoginUI.fieldBtnStyle());
        btn.setOnMouseEntered(ev -> btn.setStyle(LoginUI.fieldBtnHover()));
        btn.setOnMouseExited(ev  -> btn.setStyle(LoginUI.fieldBtnStyle()));

        VBox btnBox = new VBox(btn);

        VBox card = new VBox(14, icon, titleLbl, descLbl, btnBox);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(220);
        card.setPadding(new Insets(28));
        card.setStyle(
            "-fx-background-color: #111111;" +
            "-fx-border-color: #2a2a2a; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );

        // Hover lift effect
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #161616;" +
            "-fx-border-color: #555555; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;" +
            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.05), 20, 0, 0, 4);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: #111111;" +
            "-fx-border-color: #2a2a2a; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        ));

        return card;
    }
}