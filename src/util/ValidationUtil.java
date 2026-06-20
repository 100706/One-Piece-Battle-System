package util;

import java.util.Set;

/**
 * Utility class to validate inputs such as character names, stats, and uniqueness.
 */
public class ValidationUtil {

    /**
     * Validates that the character name is not null or empty.
     * @param name the name to validate
     */
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be null, empty, or whitespace-only.");
        }
    }

    /**
     * Validates that stats (health, attack, defense) are within valid ranges.
     * @param health must be greater than 0
     * @param attackPower must be greater than 0
     * @param defense must be non-negative
     */
    public static void validateStats(int health, int attackPower, int defense) {
        if (health <= 0) {
            throw new IllegalArgumentException("Health must be greater than 0 (provided: " + health + ").");
        }
        if (attackPower <= 0) {
            throw new IllegalArgumentException("Attack power must be greater than 0 (provided: " + attackPower + ").");
        }
        if (defense < 0) {
            throw new IllegalArgumentException("Defense power cannot be negative (provided: " + defense + ").");
        }
    }

    /**
     * Validates that a character name does not already exist in a set of existing names.
     * @param name the name to check
     * @param existingNames the set of current character names
     */
    public static void validateUniqueName(String name, Set<String> existingNames) {
        validateName(name);
        if (existingNames.contains(name.trim())) {
            throw new IllegalArgumentException("Duplicate character name found: '" + name.trim() + "'. Character names must be unique.");
        }
    }
}
