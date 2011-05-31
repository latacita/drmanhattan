package profesor;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	public TareaProfesor(Socket s){
		conexion = s;
		this.start();
	}



	public void run(){
		try {
			//flujos de transmision
			DataInputStream dis = new DataInputStream(conexion.getInputStream());
			int recibido;
			recibido = dis.readInt();
			System.out.println("prof: recibido: "+recibido);
			//mientras no se acabe el examen
			//TODO quizas anadir isInterrupt a la condicion, en caso de que no acabe el alumno
			while(recibido != comun.Global.FINEXAMEN){

				switch (recibido) {
				
				
				

				}
			}
			//acabar la conexion, el alumno acaba la prueba
			System.out.println("Finalizado examen");
			//TODO LOG
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
