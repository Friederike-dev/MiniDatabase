package miniDB;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class NeuerEintrag extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//für die Eingabefelder
	private JTextField name, nachname, strasse, plz, ort, telefon;
	//für die Schaltflächen
	private JButton ok, abbrechen;
	
	//die innere Klasse für den ActionListener
	class NeuListener implements ActionListener {
		@Override
		public void actionPerformed (ActionEvent e) {
			//wurde auf OK geklickt?
			if(e.getActionCommand().equals("ok"))
				//dann die Daten übernehmen
				uebernehmen();
			
			//wurde auf Abbrechen geklickt?
			if (e.getActionCommand().equals("abbrechen"))
				//dann den Dialog schließen
				dispose();
		}
	}
	
	//der Konstruktor
	public NeuerEintrag (JFrame parent, boolean modal) {
		super(parent, modal);
		setTitle("Neuer Eintrag");
		//die Oberfläche erstellen
		initGui();
		
		//Standardoperation setzen
		//hier den Dialog ausblenden und löschen
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void initGui() {
		setLayout (new GridLayout(0,2));
		//für die Eingabe
		add(new JLabel("Vorname:"));
		name = new JTextField();
		add(name);
		add(new JLabel("Nachname:"));
		nachname = new JTextField();
		add(nachname);
		add(new JLabel("Strasse:"));
		strasse = new JTextField();
		add(strasse);
		add(new JLabel("Plz:"));
		plz = new JTextField();
		add(plz);
		add(new JLabel("Ort:"));
		ort = new JTextField();
		add(ort);
		add(new JLabel("Telefon:"));
		telefon = new JTextField();
		add(telefon);
		
		//die Schaltflächen
		ok = new JButton("OK");
		ok.setActionCommand("ok");
		abbrechen = new JButton("Abbrechen");
		abbrechen.setActionCommand("abbrechen");
		
		NeuListener listener = new NeuListener();
		ok.addActionListener(listener);
		abbrechen.addActionListener(listener);
		
		add(ok);
		add(abbrechen);
		
		//packen und anzeigen
		pack();
		setVisible(true);
		
	}

	
	//die Methode legt einen neuen Datensatz an
	public void uebernehmen() {
		Connection verbindung;
		ResultSet ergebnisMenge;
		try {
			//Verbindung herstellen und Ergebnismenge beschaffen
			//hier nutzen wir unsere statischen Funktionen die wir in MiniDBTools erstellt haben
			verbindung = MiniDBTools.oeffnenDB("jdbc:derby:adressenDB");
			ergebnisMenge = MiniDBTools.liefereErgebnis(verbindung, "SELECT * FROM adressen");
			//zur "Einfüge-Zeile" bewegen
			ergebnisMenge.moveToInsertRow();
			
			//die Nummer in der 1. Spalte (laufende Nummer) wird automatisch gesetzt
			//angegeben werden die Nummer der Spalte und der neue Wert
			//es sind auch leere Einträge möglich, genaugenommen ist es dann ein leerer String (also nicht 'null')
			ergebnisMenge.updateString(2,  name.getText());
			ergebnisMenge.updateString(3,  nachname.getText());
			ergebnisMenge.updateString(4,  strasse.getText());
			ergebnisMenge.updateString(5,  plz.getText());
			ergebnisMenge.updateString(6,  ort.getText());
			ergebnisMenge.updateString(7,  telefon.getText());
			
			//und die neue Zeile einfügen
			ergebnisMenge.insertRow();
			//Ergebnismenge und Verbindung schließen
			ergebnisMenge.close();
			verbindung.close();
			//und das Datenbanksystem auch
			MiniDBTools.schliessenDB("jdbc:derby:adressenDB");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Problem: \n" + e.toString());
		}
	}
	
	
	
}
