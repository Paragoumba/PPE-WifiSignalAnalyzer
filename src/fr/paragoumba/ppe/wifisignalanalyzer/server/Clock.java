package fr.paragoumba.ppe.wifisignalanalyzer.server;

public class Clock extends Thread {

    private static Clock clock;
    private static long gap = 1;
    private static boolean running = true;

    private Clock(){

        super("Thread-Clock");

    }

    @Override
    public void run() {


        while (running){

            try {

                Thread.sleep(gap);

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

            //WifiSignalDisplayer.getData(null);
        }
    }

    public static void setGap(long gap){

        Clock.gap = gap;
        System.out.println("New Gap : " + gap + "ms");

    }

    public static void init(){

        clock = new Clock();
        clock.start();

    }

    public static void halt(){

        running = false;

    }
}
