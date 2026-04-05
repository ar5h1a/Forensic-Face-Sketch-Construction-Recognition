import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LoginUI {

    public static Scene createLoginScene(Stage stage) {

        // ── Left branding ──
        VBox logoBox = new VBox(14);
        logoBox.setAlignment(Pos.CENTER);

        // Try to load actual logo, fallback to text
        ImageView logoImage = new ImageView();
        try {
            Image logo = new Image("file:resources/logo.png");
            logoImage.setImage(logo);
            logoImage.setFitWidth(280);
            logoImage.setFitHeight(120);
            logoImage.setPreserveRatio(true);
            logoBox.getChildren().add(logoImage);
        } catch (Exception e) {
            // Fallback: use text logo
            Label logo = new Label("");
            logo.setStyle(
                "-fx-font-size: 56px; -fx-text-fill: white;" +
                "-fx-font-family: 'Courier New'; -fx-font-weight: bold;"
            );
            logoBox.getChildren().add(logo);
        }

        Label appName = new Label("OCULUS");
        appName.setStyle(
            "-fx-text-fill: white; -fx-font-size: 30px;" +
            "-fx-font-weight: bold; -fx-font-family: 'Courier New';"
        );

        Rectangle rule = new Rectangle(180, 1);
        rule.setFill(Color.web("#666666"));

        Label tagline = new Label("FORENSIC SKETCH SYSTEM");
        tagline.setStyle(
            "-fx-text-fill: #aaaaaa; -fx-font-size: 11px;" +
            "-fx-font-family: 'Courier New';"
        );
        Label desc = new Label("Construct and identify suspect\nfacial composites with precision.");
        desc.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px; -fx-font-family: 'Courier New';");

        logoBox.getChildren().addAll(appName, rule, tagline, desc);

        VBox leftBox = new VBox(14, logoBox);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 70));

        // ── Right login box ──
        Label title = new Label("SIGN IN");
        title.setStyle(
            "-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );
        Label subtitleText = new Label("Enter your credentials to continue");
        subtitleText.setStyle(
            "-fx-text-fill: #bbbbbb; -fx-font-size: 12px; -fx-font-family: 'Courier New';"
        );

        TextField username    = styledField("Username");
        PasswordField password = styledPassField("Password");

        Button loginBtn = new Button("SIGN IN");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle(fieldBtnStyle());
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(fieldBtnHover()));
        loginBtn.setOnMouseExited(e  -> loginBtn.setStyle(fieldBtnStyle()));

        Label message = new Label();
        message.setStyle("-fx-text-fill: #ee6666; -fx-font-size: 11px; -fx-font-family: 'Courier New';");

        Label signupText = new Label("No account?");
        signupText.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px; -fx-font-family: 'Courier New';");
        Button signupBtn = new Button("Create one →");
        signupBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #dddddd;" +
            "-fx-underline: true; -fx-font-family: 'Courier New'; -fx-font-size: 11px;" +
            "-fx-cursor: hand; -fx-padding: 0;"
        );

        HBox signupRow = new HBox(6, signupText, signupBtn);
        signupRow.setAlignment(Pos.CENTER_LEFT);

        loginBtn.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = password.getText();
            if (user.isEmpty() || pass.isEmpty()) {
                message.setText("All fields required."); return;
            }
            if (user.equals("admin") && pass.equals("1234")) {
                // Set logged in user for ProfileUI
                ProfileUI.setCurrentUser(user);
                stage.setScene(ChoiceUI.createChoiceScene(stage));
            } else {
                message.setText("Invalid credentials.");
            }
        });
        signupBtn.setOnAction(e -> stage.setScene(SignupUI.createSignupScene(stage)));

        VBox loginBox = new VBox(12,
            title, subtitleText, new Label(""),
            styledLabel("USERNAME"), username,
            styledLabel("PASSWORD"), password,
            loginBtn, message, signupRow
        );
        loginBox.setAlignment(Pos.CENTER_LEFT);
        loginBox.setMaxWidth(310);
        loginBox.setPadding(new Insets(32));
        loginBox.setStyle(
            "-fx-background-color: rgba(0,0,0,0.75);" +
            "-fx-border-color: #444444; -fx-border-width: 1;" +
            "-fx-background-radius: 6; -fx-border-radius: 6;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox content = new HBox(leftBox, spacer, loginBox);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(0, 70, 0, 0));

        // ── Background — more visible, lighter blur ──
        Image bg = new Image("file:resources/bg.jpg");
        ImageView bgView = new ImageView(bg);
        bgView.setEffect(new GaussianBlur(5));
        bgView.setFitWidth(900); bgView.setFitHeight(600);
        bgView.setOpacity(0.65);

        // lighter overlay so bg shows through
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        overlay.setPrefSize(900, 600);

        StackPane root = new StackPane(bgView, overlay, content);
        return new Scene(root, 900, 600);
    }

    // ── Shared helpers used by SignupUI too ──
    static TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setMaxWidth(Double.MAX_VALUE);
        f.setStyle(inputStyle());
        f.focusedProperty().addListener((obs, o, n) ->
            f.setStyle(n ? inputFocused() : inputStyle()));
        return f;
    }

    static PasswordField styledPassField(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt);
        f.setMaxWidth(Double.MAX_VALUE);
        f.setStyle(inputStyle());
        f.focusedProperty().addListener((obs, o, n) ->
            f.setStyle(n ? inputFocused() : inputStyle()));
        return f;
    }

    static Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill: #aaaaaa; -fx-font-size: 10px;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 1px;"
        );
        return l;
    }

    private static String inputStyle() {
        return "-fx-background-color: #1a1a1a; -fx-text-fill: #eeeeee;" +
               "-fx-prompt-text-fill: #555555; -fx-font-family: 'Courier New';" +
               "-fx-font-size: 13px; -fx-padding: 9 12 9 12;" +
               "-fx-border-color: #3a3a3a; -fx-border-width: 1;" +
               "-fx-background-radius: 3; -fx-border-radius: 3;";
    }

    private static String inputFocused() {
        return "-fx-background-color: #1a1a1a; -fx-text-fill: #eeeeee;" +
               "-fx-prompt-text-fill: #555555; -fx-font-family: 'Courier New';" +
               "-fx-font-size: 13px; -fx-padding: 9 12 9 12;" +
               "-fx-border-color: #aaaaaa; -fx-border-width: 1;" +
               "-fx-background-radius: 3; -fx-border-radius: 3;";
    }

    static String fieldBtnStyle() {
        return "-fx-background-color: #252525; -fx-text-fill: #eeeeee;" +
               "-fx-font-family: 'Courier New'; -fx-font-size: 13px;" +
               "-fx-font-weight: bold; -fx-padding: 10 0 10 0;" +
               "-fx-background-radius: 3; -fx-border-color: #555555;" +
               "-fx-border-width: 1; -fx-border-radius: 3; -fx-cursor: hand;";
    }

    static String fieldBtnHover() {
        return "-fx-background-color: #3a3a3a; -fx-text-fill: white;" +
               "-fx-font-family: 'Courier New'; -fx-font-size: 13px;" +
               "-fx-font-weight: bold; -fx-padding: 10 0 10 0;" +
               "-fx-background-radius: 3; -fx-border-color: #aaaaaa;" +
               "-fx-border-width: 1; -fx-border-radius: 3; -fx-cursor: hand;";
    }
}