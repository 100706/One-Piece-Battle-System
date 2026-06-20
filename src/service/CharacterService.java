package service;

import exception.CharacterNotFoundException;
import model.Character;
import util.ValidationUtil;
import java.util.*;

/**
 * Service managing the lifecycle, registration, and searches of characters.
 * Showcases the Collections Framework (HashMap, List) and Exception Handling.
 */
public class CharacterService {
    private final Map<String, Character> characters = new HashMap<>();

    /**
     * Registers a new character in the system.
     * Validates input stats and ensures name uniqueness.
     * @param character The character to add
     * @throws IllegalArgumentException if validation checks fail
     */
    public void addCharacter(Character character) {
        if (character == null) {
            throw new IllegalArgumentException("Character cannot be null.");
        }
        ValidationUtil.validateName(character.getName());
        ValidationUtil.validateStats(character.getHealth(), character.getAttackPower(), character.getDefense());
        ValidationUtil.validateUniqueName(character.getName(), characters.keySet());

        characters.put(character.getName(), character);
    }

    /**
     * Searches for a character by name (case-insensitive).
     * @param name The character name to find
     * @return The Character object
     * @throws CharacterNotFoundException if no matching character is found
     */
    public Character searchCharacter(String name) throws CharacterNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be empty.");
        }
        String cleanName = name.trim();
        // Check case-insensitive search
        for (String key : characters.keySet()) {
            if (key.equalsIgnoreCase(cleanName)) {
                return characters.get(key);
            }
        }
        throw new CharacterNotFoundException("Character with name '" + name + "' was not found in the registry.");
    }

    /**
     * Updates the statistics of an existing character.
     * @param character The character details to update
     * @throws CharacterNotFoundException if the character does not exist in the system
     */
    public void updateCharacter(Character character) throws CharacterNotFoundException {
        if (character == null) {
            throw new IllegalArgumentException("Character cannot be null.");
        }
        // First check if the character exists (name-based key)
        if (!characters.containsKey(character.getName())) {
            throw new CharacterNotFoundException("Cannot update. Character '" + character.getName() + "' does not exist.");
        }
        ValidationUtil.validateStats(character.getHealth(), character.getAttackPower(), character.getDefense());
        characters.put(character.getName(), character);
    }

    /**
     * Removes a character from the global registry.
     * @param name The name of the character to delete
     * @throws CharacterNotFoundException if character is not found
     */
    public void removeCharacter(String name) throws CharacterNotFoundException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name to remove cannot be empty.");
        }
        Character removed = characters.remove(name.trim());
        if (removed == null) {
            throw new CharacterNotFoundException("Cannot remove. Character '" + name + "' does not exist.");
        }
    }

    /**
     * Retrieves all characters currently registered.
     * @return List of all characters
     */
    public List<Character> getAllCharacters() {
        return new ArrayList<>(characters.values());
    }

    /**
     * Clears all characters from the registry (useful for testing or reloads).
     */
    public void clearRegistry() {
        characters.clear();
    }
}
