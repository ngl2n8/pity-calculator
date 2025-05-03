package com.hsr.pity;

import com.hsr.pity.logic.PityCalculator;
import com.hsr.pity.database.BannerHistoryDao;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;

import java.time.LocalDateTime;

public class OverlayController {
    private static final Logger logger = LoggerFactory.getLogger(OverlayController.class);

    @FXML private VBox rootContainer;
    @FXML private Label pityCounterLabel;
    @FXML private Label probabilityLabel;
    @FXML private ProgressBar pityProgressBar;
    @FXML private TextField manualPityInput;
    @FXML private ComboBox<String> itemTypeComboBox;
    @FXML private CheckBox is5StarCheckBox;
    @FXML private Button savePityButton;

    private int currentPity = 0;
    private final PityCalculator calculator = new PityCalculator();
    private final BannerHistoryDao historyDao = new BannerHistoryDao();

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

        // Инициализация ComboBox
        itemTypeComboBox.getItems().addAll(
            "Персонаж",
            "Световой конус"
        );
        
        // Установка значений по умолчанию
        is5StarCheckBox.setSelected(false);
        itemTypeComboBox.setValue("Персонаж");

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
            int pityCount = Integer.parseInt(manualPityInput.getText().trim());
            if (pityCount < 0 || pityCount > 90) {
                showError("Количество круток должно быть от 0 до 90");
                return;
            }

            String itemType = itemTypeComboBox.getValue();
            if (itemType == null || itemType.isEmpty()) {
                showError("Выберите тип предмета");
                return;
            }

            // Сохраняем в базу данных
            historyDao.insertRecord(
                LocalDateTime.now(),
                itemType,
                pityCount,
                is5StarCheckBox.isSelected()
            );

            // Обновляем интерфейс
            updateUI(pityCount);
            
            // Очищаем поле ввода
            manualPityInput.clear();
            
            // Показываем сообщение об успехе
            showInfo("Запись успешно сохранена");
            
        } catch (NumberFormatException e) {
            showError("Введите корректное число круток");
        }
    }

    public void updatePityCounter(int newPity) {
        this.currentPity = newPity;
    }

    private void updateUI() {
        double probability = PityCalculator.calculateCurrentRate(currentPity);
        int remaining = PityCalculator.calculateRemainingPity(currentPity);
        
        pityProgressBar.setProgress(currentPity / 90.0);
        pityCounterLabel.setText(String.format("Крутки: %d", currentPity));
        probabilityLabel.setText(String.format("Шанс: %.1f%%", probability * 100));

        // Динамическое изменение цвета
        if (currentPity >= 74) {
            pityProgressBar.setStyle("-fx-accent: #FF5555;");
        } else {
            pityProgressBar.setStyle("-fx-accent: #FFB84D;");
        }
    }

    private void updateUI(int pityCount) {
        double probability = PityCalculator.calculateCurrentRate(pityCount);
        int remaining = PityCalculator.calculateRemainingPity(pityCount);
        
        pityProgressBar.setProgress(pityCount / 90.0);
        pityCounterLabel.setText(String.format("Крутки: %d", pityCount));
        probabilityLabel.setText(String.format("Шанс: %.1f%%", probability * 100));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}