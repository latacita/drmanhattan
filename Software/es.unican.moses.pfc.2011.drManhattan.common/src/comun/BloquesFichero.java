package comun;

import java.io.Serializable;

/**
*
* Clase que representa el mensaje enviado tanto por el profesor como por los alumnos
* conteniendo parte o totalidad de un fichero.
* 
* @author Manuel Pando
*
*/
public class BloquesFichero implements Serializable {

	private static final long serialVersionUID = -8070432745902251384L;

	//Nombre del fichero
	public String nombreFichero;
	
	//Contiene hasta 4KBytes de datos del fichero a transmitir
	public byte[] bloque = new byte[4096];
	
	//Numero de bytes utiles transmitidos, entre 1 y 4096
	public int datosUtiles;
	
	//True indica que no se han de recibir mas, el fichero esta transmitido totalmente
	public boolean ultimoBloque;
	
	//MD5 del fichero
	public String md5;
}
