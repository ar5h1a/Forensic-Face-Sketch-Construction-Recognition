import javafx.scene.layout.TilePane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;
import java.io.File;
import java.util.Arrays;

public class FeatureLoader {

    public static void loadFeatures(TilePane panel, Pane canvas, String type) {
        panel.getChildren().clear();

        String folderPath = "resources/features/" + type.toLowerCase();
        File folder = new File(folderPath);

        if (!folder.exists()) {
            System.out.println("Folder not found: " + folderPath);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> {
            String l = name.toLowerCase();
            return l.endsWith(".png") || l.endsWith(".jpg") || l.endsWith(".jpeg");
        });

        if (files == null || files.length == 0) {
            System.out.println("No images in: " + folderPath);
            return;
        }

        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        for (File file : files) {
            Image image = new Image(file.toURI().toString());

            ImageView imgView = new ImageView(image);
            imgView.setFitWidth(72);
            imgView.setFitHeight(72);
            imgView.setPreserveRatio(true);

            StackPane tile = new StackPane(imgView);
            tile.setPrefSize(82, 82);
            tile.setStyle(normalStyle());
            tile.setCursor(Cursor.HAND);

            tile.setOnMouseEntered(e -> tile.setStyle(hoverStyle()));
            tile.setOnMouseExited(e  -> tile.setStyle(normalStyle()));

            tile.setOnMouseClicked(e ->
                CanvasManager.addImageFeature(canvas, image, type)
            );

            panel.getChildren().add(tile);
        }
    }

    private static String normalStyle() {
        return "-fx-background-color: #1e1e1e;" +
               "-fx-border-color: #333333;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 4;" +
               "-fx-background-radius: 4;";
    }

    private static String hoverStyle() {
        // Neutral light grey hover — no yellow
        return "-fx-background-color: #3a3a3a;" +
               "-fx-border-color: #888888;" +
               "-fx-border-width: 1;" +
               "-fx-border-radius: 4;" +
               "-fx-background-radius: 4;";
    }
}