package it.polito.tdp.SimulatoreDiTrasportoMerce;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Model;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Simulator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	private Simulator simulatore;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="ticTir"
	private RadioButton ticTir; // Value injected by FXMLLoader

	@FXML // fx:id="ticAereo"
	private RadioButton ticAereo; // Value injected by FXMLLoader

	@FXML // fx:id="volumeTir"
	private TextField volumeTir; // Value injected by FXMLLoader

	@FXML // fx:id="volumeAereo"
	private TextField volumeAereo; // Value injected by FXMLLoader

	@FXML // fx:id="pesoTir"
	private TextField pesoTir; // Value injected by FXMLLoader

	@FXML // fx:id="pesoAereo"
	private TextField pesoAereo; // Value injected by FXMLLoader

	@FXML // fx:id="velocitaTir"
	private TextField velocitaTir; // Value injected by FXMLLoader

	@FXML // fx:id="velocitaAereo"
	private TextField velocitaAereo; // Value injected by FXMLLoader

	@FXML // fx:id="btnCreaGrafo"
	private Button btnCreaGrafo; // Value injected by FXMLLoader

	@FXML // fx:id="costoTir"
	private TextField costoTir; // Value injected by FXMLLoader

	@FXML // fx:id="costoAereo"
	private TextField costoAereo; // Value injected by FXMLLoader

	@FXML // fx:id="outputGrafo"
	private TextArea outputGrafo; // Value injected by FXMLLoader

	@FXML // fx:id="ordiniGiornalieri"
	private TextField ordiniGiornalieri; // Value injected by FXMLLoader

	@FXML // fx:id="nGiorni"
	private TextField nGiorni; // Value injected by FXMLLoader

	@FXML // fx:id="oraInizio"
	private Spinner<?> oraInizio; // Value injected by FXMLLoader

	@FXML // fx:id="oraFine"
	private Spinner<?> oraFine; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimula"
	private Button btnSimula; // Value injected by FXMLLoader

	@FXML // fx:id="timeout"
	private TextField timeout; // Value injected by FXMLLoader

	@FXML // fx:id="outputOrdini"
	private TextArea outputOrdini; // Value injected by FXMLLoader

	@FXML // fx:id="outputGenerale"
	private TextArea outputGenerale; // Value injected by FXMLLoader

	@FXML // fx:id="myProgressBar"
	private ProgressBar myProgressBar; // Value injected by FXMLLoader

	@FXML // fx:id="idOrdine"
	private TextField idOrdine; // Value injected by FXMLLoader

	@FXML // fx:id="outputOridneSpecifico"
	private TextArea outputOridneSpecifico; // Value injected by FXMLLoader

	@FXML // fx:id="btnTracciaOrdine"
	private Button btnTracciaOrdine; // Value injected by FXMLLoader

	@FXML
	void cercaOrdine(ActionEvent event) {

		outputOridneSpecifico.clear();
		outputOridneSpecifico.appendText(model.tracciaOrdine(Integer.parseInt(idOrdine.getText())));
	}

	@FXML
	void creaGrafo(ActionEvent event) {
		outputGrafo.clear();
		model.generaMezzo("Autobus", Double.parseDouble(pesoTir.getText().replace(",", ".")),
				Double.parseDouble(volumeTir.getText().replace(",", ".")),
				Double.parseDouble(velocitaTir.getText().replace(",", ".")),
				Double.parseDouble(costoTir.getText().replace(",", ".")));

		if (ticAereo.isSelected()) {
			model.generaMezzo("Aereo", Double.parseDouble(pesoAereo.getText().replace(",", ".")),
					Double.parseDouble(volumeAereo.getText()), Double.parseDouble(velocitaAereo.getText()),
					Double.parseDouble(costoAereo.getText()));
		}

		outputGrafo.appendText(model.creaGrafo());
		btnSimula.setDisable(false);
	}

	@FXML
	void simula(ActionEvent event) {
		outputOrdini.clear();
		outputGenerale.clear();
		model.clearTableOrdini();
		model.clearTableOrdiniConsegnati();
		this.simulatore = new Simulator();
		simulatore.setnGiorni(Integer.parseInt(nGiorni.getText()));
		simulatore.setnOrdiniGiornalieri(Integer.parseInt(ordiniGiornalieri.getText()));
		simulatore.setDataInizio(LocalDateTime.now().toLocalDate());
		simulatore.setTimeout(1);
		simulatore.setOraInizio(8, 0);
		simulatore.setOraFine(20, 0);
		simulatore.init(model.getDijkstra(), model.grafo, model.getMezziConSpecifiche(), model.getMappaCitta().values(),
				model.getMetropoli());
		outputOrdini.appendText(model.getOrdini());
		simulatore.run();
		outputGenerale.appendText("Ordini consegnati = " + simulatore.getnOrdiniCompletati() + "\n\n" + "Tir = "
				+ simulatore.getnTir() + "\n\n" + "Aerei = " + simulatore.getNnAerei());
		btnTracciaOrdine.setDisable(false);
	}

	public void setModel(Model modello) {
		this.model = modello;
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert ticTir != null : "fx:id=\"ticTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert ticAereo != null : "fx:id=\"ticAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert volumeTir != null : "fx:id=\"volumeTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert volumeAereo != null : "fx:id=\"volumeAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert pesoTir != null : "fx:id=\"pesoTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert pesoAereo != null : "fx:id=\"pesoAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert velocitaTir != null : "fx:id=\"velocitaTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert velocitaAereo != null : "fx:id=\"velocitaAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert costoTir != null : "fx:id=\"costoTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert costoAereo != null : "fx:id=\"costoAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputGrafo != null : "fx:id=\"outputGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert ordiniGiornalieri != null
				: "fx:id=\"ordiniGiornalieri\" was not injected: check your FXML file 'Scene.fxml'.";
		assert nGiorni != null : "fx:id=\"nGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
		assert oraInizio != null : "fx:id=\"oraInizio\" was not injected: check your FXML file 'Scene.fxml'.";
		assert oraFine != null : "fx:id=\"oraFine\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
		assert timeout != null : "fx:id=\"timeout\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputOrdini != null : "fx:id=\"outputOrdini\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputGenerale != null : "fx:id=\"outputGenerale\" was not injected: check your FXML file 'Scene.fxml'.";
		assert myProgressBar != null : "fx:id=\"myProgressBar\" was not injected: check your FXML file 'Scene.fxml'.";
		assert idOrdine != null : "fx:id=\"idOrdine\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputOridneSpecifico != null
				: "fx:id=\"outputOridneSpecifico\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnTracciaOrdine != null
				: "fx:id=\"btnTracciaOrdine\" was not injected: check your FXML file 'Scene.fxml'.";

	}
}
