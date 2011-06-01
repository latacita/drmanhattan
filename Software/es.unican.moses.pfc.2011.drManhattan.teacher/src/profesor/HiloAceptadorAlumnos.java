package profesor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
 * @author Manuel Pando
 *
 */
public class HiloAceptadorAlumnos extends Thread{

	//para aceptar conexiones
	private ServerSocket sSocket;


	List<Socket> listaSocket;

	/**
	 * Constructor
	 */
	public HiloAceptadorAlumnos(){
		try{
			//Inicializa las variables
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
			while(!this.isInterrupted()){
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
				 * --------- Comprobar si desconectamos, no aceptar mas -------------------
				 *  
				 */				
			}
			System.out.println("Salgo del thread");
			
			
		}catch(Exception e){
			//TODO tratamiento de errores
			e.printStackTrace();
		}
	}
	
	/**
	 * Evita que se acepten nuevas conexiones una vez comenzada la prueba
	 */
	public void desconectar(String dirResultados){
		System.out.println("prof: desconectar");
		this.interrupt();
		/*
		 * Con cada uno de los sockets creados a partir de las conexiones de los alumnos
		 * crear un hilo de ejecucion para la finalizacion de la prueba
		 */
		
		Iterator<Socket> iterador = listaSocket.listIterator();

		//recorrer cada soket de alumnos
		while(iterador.hasNext()){

			Socket s = iterador.next();				

			//si la conexion sigue abierta
			if(!s.isClosed()){
				new TareaProfesor(s, dirResultados);
			}
		}
	}
	
	

	

	/**
	 * Enviar un fichero a todos los alumnos conectados
	 * @param ficheroEnviar ruta del fichero a enviar
	 */
	public void envioFichero(File ficheroEnviar){
		try{

			//primero se realiza el md5 al fichero, para no repetirlo cada vez

			MessageDigest digest = MessageDigest.getInstance("MD5");
			FileInputStream is = new FileInputStream(ficheroEnviar);				
			byte[] buffer = new byte[4096];
			int read = 0;

			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			is.close();
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String md5 = bigInt.toString(16);
			System.out.println("MD5: " + md5);

			/*
			 * Se envia un fichero a todos los alumnos conectados.
			 * Recorre la lista de sockets y envia secuencialmente partes del fichero.
			 * 
			 */
			Iterator<Socket> iterador = listaSocket.listIterator();

			//recorrer cada soket de alumnos
			while(iterador.hasNext()){

				//variables utilizadas en caso de que haya que reenviar el fichero por problemas				
				boolean enviar = true;
				int contadorEnvios = 1;
				
				//hasta 3 posibles envios
				while(enviar && contadorEnvios<=3){

					Socket s = iterador.next();				

					//si la conexion sigue abierta
					if(!s.isClosed()){
						//enviar todo el fichero

						//indicarle al alumno que vamos a enviar el fichero
						DataOutputStream dos = new DataOutputStream(s.getOutputStream());
						dos.writeInt(comun.Global.ENVIOFICHERO);

						//obtener el canal de salida
						ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

						//variable auxiliar para marcar cuando se env�a el �ltimo mensaje
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

							//el n�mero de bytes leidos
							bloque.datosUtiles = leidos;

							//si se ha leido menos de lo posible, es porque se ha acabado
							if (leidos < bloque.bloque.length){
								//por tanto es el ultimo mensaje
								bloque.ultimoBloque = true;
								enviadoUltimo = true;
							}else{
								bloque.ultimoBloque = false;
							}

							bloque.md5 = md5;

							//enviar por el socket  
							oos.writeObject(bloque);

							//si es el �ltimo mensaje, salimos del bucle
							if (bloque.ultimoBloque){
								break; //TODO intentar quitar este break
							}
							//se crea un nuevo bloque
							bloque = new BloquesFichero();
							bloque.nombreFichero = ficheroEnviar.getName();
							bloque.md5 = md5;

							//y se leen sus bytes
							leidos = fis.read(bloque.bloque);
						}

						//si el fichero tenia un tama�o multiplo del numero de bytes que se leen cada vez, el ultimo mensaje 
						//no estara marcado como ultimo, ya que la condicion leidos < bloque.bloque.length, no se cumple
						if (!enviadoUltimo){
							bloque.ultimoBloque = true;
							bloque.datosUtiles = 0;
							oos.writeObject(bloque);
						}
						
						//leer boolean para comprobar la integridad del fichero
						DataInputStream dis = new DataInputStream(s.getInputStream());
						boolean rec = dis.readBoolean(); //true si se ha mandado correctamente, false en caso contrario
						enviar = !rec; //variable de control del bucle, true significa que hay que volver a intentar enviarlo
						contadorEnvios++;	
						//en este punto el fichero esta enviado al alumno o hay que vovler a intentarlo
						fis.close();
					}//if(!s.isClosed())
				}//while(enviar && contadorEnvios<=3)

			}//while(iterador.hasNext())
		}catch(IOException e) {
			//TODO error en el fichero
		} catch (NoSuchAlgorithmException e) {
			// TODO error al cargar md5
			e.printStackTrace();
		}
	}

	/**
	 * Enviar a los alumnos conectados la notificacion de que comienza la prueba
	 */
	public void inicioPrueba(boolean temporizar, int minutos, String dirResultados){
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
			desconectar(dirResultados);
		}catch(Exception e){
			//TODO tratar errores
		}
	}

}