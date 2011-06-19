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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;

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

	private List<Socket> listaSocket;

	//variables usadas en caso de reconexion
	private int hora;
	private int minutos;
	private boolean temporizado;
	private String dirRes;
	private BigInteger id;
	private boolean pruebaFinalizada;
	private JButton btnFin;

	/**
	 * Constructor
	 */
	public HiloAceptadorAlumnos(JButton fin){
		try{
			//Inicializa las variables
			sSocket = new ServerSocket(Global.PUERTOPROFESOR);
			listaSocket = new LinkedList<Socket>();
			id = new BigInteger(128, new Random(System.currentTimeMillis()));
			pruebaFinalizada = false;
			btnFin = fin;
			this.start();
		}catch(Exception e){			
			e.printStackTrace();
		}
	}


	/**
	 * Mientras este activo espera nuevas conexiones de alumnos
	 */
	public void run(){
		try{
			while(true){
				Socket socket;
				//esperar a nueva conexion
				socket = sSocket.accept();
				new ProcesaConexion(socket, this).start();
				}
		}catch(Exception e){			
			e.printStackTrace();
		}
	}

	/**
	 * Evita que se acepten nuevas conexiones una vez comenzada la prueba
	 */
	private void desconectar(String dirResultados){
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
				new TareaProfesor(s, dirResultados, this);
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

					//variable auxiliar para marcar cuando se envia el ultimo mensaje
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

						//el numero de bytes leidos
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

						//si es el ultimo mensaje, salimos del bucle
						if (bloque.ultimoBloque){
							break;
						}
						//se crea un nuevo bloque
						bloque = new BloquesFichero();
						bloque.nombreFichero = ficheroEnviar.getName();
						bloque.md5 = md5;

						//y se leen sus bytes
						leidos = fis.read(bloque.bloque);
					}

					//si el fichero tenia un tamano multiplo del numero de bytes que se leen cada vez, el ultimo mensaje 
					//no estara marcado como ultimo, ya que la condicion leidos < bloque.bloque.length, no se cumple
					if (!enviadoUltimo){
						bloque.ultimoBloque = true;
						bloque.datosUtiles = 0;
						oos.writeObject(bloque);
					}

					fis.close();
				}//if(!s.isClosed())

			}//while(iterador.hasNext())
			Logger logger = Logger.getLogger("PFC");
			logger.log(Level.INFO, "Fichero "+ficheroEnviar.getAbsolutePath()+" enviado a los alumnos conectados");
		}catch(IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * Clase privada para procesar los datos al aceptar una conexion nueva.
	 * 
	 * @author Manuel Pando
	 *
	 */
	private class ProcesaConexion extends Thread{

		private Socket conexion;
		private HiloAceptadorAlumnos hiloPrincipal;

		public ProcesaConexion(Socket s, HiloAceptadorAlumnos haa){
			conexion = s;
			hiloPrincipal = haa;
		}

		public void run(){

			try{

				DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
				DataInputStream dis = new DataInputStream(conexion.getInputStream());

				boolean reconexion = dis.readBoolean();
				if(!reconexion){
					//la conexion aceptada no es una reconexion
					boolean aceptado = true;

					if(hiloPrincipal.isInterrupted()){
						aceptado = false;
					}

					dos.writeBoolean(aceptado);
					if(aceptado){
						ObjectInputStream ois = new ObjectInputStream(conexion.getInputStream());
						Object temp = ois.readObject();				
						DatosAlumno da = (DatosAlumno) temp;
						Logger logger = Logger.getLogger("PFC");
						logger.log(Level.INFO, "Alumno: "+da.nombre+" "+da.apellidos+" conectado");

						listaSocket.add(conexion);
					}
				}else{
					//hay reconexion

					//dejar la opcion a no permitir reconexiones, de momento se permiten
					//en el siguiente if() se comprueba que sea una reconexion correcta
					dos.writeBoolean(true);

					ObjectInputStream ois = new ObjectInputStream(conexion.getInputStream());
					Object temp = ois.readObject();
					DatosAlumno da = (DatosAlumno) temp;

					//si la id recibida es correcta
					if(da.id.toString().trim().equals(id.toString().trim())){

						//y la prueba no ha finalizado
						if(!pruebaFinalizada){

							Date ahora = new Date(System.currentTimeMillis());
							Date limite = new Date(ahora.getYear(), ahora.getMonth(), ahora.getDate(), hiloPrincipal.hora, hiloPrincipal.minutos, 0);
							long diferencia =  limite.getTime()-ahora.getTime();
							double segundosD = Math.floor(diferencia/1000);
							int segundosI = (int) segundosD;
							int minutosEnteros = segundosI / 60;
							int segundosEnteros = segundosI - 60*minutosEnteros;

							//y queda tiempo en caso de ser temporizado					

							if(temporizado){
								if((minutosEnteros >= 0) && (segundosEnteros>0)){
									//permitir la reconexion
									dos.writeBoolean(true);

									Logger logger = Logger.getLogger("PFC");
									logger.log(Level.INFO, "Alumno: "+da.nombre+" "+da.apellidos+" reconectado");
									//Directamente se reinicia la prueba
									dos.writeInt(Global.COMIENZOPRUEBA);

									ComienzoExamen ce = new ComienzoExamen();
									ce.pruebaTemporizada = temporizado;
									ce.minutosPrueba = minutosEnteros;
									ce.segundosPrueba = segundosEnteros;
									ce.sesion = id;

									ObjectOutputStream oos = new ObjectOutputStream(conexion.getOutputStream());
									oos.writeObject(ce);

									listaSocket.add(conexion);
									new TareaProfesor(conexion, dirRes, hiloPrincipal);
								}else{
									dos.writeBoolean(false);
								}
							}else{
								//permitir la reconexion
								dos.writeBoolean(true);

								Logger logger = Logger.getLogger("PFC");
								logger.log(Level.INFO, "Alumno: "+da.nombre+" "+da.apellidos+" reconectado");
								//Directamente se reinicia la prueba
								dos.writeInt(Global.COMIENZOPRUEBA);

								ComienzoExamen ce = new ComienzoExamen();
								ce.pruebaTemporizada = temporizado;
								ce.minutosPrueba = minutosEnteros;
								ce.segundosPrueba = segundosEnteros;
								ce.sesion = id;

								ObjectOutputStream oos = new ObjectOutputStream(conexion.getOutputStream());
								oos.writeObject(ce);

								listaSocket.add(conexion);
								new TareaProfesor(conexion, dirRes, hiloPrincipal);
							}
						}else{
							dos.writeBoolean(false);
						}

					}else{
						//y si no lo es, denegarla
						dos.writeBoolean(false);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}



	/**
	 * Enviar a los alumnos conectados la notificacion de que comienza la prueba
	 */
	public void inicioPrueba(boolean temporizar, int minutos, String dirResultados, int h, int m, int segundos){
		hora = h;
		this.minutos = m;
		temporizado = temporizar;
		dirRes = dirResultados;
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
					dos.writeInt(comun.Global.COMIENZOPRUEBA);

					ComienzoExamen ce = new ComienzoExamen();
					ce.pruebaTemporizada = temporizar;
					ce.minutosPrueba = minutos;
					ce.segundosPrueba = segundos;					
					ce.sesion = id;

					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					oos.writeObject(ce);

				}
			}
			desconectar(dirResultados);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * Enviar a los alumnos conectados la notificacion de que la prueba acaba
	 */
	public void finPrueba(boolean tiempo){
		try{
			this.pruebaFinalizada = true;
			btnFin.setEnabled(false);

			Logger logger = Logger.getLogger("PFC");
			if(tiempo){
				logger.log(Level.INFO, "El tiempo destinado a la prueba ha finalizado");
			}else{
				logger.log(Level.INFO, "Finaliza la prueba a orden del profesor");
			}

			/*
			 * Recorre la lista de sockets y envia secuencialmente la notificacion de que finaliza la prueba
			 */
			Iterator<Socket> iterador = listaSocket.listIterator();

			//recorrer cada soket de alumnos
			while(iterador.hasNext()){

				Socket s = iterador.next();				

				//si la conexion sigue abierta
				if(!s.isClosed()){
					//notificar
					DataOutputStream dos = new DataOutputStream(s.getOutputStream());
					dos.writeInt(comun.Global.FINPRUEBA);
					s.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}