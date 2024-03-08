import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ChooseCharacterGUI extends JPanel {
    private final JButton nextButton = new JButton("Next");
//    private final JButton backButton = new JButton("Back");
    private final JButton confirmButton = new JButton("Confirm");
    private final JTextField playerNameField = new JTextField(20);
    private final JTextArea characterSelectionArea = new JTextArea(12, 20);
    private final JLabel imageLabel = new JLabel();
    private final JList<String> characterList = new JList<>(new String[]{"Warrior", "Mage"});
    private final ImageIcon warriorImage = new ImageIcon(getClass().getResource("/Warrior.jpg"));
    private final ImageIcon mageImage = new ImageIcon(getClass().getResource("/Mage.jpg"));
    private final ImageIcon defaultImage = new ImageIcon(getClass().getResource("/DefaultCareer.jpg"));
    private Map<String, ImageIcon> characterImages = new HashMap<>();

    private String selectedCharacter = "";
    private GameGUI gameGUI;
    private Player player;

    public ChooseCharacterGUI(GameGUI gameGUI, Player player) {
        this.gameGUI = gameGUI;
        this.player = player;
        setLayout(new BorderLayout());

        characterImages.put("Warrior", warriorImage);
        characterImages.put("Mage", mageImage);

        // Disable Next button initially
        nextButton.setEnabled(false);

        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.add(backButton, BorderLayout.WEST);

        JPanel nameAndConfirmPanel = new JPanel();
        nameAndConfirmPanel.add(new JLabel("Enter Your Name: "));
        nameAndConfirmPanel.add(playerNameField);
        nameAndConfirmPanel.add(confirmButton);
        confirmButton.setEnabled(false);
        topPanel.add(nameAndConfirmPanel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        characterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        characterList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    selectedCharacter = characterList.getSelectedValue();
                    updateCharacterImage(selectedCharacter);
                    checkEnableNextButton();
                }
            }
        });

        playerNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkEnableNextButton();
            }

            public void removeUpdate(DocumentEvent e) {
                checkEnableNextButton();
            }

            public void changedUpdate(DocumentEvent e) {
                checkEnableNextButton();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(characterList);
        listScrollPane.setBorder(new TitledBorder("Select Your Character"));
        listScrollPane.setPreferredSize(new Dimension(150, 200));

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        updateCharacterImage("Default");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, imageLabel);
        splitPane.setDividerLocation(150);
        splitPane.setResizeWeight(0.5);

        confirmButton.addActionListener(e -> confirmSelection());
//        backButton.addActionListener(e -> gameGUI.goBack());
        nextButton.addActionListener(e -> gameGUI.displayChooseWeaponPage());

        add(splitPane, BorderLayout.CENTER);

        characterSelectionArea.setEditable(false);
        JScrollPane characterSelectionScrollPane = new JScrollPane(characterSelectionArea);
        characterSelectionScrollPane.setBorder(new TitledBorder("Character Information"));

        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmPanel.add(confirmButton); // Add the confirm button to its own panel

        // Place the confirmPanel at the SOUTH position of the bottomPanel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(confirmPanel, BorderLayout.SOUTH);
        bottomPanel.add(characterSelectionScrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void confirmSelection() {
        String name = playerNameField.getText().trim();
        if (!name.isEmpty() && selectedCharacter != null && !selectedCharacter.isEmpty()) {
            player.setName(name);
            setPlayerCharacter(selectedCharacter);
            nextButton.setEnabled(true);  // Enable Next button here
            JOptionPane.showMessageDialog(this, "Name set to: " + player.getName() + ", Career set to: " + player.getCharacter() + ". It can not be changed." );
            confirmButton.setEnabled(false);
            characterSelectionArea.setText(getCharacterDetails(selectedCharacter));
        } else {
            JOptionPane.showMessageDialog(null, "Please enter your name and select a character.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void setPlayerCharacter(String characterType) {
        Character character;
        switch (characterType) {
            case "Warrior":
                character = new Warrior(); // Create a new Warrior object with appropriate parameters
                break;
            case "Mage":
                character = new Mage(); // Create a new Mage object with appropriate parameters
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid character selection.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
        player.setCharacter(character); // Use the setCharacter method with the newly created character object
    }

    private void updateCharacterImage(String character) {
        ImageIcon image = characterImages.getOrDefault(character, defaultImage);
        imageLabel.setIcon(image);
    }

    private String getCharacterDetails(String characterType) {
        if (player.getCharacter() != null) {
            return String.format("Player: %s has chosen %s%n%s's Strength: %d%n%s's Defense: %d",
                    player.getName(),
                    characterType,
                    characterType,
                    player.getCharacter().calculateAttack(),
                    characterType,
                    player.getCharacter().calculateDefense());
        }
        return "Character details not available.";
    }
    private void checkEnableNextButton() {
        String name = playerNameField.getText().trim();
        boolean isCharacterSelected = characterList.getSelectedIndex() != -1;
        confirmButton.setEnabled(!name.isEmpty() && isCharacterSelected);
    }

}


