package fr.paragoumba.ppe.wifisignalanalyzer.client;

import javafx.application.Platform;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    public static String ip;
    public static int port;
    private static Socket socket;
    public static boolean running = true;
    private static long gap = 0;

    public static void connect(){

        try {

            socket = new Socket(ip, port);

            new Thread(() -> {

                try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    while (running) {

                        if (gap != 0) {

                            out.println(gap);
                            gap = 0;

                        }
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }, "Output-Thread").start();

            new Thread(() -> {

                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String line;
                    Yaml yaml = new Yaml();

                    while ((line = in.readLine()) != null) {

                        Object o = yaml.load(line);

                        if (o instanceof String){

                            System.out.println(o);

                        } else if (o instanceof ArrayList){

                            WifiSignalDisplayer.getData((ArrayList<HashMap<String, String>>) o);

                        }
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }, "Input-Thread").start();

        } catch (IOException e) {

            Controller.state = "Connection failed";
            e.printStackTrace();

        }
    }

    public static void disconnect(){

        try {

            running = false;

            if (socket != null) {

                socket.close();

            }

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static void setGap(long gap){

        Client.gap = gap;

    }
}
