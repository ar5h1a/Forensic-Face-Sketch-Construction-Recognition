import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MatchHistoryUI {

    public static Scene createMatchHistoryScene(Stage stage) {
        // ── TOP BAR ──
        HBox topBar = new HBox(8);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("OCULUS - MATCH HISTORY");
        title.setStyle(
            "-fx-text-fill: #c8cdd6; -fx-font-size: 17px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 5 12 5 12;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        );

        topBar.getChildren().addAll(title, topSpacer, backBtn);
        topBar.setStyle(
            "-fx-background-color: #222830;" +
            "-fx-padding: 9 16 9 16;" +
            "-fx-border-color: #3e4451; -fx-border-width: 0 0 1 0;"
        );

        backBtn.setOnAction(e -> stage.setScene(DashboardUI.createDashboardScene(stage)));

        // ── MAIN CONTENT ──
        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: #2b303a;");
        content.setPadding(new Insets(20));

        Label pageTitle = new Label("MATCH HISTORY");
        pageTitle.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        // ── STATISTICS ──
        HBox statsBox = new HBox(20);
        statsBox.setStyle("-fx-background-color: #1e232c;");
        statsBox.setPadding(new Insets(16));

        VBox stat1 = createStatBox("Total Matches", "0", "#7aab7a");
        VBox stat2 = createStatBox("Matches Found", "0", "#7a9aab");
        VBox stat3 = createStatBox("No Matches", "0", "#c07a7a");
        VBox stat4 = createStatBox("Avg Confidence", "0%", "#abadb7");

        statsBox.getChildren().addAll(stat1, stat2, stat3, stat4);

        // ── TABLE ──
        TableView<String> table = new TableView<>();
        table.setStyle("-fx-font-size: 11px; -fx-font-family: 'Courier New';");
        table.setPrefHeight(300);

        // Add sample data
        table.getItems().addAll(
            "sketch_001 | suspect_id_123 | 95.2% | MATCH FOUND | 2025-01-15 10:30",
            "sketch_002 | suspect_id_456 | 88.7% | MATCH FOUND | 2025-01-15 11:00",
            "sketch_003 | — | — | NO MATCH | 2025-01-15 11:45",
            "sketch_004 | suspect_id_789 | 92.1% | MATCH FOUND | 2025-01-15 12:30"
        );

        // ── BUTTONS ──
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button exportBtn = new Button("📥 Export CSV");
        exportBtn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 7 14 7 14;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        );

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 7 14 7 14;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        );

        exportBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export");
            alert.setHeaderText("Match History Exported");
            alert.setContentText("File saved to: Downloads/match_history.csv");
            alert.showAndWait();
        });

        refreshBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Refresh");
            alert.setHeaderText("Data Refreshed");
            alert.setContentText("Match history updated!");
            alert.showAndWait();
        });

        buttonBox.getChildren().addAll(exportBtn, refreshBtn);

        content.getChildren().addAll(pageTitle, statsBox, new Label(""), table, buttonBox);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #2b303a;");

        return new Scene(root, 1000, 700);
    }

    private static VBox createStatBox(String label, String value, String color) {
        Label labelLbl = new Label(label);
        labelLbl.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 10px;" +
            "-fx-font-family: 'Courier New';"
        );

        Label valueLbl = new Label(value);
        valueLbl.setStyle(
            "-fx-text-fill: " + color + "; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        VBox box = new VBox(4, labelLbl, valueLbl);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPrefWidth(150);
        return box;
    }
    public static void addMatch(String sketch, String result, double similarity, String status) {
        System.out.println("Saved Match:");
        System.out.println("Sketch: " + sketch);
        System.out.println("Result: " + result);
        System.out.println("Similarity: " + similarity);
        System.out.println("Status: " + status);
    }
}