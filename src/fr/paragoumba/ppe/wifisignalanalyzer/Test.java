package fr.paragoumba.ppe.wifisignalanalyzer;

public class Test {

    public static void main(String[] args) {

        Thread numberThread = new Thread(() -> {

            for (int i = 0; i != 26; i++){

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(i);

            }

        });

        Thread alphaThread = new Thread(() -> {

            char[] alpha = "abcdefghijklmnopqrstuvwxyz".toCharArray();

            for (char c : alpha) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(c);

            }

        });

        numberThread.start();
        alphaThread.start();
    }
}
