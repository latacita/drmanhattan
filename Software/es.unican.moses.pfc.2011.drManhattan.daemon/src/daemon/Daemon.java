package daemon;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import comun.Global;

/**
 *
 * Clase que define el comportamiento de un Daemon del sistema capaz de interactuar con IPTables para controlar el acceso a la red.
 * 
 * 
 * @author Manuel Pando
 *
 */
public class Daemon {


	//ServerSocket para escuchar en el puerto definido
	ServerSocket ss;	

	/**
	 * Constructor, crea el daemon.
	 */
	public Daemon(){		
		try {
			ss = new ServerSocket(Global.PUERTODAEMON);
			esperaConexion();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Una vez que el daemon esta creado, este metodo espera a que se cree una conexion.
	 * Una vez la conexion esta establecida, crea un hilo para poder atenderla y seguir escuchando a posibles nuevas conexiones.
	 */
	private void esperaConexion() { 
		try {
			while(true){
				Socket socket = ss.accept();			
				new ProcesoDaemon(socket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Clase privada que representa la tarea a realizar por el daemon cuando se ha establecido una conexion.
	 * 
	 * @author Manuel Pando
	 *
	 */
	private class ProcesoDaemon extends Thread{

		//socket de la conexion
		private Socket socket;

		//canal de entrada
		private DataInputStream dis;


		/**
		 * Constructor
		 * @param s socket de la conexion creada entre el daemon y el programa del alumno
		 */
		public ProcesoDaemon(Socket s){
			try{
				//asigna e inicializa atributos
				socket = s;
				dis = new DataInputStream(socket.getInputStream());

				//comienza el thread
				this.start();
			}catch(IOException e){
				e.printStackTrace();
				//TODO log?
			}
		}


		/**
		 * Las acciones durante la vida del thread.
		 */
		public void run(){
			try {

				//recibir del programa cliente la primera opcion
				int recibido = dis.readInt();

				String comando; //variable utilizada para llamar al sistema y ejecutar ese comando

				while(recibido != Global.FINEXAMEN){
					//realizar las operaciones correspondientes en funcion de la opcion requerida
					switch(recibido){

					//denegar acceso a la red						
					case Global.NORED:
						
						/*
						 * Para denegar el acceso se cambia la politica de los paquetes salientes del PC de modo
						 * que cualquier paquete dirigido a cualquier direccion es descartado.
						 * Con esto se consigue que no se puedan hacer peticiones a otros equipos, pero si recibir
						 * los datos del computador del profesor.
						 */
						
						//ejecutar comando IPtables para denegar la red
						comando = "iptables -P OUTPUT DROP";
						Runtime.getRuntime().exec(comando);
						comando = "iptables -A OUTPUT -s 127.0.0.1 -j ACCEPT";
						Runtime.getRuntime().exec(comando);
						break;
					
					//dar acceso a toda la red
					case Global.SIRED:
						
						//cambiar la politica
						comando = "iptables -P OUTPUT ACCEPT";
						Runtime.getRuntime().exec(comando);
						
						comando = "iptables -D OUTPUT -s 127.0.0.1 -j ACCEPT";
						Runtime.getRuntime().exec(comando);
						break;						
						
					default:
						//TODO error, no deberia llegar aqui
						break;

					}//switch
					recibido = dis.readInt();
				}//while
			}catch(Exception e){
			}

		}
	}
}

