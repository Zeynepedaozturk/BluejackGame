import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlueJack {
    static String[] colors = {"Blue", "Yellow", "Red", "Green"};
    static final String THROWN_CARD = "X";

    static final String GAME_HISTORY_FILE = "gamehistory.txt";

    public static void main(String[] args) {
        System.out.println("-- WELCOME TO THE BLUEJACK --");
        System.out.println("       -- THE CARDS --       ");
        System.out.println("----------------------------------");

        String[] computerCards = new String[10];
        String[] player2Cards = new String[10];

        dealTheCards(computerCards, colors);//the cards are randomly distributed to the players.
        dealAdditionalCards(computerCards, colors);//the cards are randomly distributed to the players.

        dealTheCards(player2Cards, colors);//the cards are randomly distributed to the players.
        dealAdditionalCards(player2Cards, colors);//the cards are randomly distributed to the players.
 
        player2Cards = selectCards(player2Cards);// players can only choose 4 cards

        System.out.println("Computer Cards :");
        printTheCards(computerCards);//prints the player's cards on the screen

        System.out.println(" ");

        System.out.println("Player2 Cards:");
        printTheCards(player2Cards);

        playTurn(player2Cards);/*manages the player's turn. It provides the player with options and 
        *performs operations according to the user's choice.
        */

        throwComputerCards(computerCards);//controls the computer's card throwing.

        System.out.println("After computer playing cards:");
        System.out.println("-----------------------------------------");
        System.out.println("Computer Cards :");
        printTheCards(computerCards);

        System.out.println(" ");

        System.out.println("Player2 Cards:");
        printTheCards(player2Cards);

        playTurn(player2Cards);/*After the computer throws the card, it prints 
        *the card status on the screen and gives the second player the opportunity to play his turn.
        */

        determineWinner(computerCards, player2Cards);//the winner of the game

        String[] result = determineWinner(computerCards, player2Cards);
        String playerName = result[0];
        int playerScore = Integer.parseInt(result[1]);

        // Display the result
        System.out.println("Computer's Sum: " + calculateSum(computerCards));
        System.out.println("Player's Sum: " + calculateSum(player2Cards));
        System.out.println("Result: " + result[2]);

        // Update and display the top 10 scores
        updateTop10Scores(playerName, playerScore);
        displayTop10Scores();

        writeGameHistory(playerName, playerScore,computerCards);
        displayGameHistory();
    }

    public static void dealTheCards(String[] playerCards, String[] color) {
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            int colorIndex = random.nextInt(color.length);
            int numberIndex = random.nextInt(10) + 1;

            String card = color[colorIndex] + " " + numberIndex;
            playerCards[i] = card;
        }
    }

    public static void dealAdditionalCards(String[] pCards, String[] color) {
    	//joker/5cards 
    	Random random = new Random();

        for (int i = 5; i < 10; i++) {
            if (random.nextDouble() <= 0.2) {
                // Joker card
                pCards[i] = (random.nextBoolean() ? "Joker + or -" : "Joker x2");
            } else {
                // Normal card
                int cIndex = random.nextInt(color.length);
                int nIndex = random.nextInt(10) + 1;
                pCards[i] = color[cIndex] + " " + nIndex;
            }
        }
    }

    public static void printTheCards(String[] cards) {
        for (String card : cards) {
            System.out.println(card);
        }
    }

    public static void throwCard(String[] playerCards, int index) {
    	//it serves to discard the player's card in a certain index.
    	if (index >= 0 && index < playerCards.length) {
            playerCards[index] = THROWN_CARD;
        }
    }

    public static void throwComputerCard(String[] computerCards) {
    	//computer randomly discards a card
    	Random random = new Random();
        int index = random.nextInt(computerCards.length);
        computerCards[index] = THROWN_CARD;
    }

    public static void playTurn(String[] playerCards) {
    	/*it allows the player to play his turn. 
    	*It provides the player with various options and performs 
    	*operations according to the user's choice.
    	**/
    	Scanner scanner = new Scanner(System.in);

        int sum = calculateSum(playerCards);
        boolean endTurn = false;

        while (!endTurn) {
            System.out.println("Player's Sum: " + sum);
            System.out.println("Options: 1. Stand, 2. Play a card, 3. End turn");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    endTurn = true;
                    break;
                case 2:
                    System.out.print("Enter the index of the card to play: ");
                    int cardIndex = scanner.nextInt();
                    if (cardIndex >= 0 && cardIndex < playerCards.length && !playerCards[cardIndex].equals(THROWN_CARD)) {
                        sum += getCardValue(playerCards[cardIndex]);
                        playerCards[cardIndex] = THROWN_CARD;
                        endTurn = true;
                    } else {
                        System.out.println("Invalid card index.");
                    }
                    break;
                case 3:
                    endTurn = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static int calculateSum(String[] playerCards) {
    	//calculates the total value of the cards

    	int sum = 0;
        for (String card : playerCards) {
            if (!card.equals(THROWN_CARD)) {
                sum += getCardValue(card);
            }
        }
        return sum;
    }

    public static int getCardValue(String card) {
    	/* it takes the value of a card. If the card is a Joker, 
    	*it returns the value as 0; otherwise, it returns the number value of the card.
        */
        if (card.startsWith("Joker")) {
            return 0; // You can adjust this value based on your game rules
        } else {
            // Extract the number part of the card and convert it to an integer
            return Integer.parseInt(card.split(" ")[1]);
        }
    }

    public static String[] determineWinner(String[] computerCards, String[] playerCards) {
    	//determines the winner of the game by comparing the total values of the players' cards.
    	int computerSum = calculateSum(computerCards);
        int playerSum = calculateSum(playerCards);

        System.out.println("Computer's Sum: " + computerSum);
        System.out.println("Player's Sum: " + playerSum);

        String result;
        if (playerSum > 20) {
            result = "Player busts. Computer wins!";
        } else if (computerSum > 20) {
            result = "Computer busts. Player wins!";
        } else if (playerSum == 20) {
            result = "Player wins with exactly 20 points!";
        } else if (computerSum == 20) {
            result = "Computer wins with exactly 20 points!";
        } else if (playerSum > computerSum) {
            result = "Player wins!";
        } else if (playerSum < computerSum) {
            result = "Computer wins!";
        } else {
            result = "It's a tie!";
        }

        // Return the player's name, score, and the result
        return new String[]{ "Player", String.valueOf(playerSum), result };
    }


    public static String[] selectCards(String[] playerCards) {
        // player choose from their own cards
    	Scanner scanner = new Scanner(System.in);

        System.out.println("Select 4 cards from your hand:");
        String[] selectedCards = new String[4];

        for (int i = 0; i < 4; i++) {
            printTheCards(playerCards);
            System.out.print("Enter the index of the card to select: ");
            System.out.println("(The index of the cards starts from zero)");
            int cardIndex = scanner.nextInt();
            System.out.println("-----------------------------------------");

            if (cardIndex >= 0 && cardIndex < playerCards.length && !playerCards[cardIndex].equals(THROWN_CARD)) {
                selectedCards[i] = playerCards[cardIndex];
                playerCards[cardIndex] = THROWN_CARD; //The card has been selected 
            } else {
                System.out.println("Invalid card index. Please try again.");
                i--; // In case of invalid selection, it is reduced so that it remains at the same level.
            }
        }

        //This section removes the selected cards from the original player cards.
        for (String selectedCard : selectedCards) {
            for (int j = 0; j < playerCards.length; j++) {
                if (playerCards[j].equals(selectedCard)) {
                    playerCards[j] = THROWN_CARD;
                    break;
                }
            }
        }

        return selectedCards;
    }

    public static void throwComputerCards(String[] computerCards) {
    	//computer randomly throws 4 cards that it selects
    	Random random = new Random();
        int cardsToThrow = 4; 

        for (int i = 0; i < cardsToThrow; i++) {
            int index;
            do {
                index = random.nextInt(computerCards.length);
            } while (computerCards[index].equals(THROWN_CARD));

            computerCards[index] = THROWN_CARD;
        }
    }


    public static void updateTop10Scores(String playerName, int playerScore) {
        // Create the file if it doesn't exist
        createFileIfNotExists("top10scores.txt");

        // Read existing top 10 scores from file
        Player[] top10Scores = readTop10Scores();

        // Add the current player to the top 10 scores
        Player currentPlayer = new Player(playerName, playerScore);
        int index = findInsertionIndex(top10Scores, currentPlayer);
        if (index < top10Scores.length) {
            // Shift elements to make room for the new score
            System.arraycopy(top10Scores, index, top10Scores, index + 1, top10Scores.length - index - 1);
            top10Scores[index] = currentPlayer;
        }

        // Write the updated top 10 scores to the file
        writeTop10Scores(top10Scores);

    }

    public static Player[] readTop10Scores() {
        Player[] top10Scores = new Player[10];
        try (BufferedReader reader = new BufferedReader(new FileReader("top10scores.txt"))) {
            for (int i = 0; i < 10; i++) {
                String line = reader.readLine();
                if (line != null) {
                    String[] parts = line.split(",");
                    top10Scores[i] = new Player(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return top10Scores;
    }

    public static void writeTop10Scores(Player[] top10Scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("top10scores.txt"))) {
            for (Player player : top10Scores) {
                if (player != null) {
                    writer.write(player.getName() + "," + player.getScore());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayTop10Scores() {
    	//prints the top 10 scores on the screen.
        System.out.println("Top 10 Scores:");
        Player[] top10Scores = readTop10Scores();
        for (int i = 0; i < top10Scores.length; i++) {
            if (top10Scores[i] != null) {
                System.out.println((i + 1) + ". " + top10Scores[i]);
            }
        }
    }
    

    static class Player {
        private String name;
        private int score;

        public Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return name + " - " + score + " points";
        }
    }

    
    private static int findInsertionIndex(Player[] top10Scores, Player currentPlayer) {
    	/*finds the index of the appropriate place for a 
    	*new player's score to be added to the top 10 score list. 
    	*The new score is placed in the correct position by comparing it with the existing scores.
    	*/
    	for (int i = 0; i < top10Scores.length; i++) {
            if (top10Scores[i] == null || currentPlayer.getScore() > top10Scores[i].getScore()) {
                return i;
            }
        }
        return top10Scores.length;
    }

    public static void createFileIfNotExists(String fileName) {
    	/*checks whether the specified file exists. If there is no file, 
         * it creates a new file. This method ensures the existence of the 
         * file used to store the top 10 scores.
    	 */
    	File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeGameHistory(String playerName, int playerScore, String[] computerCards) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GAME_HISTORY_FILE, true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
            String date = dateFormat.format(new Date());
            writer.write(playerName + ":" + playerScore + " - Computer:" + (calculateSum(computerCards) - playerScore) + ", " + date);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void displayGameHistory() {
        System.out.println("Game History:");
        try (BufferedReader reader = new BufferedReader(new FileReader(GAME_HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}