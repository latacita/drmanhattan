package cliente;

import java.awt.EventQueue;

/**
 * Clase que inicia la aplicacion en el cliente.
 * @author Manuel Pando Muñoz
 *
 */
public class LanzadorCliente {


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GUICliente();					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
