package fr.paragoumba.ppe.wifisignalanalyzer.server;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class WifiDataComputer {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static boolean running = true;
    private static long gap = 1000;

    public static void main(String[] args) {

        try {

            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            socket = serverSocket.accept();

        } catch (IOException e) {

            e.printStackTrace();

        }

        Thread outThread = new Thread(() -> {

            try (PrintStream out = new PrintStream(socket.getOutputStream())) {

                while (running) {

                    StringBuilder fileBuilder = new StringBuilder();

                    try(FileInputStream fis = new FileInputStream(new File(args[1]));
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader br = new BufferedReader(isr)){

                        String line;

                        while ((line = br.readLine()) != null) {

                            fileBuilder.append(line);
                            fileBuilder.append('\n');

                        }

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                    String scan = fileBuilder.toString();

                    //Replace each gap of ten spaces by a void string
                    scan = scan.replaceAll(" {10}", "");

                    //Cutting all chars before "Cell"
                    scan = scan.substring(scan.indexOf("Cell"));

                    //Initializing variables to store data
                    ArrayList<HashMap<String, String>> cells = new ArrayList<>();
                    HashMap<String, String> cellInfos = new HashMap<>();

                    //Split at each line
                    for (String line : scan.split("\n")) {

                        //Ignoring all unwanted lines
                        if (!line.startsWith("IE: ") && !line.startsWith("Extra:") && !line.startsWith("    ") && !line.startsWith("Mode:") && !line.startsWith("Bit Rates:") && !line.startsWith("Encryption key:") && !line.startsWith("Channel:") && !Character.isDigit(line.toCharArray()[0])) {

                            if (!line.startsWith("Cell ")) {

                                if (line.startsWith("Frequency:")) {

                                    cellInfos.put("frequency", line.substring(line.indexOf(":") + 1, line.indexOf(" (") - 4));

                                } else if (line.startsWith("Quality=")) {

                                    cellInfos.put("quality", line.substring(8, line.indexOf("  ") - 3));
                                    cellInfos.put("level", line.substring(line.indexOf("Signal level=") + 13, line.lastIndexOf(" ") - 5));


                                } else if (line.startsWith("ESSID:\"")) {

                                    String ssid = line.substring(7, line.lastIndexOf("\""));
                                    cellInfos.put("ssid", scan.substring(scan.indexOf(ssid) + ssid.length()).contains(ssid) ? ssid + " (Cell " + cellInfos.get("id") + ")" : ssid);

                                }

                            } else {

                                //If line starts with "Cell", we add the old cellInfos map to cells' list and we instantiate it with an empty map

                                if (!cellInfos.isEmpty()) {

                                    cells.add(cellInfos);
                                    cellInfos = new HashMap<>();

                                }

                                cellInfos.put("id", Integer.parseInt(line.substring(5, 7)) + "");
                                cellInfos.put("address", line.substring(line.indexOf("Address: ") + 9));
                            }
                        }
                    }

                    cells.add(cellInfos);

                    //Preventing Yaml to put line breaks in the output if it's <= to 132 chars
                    DumperOptions options = new DumperOptions();
                    options.setWidth(132);

                    Yaml yaml = new Yaml(options);
                    String data = yaml.dump(cells);

                    //Debug
                    System.out.println(data);

                    //Sending data
                    out.print(data);

                    Thread.sleep(gap);

                }

            } catch (IOException | InterruptedException e) {

                e.printStackTrace();

            }

        }, "Output-Thread");

        outThread.start();

        try(InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(isr)) {

            String line;

            while ((line = in.readLine()) != null) {

                if (!line.equals("")) {

                    gap = Long.parseLong(line);

                    //Debug
                    System.out.println("Gap changed to " + gap + "ms");

                }
            }

        } catch (IOException e) {

            e.printStackTrace();

        }

        running = false;

        try {

            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}
