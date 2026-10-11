package web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Utility for JSON serialization, deserialization, and HTTP response handling.
 */
public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Gson getGson() {
        return gson;
    }

    public static void writeJson(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        PrintWriter out = resp.getWriter();
        out.write(gson.toJson(data));
        out.flush();
    }

    public static void writeSuccess(HttpServletResponse resp, String message, Object payload) throws IOException {
        Map<String, Object> map = Map.of(
            "status", "success",
            "message", message,
            "data", payload != null ? payload : Map.of()
        );
        writeJson(resp, HttpServletResponse.SC_OK, map);
    }

    public static void writeError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        Map<String, Object> map = Map.of(
            "status", "error",
            "message", message
        );
        writeJson(resp, statusCode, map);
    }

    public static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
