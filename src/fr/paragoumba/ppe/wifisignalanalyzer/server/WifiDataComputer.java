package fr.paragoumba.ppe.wifisignalanalyzer.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class WifiDataComputer {

    private static boolean running = true;
    private static long gap = 10000;

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            Socket socket = serverSocket.accept();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            Thread outThread = new Thread(() -> {

                long start = System.currentTimeMillis();

                while (running) {

                    if (System.currentTimeMillis() - start >= gap) {

                        //Debug
                        System.out.println(System.currentTimeMillis() - start + ">=" + gap);

                        StringBuilder fileBuilder = new StringBuilder();

                        try(FileInputStream fis = new FileInputStream(new File(args[1]));
                            InputStreamReader isr = new InputStreamReader(fis);
                            BufferedReader br = new BufferedReader(isr)) {

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

                            //Debug
                            System.out.println(line);

                            //Ignoring all unwanted lines
                            if (!line.startsWith("IE: ") && !line.startsWith("Extra:") && !line.startsWith("    ") && !line.startsWith("Mode:") && !line.startsWith("Bit Rates:") && !line.startsWith("Encryption key:") && !line.startsWith("Channel:") && !Character.isDigit(line.toCharArray()[0])) {

                                if (!line.startsWith("Cell ")) {

                                    if (line.startsWith("Frequency:")) {

                                        cellInfos.put("Frequency", line.substring(line.indexOf(":") + 1, line.indexOf(" (")));

                                    } else if (line.startsWith("Quality=")) {

                                        cellInfos.put("Quality", line.substring(8, line.indexOf("  ")));
                                        cellInfos.put("Level", line.substring(line.indexOf("Signal level=") + 13, line.lastIndexOf(" ")));

                                    } else if (line.startsWith("ESSID:\"")) {

                                        cellInfos.put("SSID", line.substring(7, line.lastIndexOf("\"")));

                                    }

                                } else {

                                    //If line starts with "Cell", we add the old cellInfos map to cells' list and we instantiate it with an empty map

                                    if (!cellInfos.isEmpty()) {

                                        cells.add(cellInfos);
                                        cellInfos = new HashMap<>();

                                    }

                                    cellInfos.put("Cell", line.substring(5, 7));
                                    cellInfos.put("Address", '"' + line.substring(line.indexOf("Address: ") + 9) + '"');
                                }
                            }
                        }

                        cells.add(cellInfos);

                        //Sending data
                        out.println(cells);

                        //Setting start time
                        start = System.currentTimeMillis();
                    }
                }

            }, "Output-Thread");

            System.out.println("On attend pas Patrick ?");

            Thread inThread = new Thread(() -> {

                try {

                    String line;

                    //Debug
                    System.out.println(in.ready());

                    while ((line = in.readLine()) != null) {

                        if (line.length() == 0) {

                            out.println("\u2301");
                            running = false;
                            break;

                        }

                        gap = Long.parseLong(line);

                        //Debug
                        System.out.println("Gap changed to " + gap + "ms");
                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }, "Input-Thread");

            outThread.start();
            inThread.start();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}
