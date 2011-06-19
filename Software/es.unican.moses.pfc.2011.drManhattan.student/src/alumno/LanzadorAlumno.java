/*
Dr Manhattan is a network access control software to control and organize exams.

Copyright (C) 2011  Manuel Pando Muñoz

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package alumno;

import java.awt.EventQueue;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Clase que inicia la aplicacion en el alumno.
 * @author Manuel Pando
 *
 */
public class LanzadorAlumno {


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
						new GUIAlumno();			
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
