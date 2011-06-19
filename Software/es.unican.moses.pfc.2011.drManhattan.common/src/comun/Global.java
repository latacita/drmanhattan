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

/**
*
* Clase que con tiene constantes globales del programa
* 
* @author Manuel Pando
*
*/

public class Global {
	
	public static final int PUERTODAEMON = 8889;
	
	public static final int PUERTOPROFESOR = 8888;
	
	public static final int COMIENZOPRUEBA = 0;
	
	public static final int ENVIOFICHERO = 1;
	
	public static final int SIRED = 2;
	
	public static final int NORED = 3;
		
	public static final int FINPRUEBA = 4;
	
	public static final int FINRESULTADOS = 5;
	
	public static final int BLOQUEOK = 6;
	
	public static final int BLOQUEMAL = 7;
	
	public static final String ficheroEstado = "/usr/bin/drManhattan/dralu.dat";
	
	public static final String ficheroClave = "/usr/bin/drManhattan/dralu.key";	
	
}
