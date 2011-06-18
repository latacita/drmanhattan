package profesor;

import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JOptionPane;


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
		/*
		 * Si hay una instancia de la aplicacion ejecutandose no se inicia, se recibe el PID
		 * en el argumento
		 */
		if(args.length == 0){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new GUIProfesor();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}else{
			JOptionPane.showMessageDialog(new JLabel(), "La aplicación ya está en marcha, PID: "+args[0]);
		}

	}

}
