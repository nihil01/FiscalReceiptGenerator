package az.horosho;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Objects;

public class PrinterHelperMethods implements FileInterfaces{
//    private boolean finalStatus;
    public final String CONFIG_PATH =
            "C:\\Users\\orkhan.narbayov\\Desktop\\printer\\FiscalPrinter\\src\\main\\resources\\config.cfg";

    @Override
    public PrintService getPrintService(){
        String confLines = getDataFromConfig(CONFIG_PATH, ConfigAttributes.PRINTER_NAME);
        System.out.println("PRINTER NAME IS " + confLines);
        if (confLines == null) return null;

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            System.out.println(printService.getName());
            if (printService.getName().equalsIgnoreCase(confLines)) {
                System.out.println("PRINTER FOUND!");
                return printService;
            }
        }
        return null;
    }

    @Override
    public ArrayList<String> readFile(String path) {
        if (!checkFileExists(path)) return null;
        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;
            ArrayList<String> output = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] lines = line.split("\n");
                Collections.addAll(output, lines);
            }
            return output;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkFileExists(String path) {
        Path pathData = Paths.get(path);
        return (Files.exists(pathData) && Files.isRegularFile(pathData));
    }

    public String getDataFromConfig(String configPath, ConfigAttributes cfg){
        ArrayList<String> confData = readFile(configPath);
        if (confData.isEmpty()) return null;
        return confData.stream()
                .filter(s-> s.contains(cfg.toString()))
                .map(s -> {
                    String[] spl = s.split("=");
                    return spl.length == 2 ? spl[1].trim() : null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

//    public boolean checkPrinterStatus(){
//        PrintService printer = getPrintService();
//
//        if (printer == null) return false;
//        PrintServiceAttributeSet printServiceAttributeSet = printer.getAttributes();
//
//        PrinterState state = (PrinterState) printServiceAttributeSet.get(PrinterState.class);
//        PrinterStateReasons stateReasons = (PrinterStateReasons) printServiceAttributeSet.get(PrinterStateReasons.class);
//
//        if (state == null) {
//            System.out.println("Printer state is unavailable.");
//            return true;
//        }
//
//        // Печатаем состояние для отладки
//        System.out.println("Printer State: " + state);
//        if (stateReasons != null) {
//            System.out.println("Printer State Reasons: " + stateReasons);
//        }
//
//        if (state == PrinterState.IDLE || state == PrinterState.PROCESSING) {
//            if (stateReasons != null && !stateReasons.isEmpty()) {
//                System.out.println("Printer has warnings: " + stateReasons);
//            }
//            return true;
//        }
//
//        System.out.println("Printer is not ready.");
//        if (stateReasons != null) {
//            for (PrinterStateReason reason : stateReasons.keySet()) {
//                System.out.println("Problem: " + reason + " - " + stateReasons.get(reason));
//            }
//        }
//
//        return false;
//    }
}
