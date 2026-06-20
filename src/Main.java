import exception.CharacterNotFoundException;
import exception.InvalidBattleException;
import model.*;
import model.Character;
import service.BattleService;
import service.CharacterService;
import service.CrewService;
import util.FileManager;
import util.ValidationUtil;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class and CLI front-end for the Battle Simulation System.
 * Showcases Service Layer Integration, Polymorphism, Custom Exception Handling,
 * File Handling, Input Validation, and Collections Framework.
 */
public class Main {
    private static final String CHARACTERS_FILE = "characters.csv";
    private static final String BATTLE_HISTORY_FILE = "battle_history.txt";

    private final CharacterService characterService = new CharacterService();
    private final CrewService crewService = new CrewService();
    private final BattleService battleService = new BattleService();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {
        System.out.println("=========================================================");
        System.out.println("        BATTLE SIMULATION SYSTEM (ONE PIECE EDITION)     ");
        System.out.println("=========================================================");
        
        // 1. Initializing and loading default characters if CSV is empty/nonexistent
        loadInitialData();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Choose an option (1-9): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayAllCharacters();
                    break;
                case "2":
                    addNewCharacter();
                    break;
                case "3":
                    searchCharacter();
                    break;
                case "4":
                    manageCrews();
                    break;
                case "5":
                    runBattleSimulation();
                    break;
                case "6":
                    showBattleHistory();
                    break;
                case "7":
                    saveData();
                    break;
                case "8":
                    demonstratePolymorphism();
                    break;
                case "9":
                    System.out.println("\nThank you for using the Battle Simulation System! Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("\n[Error] Invalid selection. Please enter a number between 1 and 9.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1. List All Registered Characters");
        System.out.println("2. Add New Character (with validation)");
        System.out.println("3. Search Character by Name");
        System.out.println("4. Manage Pirate Crews");
        System.out.println("5. Run Battle Simulation (Luffy vs Zoro, etc.)");
        System.out.println("6. View Archived Battle History Logs");
        System.out.println("7. Save Current Character List to File");
        System.out.println("8. Demonstrate OOP Polymorphism & Haki (Demo)");
        System.out.println("9. Exit");
        System.out.println("---------------------------------------------");
    }

    private void loadInitialData() {
        try {
            System.out.println("Attempting to load character database from " + CHARACTERS_FILE + "...");
            List<Character> loaded = FileManager.loadCharacters(CHARACTERS_FILE);
            if (loaded.isEmpty()) {
                System.out.println("No file database found. Populating with initial default character records.");
                populateDefaults();
                FileManager.saveCharacters(characterService.getAllCharacters(), CHARACTERS_FILE);
            } else {
                for (Character c : loaded) {
                    characterService.addCharacter(c);
                }
                System.out.println("Successfully loaded " + loaded.size() + " characters from CSV.");
            }
        } catch (IOException e) {
            System.err.println("Failed to read characters file. Initializing empty registry. Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Data corruption detected when reading character database: " + e.getMessage());
        }
    }

    private void populateDefaults() {
        // Create initial characters
        Character luffy = new Captain("Monkey D. Luffy", 120, 85, 30, "StrawHat Pirates");
        Character zoro = new Swordsman("Roronoa Zoro", 100, 80, 25, 3);
        Character nami = new Navigator("Nami", 80, 50, 15, "Clima-Tact Staff");
        Character law = new DevilFruitUser("Trafalgar Law", 110, 78, 20, "Ope Ope no Mi", "Paramecia");
        Character shanks = new Captain("Red-Haired Shanks", 150, 95, 35, "Red Hair Pirates");

        characterService.addCharacter(luffy);
        characterService.addCharacter(zoro);
        characterService.addCharacter(nami);
        characterService.addCharacter(law);
        characterService.addCharacter(shanks);
    }

    private void displayAllCharacters() {
        List<Character> list = characterService.getAllCharacters();
        System.out.println("\n=== REGISTERED CHARACTERS (Total: " + list.size() + ") ===");
        for (Character c : list) {
            System.out.println(c);
        }
    }

    private void addNewCharacter() {
        System.out.println("\n--- Add New Character ---");
        try {
            System.out.print("Enter Role (Captain, Swordsman, Navigator, DevilFruitUser): ");
            String role = scanner.nextLine().trim();
            System.out.print("Enter Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Health: ");
            int health = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Attack Power: ");
            int attack = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Defense: ");
            int defense = Integer.parseInt(scanner.nextLine().trim());

            Character character = null;
            if (role.equalsIgnoreCase("Captain")) {
                System.out.print("Enter Crew Name: ");
                String crew = scanner.nextLine().trim();
                character = new Captain(name, health, attack, defense, crew);
            } else if (role.equalsIgnoreCase("Swordsman")) {
                System.out.print("Enter Sword Count: ");
                int swords = Integer.parseInt(scanner.nextLine().trim());
                character = new Swordsman(name, health, attack, defense, swords);
            } else if (role.equalsIgnoreCase("Navigator")) {
                System.out.print("Enter Weather Tool (e.g. Clima-Tact): ");
                String tool = scanner.nextLine().trim();
                character = new Navigator(name, health, attack, defense, tool);
            } else if (role.equalsIgnoreCase("DevilFruitUser")) {
                System.out.print("Enter Devil Fruit Name: ");
                String dfName = scanner.nextLine().trim();
                System.out.print("Enter Devil Fruit Type (Paramecia, Zoan, Logia): ");
                String dfType = scanner.nextLine().trim();
                character = new DevilFruitUser(name, health, attack, defense, dfName, dfType);
            } else {
                throw new IllegalArgumentException("Invalid role provided. Choose from: Captain, Swordsman, Navigator, DevilFruitUser.");
            }

            characterService.addCharacter(character);
            System.out.println("\n[Success] Added new character: " + name);
        } catch (NumberFormatException e) {
            System.out.println("\n[Error] Invalid integer input for stats.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[Validation Failure] " + e.getMessage());
        }
    }

    private void searchCharacter() {
        System.out.println("\n--- Search Character ---");
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();
        try {
            Character found = characterService.searchCharacter(name);
            System.out.println("\n[Match Found] " + found);
        } catch (CharacterNotFoundException e) {
            System.out.println("\n[Error] " + e.getMessage());
        }
    }

    private void manageCrews() {
        System.out.println("\n--- Crew Management ---");
        System.out.println("1. Create New Crew");
        System.out.println("2. Add Character to Crew");
        System.out.println("3. Remove Character from Crew");
        System.out.println("4. Display Crew Members");
        System.out.print("Select choice: ");
        String subChoice = scanner.nextLine().trim();

        try {
            switch (subChoice) {
                case "1":
                    System.out.print("Enter Crew Name: ");
                    String crewName = scanner.nextLine().trim();
                    crewService.createCrew(crewName);
                    System.out.println("Crew '" + crewName + "' initialized.");
                    break;
                case "2":
                    System.out.print("Enter Crew Name: ");
                    String crew = scanner.nextLine().trim();
                    System.out.print("Enter Character Name to add: ");
                    String charName = scanner.nextLine().trim();
                    Character character = characterService.searchCharacter(charName);
                    crewService.addMember(crew, character);
                    System.out.println(character.getName() + " added to " + crew);
                    break;
                case "3":
                    System.out.print("Enter Crew Name: ");
                    String cr = scanner.nextLine().trim();
                    System.out.print("Enter Character Name to remove: ");
                    String ch = scanner.nextLine().trim();
                    crewService.removeMember(cr, ch);
                    System.out.println(ch + " removed from " + cr);
                    break;
                case "4":
                    System.out.print("Enter Crew Name: ");
                    String displayCr = scanner.nextLine().trim();
                    crewService.displayCrew(displayCr);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (CharacterNotFoundException e) {
            System.out.println("\n[Error] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\n[Validation Failure] " + e.getMessage());
        }
    }

    private void runBattleSimulation() {
        System.out.println("\n--- Battle Arena ---");
        System.out.print("Enter Attacker Name: ");
        String name1 = scanner.nextLine().trim();
        System.out.print("Enter Defender Name: ");
        String name2 = scanner.nextLine().trim();

        try {
            Character c1 = characterService.searchCharacter(name1);
            Character c2 = characterService.searchCharacter(name2);

            // Execute the battle
            Character winner = battleService.startBattle(c1, c2);

            // Append this specific battle's simulation history back to persistent file storage
            // Find the recent logs corresponding to the battle
            List<String> history = battleService.battleHistory();
            // We'll write the complete archive to file
            FileManager.saveBattleHistory(history, BATTLE_HISTORY_FILE);
            System.out.println("Battle log successfully archived to " + BATTLE_HISTORY_FILE);

            // Reset health values of combatants for subsequent menu activities
            c1.resetHealth();
            c2.resetHealth();

        } catch (CharacterNotFoundException e) {
            System.out.println("\n[Error] Character not registered: " + e.getMessage());
        } catch (InvalidBattleException e) {
            System.out.println("\n[Invalid Matchup] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n[Storage Error] Failed to write battle logs to " + BATTLE_HISTORY_FILE);
        }
    }

    private void showBattleHistory() {
        try {
            System.out.println("\nReading all archived battle logs from " + BATTLE_HISTORY_FILE + "...");
            List<String> archivedLogs = FileManager.loadBattleHistory(BATTLE_HISTORY_FILE);
            if (archivedLogs.isEmpty()) {
                System.out.println("No battle history found in file.");
            } else {
                System.out.println("\n=== PERSISTENT BATTLE HISTORY FILE ===");
                for (String line : archivedLogs) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.println("\n[Error] Failed to read battle history file: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            FileManager.saveCharacters(characterService.getAllCharacters(), CHARACTERS_FILE);
            System.out.println("\n[Success] Characters saved to " + CHARACTERS_FILE);
        } catch (IOException e) {
            System.out.println("\n[Storage Error] Failed to save characters: " + e.getMessage());
        }
    }

    /**
     * Demonstrates Polymorphism, abstract class parent reference, and interface methods.
     */
    private void demonstratePolymorphism() {
        System.out.println("\n=== OOP Polymorphism & Interface Demonstration ===");
        
        // Parent references holding children subclasses (Polymorphism)
        Character pirateCaptain = new Captain("Monkey D. Luffy", 120, 85, 30, "StrawHats");
        Character pirateSwordsman = new Swordsman("Roronoa Zoro", 100, 80, 25, 3);
        Character enemyUser = new DevilFruitUser("Sir Crocodile", 110, 75, 22, "Suna Suna no Mi", "Logia");

        System.out.println("Executing attacks polymorphically (Character c.attack(opponent)):");
        System.out.print("1. ");
        pirateCaptain.attack(enemyUser);
        System.out.print("2. ");
        pirateSwordsman.attack(enemyUser);

        System.out.println("\nExecuting special moves polymorphically (Character c.specialMove(opponent)):");
        System.out.print("1. ");
        pirateCaptain.specialMove(enemyUser);
        System.out.print("2. ");
        pirateSwordsman.specialMove(enemyUser);

        System.out.println("\nExecuting Interface-based behaviors (HakiUser / SpecialAbility):");
        if (pirateCaptain instanceof HakiUser) {
            HakiUser hakiUser = (HakiUser) pirateCaptain;
            System.out.print("Luffy Haki Action: ");
            hakiUser.useHaki(enemyUser);
        }

        if (pirateSwordsman instanceof SpecialAbility) {
            SpecialAbility abilityUser = (SpecialAbility) pirateSwordsman;
            System.out.print("Zoro Ability Action: ");
            abilityUser.activateSpecialAbility(enemyUser);
        }

        System.out.println("====================================================");
    }
}
