package com.hsr.pity;

import com.hsr.pity.logic.PityCalculator;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OverlayController {
    private static final Logger logger = LoggerFactory.getLogger(OverlayController.class);

    @FXML private VBox rootContainer;
    @FXML private Label pityCounterLabel;
    @FXML private Label probabilityLabel;
    @FXML private ProgressBar pityProgressBar;
    @FXML private TextField manualPityInput;
    @FXML private Button savePityButton;

    private int currentPity = 0;
    private final PityCalculator calculator = new PityCalculator();

    @FXML
    public void initialize() {
        logger.info("Initializing UI components");

        // Настройка стилей
        rootContainer.setStyle("-fx-background-color: rgba(20, 20, 30, 0.7);" +
                "-fx-border-color: rgba(255, 200, 100, 0.3);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-padding: 15;");

        pityProgressBar.setStyle("-fx-accent: #FFB84D;");

        // Добавляем валидацию ввода
        manualPityInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                manualPityInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Анимация плавного обновления
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateUI();
            }
        }.start();
    }

    @FXML
    private void handleSavePity() {
        try {
            String input = manualPityInput.getText();
            if (!input.isEmpty()) {
                int newPity = Integer.parseInt(input);
                if (newPity >= 0 && newPity <= 90) {
                    currentPity = newPity;
                    manualPityInput.clear();
                    logger.info("Manually set pity counter to: {}", newPity);
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid pity input: {}", manualPityInput.getText());
        }
    }

    public void updatePityCounter(int newPity) {
        this.currentPity = newPity;
    }

    private void updateUI() {
        double probability = calculator.calculateCurrentRate(currentPity);
        int remaining = calculator.calculateRemainingPity(currentPity);

        pityCounterLabel.setText(String.format("Pity: %d/90", currentPity));
        probabilityLabel.setText(String.format("Chance: %.1f%%", probability * 100));
        pityProgressBar.setProgress(currentPity / 90.0);

        // Динамическое изменение цвета
        if (currentPity >= 74) {
            pityProgressBar.setStyle("-fx-accent: #FF5555;");
        } else {
            pityProgressBar.setStyle("-fx-accent: #FFB84D;");
        }
    }
}