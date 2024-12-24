package az.horosho;

import javax.print.PrintService;
import java.util.ArrayList;

public interface FileInterfaces {

    ArrayList<String> readFile(String path);

    boolean checkFileExists(String path);

//    boolean checkPrinterStatus();

    PrintService getPrintService();

}
