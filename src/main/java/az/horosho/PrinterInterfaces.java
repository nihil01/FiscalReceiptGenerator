package az.horosho;

import java.io.File;

import javax.print.PrintException;
import javax.print.PrintService;

public interface PrinterInterfaces {

    void printReceipt(byte[] i) throws PrintException;

    void printReceipt(File filePath) throws PrintException;

}
