package profesor;

import java.awt.Color;
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
    	
    	if(rec.getLevel() == Level.SEVERE){
    		//taLog.setForeground(Color.RED);
    		taLog.append(new Date().toString()+" "+formatMessage(rec)+"\n");  		 
    		//taLog.setForeground(Color.BLACK);
    	}else{
    		taLog.append(new Date().toString()+" "+formatMessage(rec)+"\n");	
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
