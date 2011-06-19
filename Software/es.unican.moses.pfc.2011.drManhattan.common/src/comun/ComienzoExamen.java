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
