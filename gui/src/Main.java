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
        finestra.setIconImage((new ImageIcon(
            Main.class.getResource("/assets/images/icons/logo.png")).getImage()));
        finestra.getContentPane().setBackground(new Color(39, 35, 49));
        finestra.setVisible(true);
    }
}