import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame finestra = new JFrame();
        finestra.setTitle("GuildChess");
        finestra.setSize(1600, 900);
        finestra.setLocationRelativeTo(null);
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra.setResizable(false);
        Color DaOne = new Color(37, 36, 60);
        finestra.setIconImage((new ImageIcon(
            Main.class.getResource("/assets/images/icons/logo.png")).getImage()));
        finestra.getContentPane().setBackground(DaOne);
        finestra.setLayout(new BorderLayout());

        JPanel latterale = new JPanel();
        latterale.setPreferredSize(new Dimension(300, 900));
        latterale.setBackground(DaOne);
        latterale.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        JPanel campo = new JPanel();
        campo.setPreferredSize(new Dimension(1300, 900));
        campo.setBackground(DaOne);
        campo.setBorder(BorderFactory.createLineBorder(new Color(166, 166, 166), 1));
        campo.setLayout(new CardLayout());
        finestra.add(latterale, BorderLayout.WEST);
        finestra.add(campo, BorderLayout.CENTER);

        finestra.setVisible(true);
    }
}