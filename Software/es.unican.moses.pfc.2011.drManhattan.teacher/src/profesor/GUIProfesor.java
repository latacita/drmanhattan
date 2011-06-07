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
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * Clase que crea la GUI del profesor.
 * 
 * @author Manuel Pando
 *
 */
public class GUIProfesor {

	private JFrame frmDrmanhattan;

	private JTextField tfNombreAsignatura;
	private JTextField tfDirectorioResultados;
	private JTextField tfHoraLimite;

	private JLabel lblNombreAsignatura;
	private JLabel lblRecibirResultadosEn;
	private JLabel lblHoraLimiteExamen;

	private JButton btnComienzoExamen;
	private JButton btnFinExamen;	
	private JButton btnExplorarDirResultados;

	private HiloAceptadorAlumnos aceptaAlumnos;
	private JPanel panelLog;
	private JScrollPane scrollPane;
	private JTextArea taLog;

	private JButton btnEnviarFichero;

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
		frmDrmanhattan.setResizable(false);
		frmDrmanhattan.setTitle("drManhattan - Profesor");
		frmDrmanhattan.setBounds(100, 100, 664, 598);
		frmDrmanhattan.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDrmanhattan.getContentPane().setLayout(null);


		btnComienzoExamen = new JButton("Comienzo de prueba");		
		btnComienzoExamen.setToolTipText("Comienza la prueba, no se admiten nuevos alumnos");
		btnComienzoExamen.setBounds(10, 150, 205, 23);
		frmDrmanhattan.getContentPane().add(btnComienzoExamen);

		btnFinExamen = new JButton("Fin de prueba");		
		btnFinExamen.setToolTipText("Finaliza la prueba");
		btnFinExamen.setEnabled(false);
		btnFinExamen.setBounds(10, 199, 205, 23);
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

		tfDirectorioResultados = new JTextField();
		tfDirectorioResultados.setText("/home/resultados");
		tfDirectorioResultados.setBounds(251, 70, 262, 23);
		frmDrmanhattan.getContentPane().add(tfDirectorioResultados);
		tfDirectorioResultados.setColumns(10);

		btnExplorarDirResultados = new JButton("Explorar");		
		btnExplorarDirResultados.setToolTipText("Navegar por el sistema de ficheros para seleccionar el directorio donde se recibiran los resultados");
		btnExplorarDirResultados.setBounds(534, 70, 115, 23);
		frmDrmanhattan.getContentPane().add(btnExplorarDirResultados);

		lblHoraLimiteExamen = new JLabel("Hora fin de examen (hh:mm):");
		lblHoraLimiteExamen.setBounds(10, 104, 223, 23);
		frmDrmanhattan.getContentPane().add(lblHoraLimiteExamen);

		tfHoraLimite = new JTextField();
		tfHoraLimite.setHorizontalAlignment(SwingConstants.CENTER);
		tfHoraLimite.setText("10:25");
		tfHoraLimite.setBounds(251, 105, 59, 20);
		frmDrmanhattan.getContentPane().add(tfHoraLimite);
		tfHoraLimite.setColumns(10);

		panelLog = new JPanel();
		panelLog.setBorder(new TitledBorder(null, "Eventos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog.setBounds(10, 239, 586, 292);
		frmDrmanhattan.getContentPane().add(panelLog);
		panelLog.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 23, 564, 258);
		panelLog.add(scrollPane);

		taLog = new JTextArea();
		taLog.setEditable(false);
		scrollPane.setViewportView(taLog);

		btnEnviarFichero = new JButton("Enviar fichero");		
		btnEnviarFichero.setToolTipText("Envia un fichero a todos los alumnos conectados");
		btnEnviarFichero.setBounds(308, 150, 150, 23);
		frmDrmanhattan.getContentPane().add(btnEnviarFichero);

		aceptaAlumnos = new HiloAceptadorAlumnos();

		logger = Logger.getLogger("PFC");
		try {
			FileHandler fh;
			fh = new FileHandler("/home/manuel/Escritorio/drManhattan", true);
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
				int minutos = 0;
				try{
					Date ahora = new Date(System.currentTimeMillis());

					String textoHora = tfHoraLimite.getText().trim();
					String hora = textoHora.substring(0, textoHora.indexOf(':')).trim();
					String minuto = textoHora.substring(textoHora.indexOf(':')+1).trim();

					int hor = Integer.parseInt(hora)-ahora.getHours();
					int min = (Integer.parseInt(minuto)-ahora.getMinutes());
					if(min<0){
						min+=60;
					}
					if(ahora.getMinutes() >= Integer.parseInt(minuto)){
						hor--;
					}
					minutos = hor*60 + min;

					if(minutos > 0){
						temporizar = true;
					}

				}catch (Exception e) {
					JOptionPane.showMessageDialog(frmDrmanhattan, "Error al introducir la hora limite");
					return; //No se comienza la prueba con una hora incorrecta
				}

				//TODO mejorar el modo de calcular los minutos

				String asignatura = tfNombreAsignatura.getText().trim();
				String asignaturaSinEspacios = "";

				StringTokenizer tokenizer = new StringTokenizer(asignatura);
				while (tokenizer.hasMoreElements()){
					asignaturaSinEspacios += tokenizer.nextElement();
				}

				String dirResultados = tfDirectorioResultados.getText().trim();

				//comprobacion de permisos
				File directorio = new File(dirResultados);
				boolean permisos = directorio.canWrite();
				System.out.println(dirResultados+" "+permisos);
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
					tfHoraLimite.setEnabled(false);
					tfNombreAsignatura.setEnabled(false);

					aceptaAlumnos.inicioPrueba(temporizar, minutos, dirResultados.trim());
					logger.log(Level.INFO, "Comienza la prueba de la asignatura "+tfNombreAsignatura.getText().trim());
				}else{
					//la prueba no comienza, asi que no se hace nada
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
				}

			}
		});

	}
}
