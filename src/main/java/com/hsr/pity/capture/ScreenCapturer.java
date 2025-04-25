package com.hsr.pity.capture;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.image.BufferedImage;

public class ScreenCapturer {
    private final Java2DFrameConverter converter = new Java2DFrameConverter();
    private final int x, y, width, height;

    public ScreenCapturer(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public BufferedImage capture() throws FrameGrabber.Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop")) {
            grabber.setFormat("gdigrab");
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            grabber.setFrameRate(1);
            grabber.start();

            Frame frame = grabber.grab();
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            Mat mat = converter.convert(frame);

            return matToBufferedImage(mat);
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Frame frame = converter.convert(mat);
        return this.converter.convert(frame);
    }
}