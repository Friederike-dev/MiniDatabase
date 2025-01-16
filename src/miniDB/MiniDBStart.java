package miniDB;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;


public class MiniDBStart extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//die innere Klasse f�r den ActionListener
	class MeinListener implements ActionListener {

		@Override
		public void actionPerformed (ActionEvent e) {
			//wurde auf 'Listenanzeige' geklickt?
			if (e.getActionCommand().equals("liste"))
				//dann die Listenanzeige starten
				anzeigeListe();
			//wurde auf 'Einzelanzeige' geklickt?
			if (e.getActionCommand().equals("einzel"))
				//dann die Einzelanzeige starten
				anzeigeEinzel();
			//wurde auf 'neuer Eintrag' geklickt?
			if (e.getActionCommand().equals("neu"))
				//dann das Anlegen starten
				neuerEintrag();
			//wurde auf 'Beenden' geklickt?
			if (e.getActionCommand().equals("ende"))
				//dann beenden
				beenden();
		}
	}


	//der Konstruktor
	public MiniDBStart(String titel) {
		super(titel);
		//ein FlowLayout
		setLayout (new FlowLayout (FlowLayout.LEFT));

		//die Schaltfl�chen
		JButton liste = new JButton("Listenanzeige");
		liste.setActionCommand("liste");
		JButton einzel = new JButton("Einzelanzeige");
		einzel.setActionCommand("einzel");
		JButton neu = new JButton("Neuer Eintrag");
		neu.setActionCommand("neu");
		JButton beenden = new JButton("Beenden");
		beenden.setActionCommand("ende");

		MeinListener listener = new MeinListener();
		liste.addActionListener(listener);
		einzel.addActionListener(listener);
		neu.addActionListener(listener);
		beenden.addActionListener(listener);

		add(liste);
		add(einzel);
		add(neu);
		add(beenden);

		//Gr��e setzen, Standardverhalten festlegen und anzeigen
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

	//die Dummy-Methoden
	private void anzeigeListe() {
		new ListenAnzeige(this, true);
	}

	private void anzeigeEinzel() {
		new BearbeitenEintrag(this, true);
	}

	private void neuerEintrag() {
		new NeuerEintrag(this, true);
	}

	private void beenden() {
		System.exit(0);
	}

}
