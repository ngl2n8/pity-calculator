package com.hsr.pity;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class OverlayController {
    @FXML
    private StackPane overlayPane;

    @FXML
    private Label pityCounterLabel;

    public OverlayController() {
        // Инициализация FXML (нужен файл overlay.fxml)
    }

    public void updatePityCounter(int count) {
        Platform.runLater(() ->
                pityCounterLabel.setText("Pity Counter: " + count)
        );
    }
}