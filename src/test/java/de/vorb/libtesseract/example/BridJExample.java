package de.vorb.libtesseract.example;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.TessBaseAPI;
import de.vorb.tesseract.bridj.Tesseract.TessOcrEngineMode;

public class BridJExample {
  public static void main(String[] args) throws IOException {
    // provide the native library file
    BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

    long start = System.currentTimeMillis();
    // create a reference to an execution handle
    final Pointer<TessBaseAPI> handle = Tesseract.TessBaseAPICreate();

    // init Tesseract with data path, language and OCR engine mode
    Tesseract.TessBaseAPIInit2(handle, Pointer.pointerToCString("tessdata"),
        Pointer.pointerToCString("deu-frak"), TessOcrEngineMode.OEM_DEFAULT);

    // set page segmentation mode
    Tesseract.TessBaseAPISetPageSegMode(handle,
        Tesseract.TessPageSegMode.PSM_AUTO);

    // read the image into memory
    final BufferedImage inputImage = ImageIO.read(new File("input.png"));

    // get the image data
    final DataBuffer imageBuffer = inputImage.getRaster().getDataBuffer();
    final byte[] imageData = ((DataBufferByte) imageBuffer).getData();

    // image properties
    final int width = inputImage.getWidth(), height = inputImage.getHeight();
    final int bitsPerPixel = inputImage.getColorModel().getPixelSize();
    final int bytesPerPixel = bitsPerPixel / 8;
    final int bytesPerLine = (int) Math.ceil(width * bitsPerPixel / 8.0);

    // set the image
    Tesseract.TessBaseAPISetImage(handle,
        Pointer.pointerToBytes(ByteBuffer.wrap(imageData)), width, height,
        bytesPerPixel, bytesPerLine);

    // get the text result
    final String txt = Tesseract.TessBaseAPIGetUTF8Text(handle).getCString();

    // print the result
    System.out.println(txt);

    // calculate the time
    System.out.println("time: " + (System.currentTimeMillis() - start) + "ms");

    // delete handle
    Tesseract.TessBaseAPIDelete(handle);
  }
}
