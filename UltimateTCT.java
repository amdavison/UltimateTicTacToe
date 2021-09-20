import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * UltimateTCT.java creates and runs Ultimate Tic Tac Toe GUI.
 * @author Andrew Davison
 *
 */
public class UltimateTCT {
    private Board mainBoard = new Board();
    private String p1, p2, lastBoard, lastHumanBoard, borderTitle;
    private char player1 = 'X', player2 = 'O', currentPlayer, lastPlayer;
    private int gameCount, countX, countO, averageX, averageO, winX, winO,
            lastPosition, lastHumanPosition, completedTurns, completedGames = 0, numberOfPlayers = 1;
    private boolean isPlayer1, gameEnded, flag = false;
    private SecureRandom randomNumber;
    private ArrayList<Integer> movesPlayerX = new ArrayList<>();
    private ArrayList<Integer> movesPlayerO = new ArrayList<>();
    private ArrayList<String> availableBoards = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));

    private ArrayList<Board> boardList = new ArrayList<>();
    private ArrayList<JPanel> localBoards = new ArrayList<>();

    private JFrame frame;
    private JPanel gameStatus, playing, manage, gamePanel, statsPanel;
    private JButton playerO, playerX, restart, undo, playingStats;
    private JLabel nowPlaying, manageGame, wins, games, moves;
    private JTextField winPercentage, totalGames, averagePerWin;
    private Border border;
    private Color bg;

    // UltimateTCT class constructor
    public UltimateTCT() {
        this.mainBoard.setName("Main"); // set name for main board

        // create GUI items
        this.frame = new JFrame("Tic Tac Toe Game");
        this.frame.setSize(800, 600);
        this.frame.setLocation(250, 75);

        this.gameStatus = new JPanel();
        this.gameStatus.setLayout(new GridLayout(1, 2));
        this.playing = new JPanel();
        this.manage = new JPanel();
        this.gamePanel = new JPanel();
        this.gamePanel.setLayout(new GridLayout(3, 3));

        this.statsPanel = new JPanel();

        this.nowPlaying = new JLabel("Now Playing: ");
        this.playerX = new JButton("Player X");
        this.playerO = new JButton("Player O");
        this.bg = playerO.getBackground();
        this.playing.add(nowPlaying);
        this.playing.add(playerX);
        this.playing.add(playerO);
        this.manageGame = new JLabel("Manage Game: ");
        this.restart = new JButton("Restart");
        this.undo = new JButton("Undo");
        this.undo.setEnabled(false);
        this.manage.add(manageGame);
        this.manage.add(restart);
        this.manage.add(undo);

        // loop to create board panels and boards
        for (int boardNumber = 1; boardNumber < 10; boardNumber++) {
            this.borderTitle = "Board " + boardNumber;
            this.border = BorderFactory.createTitledBorder(borderTitle);
            final JPanel gameBoard = new JPanel(); // local gameBoard
            gameBoard.setLayout(new GridLayout(3, 3));
            gameBoard.setBackground(Color.GREEN);
            gameBoard.setBorder(border);
            gameBoard.setName(Integer.toString(boardNumber));
            this.localBoards.add(gameBoard);
            Board board = new Board(); // 2 dimensional array representation of board
            board.setName(Integer.toString(boardNumber));
            this.boardList.add(board);
            int boardPosition = 1;
            for (int row = 0; row < 3; row++) { // iterate through rows and columns for creating and setting buttons
                for (int col = 0; col < 3; col++) {
                    final JButton button = new JButton();
                    button.setName(Integer.toString(boardPosition++));
                    button.addActionListener(new ActionListener() { // create local listener for board buttons
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button.setText(Character.toString(currentPlayer)); // update button text
                            button.setEnabled(false); // disable button
                            humanMove(board, Integer.parseInt(button.getName())); // call humanMove method for physical button push
                            if (p2.contentEquals("computer") && !gameEnded) { // check for computer player and if gameEnded
                                playAI(); // call playAI method
                            } else
                            if (gameEnded == true) { // if gameEnded
                                undo.setEnabled(false); // disable undo button
                                lockAll(); // lock all local boards
                                playerX.setBackground(bg); // reset player buttons
                                playerX.setForeground(Color.BLACK);
                                playerO.setBackground(bg);
                            } else { // print current player message to console
                                System.out.println("\nPlayer " + currentPlayer + "'s move");
                            }
                        } // end of inner method
                    }); // end ActionListener
                    gameBoard.add(button); // add button to local board
                } // end column for(..)
            } // end row for(..)
            this.gamePanel.add(gameBoard); // add local board to the gamePanel
        } // end for(..)

        // set other items in GUI
        this.playingStats = new JButton("Playing Stats");
        this.wins = new JLabel("Win %: ");
        this.winPercentage = new JTextField(15);
        this.winPercentage.setEditable(false);
        this.games = new JLabel("Total # of games: ");
        this.totalGames = new JTextField(3);
        this.totalGames.setEditable(false);
        this.moves = new JLabel("Average # of moves per win: ");
        this.averagePerWin = new JTextField(8);
        this.averagePerWin.setEditable(false);

        this.gameStatus.add(playing);
        this.gameStatus.add(manage);

        this.statsPanel.add(playingStats);
        this.statsPanel.add(wins);
        this.statsPanel.add(winPercentage);
        this.statsPanel.add(games);
        this.statsPanel.add(totalGames);
        this.statsPanel.add(moves);
        this.statsPanel.add(averagePerWin);

        this.frame.add(gameStatus, BorderLayout.NORTH);
        this.frame.add(gamePanel, BorderLayout.CENTER);
        this.frame.add(statsPanel, BorderLayout.SOUTH);

        // create action listeners for management buttons
        this.restart.addActionListener(new ButtonListener());
        this.undo.addActionListener(new ButtonListener());
        this.playingStats.addActionListener(new ButtonListener());

        // set default close operation and frame to visible
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);

        // Opening message pane with game instructions
        JOptionPane.showMessageDialog(null, "Welcome to Ultimate Tic Tac Toe!\n"
                + "Rules:\n"
                + "    1.  The game is played with 9 local boards that create one main board.\n"
                + "    2.  First player is chosen at random and current player is highlighted at\n"
                + "          the top of the window.\n"
                + "    3.  The first player has a choice of any position in any local board.\n"
                + "    4.  After a move is made, the next move MUST be played on the local board that\n"
                + "          corresponds with the position of the last move.\n"
                + "    5.  For example, if Player X chooses position 4 on local board 6 then, Player O\n"
                + "          must play an available position on local board 4.\n"
                + "    6.  Once a board is closed, by win or tie, if the next move was supposed to be on\n"
                + "          that board, the next player may choose any position on the main board.\n"
                + "    7.  In a two player game the last move may be undone and the player can remake\n"
                + "          their move, but will be penalized with the extra move.\n"
                + "    8.  If playing against the computer, undo deletes the last AI and human moves.\n"
                + "    9.  The human player then can remake their move, but extra move plenalty still\n"
                + "          applies.\n\n"
                + "Good luck and be sure to pay attention!\n\n"
                + "Press \"Ok\" to start new game!");
        initializeGame(); // initialize new game
    } // end of constructor

    //  ButtonListener class for game management buttons
    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            JButton button = (JButton) ae.getSource(); // create button reference for management buttons
            if (button.getText().contentEquals("Restart")) { // restart button
                System.out.println("\nRestart pressed...\n");
                completedTurns = 0; // clear completedTurns
                undo.setEnabled(false); // disable undo button
                for (JPanel gameBoard : localBoards) { // iterate through local boards
                    gameBoard.setBackground(Color.GREEN); // set panel color to green
                    ((TitledBorder) gameBoard.getBorder()).setTitleColor(Color.BLACK); // set border title to black
                    for (Component c : gameBoard.getComponents()) { // iterate through panel components
                        c.setEnabled(true); // enable all components
                        ((AbstractButton) c).setText(""); // clear text on all board buttons
                    } // end for each component(..)
                } // end for each gameBoard(..)
                for (Board board : boardList) { // iterate through board list
                    System.out.println("\nResetting Board " + board.getName());
                    board.resetBoard(); // reset board
                } // end for each board
                availableBoards.clear(); // clear availableBoards list
                for (int board = 1; board < 10; board++) { // load board list with available boards
                    availableBoards.add(Integer.toString(board));
                } // end for(..)
                System.out.println("\nResetting Main Board");
                mainBoard.resetBoard(); // reset mainBoard
                System.out.println();
                averagePerWin.setText(""); // clear text in average text field
                winPercentage.setText(""); // clear text in percentage text field
                movesPlayerX.clear(); // clear player X moves list
                movesPlayerO.clear(); // clear player O moves list
                lastBoard = null; // set lastBoard reference to null
                lastHumanBoard = null;
                lastPosition = 0; // set last Position reference to 0
                lastHumanPosition = 0;
                if (gameEnded == false) {
                    gameCount--; // decrement gameCount
                    initializeGame(); // initialize new game
                } else {
                    initializeGame(); // initialize new game
                }
            } else
            if (button.getText().contentEquals("Undo")) { // undo button
                System.out.println("\nUndo pressed...");
                undoMove();
                if (numberOfPlayers == 1) {
                    System.out.println("Completed undo for Player O...");
                    countO--;
                    lastBoard = lastHumanBoard;
                    lastPosition = lastHumanPosition;
                    undoMove();
                }
            } else
            if (button.getText().contentEquals("Playing Stats")) { // playing statistics
                System.out.println("\nPlaying Stats pressed..."); // print console messages
                System.out.println("\tPlayer X's win count: " + winX);
                System.out.println("\tPlayer O's win count: " + winO);
                try { // try to calculate percentages and set percentage text field
                    double winPerX = Math.round((double)(winX * 100) / (double) completedGames); // calculate win percentage for X
                    double winPerO = Math.round((double)(winO * 100) / (double) completedGames); // calculate win percentage for O
                    winPercentage.setText("X: " + winPerX + "%   O: " + winPerO + "%   T: " + (double)(100 - (winPerX + winPerO)) + "%");
                } catch (ArithmeticException zero) { // accept situation that there are no completed games (divisible by 0 situation)
                    winPercentage.setText("X: 0.0%   O: 0.0%   T: 0.0%"); // set 0% messages for X, O, and ties
                }
                totalGames.setText(Integer.toString(gameCount)); // set total games text field
                averagePerWin.setText("X: " + averageX + " / O: " + averageO); // set average text field
            } // end else if(..)
        } // end of actionPerformed method

    } // end of inner class

    // Board class
    class Board {
        private char[][] board = new char[3][3]; // 2 dimensional array of characters
        private String name; // name reference

        // Board class constructor
        public Board() {
            this.setName(""); // set name to empty string
            for (int row = 0; row < 3; row++) { // iterate through rows and columns of array
                for (int col = 0; col < 3; col++) {
                    this.board[row][col] = '-'; // initialize board with '-' characters
                } // end column for(..)
            } // end row for(..)
        } // end of Board constructor

        /**
         * Sets player character in the row and column of board.
         * @param row row location
         * @param col column location
         * @param player player character
         */
        public void setElement(int row, int col, char player) {
            this.board[row][col] = player; // set player character in row/col location
            System.out.println();
            drawBoard(); // draw board to console
        } // end setElement method

        /**
         * Resets board to initial character values '-'.
         */
        public void resetBoard() {
            for (int row = 0; row < 3; row++) { // iterate through rows and columns of board
                for (int col = 0; col < 3; col++) {
                    this.board[row][col] = '-'; // set row/column to empty space character
                } // end column for(..)
            } // end row for(..)
            drawBoard(); // draw board to console
        } // end resetBoard method

        /**
         * Returns character of player who won board or '-'.
         * @param board
         * @return
         */
        public char checkWin() {
            // checks rows
            for (int row = 0; row < 3; row++) {
                if (board[row][0] == board[row][1] && board[row][1] == board[row][2] && board[row][0] != '-') {
                    return board[row][0];
                }
            }
            // checks columns
            for (int col = 0; col < 3; col++) {
                if (board[0][col] == board[1][col] && board[1][col] == board[2][col] && board[0][col] != '-') {
                    return board[0][col];
                }
            }
            // check diagonals
            if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '-') {
                return board[0][0];
            }
            if (board[2][0] == board[1][1] && board[1][1] == board[0][2] && board[2][0] != '-') {
                return board[2][0];
            }
            return '-'; // return empty character if no win
        } // end checkWin method

        /**
         * If board is full returns true if a board has no winner, otherwise false
         * @param board
         * @return true if board is a tie, otherwise false
         */
        public boolean checkTie() {
            for (int row = 0; row < 3;  row++) { // iterate through rows and columns of board
                for (int col = 0; col < 3; col++) {
                    if (this.board[row][col] == '-') { // if empty character found return false
                        return false;
                    } // end if(..)
                } // end column for(..)
            } // end row for(..)
            return true; // return true if board is full
        } // end checkTie method

        /**
         * Prints board to console.
         */
        public void drawBoard() {
            System.out.println("Board " + getName() + ":"); // print board header
            for (int row = 0; row < 3; row++) { // iterate through rows and columns
                for (int col = 0; col < 3; col++) {
                    System.out.print(" " + this.board[row][col] + " "); // print board element
                } // end column for(..)
                System.out.println(); // after three elements print new line
            } // end row for(..)
        } // end drawBoard method

        /**
         * Checks board for a block situation, where AI's opponent would win, and returns position of block, 0 otherwise.
         * @return integer of position to play
         */
        public int checkBlock() {
            System.out.println("\tChecking for a block...");
            // position 1 situation
            if (((this.board[0][1] == lastPlayer && this.board[0][2] == lastPlayer) ||
                    (this.board[1][0] == lastPlayer && this.board[2][0] == lastPlayer) ||
                    (this.board[1][1] == lastPlayer && this.board[2][2] == lastPlayer)) && this.board[0][0] == '-') {
                return 1;
            } else // position 2 situation
                if (((this.board[0][0] == lastPlayer && this.board[0][2] == lastPlayer) ||
                        (this.board[1][1] == lastPlayer && this.board[2][1] == lastPlayer)) && this.board[0][1] == '-') {
                    return 2;
                } else // position 3 situation
                    if (((this.board[0][0] == lastPlayer && this.board[0][1] == lastPlayer) ||
                            (this.board[1][2] == lastPlayer && this.board[2][2] == lastPlayer) ||
                            (this.board[1][1] == lastPlayer && this.board[2][0] == lastPlayer)) && this.board[0][2] == '-') {
                        return 3;
                    } else // position 4 situation
                        if (((this.board[0][0] == lastPlayer && this.board[2][0] == lastPlayer) ||
                                (this.board[1][1] == lastPlayer && this.board[1][2] == lastPlayer)) && this.board[1][0] == '-') {
                            return 4;
                        } else // position 5 situation
                            if (((this.board[0][0] == lastPlayer && this.board[2][2] == lastPlayer) ||
                                    (this.board[0][2] == lastPlayer && this.board[2][0] == lastPlayer) ||
                                    (this.board[0][1] == lastPlayer && this.board[2][1] == lastPlayer) ||
                                    (this.board[1][0] == lastPlayer && this.board[1][2] == lastPlayer)) && this.board[1][1] == '-') {
                                return 5;
                            } else // position 6 situation
                                if (((this.board[0][2] == lastPlayer && board[2][2] == lastPlayer) ||
                                        (this.board[1][0] == lastPlayer && this.board[1][1] == lastPlayer)) && this.board[1][2] == '-') {
                                    return 6;
                                } else // position 7 situation
                                    if (((this.board[0][0] == lastPlayer && this.board[1][0] == lastPlayer) ||
                                            (this.board[0][2] == lastPlayer && this.board[1][1] == lastPlayer) ||
                                            (this.board[2][1] == lastPlayer && this.board[2][2] == lastPlayer)) && this.board[2][0] == '-') {
                                        return 7;
                                    } else // position 8 situation
                                        if (((this.board[0][1] == lastPlayer && this.board[1][1] == lastPlayer) ||
                                                (this.board[2][0] == lastPlayer && this.board[2][2] == lastPlayer)) && this.board[2][1] == '-') {
                                            return 8;
                                        } else // position 9 situation
                                            if (((this.board[0][2] == lastPlayer && this.board[1][2] == lastPlayer) ||
                                                    (this.board[0][0] == lastPlayer && this.board[1][1] == lastPlayer) ||
                                                    (this.board[2][0] == lastPlayer && this.board[2][1] == lastPlayer)) && this.board[2][2] == '-') {
                                                return 9;
                                            }
            return 0; // return 0 if no optimal position found
        } // end of checkBlock method

        /**
         * Checks board for an AI instant win situation and returns position of block, 0 otherwise.
         * @return integer of position to play
         */
        public int getNextMove() {
            int blockPosition = checkBlock(); // check for a block
            if (blockPosition != 0) { // if block position found
                return blockPosition; // return block position
            } else// position 1 situation
                if (((this.board[0][1] == currentPlayer && this.board[0][2] == currentPlayer) ||
                        (this.board[1][0] == currentPlayer && this.board[2][0] == currentPlayer) ||
                        (this.board[1][1] == currentPlayer && this.board[2][2] == currentPlayer)) && this.board[0][0] == '-') {
                    return 1;
                } else// position 2 situation
                    if (((this.board[0][0] == currentPlayer && this.board[0][2] == currentPlayer) ||
                            (this.board[1][1] == currentPlayer && this.board[2][1] == currentPlayer)) && this.board[0][1] == '-') {
                        return 2;
                    } else// position 3 situation
                        if (((this.board[0][0] == currentPlayer && this.board[0][1] == currentPlayer) ||
                                (this.board[1][2] == currentPlayer && this.board[2][2] == currentPlayer) ||
                                (this.board[1][1] == currentPlayer && this.board[2][0] == currentPlayer)) && this.board[0][2] == '-') {
                            return 3;
                        } else// position 4 situation
                            if (((this.board[0][0] == currentPlayer && this.board[2][0] == currentPlayer) ||
                                    (this.board[1][1] == currentPlayer && this.board[1][2] == currentPlayer)) && this.board[1][0] == '-') {
                                return 4;
                            } else// position 5 situation
                                if (((this.board[0][0] == currentPlayer && this.board[2][2] == currentPlayer) ||
                                        (this.board[0][2] == currentPlayer && this.board[2][0] == currentPlayer) ||
                                        (this.board[0][1] == currentPlayer && this.board[2][1] == currentPlayer) ||
                                        (this.board[1][0] == currentPlayer && this.board[1][2] == currentPlayer)) && this.board[1][1] == '-') {
                                    return 5;
                                } else// position 6 situation
                                    if (((this.board[0][2] == currentPlayer && this.board[2][2] == currentPlayer) ||
                                            (this.board[1][0] == currentPlayer && this.board[1][1] == currentPlayer)) && this.board[1][2] == '-') {
                                        return 6;
                                    } else// position 7 situation
                                        if (((this.board[0][0] == currentPlayer && this.board[1][0] == currentPlayer) ||
                                                (this.board[0][2] == currentPlayer && this.board[1][1] == currentPlayer) ||
                                                (this.board[2][1] == currentPlayer && this.board[2][2] == currentPlayer)) && this.board[2][0] == '-') {
                                            return 7;
                                        } else// position 8 situation
                                            if (((this.board[0][1] == currentPlayer && this.board[1][1] == currentPlayer) ||
                                                    (this.board[2][0] == currentPlayer && this.board[2][2] == currentPlayer)) && this.board[2][1] == '-') {
                                                return 8;
                                            } else// position 9 situation
                                                if (((this.board[0][2] == currentPlayer && this.board[1][2] == currentPlayer) ||
                                                        (this.board[0][0] == currentPlayer && this.board[1][1] == currentPlayer) ||
                                                        (this.board[2][0] == currentPlayer && this.board[2][1] == currentPlayer)) && this.board[2][2] == '-') {
                                                    return 9;
                                                }
            return 0; // return 0 if no optimal position found
        } // end of getNextMove method

        /**
         * Checks board to see if move is valid, i.e. empty space.
         * @param row row location
         * @param col column location
         * @return true if move is valid, otherwise false
         */
        public boolean validateMove(int row, int col) {
            if (this.board[row][col] == '-') { // check if board row/col is empty
                return true; // return true
            }
            return false; // return false if space is taken
        } // end validateMove method

        // returns name of board
        public String getName() {
            return name;
        } // end getName method

        // sets name of board
        public void setName(String name) {
            this.name = name;
        } // end setName method

    } // end of inner Board class

    /**
     * Locks all local boards in GUI.
     */
    public void lockAll() {
        for (JPanel gameBoard : localBoards) { // iterate through localBoards list
            if (gameBoard.getBackground() == Color.BLUE || gameBoard.getBackground() == Color.CYAN ||
                    gameBoard.getBackground() == Color.ORANGE) { // check if board has been won or tied
                continue; // skip board
            } // end if(..)
            gameBoard.setBackground(Color.RED); // set local board color to red
            for (Component c : gameBoard.getComponents()) { // iterate through components
                if (((JButton) c).getText() == "") { // if previously open button
                    c.setEnabled(false); // disable button
                } // end if(..)
            } // end for each component(..)
        } // end for each gameBoard(..)
    } // end of lockAll method

    /**
     * Locks all gameBoards except for the next board to be played on.
     * @param targetBoard next board
     */
    public void lockBoard(int targetBoard) {
        boolean found = false; // found reference initialized to false
        for (JPanel gameBoard : localBoards) { // iterate through localBoards list
            if (gameBoard.getName().equalsIgnoreCase(Integer.toString(targetBoard))) { // if targetBoard
                found = true; // set found to true
                if ((gameBoard.getBackground() == Color.BLUE || gameBoard.getBackground() == Color.CYAN ||
                        gameBoard.getBackground() == Color.ORANGE) && found) { // if target is already won or tied and found
                    flag = true; // set flag to true
                } // end gameBoard color if(..)
                continue; // skip board
            } // end gameBoard name if(..)
            if (gameBoard.getBackground() == Color.BLUE || gameBoard.getBackground() == Color.CYAN ||
                    gameBoard.getBackground() == Color.ORANGE) { // check for other win or tie boards
                continue; // skip board
            } // end gameBoard color if(..)
            gameBoard.setBackground(Color.RED); // set localBoard color to red
            for (Component c : gameBoard.getComponents()) { // iterate through components
                c.setEnabled(false); // disable component
            } // end for each component(..)
        } // end for each gameBoard(..)
        if (flag == true) { // check if flag is true
            unlockAll(); // call unlockAll to open main board
        } // end flag if(..)
    } // end of lockBoard method

    /**
     * Unlocks all localBoards
     */
    public void unlockAll() {
        flag = false; // set flag to false
        for (JPanel gameBoard : localBoards) { // iterate through localBoards list
            if (gameBoard.getBackground() == Color.BLUE || gameBoard.getBackground() == Color.CYAN ||
                    gameBoard.getBackground() == Color.ORANGE) { // if board has been won or tied
                continue; // skip board
            } // end gameBoard color if(..)
            gameBoard.setBackground(Color.GREEN); // set localBoard color to green
            for (Component c : gameBoard.getComponents()) { // iterate through localBoard components
                if (((JButton) c).getText() == "") { // if button has not been played
                    c.setEnabled(true); // enable button
                } // end if(..)
            } // end for each component(..)
        } // end for each gameBoard(..)
    } // end of unlockAll method

    /**
     * Unlocks localBoards.
     */
    public void unlockBoard() {
        for (JPanel gameBoard : localBoards) { // iterate through localBoards list
            if (gameBoard.getBackground() == Color.BLUE || gameBoard.getBackground() == Color.CYAN ||
                    gameBoard.getBackground() == Color.ORANGE) { // if board is won or tied
                continue; // skip
            }
            if (gameBoard.getBackground() == Color.RED) { // if localBoard color is red
                for (Component c : gameBoard.getComponents()) { // iterate through localBoard components
                    if (((JButton) c).getText() == "") { // if button has not been played
                        c.setEnabled(true); // enable button
                    } // end JButton if(..)
                } // end for each component(..)
                gameBoard.setBackground(Color.GREEN); // set localBoard color to green
            } // end gameBoard color if(..)
        } // end for each gameBoard(..)
    } // end of unlockBoard method

    /**
     * Calculates row position.
     * @param board integer of localBoard position in mainBoard
     * @return integer of row location
     */
    public int calculateRow(int board) {
        if (board == 1 || board == 2 || board == 3) {
            return 0; // return 0 for first row location
        } else
        if (board == 4 || board == 5 || board == 6) {
            return 1; // return 1 for middle row location
        }
        return 2; // return 2 otherwise
    } // end calculateRow method

    /**
     * Calculates column position.
     * @param board integer of localBoard position in mainBoard
     * @return integer of column location
     */
    public int calculateCol(int board) {
        if (board == 1 || board == 4 || board == 7) {
            return 0; // return 0 for first column location
        } else
        if (board == 2 || board == 5 || board == 8) {
            return 1; // return 1 for middle column
        }
        return 2; // return 2 otherwise
    } // end calculateCol method

    /**
     * Calculates average for player
     * @param array ArrayList of integers, moves to get a win for each won board
     * @return integer average for player
     */
    public int getAverage(ArrayList<Integer> array) {
        if (array.size() !=0) {
            int total = 0; // initialize total counter
            for (Integer moves : array) { // iterate through each integer in ArrayList
                total += moves; // increment total by amount
            } // end for each moves(..)
            return total / array.size(); // return average
        }
        return 0;
    } // end of getAverage method

    /**
     * Checks localBoard for a win or tie
     * @param localBoard
     * @return true if localBoard is won or tied, otherwise false
     */
    public boolean checkLocalBoard(Board localBoard) {
        boolean localWin = false; // initialize localWin to false
        char boardResult = localBoard.checkWin(); // call checkWin to get character
        boolean tie = localBoard.checkTie(); // call checkTie to get boolean
        if (boardResult != '-' || tie) { // check if result is a win or if there is a tie
            localWin = true; // set localWin to true
            availableBoards.remove(localBoard.getName()); // remove board identifier from availableBoards list
            int mainRow = calculateRow(Integer.parseInt(localBoard.getName())); // calculate row for mainBoard
            int mainCol = calculateCol(Integer.parseInt(localBoard.getName())); // calculate column for mainBoard
            for (JPanel gameBoard : localBoards) { // iterate through localBoards list
                if (gameBoard.getName().contentEquals(localBoard.getName())) { // if the won or tied board
                    for (Component c : gameBoard.getComponents()) { // iterate through localBoard components
                        c.setEnabled(false); // disable all components
                    } // end for each component(..)
                    if (boardResult == 'X') { // if X win
                        gameBoard.setBackground(Color.BLUE); // set localBoard color to blue
                        ((TitledBorder) gameBoard.getBorder()).setTitleColor(Color.WHITE); // set Title color to white
                        movesPlayerX.add(countX); // add move count to moves list
                        countX = 0; // reset move count to 0
                        // print message to console
                        System.out.println("\nPlayer " + boardResult + " WINS board " + gameBoard.getName() + "!!");
                        mainBoard.setElement(mainRow, mainCol, player1); // set mainBoard element with 'X'
                    } else
                    if (boardResult == 'O') { // if O win
                        gameBoard.setBackground(Color.CYAN); // set localBoard color to cyan
                        movesPlayerO.add(countO); // add move count to moves list
                        countO = 0; // reset move count to 0
                        // print message to console
                        System.out.println("\nPlayer " + boardResult + " WINS board " + gameBoard.getName() + "!!");
                        mainBoard.setElement(mainRow, mainCol, boardResult); // set mainBoard element with 'O'
                    } else
                    if (tie) { // if tie
                        gameBoard.setBackground(Color.ORANGE); // set localBoard color to orange
                        // print message to console
                        System.out.println("\nTie on board " + gameBoard.getName());
                        mainBoard.setElement(mainRow, mainCol, 'T'); // set mainBoard element with 'T'
                    }
                    if (lastPosition == Integer.parseInt(lastBoard)) { // if next play is on the same localBoard
                        unlockBoard(); // call unlockBoard
                    } // end if(..)
                    break; // break from loop
                } // end if(..)
            } // end for each gameBoard(..)
        } // end boardResult if(..)
        return localWin; // return localWin
    } // end of checkLocalBoard method

    /**
     * Checks mainBoard for a win or tie
     */
    public void checkMain() {
        char mainResult = mainBoard.checkWin(); // get checkWin character
        boolean tie = mainBoard.checkTie(); // get checkTie boolean
        if (mainResult != '-' || tie) { // if win or a tie
            undo.setEnabled(false);
            playerX.setBackground(bg); // reset player button colors
            playerX.setForeground(Color.BLACK);
            playerO.setBackground(bg);
            gameEnded = true; // set gameEnded to true
            lockAll(); // lock all localBoards
            if (mainResult == 'X') { // if X win
                System.out.println("\nPlayer X WINS the game!!"); // print message to console
                averageX = getAverage(movesPlayerX); // getAverage for X
                averageO = getAverage(movesPlayerO); // getAverage for O
                winX++; // increment X win count
                countO = 0; // reset move count for O
                //print messaged to console
                System.out.println("\tX's moves to win: " + movesPlayerX);
                System.out.println("\tO's moves to win: " + movesPlayerO);
                System.out.println("\tPlayer X's average is: " + averageX);
                System.out.println("\tPlayer O's average is: " + averageO);
                JOptionPane.showMessageDialog(null, "Player X WINS the game!!"); // display winner popup
                completedGames++; // increment completed games
            } else
            if (mainResult == 'O'){ // if O win
                System.out.println("\nPlayer O WINS the game!!"); // print message to console
                averageX = getAverage(movesPlayerX); // getAverage for X
                averageO = getAverage(movesPlayerO); // getAverage for O
                winO++; // increment O win count
                countX = 0; // reset move count for X
                // print messages to console
                System.out.println("\tX's moves to win: " + movesPlayerX);
                System.out.println("\tO's moves to win: " + movesPlayerO);
                System.out.println("\tPlayer X's average is: " + averageX);
                System.out.println("\tPlayer O's average is: " + averageO);
                JOptionPane.showMessageDialog(null, "Player O WINS the game!!"); // display winner popup
                completedGames++; // increment completed games
            } else
            if (tie) { // if tie
                System.out.println("\nTied game!"); // print message to console
                countX = 0; // reset move counts
                countO = 0;
                averageX = getAverage(movesPlayerX); // getAverage for X
                averageO = getAverage(movesPlayerO); // getAverage for O
                // display messages to console
                System.out.println("\tX's moves to win: " + movesPlayerX);
                System.out.println("\tO's moves to win: " + movesPlayerO);
                System.out.println("\tPlayer X's average is: " + averageX);
                System.out.println("\tPlayer O's average is: " + averageO);
                JOptionPane.showMessageDialog(null, "Tied game!"); // display tie popup
                completedGames++; // increment completedGames
            } // end else is(..)
        } // end if(..)
    } // end of checkMain method

    /**
     * Handles a human player move.
     * @param localBoard board played on
     * @param position position played
     */
    public void humanMove(Board localBoard, int position) {
        undo.setEnabled(true);
        completedTurns++; // increment completedTurns
        int row = calculateRow(position); // calculateRow
        int col = calculateCol(position); // calculateCol
        if (!isPlayer1) { // if player2
            countO++; // increment O move count
            localBoard.setElement(row, col, player2); // set localBoard element to player2 character
            lastBoard = localBoard.getName(); // set lastBoard to localBoard name
            lastPosition = position; // set lastPosition to position played
            isPlayer1 = true; // set isPlayer1 to true
            currentPlayer = player1; // set currentPlayer to player1
            lastPlayer = player2; // set lastPlayer to player2
            playerX.setBackground(Color.BLUE); // update player button colors
            playerX.setForeground(Color.WHITE);
            playerO.setBackground(bg);
            // print move count message to console
            System.out.println("\nPlayer O current move count: " + countO);
        } else { // player1 played
            countX++; // increment X move count
            localBoard.setElement(row, col, player1); // set localBoard element to player1 character
            lastBoard = localBoard.getName(); // set lastBoard to localBoard name
            lastPosition = position; // set lastPosition to position played
            isPlayer1 = false; // set isPlayer1 to false
            currentPlayer = player2; // set currentPlayer to player2
            lastPlayer = player1; // set lastPlayer to player1
            playerX.setForeground(Color.BLACK); // update player button colors
            playerX.setBackground(bg);
            playerO.setBackground(Color.CYAN);
            // print move count message to console
            System.out.println("\nPlayer X current move count: " + countX);
        }
        lastHumanBoard = lastBoard; // set lastHumanBoard played on
        lastHumanPosition = lastPosition; // set lastHumanPosition played
        unlockBoard(); // unlockBoard
        lockBoard(lastPosition); // lockBoard
        boolean localWin = checkLocalBoard(localBoard); // checkLocalBoard
        if (localWin == true) { // if win
            checkMain(); // checkMain
        }
    } // end humanMove method

    /**
     * Clears last move from last local board.
     */
    public void undoMove() {
        System.out.println("\tUndoing Player " + lastPlayer + "'s move on Board " + lastBoard + ", position " + lastPosition);
        char temp = currentPlayer; // temporary variable for currentPlayer
        currentPlayer = lastPlayer; // set currentPlayer to lastPlayer
        lastPlayer = temp; // set lastPlayer to temporary variable
        int row = calculateRow(lastPosition); // calculate row of last position
        int col = calculateCol(lastPosition); // calculate column of last position
        for (JPanel gameBoard : localBoards) { // iterate through localBoards
            if (gameBoard.getName().contentEquals(lastBoard)) { // find lastBoard
                for (Component c : gameBoard.getComponents()) { // iterate through components
                    if (c.getName().contentEquals(Integer.toString(lastPosition))) { // find last button
                        c.setEnabled(true); // enable button
                        ((AbstractButton) c).setText(""); // clear button text
                        break; // break from loop
                    } // end if(..)
                } // end for each component
                break; // break from loop
            } // end if(..)
        } // end for each gameBoard
        for (Board board : boardList) { // iterate through boardList
            if (board.getName().contentEquals(lastBoard)) { // find last board
                board.setElement(row, col, '-'); // reset board position
                if (isPlayer1 == true) { // player1 called reset
                    System.out.println("\nPlayer O's turn\n");
                    isPlayer1 = false; // set isPlayer1 to false
                    playerO.setBackground(Color.CYAN); // set player2 button color
                    playerX.setBackground(bg); // reset player1 button color
                    playerX.setForeground(Color.BLACK);
                } else { // player2 called reset
                    System.out.println("\nPlayer X's turn\n");
                    isPlayer1 = true; // set isPlayer1 to true
                    playerX.setBackground(Color.BLUE); // set player1 button color
                    playerX.setForeground(Color.WHITE);
                    playerO.setBackground(bg); // reset player2 color
                }
            } // end if(..)
        } // end for each board
        if (completedTurns == 1) { // check if first turn of game
            unlockBoard(); // unlock board
            completedTurns--; // decrement completedTurns
            undo.setEnabled(false); // disable undo button
        } else
        if (completedTurns > 1) { // if not the first played turn
            unlockBoard(); // unlock board
            lockBoard(Integer.parseInt(lastBoard)); // lock board for lastPosition
            completedTurns--; // decrement completedTurns
            undo.setEnabled(false); // disable undo button
        } // end else if(..)
    } // end undoMove method

    /**
     * Initiates first move if computer is the first to play.
     * @param playBoard board to be played on
     * @param playPosition position to be played on
     */
    public void firstAIMove(int playBoard, int playPosition) {
        System.out.println("\nPlayer " + currentPlayer + " beginning game on board " + playBoard + ", position " + playPosition);
        completedTurns++; // increment completedTurns
        for (JPanel gameBoard : localBoards) { // iterate through localBoards list
            if (Integer.parseInt(gameBoard.getName()) == playBoard) { // if localBoard
                for (Component c : gameBoard.getComponents()) { // iterate through components
                    if (Integer.parseInt(((JButton) c).getName()) == playPosition) { // if playPosition
                        ((AbstractButton) c).setText(Character.toString(currentPlayer)); // set button text to currentPlayer
                        c.setEnabled(false); // disable button
                        lastBoard = Integer.toString(playBoard); // set lastBoard to playBoard
                        lastPosition = playPosition; // set lastPosition to playPosition
                        break; // break from loop
                    } // end if(..)
                } // end for each component
                break; // break from loop
            } // end if(..)
        } // end for each gameBoard
        for (Board board : boardList) { // iterate through boardList
            if (Integer.parseInt(board.getName()) == playBoard) { // if playBoard
                int row = calculateRow(playPosition); // calculateRow
                int col = calculateCol(playPosition); // calculateCol
                board.setElement(row, col, currentPlayer); // set localBoard element to currentPlayer character
                break; // break from loop
            } // end if(..)
        } // end for each board
        if (!isPlayer1) { // if player2 played
            countO++; // increment O moves count
            currentPlayer = player1; // set currentPlayer to player1
            lastPlayer = player2; // set lastPlayer to player 2
            isPlayer1 = true; // set isPlayer1 to true
            playerX.setBackground(Color.BLUE); // update player button colors
            playerX.setForeground(Color.WHITE);
            playerO.setBackground(bg);
            // print move count message to console
            System.out.println("\nPlayer O current move count: " + countO);
        } else { // player1 played
            countX++; // increment X moves count
            currentPlayer = player2; // set current player to player2
            lastPlayer = player1; // set lastPlayer to player1
            isPlayer1 = false; // set isPlayer1 to false
            playerO.setBackground(Color.CYAN); // update player button colors
            playerX.setBackground(bg);
            playerX.setForeground(Color.BLACK);
            // print move count message to console
            System.out.println("\nPlayer X current move count: " + countX);
        }
        unlockBoard(); // unlockBoard
        lockBoard(lastPosition); // lockBoard
        if (numberOfPlayers == 0 && gameEnded == false) { // if computer v. computer game and game not ended
            playAI(); // call playAI method
        }
    } // end firstAIMove method

    /**
     * Calculates move for computer player.
     */
    public void calculateMoveAI() {
        System.out.println("\nCalculating AI move for Player " + currentPlayer + " on board " + lastPosition);
        int playPosition = 0; // initialize playPosition to 0
        if (!availableBoards.contains(Integer.toString(lastPosition))) { // check if position is not in availableBoards
            System.out.println("\tBoard " + lastPosition + " not available...");
            lastPosition = 1 + randomNumber.nextInt(9); // set lastPosition to random integer
            while (!availableBoards.contains(Integer.toString(lastPosition))) { // check if not in availableBoards
                lastPosition = 1 + randomNumber.nextInt(9); // while not available, get new number for lastPosition
            }
            System.out.println("\t\tNew board chosen: Board " + lastPosition); // print message to console
        }
        for (Board board : boardList) { // iterate through boardList
            if (board.getName().contentEquals(Integer.toString(lastPosition))) { // if board is found
                playPosition = board.getNextMove(); // call getNextMove to check for a block or win
                if (playPosition == 0) { // if no block or win
                    System.out.println("\tNo optimal move found, calculating new move...");
                    playPosition = 1 + randomNumber.nextInt(9); // get random number for playPosition
                    int row = calculateRow(playPosition); // calculateRow
                    int col = calculateCol(playPosition); // calculateCol
                    boolean validMove = board.validateMove(row, col); // validateMove
                    while (validMove == false && gameEnded == false) { // while not a valid move and game hasn't ended
                        System.out.println("\t\tInvalid move found...searching for a new position...");
                        playPosition = 1 + randomNumber.nextInt(9); // update playPosition to new random number
                        row = calculateRow(playPosition); // calculateRow
                        col = calculateCol(playPosition); // calculateCol
                        validMove = board.validateMove(row, col); // validateMove
                    } // end while(..)
                    System.out.println("\tNew valid move position is: " + playPosition);
                } else {
                    System.out.println("\tFound an optimal move at position " + playPosition);
                } // end else
                break; // break from loop
            } // end if(..)
        } // end for each board(..)
        if (!isPlayer1 && p2.contentEquals("computer")) { // if player2 is a computer
            countO++; // increment O move count
        } else
        if (p1.contentEquals("computer")){ // if player1 is a computer
            countX++; // increment X count
        }
        makeMove(lastPosition, playPosition); // call makeMove
    } // end calculateMoveAI method

    /**
     * Makes move for computer player.
     * @param playBoard board to be played on
     * @param playPosition position on board to be played on
     */
    public void makeMove(int playBoard, int playPosition) {
        completedTurns++; // increment completedTurns
        boolean win = false;
        for (JPanel gameBoard : localBoards) { // iterate through localBoards list
            if (gameBoard.getName().contentEquals(Integer.toString(playBoard))) { // if the board to be played on
                for (Component c : gameBoard.getComponents()) { // iterate through components
                    if (c.getName().contentEquals(Integer.toString(playPosition))) { // if component is the playPosition
                        ((AbstractButton) c).setText(Character.toString(currentPlayer)); // set button text to currentPlayer
                        c.setEnabled(false); // disable button
                        lastBoard = Integer.toString(playBoard); // update lastBoard
                        lastPosition = playPosition; // update lastPosition
                        break; // break from loop
                    } // end if(..)
                } // end for each component
                break; // break from loop
            } // end if(..)
        } // end for each gameBoard
        for (Board board : boardList) { // iterate through boardList
            if (Integer.parseInt(board.getName()) == playBoard) { // if board to be played on
                int row = calculateRow(playPosition); // calculateRow
                int col = calculateCol(playPosition); // calculateCol
                board.setElement(row, col, currentPlayer); // set board element with player character
                win = checkLocalBoard(board); // checkLocalBoard
                if (win) { // if win
                    checkMain(); // call checkMain
                }
                break; // break from loop
            } // end if(..)
        } // end for each board
        if (gameEnded == false) { // if game has not ended
            if (!isPlayer1) { // if player2
                if (!win) {
                    // print current move count to console
                    System.out.println("\nPlayer O current move count: " + countO);
                }
                currentPlayer = player1; // set currentPlayer to player1
                lastPlayer = player2; // set lastPlayer to player2
                isPlayer1 = true; // set isPlayer1 to true
                playerX.setBackground(Color.BLUE); // update player button settings
                playerX.setForeground(Color.WHITE);
                playerO.setBackground(bg);
            } else { // player1 played
                if (!win) {
                    // print current move count to console
                    System.out.println("\nPlayer X current move count: " + countX);
                }
                currentPlayer = player2; // set currentPlayer to player2
                lastPlayer = player1; // set lastPlayer to player1
                isPlayer1 = false; // set isPlayer1 to false
                playerO.setBackground(Color.CYAN); // update player button settings
                playerX.setBackground(bg);
                playerX.setForeground(Color.BLACK);
            }
            unlockBoard(); // call unlockBoard
            lockBoard(lastPosition); // call lockBoard
            if (numberOfPlayers == 0 && gameEnded == false) { // if computer v. computer game and game not ended
                playAI(); // call playAI method
            }
        } // end if(..)
    } // end makeMoveAI method

    /**
     * Initializes new game.
     */
    public void initializeGame() {
        System.out.println("Starting new game...\n");
        countX = 0; // set move counts to 0
        countO = 0;
        gameEnded = false; // set gameEnded to false
        gameCount++; // increment gameCount
        totalGames.setText(Integer.toString(gameCount)); // update totalGames text field
        // display request number of players popup
        String playerInput = JOptionPane.showInputDialog("Enter the number of players (0-2).\n     (Default single player)");
        if (playerInput != null) { // if number of players is not null
            try {
                numberOfPlayers = Integer.parseInt(playerInput); // set number of players to input
            }catch (NumberFormatException e) { // except a NumberFormatException
                numberOfPlayers = 1; // default number of players to 1
            }
        }
        // print message to console
        System.out.println("The number of players is: " + numberOfPlayers);
        this.randomNumber = new SecureRandom(); // generate a random number to choose first move player
        int startingPlayer = 1 + randomNumber.nextInt(2);
        if (numberOfPlayers == 0) { // if no players
            System.out.println("\tComputer vs. Computer game");
            this.p1 = "computer"; // set p1 and p2 to computer
            this.p2 = "computer";
        } else
        if (numberOfPlayers == 1) { // if single player
            System.out.println("\tHuman vs. Computer game");
            this.p1 = "human"; // set p1 to human and p2 to computer
            this.p2 = "computer";
        } else {
            System.out.println("\tHuman vs. Human game");
            this.p1 = "human"; // set p1 nd p2 to human
            this.p2 = "human";
        }
        if (startingPlayer == 1) { // if starting player number is 1
            System.out.println("\tPlayer " + player1 + " will take the first move");
            currentPlayer = player1; // set currentPlayer to player1
            lastPlayer = player2; // set lastPlayer to player2
            isPlayer1 = true; // set isPlayer1 to true
            playerX.setBackground(Color.BLUE); // update player button colors
            playerX.setForeground(Color.WHITE);
            playerO.setBackground(bg);
        } else { // starting player is player2
            System.out.println("\tPlayer " + player2 + " will take the first move");
            currentPlayer = player2; // set currentPlayer to player2
            lastPlayer = player1; // set lastPlayer to player1
            isPlayer1 = false; // set isPlayer1 to false
            playerO.setBackground(Color.CYAN); // update player button colors
            playerX.setBackground(bg);
            playerX.setForeground(Color.BLACK);
        }
        playAI(); // call playAI method
    } // end initializeGame method

    /**
     * Controls computer plays.
     */
    public void playAI()  {
        if (completedGames > 0 && numberOfPlayers == 0) {
            this.frame.update(this.frame.getGraphics()); // updates GUI on all subsequent computer v computer games
        }
        if ((p1.contentEquals("computer") && currentPlayer == 'X')) { // player1's move
            if (lastBoard == null) { // if lastBoard is null, first move of the game
                int playBoard = 1 + randomNumber.nextInt(9); // generate random number for playBoard
                int playPosition = 1 + randomNumber.nextInt(9); // generate random number for playPosition
                firstAIMove(playBoard, playPosition); // call firstAIMove
            } else { // subsequent AI move
                calculateMoveAI(); // call calculateMoveAI
            }
        } else
        if ((p2.contentEquals("computer") && currentPlayer == 'O')) { // player2's move
            if (lastBoard == null) { // if lastBoard is null, first move of the game
                int playBoard = 1 + randomNumber.nextInt(9); // generate random number for playBoard
                int playPosition = 1 + randomNumber.nextInt(9); // generate random number for playPosition
                firstAIMove(playBoard, playPosition); // call firstAIMove
            } else { // subsequent AI move
                calculateMoveAI(); // call calculateMoveAI
            }
        } // end else if(..)
    } // end playAI method

} // end of UTCT class