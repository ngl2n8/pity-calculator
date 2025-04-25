package com.hsr.pity;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC3;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ImageProcessor {
    private static final Tesseract tesseract = new Tesseract();

    static {
        // Указываем путь к данным Tesseract (если не в системной папке)
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(7); // Сегментация одной строки
        tesseract.setOcrEngineMode(3); // LSTM OCR
    }

    public static int processPityCounter(BufferedImage image) {
        Mat mat = convertToMat(image);
        Mat processed = preprocessImage(mat);
        return performOCR(processed);
    }

    // Конвертация BufferedImage в OpenCV Mat
    private static Mat convertToMat(BufferedImage image) {
        // Конвертация в 3-канальный BGR формат
        BufferedImage bgrImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR
        );
        bgrImage.getGraphics().drawImage(image, 0, 0, null);

        byte[] pixels = ((DataBufferByte) bgrImage.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(bgrImage.getHeight(), bgrImage.getWidth(), CV_8UC3);
        mat.data().put(pixels);
        return mat;
    }

    // Предобработка изображения для OCR
    private static Mat preprocessImage(Mat src) {
        Mat gray = new Mat();
        Mat thresholded = new Mat();
        Mat kernel = getStructuringElement(MORPH_RECT, new Size(3, 3));

        // Уменьшение шума и улучшение контраста
        cvtColor(src, gray, COLOR_BGR2GRAY);
        GaussianBlur(gray, gray, new Size(3, 3), 0);
        adaptiveThreshold(
                gray,
                thresholded,
                255,
                ADAPTIVE_THRESH_GAUSSIAN_C,
                THRESH_BINARY,
                11,
                2
        );
        morphologyEx(thresholded, thresholded, MORPH_CLOSE, kernel);

        return thresholded;
    }

    // Распознавание текста с помощью Tesseract
    private static int performOCR(Mat image) {
        try {
            // Временное сохранение изображения для Tesseract
            String tempFile = "temp_ocr.png";
            opencv_imgcodecs.imwrite(tempFile, image);

            // Распознавание с постобработкой
            String result = tesseract.doOCR(new java.io.File(tempFile))
                    .replaceAll("[^\\d]", "") // Оставляем только цифры
                    .trim();

            return result.isEmpty() ? 0 : Integer.parseInt(result);
        } catch (TesseractException | NumberFormatException e) {
            System.err.println("OCR Error: " + e.getMessage());
            return 0; // Возвращаем 0 при ошибке распознавания
        }
    }
}