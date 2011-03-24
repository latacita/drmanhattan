package comun;

import java.io.Serializable;

/**
 *
 * Clase que representa el mensaje enviado por el servidor a cada uno de los clientes
 * indicando el comienzo del examen.
 * 
 * @author Manuel Pando Muñoz
 *
 */
public class ComienzoExamen implements Serializable{
	
	//True indica que hay limite de tiempo prefijado, false lo contrario
	public boolean examenTemporizado;
	
	//En caso de haber limite prefijado, cuantos minutos durara
	public int minutosExamen;
	
	//True indica a los clientes que han de recibir un fichero de enunciado de examen
	public boolean hayEnunciado;	
	
}
