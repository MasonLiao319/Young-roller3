import java.awt.*;
import javax.swing.*;

public class SplashGUI extends JPanel {
    JButton nextButton = new JButton("Choose your career");

    SplashGUI(GameGUI gameGUI) {
        setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/Splash1.jpg"));
        JLabel imageLabel = new JLabel(imageIcon);
        add(imageLabel, BorderLayout.CENTER);
        add(new JLabel("Welcome to the Young Roller!", SwingConstants.CENTER), BorderLayout.NORTH);

        // Set up a panel with a flow layout to hold the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextButton.setMargin(new Insets(5, 10, 5, 10)); // Adjust the button's margin if needed
        buttonPanel.add(nextButton); // Add the button to the panel

        // Add the panel instead of the button directly
        add(buttonPanel, BorderLayout.SOUTH);

        nextButton.addActionListener(e -> gameGUI.displayChooseCharacterPage());
    }
}

