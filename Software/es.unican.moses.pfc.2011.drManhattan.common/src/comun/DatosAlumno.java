package comun;
import java.io.Serializable;
import java.math.BigInteger;

/**
*
* Clase que representa los datos de un alumno, para notificar al inicio de la prueba
* 
* @author Manuel Pando
*
*/

public class DatosAlumno implements Serializable{

	private static final long serialVersionUID = 4539746680450927737L;
	public String nombre;
	public String apellidos;
	public BigInteger id;
	
}
