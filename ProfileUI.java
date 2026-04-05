import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProfileUI {

    private static String currentUsername = "admin";
    private static String currentPassword = "1234";  // For demo only

    public static void setCurrentUser(String username) {
        currentUsername = username;
    }

    public static Scene createProfileScene(Stage stage) {
        // ── TOP BAR ──
        HBox topBar = new HBox(8);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("OCULUS - PROFILE");
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
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        // Title
        Label pageTitle = new Label("MY PROFILE");
        pageTitle.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        // Profile Box
        VBox profileBox = new VBox(12);
        profileBox.setPadding(new Insets(20));
        profileBox.setStyle(
            "-fx-background-color: #1e232c;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );
        profileBox.setMaxWidth(500);

        // Username
        HBox usernameRow = createInfoRow("USERNAME:", currentUsername);
        
        // Email
        HBox emailRow = createInfoRow("EMAIL:", currentUsername + "@oculus.local");
        
        // Member Since
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        HBox dateRow = createInfoRow("MEMBER SINCE:", LocalDateTime.now().format(formatter));
        
        // Stats
        HBox statsTitle = new HBox();
        Label statsTitleLabel = new Label("STATISTICS");
        statsTitleLabel.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 2px;"
        );
        statsTitle.getChildren().add(statsTitleLabel);

        HBox stat1 = createStatRow("Total Sketches:", "5", "#7aab7a");
        HBox stat2 = createStatRow("Total Matches:", "3", "#7a9aab");
        HBox stat3 = createStatRow("Avg Confidence:", "94.5%", "#abadb7");

        profileBox.getChildren().addAll(
            usernameRow, emailRow, dateRow,
            new Separator(),
            statsTitle,
            stat1, stat2, stat3
        );

        // Buttons
        VBox buttonBox = new VBox(8);
        buttonBox.setMaxWidth(500);

        Button changePassBtn = createButton("🔐 Change Password");
        Button logoutBtn = createButton("↪ Logout");

        buttonBox.getChildren().addAll(changePassBtn, logoutBtn);

        changePassBtn.setOnAction(e -> showChangePasswordDialog(stage));

        logoutBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Logout");
            confirm.setHeaderText("Confirm Logout");
            confirm.setContentText("Are you sure you want to logout?");
            if (confirm.showAndWait().get() == ButtonType.OK) {
                stage.setScene(LoginUI.createLoginScene(stage));
            }
        });

        content.getChildren().addAll(pageTitle, profileBox, buttonBox);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #2b303a;");

        return new Scene(root, 900, 680);
    }

    private static HBox createInfoRow(String label, String value) {
        Label labelLbl = new Label(label);
        labelLbl.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-min-width: 120;"
        );

        Label valueLbl = new Label(value);
        valueLbl.setStyle(
            "-fx-text-fill: #e8edff; -fx-font-size: 12px;" +
            "-fx-font-family: 'Courier New';"
        );

        HBox row = new HBox(12);
        row.getChildren().addAll(labelLbl, valueLbl);
        return row;
    }

    private static HBox createStatRow(String label, String value, String color) {
        Label labelLbl = new Label(label);
        labelLbl.setStyle(
            "-fx-text-fill: #8a9bb0; -fx-font-size: 11px;" +
            "-fx-font-family: 'Courier New'; -fx-min-width: 120;"
        );

        Label valueLbl = new Label(value);
        valueLbl.setStyle(
            "-fx-text-fill: " + color + "; -fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );

        HBox row = new HBox(12);
        row.getChildren().addAll(labelLbl, valueLbl);
        return row;
    }

    private static Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 8 16 8 16;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        );
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: #4a5260; -fx-text-fill: #ffffff;" +
            "-fx-font-size: 11px; -fx-padding: 8 16 8 16;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #8a9bb0; -fx-border-width: 1; -fx-border-radius: 3;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: #333a47; -fx-text-fill: #c8cdd6;" +
            "-fx-font-size: 11px; -fx-padding: 8 16 8 16;" +
            "-fx-background-radius: 3; -fx-cursor: hand;" +
            "-fx-border-color: #3e4451; -fx-border-width: 1; -fx-border-radius: 3;"
        ));
        return btn;
    }

    private static void showChangePasswordDialog(Stage stage) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update Your Password");

        DialogPane pane = dialog.getDialogPane();
        pane.setStyle("-fx-background-color: #2b303a;");

        PasswordField oldPassField = new PasswordField();
        oldPassField.setPromptText("Current password");
        oldPassField.setStyle(inputStyle());

        PasswordField newPassField = new PasswordField();
        newPassField.setPromptText("New password (min 6 chars)");
        newPassField.setStyle(inputStyle());

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm new password");
        confirmPassField.setStyle(inputStyle());

        Label messageLabel = new Label("");
        messageLabel.setStyle("-fx-text-fill: #ee6666; -fx-font-size: 10px;");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().addAll(
            new Label("Current Password:"),
            oldPassField,
            new Label("New Password:"),
            newPassField,
            new Label("Confirm Password:"),
            confirmPassField,
            messageLabel
        );

        // Style labels
        for (var node : content.getChildren()) {
            if (node instanceof Label && !(node == messageLabel)) {
                ((Label) node).setStyle("-fx-text-fill: #c8cdd6; -fx-font-size: 10px; -fx-font-family: 'Courier New';");
            }
        }

        pane.setContent(content);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String oldPass = oldPassField.getText();
                String newPass = newPassField.getText();
                String confirmPass = confirmPassField.getText();

                // Validate old password
                if (!oldPass.equals(currentPassword)) {
                    messageLabel.setText("❌ Current password is incorrect!");
                    return false;
                }

                // Validate new password length
                if (newPass.length() < 6) {
                    messageLabel.setText("❌ New password must be at least 6 characters!");
                    return false;
                }

                // Validate passwords match
                if (!newPass.equals(confirmPass)) {
                    messageLabel.setText("❌ Passwords don't match!");
                    return false;
                }

                // All valid - update password
                currentPassword = newPass;
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Password Changed");
                success.setContentText("Your password has been updated successfully!");
                success.showAndWait();
                
                return true;
            }
            return false;
        });

        dialog.showAndWait();
    }

    private static String inputStyle() {
        return "-fx-background-color: #1a1a1a; -fx-text-fill: #eeeeee;" +
               "-fx-prompt-text-fill: #555555; -fx-font-family: 'Courier New';" +
               "-fx-font-size: 12px; -fx-padding: 8 10 8 10;" +
               "-fx-border-color: #3a3a3a; -fx-border-width: 1;" +
               "-fx-background-radius: 3; -fx-border-radius: 3;";
    }
}