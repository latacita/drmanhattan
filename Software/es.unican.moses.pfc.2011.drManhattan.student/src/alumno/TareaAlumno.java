package alumno;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Cipher;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import alumno.GUIAlumno.CuentaTiempo;

import comun.BloquesFichero;
import comun.ComienzoExamen;
import comun.DatosAlumno;
import comun.Global;

/**
 * 
 * Clase que representa la actividad a realizar por la aplicacion del alumno.
 * 
 * @author Manuel Pando
 *
 */
public class TareaAlumno extends Thread{

	private Socket socketAlumno;
	private String dirEnunciado;
	private JLabel estado;
	private CuentaTiempo ct;
	private Socket socketDaemon;
	private DatosAlumno datos;
	private DataInputStream dis;
	private String ipProfesor;
	private int estadoPaquete;
	private JButton bFin;
	private JButton bRes;



	/**
	 * 
	 * @param ip direcion del computador del profesor
	 * @param directorioEnunciado directorio donde guardar los ficheros enviados por el profesor
	 * @param estado Etiqueta para notificar el estado de la aplicacion
	 * @param da datos del alumno
	 * @param ct variable usada para notificar el tiempo restante en la prueba
	 * @param reconectar booleano true indica que nos estamos reconectando con la prueba ya iniciada
	 * @param fin boton de la GUI del alumno que habra que activar al comenzar la prueba
	 * @param resultados boton de la GUI del alumno que habra que activar al comenzar la prueba
	 * @throws Exception 
	 */
	public TareaAlumno(String ip, String directorioEnunciado, JLabel estado, DatosAlumno da, CuentaTiempo ct, boolean reconectar, JButton fin, JButton resultados) throws Exception{		
		try {
			
			//creacion del socket e inicio del hilo de ejecucion
			socketAlumno = new Socket(ip, comun.Global.PUERTOPROFESOR);
			dirEnunciado = directorioEnunciado;
			this.estado = estado;
			this.ct = ct;
			datos = da;
			this.ipProfesor = ip;
			bFin = fin;
			bRes = resultados;

			DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());

			//indicar al profesor si nos estamos reconectando
			dos.writeBoolean(reconectar);

			if(!reconectar){
				//si no se trata de una reconexion
				dis = new DataInputStream(socketAlumno.getInputStream());

				boolean aceptado = dis.readBoolean();

				if(!aceptado){

					estado.setText("Conexion no aceptada");
					socketAlumno.close();

				}else{					
					ObjectOutputStream oos = new ObjectOutputStream(socketAlumno.getOutputStream());					
					oos.writeObject(da);					
					estado.setText("Conectado");
					this.start();
				}
			}else{
				//si nos reconectamos

				dis = new DataInputStream(socketAlumno.getInputStream());
				boolean aceptado = dis.readBoolean();

				if(!aceptado){
					estado.setText("Reconexion no aceptada");
					socketAlumno.close();
				}else{
					ObjectOutputStream oos = new ObjectOutputStream(socketAlumno.getOutputStream());
					oos.writeObject(da);
					aceptado = dis.readBoolean();
					if(!aceptado){
						estado.setText("Reconexion no aceptada");
						socketAlumno.close();
					}else{
						estado.setText("Reconectado");
						this.start();
					}					
				}


			}
		}catch (Exception e) {
			throw e;
		}		
	}


	/**
	 * Acciones durante la vida del thread.
	 * Actua segun los mensajes recibidos.
	 */
	public void run(){

		try {

			//recibir opcion del profesor
			int recibido = dis.readInt();

			//mientras no se acabe el examen
			while(recibido != comun.Global.FINPRUEBA){

				switch (recibido) {

				//opcion de recibir un fichero
				case comun.Global.ENVIOFICHERO:
					String estadoAnterior =  estado.getText().trim();
					estado.setText("Recibiendo fichero");

					ObjectInputStream ois = new ObjectInputStream(socketAlumno.getInputStream());					

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

					//si el fichero no existia fisicamente, crearlo para poder volcar los datos
					if(!enunciado.exists()){
						enunciado.createNewFile();
					}
					fos = new FileOutputStream(enunciado);					

					BloquesFichero bloque = new BloquesFichero();

					do
					{
						//leer el bloque recibido
						Object mensajeAux = ois.readObject();

						//que ha de ser lo que se esta esperando
						if (mensajeAux instanceof BloquesFichero){
							bloque = (BloquesFichero) mensajeAux;
							//se escribe en el fichero
							fos.write(bloque.bloque, 0, bloque.datosUtiles);							
						}else{
							//TODO tratar error, el mensaje no es del tipo esperado
							break;
						}

					} while (!bloque.ultimoBloque);

					//comprobacion de integridad
					MessageDigest digest = MessageDigest.getInstance("MD5");
					FileInputStream is = new FileInputStream(enunciado);				
					byte[] buffer = new byte[4096];
					int read = 0;

					while( (read = is.read(buffer)) > 0) {
						digest.update(buffer, 0, read);
					}
					is.close();
					byte[] md5sum = digest.digest();
					BigInteger bigInt = new BigInteger(1, md5sum);
					String md5 = bigInt.toString(16);

					//si no coinciden los md5
					if(!md5.trim().equals(bloque.md5.trim())){
						JOptionPane.showMessageDialog(estado, "Hubo errores en la transferencia del fichero");
					}else{

						//renombrar el fichero con el nombre y la extension del recibido
						File definitivo;
						if(dirEnunciado.charAt(dirEnunciado.length()-1) == File.separatorChar){
							definitivo = new File(dirEnunciado + bloque.nombreFichero);
						}else{
							definitivo = new File(dirEnunciado + File.separator + bloque.nombreFichero);
						}

						definitivo.createNewFile();

						enunciado.renameTo(definitivo);
					}
					//fin comprobacion integridad


					fos.close();
					estado.setText(estadoAnterior);

					break;

				case comun.Global.COMIENZOPRUEBA:

					try{
						ois = new ObjectInputStream(socketAlumno.getInputStream());
						Object temp = ois.readObject();

						ComienzoExamen ce = (ComienzoExamen) temp;

						if(ce.pruebaTemporizada){
							ct.setMinutos(ce.minutosPrueba, ce.segundosPrueba);
						}

						/* Eliminar acceso red */

						//crear el socket con el proceso daemon
						//esta en el mismo equipo
						socketDaemon = new Socket("127.0.0.1", comun.Global.PUERTODAEMON);
						DataOutputStream dosd = new DataOutputStream(socketDaemon.getOutputStream());
						//opcion de denegar el acceso a la red
						dosd.writeInt(Global.NORED);

						estado.setText("Realizando prueba");
						bFin.setEnabled(true);
						bRes.setEnabled(true);


						//escribir el fichero de estado

						Date ahora = new Date(System.currentTimeMillis());												
						String fecha = ahora.toString();
						String linea = ce.sesion.toString();
						String ipProf = this.ipProfesor;
						String dirEnun = this.dirEnunciado;
						String nombre = datos.nombre;
						String apellido = datos.apellidos;

						String aCifrar = fecha + "\n" + linea + "\n" + ipProf + "\n" + dirEnun + "\n" + nombre + "\n" + apellido;

						ObjectInputStream oisClave = new ObjectInputStream(new FileInputStream(Global.ficheroClave));
						Key key = (Key) oisClave.readObject();
						oisClave.close();

						File estado = new File(Global.ficheroEstado);

						if(estado.exists()){
							estado.delete();
							estado.createNewFile();
						}

						FileOutputStream fosEstado = new FileOutputStream(estado);
						byte[] lineaCifrada = cifrar(aCifrar, key);
						fosEstado.write(lineaCifrada.length);
						fosEstado.write(lineaCifrada);
						fosEstado.close();
					}catch(Exception e){
						JOptionPane.showMessageDialog(bFin, "Error interno al intentar comenzar la prueba");
					}

					break;					

				default:
					estadoPaquete = recibido;
					break;
				}
				recibido = dis.readInt();
			}
			//recibe FINEXAMEN del profesor
			//devolver red y finalizar
			finalizarTiempo();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}


	/**
	 * 
	 * Cifra un string usando el algoritmo AES
	 * 
	 * @param input string a cifrar
	 * @param key clave
	 * @return bytes cifrados
	 */
	private byte[] cifrar(String input, Key key){
		try {

			Cipher cifrador = Cipher.getInstance("AES");	
			cifrador.init(Cipher.ENCRYPT_MODE, key);
			byte[] inputBytes = input.getBytes();
			return cifrador.doFinal(inputBytes);

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * Metodo para cuando es el profesor quien decide finalizar la prueba o se acaba el tiempo.
	 * Permite de nuevo el acceso a la red.
	 */
	public void finalizarTiempo(){		
		try {
			this.interrupt();
			DataOutputStream dosd = new DataOutputStream(socketDaemon.getOutputStream());
			//opcion de permitir el acceso a la red
			dosd.writeInt(Global.SIRED);
			DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());
			dos.writeInt(Global.FINPRUEBA);
			ObjectOutputStream oos = new ObjectOutputStream(socketAlumno.getOutputStream());
			oos.writeObject(datos);
			dos.writeBoolean(true); //indica que ha finalizado la prueba por tiempo
			socketAlumno.close();
			socketDaemon.close();
			File fichEstado = new File(Global.ficheroEstado);
			fichEstado.delete();
			JOptionPane.showMessageDialog(estado, "El tiempo destinado para la realizacion de la prueba ha finalizado.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Metodo para cuando se desea dar por finalizada una prueba sin enviar fichero de resultados.
	 * Permite de nuevo el acceso a la red.
	 */
	public void finalizar(){		
		try {
			this.interrupt();
			DataOutputStream dosd = new DataOutputStream(socketDaemon.getOutputStream());
			//opcion de permitir el acceso a la red
			dosd.writeInt(Global.SIRED);
			DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());
			dos.writeInt(Global.FINPRUEBA);
			ObjectOutputStream oos = new ObjectOutputStream(socketAlumno.getOutputStream());
			oos.writeObject(datos);
			dos.writeBoolean(false);
			socketAlumno.close();
			socketDaemon.close();
			File fichEstado = new File(Global.ficheroEstado);
			fichEstado.delete();
			File fichClave = new File(Global.ficheroClave);
			fichClave.delete();			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Metodo para cuando se desea dar por finalizada una prueba y enviar un fichero de resultados.
	 * Permite de nuevo el acceso a la red.
	 */	
	public void enviarYFinalizar(File resultados){
		try {
			DataOutputStream dosd = new DataOutputStream(socketDaemon.getOutputStream());
			//opcion de permitir el acceso a la red para enviar el fichero
			dosd.writeInt(Global.SIRED);
			//DataInputStream 
			MessageDigest digest = MessageDigest.getInstance("MD5");
			FileInputStream is = new FileInputStream(resultados);				
			byte[] buffer = new byte[4096];
			int read = 0;
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			is.close();
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String md5 = bigInt.toString(16);
			//Enviar el fichero
			DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());
			dos.writeInt(Global.FINRESULTADOS);


			//obtener el canal de salida
			ObjectOutputStream oos = new ObjectOutputStream(socketAlumno.getOutputStream());
			oos.writeObject(datos);
			
			//variable auxiliar para marcar cuando se envia el ultimo mensaje
			boolean enviadoUltimo = false;

			//abrir el fichero
			FileInputStream fis = new FileInputStream(resultados);

			//se instancia y rellena un mensaje de envio de fichero
			BloquesFichero bloque = new BloquesFichero();

			bloque.nombreFichero = resultados.getName();

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
					break; //TODO intentar quitar este break
				}
				//se crea un nuevo bloque
				bloque = new BloquesFichero();
				bloque.nombreFichero = resultados.getName();
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

			/*
			 * El inputstream esta leyendo un posible mensaje de fin de prueba por parte del profesor en el metodo run()
			 * No se puede intentar abrir otro canal de entrada, puesto que se recibe en el otro metodo, hay que sondear hasta que
			 * se haya recibido en run() el mensaje del profesor destinado a este metodo.
			 * Generalmente no habra tiempo de espera.
			 */			
			while((estadoPaquete != Global.BLOQUEMAL) && (estadoPaquete != Global.BLOQUEOK)){				
				Thread.sleep(100);
			}

			fis.close();
			//finaliza el examen
			dos.writeInt(Global.FINPRUEBA);
			dos.writeBoolean(false);
			socketAlumno.close();
			socketDaemon.close();
			File fichEstado = new File(Global.ficheroEstado);
			fichEstado.delete();
			File fichClave = new File(Global.ficheroClave);
			fichClave.delete();
			if(estadoPaquete == Global.BLOQUEOK){
				JOptionPane.showMessageDialog(estado, "Archivo enviado correctamente, finaliza la prueba.");				
			}else{
				JOptionPane.showMessageDialog(estado, "Error en la transferencia del archivo, finaliza la prueba.");
			}
			System.exit(0);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}