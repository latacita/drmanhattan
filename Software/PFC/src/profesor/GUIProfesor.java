package profesor;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * 
 * Clase que crea la GUI del profesor.
 * 
 * @author Manuel Pando Muñoz
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
	
	private boolean hayEnunciado;
	private String ficheroEnunciado;
	private String asignatura;
	private int minutosExamen;
	
	private HiloAceptadorAlumnos aceptaAlumnos;
	private JPanel panelLog;
	private JScrollPane scrollPane;
	private JTextArea taLog;

	private JButton btnEnviarFichero;
	


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

		asignatura = "";

		frmDrmanhattan = new JFrame();
		frmDrmanhattan.setResizable(false);
		frmDrmanhattan.setTitle("drManhattan");
		frmDrmanhattan.setBounds(100, 100, 620, 598);
		frmDrmanhattan.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDrmanhattan.getContentPane().setLayout(null);


		btnComienzoExamen = new JButton("ComienzoExamen");		
		btnComienzoExamen.setBounds(10, 150, 178, 23);
		frmDrmanhattan.getContentPane().add(btnComienzoExamen);

		btnFinExamen = new JButton("Fin de examen");		
		btnFinExamen.setBounds(10, 199, 178, 23);
		frmDrmanhattan.getContentPane().add(btnFinExamen);

		lblNombreAsignatura = new JLabel("Nombre de la asignatura:");
		lblNombreAsignatura.setBounds(10, 36, 160, 23);
		frmDrmanhattan.getContentPane().add(lblNombreAsignatura);

		tfNombreAsignatura = new JTextField();		
		tfNombreAsignatura.setText("PFC");
		tfNombreAsignatura.setBounds(196, 36, 262, 23);
		frmDrmanhattan.getContentPane().add(tfNombreAsignatura);
		tfNombreAsignatura.setColumns(10);

		lblRecibirResultadosEn = new JLabel("Recibir resultados en:");
		lblRecibirResultadosEn.setBounds(10, 70, 160, 23);
		frmDrmanhattan.getContentPane().add(lblRecibirResultadosEn);

		tfDirectorioResultados = new JTextField();
		tfDirectorioResultados.setText("/home/resultados");
		tfDirectorioResultados.setBounds(196, 70, 262, 23);
		frmDrmanhattan.getContentPane().add(tfDirectorioResultados);
		tfDirectorioResultados.setColumns(10);

		btnExplorarDirResultados = new JButton("Explorar");		
		btnExplorarDirResultados.setBounds(479, 70, 115, 23);
		frmDrmanhattan.getContentPane().add(btnExplorarDirResultados);

		lblHoraLimiteExamen = new JLabel("Hora fin de examen (hh:mm):");
		lblHoraLimiteExamen.setBounds(10, 104, 178, 23);
		frmDrmanhattan.getContentPane().add(lblHoraLimiteExamen);

		tfHoraLimite = new JTextField();
		tfHoraLimite.setHorizontalAlignment(SwingConstants.CENTER);
		tfHoraLimite.setText("10:25");
		tfHoraLimite.setBounds(196, 105, 59, 20);
		frmDrmanhattan.getContentPane().add(tfHoraLimite);
		tfHoraLimite.setColumns(10);
		
		panelLog = new JPanel();
		panelLog.setBorder(new TitledBorder(null, "Eventos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog.setBounds(10, 239, 584, 292);
		frmDrmanhattan.getContentPane().add(panelLog);
		panelLog.setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 23, 564, 258);
		panelLog.add(scrollPane);
		
		taLog = new JTextArea();
		taLog.setEditable(false);
		taLog.setText("Aqu\u00ED van cosas como alumno tal se conecta, acaba, se reciben resultados...");
		scrollPane.setViewportView(taLog);
		
		btnEnviarFichero = new JButton("Enviar fichero");		
		btnEnviarFichero.setBounds(335, 150, 123, 23);
		frmDrmanhattan.getContentPane().add(btnEnviarFichero);
		
		aceptaAlumnos = new HiloAceptadorAlumnos();
		
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
				
				//TODO deshabilitar una vez enviado?

			}
		});

	}
}
		