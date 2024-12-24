package az.horosho.fiscalService.ReceiptSamples;

import az.horosho.ConfigAttributes;
import az.horosho.PrinterHelperMethods;
import az.horosho.fiscalService.responses.*;

import java.util.ArrayList;

public interface Common {
    default byte[] concat(byte[][] arrays){
        int length = 0;

        for (byte[] arr : arrays) {
            length += arr.length;
        }

        byte[] result = new byte[length];
        int pos = 0;

        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, pos, arr.length);
            pos += arr.length;
        }
        return result;
    }

    default Byte[] concat(ArrayList<Byte[]> arrays){
        int length = 0;

        for (Byte[] arr : arrays) {
            length += arr.length;
        }

        Byte[] result = new Byte[length];
        int pos = 0;

        for (Byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, pos, arr.length);
            pos += arr.length;
        }
        
        return result;
    }

    default Byte[] toByteArray(byte[] array){
        Byte[] byteArray = new Byte[array.length];

        for(int i = 0; i < array.length; i++){
            byteArray[i] = array[i];
        }

        return byteArray;
    }

    default byte[] toByteArray(Byte[] array){
        byte[] byteArray = new byte[array.length];

        for(int i = 0; i < array.length; i++){
            byteArray[i] = array[i];
        }

        return byteArray;
    }

    default<T extends CreateDocument> HeaderPart generateHeaderPartCreateDocument(String chequeType, ConcreteResponseStructure<T> responseDocument){
        PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();

        String path = printerHelperMethods.getDataFromConfig(printerHelperMethods.CONFIG_PATH,
                ConfigAttributes.GET_INFO_OBJ_PATH);
        GetInfo getInfoData = GetInfo.deserializeObject(path);
        HeaderPart headerPart;

        if (responseDocument != null) {
           headerPart = new HeaderPart(getInfoData, chequeType, responseDocument.getData().getDocument_number());
        }else{
            headerPart = new HeaderPart(getInfoData, chequeType, 0);
        }

        if (getInfoData == null){
            System.err.println("getInfoData is null. Aborting..");
            return null;
        }

        return headerPart;
    }

    default<U extends GetControlTape> HeaderPart generateHeaderPartControlTape(String chequeType, ConcreteResponseStructure<U> responseDocument){
        PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();

        String path = printerHelperMethods.getDataFromConfig(printerHelperMethods.CONFIG_PATH,
                ConfigAttributes.GET_INFO_OBJ_PATH);
        GetInfo getInfoData = GetInfo.deserializeObject(path);

        HeaderPart headerPart;
        if (responseDocument != null) {
            headerPart = new HeaderPart(getInfoData, chequeType, responseDocument.getData().getShiftNumber());
        }else{
            headerPart = new HeaderPart(getInfoData, chequeType, 0);
        }

        if (getInfoData == null){
            System.err.println("getInfoData is null. Aborting..");
            return null;
        }

        return headerPart;
    }

    default GetInfo getInfoData(){
        PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();

        String path = printerHelperMethods.getDataFromConfig(printerHelperMethods.CONFIG_PATH,
                ConfigAttributes.GET_INFO_OBJ_PATH);
        return GetInfo.deserializeObject(path);
    }

}
