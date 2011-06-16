package comun;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * Clase que representa el mensaje enviado por el profesor a cada uno de los alumnos
 * indicando el comienzo de la prueba
 * 
 * @author Manuel Pando
 *
 */
public class ComienzoExamen implements Serializable{

	private static final long serialVersionUID = -4810084840430024581L;

	//True indica que hay limite de tiempo prefijado, false lo contrario
	public boolean pruebaTemporizada;
	
	//En caso de haber limite prefijado, cuantos minutos durara
	public int minutosPrueba;
	
	//y cuantos segundos
	public int segundosPrueba;
	
	//identificador de la prueba
	public BigInteger sesion;
	
}
