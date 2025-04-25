package com.hsr.pity;

import com.hsr.pity.capture.ScreenCapturer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.awt.image.BufferedImage;

public class App extends Application {
    private OverlayController overlayController;
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    @Override

    public void start(Stage primaryStage) throws Exception {
//        logger.info("Starting application");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/overlay.fxml"));
        Parent root = loader.load();
        overlayController = loader.getController();

        Scene scene = new Scene(root, 300, 200);
        scene.setFill(null);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        new Thread(this::startCaptureLoop).start();
        primaryStage.show();
    }

    private void startCaptureLoop() {
        ScreenCapturer capturer = new ScreenCapturer(0, 0, 1920, 1080);
        while (true) {
            try {
                BufferedImage image = capturer.capture();
                int pityCount = ImageProcessor.processPityCounter(image);
                Platform.runLater(() -> overlayController.updatePityCounter(pityCount));
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) { launch(args); }
}