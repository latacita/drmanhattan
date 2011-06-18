package profesor;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.JScrollPane;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Toolkit;
import javax.swing.JFormattedTextField;
import javax.swing.ImageIcon;
import java.awt.Font;

/**
 * 
 * Clase que crea la GUI del profesor.
 * 
 * @author Manuel Pando
 *
 */
public class GUIProfesor {

	private JFrame frmDrmanhattan;

	private JPanel panelLog;

	private JScrollPane scrollPane;

	private JTextArea taLog;

	private JTextField tfNombreAsignatura;
	private JTextField tfDirectorioResultados;

	private JFormattedTextField ftfHoraLImite;

	private JLabel lblNombreAsignatura;
	private JLabel lblRecibirResultadosEn;
	private JLabel lblHoraLimiteExamen;
	private JLabel lblAutor;

	private JButton btnComienzoExamen;
	private JButton btnFinExamen;	
	private JButton btnExplorarDirResultados;
	private JButton btnEnviarFichero;

	private HiloAceptadorAlumnos aceptaAlumnos;

	private Logger logger;


	/**
	 * Create the application.
	 */
	public GUIProfesor() {
		initialize();		
		frmDrmanhattan.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frmDrmanhattan = new JFrame();
		frmDrmanhattan.setIconImage(Toolkit.getDefaultToolkit().getImage("/usr/share/drManhattanProfesor/iconos/icono.png"));
		frmDrmanhattan.setResizable(false);
		frmDrmanhattan.setTitle("drManhattan - Profesor");
		frmDrmanhattan.setBounds(100, 100, 706, 695);
		frmDrmanhattan.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmDrmanhattan.getContentPane().setLayout(null);


		btnComienzoExamen = new JButton("Comienzo de prueba");
		btnComienzoExamen.setHorizontalAlignment(SwingConstants.LEFT);
		btnComienzoExamen.setIcon(new ImageIcon("/usr/share/drManhattanProfesor/iconos/inicio.png"));
		btnComienzoExamen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnComienzoExamen.setToolTipText("Comienza la prueba, no se admiten nuevos alumnos");
		btnComienzoExamen.setBounds(10, 167, 223, 50);
		frmDrmanhattan.getContentPane().add(btnComienzoExamen);

		btnFinExamen = new JButton("Fin de prueba");
		btnFinExamen.setHorizontalAlignment(SwingConstants.LEFT);
		btnFinExamen.setIcon(new ImageIcon("/usr/share/drManhattanProfesor/iconos/fin.png"));
		btnFinExamen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnFinExamen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnFinExamen.setToolTipText("Finaliza la prueba");
		btnFinExamen.setEnabled(false);
		btnFinExamen.setBounds(10, 251, 223, 50);
		frmDrmanhattan.getContentPane().add(btnFinExamen);

		lblNombreAsignatura = new JLabel("Nombre de la asignatura:");
		lblNombreAsignatura.setBounds(10, 36, 223, 23);
		frmDrmanhattan.getContentPane().add(lblNombreAsignatura);

		tfNombreAsignatura = new JTextField();		
		tfNombreAsignatura.setText("PFC");
		tfNombreAsignatura.setBounds(251, 36, 262, 23);
		frmDrmanhattan.getContentPane().add(tfNombreAsignatura);
		tfNombreAsignatura.setColumns(10);

		lblRecibirResultadosEn = new JLabel("Recibir resultados en:");
		lblRecibirResultadosEn.setBounds(10, 70, 160, 23);
		frmDrmanhattan.getContentPane().add(lblRecibirResultadosEn);

		lblAutor = new JLabel("Manuel Pando Muñoz - Proyecto fin de carrera");
		lblAutor.setBounds(10, 640, 283, 13);
		frmDrmanhattan.getContentPane().add(lblAutor);
		lblAutor.setFont(new Font("Dialog", Font.BOLD, 10));

		tfDirectorioResultados = new JTextField();
		tfDirectorioResultados.setText("/home/manuel/Escritorio/FicherosProfesor");
		tfDirectorioResultados.setBounds(251, 70, 262, 23);
		frmDrmanhattan.getContentPane().add(tfDirectorioResultados);
		tfDirectorioResultados.setColumns(10);

		btnExplorarDirResultados = new JButton("Explorar");
		btnExplorarDirResultados.setIcon(new ImageIcon("/usr/share/drManhattanProfesor/iconos/explorar.png"));
		btnExplorarDirResultados.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnExplorarDirResultados.setToolTipText("Navegar por el sistema de ficheros para seleccionar el directorio donde se recibiran los resultados");
		btnExplorarDirResultados.setBounds(525, 56, 142, 50);
		frmDrmanhattan.getContentPane().add(btnExplorarDirResultados);

		lblHoraLimiteExamen = new JLabel("Hora fin de examen (hh:mm):");
		lblHoraLimiteExamen.setBounds(10, 104, 223, 23);
		frmDrmanhattan.getContentPane().add(lblHoraLimiteExamen);

		panelLog = new JPanel();
		panelLog.setBorder(new TitledBorder(null, "Eventos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog.setBounds(10, 313, 680, 292);
		frmDrmanhattan.getContentPane().add(panelLog);
		panelLog.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 23, 658, 258);
		panelLog.add(scrollPane);

		taLog = new JTextArea();
		taLog.setEditable(false);
		scrollPane.setViewportView(taLog);

		btnEnviarFichero = new JButton("Enviar fichero");
		btnEnviarFichero.setIcon(new ImageIcon("/usr/share/drManhattanProfesor/iconos/envioFichero.png"));
		btnEnviarFichero.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnEnviarFichero.setToolTipText("Envia un fichero a todos los alumnos conectados");
		btnEnviarFichero.setBounds(314, 167, 184, 50);
		frmDrmanhattan.getContentPane().add(btnEnviarFichero);

		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter("## : ##");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		ftfHoraLImite = new JFormattedTextField(formatter);
		ftfHoraLImite.setHorizontalAlignment(SwingConstants.CENTER);
		ftfHoraLImite.setText("00 : 01");
		ftfHoraLImite.setBounds(251, 105, 59, 23);
		frmDrmanhattan.getContentPane().add(ftfHoraLImite);

		aceptaAlumnos = new HiloAceptadorAlumnos();

		logger = Logger.getLogger("PFC");
		try {
			FileHandler fh;
			fh = new FileHandler();
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
			FormatoLog mh = new FormatoLog(taLog);
			fh.setFormatter(mh);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//Manejadores de eventos

		/**
		 * Manejador el evento de pulsar boton para saleccionar la carpeta destino.
		 * Crea un dialogo que permite navegar por el sistema de ficheros y escoger un directorio destino.
		 */
		btnExplorarDirResultados.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(chooser);
				if (returnVal == JFileChooser.APPROVE_OPTION){					
					File dirO = chooser.getSelectedFile();
					tfDirectorioResultados.setText(dirO.getAbsolutePath());
				}
			}
		});


