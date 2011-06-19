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
