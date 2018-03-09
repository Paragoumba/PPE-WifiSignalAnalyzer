package fr.paragoumba.ppe.wifisignalanalyzer.client;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    public static String ip;
    public static int port;

    private static Socket socket;
    private static DataOutputStream out;

    public static void connect(){

        try {

            socket = new Socket(ip, port);
            out = new DataOutputStream(socket.getOutputStream());

            Thread inThread = new Thread(() -> {

                try(InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                    BufferedReader in = new BufferedReader(isr)){

                    String line;

                    while ((line = in.readLine()) != null){

                        if (!line.startsWith("-")) System.out.println("Not some data.");
                        else {

                            Yaml yaml = new Yaml();
                            Controller.updateSeries((ArrayList<HashMap<String, String>>) yaml.load(line));

                        }
                    }

                } catch (IOException e){

                    e.printStackTrace();

                }

                try {

                    if (socket != null) socket.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }, "Input-Thread");

            inThread.start();

        } catch (IOException e) {

            Controller.state = "Connection failed";
            e.printStackTrace();

        }
    }

    public static void setGap(long gap){

        try {

            out.writeLong(gap);
            out.writeChar('\n');

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void disconnect(){

        try {

            out.close();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}