		/**
		 * Manejador del evento de pulsar el boton de enviar fichero
		 */
		btnEnviarFichero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				//Abrir dialogo para escoger el fichero a enviar

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(chooser);

				if (returnVal == JFileChooser.APPROVE_OPTION){
					File archivoEnviar = chooser.getSelectedFile();
					aceptaAlumnos.envioFichero(archivoEnviar);
				}

			}
		});

		/**
		 * Manejador del evento de pulsar el boton de comenzar la prueba
		 */
		btnComienzoExamen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				boolean temporizar = false;
				int horaLimite;
				int minutosLimite;
				int minutosEnteros;
				int segundosEnteros;
				try{
					Date ahora = new Date(System.currentTimeMillis());

					String textoHora = ftfHoraLImite.getText().trim();
					String hora = textoHora.substring(0, textoHora.indexOf(':')).trim();
					String minuto = textoHora.substring(textoHora.indexOf(':')+1).trim();

					horaLimite = Integer.parseInt(hora);
					minutosLimite = Integer.parseInt(minuto);

					if((horaLimite > 23) || (horaLimite <0) || (minutosLimite <0) || (minutosLimite>59)){
						//hora introducida incorrecta
						throw new NumberFormatException();
					}

					Date limite = new Date(ahora.getYear(), ahora.getMonth(), ahora.getDate(), horaLimite, minutosLimite, 0);

					long diferencia =  limite.getTime()-ahora.getTime();
					double segundosD = Math.floor(diferencia/1000);
					int segundosI = (int) segundosD;

					minutosEnteros = segundosI / 60;
					segundosEnteros = segundosI-60*minutosEnteros;

					if(segundosI>0){
						temporizar = true;
					}



				}catch (Exception e) {
					JOptionPane.showMessageDialog(frmDrmanhattan, "Error al introducir la hora limite");
					return; //No se comienza la prueba con una hora incorrecta
				}

				//TODO mejorar el modo de calcular los minutos

				String asignatura = tfNombreAsignatura.getText().trim();
				String asignaturaSinEspacios = "";

				if(asignatura.isEmpty() || tfDirectorioResultados.getText().trim().isEmpty()){
					JOptionPane.showMessageDialog(frmDrmanhattan, "Rellena todos los datos antes de comenzar");
				}else{

					StringTokenizer tokenizer = new StringTokenizer(asignatura);
					while (tokenizer.hasMoreElements()){
						asignaturaSinEspacios += tokenizer.nextElement();
					}

					String dirResultados = tfDirectorioResultados.getText().trim();

					//comprobacion de permisos
					File directorio = new File(dirResultados);
					boolean permisos = directorio.canWrite();				
					if(!permisos){
						JOptionPane.showMessageDialog(frmDrmanhattan, "No hay permisos de escritura en el directorio seleccionado" +
						", no se recogeran resultados");
					}

					if(dirResultados.charAt(dirResultados.length()-1) == File.separatorChar){
						dirResultados = dirResultados+asignaturaSinEspacios;
					}else{
						dirResultados = dirResultados+File.separator+asignaturaSinEspacios;
					}


					int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "¿Comenzar la prueba con las caracteristicas seleccionadas?");				
					if (JOptionPane.OK_OPTION == confirmado){					
						btnEnviarFichero.setEnabled(false);
						btnExplorarDirResultados.setEnabled(false);
						btnFinExamen.setEnabled(true);
						btnComienzoExamen.setEnabled(false);
						tfDirectorioResultados.setEnabled(false);
						ftfHoraLImite.setEnabled(false);
						tfNombreAsignatura.setEnabled(false);

						aceptaAlumnos.inicioPrueba(temporizar, minutosEnteros, dirResultados.trim(), horaLimite, minutosLimite, segundosEnteros);
						logger.log(Level.INFO, "Comienza la prueba de la asignatura "+tfNombreAsignatura.getText().trim());
					}else{
						//la prueba no comienza, asi que no se hace nada
					}
				}
			}
		});

		/**
		 * Manejador del evento de pulsar el boton para finalizar la prueba.
		 */
		btnFinExamen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "¿Finalizar la prueba?");				
				if (JOptionPane.OK_OPTION == confirmado){
					aceptaAlumnos.finPrueba();
					logger.log(Level.INFO, "Finaliza la prueba a orden del profesor");
					btnFinExamen.setEnabled(false);
				}

			}
		});


		/**
		 * Manejador del evento de cerrar la ventana.
		 */
		frmDrmanhattan.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				if(btnFinExamen.isEnabled()){
					int confirmado = JOptionPane.showConfirmDialog(frmDrmanhattan, "Si cierra, finaliza la prueba");
					if (JOptionPane.OK_OPTION == confirmado){
						logger.log(Level.INFO, "Finaliza la prueba a orden del profesor");
						aceptaAlumnos.finPrueba();
						btnFinExamen.setEnabled(false);
					}
				}else{
					System.exit(0);
				}
			}
		});
	}
}
