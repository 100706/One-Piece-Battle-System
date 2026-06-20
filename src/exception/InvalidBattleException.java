package exception;

/**
 * Custom checked exception thrown when an invalid battle condition is encountered.
 */
public class InvalidBattleException extends Exception {
    public InvalidBattleException(String message) {
        super(message);
    }
}
