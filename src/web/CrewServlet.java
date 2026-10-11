package web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.CharacterNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Character;
import service.CharacterService;
import service.CrewService;

import java.io.IOException;
import java.util.*;

/**
 * REST API Servlet for Crew Management.
 */
@WebServlet(name = "CrewServlet", urlPatterns = {"/api/crews", "/api/crews/*"})
public class CrewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CrewService crewService = (CrewService) getServletContext().getAttribute(AppContextListener.ATTR_CREW_SERVICE);
        String crewName = req.getParameter("name");

        if (crewName != null && !crewName.trim().isEmpty()) {
            List<Character> members = crewService.displayCrew(crewName.trim());
            Map<String, Object> crewData = new HashMap<>();
            crewData.put("crewName", crewName.trim());
            crewData.put("memberCount", members.size());
            crewData.put("members", members);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, crewData);
            return;
        }

        Set<String> crewNames = crewService.getAllCrewNames();
        List<Map<String, Object>> crewsList = new ArrayList<>();
        for (String cName : crewNames) {
            List<Character> members = crewService.displayCrew(cName);
            Map<String, Object> cMap = new HashMap<>();
            cMap.put("crewName", cName);
            cMap.put("memberCount", members.size());
            cMap.put("members", members);
            crewsList.add(cMap);
        }

        // Sort by crew name
        crewsList.sort(Comparator.comparing(m -> ((String) m.get("crewName"))));
        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, crewsList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CrewService crewService = (CrewService) getServletContext().getAttribute(AppContextListener.ATTR_CREW_SERVICE);
        CharacterService charService = (CharacterService) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_SERVICE);

        String action = req.getParameter("action");
        String crewName = req.getParameter("crewName");
        String characterName = req.getParameter("characterName");

        String body = JsonUtil.readBody(req);
        if (body != null && !body.trim().isEmpty() && (action == null || action.isEmpty())) {
            try {
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                if (json.has("action")) action = json.get("action").getAsString();
                if (json.has("crewName")) crewName = json.get("crewName").getAsString();
                if (json.has("characterName")) characterName = json.get("characterName").getAsString();
            } catch (Exception ignored) {}
        }

        try {
            if ("create".equalsIgnoreCase(action) || "createCrew".equalsIgnoreCase(action)) {
                if (crewName == null || crewName.trim().isEmpty()) {
                    JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Crew name cannot be empty.");
                    return;
                }
                crewService.createCrew(crewName.trim());
                JsonUtil.writeSuccess(resp, "Crew '" + crewName.trim() + "' created successfully.", null);
            } else if ("addMember".equalsIgnoreCase(action) || "add".equalsIgnoreCase(action)) {
                if (crewName == null || characterName == null) {
                    JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Both crewName and characterName are required.");
                    return;
                }
                Character member = charService.searchCharacter(characterName.trim());
                crewService.addMember(crewName.trim(), member);
                JsonUtil.writeSuccess(resp, "Character '" + characterName.trim() + "' added to crew '" + crewName.trim() + "'.", null);
            } else {
                JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Unknown action. Supported: create, addMember");
            }
        } catch (CharacterNotFoundException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CrewService crewService = (CrewService) getServletContext().getAttribute(AppContextListener.ATTR_CREW_SERVICE);
        String crewName = req.getParameter("crewName");
        String characterName = req.getParameter("characterName");

        if (crewName == null || characterName == null) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Both crewName and characterName parameters are required.");
            return;
        }

        try {
            crewService.removeMember(crewName.trim(), characterName.trim());
            JsonUtil.writeSuccess(resp, "Character '" + characterName.trim() + "' removed from crew '" + crewName.trim() + "'.", null);
        } catch (CharacterNotFoundException e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            JsonUtil.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
