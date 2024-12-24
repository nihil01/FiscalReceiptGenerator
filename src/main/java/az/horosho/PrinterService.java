package az.horosho;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class PrinterService implements PrinterInterfaces{

    @Override
    public void printReceipt(byte[] input) throws PrintException {
        PrintService service = new PrinterHelperMethods().getPrintService();

        if (service != null){
            System.out.println("STARTING PRINT");
            System.out.println(service.getName());
            DocPrintJob job = service.createPrintJob();

            Doc doc = new SimpleDoc(input, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);
        }
    }


    @Override
    public void printReceipt(File file) throws PrintException {
        PrintService service = new PrinterHelperMethods().getPrintService();

        if (!file.exists()) {
            System.err.println("File does not exist!!");
            return;
        }

        try(InputStream in = new FileInputStream(file)) {

            DocPrintJob job = service.createPrintJob();
            Doc doc = new SimpleDoc(in, DocFlavor.INPUT_STREAM.PNG, null);

            HashPrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new MediaPrintableArea(0, 0, 50, 50, MediaPrintableArea.MM));
            aset.add(MediaSizeName.INVOICE);
            job.print(doc, aset);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
