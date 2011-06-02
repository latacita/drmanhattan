package profesor;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import comun.BloquesFichero;
import comun.DatosAlumno;
import comun.Global;

/**
 * 
 * Clase que representa las operaciones que realiza la aplicacion del profesor 
 * en cada una de las conexiones.
 * 
 * @author Manuel Pando
 *
 */

public class TareaProfesor extends Thread{

	private Socket conexion;
	private String dirResultados;

	public TareaProfesor(Socket s, String dirResultados){
		conexion = s;
		this.dirResultados = dirResultados;
		this.start();
	}



	public void run(){
		try {
			//flujos de transmision
			DataInputStream dis = new DataInputStream(conexion.getInputStream());
			int recibido;
			recibido = dis.readInt();
			//mientras no se acabe el examen
			//TODO quizas anadir isInterrupt a la condicion, en caso de que no acabe el alumno
			while((recibido != comun.Global.FINEXAMEN) || (recibido != comun.Global.FINRESULTADOS)){

				switch (recibido) {
				
				//caso: finalizar examen y enviar				
				case Global.FINRESULTADOS:
										
					//el archivo de resultados se guarda en el directorio especificado/nombre de la asignatura/apellidosNombre
					ObjectInputStream ois = new ObjectInputStream(conexion.getInputStream());
					DatosAlumno datos = (DatosAlumno) ois.readObject();
					String apellidosNombre = datos.apellidos.trim() + datos.nombre.trim();
					String apellidosNombreSinEspacios = "";
					
					StringTokenizer tokenizer = new StringTokenizer(apellidosNombre);
					while (tokenizer.hasMoreElements()){
						apellidosNombreSinEspacios += tokenizer.nextElement();
					}
					//crear el directorio
					
					if(dirResultados.charAt(dirResultados.length()-1) == File.separatorChar){
						dirResultados = dirResultados+apellidosNombreSinEspacios+File.separator;
					}else{
						dirResultados = dirResultados+File.separator+apellidosNombreSinEspacios+File.separator;
					}
					
					File directorio = new File(dirResultados);
					directorio.mkdirs();


					File resultados;
					FileOutputStream fos;
					
					
					resultados = new File(directorio.getAbsolutePath().trim()+"temporal");
					

					//si el fichero no existia fisicamente, crearlo para poder volcar los datos
					if(!resultados.exists()){
						resultados.createNewFile();
					}
					fos = new FileOutputStream(resultados);					

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

					//si no coinciden los md5
					if(!md5.trim().equals(bloque.md5.trim())){
						//pedir reenvio
						DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
						dos.writeBoolean(false);
					}else{
						DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
						dos.writeBoolean(true);
					}					
					//fin comprobacion integridad

					//TODO revisar el renameTo(), no funciona bien
					//Nota: parece que el problema con renameTo es de windows, en Ubuntu funciona 
					//renombrar el fichero con el nombre y la extension del recibido
					File definitivo;
					if(dirResultados.charAt(dirResultados.length()-1) == File.separatorChar){
						definitivo = new File(dirResultados + bloque.nombreFichero);
					}else{
						definitivo = new File(dirResultados + File.separator + bloque.nombreFichero);
					}

					definitivo.createNewFile();

					boolean res = resultados.renameTo(definitivo);
					
					if(!res){
						//error en el rename
					}
					
					fos.close();
					Logger logger = Logger.getLogger("PFC");
					logger.log(Level.INFO, "Finaliza la prueba el alumno: "+datos.nombre+" "+datos.apellidos+"\nArchivo de resultados: "+definitivo.getAbsolutePath());
					break;
							
				case Global.FINEXAMEN:
					ois = new ObjectInputStream(conexion.getInputStream());
					datos = (DatosAlumno) ois.readObject();
					logger = Logger.getLogger("PFC");
					logger.log(Level.INFO, "Finaliza la prueba el alumno: "+datos.nombre+" "+datos.apellidos+".\n Sin archivo de resultados");
					break;
				default:
					break;


				}
				recibido = dis.readInt();
			}
			//acabar la conexion, el alumno acaba la prueba
			conexion.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}