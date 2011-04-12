package alumno;

import java.awt.EventQueue;

/**
 * Clase que inicia la aplicacion en el alumno.
 * @author Manuel Pando Muñoz
 *
 */
public class LanzadorAlumno {


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GUIAlumno();					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
