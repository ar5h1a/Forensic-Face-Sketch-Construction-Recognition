import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SplashScreen {

    public static Scene createSplash(Stage stage) {

        // Subtle dot-grid background
        Canvas grid = new Canvas(900, 600);
        GraphicsContext gc = grid.getGraphicsContext2D();
        gc.setFill(Color.web("#0e1318"));
        gc.fillRect(0, 0, 900, 600);
        gc.setFill(Color.web("#2a2a2a", 0.8));
        for (int x = 30; x < 900; x += 28)
            for (int y = 30; y < 600; y += 28)
                gc.fillOval(x, y, 1.5, 1.5);

        // Version label
        Label build = new Label("v1.0");
        build.setStyle(
            "-fx-font-size: 9px; -fx-text-fill: #3a3a3a;" +
            "-fx-font-family: 'Courier New';"
        );
        build.setOpacity(0);

        // ✅ LOGO (bigger)
        ImageView logo = new ImageView(new Image("file:resources/logo_full.png"));
        logo.setFitWidth(320);   // 🔥 increased size
        logo.setPreserveRatio(true);
        logo.setOpacity(0);

        // Center layout
        VBox center = new VBox(logo);
        center.setAlignment(Pos.CENTER);

        // Root
        StackPane.setAlignment(build, Pos.BOTTOM_RIGHT);
        StackPane root = new StackPane(grid, center, build);
        StackPane.setMargin(build, new javafx.geometry.Insets(0, 20, 14, 0));
        root.setStyle("-fx-background-color: #0e1318;");

        // ───── Animations ─────

        // Logo fade-in
        FadeTransition fLogo = fade(logo, 1200);

        // Slight zoom effect (premium feel)
        ScaleTransition scale = new ScaleTransition(Duration.millis(1200), logo);
        scale.setFromX(0.85);
        scale.setFromY(0.85);
        scale.setToX(1);
        scale.setToY(1);

        // Build label fade
        FadeTransition fBuild = fade(build, 600);

        // Sequence
        PauseTransition p1 = pause(300, () -> {
            fLogo.play();
            scale.play();
        });

        PauseTransition p2 = pause(900, () -> fBuild.play());

        // Exit
        PauseTransition leave = pause(3200, () -> {
            FadeTransition out = new FadeTransition(Duration.millis(600), root);
            out.setFromValue(1);
            out.setToValue(0);
            out.setOnFinished(e -> stage.setScene(LoginUI.createLoginScene(stage)));
            out.play();
        });

        // Play
        p1.play();
        p2.play();
        leave.play();

        return new Scene(root, 900, 600);
    }

    private static FadeTransition fade(javafx.scene.Node node, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), node);
        ft.setFromValue(0); ft.setToValue(1);
        return ft;
    }

    private static PauseTransition pause(int ms, Runnable action) {
        PauseTransition p = new PauseTransition(Duration.millis(ms));
        p.setOnFinished(e -> action.run());
        return p;
    }
}