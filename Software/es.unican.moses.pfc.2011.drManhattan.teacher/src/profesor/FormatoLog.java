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
    		taLog.append("¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡\n"+new Date().toString()+" "+formatMessage(rec)+"\n");
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
