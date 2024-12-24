package az.horosho.fiscalService;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.print.PrintException;

import az.horosho.ConfigAttributes;
import az.horosho.PrinterHelperMethods;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import az.horosho.PrinterService;


public class ReceiptActions {
    private final static String QR_DEFAULT_PATH = new PrinterHelperMethods().
            getDataFromConfig(new PrinterHelperMethods().CONFIG_PATH, ConfigAttributes.QR_DEFAULT_PATH);
    
        public static void generateFiscalQRCode(String fiscalID){

            try {
                BitMatrix bmp = new QRCodeWriter().encode("https://monitoring.e-kassa.az/#/index?doc=" + fiscalID,
                 BarcodeFormat.QR_CODE, 300, 300);
    
                    int width = bmp.getWidth();
                    int height = bmp.getHeight();
                    System.out.println(width + "x" + height);

                final BufferedImage image = getBufferedImage(width, height, bmp);


                try{
                    File fileToSaveData = new File(QR_DEFAULT_PATH);
                    if(fileToSaveData.exists()){
                        ImageIO.write(image, "PNG", fileToSaveData);
                    }
                    System.out.println("FILE HAS BEEN SAVED SUCCESSFULLY!");

                    
                    new PrinterService().printReceipt(fileToSaveData);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PrintException e) {
                    e.printStackTrace();
                }

            
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static BufferedImage getBufferedImage(int width, int height, BitMatrix bmp) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bmp.get(x, y)) {
                    image.setRGB(x, y, Color.BLACK.getRGB());  // Black for QR code
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB());  // White background
                }
            }
        }
        return image;
    }
}
