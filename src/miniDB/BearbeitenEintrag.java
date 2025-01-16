package miniDB;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

public class BearbeitenEintrag extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//für die Eingabefelder
	private JTextField name, nachname, strasse, plz, ort, telefon;
	//für die Anzeige
	//################################################ für Einsendeaufgabe 1 ###############################################
	private JLabel nummer, datensatzNummer;

	//für die Aktionen
	private MeineAktionen loeschenAct, vorAct, zurueckAct, startAct, endeAct, aktualisierenAct;

	//für die Verbindung
	private Connection verbindung;
	private ResultSet ergebnisMenge;

	//für die Abfrage
	private String sqlAbfrage;


	//innere Klasse für die Fensterereignisse
	class FensterListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);

			//die Datenbankverbindung trennen (!!!)
			//'ergebnisMenge' und 'verbindung' sind Variablen der äußeren Klasse
			try {
				BearbeitenEintrag.this.ergebnisMenge.close();
				BearbeitenEintrag.this.verbindung.close();
				MiniDBTools.schliessenDB("jdbc:derby:adressenDB");
			}
			catch (Exception exc) {
				JOptionPane.showMessageDialog(null, "Problem: \n" + exc.toString());
			}
		}
	}



	//eine innere Klasse für die Aktionen
	class MeineAktionen extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		//der Konstruktor
		public MeineAktionen (String text, ImageIcon icon, String beschreibung, KeyStroke shortcut, String actionText) {
			//Konstruktor der übergeordneten Klass mit dem Text und dem Icon aufrufen
			super(text, icon);
			putValue(SHORT_DESCRIPTION, beschreibung);
			//den Shortcut
			putValue(ACCELERATOR_KEY, shortcut);
			//das ActionCommand
			putValue(ACTION_COMMAND_KEY, actionText);	
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("vor"))
				ganzVor();
			if(e.getActionCommand().equals("zurueck"))
				ganzZurueck();
			if(e.getActionCommand().equals("einenvor"))
				einenVor();
			if(e.getActionCommand().equals("einenzurueck"))
				einenZurueck();
			if(e.getActionCommand().equals("loeschen"))
				loeschen();
			if(e.getActionCommand().equals("aktualisieren"))
				aktualisieren();
		}
	}

	//der Konstruktor der Klasse BearbeitenEintrag
	public BearbeitenEintrag(JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Einträge bearbeiten");

		//wir nehmen ein BorderLayout
		setLayout(new BorderLayout());

		//die Aktionen erstellen
		loeschenAct = new MeineAktionen("Datensatz löschen", new ImageIcon("icons/Delete24.gif"), "Löscht den aktuellen Datensatz", null, "loeschen");

		vorAct = new MeineAktionen("Einen Datensatz weiter", new ImageIcon("icons/Forward24.gif"), "Blättert einen Datensatz weiter", null, "einenvor");

		zurueckAct = new MeineAktionen("Einen Datensatz zurück", new ImageIcon("icons/Back24.gif"), "Blättert einen Datensatz zurück", null, "einenzurueck");

		startAct = new MeineAktionen("Zum ersten Datensatz", new ImageIcon("icons/Front24.gif"), "Geht zum ersten Datensatz", null, "vor");

		endeAct = new MeineAktionen("Zum letzten Datensatz", new ImageIcon("icons/End24.gif"), "Geht zum letzten Datensatz", null, "zurueck");

		aktualisierenAct = new MeineAktionen("Änderungen speichern", new ImageIcon("icons/Save24.gif"), "Speichert Änderungen am Datensatz", null, "aktualisieren");

		//die Symbolleiste oben einfügen
		add(symbolleiste(), BorderLayout.NORTH);

		//die Oberfläche erstellen und einfügen
		add(initGui(), BorderLayout.CENTER);

		//zuerst nehmen wir alle Einträge aus der Tabelle 'adressen'
		sqlAbfrage = "SELECT * FROM adressen";

		//die Datenbankverbindung herstellen und dann die Daten lesen
		initDB();

		//Verbindung mit dem Listener
		//hier vor dem Anzeigen hinzufügen!
		addWindowListener(new FensterListener());

		//packen und anzeigen
		pack();
		setVisible(true);



		//Standardoperation setzen
		//hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	}

	//fügt die Felder in ein Panel ein und liefert das Panel zurück
	private JPanel initGui() {

		JPanel tempPanel = new JPanel();
		//im GridLayout mit 2 Spalten
		tempPanel.setLayout(new GridLayout(0,2));

		//für die Nummer (nur Anzeige)
		tempPanel.add(new JLabel("ID-Nummer:"));
		nummer = new JLabel();
		tempPanel.add(nummer);

		//für die anderen Felder
		tempPanel.add(new JLabel("Vorname:"));
		name = new JTextField();
		tempPanel.add(name);
		tempPanel.add(new JLabel("Nachname:"));
		nachname = new JTextField();
		tempPanel.add(nachname);
		tempPanel.add(new JLabel("Strasse:"));
		strasse = new JTextField();
		tempPanel.add(strasse);
		tempPanel.add(new JLabel("PLZ:"));
		plz = new JTextField();
		tempPanel.add(plz);
		tempPanel.add(new JLabel("Ort:"));
		ort = new JTextField();
		tempPanel.add(ort);
		tempPanel.add(new JLabel("Telefon:"));
		telefon = new JTextField();
		tempPanel.add(telefon);

		//################################################ für Einsendeaufgabe 1 ###############################################
		datensatzNummer = new JLabel();
		tempPanel.add(datensatzNummer);

		//zurückgeben
		return tempPanel;

	}

	//die Symbolleiste erzeugen und zurückgeben
	private JToolBar symbolleiste() {
		JToolBar leiste = new JToolBar();
		//die Symbole über die Aktionen einbauen
		leiste.add(loeschenAct);
		leiste.add(aktualisierenAct);
		//Abstand einbauen
		leiste.addSeparator();
		leiste.add(startAct);
		leiste.add(zurueckAct);
		leiste.add(vorAct);
		leiste.add(endeAct);

		//die komplette Leiste zurückgeben
		return (leiste);
	}

	//die Verbindung zur Datenbank herstellen
	private void initDB() {
		try {
			//Verbindung herstellen und Ergebnismenge beschaffen
			verbindung=MiniDBTools.oeffnenDB("jdbc:derby:adressenDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, sqlAbfrage);
			if(ergebnisMenge.next())
				datenLesen();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//die Methode liest die Daten und schreibt sie in die Felder
	private void datenLesen() {
		try {

			if(ergebnisMenge.getRow()==0) {
				nummer.setText("kein Eintrag");
				name.setText("");
				nachname.setText("");
				strasse.setText("");
				plz.setText("");
				ort.setText("");
				telefon.setText("");
				//################################################ für Einsendeaufgabe 1 ###############################################
				datensatzNummer.setText("");


			}
			else {

				nummer.setText(Integer.toString(ergebnisMenge.getInt(1)));
				name.setText(ergebnisMenge.getString(2));
				nachname.setText(ergebnisMenge.getString(3));
				strasse.setText(ergebnisMenge.getString(4));
				plz.setText(ergebnisMenge.getString(5));
				ort.setText(ergebnisMenge.getString(6));
				telefon.setText(ergebnisMenge.getString(7));
				//################################################ für Einsendeaufgabe 1 ###############################################
				datensatzNummer.setText(datensatzNummerAnzeige());
			}
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//################################################ für Einsendeaufgabe 1 ###############################################
	//die Methode gibt einen String mit dem Text für das Label 'datensatzNummer' zurück
	private String datensatzNummerAnzeige() {
		//Variable für den Rückgabe-String
		String text = new String();
		//Variablen für die Zahl des aktuellen Datensatzes und der Gesamtzahl der Datensätze
		int aktuellerDatensatz, gesamtZahlDatensaetze;
		text = "";

		try {
			//zum Ausgeben in der Console und Testen, da anders als der erste Index bei einem Array die erste Zeile in der Datenbank die Ziffer '1' hat.
			System.out.println(ergebnisMenge.getRow());
	
			//mit getRow() holen wir uns die aktuelle Zeilennummer
			//die erste Zeile, also der erste Datensatz, hat die Zeilennummer 1 - daher braucht man nicht wie bei den Einträgen in einem Array '1' zu addieren
			aktuellerDatensatz = ergebnisMenge.getRow();
			//und wir merken uns die aktuelle Position
			int position = aktuellerDatensatz;

			//dann gehen wir zur ersten Zeile in der Ergebnismenge
			ergebnisMenge.first();
			System.out.println(ergebnisMenge.getRow());
			
			//die erste Zeile hat dabei die Zeilennummer '1'
			gesamtZahlDatensaetze = 1;
			//entweder die gesamtZahlDatensaetze über ergebnisMenge.last() und ergebnisMenge.getRow() abfragen oder wie hier durch ein herkömmliches Durchzählen:
			while(ergebnisMenge.next())
				gesamtZahlDatensaetze++;

			//hier setzen wir die aktuellen Werte für 'aktuellerDatensatz' und 'gesamtZahlDatensaetze' in die Oberfläche ein
			text = "Datensatz " + Integer.toString(aktuellerDatensatz)  + " von " + Integer.toString(gesamtZahlDatensaetze);

			//und wieder zur alten Position gehen
			ergebnisMenge.absolute(position);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
		return text;
	}

	//die Methode geht zum ersten Datensatz
	private void ganzVor() {
		try {
			//ganz nach vorne gehen
			ergebnisMenge.first();
			datenLesen();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}

	//die Methode geht zum letzten Datensatz
	private void ganzZurueck() {

		try {
			//ganz nach hinten gehen
			ergebnisMenge.last();
			datenLesen();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}

	}

	//################################################ für Einsendeaufgabe 2 ###############################################
	//die Methode geht einen Datensatz vor
	private void einenVor() {

		try {

			//wenn der Cursor in der Ergebnismenge bereits beim letzten Eintrag ist, verlassen wir die Methode direkt wieder
			if(ergebnisMenge.isLast())
				return;

			else {

				//gibt es noch einen Datensatz?
				ergebnisMenge.next();
				datenLesen();
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}

	}

	//die Methode geht einen Datensatz zurück
	private void einenZurueck() {

		try {

			//wenn der Cursor in der Ergebnismenge bereits beim ersten Eintrag ist, verlassen wir die Methode direkt wieder
			if(ergebnisMenge.isFirst())
				return;

			else {

				//gibt es noch einen Datensatz davor?
				ergebnisMenge.previous();
				datenLesen();
			}
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}

	}

	//die Methode löscht einen Datensatz
	private void loeschen() {

		try {
			//mit deleteRow() wird ein Eintrag auf null-Werte gesetzt und der Datensatz könnte noch angezeigt werden, mit Null-Werten
			//deswegen schließen wir nach dem löschen die ErgebnisMenge und öffnen sie neu
			//Beim Neuöffnen wollen wir wieder an derselben Stelle sein, daher merken wir uns die Position
			int position = ergebnisMenge.getRow();

			//den Eintrag löschen
			ergebnisMenge.deleteRow();
			//Ergebnismenge schließen
			ergebnisMenge.close();
			//und neu öffnen
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, sqlAbfrage);
			//und wieder zur alten Position gehen
			ergebnisMenge.absolute(position);

			//stehen wir jetzt hinter dem letzten Datensatz? (z.B. weil wir den letzten Datensatz gelöscht haben)
			if(ergebnisMenge.isAfterLast())
				//dann zum letzten gehen (also vor den letzten, damit der dann gelesen werden kann)
				ergebnisMenge.last();
			//die Daten neu lesen
			datenLesen();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
			//gibt es nur einen Datensatz und der wird gelöscht, würde hier eine Ausnahme passieren
			// aber deswegen wurden in datenLesen() noch eine Überprüfung ob es einen Datensatz gibt eingefügt
		}

	}

	//die Methode aktualisiert einen Eintrag
	private void aktualisieren() {

		try {
			//wir müssen uns merken wo wir sind
			int position = ergebnisMenge.getRow();

			//die Daten aktualisieren
			ergebnisMenge.updateString(2, name.getText());
			ergebnisMenge.updateString(3, nachname.getText());
			ergebnisMenge.updateString(4, strasse.getText());
			ergebnisMenge.updateString(5, plz.getText());
			ergebnisMenge.updateString(6, ort.getText());
			ergebnisMenge.updateString(7, telefon.getText());

			//den Datensatz aktualisieren
			ergebnisMenge.updateRow();

			//Ergebnismenge schließen
			ergebnisMenge.close();
			//und neu öffnen
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, sqlAbfrage);
			//und wieder zur alten Position gehen
			ergebnisMenge.absolute(position);
			//die Daten neu lesen
			datenLesen();

		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}

	}


}
