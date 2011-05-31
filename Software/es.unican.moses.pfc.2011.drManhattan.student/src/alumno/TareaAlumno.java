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
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JLabel;

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



	/**
	 * 
	 * @param ip direcion del computador del profesor
	 * @param directorioEnunciado directorio donde guardar los ficheros enviados por el profesor
	 * @param estado Etiqueta para notificar el estado de la aplicacion
	 * @param da datos del alumno
	 * @param ct variable usada para notificar el tiempo restante en la prueba
	 */
	public TareaAlumno(String ip, String directorioEnunciado, JLabel estado, DatosAlumno da, CuentaTiempo ct){		
		try {
			//creacion del socket e inicio del hilo de ejecucion
			socketAlumno = new Socket(ip, comun.Global.PUERTOPROFESOR);
			dirEnunciado = directorioEnunciado;
			this.estado = estado;
			this.ct = ct;
			
			//ObjectInputStream ois = new ObjectInputStream(socketAlumno.getInputStream());
			//if(ois.readBoolean()){
			
				ObjectOutputStream oos = new ObjectOutputStream(socketAlumno.getOutputStream());
				oos.writeObject(da);			

				estado.setText("Conectado");
				this.start();
		/*	}else{
				estado.setText("Conexion no aceptada");
			}*/

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
			//flujos de transmision
			DataInputStream dis = new DataInputStream(socketAlumno.getInputStream());

			//recibir opcion del profesor
			int recibido = dis.readInt();

			//mientras no se acabe el examen
			while(recibido != comun.Global.FINEXAMEN){

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
						//pedir reenvio
						DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());
						dos.writeBoolean(false);
					}else{
						DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());
						dos.writeBoolean(true);
					}					
					//fin comprobacion integridad

					//TODO revisar el renameTo(), no funciona bien
					//Nota: parece que el problema con renameTo es de windows, en Ubuntu funciona 
					//renombrar el fichero con el nombre y la extension del recibido
					File definitivo;
					if(dirEnunciado.charAt(dirEnunciado.length()-1) == File.separatorChar){
						definitivo = new File(dirEnunciado + bloque.nombreFichero);
					}else{
						definitivo = new File(dirEnunciado + File.separator + bloque.nombreFichero);
					}

					definitivo.createNewFile();

					boolean res = enunciado.renameTo(definitivo);
					System.out.println("rename: " + res);

					fos.close();
					estado.setText(estadoAnterior);

					break;

				case comun.Global.COMIENZOEXAMEN:

					ois = new ObjectInputStream(socketAlumno.getInputStream());
					Object temp = ois.readObject();

					ComienzoExamen ce = (ComienzoExamen) temp;
					if(ce.examenTemporizado){
						ct.setMinutos(ce.minutosExamen);
					}
					
					/* Eliminar acceso red */
					
					//crear el socket con el proceso daemon
					//esta en el mismo equipo
					socketDaemon = new Socket("127.0.0.1", comun.Global.PUERTODAEMON);
					DataOutputStream dosd = new DataOutputStream(socketDaemon.getOutputStream());
					//opcion de denegar el acceso a la red
					dosd.writeInt(Global.NORED);

					estado.setText("Realizando prueba");
					break;					
					
				default:
					break;
				}
				recibido = dis.readInt();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public void finalizar(){		
		try {
			System.out.println("alumno: finalizar");
			this.interrupt();
			//Socket socketDaemon = new Socket("127.0.0.1", comun.Global.PUERTODAEMON);
			DataOutputStream dosd = new DataOutputStream(socketDaemon.getOutputStream());
			System.out.println("alumno: canal salida daemon");
			//opcion de denegar el acceso a la red
			dosd.writeInt(Global.SIRED);
			
			System.out.println("alumno: permitir red");
			
			DataOutputStream dos = new DataOutputStream(socketAlumno.getOutputStream());
			System.out.println("alumno: canal salida prof");
			dos.writeInt(Global.FINEXAMEN);
			System.out.println("alumno: finalizar prueba");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
