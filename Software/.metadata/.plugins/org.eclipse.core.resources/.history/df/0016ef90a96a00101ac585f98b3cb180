package profesor;

import java.net.ServerSocket;
import java.net.Socket;
import comun.Global;

/**
 * 
 * Clase encargada de aceptar nuevos alumnos.
 * 
 * @author Manuel Pando Muñoz
 *
 */
public class HiloAceptadorAlumnos extends Thread{

	//para aceptar conexiones
	private ServerSocket sSocket;

	//con valor true se dejan de aceptar alumnos nuevos
	private boolean desconectar;

	/**
	 * Constructor
	 */
	public HiloAceptadorAlumnos(){
		try{
			//Inicializa las variables
			desconectar = false;
			sSocket = new ServerSocket(Global.PUERTOPROFESOR);
			this.start();
		}catch(Exception e){
			//TODO tratamiento de errores
			e.printStackTrace();
		}
	}


	public void run(){
		try{			
			while(!desconectar){
				Socket socket;

				//esperar a nueva conexion
				socket = sSocket.accept();

				/*
				 * TODO
				 * Utilizar el socket creado, crear un hilo para la interaccion
				 * 
				 * Comprobar si desconectamos
				 *  
				 */				
			}
		}catch(Exception e){
			//TODO tratamiento de errores
			e.printStackTrace();
		}
	}
	
}