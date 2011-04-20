package alumno;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * Clase que representa la actividad a realizar por la aplicación del alumno.
 * 
 * @author Manuel Pando Muñoz
 *
 */
public class TareaAlumno extends Thread{

	Socket socketAlumno;

	/**
	 * Constructor
	 * @param ip  direcion del computador del profesor
	 */
	public TareaAlumno(String ip){		
		try {
			//creacion del socket e inicio del hilo de ejecucion
			socketAlumno = new Socket(ip, comun.Global.PUERTOPROFESOR);
			this.start();			

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	public void run(){

		try {
			DataInputStream dis = new DataInputStream(socketAlumno.getInputStream());
			DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());  
			
			//recibir opcion del profesor
			int recibido = dis.readInt();
			
			System.out.println("Alumno recibe: " + recibido);
			
			//mientras no se acabe el examen
			while(recibido != comun.Global.FINEXAMEN){
				
				switch (recibido) {
				//opcion de recibir un fichero
				case comun.Global.ENVIOFICHERO:

					//TODO recibir fichero

					break;					

				default:
					break;
				}
				recibido = dis.readInt();
				System.out.println("Alumno recibe: " + recibido);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}	

}
