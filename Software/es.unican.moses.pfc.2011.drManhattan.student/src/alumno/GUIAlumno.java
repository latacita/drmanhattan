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
package alumno;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import comun.DatosAlumno;
import comun.Global;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.util.Date;
import java.awt.Cursor;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import java.awt.Font;


/**
 * 
 * Clase que crea la GUI del alumno.
 * 
 * @author Manuel Pando
 *
 */
public class GUIAlumno{

	private JFrame frmDrmanhattan;

	private JTextField tfNombre;
	private JTextField tfApellido;
	private JTextField tfDirEnunciado;
	private JTextField tfIPProfesor;

	private JLabel lblIpProfesor;
	private JLabel lblApellido;
	private JLabel lblNombre;	
	private JLabel lblDirectorioEnunciado;
	private JLabel lblTiempoRestante;
	private JLabel lblTiempo;
	private JLabel lblTextoEstado;
	private JLabel lblEstado;
	private JLabel lblAutor;

	private JButton btnConectar;
	private JButton btnExplorar;
	private JButton btnFinalizar;
	private JButton btnEnviarResultados;

	//variable para controlar la cuenta atras
	private boolean finExamen = false;	
	private CuentaTiempo ct = new CuentaTiempo();
	private TareaAlumno tarea;

	private Key key = null;
	private Cipher cipher = null;


