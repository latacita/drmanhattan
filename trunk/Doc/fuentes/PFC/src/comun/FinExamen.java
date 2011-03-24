package comun;

import java.io.Serializable;

/**
*
* Clase que representa el mensaje enviado por cada cliente al servidor cuando se finaliza el examen.
* 
* @author Manuel Pando Muñoz
*
*/
public class FinExamen implements Serializable{
	
	//Nombre y apellidos del alumno que finaliza el examen
	public String nombre;
	public 	String apellidos;
	
	//True indica que el alumno envia resultados
	public boolean hayResultados;	
	
}
