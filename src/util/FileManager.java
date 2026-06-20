package util;

import model.Character;
import model.Captain;
import model.Swordsman;
import model.Navigator;
import model.DevilFruitUser;
import java.io.*;
import java.util.*;

/**
 * Utility class to manage persistent storage of characters and battle logs.
 * Showcases File Handling (BufferedReader, BufferedWriter, FileReader, FileWriter) and Try-with-Resources.
 */
public class FileManager {

    /**
     * Saves a list of characters to a CSV file.
     * @param characters The list of characters to save
     * @param filePath The file path
     * @throws IOException if an I/O error occurs
     */
    public static void saveCharacters(List<Character> characters, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Character c : characters) {
                StringBuilder sb = new StringBuilder();
                sb.append(c.getRole()).append(",")
                  .append(c.getName()).append(",")
                  .append(c.getMaxHealth()).append(",")
                  .append(c.getAttackPower()).append(",")
                  .append(c.getDefense());

                if (c instanceof Captain) {
                    sb.append(",").append(((Captain) c).getCrewName());
                } else if (c instanceof Swordsman) {
                    sb.append(",").append(((Swordsman) c).getSwordCount());
                } else if (c instanceof Navigator) {
                    sb.append(",").append(((Navigator) c).getNavigationTool());
                } else if (c instanceof DevilFruitUser) {
                    sb.append(",").append(((DevilFruitUser) c).getDevilFruitName())
                      .append(",").append(((DevilFruitUser) c).getDevilFruitType());
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Loads a list of characters from a CSV file.
     * Parses the CSV records and instantiates the correct specialized subclasses dynamically.
     * @param filePath The file path
     * @return List of Character objects
     * @throws IOException if an I/O error occurs
     */
    public static List<Character> loadCharacters(String filePath) throws IOException {
        List<Character> characters = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return characters; // Return empty list if file doesn't exist yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String role = parts[0].trim();
                String name = parts[1].trim();
                int health = Integer.parseInt(parts[2].trim());
                int attackPower = Integer.parseInt(parts[3].trim());
                int defense = Integer.parseInt(parts[4].trim());

                Character character = null;
                switch (role) {
                    case "Captain":
                        if (parts.length >= 6) {
                            character = new Captain(name, health, attackPower, defense, parts[5].trim());
                        }
                        break;
                    case "Swordsman":
                        if (parts.length >= 6) {
                            int swords = Integer.parseInt(parts[5].trim());
                            character = new Swordsman(name, health, attackPower, defense, swords);
                        }
                        break;
                    case "Navigator":
                        if (parts.length >= 6) {
                            character = new Navigator(name, health, attackPower, defense, parts[5].trim());
                        }
                        break;
                    case "DevilFruitUser":
                        if (parts.length >= 7) {
                            character = new DevilFruitUser(name, health, attackPower, defense, parts[5].trim(), parts[6].trim());
                        }
                        break;
                    default:
                        System.err.println("Unknown character role: " + role);
                }

                if (character != null) {
                    characters.add(character);
                }
            }
        }
        return characters;
    }

    /**
     * Saves/Appends battle history logs to a text file.
     * @param logs The lines of log text
     * @param filePath The file path
     * @throws IOException if an I/O error occurs
     */
    public static void saveBattleHistory(List<String> logs, String filePath) throws IOException {
        // Use true for append mode in FileWriter to keep a historical archive
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String log : logs) {
                writer.write(log);
                writer.newLine();
            }
            writer.write("========================================================================");
            writer.newLine();
        }
    }

    /**
     * Loads the entire battle history from the text file.
     * @param filePath The file path
     * @return List of history strings
     * @throws IOException if an I/O error occurs
     */
    public static List<String> loadBattleHistory(String filePath) throws IOException {
        List<String> history = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return history;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                history.add(line);
            }
        }
        return history;
    }
}
