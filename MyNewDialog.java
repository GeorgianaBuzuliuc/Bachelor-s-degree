import javax.swing.JDialog;
import java.awt.Color;
import javax.swing.JLabel;

public class MyNewDialog extends JDialog {
	private JLabel textLabel; // Variabilă pentru etichetă de text
	
    public MyNewDialog() {
        super(); // Apel constructor JDialog
        
        // Configurarea ferestrei noi
		
        setTitle("Informatii");
        setSize(240, 600);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// Asigură-te că ai o imagine numită "background.jpg" în directorul proiectului
		getContentPane().setBackground(new Color(221,160,221)); // Exemplu de fundal roșu
        setVisible(true);
		
		// Restul codului constructorului
        textLabel = new JLabel(); // Inițializează eticheta de text
        add(textLabel); // Adaugă eticheta în conținutul ferestrei
    }
	// Metoda pentru a seta textul etichetei
    public void setText(String text) {
        textLabel.setText(text); // Setează textul etichetei
    }
}