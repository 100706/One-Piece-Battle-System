package service;

import exception.CharacterNotFoundException;
import model.Character;
import util.ValidationUtil;
import java.util.*;

/**
 * Service managing crew creation and member assignments.
 * Showcases HashMap and ArrayList usage for group relationships.
 */
public class CrewService {
    private final Map<String, ArrayList<Character>> crews = new HashMap<>();

    /**
     * Creates a new pirate crew by name.
     * @param crewName Name of the crew
     */
    public void createCrew(String crewName) {
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new IllegalArgumentException("Crew name cannot be empty.");
        }
        crews.putIfAbsent(crewName.trim(), new ArrayList<>());
    }

    /**
     * Adds a character to an existing crew. Automatically creates the crew if it doesn't exist.
     * Prevents duplicate characters in the same crew.
     * @param crewName Crew name
     * @param character Character to add
     */
    public void addMember(String crewName, Character character) {
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new IllegalArgumentException("Crew name cannot be empty.");
        }
        if (character == null) {
            throw new IllegalArgumentException("Character cannot be null.");
        }
        
        String cleanCrew = crewName.trim();
        createCrew(cleanCrew);
        
        ArrayList<Character> members = crews.get(cleanCrew);
        // Prevent duplicate addition in the same crew
        for (Character c : members) {
            if (c.getName().equalsIgnoreCase(character.getName())) {
                throw new IllegalArgumentException(character.getName() + " is already a member of the crew '" + cleanCrew + "'.");
            }
        }
        members.add(character);
    }

    /**
     * Removes a character from a crew by name.
     * @param crewName Crew name
     * @param characterName Character name to remove
     * @throws CharacterNotFoundException if the character is not part of this crew
     */
    public void removeMember(String crewName, String characterName) throws CharacterNotFoundException {
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new IllegalArgumentException("Crew name cannot be empty.");
        }
        if (characterName == null || characterName.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be empty.");
        }

        String cleanCrew = crewName.trim();
        if (!crews.containsKey(cleanCrew)) {
            throw new IllegalArgumentException("Crew '" + cleanCrew + "' does not exist.");
        }

        ArrayList<Character> members = crews.get(cleanCrew);
        Character found = null;
        for (Character c : members) {
            if (c.getName().equalsIgnoreCase(characterName.trim())) {
                found = c;
                break;
            }
        }

        if (found == null) {
            throw new CharacterNotFoundException("Character '" + characterName + "' was not found in crew '" + cleanCrew + "'.");
        }
        members.remove(found);
    }

    /**
     * Displays and returns members of a crew.
     * @param crewName Crew name
     * @return List of crew members
     */
    public List<Character> displayCrew(String crewName) {
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new IllegalArgumentException("Crew name cannot be empty.");
        }
        String cleanCrew = crewName.trim();
        if (!crews.containsKey(cleanCrew)) {
            System.out.println("Crew '" + cleanCrew + "' does not exist.");
            return Collections.emptyList();
        }

        ArrayList<Character> members = crews.get(cleanCrew);
        System.out.println("\n=== Crew Members of: " + cleanCrew + " (Total: " + members.size() + ") ===");
        if (members.isEmpty()) {
            System.out.println(" No members found.");
        } else {
            for (Character c : members) {
                System.out.println(" - " + c);
            }
        }
        return new ArrayList<>(members);
    }

    /**
     * Retrieves all crew names.
     * @return Set of crew names
     */
    public Set<String> getAllCrewNames() {
        return new HashSet<>(crews.keySet());
    }

    /**
     * Clears all crews (for testing/reset purposes).
     */
    public void clearCrews() {
        crews.clear();
    }
}
