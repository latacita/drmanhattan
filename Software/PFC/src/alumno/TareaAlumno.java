package alumno;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
	String dirEnunciado;

	/**
	 * Constructor
	 * @param ip  direcion del computador del profesor
	 * @param directorioEnunciado directorio donde guardar los ficheros enviados por el profesor
	 */
	public TareaAlumno(String ip, String directorioEnunciado){		
		try {
			//creacion del socket e inicio del hilo de ejecucion
			socketAlumno = new Socket(ip, comun.Global.PUERTOPROFESOR);
			dirEnunciado = directorioEnunciado;
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
										
					File archivo = new File ("C:\\cococo.txt");
					archivo.createNewFile();
					
					if(archivo.exists()){
						archivo.delete();
						System.out.println("borrado");
					}else{
						System.out.println("no existia");
					}
					
										
					//crear el flujo de salida para guardar el fichero
					//como inicialmente no se conoce ni el nombre ni la extension
					//se deja general, al finalizar el envio, se cambia
					
					File enunciado;
					FileOutputStream fos;
					
					if(dirEnunciado.charAt(dirEnunciado.length()-1) == File.separatorChar){
						enunciado = new File(dirEnunciado+"temporal");
					}else{
						enunciado = new File(dirEnunciado+File.separator+"temporal");
						
					}
					System.out.println("Fichero enunciado: "+enunciado.getAbsolutePath());
					//si el fichero no existia fisicamente, crearlo para poder volcar los datos
					if(!enunciado.exists()){
						enunciado.createNewFile();
					}
					fos = new FileOutputStream(enunciado);
					
					
					BloquesFichero bloque;

					System.out.println("a: 1");
					do
					{
						//leer el bloque recibido
						Object mensajeAux = ois.readObject();

						//que ha de ser lo que se esta esperando
						if (mensajeAux instanceof BloquesFichero){
							bloque = (BloquesFichero) mensajeAux;
							// Se escribe en el fichero
							//System.out.print(new String(bloque.bloque, 0, bloque.datosUtiles));
							fos.write(bloque.bloque, 0, bloque.datosUtiles);
						}else{
							//TODO tratar error, el mensaje no es del tipo esperado
							break;

						}
						System.out.println("a: 2");
					} while (!bloque.ultimoBloque);
					
					//TODO rename al fichero
					
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
