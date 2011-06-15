package alumno;

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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		tfIPProfesor.setBounds(202, 161, 89, 22);
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
				//TODO caso de que cancelo
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
			/*
			 * Comprobar el estado anterior de la aplicacion
			 */


			File estado = new File(Global.ficheroEstado);
			/*
			 * El fichero sera
			 * 
			 * un numero: en caso de que se hubiese iniciado la prueba
			 * ipServidor
			 * 
			 */
			if(estado.exists()){
				System.out.println("El fichero existe");
				FileReader fr = new FileReader(estado);
				BufferedReader br = new BufferedReader(fr);				
				String linea = br.readLine();
				System.out.println("linea: " + linea);
				String ipProf = br.readLine();
				System.out.println("ip prof: "+ipProf);
				String dirEnun = br.readLine();
				System.out.println("dir enum: "+dirEnun);
				String nombre = br.readLine();
				System.out.println("nombre: "+nombre);
				String apellido = br.readLine();
				System.out.println("apellido: "+apellido);

				br.close();
				fr.close();

				//reconectarse al servidor
				reconectar(ipProf, dirEnun, nombre, apellido);

			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void reconectar(String ip, String dirE, String nom, String ape){

		System.out.println("Metodo reconectar de GUI Alumno");

		String ipProfesor = ip;
		String dirEnun = dirE;
		DatosAlumno da = new DatosAlumno();
		da.nombre = nom;
		da.apellidos = ape;


		tarea = new TareaAlumno(ipProfesor, dirEnun, lblEstado, da, ct, true);
		System.out.println("Deshabilitando botones");
		btnConectar.setEnabled(false);
		btnExplorar.setEnabled(false);
		tfNombre.setEnabled(false);
		tfDirEnunciado.setEnabled(false);
		tfApellido.setEnabled(false);
		tfIPProfesor.setEnabled(false);
		btnFinalizar.setEnabled(true);
		btnEnviarResultados.setEnabled(true);

	}



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