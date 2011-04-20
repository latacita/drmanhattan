package alumno;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import comun.BloquesFichero;

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

					System.out.println("a: 0");
					//TODO recibir fichero

					ObjectInputStream ois = new ObjectInputStream(socketAlumno.getInputStream());

					FileOutputStream fos = new FileOutputStream("C:\\copia.pdf"); //provisional

					BloquesFichero bloque;

					System.out.println("a: 1");
					do
					{
						//leer el bloque recibido
						Object mensajeAux = ois.readObject();

						//que ha de ser lo que se esta esperando
						if (mensajeAux instanceof BloquesFichero){
							bloque = (BloquesFichero) mensajeAux;
							// Se escribe en pantalla y en el fichero
							System.out.print(new String(bloque.bloque, 0, bloque.datosUtiles));
							fos.write(bloque.bloque, 0, bloque.datosUtiles);
						}else{
							//TODO tratar error, el mensaje no es del tipo esperado
							break;

						}
						System.out.println("a: 2");
					} while (!bloque.ultimoBloque);

					fos.close();
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}	

}
