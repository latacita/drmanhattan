package profesor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import comun.BloquesFichero;
import comun.ComienzoExamen;
import comun.DatosAlumno;
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
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object temp = ois.readObject();				
				DatosAlumno da = (DatosAlumno) temp;				
				System.out.println("Alumno: "+da.nombre+" "+da.apellidos+" conectado");

				listaSocket.add(socket);

				/*
				 * TODO
				 * Utilizar el socket creado, crear un hilo para la interaccion, log con su nombre y tal
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

	/**
	 * Enviar un fichero a todos los alumnos conectados
	 * @param ficheroEnviar ruta del fichero a enviar
	 */
	public void envioFichero(File ficheroEnviar){
		try{			

			/*
			 * Se envia un fichero a todos los alumnos conectados.
			 * Recorre la lista de sockets y envia secuencialmente partes del fichero.
			 * 
			 */
			Iterator<Socket> iterador = listaSocket.listIterator();

			//recorrer cada soket de alumnos
			while(iterador.hasNext()){

				Socket s = iterador.next();				

				//si la conexion sigue abierta
				if(!s.isClosed()){
					//enviar todo el fichero

					//indicarle al alumno que vamos a enviar el fichero
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					dos.writeInt(comun.Global.ENVIOFICHERO);

					//obtener el canal de salida
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

					//variable auxiliar para marcar cuando se envía el último mensaje
					boolean enviadoUltimo = false;

					//abrir el fichero
					FileInputStream fis = new FileInputStream(ficheroEnviar);

					//se instancia y rellena un mensaje de envio de fichero
					BloquesFichero bloque = new BloquesFichero();

					bloque.nombreFichero = ficheroEnviar.getName();

					//leer los bytes a enviar
					int leidos = fis.read(bloque.bloque);

					//mientras se lean datos del fichero
					while (leidos > -1){						

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
						//enviar por el socket  
						oos.writeObject(bloque);

						//si es el último mensaje, salimos del bucle
						if (bloque.ultimoBloque){
							break; //TODO intentar quitar este break
						}
						//se crea un nuevo bloque
						bloque = new BloquesFichero();
						bloque.nombreFichero = ficheroEnviar.getName();

						//y se leen sus bytes
						leidos = fis.read(bloque.bloque);
					}

					//si el fichero tenia un tamaño multiplo del numero de bytes que se leen cada vez, el ultimo mensaje 
					//no estara marcado como ultimo, ya que la condicion leidos < bloque.bloque.length, no se cumple
					if (!enviadoUltimo){
						bloque.ultimoBloque = true;
						bloque.datosUtiles = 0;
						oos.writeObject(bloque);
					}
					//en este punto el fichero esta enviado al alumno
					fis.close();

				}//if(!s.isClosed())

			}//while(iterador.hasNext())
		}catch(Exception e){
			//TODO tratar errores
		}
	}

	/**
	 * Enviar a los alumnos conectados la notificacion de que comienza la prueba
	 */
	public void inicioPrueba(boolean temporizar, int minutos){
		try{
			
			/*
			 * Recorre la lista de sockets y envia secuencialmente la notificacion de que se inicia la prueba
			 */
			Iterator<Socket> iterador = listaSocket.listIterator();

			//recorrer cada soket de alumnos
			while(iterador.hasNext()){

				Socket s = iterador.next();				

				//si la conexion sigue abierta
				if(!s.isClosed()){
					//notificar
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					dos.writeInt(comun.Global.COMIENZOEXAMEN);
					//TODO crear hilo para esperar fin prueba
					
					ComienzoExamen ce = new ComienzoExamen();
					ce.examenTemporizado = temporizar;
					ce.minutosExamen = minutos;
					
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					oos.writeObject(ce);
					
				}
			}
			
		}catch(Exception e){
			//TODO tratar errores
		}
	}

}