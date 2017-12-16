package fr.paragoumba.ppe.wifisignalanalyzer.client;

import fr.paragoumba.ppe.wifisignalanalyzer.server.Clock;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class WifiSignalDisplayer extends Application {

    private static String dataPath;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Client.ip = getParameters().getNamed().get("ip");
        Client.port = Integer.parseInt(getParameters().getNamed().get("port"));

        Client.connect();

        dataPath = getParameters().getNamed().get("dataPath");
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Rectangle2D bounds = Screen.getPrimary().getBounds();

        primaryStage.setScene(new Scene(root, bounds.getWidth(), bounds.getHeight()));
        primaryStage.getIcons().add(new Image("wifi.png"));
        primaryStage.setFullScreenExitHint("");
        primaryStage.setTitle("WifiSignalAnalyzer");
        primaryStage.show();
    }

    @Override
    public void stop(){

        Client.running = false;

        Clock.halt();
        Client.disconnect();

    }

    public static void getData(ArrayList<HashMap<String, String>> list){

        if (Controller.saveData){

            writeData(list);

        }

        ArrayList<Cell> data = new ArrayList<>();

        for (HashMap<String, String> map : list){

            Cell cell = new Cell(Integer.parseInt(map.get("id")), map.get("address"), map.get("ssid"), Double.parseDouble(map.get("quality")), Double.parseDouble(map.get("frequence")), Integer.parseInt(map.get("level")));
            data.add(cell);

        }

        System.out.println("Getting data. (" + calculateWatt(data.get(0).getLevel()));

    }

    private static void writeData(ArrayList<HashMap<String, String>> data) {

        try(FileOutputStream fos = new FileOutputStream(new File(dataPath + LocalDateTime.now().getYear() + '-' + LocalDateTime.now().getMonthValue() + '-' + LocalDateTime.now().getDayOfMonth() + ' ' + LocalDateTime.now().getHour() + LocalDateTime.now().getMinute() + LocalDateTime.now().getSecond() + ".txt"));
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw)){

            bw.write(data.toString().replaceAll("=", ": "));

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    private static double calculateWatt(double dbm){

        /*dbm = 10 * Math.log10(watt) + 30;
        dbm / 10 - 3 = Math.log10(watt);*/

        return Math.pow(10, dbm / 10 - 3);

    }
}
