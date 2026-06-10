package com.pavlyk.guildchess;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame finestra = new JFrame();
        finestra.setTitle("GuildChess");
        finestra.setSize(1280, 720);
        finestra.setLocationRelativeTo(null);
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        finestra.setResizable(false);
        Color ripempitivoColore = new Color(28, 31, 57);
        Color cerchiativoColore = new Color(68, 85, 255);
        finestra.setIconImage((new ImageIcon(
            Main.class.getResource("/icons/logo.png")).getImage()));
        finestra.getContentPane().setBackground(ripempitivoColore);
        finestra.setLayout(new BorderLayout());

        JPanel latterale = new JPanel();
        latterale.setPreferredSize(new Dimension(300, 900));
        latterale.setBackground(ripempitivoColore);
        latterale.setBorder(BorderFactory.createLineBorder(cerchiativoColore, 1));

        latterale.add(Box.createVerticalStrut(45)); //spazio
        ImageIcon bannerORG = new ImageIcon(Main.class.getResource("/icons/banner.png"));
        Image banner = bannerORG.getImage().getScaledInstance(248, 27, Image.SCALE_SMOOTH);
        JLabel logobanner = new JLabel(new ImageIcon(banner));
        latterale.add(logobanner);

        JPanel campo = new JPanel();
        campo.setPreferredSize(new Dimension(1300, 900));
        campo.setBackground(ripempitivoColore);
        campo.setBorder(BorderFactory.createLineBorder(cerchiativoColore, 1));
        campo.setLayout(new CardLayout());

        finestra.add(latterale, BorderLayout.WEST);
        finestra.add(campo, BorderLayout.CENTER);

        finestra.setVisible(true);
    }
}