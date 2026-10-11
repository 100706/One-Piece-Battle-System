package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BattleService;
import service.CharacterService;
import service.CrewService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check and telemetry servlet.
 */
@WebServlet(name = "StatusServlet", urlPatterns = {"/api/status"})
public class StatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CharacterService charService = (CharacterService) getServletContext().getAttribute(AppContextListener.ATTR_CHAR_SERVICE);
        CrewService crewService = (CrewService) getServletContext().getAttribute(AppContextListener.ATTR_CREW_SERVICE);
        BattleService battleService = (BattleService) getServletContext().getAttribute(AppContextListener.ATTR_BATTLE_SERVICE);

        Map<String, Object> status = new HashMap<>();
        status.put("serverStatus", "ONLINE");
        status.put("serverName", getServletContext().getServerInfo());
        status.put("servletVersion", getServletContext().getMajorVersion() + "." + getServletContext().getMinorVersion());
        status.put("registeredCharacters", charService != null ? charService.getAllCharacters().size() : 0);
        status.put("registeredCrews", crewService != null ? crewService.getAllCrewNames().size() : 0);
        status.put("activeBattleLogs", battleService != null ? battleService.battleHistory().size() : 0);
        status.put("timestamp", System.currentTimeMillis());

        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, status);
    }
}
