package hangman;
import java.io.File;
import java.util.Set;

public interface IEvilHangmanGame {
	@SuppressWarnings("serial")
	public static class GuessAlreadyMadeException extends Exception {
	}

	public void startGame(File dictionary, int wordLength);

	public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException;
	
}
