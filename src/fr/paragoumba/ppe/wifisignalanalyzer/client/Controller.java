package fr.paragoumba.ppe.wifisignalanalyzer.client;

import fr.paragoumba.ppe.wifisignalanalyzer.server.Clock;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class Controller {

    @FXML
    CheckBox saveDataCheckBox;

    @FXML
    TextField gapTextField;

    @FXML
    TextField pathTextField;

    @FXML
    LineChart<Double, Double> chart;

    @FXML
    Label stateLabel;

    public static String state = "";
    public static boolean saveData = false;

    public void initialize(){

        stateLabel.setText(state);

        gapTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue.matches("\\d*")) {

                gapTextField.setText(newValue.replaceAll("[^\\d]", ""));

            }
        });

        saveDataCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> saveData = newValue);
    }

    public void updateChart(ArrayList<XYChart.Data<Double, Double>> list){

        chart.getData().add(new XYChart.Series<>(FXCollections.observableList(list)));

    }

    @FXML
    public void setGap(){

        String gap = gapTextField.getText();

        gapTextField.setPromptText(gap + "ms");
        gapTextField.setText("");
        Clock.setGap(!gap.equals("") ? Long.parseLong(gap) : 1);

    }
}
