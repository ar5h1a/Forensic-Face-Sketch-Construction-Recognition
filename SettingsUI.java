import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SettingsUI {

    public static Scene createSettingsScene(Stage stage) {
        // ── TOP BAR ──
        HBox topBar = new HBox(8);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("OCULUS - SETTINGS");
        title.setStyle(
            "-fx-text-fill: #c8cdd6; -fx-font-size: 17px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back");
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

        backBtn.setOnAction(e -> stage.setScene(ProfileUI.createProfileScene(stage)));

        // ── MAIN CONTENT ──
        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: #2b303a;");
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        Label pageTitle = new Label("SETTINGS");
        pageTitle.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        // ── APPEARANCE SECTION ──
        VBox appearanceBox = new VBox(12);
        appearanceBox.setPadding(new Insets(20));
        appearanceBox.setStyle(
            "-fx-background-color: #1e232c;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );
        appearanceBox.setMaxWidth(500);

        Label appearanceTitle = new Label("APPEARANCE");
        appearanceTitle.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        HBox themeRow = new HBox(12);
        Label themeLabel = new Label("Theme:");
        themeLabel.setStyle("-fx-text-fill: #c8cdd6; -fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-min-width: 100;");
        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Dark Mode", "Light Mode");
        themeCombo.setValue("Dark Mode");
        themeCombo.setStyle("-fx-font-size: 11px;");
        themeRow.getChildren().addAll(themeLabel, themeCombo);

        HBox fontRow = new HBox(12);
        Label fontLabel = new Label("Font Size:");
        fontLabel.setStyle("-fx-text-fill: #c8cdd6; -fx-font-size: 11px; -fx-font-family: 'Courier New'; -fx-min-width: 100;");
        Spinner<Integer> fontSpinner = new Spinner<>(10, 16, 12);
        fontSpinner.setStyle("-fx-font-size: 11px;");
        fontRow.getChildren().addAll(fontLabel, fontSpinner);

        appearanceBox.getChildren().addAll(appearanceTitle, themeRow, fontRow);

        // ── NOTIFICATIONS SECTION ──
        VBox notifBox = new VBox(12);
        notifBox.setPadding(new Insets(20));
        notifBox.setStyle(
            "-fx-background-color: #1e232c;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );
        notifBox.setMaxWidth(500);

        Label notifTitle = new Label("NOTIFICATIONS");
        notifTitle.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        CheckBox notifCheck = new CheckBox("Enable in-app notifications");
        notifCheck.setSelected(true);
        notifCheck.setStyle("-fx-text-fill: #c8cdd6; -fx-font-size: 11px; -fx-font-family: 'Courier New';");

        CheckBox emailCheck = new CheckBox("Enable email notifications");
        emailCheck.setSelected(false);
        emailCheck.setStyle("-fx-text-fill: #c8cdd6; -fx-font-size: 11px; -fx-font-family: 'Courier New';");

        notifBox.getChildren().addAll(notifTitle, notifCheck, emailCheck);

        // ── BUTTONS ──
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(500);

        Button saveBtn = new Button("✓ Save Settings");
        saveBtn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 8 20 8 20;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        );

        Button resetBtn = new Button("↻ Reset");
        resetBtn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 8 20 8 20;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        );

        saveBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Settings Saved");
            alert.setContentText("Your settings have been saved!");
            alert.showAndWait();
        });

        resetBtn.setOnAction(e -> {
            themeCombo.setValue("Dark Mode");
            fontSpinner.getValueFactory().setValue(12);
            notifCheck.setSelected(true);
            emailCheck.setSelected(false);
        });

        buttonBox.getChildren().addAll(saveBtn, resetBtn);

        content.getChildren().addAll(pageTitle, appearanceBox, notifBox, buttonBox);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #2b303a;");

        return new Scene(root, 900, 680);
    }
}