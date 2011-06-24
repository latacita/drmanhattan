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
	private ServerSocket ss;

	Socket anterior;

	/**
	 * Constructor, crea el daemon.
	 */
	public Daemon(){		
		try {
			ss = new ServerSocket(Global.PUERTODAEMON);
			anterior = new Socket();
			esperaConexion();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Una vez que el daemon esta creado, este metodo espera a que se cree una conexion.
	 * Una vez la conexion esta establecida, crea un hilo para poder atenderla. No acepta varias conexiones simultaneas.
	 * 
	 * Al no aceptar varias conexiones se evita que, desde la aplicacion drManhattan-Alumno se desee eliminar el acceso a red
	 * y se conecte otro programa para permitirlo.
	 * 
	 */
	private void esperaConexion() { 
		try {
			while(true){
				Socket socket = ss.accept();
				//si hay una conexion activa				
				if(!anterior.isConnected() || anterior.isClosed()){
					anterior = socket;
					new ProcesoDaemon(socket);
				}else{
					//desecharla
					socket.close();
				}
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

				while(recibido != Global.FINPRUEBA){
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
				socket.close();
			}catch(Exception e){
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		}
	}
}

