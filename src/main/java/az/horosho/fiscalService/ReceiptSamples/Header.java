package az.horosho.fiscalService.ReceiptSamples;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import az.horosho.PrinterService;
import az.horosho.fiscalService.responses.ConcreteResponseStructure;
import az.horosho.fiscalService.responses.GetInfo;

import javax.print.PrintException;

public class Header implements Common{

    private final GetInfo data;
    private byte[][] structure;

    public Header(ConcreteResponseStructure<GetInfo> resp){
        this.data = resp.getData();
        initializeStructure();
    }

    private void initializeStructure(){

        for(Field field: data.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                System.out.println(field.getName() + " : " + field.get(data));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        this.structure = new byte[][]{
            new byte[]{0x1B, 0x40},                             // Initialize printer
            new byte[]{0x1B, 0x21, 0x01},                       // Center alignment
            this.data.getObject_address().getBytes(StandardCharsets.UTF_8),
            this.data.getCompany_name().getBytes(StandardCharsets.UTF_8),
            this.data.getCompany_tax_number().getBytes(StandardCharsets.UTF_8),
            this.data.getObject_tax_number().getBytes(StandardCharsets.UTF_8),
            this.data.getLast_doc_number().getBytes(StandardCharsets.UTF_8),        
            new byte[]{0x1B, 0x64, 0x02},                       // Feed 2 lines
            new byte[]{0x1D, 0x56, 0x42, 0x00},                 // Partial cut
            new byte[]{0x1B, 0x32, 0x02}
        };
    }
    
    
    public void generate() {
        PrinterService printerService = new PrinterService();
            try
            {
                printerService.printReceipt(concat(structure));
            } catch(
            PrintException e)
            {
                System.err.println("COULD NOT PRINT GET INFO RECEIPT " + e.getMessage());
            }
        }
    }

