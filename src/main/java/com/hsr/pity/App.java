package com.hsr.pity;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting application initialization");

            // Загрузка FXML с абсолютным путем
            URL fxmlUrl = getClass().getResource("/com/hsr/pity/overlay.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("FXML file not found");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Настройка прозрачной сцены
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // Настройка прозрачного окна
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            primaryStage.setScene(scene);

            // Проверка содержимого
            if (root.getChildrenUnmodifiable().isEmpty()) {
                logger.warn("Root container is empty!");
            }

            primaryStage.show();
            logger.info("Application window shown successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}