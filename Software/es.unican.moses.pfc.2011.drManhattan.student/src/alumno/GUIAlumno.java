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
import java.awt.Cursor;
import java.awt.Toolkit;


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
		frmDrmanhattan.setBounds(100, 100, 583, 338);
		frmDrmanhattan.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmDrmanhattan.getContentPane().setLayout(null);

		btnConectar = new JButton("Conectar");
		btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnConectar.setBounds(10, 212, 101, 23);
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
		btnExplorar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnExplorar.setBounds(461, 127, 108, 23);
		btnExplorar.setToolTipText("Navegar por el sistema de ficheros para seleccionar el directorio donde se recibira el enunciado");
		frmDrmanhattan.getContentPane().add(btnExplorar);

		btnFinalizar = new JButton("Finalizar");
		btnFinalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnFinalizar.setEnabled(false);
		btnFinalizar.setBounds(64, 271, 117, 25);
		btnFinalizar.setToolTipText("Finaliza la prueba sin enviar archivo de resultados");
		frmDrmanhattan.getContentPane().add(btnFinalizar);

		btnEnviarResultados = new JButton("Enviar resultados y finalizar");
		btnEnviarResultados.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnEnviarResultados.setEnabled(false);
		btnEnviarResultados.setBounds(239, 271, 261, 25);
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

				String ipProfesor = tfIPProfesor.getText();
				String dirEnun = tfDirEnunciado.getText();
				DatosAlumno da = new DatosAlumno();
				da.nombre = tfNombre.getText();
				da.apellidos = tfApellido.getText();


				//comprobacion de permisos
				File directorio = new File(dirEnun);
				boolean permisos = directorio.canWrite();
				if(!permisos){
					JOptionPane.showMessageDialog(frmDrmanhattan, "No hay permisos de escritura en el directorio seleccionado" +
					", no se recibran archivos del profesor");
				}

				int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "¿Conectarse con las caracteristicas seleccionadas?");				
				if (JOptionPane.OK_OPTION == confirmado){
					tarea = new TareaAlumno(ipProfesor, dirEnun, lblEstado, da, ct, false);
					btnConectar.setEnabled(false);
					btnExplorar.setEnabled(false);
					tfNombre.setEnabled(false);
					tfDirEnunciado.setEnabled(false);
					tfApellido.setEnabled(false);
					tfIPProfesor.setEnabled(false);
					btnFinalizar.setEnabled(true);
					btnEnviarResultados.setEnabled(true);
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
					int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "Vas a finalizar la prueba sin enviar resultados");
					if (JOptionPane.OK_OPTION == confirmado){					
						tarea.finalizar();
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

		tarea = new TareaAlumno(ipProfesor, dirEnun, lblEstado, da, ct, true);

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
		btnFinalizar.setEnabled(true);
		btnEnviarResultados.setEnabled(true);

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
					if(minutos == 0){
						finExamen = true;												
						tarea.finalizarTiempo();
						break;
					}

					if(minutos == 5){
						aviso.start();
					}

					minutos--;

					//si ademas, los minutos tambien son 0, se ha llegado al fin de examen

					segundos = 59;

					//sino simplemente se decrementa un segundo
				}else{

					segundos--;
				}
				//se muestra el nuevo tiempo

				lblTiempo.setText(minutos+":"+segundos);
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