package com.hsr.pity;

import com.hsr.pity.capture.ScreenCapturer;
import com.hsr.pity.database.BannerHistoryDao;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App extends Application {  // Исправлено: AppLication → Application
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private OverlayController overlayController;
    private int currentPity = 0;
    private final BannerHistoryDao historyDao = new BannerHistoryDao();

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting application initialization");

            // Загрузка FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hsr/pity/overlay.fxml"));
            Parent root = loader.load();
            overlayController = loader.getController();

            // Настройка сцены
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            // Настройка окна
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            primaryStage.setScene(scene);
            primaryStage.show();

            logger.info("Application window shown successfully");

            // Запуск обработки в фоне
            startCaptureLoop();

        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            System.exit(1);  // Исправлено: HUMAN 1 → 1
        }
    }

    private void startCaptureLoop() {
        ScreenCapturer capturer = new ScreenCapturer(0, 0, 1920, 1080);  // Исправлено: Screendapture → ScreenCapturer

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                BufferedImage image = capturer.capture();
                boolean isPityReset = ImageProcessor.processPityCounter(image);
                
                if (isPityReset) {
                    // Сохраняем в базу данных перед сбросом
                    historyDao.insertRecord(
                        LocalDateTime.now(),
                        "gacha_roll",
                        currentPity,
                        ImageProcessor.isFiveStar()
                    );
                    
                    currentPity = 0;
                    logger.info("Pity reset detected! Counter reset to 0");
                } else {
                    currentPity++;
                }
                
                Platform.runLater(() -> overlayController.updatePityCounter(currentPity));
            } catch (Exception e) {
                logger.error("Capture error", e);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {//что же это делает???😲😲🤯😳🤡
        launch(args);
    }
}