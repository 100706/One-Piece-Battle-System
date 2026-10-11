package web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.CharacterNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import model.Character;
import service.CharacterService;
import util.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Servlet for Character CRUD operations.
 */
@WebServlet(name = "CharacterServlet", urlPatterns = {"/api/characters", "/api/characters/*"})
public class CharacterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CharacterService charService = (CharacterService) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_SERVICE);
        String search = req.getParameter("search");

        if (search != null && !search.trim().isEmpty()) {
            try {
                Character found = charService.searchCharacter(search.trim());
                JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, toDto(found));
                return;
            } catch (CharacterNotFoundException e) {
                JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            } catch (Exception e) {
                JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
        }

        List<Character> list = charService.getAllCharacters();
        List<Map<String, Object>> dtoList = new ArrayList<>();
        for (Character c : list) {
            dtoList.add(toDto(c));
        }
        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, dtoList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CharacterService charService = (CharacterService) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_SERVICE);
        String csvPath = (String) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_FILE);

        String role = req.getParameter("role");
        String name = req.getParameter("name");
        String healthStr = req.getParameter("health");
        String attackStr = req.getParameter("attackPower");
        String defenseStr = req.getParameter("defense");
        String crewName = req.getParameter("crewName");
        String swordCountStr = req.getParameter("swordCount");
        String tool = req.getParameter("navigationTool");
        String fruitName = req.getParameter("devilFruitName");
        String fruitType = req.getParameter("devilFruitType");

        // If JSON payload was submitted instead of form data
        String body = JsonUtil.readBody(req);
        if (body != null && !body.trim().isEmpty() && (name == null || name.isEmpty())) {
            try {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                if (json.has("role")) role = json.get("role").getAsString();
                if (json.has("name")) name = json.get("name").getAsString();
                if (json.has("health")) healthStr = json.get("health").getAsString();
                if (json.has("attackPower")) attackStr = json.get("attackPower").getAsString();
                if (json.has("defense")) defenseStr = json.get("defense").getAsString();
                if (json.has("crewName")) crewName = json.get("crewName").getAsString();
                if (json.has("swordCount")) swordCountStr = json.get("swordCount").getAsString();
                if (json.has("navigationTool")) tool = json.get("navigationTool").getAsString();
                if (json.has("devilFruitName")) fruitName = json.get("devilFruitName").getAsString();
                if (json.has("devilFruitType")) fruitType = json.get("devilFruitType").getAsString();
            } catch (Exception ignored) {}
        }

        try {
            if (role == null || name == null || healthStr == null || attackStr == null || defenseStr == null) {
                JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required character fields (role, name, health, attackPower, defense).");
                return;
            }

            int health = Integer.parseInt(healthStr.trim());
            int attack = Integer.parseInt(attackStr.trim());
            int defense = Integer.parseInt(defenseStr.trim());

            Character newChar;
            switch (role.trim().toLowerCase()) {
                case "captain":
                    newChar = new Captain(name.trim(), health, attack, defense, (crewName != null && !crewName.trim().isEmpty()) ? crewName.trim() : "Unaffiliated");
                    break;
                case "swordsman":
                    int swords = (swordCountStr != null && !swordCountStr.trim().isEmpty()) ? Integer.parseInt(swordCountStr.trim()) : 1;
                    newChar = new Swordsman(name.trim(), health, attack, defense, swords);
                    break;
                case "navigator":
                    newChar = new Navigator(name.trim(), health, attack, defense, (tool != null && !tool.trim().isEmpty()) ? tool.trim() : "Log Pose & Clima-Tact");
                    break;
                case "devilfruituser":
                    newChar = new DevilFruitUser(name.trim(), health, attack, defense,
                            (fruitName != null && !fruitName.trim().isEmpty()) ? fruitName.trim() : "Unknown Fruit",
                            (fruitType != null && !fruitType.trim().isEmpty()) ? fruitType.trim() : "Paramecia");
                    break;
                default:
                    JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Unknown role: " + role + ". Allowed: Captain, Swordsman, Navigator, DevilFruitUser");
                    return;
            }

            charService.addCharacter(newChar);

            // Persist to CSV
            if (csvPath != null) {
                FileManager.saveCharacters(charService.getAllCharacters(), csvPath);
            }

            JsonUtil.writeSuccess(resp, "Character registered successfully!", toDto(newChar));
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Health, attack, and defense must be valid integers.");
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CharacterService charService = (CharacterService) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_SERVICE);
        String csvPath = (String) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_FILE);
        String name = req.getParameter("name");

        if (name == null || name.trim().isEmpty()) {
            // Check path info /api/characters/{name}
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                name = pathInfo.substring(1);
            }
        }

        if (name == null || name.trim().isEmpty()) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Parameter 'name' is required to delete character.");
            return;
        }

        try {
            charService.removeCharacter(name.trim());
            if (csvPath != null) {
                FileManager.saveCharacters(charService.getAllCharacters(), csvPath);
            }
            JsonUtil.writeSuccess(resp, "Character '" + name + "' removed successfully.", null);
        } catch (CharacterNotFoundException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Map<String, Object> toDto(Character c) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", c.getName());
        map.put("role", c.getRole());
        map.put("health", c.getHealth());
        map.put("maxHealth", c.getMaxHealth());
        map.put("attackPower", c.getAttackPower());
        map.put("defense", c.getDefense());
        map.put("abilities", c.getAbilities());

        if (c instanceof Captain) {
            map.put("crewName", ((Captain) c).getCrewName());
            map.put("specialTrait", "Crew: " + ((Captain) c).getCrewName());
            map.put("hasHaki", true);
        } else if (c instanceof Swordsman) {
            map.put("swordCount", ((Swordsman) c).getSwordCount());
            map.put("specialTrait", ((Swordsman) c).getSwordCount() + " Blades");
            map.put("hasSpecialAbility", true);
        } else if (c instanceof Navigator) {
            map.put("navigationTool", ((Navigator) c).getNavigationTool());
            map.put("specialTrait", "Tool: " + ((Navigator) c).getNavigationTool());
            map.put("hasSpecialAbility", true);
        } else if (c instanceof DevilFruitUser) {
            DevilFruitUser df = (DevilFruitUser) c;
            map.put("devilFruitName", df.getDevilFruitName());
            map.put("devilFruitType", df.getDevilFruitType());
            map.put("specialTrait", df.getDevilFruitName() + " (" + df.getDevilFruitType() + ")");
            map.put("hasSpecialAbility", true);
        }
        return map;
    }
}
