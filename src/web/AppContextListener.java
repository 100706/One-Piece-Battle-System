package web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.*;
import model.Character;
import service.BattleService;
import service.CharacterService;
import service.CrewService;
import util.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Initializes and manages shared service lifecycle within the Tomcat ServletContext.
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    public static final String ATTR_CHAR_SERVICE = "characterService";
    public static final String ATTR_CREW_SERVICE = "crewService";
    public static final String ATTR_BATTLE_SERVICE = "battleService";
    public static final String ATTR_CHAR_FILE = "charactersFilePath";
    public static final String ATTR_HISTORY_FILE = "historyFilePath";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        CharacterService characterService = new CharacterService();
        CrewService crewService = new CrewService();
        BattleService battleService = new BattleService();

        // Resolve data storage paths in a reliable writable location
        String appPath = ctx.getRealPath("/");
        File dataDir = (appPath != null) ? new File(appPath, "WEB-INF/data") : new File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File charFile = new File(dataDir, "characters.csv");
        File historyFile = new File(dataDir, "battle_history.txt");

        // Try reading existing root characters.csv first if available
        File rootCharFile = new File("characters.csv");
        if (rootCharFile.exists() && (!charFile.exists() || charFile.length() == 0)) {
            try {
                List<Character> rootChars = FileManager.loadCharacters(rootCharFile.getAbsolutePath());
                if (!rootChars.isEmpty()) {
                    FileManager.saveCharacters(rootChars, charFile.getAbsolutePath());
                }
            } catch (Exception ignored) {}
        }

        ctx.setAttribute(ATTR_CHAR_SERVICE, characterService);
        ctx.setAttribute(ATTR_CREW_SERVICE, crewService);
        ctx.setAttribute(ATTR_BATTLE_SERVICE, battleService);
        ctx.setAttribute(ATTR_CHAR_FILE, charFile.getAbsolutePath());
        ctx.setAttribute(ATTR_HISTORY_FILE, historyFile.getAbsolutePath());

        // Load data
        try {
            List<Character> loaded = FileManager.loadCharacters(charFile.getAbsolutePath());
            if (loaded.isEmpty()) {
                populateDefaults(characterService, crewService);
                FileManager.saveCharacters(characterService.getAllCharacters(), charFile.getAbsolutePath());
            } else {
                for (Character c : loaded) {
                    characterService.addCharacter(c);
                    // Also auto-assign captain to their crew
                    if (c instanceof Captain) {
                        Captain cap = (Captain) c;
                        crewService.addMember(cap.getCrewName(), cap);
                    }
                }
            }
        } catch (IOException e) {
            populateDefaults(characterService, crewService);
        }

        // Initialize sample crew grouping if crews are empty
        populateSampleCrews(characterService, crewService);

        System.out.println(">>> [Tomcat] One Piece Battle Simulation System deployed successfully!");
    }

    private void populateDefaults(CharacterService charService, CrewService crewService) {
        Character luffy = new Captain("Monkey D. Luffy", 120, 85, 30, "StrawHat Pirates");
        Character zoro = new Swordsman("Roronoa Zoro", 100, 80, 25, 3);
        Character nami = new Navigator("Nami", 80, 50, 15, "Clima-Tact Staff");
        Character law = new DevilFruitUser("Trafalgar Law", 110, 78, 20, "Ope Ope no Mi", "Paramecia");
        Character shanks = new Captain("Red-Haired Shanks", 150, 95, 35, "Red Hair Pirates");
        Character katakuri = new DevilFruitUser("Charlotte Katakuri", 130, 88, 28, "Mochi Mochi no Mi", "Special Paramecia");

        charService.addCharacter(luffy);
        charService.addCharacter(zoro);
        charService.addCharacter(nami);
        charService.addCharacter(law);
        charService.addCharacter(shanks);
        charService.addCharacter(katakuri);
    }

    private void populateSampleCrews(CharacterService charService, CrewService crewService) {
        try {
            if (crewService.getAllCrewNames().isEmpty()) {
                crewService.createCrew("StrawHat Pirates");
                crewService.createCrew("Red Hair Pirates");
                crewService.createCrew("Heart Pirates");
                crewService.createCrew("Big Mom Pirates");

                for (Character c : charService.getAllCharacters()) {
                    if (c.getName().contains("Luffy") || c.getName().contains("Zoro") || c.getName().contains("Nami")) {
                        try { crewService.addMember("StrawHat Pirates", c); } catch (Exception ignored) {}
                    } else if (c.getName().contains("Shanks")) {
                        try { crewService.addMember("Red Hair Pirates", c); } catch (Exception ignored) {}
                    } else if (c.getName().contains("Law")) {
                        try { crewService.addMember("Heart Pirates", c); } catch (Exception ignored) {}
                    } else if (c.getName().contains("Katakuri")) {
                        try { crewService.addMember("Big Mom Pirates", c); } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> [Tomcat] One Piece Battle Simulation System shutting down.");
    }
}
