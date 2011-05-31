package comun;

import java.io.Serializable;

/**
*
* Clase que representa el mensaje enviado por cada alumno al profesor cuando se finaliza el examen.
* 
* @author Manuel Pando
*
*/
public class FinExamen implements Serializable{

	private static final long serialVersionUID = -1006798703096483645L;
	
	//Nombre y apellidos del alumno que finaliza el examen
	public String nombre;
	public 	String apellidos;
	
	//True indica que el alumno envia resultados
	public boolean hayResultados;	
	
}
