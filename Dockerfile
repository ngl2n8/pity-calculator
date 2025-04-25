FROM adoptopenjdk/openjdk17:alpine

# Установка зависимостей OpenCV и Tesseract
RUN apk add --no-cache \
    opencv \
    tesseract-ocr \
    tesseract-ocr-data-eng

WORKDIR /app
COPY target/pity-calculator-1.0.0.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]