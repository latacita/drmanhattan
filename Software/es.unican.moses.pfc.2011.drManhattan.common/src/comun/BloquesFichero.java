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
