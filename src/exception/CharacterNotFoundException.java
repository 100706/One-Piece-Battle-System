package exception;

/**
 * Custom checked exception thrown when a character cannot be found in the system.
 */
public class CharacterNotFoundException extends Exception {
    public CharacterNotFoundException(String message) {
        super(message);
    }
}
