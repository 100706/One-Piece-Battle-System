package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BattleService;
import util.FileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * REST API Servlet for archived battle history logs.
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/api/history"})
public class HistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BattleService battleService = (BattleService) getServletContext().getAttribute(AppContextListener.ATTR_BATTLE_SERVICE);
        String historyFile = (String) getServletContext().getAttribute(AppContextListener.ATTR_HISTORY_FILE);

        List<String> fileLogs = new ArrayList<>();
        if (historyFile != null) {
            fileLogs = FileManager.loadBattleHistory(historyFile);
        }

        List<String> sessionLogs = battleService.battleHistory();

        // Extract summary matches from file logs for clean dashboard presentation
        List<String> matchSummaries = new ArrayList<>();
        for (String line : fileLogs) {
            if (line.startsWith("Match: ")) {
                matchSummaries.add(line);
            }
        }
        if (matchSummaries.isEmpty()) {
            for (String line : sessionLogs) {
                if (line.startsWith("Match: ")) {
                    matchSummaries.add(line);
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalMatches", matchSummaries.size());
        data.put("summaries", matchSummaries);
        data.put("rawLogs", fileLogs.isEmpty() ? sessionLogs : fileLogs);

        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, data);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BattleService battleService = (BattleService) getServletContext().getAttribute(AppContextListener.ATTR_BATTLE_SERVICE);
        String historyFile = (String) getServletContext().getAttribute(AppContextListener.ATTR_HISTORY_FILE);

        battleService.clearHistory();
        if (historyFile != null) {
            File f = new File(historyFile);
            if (f.exists()) {
                try (FileWriter fw = new FileWriter(f, false)) {
                    fw.write("");
                }
            }
        }

        JsonUtil.writeSuccess(resp, "Battle history archive cleared successfully.", null);
    }
}
