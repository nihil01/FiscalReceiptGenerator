package az.horosho.UI;

import az.horosho.ConfigAttributes;
import az.horosho.PrinterHelperMethods;
import az.horosho.VariablesForRequestProcessing;
import az.horosho.fiscalService.responses.GetInfo;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

public class HelperMethods {
    private static HashMap<String, Component> namedComponents = null;

    protected static String replaceWithVariables(String input){
        VariablesForRequestProcessing variablesForRequestProcessing = new VariablesForRequestProcessing();
        String newInput;
        if (input.contains("{{token}}")){
            newInput = input.replace("{{token}}", String.format("\"%s\"",variablesForRequestProcessing.getPreferences("token")));
        }else{
            newInput = input;
        }
        return newInput;
    }

    protected static RequestBody prepareJsonOutput(String input){
        return RequestBody.create(MediaType.parse("application/json"), input);
    }

    public static void createComponentMap(Component[] components) {
        if (namedComponents == null) {
            namedComponents = new HashMap<>();
        }
        for (Component component : components) {
            if (component.getName() != null) {
                namedComponents.put(component.getName(), component);
            }
            if (component instanceof Container) {
                createComponentMap(((Container) component).getComponents());
            }
        }
    }

    protected static Component getComponentByName(String name){
        if (namedComponents.containsKey(name)) {
            return namedComponents.get(name);
        }
        return null;
    }

    protected static JButton[] getAllButtons(){
        return  new JButton[]{
                new JButton("GET INFO"),
                new JButton("LOGIN"),
                new JButton("SHIFT STATUS"),
                new JButton("OPEN SHIFT"),
                new JButton("CLOSE SHIFT"),
                new JButton("CREATE DOCUMENT"),
                new JButton("GET LAST DOCUMENT"),
                new JButton("GET X REPORT"),
                new JButton("GET PERIODIC Z REPORT"),
                new JButton("GET CONTROL TAPE"),
                new JButton("LOGOUT")
        };
    }

    protected static void notifyUser(String message){
        Frame frame = new Frame();
        JOptionPane.showMessageDialog(frame, message);
    }

    protected static boolean checkDataEmpty(String jsonData){
        if (jsonData.trim().isEmpty()){
            notifyUser("Пустое значение недопустимо!");
            return true;
        }
        return false;
    }

    protected static boolean isGetInfoInitialized(){
        System.out.println("CHECKING");
        PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();

        String getInfoPath = printerHelperMethods.getDataFromConfig(
                printerHelperMethods.CONFIG_PATH, ConfigAttributes.GET_INFO_OBJ_PATH);

        return Files.exists(Paths.get(getInfoPath));
    }


    protected static void replaceCharsToASCII(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Открываем доступ к приватным полям
            try {
                Object value = field.get(obj);

                if (value instanceof String) {
                    String original = (String) value;
                    System.out.println("ORIGINAL: " + original);
                    if (original != null) {
                        String replaced = original.toLowerCase(Locale.ROOT)
                                .replace("ə", "e")
                                .replace("ö", "o")
                                .replace("ş", "s")
                                .replace("ğ", "g")
                                .replace("ı", "i")
                                .replace("ç", "c")
                                .replace("ü", "u");
                        System.out.println("Field: " + field.getName() + " | Replaced: " + replaced);

                        field.set(obj, replaced);
                    }
                } else if (value != null && !field.getType().isPrimitive()) {
                    replaceCharsToASCII(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
