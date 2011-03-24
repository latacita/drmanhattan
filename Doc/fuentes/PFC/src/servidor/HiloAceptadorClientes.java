package servidor;

import java.net.ServerSocket;
import java.net.Socket;
import comun.Global;

/**
 * 
 * Clase encargada de aceptar nuevos clientes.
 * 
 * @author Manuel Pando Mu�oz
 *
 */
public class HiloAceptadorClientes extends Thread{

	//para aceptar conexiones
	private ServerSocket sSocket;

	//con valor true se dejan de aceptar clientes nuevos
	private boolean desconectar;

	/**
	 * Constructor
	 */
	public HiloAceptadorClientes(){
		try{
			//Inicializa las variables
			desconectar = false;
			sSocket = new ServerSocket(Global.PUERTOSERVIDOR);
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
				 * Utilizar el socket creado, crear un hilo para la interaccion entre cliente y servidor
				 *  
				 */				
			}
		}catch(Exception e){
			//TODO tratamiento de errores
			e.printStackTrace();
		}
	}
	
}