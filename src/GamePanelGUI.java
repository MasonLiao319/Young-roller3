import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GamePanelGUI extends JPanel {
    private JTextArea textArea;
    private JLabel playerInfoLabel, opponentInfoLabel, playerNameLabel, opponentNameLabel;
    private JButton rollDiceButton;
    private JButton resetButton; // Button to go back to the previous screen
    private JButton restartButton; // Button to restart the game
    private JButton bagButton; // Button to display bag contents
    private JButton exitButton;
    private GameGUI gameGUI;
    private Player player;
    private Opponent opponent;
    private JPopupMenu bagMenu;
    private Random random;
    private boolean fightStarted = false;

    private boolean goldRewardGiven = false;

    // Constructor that accepts a GameGUI reference
    GamePanelGUI(GameGUI gameGUI, Player player) {
        this.gameGUI = gameGUI;
        this.player = player;
        this.opponent = new Dragon();
        this.bagMenu = new JPopupMenu("Bag Menu");
        this.random = new Random();
        initializeBagMenu();

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(1, 8, 10, 10)); // Including Exit button
        resetButton = createButton("RESET GAME");
        restartButton = createButton("RESTART");
        bagButton = createButton("BAG");
        exitButton = createButton("EXIT");

        topPanel.add(resetButton);
        topPanel.add(restartButton);
        topPanel.add(bagButton);
        topPanel.add(exitButton);

        // Center panel with player and opponent info
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel playerPanel = createPlayerPanel();
        JPanel opponentPanel = createOpponentPanel();

        // VS label
        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(new Font("Arial", Font.BOLD, 40));

        JPanel mainPanel = new JPanel(new GridLayout(1, 3));
        mainPanel.add(playerPanel);
        mainPanel.add(vsLabel);
        mainPanel.add(opponentPanel);

        // Bottom panel with text area and roll button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        rollDiceButton = new JButton("ROLL DICE");
        rollDiceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rollDiceAction();
            }
        });

        JPanel rollDicePanel = new JPanel();
        rollDicePanel.add(rollDiceButton);
        bottomPanel.add(rollDicePanel, BorderLayout.NORTH);
        textArea = new JTextArea(16, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Process"));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        // Layout setup
        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        initializeBagMenu();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setActionCommand(text);
        button.addActionListener(this::handleButtonAction);
        return button;
    }

    private void initializeBagMenu() {
        for (Armory item : player.getInventory().getItems()) {
            bagMenu.add(new JMenuItem(item.getName()));
        }
        // Add gold to the bag menu
        bagMenu.add(new JMenuItem("Gold: " + player.getGold()));
    }
    private void handleButtonAction(ActionEvent e) {
        String command = e.getActionCommand();
        if ("RESET".equals(command)) {
            // Call the method to reset the player's info and return to splash page
            gameGUI.initializeSplashPage();
        } else if ("RESTART".equals(command)) {
            restartGame(); // Restart the game
        } else if ("BAG".equals(command)) {
            openBagMenu(); // Open the bag menu
        } else if ("EXIT".equals(command)) {
            System.exit(0); // Exit the game
        }
    }

    private void openBagMenu() {
        // Ensure the bag menu is up-to-date with the inventory
        bagMenu.removeAll();
        for (Armory item : player.getInventory().getItems()) {
            bagMenu.add(new JMenuItem(item.getName()));
        }
        // Update the gold count
        bagMenu.add(new JMenuItem("Gold: " + player.getGold()));
        // Show the bag menu at the location of the bagButton
        bagMenu.show(bagButton, 0, bagButton.getHeight());
    }
    private void updateBagMenu() {
        bagMenu.removeAll();

        for (Armory item : player.getInventory().getItems()) {
            bagMenu.add(new JMenuItem(item.getName()));
        }
        bagMenu.add(new JMenuItem("Gold: " + player.getGold())); // Add gold to the bag menu
    }
    private void restartGame() {
        // Reset player and opponent HP
        player.setHealthPoints(100);
        opponent = new Dragon();

        // Update the game status
        updateGameStatus();

        // Clear the game text area
        textArea.setText("");

        // Re-enable the roll dice button
        rollDiceButton.setEnabled(true);
    }

    private JPanel createPlayerPanel() {
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerNameLabel = new JLabel(player.getName(), SwingConstants.CENTER);
        playerInfoLabel = new JLabel("Player HP: " + player.getHealthPoints(), SwingConstants.CENTER);

        // Load the player image (assuming player.getCharacterImagePath() returns a valid path)
        ImageIcon playerImage = new ImageIcon(getClass().getResource(player.getCharacterImagePath()));
        JLabel playerImageLabel = new JLabel(playerImage);

        // Add components to the player panel
        playerPanel.add(playerNameLabel, BorderLayout.PAGE_END);
        playerPanel.add(playerInfoLabel, BorderLayout.PAGE_START);
        playerPanel.add(playerImageLabel, BorderLayout.CENTER);

        return playerPanel;
    }

    private JPanel createOpponentPanel() {
        JPanel opponentPanel = new JPanel(new BorderLayout());
        opponentNameLabel = new JLabel(opponent.getName(), SwingConstants.CENTER);
        opponentInfoLabel = new JLabel("Opponent HP: " + opponent.getHealthPoints(), SwingConstants.CENTER);


        ImageIcon opponentImage = new ImageIcon(getClass().getResource("/chinese-dragon1.jpg"));
        JLabel opponentImageLabel = new JLabel(opponentImage);

        // Add components to the opponent panel
        opponentPanel.add(opponentNameLabel, BorderLayout.PAGE_END);
        opponentPanel.add(opponentInfoLabel, BorderLayout.PAGE_START);
        opponentPanel.add(opponentImageLabel, BorderLayout.CENTER);

        return opponentPanel;
    }

    protected void rollDiceAction() {
        int playerRoll = rollDice();
        int opponentRoll = rollDice();

        updateGameText("Player rolls a dice: " + playerRoll);
        updateGameText("Opponent rolls a dice: " + opponentRoll);

        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerRoll > opponentRoll) {
                    int damage = player.attack(opponent);
                    updateGameText(player.getCharacter().getName() + " " +player.getName() + " attacks and deals " + damage + " actual damage to Opponent!");
                } else if (opponentRoll > playerRoll) {
                    int damage = opponent.attack(player);
                    updateGameText("Opponent attacks and deals " + damage + " actual damage to Player!");
                } else {
                    updateGameText("The dice roll is a tie. No damage dealt.");
                }

                updateGameStatus();

                if (!player.isAlive() || !opponent.isAlive()) {
                    endGame();
                }
                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    private int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }

    private void updateGameText(String text) {
        textArea.append(text + "\n");
    }

    private void updateGameStatus() {
        playerInfoLabel.setText("Player HP: " + player.getHealthPoints());
        opponentInfoLabel.setText("Opponent HP: " + opponent.getHealthPoints());
    }

    private void endGame() {
        rollDiceButton.setEnabled(false);
        if (!player.isAlive()) {
            updateGameText("Player has been defeated! Opponent wins!");
        } else if (!opponent.isAlive()) {
            updateGameText("Opponent has been defeated! Player wins!");
            // If the dragon is defeated and the reward hasn't been given yet, give the reward
            if (!goldRewardGiven) {
                player.addGold(10); // Add 10 gold to the player's inventory
                updateGameText("Player has been awarded 10 gold!");
                JOptionPane.showMessageDialog(this, "You have been awarded 10 gold! Check your items in the bag.", "Reward", JOptionPane.INFORMATION_MESSAGE);
                goldRewardGiven = true; // Set the flag so the reward isn't given again
                updateBagMenu(); // Update the bag menu with the new item
            }
        } else {
            updateGameText("The game has ended unexpectedly.");
        }
    }

}
