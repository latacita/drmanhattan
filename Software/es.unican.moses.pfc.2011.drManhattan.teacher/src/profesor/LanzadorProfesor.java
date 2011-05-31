package profesor;

import java.awt.EventQueue;

/**
 * Clase que inicia la aplicacion del profesor.
 * @author Manuel Pando
 *
 */
public class LanzadorProfesor {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GUIProfesor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
