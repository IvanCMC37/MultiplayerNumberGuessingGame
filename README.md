# MultiplayerNumberGuessingGame
Develop a simple game played via the network.

## The game:
It is a simple guessing game. The server generates a secret code - an X digit number (where X is a number from 3 to 8 determined by the client at the start of the game). The client’s task is to guess the combination. Each combo has only unique digits (no repeats). The client task is to guess the exact combination with the fewest number of guesses (up to a maximum of 10 guesses).
For each incorrect guess the client gets a clue in the form of two numbers:
Correct Positions: number of digits in guess that are in the correct position
Incorrect Positions: number of digits in guess that occur in the code but are in an incorrect position
If the client correctly guesses the code, the server announces the number of guesses made.
If the client fails to guess the code after 10 attempts, the server announces the code.
## Multiplayer version:
The server maintains a lobby queue. Clients have to register with the server (using their first name) to be added to the lobby queue before they can play. Clients can register at any time.
The game is played in rounds. At the start of each round, the server takes the first three clients from the lobby (or all clients if there are less than three clients in the lobby), and starts the round.
First the server announces the number of participants. Then the player in the group that was first in the lobby queue gets to choose the number of digits in the code (X).
The server then generates a random code with X unique digits. The game then enters the guessing phase. Each player can guess at any time (with their number of guesses tracked by the server).
Once all players have either:
Correctly guessed the code,
Reached their maximum guesses – 10,
or
Chosen to forfeit by pressing f (giving them a guess count of 11)
The server announces to all clients the number of guesses for each client (ranked from lowest to highest). Players can then choose to play again (p), or quit (q). The players that chose to play again are added back into the end of the lobby queue, and the process repeats with a new round.
