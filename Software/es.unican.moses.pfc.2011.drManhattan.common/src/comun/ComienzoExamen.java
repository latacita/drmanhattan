package comun;

import java.io.Serializable;

/**
 *
 * Clase que representa el mensaje enviado por el profesor a cada uno de los alumnos
 * indicando el comienzo del examen.
 * 
 * @author Manuel Pando
 *
 */
public class ComienzoExamen implements Serializable{

	private static final long serialVersionUID = -4810084840430024581L;

	//True indica que hay limite de tiempo prefijado, false lo contrario
	public boolean examenTemporizado;
	
	//En caso de haber limite prefijado, cuantos minutos durara
	public int minutosExamen;
	
	//y cuantos segundos
	public int segundosExamen;
	
}