	/**
	 * Create the application.
	 */
	public GUIAlumno() {		
		initialize();
		frmDrmanhattan.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {


		//creacion y distribucion de componentes

		frmDrmanhattan = new JFrame();
		frmDrmanhattan.setResizable(false);

		frmDrmanhattan.setIconImage(Toolkit.getDefaultToolkit().getImage("/usr/share/drManhattanAlumno/iconos/icono.png"));
		frmDrmanhattan.setAlwaysOnTop(false);
		frmDrmanhattan.setTitle("drManhattan - Alumno");
		frmDrmanhattan.setBounds(100, 100, 649, 398);
		frmDrmanhattan.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmDrmanhattan.getContentPane().setLayout(null);

		btnConectar = new JButton("Conectar");
		btnConectar.setHorizontalAlignment(SwingConstants.LEFT);
		btnConectar.setIcon(new ImageIcon("/usr/share/drManhattanAlumno/iconos/conectar.png"));
		btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnConectar.setBounds(10, 212, 145, 47);
		btnConectar.setToolTipText("Intenta conectarse al computador del profesor, una vez conectado no podran cambiarse los atributos");
		frmDrmanhattan.getContentPane().add(btnConectar);

		lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 62, 152, 23);
		frmDrmanhattan.getContentPane().add(lblNombre);

		lblApellido = new JLabel("Apellidos:");
		lblApellido.setBounds(10, 96, 152, 23);
		frmDrmanhattan.getContentPane().add(lblApellido);

		tfNombre = new JTextField();
		tfNombre.setText("Nombre");
		tfNombre.setBounds(202, 59, 126, 23);
		frmDrmanhattan.getContentPane().add(tfNombre);
		tfNombre.setColumns(10);

		tfApellido = new JTextField();
		tfApellido.setText("Apellidos");
		tfApellido.setBounds(202, 93, 233, 23);
		frmDrmanhattan.getContentPane().add(tfApellido);
		tfApellido.setColumns(10);

		lblDirectorioEnunciado = new JLabel("Directorio enunciado:");
		lblDirectorioEnunciado.setBounds(10, 130, 174, 23);
		frmDrmanhattan.getContentPane().add(lblDirectorioEnunciado);

		tfDirEnunciado = new JTextField();
		tfDirEnunciado.setText("/home/manuel/Escritorio/FicherosAlumno");
		tfDirEnunciado.setBounds(202, 127, 233, 23);
		frmDrmanhattan.getContentPane().add(tfDirEnunciado);
		tfDirEnunciado.setColumns(10);

		btnExplorar = new JButton("Explorar");
		btnExplorar.setIcon(new ImageIcon("/usr/share/drManhattanAlumno/iconos/explorar.png"));
		btnExplorar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnExplorar.setBounds(467, 118, 152, 47);
		btnExplorar.setToolTipText("Navegar por el sistema de ficheros para seleccionar el directorio donde se recibira el enunciado");
		frmDrmanhattan.getContentPane().add(btnExplorar);

		btnFinalizar = new JButton("Finalizar");
		btnFinalizar.setIcon(new ImageIcon("/usr/share/drManhattanAlumno/iconos/fin.png"));
		btnFinalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnFinalizar.setEnabled(false);
		btnFinalizar.setBounds(10, 287, 145, 47);
		btnFinalizar.setToolTipText("Finaliza la prueba sin enviar archivo de resultados");
		frmDrmanhattan.getContentPane().add(btnFinalizar);

		btnEnviarResultados = new JButton("Enviar resultados y finalizar");
		btnEnviarResultados.setIcon(new ImageIcon("/usr/share/drManhattanAlumno/iconos/envioFichero.png"));
		btnEnviarResultados.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnEnviarResultados.setEnabled(false);
		btnEnviarResultados.setBounds(250, 287, 280, 47);
		btnEnviarResultados.setToolTipText("Finaliza la prueba y permite enviar un unico archivo de resultados");
		frmDrmanhattan.getContentPane().add(btnEnviarResultados);

		lblIpProfesor = new JLabel("IP Profesor:");
		lblIpProfesor.setBounds(10, 164, 152, 23);
		frmDrmanhattan.getContentPane().add(lblIpProfesor);

		tfIPProfesor = new JTextField();
		tfIPProfesor.setHorizontalAlignment(SwingConstants.LEFT);
		tfIPProfesor.setText("127.0.0.1");
		tfIPProfesor.setBounds(202, 161, 108, 22);
		frmDrmanhattan.getContentPane().add(tfIPProfesor);
		tfIPProfesor.setColumns(10);

		lblTiempoRestante = new JLabel("Tiempo restante:");
		lblTiempoRestante.setBounds(10, 11, 152, 23);
		frmDrmanhattan.getContentPane().add(lblTiempoRestante);

		lblTiempo = new JLabel("- Desconocido -");
		lblTiempo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTiempo.setBounds(202, 12, 126, 19);
		frmDrmanhattan.getContentPane().add(lblTiempo);

		lblTextoEstado = new JLabel("Estado: ");
		lblTextoEstado.setBounds(202, 212, 75, 23);
		frmDrmanhattan.getContentPane().add(lblTextoEstado);

		lblEstado = new JLabel("No conectado");
		lblEstado.setBounds(289, 212, 280, 23);
		frmDrmanhattan.getContentPane().add(lblEstado);

		lblAutor = new JLabel("Manuel Pando Muñoz - Proyecto fin de carrera");
		lblAutor.setFont(new Font("Dialog", Font.BOLD, 10));
		lblAutor.setBounds(10, 346, 625, 15);
		frmDrmanhattan.getContentPane().add(lblAutor);


		//Manejadores de eventos

		/**
		 * Manejador de pulsar el boton para seleccionar la carpeta destino.
		 * Crea un dialogo que permite navegar por el sistema de ficheros y escoger un directorio destino donde se guardara el enunciado
		 */
		btnExplorar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(chooser);
				if (returnVal == JFileChooser.APPROVE_OPTION){					
					File dirO = chooser.getSelectedFile();
					tfDirEnunciado.setText(dirO.getAbsolutePath());
				}
			}
		});

		/**
		 * Manejador del evento de pulsar el boton conectar.
		 */
		btnConectar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				String ipProfesor = tfIPProfesor.getText().trim();
				String dirEnun = tfDirEnunciado.getText().trim();
				DatosAlumno da = new DatosAlumno();
				da.nombre = tfNombre.getText().trim();
				da.apellidos = tfApellido.getText().trim();


				if(ipProfesor.isEmpty() || dirEnun.isEmpty() || da.nombre.isEmpty() || da.apellidos.isEmpty()){
					JOptionPane.showMessageDialog(frmDrmanhattan, "Rellena todos los datos antes de comenzar");
				}else{

					//comprobacion de permisos
					File directorio = new File(dirEnun);
					boolean permisos = directorio.canWrite();
					if(!permisos){
						JOptionPane.showMessageDialog(frmDrmanhattan, "No hay permisos de escritura en el directorio seleccionado" +
						", no se recibran archivos del profesor");
					}				


					int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "¿Conectarse con las caracteristicas seleccionadas?");				
					if (JOptionPane.OK_OPTION == confirmado){
						try {
							tarea = new TareaAlumno(ipProfesor, dirEnun, lblEstado, da, ct, false, btnFinalizar, btnEnviarResultados);
							btnConectar.setEnabled(false);
							btnExplorar.setEnabled(false);
							tfNombre.setEnabled(false);
							tfDirEnunciado.setEnabled(false);
							tfApellido.setEnabled(false);
							tfIPProfesor.setEnabled(false);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(frmDrmanhattan, "Error en la conexion");
						}					
					}
				}
			}
		});


		/**
		 * Manejador del evento de pulsar el boton finaliza la prueba sin enviar resultados
		 */
		btnFinalizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "Vas a finalizar la prueba sin enviar resultados");				
				if (JOptionPane.OK_OPTION == confirmado){
					tarea.finalizar();
				}
			}
		});


		/**
		 * Manejador del evento de pulsar el boton finaliza la prueba enviando un fichero de resultados
		 */
		btnEnviarResultados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(chooser);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File resultados = chooser.getSelectedFile();
					tarea.enviarYFinalizar(resultados);
				}
			}
		});

		/**
		 * Manejador del evento de cerrar la ventana.
		 */
		frmDrmanhattan.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				if(!btnConectar.isEnabled()){
					if(!btnFinalizar.isEnabled()){
						int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "¿Desea cerrar realmente?");
						if (JOptionPane.OK_OPTION == confirmado){					
							System.exit(0);
						}
					}else{
						int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "Vas a finalizar la prueba sin enviar resultados");
						if (JOptionPane.OK_OPTION == confirmado){
							tarea.finalizar();
						}
					}
				}else{
					System.exit(0);
				}
			}
		});



		try {

			//Comprobar el estado anterior de la aplicacion

			key = KeyGenerator.getInstance("AES").generateKey();
			cipher = Cipher.getInstance("AES");

			File fichClave = new File(Global.ficheroClave);

			if(fichClave.exists()){				
				//si el fichero de clave existe, lo cargo				
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichClave));
				key = (Key) ois.readObject();
				ois.close();
			}else{
				//si no, lo creo y guardo la clave				
				fichClave.createNewFile();
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(Global.ficheroClave));
				outputStream.writeObject(key);
				outputStream.close();				
			}

			File estado = new File(Global.ficheroEstado);
			if(estado.exists()){

				/*
				 * Estructura del fichero
				 * -Fecha
				 * -Sesion
				 * -ip
				 * -dirEnun
				 * -nombre
				 * -apellido
				 */				

				FileInputStream fisEstado = new FileInputStream(estado);					
				int tamano = fisEstado.read();
				byte[] lineaDescifrada = new byte[tamano];
				fisEstado.read(lineaDescifrada);
				String desCi = descifrar(lineaDescifrada);
				fisEstado.close();

				//lectura del contenido del fichero
				//cada parametro esta separado por un salto de linea

				int posIntro = desCi.indexOf("\n");
				String fecha = desCi.substring(0, posIntro).trim();
				desCi = desCi.substring(posIntro+1).trim();

				Date fechaEstado = new Date(fecha);
				Date ahora = new Date(System.currentTimeMillis());
				//si los datos de estado son del mismo dia

				if((ahora.getDate() == fechaEstado.getDate()) && (ahora.getDay() == fechaEstado.getDay())){

					posIntro = desCi.indexOf("\n");
					String sesion = desCi.substring(0, posIntro).trim();
					desCi = desCi.substring(posIntro+1).trim();

					posIntro = desCi.indexOf("\n");
					String ipProf = desCi.substring(0, posIntro).trim();
					desCi = desCi.substring(posIntro+1).trim();

					posIntro = desCi.indexOf("\n");
					String dirEnun = desCi.substring(0, posIntro).trim();
					desCi = desCi.substring(posIntro+1).trim();

					posIntro = desCi.indexOf("\n");
					String nombre = desCi.substring(0, posIntro).trim();
					desCi = desCi.substring(posIntro+1).trim();

					String apellido = desCi.trim();

					BigInteger id = new BigInteger(sesion);


					//reconectarse al profesor
					reconectar(ipProf, dirEnun, nombre, apellido, id);
				}else{
					//si no lo son, son datos antiguos, borrarlos
					estado.delete();
					fichClave.delete();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}



	/**
	 * Devuelve el String resultado de descifrar una cadena de bytes usando el algoritmo AES.
	 */
	public String descifrar(byte[] encryptionBytes){
		try{
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] recoveredBytes = cipher.doFinal(encryptionBytes);

			String recovered = new String(recoveredBytes);

			return recovered;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}




	/**
	 * Con los parametros correspondientes al estado anterior de la aplicación, reconectarse.
	 */
	private void reconectar(String ip, String dirE, String nom, String ape, BigInteger sesion){

		String ipProfesor = ip;
		String dirEnun = dirE;
		DatosAlumno da = new DatosAlumno();
		da.nombre = nom;
		da.apellidos = ape;
		da.id = sesion;


		try {
			tarea = new TareaAlumno(ipProfesor, dirEnun, lblEstado, da, ct, true, btnEnviarResultados, btnFinalizar);
			tfApellido.setText(ape);
			tfIPProfesor.setText(ip);
			tfDirEnunciado.setText(dirE);
			tfNombre.setText(nom);

			btnConectar.setEnabled(false);
			btnExplorar.setEnabled(false);
			tfNombre.setEnabled(false);
			tfDirEnunciado.setEnabled(false);
			tfApellido.setEnabled(false);
			tfIPProfesor.setEnabled(false);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frmDrmanhattan, "Error en la conexion");
		}
	}


	//Hilo utilizado para el aviso de que restan 5 minutos para acabar la prueba.
	//Al usar un hilo aparte no se bloquea la interfaz
	Thread aviso = new Thread(new Runnable(){
		public void run(){
			frmDrmanhattan.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(frmDrmanhattan, "Quedan 5 minutos para la finalizacion");
			frmDrmanhattan.setAlwaysOnTop(false);
		}
	});



	/**
	 * Clase para mantener una cuenta atras hasta la finalizacion de la prueba para que el alumno pueda consultarlo.
	 * Actualiza cada segundo un componente label de la GUI.
	 * Se ejecuta en un thread aparte para no bloquear la interfaz.
	 * 
	 * @author Manuel Pando
	 *
	 */
	public class CuentaTiempo implements Runnable {

		/**
		 * Inicia el contador
		 * @param minutos que durara el examen
		 */
		public void setMinutos(int minutos, int segundos){
			lblTiempo.setText((minutos)+":"+segundos);
			new Thread(this).start();
		}

		/**
		 * Metodo que decrementa cada segundo el valor de la etiqueta que indica el tiempo restante sin bloquear la GUI
		 */
		public void run() {
			while(!finExamen){
				long tiempoActual = System.currentTimeMillis();
				//se lee el valor del label con la forma "minutos:segundos" y se decrementa un segundo
				int minutos = Integer.parseInt(lblTiempo.getText().substring(0, lblTiempo.getText().indexOf(':')));
				int segundos = Integer.parseInt(lblTiempo.getText().substring(lblTiempo.getText().indexOf(':')+1));

				//si los segundos son 0 significa que hay que decrementar un minuto y poner los segundos a 59

				if(segundos == 0){
					//si ademas, los minutos tambien son 0, se ha llegado al fin de examen
					if(minutos == 0){
						finExamen = true;												
						tarea.finalizarTiempo();
						break;
					}

					if(minutos == 5){
						aviso.start();
					}

					minutos--;

					segundos = 59;

					//sino simplemente se decrementa un segundo
				}else{

					segundos--;
				}
				//se muestra el nuevo tiempo
				if(segundos <=9){
					lblTiempo.setText(minutos+":0"+segundos);
				}else{
					lblTiempo.setText(minutos+":"+segundos);
				}
				try {

					long tiempoActualPasado = System.currentTimeMillis();
					//esperamos un segundo menos el tiempo transcurrido para hacer los calculos
					Thread.sleep(1000-(tiempoActualPasado-tiempoActual));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}