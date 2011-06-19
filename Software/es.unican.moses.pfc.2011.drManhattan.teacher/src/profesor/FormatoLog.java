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
package profesor;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

/**
 *
 * Clase que contiene el formato de los registros de actividad generados por la aplicacion.
 * 
 * @author Manuel Pando
 *
 */
class FormatoLog extends Formatter { 
	
	JTextArea taLog;
	
	public FormatoLog(JTextArea taLog) {
		this.taLog = taLog;
	}

	/**
	 * Formato de cada log
	 */
    public String format(LogRecord rec) {

    	//TODO Colores segun importancia
    	
    	if(rec.getLevel() == Level.SEVERE){
    		taLog.append("!!!!!!!!!!!!!!!!!!!!\n"+new Date().toString()+" "+formatMessage(rec)+"\n");
    	}else{
    		taLog.append(new Date().toString()+" "+formatMessage(rec)+"\n");	
    	}
    	
    	String comando = "logger "+formatMessage(rec);
		try {
			Runtime.getRuntime().exec(comando);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return new Date().toString()+" "+formatMessage(rec)+"\n";
    }

    /**
     * Metodo llamado al inicio
     */
    public String getHead(Handler h) {
        return "##############################################\n\n";
    }

    /**
     * Metodo llamado al finalizar
     */
    public String getTail(Handler h) {
        return "\n##############################################\n\n";
    }
}
