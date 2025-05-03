package com.hsr.pity;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_core.Size;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ImageProcessor {
    private static final Logger logger = Logger.getLogger(ImageProcessor.class.getName());
    private static final int FRAME_BUFFER_SIZE = 10;
    private static final double BRIGHTNESS_THRESHOLD = 0.4;
    private static final int MIN_BRIGHT_PIXELS = 5000;
    private static final int MIN_GOLD_PIXELS = 1000;
    private static List<Mat> frameBuffer = new ArrayList<>();
    private static boolean isRolling = false;
    private static int rollFrameCount = 0;
    private static boolean isFiveStar = false;

    // Диапазоны цветов для определения золотого
    private static final Scalar GOLD_LOWER = new Scalar(20, 100, 100, 0);
    private static final Scalar GOLD_UPPER = new Scalar(30, 255, 255, 0);

    public static boolean isFiveStar() {
        return isFiveStar;
    }

    public static boolean processPityCounter(BufferedImage image) {
        Mat mat = convertToMat(image);
        Mat processed = preprocessImage(mat);
        
        if (frameBuffer.size() >= FRAME_BUFFER_SIZE) {
            frameBuffer.remove(0).release();
        }
        frameBuffer.add(processed.clone());
        
        return detectGachaRoll(processed);
    }

    private static Mat convertToMat(BufferedImage image) {
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

    private static Mat preprocessImage(Mat src) {
        Mat hsv = new Mat();
        Mat bright = new Mat();
        Mat gold = new Mat();
        
        // Конвертация в HSV
        cvtColor(src, hsv, COLOR_BGR2HSV);
        
        // Выделение золотого цвета
        Mat lower = new Mat(hsv.size(), hsv.type(), GOLD_LOWER);
        Mat upper = new Mat(hsv.size(), hsv.type(), GOLD_UPPER);
        inRange(hsv, lower, upper, gold);
        
        // Выделение ярких областей
        Mat gray = new Mat();
        cvtColor(src, gray, COLOR_BGR2GRAY);
        threshold(gray, bright, 200, 255, THRESH_BINARY);
        
        // Освобождаем ресурсы
        hsv.release();
        gray.release();
        lower.release();
        upper.release();
        
        return bright;
    }

    private static boolean detectGachaRoll(Mat currentFrame) {
        if (frameBuffer.size() < 2) {
            return false;
        }

        // Анализируем яркость кадров
        Mat prevFrame = frameBuffer.get(frameBuffer.size() - 2);
        double currentBrightness = calculateBrightness(currentFrame);
        double prevBrightness = calculateBrightness(prevFrame);
        
        // Определяем резкие изменения яркости
        boolean hasBrightnessSpike = Math.abs(currentBrightness - prevBrightness) > BRIGHTNESS_THRESHOLD;
        
        // Подсчитываем количество ярких пикселей
        int brightPixels = countBrightPixels(currentFrame);
        
        // Проверяем наличие золотого цвета в центре экрана
        Mat hsv = new Mat();
        cvtColor(currentFrame, hsv, COLOR_BGR2HSV);
        int goldPixels = countGoldPixels(hsv);
        hsv.release();
        
        // Логика определения крутки
        if (hasBrightnessSpike && brightPixels > MIN_BRIGHT_PIXELS) {
            if (!isRolling) {
                isRolling = true;
                rollFrameCount = 0;
                isFiveStar = false;
                logger.info("Начало анимации крутки");
            }
            rollFrameCount++;
            
            // Проверяем на 5* персонажа
            if (goldPixels > MIN_GOLD_PIXELS) {
                isFiveStar = true;
                logger.info("Обнаружен 5* персонаж!");
            }
        } else {
            if (isRolling) {
                isRolling = false;
                if (rollFrameCount >= 5) {
                    logger.info("Обнаружена крутка! Длительность: " + rollFrameCount + " кадров" + 
                              (isFiveStar ? " (5* персонаж)" : ""));
                    return true;
                }
            }
        }

        return false;
    }

    private static double calculateBrightness(Mat frame) {
        Mat sum = new Mat();
        reduce(frame, sum, 0, REDUCE_SUM);
        double total = sum.getDoubleBuffer().get(0);
        sum.release();
        return total / (frame.rows() * frame.cols() * 255.0);
    }

    private static int countBrightPixels(Mat frame) {
        Mat bright = new Mat();
        threshold(frame, bright, 200, 255, THRESH_BINARY);
        Mat sum = new Mat();
        reduce(bright, sum, 0, REDUCE_SUM);
        int count = (int) (sum.getDoubleBuffer().get(0) / 255.0);
        bright.release();
        sum.release();
        return count;
    }

    private static int countGoldPixels(Mat hsv) {
        Mat gold = new Mat();
        Mat lower = new Mat(hsv.size(), hsv.type(), GOLD_LOWER);
        Mat upper = new Mat(hsv.size(), hsv.type(), GOLD_UPPER);
        inRange(hsv, lower, upper, gold);
        
        // Проверяем только центральную область экрана
        int centerX = hsv.cols() / 2;
        int centerY = hsv.rows() / 2;
        int roiSize = 200;
        
        Rect roi = new Rect(
            centerX - roiSize/2,
            centerY - roiSize/2,
            roiSize,
            roiSize
        );
        
        Mat centerRegion = new Mat(gold, roi);
        Mat sum = new Mat();
        reduce(centerRegion, sum, 0, REDUCE_SUM);
        int count = (int) (sum.getDoubleBuffer().get(0) / 255.0);
        
        centerRegion.release();
        gold.release();
        lower.release();
        upper.release();
        sum.release();
        
        return count;
    }
}