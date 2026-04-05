import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Forensic Sketch System");
        stage.setScene(SplashScreen.createSplash(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}