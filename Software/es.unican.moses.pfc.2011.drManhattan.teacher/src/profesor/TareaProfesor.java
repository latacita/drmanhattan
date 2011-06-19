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
	private HiloAceptadorAlumnos hiloPrincipal;

	public TareaProfesor(Socket s, String dirResultados, HiloAceptadorAlumnos hilo){
		conexion = s;
		this.dirResultados = dirResultados;
		this.start();
		this.hiloPrincipal = hilo;
	}


	public void run(){
		try {
			//flujos de transmision
			DataInputStream dis = new DataInputStream(conexion.getInputStream());
			int recibido;
			recibido = dis.readInt();
			DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());

			//mientras no se acabe el examen
			while((recibido != comun.Global.FINPRUEBA) || (recibido != comun.Global.FINRESULTADOS)){

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

					Logger logger;

					//si no coinciden los md5
					if(!md5.trim().equals(bloque.md5.trim())){												
						logger = Logger.getLogger("PFC");
						logger.log(Level.SEVERE, "Finaliza la prueba el alumno: "+datos.nombre+" "+datos.apellidos+"\nProblemas en transferencia de su archivo de resultados: ");
						dos.writeInt(Global.BLOQUEMAL);
					}else{
						dos.writeInt(Global.BLOQUEOK);
						//renombrar el fichero con el nombre y la extension del recibido
						File definitivo;
						if(dirResultados.charAt(dirResultados.length()-1) == File.separatorChar){
							definitivo = new File(dirResultados + bloque.nombreFichero);
						}else{
							definitivo = new File(dirResultados + File.separator + bloque.nombreFichero);
						}
						definitivo.createNewFile();

						logger = Logger.getLogger("PFC");

						boolean res = resultados.renameTo(definitivo);
						if(!res){
							logger.log(Level.SEVERE, "Finaliza la prueba el alumno: "+datos.nombre+" "+datos.apellidos+"\nArchivo de resultados con problemas");
						}						

						logger.log(Level.INFO, "Finaliza la prueba el alumno: "+datos.nombre+" "+datos.apellidos+"\nArchivo de resultados: "+definitivo.getAbsolutePath());

					}
					fos.close();

					break;

				case Global.FINPRUEBA:
					ois = new ObjectInputStream(conexion.getInputStream());
					datos = (DatosAlumno) ois.readObject();
					boolean tiempo = dis.readBoolean(); //saber si el tiempo destinado a la prueba a finalizado
					logger = Logger.getLogger("PFC");
					hiloPrincipal.finPrueba(tiempo);
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
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}


}