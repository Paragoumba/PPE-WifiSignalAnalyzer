package fr.paragoumba.ppe.wifisignalanalyzer.client;

public class Cell {

    public Cell(int id, String macAddress, String ssid, double quality, double frequence, int level){

        this.id = id;
        this.macAddress = macAddress;
        this.ssid = ssid;
        this.quality = quality;
        this.frequence = frequence;
        this.level = level;

    }

    private int id;
    private String macAddress;
    private String ssid;
    private double quality;
    private double frequence;
    private int level;

    public int getId(){

        return id;

    }

    public String getMacAddress() {

        return macAddress;

    }

    public String getSsid() {

        return ssid;

    }

    public double getQuality() {

        return quality;

    }

    public double getFrequence() {

        return frequence;

    }

    public int getLevel() {

        return level;

    }

    public void setId(int id) {

        this.id = id;

    }

    public void setMacAddress(String macAddress) {

        this.macAddress = macAddress;

    }

    public void setSsid(String ssid) {

        this.ssid = ssid;

    }

    public void setQuality(double quality) {

        this.quality = quality;

    }

    public void setFrequence(double frequence) {

        this.frequence = frequence;

    }

    public void setLevel(int level) {

        this.level = level;

    }
}
