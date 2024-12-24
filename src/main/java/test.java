import az.horosho.PrinterService;
import az.horosho.fiscalService.ReceiptActions;

import javax.print.PrintException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class test {
    public static void main(String[] args) throws IOException, PrintException {
        java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
        System.out.println("Hostname of local machine: " + localMachine.getHostName());    }
}
