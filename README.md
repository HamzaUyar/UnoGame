# UNO Game Implementation

A Java implementation of the classic UNO card game using the Mediator design pattern. This project demonstrates Object-Oriented Programming (OOP) principles and design patterns in a real-world application.

## Project Overview

This implementation of UNO simulates a complete game with multiple players, enforcing standard UNO rules. The game is entirely console-based, featuring colorful text output and detailed game state logging.

### Key Features

- Full UNO card game implementation with all standard cards and rules
- Utilizes the Mediator design pattern for loosely coupled component interaction
- Console-based UI with colorful text formatting
- Detailed gameplay logging to console and CSV files
- Automated player strategies
- Round-based gameplay with score tracking
- Customizable player count (2-4 players)

## Architecture

### Mediator Design Pattern

The project is structured around the Mediator design pattern to facilitate communication between components without them referring to each other directly. This promotes loose coupling and modular design.

Key components in the pattern:

- **IGameMediator (Interface)**: Defines the contract for mediating game component interactions
- **GameMediator (Concrete Implementation)**: Coordinates all game elements and rules
- **IGameComponent (Interface)**: Implemented by all game components requiring mediation
- **GameComponentType (Enum)**: Categorizes different types of components

### Component Structure

The game consists of several key components:

1. **Cards**
   - Base Card class
   - NumberCard for regular numbered cards
   - ActionCard and its subclasses (Skip, Reverse, Draw Two, Wild, Wild Draw Four)

2. **Players**
   - Manages player state (hand, score)
   - Implements card selection strategy

3. **Game Elements**
   - Deck: Contains and manages the full set of UNO cards
   - DrawPile: The pile from which players draw cards
   - DiscardPile: The pile where played cards are placed
   - ScoreTracker: Tracks and calculates player scores

4. **UI**
   - GameUI: Handles all console output formatting and messaging
   - ConsoleColors: Utilities for colorful console output

## Game Flow

1. The game begins by randomly determining the number of players (2-4)
2. Players draw cards to determine the first dealer
3. Each player is dealt 7 cards
4. Starting with the player after the dealer, turns proceed clockwise (by default)
5. On each turn:
   - Player must match the top card by color, number, or card type
   - If no playable card exists, player draws a card
   - Action cards trigger special effects when played
6. First player to discard all their cards wins the round
7. Winner receives points based on the cards remaining in opponents' hands
8. Game continues until a player reaches 500 points

## Logging and Score Tracking

### Console Logging

The game provides detailed console output with colorful formatting to make the game state clear:
- Round information
- Player turns and actions
- Card plays and effects
- Score updates
- Win conditions

The terminal output can become quite lengthy during gameplay, especially for longer games with multiple rounds. To make it easier to review gameplay history, all console output is also logged to the `logs` directory.

### File Logging

All game events displayed in the terminal are simultaneously recorded to log files in the `logs` directory. This dual logging system provides several benefits:
- Permanent record of all game actions and events
- Ability to review past games without scrolling through terminal history
- More readable format for analyzing lengthy game sessions

Due to the extensive nature of the console output, it's often more convenient to follow the game flow by examining these log files instead of the terminal window.

### CSV Logging

The `ScoreTracker` component records game scores to a CSV file in a simple, readable format:

- **File Location**: `csv/scores.csv`
- **Format**: 
  - Header row with column names: `Round,Player 1,Player 2,Player 3,Player 4`
  - One row per round showing scores for each player
  - Final row indicating the game winner

This provides a clear record of the game progression and final outcome.

The CSV functionality:
- Automatically creates the CSV file and directory if they don't exist
- Writes a header row for new files
- Records scores after each round
- Adds a final "Winner" row at the end of the game
- Handles IO exceptions gracefully

Example CSV output:
```
Round,Player 1,Player 2,Player 3,Player 4
Round 1,0,100,0,0
Round 2,200,0,0,0
Round 3,300,0,0,0
Winner,Player 1,,,
```

## Running the Game

To run the game:

1. Ensure you have Java 8 or higher installed
2. Compile the Java files:
   ```
   javac -d bin src/main/java/GameApp.java
   ```
3. Run the game:
   ```
   java -cp bin main.java.GameApp
   ```

The game will automatically start, create players, and run through complete rounds until a player reaches 500 points.

## Class Structure

The project follows clear OOP principles with the following package structure:

- `main.java`: Root package with the main GameApp class
- `main.java.cards`: Card classes and implementations
- `main.java.cards.actioncards`: Special action card implementations
- `main.java.game`: Core game mechanics and mediator pattern
- `main.java.players`: Player classes and strategies
- `main.java.ui`: User interface components
- `main.java.utils`: Utility classes including logging and score tracking

## Future Enhancements

Possible future improvements to the project:
- Graphical user interface
- Network multiplayer support
- Additional card variants and house rules
- AI improvements for computer players
- Persistent player statistics

