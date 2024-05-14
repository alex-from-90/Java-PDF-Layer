package ru.alex;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class OcgExample {
    public static void main(String[] args) {
        String inputFile = "src/main/resources/doc.pdf";
        String outputFolder = "pdf_outputs_layer"; // Папка для сохранения изображений
        try (PDDocument document = PDDocument.load(new File(inputFile))) {
            PDOptionalContentProperties ocProps = document.getDocumentCatalog().getOCProperties();
            if (ocProps != null) {
                PDFRenderer renderer = new PDFRenderer(document);
                for (String layerName : ocProps.getGroupNames()) {
                    // Для каждого слоя отключаем все слои...
                    for (String ln : ocProps.getGroupNames()) {
                        ocProps.setGroupEnabled(ln, false);
                    }
                    // ... и включаем только текущий
                    ocProps.setGroupEnabled(layerName, true);
                    // Рендеринг страницы с учетом установленной видимости слоёв
                    for (int i = 0; i < document.getNumberOfPages(); ++i) {
                        BufferedImage image = renderer.renderImageWithDPI(i, 300);
                        // Создание папки, если она еще не существует
                        File dir = new File(outputFolder);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        String outputFileName = outputFolder + "/" + layerName + "_page_" + (i + 1) + ".png";
                        // Сохранение рендеренной страницы в файл
                        ImageIO.write(image, "PNG", new File(outputFileName));
                    }
                    System.out.println("Слой " + layerName + " был обработан и сохранен.");
                }
            } else {
                System.out.println("Документ не содержит слоев (OCG).");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
