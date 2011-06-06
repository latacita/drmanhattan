package comun;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Mensaje extends JDialog {


	private static final long serialVersionUID = 1715693826146283381L;

	/**
	 * Crear el dialogo
	 */
	public Mensaje(String texto) {
		setResizable(false);
		setModal(true);
		setTitle("drManhattan - Informacion");
		setBounds(100, 100, 450, 143);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		{
			JLabel lblNewLabel = new JLabel(texto);
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			getContentPane().add(lblNewLabel);
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{165, 117, 0};
			gbl_panel.rowHeights = new int[]{25, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			{
				JButton btnNewButton = new JButton("Aceptar");
				btnNewButton.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent arg0) {						
						setVisible(false);						
					}
				});
				
				GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
				gbc_btnNewButton.anchor = GridBagConstraints.NORTHWEST;
				gbc_btnNewButton.gridx = 1;
				gbc_btnNewButton.gridy = 1;
				panel.add(btnNewButton, gbc_btnNewButton);
			}
		}
		setVisible(true);		
	}

}
