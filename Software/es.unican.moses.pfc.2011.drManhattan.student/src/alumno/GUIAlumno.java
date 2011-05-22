package alumno;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import comun.DatosAlumno;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * 
 * Clase que crea la GUI del alumno.
 * 
 * @author Manuel Pando Muñoz
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

	private JButton btnConectar;
	private JButton btnExplorar;

	private String nombre;
	private String apellido;
	private String dirEnunciado;
	private String ipProfesor;

	//variable para controlar la cuenta atras
	private boolean finExamen = false;

	private JLabel lblTextoEstado;

	private JLabel lblEstado;

	private CuentaTiempo ct = new CuentaTiempo();

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

		nombre = "";
		apellido = "";
		dirEnunciado = "";
		ipProfesor = "";

		//creacion y distribucion de componentes

		frmDrmanhattan = new JFrame();
		frmDrmanhattan.setTitle("drManhattan - Alumno");
		frmDrmanhattan.setResizable(false);
		frmDrmanhattan.setBounds(100, 100, 546, 300);
		frmDrmanhattan.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDrmanhattan.getContentPane().setLayout(null);

		btnConectar = new JButton("Conectar");		
		btnConectar.setBounds(10, 212, 89, 23);
		frmDrmanhattan.getContentPane().add(btnConectar);

		lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 62, 78, 23);
		frmDrmanhattan.getContentPane().add(lblNombre);

		lblApellido = new JLabel("Apellidos:");
		lblApellido.setBounds(10, 96, 78, 23);
		frmDrmanhattan.getContentPane().add(lblApellido);

		tfNombre = new JTextField();
		tfNombre.setText("Nombre");
		tfNombre.setBounds(144, 62, 126, 23);
		frmDrmanhattan.getContentPane().add(tfNombre);
		tfNombre.setColumns(10);

		tfApellido = new JTextField();
		tfApellido.setText("Apellidos");
		tfApellido.setBounds(144, 96, 212, 23);
		frmDrmanhattan.getContentPane().add(tfApellido);
		tfApellido.setColumns(10);

		lblDirectorioEnunciado = new JLabel("Directorio enunciado:");
		lblDirectorioEnunciado.setBounds(10, 130, 124, 23);
		frmDrmanhattan.getContentPane().add(lblDirectorioEnunciado);

		tfDirEnunciado = new JTextField();
		tfDirEnunciado.setText("/home/enunciado");
		tfDirEnunciado.setBounds(144, 130, 212, 23);
		frmDrmanhattan.getContentPane().add(tfDirEnunciado);
		tfDirEnunciado.setColumns(10);

		btnExplorar = new JButton("Explorar");		
		btnExplorar.setBounds(403, 130, 89, 23);
		frmDrmanhattan.getContentPane().add(btnExplorar);

		lblIpProfesor = new JLabel("IP Profesor:");
		lblIpProfesor.setBounds(10, 164, 78, 23);
		frmDrmanhattan.getContentPane().add(lblIpProfesor);

		tfIPProfesor = new JTextField();
		tfIPProfesor.setHorizontalAlignment(SwingConstants.LEFT);
		tfIPProfesor.setText("127.0.0.1");
		tfIPProfesor.setBounds(144, 164, 89, 22);
		frmDrmanhattan.getContentPane().add(tfIPProfesor);
		tfIPProfesor.setColumns(10);

		lblTiempoRestante = new JLabel("Tiempo restante:");
		lblTiempoRestante.setBounds(10, 11, 124, 23);
		frmDrmanhattan.getContentPane().add(lblTiempoRestante);

		lblTiempo = new JLabel("Tiempo restante");
		lblTiempo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTiempo.setBounds(144, 15, 126, 19);
		frmDrmanhattan.getContentPane().add(lblTiempo);

		lblTextoEstado = new JLabel("Estado: ");
		lblTextoEstado.setBounds(144, 212, 57, 23);
		frmDrmanhattan.getContentPane().add(lblTextoEstado);

		lblEstado = new JLabel("No conectado");
		lblEstado.setBounds(211, 212, 145, 23);
		frmDrmanhattan.getContentPane().add(lblEstado);

		//Manejadores de eventos

		/**
		 * Manejador de pulsar el boton para saleccionar la carpeta destino.
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
				String dirEnum = tfDirEnunciado.getText();
				DatosAlumno da = new DatosAlumno();
				da.nombre = tfNombre.getText();
				da.apellidos = tfApellido.getText();
				new TareaAlumno(ipProfesor, dirEnum, lblEstado, da, ct);
				
				//TODO si hubiese algun problema habria que volverlos a habilitar
				
				btnConectar.setEnabled(false);
				btnExplorar.setEnabled(false);
				tfNombre.setEnabled(false);
				tfDirEnunciado.setEnabled(false);
				tfApellido.setEnabled(false);
				tfIPProfesor.setEnabled(false);
			}
		});

	}


	/**
	 * Clase para mantener una cuenta atras hasta la finalizaci�n de la prueba para que el alumno pueda consultarlo.
	 * Actualiza cada segundo un componente label de la GUI.
	 * Se ejecuta en un thread aparte para no bloquear la interfaz.
	 * 
	 * @author Manuel Pando Mu�oz
	 *
	 */
	public class CuentaTiempo implements Runnable {

		/**
		 * Inicia el contador
		 * @param minutos que durara el examen
		 */
		public void setMinutos(int minutos){
			lblTiempo.setText((minutos-1)+":59");
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
				if(segundos==0){
					minutos--;

					//si ademas, los minutos tambien son 0, se ha llegado al fin de examen
					if(minutos == 0){
						finExamen = true;
						//TODO se ha acabado el tiempo de examen, hacer algo al respecto
						break;
					}					
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