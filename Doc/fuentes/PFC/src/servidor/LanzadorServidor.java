package servidor;

import java.awt.EventQueue;

/**
 * Clase que inicia la aplicacion en el servidor.
 * @author Manuel Pando Mu�oz
 *
 */
public class LanzadorServidor {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GUIServidor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
