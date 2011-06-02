package profesor;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * Clase que contiene el formato de los registros de actividad generados por la aplicacion.
 * 
 * @author Manuel Pando
 *
 */
class FormatoLog extends Formatter { 
	
	/**
	 * Formato de cada log
	 */
    public String format(LogRecord rec) {    	
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
