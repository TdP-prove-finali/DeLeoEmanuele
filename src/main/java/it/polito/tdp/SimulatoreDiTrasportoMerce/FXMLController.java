package it.polito.tdp.SimulatoreDiTrasportoMerce;

import java.net.URL;
import javafx.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Model;
import it.polito.tdp.SimulatoreTrasoortoMerce.Model.Simulator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	private Simulator simulatore;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private RadioButton ticTir;

	@FXML
	private TextField volumeTir;

	@FXML
	private TextField pesoTir;

	@FXML
	private TextField velocitaTir;

	@FXML
	private TextField costoTir;

	@FXML
	private Label percentualeTempo;

	@FXML
	private Label percentualeCosto;

	@FXML
	private RadioButton ticAereo;

	@FXML
	private TextField volumeAereo;

	@FXML
	private TextField pesoAereo;

	@FXML
	private TextField velocitaAereo;

	@FXML
	private TextField costoAereo;

	@FXML
	private TextArea outputGrafo;

	@FXML
	private Button btnCreaGrafo;

	@FXML
	private TextField nGiorni;

	@FXML
	private TextField ordiniGiornalieri;

	@FXML
	private Spinner<Integer> oraInizio;

	@FXML
	private Spinner<Integer> oraFine;

	@FXML
	private TextField timeout;

	@FXML
	private Slider percentuale;

	@FXML
	private Slider riempimento;

	@FXML
	private TextArea outputOrdini;

	@FXML
	private TextArea outputGenerale;

	@FXML
	private Label percentuale2;

	@FXML
	private Button btnSimula;

	@FXML
	private ProgressBar myProgressBar;

	@FXML
	private TextArea outputOridneSpecifico;

	@FXML
	private TextField idOrdine;

	@FXML
	private Button btnTracciaOrdine;

	@FXML
	void cercaOrdine(ActionEvent event) {

		outputOridneSpecifico.clear();
		model.tracciaOrdine(Integer.parseInt(idOrdine.getText()));
	}

	@FXML
	void creaGrafo(ActionEvent event) {
		outputGrafo.clear();
		outputGenerale.clear();
		outputOrdini.clear();
		model.generaMezzo("Autobus", Double.parseDouble(pesoTir.getText().replace(",", ".")),
				Double.parseDouble(volumeTir.getText().replace(",", ".")),
				Double.parseDouble(velocitaTir.getText().replace(",", ".")),
				Double.parseDouble(costoTir.getText().replace(",", ".")));

		if (ticAereo.isSelected()) {
			model.generaMezzo("Aereo", Double.parseDouble(pesoAereo.getText().replace(",", ".")),
					Double.parseDouble(volumeAereo.getText()), Double.parseDouble(velocitaAereo.getText()),
					Double.parseDouble(costoAereo.getText()));
		}

		double perc = percentuale.getValue();
		if (percentuale.getValue() == 0) {

			perc = 1;
		}

		if (percentuale.getValue() == 100) {
			perc = 99;
		}

		outputGrafo.appendText(model.creaGrafo(perc));
		btnSimula.setDisable(false);
	}

	@FXML
	void simula(ActionEvent event) {

		outputOridneSpecifico.clear();
		outputOrdini.clear();
		outputGenerale.clear();
		model.clearTableOrdini();
		model.clearTableOrdiniConsegnati();
		this.simulatore = new Simulator();
		simulatore.setnGiorni(Integer.parseInt(nGiorni.getText()));
		simulatore.setTimeout(Integer.parseInt(timeout.getText()));
		simulatore.setnOrdiniGiornalieri(Integer.parseInt(ordiniGiornalieri.getText()));
		simulatore.setDataInizio(LocalDateTime.now().toLocalDate());
		simulatore.setOraInizio(oraInizio.getValue(), 0);
		simulatore.setOraFine(oraFine.getValue(), 0);

		simulatore.init(model.getDijkstra(), model.grafo, model.getMezziConSpecifiche(), model.getMappaCitta(),
				model.getMetropoli(), riempimento.getValue());

		outputOrdini.appendText(model.getOrdini());

		long runStart = System.currentTimeMillis();
		simulatore.run();
		long runEnd = System.currentTimeMillis();

		long runTimeElapsed = runEnd - runStart;
		System.out.println("**** RUN ELAPSED: " + runTimeElapsed / 1000 + " seconds");

		outputGenerale.appendText("Ordini consegnati = " + simulatore.getnOrdiniCompletati() + "\n\n" + "Tir = "
				+ simulatore.getNtir() + "\n" + "Aerei = " + simulatore.getNaerei() + "\n Costo totale="
				+ simulatore.getCostoTotale());

		btnTracciaOrdine.setDisable(false);
		myProgressBar.setProgress(0);
	}

	public void setModel(Model modello) {
		this.model = modello;
	}

	@FXML
	void initialize() {
		assert ticTir != null : "fx:id=\"ticTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert volumeTir != null : "fx:id=\"volumeTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert pesoTir != null : "fx:id=\"pesoTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert velocitaTir != null : "fx:id=\"velocitaTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert costoTir != null : "fx:id=\"costoTir\" was not injected: check your FXML file 'Scene.fxml'.";
		assert ticAereo != null : "fx:id=\"ticAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert volumeAereo != null : "fx:id=\"volumeAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert pesoAereo != null : "fx:id=\"pesoAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert velocitaAereo != null : "fx:id=\"velocitaAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert costoAereo != null : "fx:id=\"costoAereo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputGrafo != null : "fx:id=\"outputGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert nGiorni != null : "fx:id=\"nGiorni\" was not injected: check your FXML file 'Scene.fxml'.";
		assert ordiniGiornalieri != null
				: "fx:id=\"ordiniGiornalieri\" was not injected: check your FXML file 'Scene.fxml'.";
		assert riempimento != null : "fx:id=\"riempimento\" was not injected: check your FXML file 'Scene.fxml'.";
		assert oraInizio != null : "fx:id=\"oraInizio\" was not injected: check your FXML file 'Scene.fxml'.";
		assert oraFine != null : "fx:id=\"oraFine\" was not injected: check your FXML file 'Scene.fxml'.";
		assert timeout != null : "fx:id=\"timeout\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputOrdini != null : "fx:id=\"outputOrdini\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputGenerale != null : "fx:id=\"outputGenerale\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
		assert myProgressBar != null : "fx:id=\"myProgressBar\" was not injected: check your FXML file 'Scene.fxml'.";
		assert outputOridneSpecifico != null
				: "fx:id=\"outputOridneSpecifico\" was not injected: check your FXML file 'Scene.fxml'.";
		assert idOrdine != null : "fx:id=\"idOrdine\" was not injected: check your FXML file 'Scene.fxml'.";
		assert btnTracciaOrdine != null
				: "fx:id=\"btnTracciaOrdine\" was not injected: check your FXML file 'Scene.fxml'.";
		assert percentuale != null : "fx:id=\"percentuale\" was not injected: check your FXML file 'Scene.fxml'.";
		assert percentuale2 != null : "fx:id=\"percentuale2\" was not injected: check your FXML file 'Scene.fxml'.";
		assert percentualeTempo != null
				: "fx:id=\"percentualeTempo\" was not injected: check your FXML file 'Scene.fxml'.";
		assert percentualeCosto != null
				: "fx:id=\"percentualeCosto\" was not injected: check your FXML file 'Scene.fxml'.";
		int initialValueInizio = 8;
		int initialValueFine = 16;
		SpinnerValueFactory<Integer> valueFactoryInizio = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12,
				initialValueInizio);
		SpinnerValueFactory<Integer> valueFactoryFine = new SpinnerValueFactory.IntegerSpinnerValueFactory(12, 23,
				initialValueFine);
		oraInizio.setValueFactory(valueFactoryInizio);
		oraFine.setValueFactory(valueFactoryFine);
		percentualeTempo.setText("" + Math.round(percentuale.getValue()) + "%");
		percentualeCosto.setText("" + Math.round(100.00 - percentuale.getValue()) + "%");
		percentuale2.setText("" + Math.round(riempimento.getValue()) + "%");
		percentuale.valueProperty().addListener((observable, oldValue, newValue) -> {

			percentualeTempo.setText("" + Math.round(percentuale.getValue()) + "%");
			percentualeCosto.setText("" + Math.round(100.00 - percentuale.getValue()) + "%");

		});

		riempimento.valueProperty().addListener((observable, oldValue, newValue) -> {

			percentuale2.setText("" + Math.round(riempimento.getValue()) + "%");

		});

	}
}
