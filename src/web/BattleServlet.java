package web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.CharacterNotFoundException;
import exception.InvalidBattleException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.BattleResult;
import model.Character;
import service.BattleService;
import service.CharacterService;
import util.FileManager;

import java.io.IOException;

/**
 * REST API Servlet for running live turn-based battle simulations.
 */
@WebServlet(name = "BattleServlet", urlPatterns = {"/api/battle"})
public class BattleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CharacterService charService = (CharacterService) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_SERVICE);
        BattleService battleService = (BattleService) getServletContext().getAttribute(AppContextListener.ATTR_BATTLE_SERVICE);
        String historyFile = (String) getServletContext().getAttribute(AppContextListener.ATTR_HISTORY_FILE);

        String fighter1 = req.getParameter("fighter1");
        String fighter2 = req.getParameter("fighter2");

        String body = JsonUtil.readBody(req);
        if (body != null && !body.trim().isEmpty() && (fighter1 == null || fighter1.isEmpty())) {
            try {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                if (json.has("fighter1")) fighter1 = json.get("fighter1").getAsString();
                if (json.has("fighter2")) fighter2 = json.get("fighter2").getAsString();
            } catch (Exception ignored) {}
        }

        if (fighter1 == null || fighter2 == null || fighter1.trim().isEmpty() || fighter2.trim().isEmpty()) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Both 'fighter1' and 'fighter2' parameters are required.");
            return;
        }

        try {
            Character c1 = charService.searchCharacter(fighter1.trim());
            Character c2 = charService.searchCharacter(fighter2.trim());

            // Run detailed simulation
            BattleResult result = battleService.simulateDetailedBattle(c1, c2);

            // Persist battle logs
            if (historyFile != null) {
                try {
                    java.util.List<String> toSave = new java.util.ArrayList<>();
                    toSave.add(result.getSummary());
                    toSave.addAll(result.getLogs());
                    FileManager.saveBattleHistory(toSave, historyFile);
                } catch (IOException e) {
                    System.err.println("Warning: Failed to persist battle logs: " + e.getMessage());
                }
            }

            // Reset health for future simulations
            c1.resetHealth();
            c2.resetHealth();

            JsonUtil.writeSuccess(resp, "Battle simulation completed successfully!", result);

        } catch (CharacterNotFoundException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (InvalidBattleException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Simulation failed: " + e.getMessage());
        }
    }
}
