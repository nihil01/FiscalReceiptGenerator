package az.horosho.UI;

import java.awt.Component;
import java.awt.GridLayout;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import az.horosho.ConfigAttributes;
import az.horosho.PrinterHelperMethods;
import az.horosho.fiscalService.ReceiptSamples.*;
import az.horosho.fiscalService.responses.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import az.horosho.VariablesForRequestProcessing;
import az.horosho.fiscalService.FiscalInterfaces;
import az.horosho.fiscalService.FiscalService;
import az.horosho.fiscalService.http.CommonCallback;
import az.horosho.fiscalService.http.HTTPProcessor;
import az.horosho.fiscalService.responses.requests.ConcreteRequestStructure;
import az.horosho.fiscalService.responses.requests.CreateDocumentTransaction;
import okhttp3.RequestBody;
import retrofit2.Call;

import static az.horosho.UI.HelperMethods.*;


public class SwingCustomUtilities {


    protected static JPanel generateLowerPanel(JButton jButton){
        GridLayout gridLayout = new GridLayout(3, 3);
        gridLayout.setHgap(5);
        gridLayout.setVgap(5);
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(gridLayout);
        lowerPanel.add(jButton);

        JButton[] jButtons;

        if (!isGetInfoInitialized()){
            JButton getInfoButton = new JButton("GET INFO");
            jButtons = new JButton[]{getInfoButton};
        }else{
            jButtons = getAllButtons();
        }

        HTTPProcessor httpProcessor = new HTTPProcessor();

        for(JButton btn : jButtons){

            if ((btn.getText()).equalsIgnoreCase("get info")){
                btn.addActionListener(e -> {
                    Call<ConcreteResponseStructure<GetInfo>> request = FiscalService.getFiscalService().getInfo();
                    httpProcessor.process(request, new CommonCallback<ConcreteResponseStructure<GetInfo>>() {
                        @Override
                        public void onSuccess(ConcreteResponseStructure<GetInfo> resp) {
                            if (resp.getCode() == 0) {
                                GetInfo getInfo = resp.getData();
                                System.out.println("BEFORE");
                                System.out.println(getInfo.toString());

                                replaceCharsToASCII(getInfo);
                                System.out.println("AFTER");
                                System.out.println(getInfo.toString());

                                PrinterHelperMethods printerHelperMethods = new PrinterHelperMethods();

                                String path = printerHelperMethods.getDataFromConfig(printerHelperMethods.CONFIG_PATH,
                                        ConfigAttributes.GET_INFO_OBJ_PATH);

                                if (path == null) {
                                    System.out.println("NO PATH FOUND!!!");
                                }
                                GetInfo.serializeObject(getInfo, path);

                                lowerPanel.removeAll();
                                JPanel newPanel = generateLowerPanel(jButton);
                                for (Component component : newPanel.getComponents()) {
                                    lowerPanel.add(component);
                                }

                                lowerPanel.revalidate();
                                lowerPanel.repaint();

                                Header header = new Header(resp);
                                header.generate();   
                            }else{
                                notifyUser(resp.getCode() + "| " + resp.getMessage());
                            }
                                                  
                        }

                        @Override
                        public void onError(Throwable t) {
                            // TODO Auto-generated method stub
                            notifyUser("Неудачный запрос! " + t.getMessage());
                        }
                    }); 
                });
            }else if ((btn.getText()).equalsIgnoreCase("login")){
                btn.addActionListener(e -> {
                    String json = ((JTextArea)getComponentByName("textArea")).getText();
                    if(checkDataEmpty(json)) return;

                    RequestBody body = prepareJsonOutput(json);
                    System.out.println(123123);
                    System.out.println(json);

                    Call<ConcreteResponseStructure<ToLogin>> toLogin = FiscalService.getFiscalService().getToLogin(body);
                    httpProcessor.process(toLogin, new CommonCallback<>(){
                        @Override
                        public void onError(Throwable err) {
                            notifyUser("Неудачный запрос! " + err.getMessage());
                        }

                        @Override
                        public void onSuccess(ConcreteResponseStructure<ToLogin> data) {
                            if(data != null){
                                if (data.getCode() == 0) {
                                    String accessToken = data.getData().getAccess_token();
                                    System.out.println(accessToken);

                                    new VariablesForRequestProcessing().setPreferences("token", accessToken);
                                    notifyUser("Успешная операция! token теперь хранится в переменных, его теперь можно использовать в запросах как {{token}}");                                    
                                }else{
                                    System.out.println("Data after receiving error");
                                    System.out.println(data.getData());
                                    notifyUser(data.getCode() + "| " + data.getMessage());
                                }
                            }else{
                                notifyUser("Неудачный запрос. Данные не обнаружены!");
                            }                      
                        }
                    });
                });
            }else if ((btn.getText()).equalsIgnoreCase("logout")) {
                btn.addActionListener(e -> {
                    String json = ((JTextArea)getComponentByName("textArea")).getText();
                    String replaced = replaceWithVariables(json);
                    
                    RequestBody body = prepareJsonOutput(replaced);
                    if(checkDataEmpty(json)) return;
                    
                    Call<ConcreteResponseStructure<ToLogin>> toLogout = FiscalService.getFiscalService().getToLogout(body);
                    httpProcessor.process(toLogout, new CommonCallback<>(){
                        @Override
                        public void onError(Throwable err) {
                            notifyUser("Неудачный запрос! " + err.getMessage());
                        }

                        @Override
                        public void onSuccess(ConcreteResponseStructure<ToLogin> data) {
                            if(data != null){
                                if (data.getCode() == 0) {
                                    new VariablesForRequestProcessing().removePreferences("token");
                                    notifyUser("Успешная операция! token был удалён");
                                }else{
                                    notifyUser(data.getCode() + "| " + data.getMessage());
                                }
                            }else{
                                notifyUser("Неудачный запрос. Данные не обнаружены!");
                            }                      
                        }
                    });
                }); 
            }
            else if((btn.getText()).equalsIgnoreCase("shift status")){
                btn.addActionListener(e -> {
                    JTextArea textArea = (JTextArea)getComponentByName("textArea");
                    if (textArea == null) {
                        notifyUser("Компонент textArea не найден.");
                        return;
                    }
                
                    String json = textArea.getText().trim();
                    if (checkDataEmpty(json)) return;
                
                    System.out.println(json);
                
                    String replacedJson = replaceWithVariables(json);
                    if (replacedJson == null) {
                        notifyUser("Ошибка при обработке переменных.");
                        return;
                    }
                    
                    RequestBody body = prepareJsonOutput(replacedJson);
                    
                
                    if (body == null) {
                        notifyUser("Ошибка формирования тела запроса.");
                        return;
                    }
                
                    FiscalInterfaces fiscalService =  FiscalService.getFiscalService();
                    if (fiscalService == null) {
                        notifyUser("Ошибка инициализации сервиса.");
                        return;
                    }
                
                    Call<ConcreteResponseStructure<GetShiftStatus>> shiftStatus = fiscalService.getShiftStatus(body);
                    httpProcessor.process(shiftStatus, new CommonCallback<>() {
                        @Override
                        public void onError(Throwable err) {
                            notifyUser("Неудачный запрос! " + err.getMessage());
                        }
                
                        @Override
                        public void onSuccess(ConcreteResponseStructure<GetShiftStatus> data) {
                            if (data.getCode() == 0) {
                                String shiftOpenTime = data.getData().getShift_open_time();
                                try {
                                    if(data.getData().isShift_open()){
                                        if (shiftOpenTime.contains("Z")) {
                                            shiftOpenTime = shiftOpenTime.substring(0, shiftOpenTime.indexOf("Z"));
                                        }
                                        LocalDateTime date = LocalDateTime.parse(shiftOpenTime);
                                        String parsedDate = String.format("%d %d %d %d:%d", 
                                        date.getDayOfMonth(), date.getMonthValue(), date.getYear(), date.getHour(), date.getMinute());
                                        notifyUser("Смена открыта! Дата открытия смены: " + parsedDate);
                                    }else{
                                        notifyUser("Смена закрыта!");
                                    }
                                    
                                } catch (Exception e) {
                                    notifyUser("Ошибка при работе с переменными: " + e.getMessage());
                                }
                            } else {
                                notifyUser(data.getCode() + "| " + data.getMessage());
                            }
                        }
                    });
                });
            }else if ((btn.getText()).equalsIgnoreCase("open shift")) {
                btn.addActionListener(e -> {
                    JTextArea textArea = (JTextArea)getComponentByName("textArea");
                    if (textArea == null) {
                        notifyUser("Компонент textArea не найден.");
                        return;
                    }
                
                    String json = textArea.getText().trim();
                    if (checkDataEmpty(json)) return;
                
                    System.out.println(json);
                
                    String replacedJson = replaceWithVariables(json);
                    if (replacedJson == null) {
                        notifyUser("Ошибка при обработке переменных.");
                        return;
                    }
                    
                    RequestBody body = prepareJsonOutput(replacedJson);
                    
                
                    if (body == null) {
                        notifyUser("Ошибка формирования тела запроса.");
                        return;
                    }
                
                    FiscalInterfaces fiscalService =  FiscalService.getFiscalService();

                    if (fiscalService == null) {
                        notifyUser("Ошибка инициализации сервиса.");
                        return;
                    }
                
                    Call<ConcreteResponseStructure<ToLogin>> openShift = fiscalService.openShift(body);
                    httpProcessor.process(openShift, new CommonCallback<>() {
                        @Override
                        public void onError(Throwable err) {
                            notifyUser("Неудачный запрос! " + err.getMessage());
                        }

                        @Override
                        public void onSuccess(ConcreteResponseStructure<ToLogin> data) {
                            if (data.getCode() == 0) {
                                System.out.println(data.getMessage());
                                notifyUser("Смена была успешно открыта!");
                            } else {
                                notifyUser(data.getCode() + "| " + data.getMessage());
                            }
                        }
                    });
                });
            }else if((btn).getText().equalsIgnoreCase("create document")){
                btn.addActionListener(_ -> {

                    JTextArea textEl = (JTextArea)getComponentByName("textArea");
                    if(checkDataEmpty(textEl.getText()) || textEl == null) return;
                    String updatedTextInput = replaceWithVariables(textEl.getText());
                    try{
                        Gson gson = new Gson();
                        Type collectiontype = new TypeToken<ConcreteRequestStructure<CreateDocumentTransaction>> (){}.getType();

                        ConcreteRequestStructure<CreateDocumentTransaction> saleTransaction = gson
                                                .fromJson(updatedTextInput, collectiontype);
                                    

                        Call<ConcreteResponseStructure<CreateDocument>> transaction = FiscalService
                        .getFiscalService().createDocument(saleTransaction);

                        httpProcessor.process(transaction, new CommonCallback<>(){
                            @Override
                            public void onSuccess(ConcreteResponseStructure<CreateDocument> data) {
                                if (data.getCode() == 0) {
                                    System.out.println("TRANSACTION TYPE " + saleTransaction.getParameters().getDocType());
                                    switch (saleTransaction.getParameters().getDocType()) {
                                        case "sale": {
                                            TrivialSale trivialSale = new TrivialSale(saleTransaction, data, null);
                                            trivialSale.generate();
                                            break;
                                        }
                                        case "prepay":{
                                            TrivialSale trivialSale = new TrivialSale(saleTransaction, data, TrivialSale.Type.PREPAYMENT);
                                            trivialSale.generate();
                                            break;
                                        }

                                        case "creditpay": {
                                            TrivialSale trivialSale = new TrivialSale(saleTransaction, data, TrivialSale.Type.CREDIT_PAY);
                                            trivialSale.generate();
                                            break;
                                        }
                                        case "deposit": {
                                            Deposit_WIthdrawSale deposit = new Deposit_WIthdrawSale(
                                                    saleTransaction, data, Deposit_WIthdrawSale.DocType.DEPOSIT);
                                            deposit.generate();
                                            break;
                                        }
                                        case "withdraw": {
                                            Deposit_WIthdrawSale withdraw = new Deposit_WIthdrawSale(
                                                    saleTransaction, data, Deposit_WIthdrawSale.DocType.WITHDRAW);
                                            withdraw.generate();
                                            break;
                                        }
                                        case "correction": {
                                            Correction correction = new Correction(saleTransaction, data);
                                            correction.generate();
                                            break;
                                        }
                                        case "money_back": {
                                            RollBack_MoneyBack rollBackMoneyBack = new RollBack_MoneyBack(RollBack_MoneyBack.Type.MoneyBack,saleTransaction, data);
                                            rollBackMoneyBack.generate();
                                            break;
                                        }
                                        case "rollback": {
                                            RollBack_MoneyBack rollBackMoneyBack = new RollBack_MoneyBack(RollBack_MoneyBack.Type.RollBack,saleTransaction, data);
                                            rollBackMoneyBack.generate();
                                            break;
                                        }
                                    }
                                } else {
                                    notifyUser(data.getCode() + "| " + data.getMessage());
                                }
                            }
    
                            @Override
                            public void onError(Throwable t) {
                                notifyUser("Неудачный запрос! " + t.getMessage());
                            }
    
                        });
                    }catch(IllegalStateException err){
                        System.err.println(err.getMessage());
                    
                    }
                });
            }else if ((btn.getText()).equalsIgnoreCase("close shift") ||
                    (btn.getText()).equalsIgnoreCase("get x report") ||
                    (btn.getText()).equalsIgnoreCase("get periodic z report")) {
                btn.addActionListener(e -> {

                    JTextArea textEl = (JTextArea)getComponentByName("textArea");
                    if(checkDataEmpty(textEl.getText()) || textEl == null) return;
                    String updatedTextInput = replaceWithVariables(textEl.getText());
                    try{
                        RequestBody body = prepareJsonOutput(updatedTextInput);
                        Call<ConcreteResponseStructure<Shift>> closeShiftData;
                        if ((btn.getText()).equalsIgnoreCase("close shift")){
                            closeShiftData = FiscalService.getFiscalService().closeShift(body);
                        } else if ((btn.getText()).equalsIgnoreCase("get periodic z report")) {
                            closeShiftData = FiscalService.getFiscalService().getPeriodicZReport(body);
                        } else {
                            closeShiftData = FiscalService.getFiscalService().getXReport(body);
                        }
                        httpProcessor.process(closeShiftData, new CommonCallback<>(){
                            @Override
                            public void onSuccess(ConcreteResponseStructure<Shift> data) {
                                if (data.getCode() == 0) {
                                    ZReport zReport;
                                    if ((btn.getText()).equalsIgnoreCase("close shift")){
                                        zReport = new ZReport(data, ZReport.Type.Z);
                                    }else if ((btn.getText()).equalsIgnoreCase("get periodic z report")) {
                                        zReport = new ZReport(data, ZReport.Type.PERIODIC_Z);
                                    }
                                    else{
                                        zReport = new ZReport(data, ZReport.Type.X);
                                    }
                                    zReport.generate();
                                } else {
                                    notifyUser(data.getCode() + "| " + data.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                notifyUser("Неудачный запрос! " + t.getMessage());
                            }

                        });
                    }catch(IllegalStateException err){
                        System.err.println(err.getMessage());
                    }
                });
            }else if (btn.getText().equalsIgnoreCase("get control tape")){
                btn.addActionListener(e -> {

                    JTextArea textEl = (JTextArea)getComponentByName("textArea");
                    if(checkDataEmpty(textEl.getText()) || textEl == null) return;

                    String updatedTextInput = replaceWithVariables(textEl.getText());
                    try{
                        RequestBody body = prepareJsonOutput(updatedTextInput);


                        Call<ConcreteResponseStructure<GetControlTape>> transaction = FiscalService
                                .getFiscalService().getControlTape(body);
                        httpProcessor.process(transaction, new CommonCallback<>(){

                            @Override
                            public void onSuccess(ConcreteResponseStructure<GetControlTape> data) {
                                if (data.getCode() == 0) {
                                    ControlTape controlTape = new ControlTape(data);
                                    controlTape.generate();
                                } else {
                                    notifyUser(data.getCode() + "| " + data.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                notifyUser("Неудачный запрос! " + t.getMessage());
                            }
                        });

                    }catch(IllegalStateException err){
                        System.err.println(err.getMessage());
                    }
                });
            }else if(btn.getText().equalsIgnoreCase("get last document")){
                btn.addActionListener(e -> {
                    System.out.println("TRIGGERED");
                    JTextArea textEl = (JTextArea) getComponentByName("textArea");
                    if (checkDataEmpty(textEl.getText()) || textEl == null) return;

                    String updatedTextInput = replaceWithVariables(textEl.getText());

                    try {
                        RequestBody body = prepareJsonOutput(updatedTextInput);

                        Call<ConcreteResponseStructure<GetLastDoc>> transaction =
                                FiscalService.getFiscalService().getLastDoc(body);

                        httpProcessor.process(transaction, new CommonCallback<>() {
                            @Override
                            public void onSuccess(ConcreteResponseStructure<GetLastDoc> data) {
                                if (data.getCode() == 0) {
                                    String docType = data.getData().getDoc_type();
                                    String docFiscalID = data.getData().getDocument_id();
                                    LastDocument lastDoc = data.getData().doc();
                                    switch (docType) {
                                        case "correction":
                                            Correction correction = new Correction(lastDoc, docFiscalID);
                                            correction.generate();
                                            break;
                                        case "sale":
                                            new TrivialSale(lastDoc, docFiscalID, null);
                                            break;
                                        case "creditpay":
                                            new TrivialSale(lastDoc, docFiscalID, TrivialSale.Type.CREDIT_PAY);
                                            break;
                                        case "prepay":
                                            new TrivialSale(lastDoc, docFiscalID, TrivialSale.Type.PREPAYMENT);
                                            break;
                                        case "money_back":
                                            RollBack_MoneyBack moneyBack = new RollBack_MoneyBack(RollBack_MoneyBack.Type.MoneyBack,lastDoc,
                                                    docFiscalID);
                                            moneyBack.generate();
                                            break;
                                        case "rollback":
                                            RollBack_MoneyBack rollBack = new RollBack_MoneyBack(RollBack_MoneyBack.Type.RollBack,
                                                    lastDoc, docFiscalID);
                                            rollBack.generate();
                                            break;
                                        case "deposit":
                                            Deposit_WIthdrawSale deposit = new Deposit_WIthdrawSale(lastDoc, docFiscalID, Deposit_WIthdrawSale.DocType.DEPOSIT);
                                            deposit.generate();
                                            break;
                                        case "withdraw":
                                            Deposit_WIthdrawSale withdraw = new Deposit_WIthdrawSale(lastDoc, docFiscalID, Deposit_WIthdrawSale.DocType.WITHDRAW);
                                            withdraw.generate();
                                            break;
                                        case "zreport":
                                            ZReport zReport = new ZReport(lastDoc, docFiscalID, ZReport.Type.Z);
                                            zReport.generate();
                                        default:
                                            throw new IllegalArgumentException("Unknown docType: " + docType);
                                    }

                                } else {
                                    notifyUser(data.getCode() + "| " + data.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                notifyUser("Неудачный запрос! " + t.getMessage());
                                System.err.println(t.getMessage());
                            }
                        });

                    } catch (IllegalStateException err) {
                        System.err.println(err.getMessage());
                    }
                });

            }
            lowerPanel.add(btn);
        }

        return lowerPanel;
    }



    protected static JPanel generateUpperPanel(JTextArea textArea){
        JPanel upperPanel = new JPanel();
        JLabel label = new JLabel("Введите JSON запрос:", JLabel.CENTER);
        textArea.setColumns(50);
        textArea.setRows(30);
        textArea.setSize(800, 300);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setName("textArea");
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        scrollPane.setVerticalScrollBar(verticalScrollBar);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        upperPanel.add(label);
        upperPanel.add(scrollPane);

        return upperPanel;
    }

    protected static void openVariableWindow(){
        var variablesForRequestProcessing = new VariablesForRequestProcessing();
        JDialog dialog = new JDialog();

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Access Token");
        JButton jButton = new JButton("Применить изменения");
        jButton.setName("token");

        JTextField textFieldToken = new JTextField();
        textFieldToken.setSize(10, 10);

        JLabel labelPhone = new JLabel("Номер телефона:");
        JTextField textFieldPhoneNumber = new JTextField();
        textFieldPhoneNumber.setSize(10, 10);

        JLabel labelShop = new JLabel("Название магазина");
        JTextField textFieldShopName = new JTextField();
        textFieldShopName.setSize(10, 10);

        JLabel labelCashier = new JLabel("Имя кассира:");
        JTextField textFieldCashier = new JTextField();
        textFieldCashier.setSize(10, 10);

        JLabel labelCashbox = new JLabel("Номер кассы:");
        JTextField textFieldCahboxNo = new JTextField();
        textFieldCahboxNo.setSize(10, 10);


        jButton.addActionListener(e -> {
            if (!textFieldToken.getText().trim().isEmpty() &&!textFieldCahboxNo.getText().isEmpty() &&
                    !textFieldCashier.getText().trim().isEmpty() && !textFieldPhoneNumber.getText().isEmpty() &&
                    !textFieldShopName.getText().isEmpty()){
                variablesForRequestProcessing.setPreferences(
                        ((JComponent)e.getSource()).getName(), textFieldToken.getText());

                variablesForRequestProcessing.setPreferences("phone", textFieldPhoneNumber.getText());
                variablesForRequestProcessing.setPreferences("shop", textFieldShopName.getText());
                variablesForRequestProcessing.setPreferences("cashier", textFieldCashier.getText());
                variablesForRequestProcessing.setPreferences("cashbox", textFieldCahboxNo.getText());

                dialog.setVisible(false);
            }else{
                notifyUser("Пустое поле ввода недопустимо!");
            }
        });

        panel.add(label);
        panel.add(textFieldToken);

        panel.add(labelPhone);
        panel.add(textFieldPhoneNumber);

        panel.add(labelShop);
        panel.add(textFieldShopName);

        panel.add(labelCashier);
        panel.add(textFieldCashier);

        panel.add(labelCashbox);
        panel.add(textFieldCahboxNo);

        panel.add(jButton);

        dialog.setModal(true);
        dialog.setSize(400, 400);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        dialog.setContentPane(panel);

        String tokenData = variablesForRequestProcessing.getPreferences("token");
        if (tokenData != null) textFieldToken.setText(tokenData);

        String phoneData = variablesForRequestProcessing.getPreferences("phone");
        if (phoneData != null) textFieldPhoneNumber.setText(phoneData);

        String shopData = variablesForRequestProcessing.getPreferences("shop");
        if (shopData != null) textFieldShopName.setText(shopData);

        String cashierData = variablesForRequestProcessing.getPreferences("cashier");
        if (cashierData != null) textFieldCashier.setText(cashierData);

        String cashboxNumber = variablesForRequestProcessing.getPreferences("cashbox");
        if (cashboxNumber != null) textFieldCahboxNo.setText(cashboxNumber);

        dialog.pack();
        dialog.setVisible(true);
    }
}
