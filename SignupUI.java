import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class SignupUI{

    
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    
    private static final String SENDER_EMAIL = "your_email@gmail.com"; // ← CHANGE THIS
    private static final String SENDER_PASS  = "xxxx xxxx xxxx xxxx";   // ← CHANGE THIS (16-char app password)

    public static Scene createSignupScene(Stage stage) {

        TextField email       = LoginUI.styledField("e.g. name@domain.com");
        TextField username    = LoginUI.styledField("Min 4 characters");
        PasswordField password = LoginUI.styledPassField("Min 6 characters");

        Button registerBtn = new Button("CREATE ACCOUNT");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle(LoginUI.fieldBtnStyle());
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(LoginUI.fieldBtnHover()));
        registerBtn.setOnMouseExited(e  -> registerBtn.setStyle(LoginUI.fieldBtnStyle()));

        Button backBtn = new Button("← Back to Login");
        backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #888888;" +
            "-fx-underline: true; -fx-font-family: 'Courier New'; -fx-font-size: 10px;" +
            "-fx-cursor: hand; -fx-padding: 0;"
        );

        Label message = new Label();
        message.setWrapText(true);
        message.setMaxWidth(260);
        message.setStyle("-fx-text-fill: #cc4444; -fx-font-size: 10px; -fx-font-family: 'Courier New';");

        registerBtn.setOnAction(e -> {
            String userEmail = email.getText().trim();
            String userName  = username.getText().trim();
            String userPass  = password.getText();

            // 1. Empty check
            if (userEmail.isEmpty() || userName.isEmpty() || userPass.isEmpty()) {
                setMsg(message, "❌ All fields are required.", false);
                return;
            }

            // 2. Email validation
            String emailErr = validateEmail(userEmail);
            if (emailErr != null) { setMsg(message, emailErr, false); return; }

            // 3. Username validation
            if (userName.length() < 4) {
                setMsg(message, "❌ Username must be at least 4 characters.", false); return;
            }
            if (!userName.matches("[A-Za-z0-9_]+")) {
                setMsg(message, "❌ Username: only letters, numbers, underscores.", false); return;
            }

            // 4. Password validation
            if (userPass.length() < 6) {
                setMsg(message, "❌ Password must be at least 6 characters.", false); return;
            }
            if (!userPass.matches(".*[A-Za-z].*")) {
                setMsg(message, "❌ Password must contain at least one letter.", false); return;
            }
            if (!userPass.matches(".*[0-9].*")) {
                setMsg(message, "❌ Password must contain at least one number.", false); return;
            }

            // All validation passed
            setMsg(message, "✓ Creating account... Sending email...", true);

             // ✅ STEP 1: SAVE USER TO BACKEND (DB)
    new Thread(() -> {
        try {
            String url = "http://localhost:8080/api/auth/signup";

            java.net.URL apiUrl = new java.net.URL(url);
            java.net.HttpURLConnection conn =
                    (java.net.HttpURLConnection) apiUrl.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonInput = "{ \"username\": \"" + userName + "\", \"password\": \"" + userPass + "\" }";

            java.io.OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String res = response.toString();
            System.out.println("Signup Response: " + res);

            // ✅ STEP 2: AFTER DB SAVE → SEND EMAIL
            boolean emailSent = sendSmtpEmail(userEmail, userName, userPass);

            javafx.application.Platform.runLater(() -> {
                if (res.toLowerCase().contains("created")) {
                    setMsg(message, "✓ Account created! You can login now.", true);

                    email.clear();
                    username.clear();
                    password.clear();
                } else {
                    setMsg(message, "⚠️ User already exists.", false);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            javafx.application.Platform.runLater(() ->
                    setMsg(message, "❌ Server error during signup.", false)
            );
        }
    }).start();
});

        backBtn.setOnAction(e -> stage.setScene(LoginUI.createLoginScene(stage)));

        Label title = new Label("CREATE ACCOUNT");
        title.setStyle(
            "-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New'; -fx-letter-spacing: 3px;"
        );
        Label subtitle = new Label("Fill in the details below");
        subtitle.setStyle(
            "-fx-text-fill: #555555; -fx-font-size: 10px; -fx-font-family: 'Courier New';"
        );

        VBox formBox = new VBox(10,
            title, subtitle, new Label(""),
            LoginUI.styledLabel("EMAIL ADDRESS"), email,
            LoginUI.styledLabel("USERNAME"),      username,
            LoginUI.styledLabel("PASSWORD"),      password,
            registerBtn, message, backBtn
        );
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);
        formBox.setPadding(new Insets(32));
        formBox.setStyle(
            "-fx-background-color: rgba(10,10,10,0.82);" +
            "-fx-border-color: #2a2a2a; -fx-border-width: 1;" +
            "-fx-background-radius: 4; -fx-border-radius: 4;"
        );

        ImageView logo = new ImageView(new Image("file:resources/logo.png"));
        logo.setFitWidth(120);
        logo.setPreserveRatio(true);
            
        Label appName = new Label("OCULUS");
        appName.setStyle(
            "-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;" +
            "-fx-font-family: 'Courier New';"
        );
        Label tagline = new Label("FORENSIC SKETCH SYSTEM");
        tagline.setStyle(
            "-fx-text-fill: #666666; -fx-font-size: 10px; -fx-font-family: 'Courier New';"
        );

        VBox leftBox = new VBox(14, logo, appName, tagline);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(0, 0, 0, 70));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox content = new HBox(leftBox, spacer, formBox);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(0, 70, 0, 0));

        Image bg = new Image("file:resources/bg.jpg");
        ImageView bgView = new ImageView(bg);
        bgView.setEffect(new GaussianBlur(14));
        bgView.setFitWidth(900); bgView.setFitHeight(600);
        bgView.setOpacity(0.35);

        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: #0a0a0a;");
        overlay.setPrefSize(900, 600);
        overlay.setOpacity(0.7);

        StackPane root = new StackPane(bgView, overlay, content);
        return new Scene(root, 900, 600);
    }

    // ================================================================
    // EMAIL VALIDATION
    // ================================================================
    static String validateEmail(String email) {
        if (email == null || email.isEmpty())
            return "❌ Email cannot be empty.";
        if (email.contains(" "))
            return "❌ Email cannot contain spaces.";
        long atCount = email.chars().filter(c -> c == '@').count();
        if (atCount == 0) return "❌ Email must contain '@'.";
        if (atCount > 1)  return "❌ Email must contain exactly one '@'.";

        int atIndex   = email.indexOf('@');
        String local  = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        if (local.isEmpty())
            return "❌ Text required before '@'.";
        if (!local.matches("[A-Za-z0-9+_.%-]+"))
            return "❌ Invalid character before '@'.";
        if (local.startsWith(".") || local.endsWith("."))
            return "❌ Cannot start/end with dot.";
        if (local.contains(".."))
            return "❌ Consecutive dots not allowed.";
        if (local.matches("[0-9]+"))
            return "❌ Cannot be only numbers.";

        if (domain.isEmpty())
            return "❌ Domain missing after '@'.";
        if (!domain.contains("."))
            return "❌ Domain must contain a dot.";
        if (domain.startsWith(".") || domain.endsWith("."))
            return "❌ Domain cannot start/end with dot.";
        if (domain.contains(".."))
            return "❌ Consecutive dots in domain.";

        String[] domainParts = domain.split("\\.");
        for (String part : domainParts) {
            if (part.isEmpty())
                return "❌ Invalid domain segment.";
            if (!part.matches("[A-Za-z0-9-]+"))
                return "❌ Invalid domain characters.";
            if (part.startsWith("-") || part.endsWith("-"))
                return "❌ Segment cannot start/end with hyphen.";
        }

        String tld = domainParts[domainParts.length - 1];
        if (!tld.matches("[A-Za-z]{2,6}"))
            return "❌ TLD must be 2–6 letters.";

        return null; // valid
    }

    // ================================================================
    // SMTP EMAIL SENDING
    // ================================================================
    private static boolean sendSmtpEmail(String toEmail, String userName, String userPass) {
        // Check if credentials are configured
        if (SENDER_EMAIL.startsWith("your_")) {
            System.out.println("⚠️ Email not configured. Please set SENDER_EMAIL and SENDER_PASS in SignupUI_FIXED.java");
            return false;
        }

        try {
            System.out.println("Connecting to " + SMTP_HOST + ":" + SMTP_PORT);
            Socket sock = new Socket(SMTP_HOST, SMTP_PORT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()), true);

            // Read greeting
            String response = reader.readLine();
            System.out.println("Server: " + response);

            // EHLO
            writer.println("EHLO localhost");
            while ((response = reader.readLine()) != null && response.length() > 3 && response.charAt(3) == '-') {
                System.out.println("Server: " + response);
            }
            System.out.println("Server: " + response);

            // STARTTLS
            writer.println("STARTTLS");
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Upgrade to SSL
            javax.net.ssl.SSLSocketFactory sf = (javax.net.ssl.SSLSocketFactory) javax.net.ssl.SSLSocketFactory.getDefault();
            javax.net.ssl.SSLSocket ssl = (javax.net.ssl.SSLSocket) sf.createSocket(sock, SMTP_HOST, SMTP_PORT, true);
            ssl.startHandshake();

            BufferedReader sslReader = new BufferedReader(new InputStreamReader(ssl.getInputStream()));
            PrintWriter sslWriter = new PrintWriter(new OutputStreamWriter(ssl.getOutputStream()), true);

            // EHLO after TLS
            sslWriter.println("EHLO localhost");
            while ((response = sslReader.readLine()) != null && response.length() > 3 && response.charAt(3) == '-') {
                System.out.println("Server: " + response);
            }
            System.out.println("Server: " + response);

            // AUTH LOGIN
            sslWriter.println("AUTH LOGIN");
            response = sslReader.readLine();
            System.out.println("Server: " + response);

            sslWriter.println(Base64.getEncoder().encodeToString(SENDER_EMAIL.getBytes()));
            response = sslReader.readLine();
            System.out.println("Server: " + response);

            sslWriter.println(Base64.getEncoder().encodeToString(SENDER_PASS.getBytes()));
            response = sslReader.readLine();
            System.out.println("Server: " + response);

            if (!response.startsWith("235")) {
                System.out.println("❌ Authentication failed!");
                ssl.close();
                sock.close();
                return false;
            }

            System.out.println("✓ Authenticated");

            // MAIL FROM
            sslWriter.println("MAIL FROM:<" + SENDER_EMAIL + ">");
            response = sslReader.readLine();
            System.out.println("Server: " + response);

            // RCPT TO
            sslWriter.println("RCPT TO:<" + toEmail + ">");
            response = sslReader.readLine();
            System.out.println("Server: " + response);

            // DATA
            sslWriter.println("DATA");
            response = sslReader.readLine();
            System.out.println("Server: " + response);

            // Email headers and body
            sslWriter.println("From: OCULUS <" + SENDER_EMAIL + ">");
            sslWriter.println("To: " + toEmail);
            sslWriter.println("Subject: Welcome to OCULUS - Account Created!");
            sslWriter.println("MIME-Version: 1.0");
            sslWriter.println("Content-Type: text/plain; charset=UTF-8");
            sslWriter.println();
            sslWriter.println("Hello " + userName + ",");
            sslWriter.println();
            sslWriter.println("Your OCULUS account has been successfully created!");
            sslWriter.println();
            sslWriter.println("Account Details:");
            sslWriter.println("  Email: " + toEmail);
            sslWriter.println("  Username: " + userName);
            sslWriter.println();
            sslWriter.println("You can now login and start creating forensic sketches.");
            sslWriter.println();
            sslWriter.println("— The OCULUS Forensic System Team");
            sslWriter.println(".");

            response = sslReader.readLine();
            System.out.println("Server: " + response);

            // QUIT
            sslWriter.println("QUIT");
            ssl.close();
            sock.close();

            System.out.println("✓ Email sent successfully!");
            return true;

        } catch (Exception ex) {
            System.err.println("❌ Email sending failed: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private static void setMsg(Label lbl, String msg, boolean success) {
        lbl.setStyle(
            (success ? "-fx-text-fill: #77cc77;" : "-fx-text-fill: #cc4444;") +
            " -fx-font-size: 10px; -fx-font-family: 'Courier New';"
        );
        lbl.setText(msg);
    }
}