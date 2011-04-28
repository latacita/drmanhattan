package profesor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import comun.BloquesFichero;
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


	List<Socket> listaSocket;

	/**
	 * Constructor
	 */
	public HiloAceptadorAlumnos(){
		try{
			//Inicializa las variables
			desconectar = false;
			sSocket = new ServerSocket(Global.PUERTOPROFESOR);

			listaSocket = new LinkedList<Socket>();

			this.start();
		}catch(Exception e){
			//TODO tratamiento de errores
			e.printStackTrace();
		}
	}


	public void run(){
		try{			
			while(!desconectar){
				System.out.println("Desde el thread del servidor, espero cliente");
				Socket socket;

				//esperar a nueva conexion
				socket = sSocket.accept();

				listaSocket.add(socket);

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

	public void envioFichero(File ficheroEnviar){
		try{
			
			System.out.println("p: 0");
			/*Iterator<Socket> iterador = listaSocket.listIterator();

			//recorrer cada soket de alumnos
			while(iterador.hasNext()){

				Socket s = iterador.next();

				//si la conexion sigue abierta
				if(!s.isClosed()){

					DataOutputStream dos = new DataOutputStream(s.getOutputStream());

				}			
			}*/

			/*
			 * Se envia un fichero a todos los alumnos conectados.
			 * Recorre la lista de sockets y envia secuencialmente partes del fichero.
			 * 
			 */

			String fichero = "C:\\IntercontinentalExchange.pdf"; //provisional

			Iterator<Socket> iterador = listaSocket.listIterator();

			//recorrer cada soket de alumnos
			while(iterador.hasNext()){
				
				System.out.println("##############################################################################");
				System.out.println("############################## SIGUIENTE SOCKET ##############################");
				System.out.println("##############################################################################");
				
				Socket s = iterador.next();				

				//si la conexion sigue abierta
				if(!s.isClosed()){
					System.out.println("p: 1");
					//enviar todo el fichero
					
					//indicarle al alumno que vamos a enviar el fichero
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					dos.writeInt(comun.Global.ENVIOFICHERO);

					//obtener el canal de salida
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

					//variable auxiliar para marcar cuando se envía el último mensaje
					boolean enviadoUltimo = false;

					System.out.println("p: 2");
					//abrir el fichero
					FileInputStream fis = new FileInputStream(fichero);

					// Se instancia y rellena un mensaje de envio de fichero
					BloquesFichero bloque = new BloquesFichero();					
					bloque.nombreFichero = fichero;

					//leer los bytes a enviar
					int leidos = fis.read(bloque.bloque);

					//mientras se lean datos del fichero
					while (leidos > -1){						

						System.out.println("p: 3");
						//el número de bytes leidos
						bloque.datosUtiles = leidos;

						//si se ha leido menos de lo posible, es porque se ha acabado
						if (leidos < bloque.bloque.length){
							//por tanto es el ultimo mensaje
							bloque.ultimoBloque = true;
							enviadoUltimo = true;
						}else{
							bloque.ultimoBloque = false;
						}
						System.out.println("p: 4");
						//enviar por el socket  
						oos.writeObject(bloque);

						//si es el último mensaje, salimos del bucle.
						if (bloque.ultimoBloque){
							break; //TODO intentar quitar este break
						}
						System.out.println("p: 5");
						//se crea un nuevo bloque
						bloque = new BloquesFichero();
						bloque.nombreFichero = fichero;

						//y se leen sus bytes
						leidos = fis.read(bloque.bloque);
						System.out.println("p: 6");
					}

					//si el fichero tenia un tamaño multiplo del numero de bytes que se leen cada vez, el ultimo mensaje 
					//no estara marcado como ultimo, ya que la condicion leidos < bloque.bloque.length, no se cumple
					if (!enviadoUltimo){
						bloque.ultimoBloque = true;
						bloque.datosUtiles = 0;
						oos.writeObject(bloque);
					}
					System.out.println("p: 7");
					//en este punto el fichero esta enviado al alumno

				}//if(!s.isClosed())

			}//while(iterador.hasNext())
		}catch(Exception e){
			//TODO tratar errores
		}
	}

}