package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import model.Character;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Servlet showcasing OOP Polymorphism, Dynamic Dispatch, and Interfaces.
 */
@WebServlet(name = "DemoServlet", urlPatterns = {"/api/demo"})
public class DemoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Character pirateCaptain = new Captain("Monkey D. Luffy", 120, 85, 30, "StrawHats");
        Character pirateSwordsman = new Swordsman("Roronoa Zoro", 100, 80, 25, 3);
        Character enemyUser = new DevilFruitUser("Sir Crocodile", 110, 75, 22, "Suna Suna no Mi", "Logia");

        List<Map<String, String>> steps = new ArrayList<>();

        // Capture console output during dynamic dispatch
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);

        try {
            System.setOut(ps);

            // Step 1: Base Polymorphic attack
            baos.reset();
            pirateCaptain.attack(enemyUser);
            steps.add(Map.of(
                "title", "1. Polymorphic Normal Attack: Captain",
                "concept", "Subclass Method Overriding via Parent Reference (Character c = new Captain(...))",
                "output", baos.toString().trim()
            ));

            baos.reset();
            pirateSwordsman.attack(enemyUser);
            steps.add(Map.of(
                "title", "2. Polymorphic Normal Attack: Swordsman",
                "concept", "Dynamic Method Dispatch resolving Swordsman implementation at runtime",
                "output", baos.toString().trim()
            ));

            // Step 2: Special Moves
            baos.reset();
            pirateCaptain.specialMove(enemyUser);
            steps.add(Map.of(
                "title", "3. Special Move: Gomu Gomu no Elephant Gun",
                "concept", "Overridden specialMove(opponent) dynamically scaling damage",
                "output", baos.toString().trim()
            ));

            baos.reset();
            pirateSwordsman.specialMove(enemyUser);
            steps.add(Map.of(
                "title", "4. Special Move: Santoryu Ougi: Sanzen Sekai",
                "concept", "Overridden specialMove(opponent) applying 3-sword technique",
                "output", baos.toString().trim()
            ));

            // Step 3: Interfaces
            baos.reset();
            if (pirateCaptain instanceof HakiUser) {
                ((HakiUser) pirateCaptain).useHaki(enemyUser);
                steps.add(Map.of(
                    "title", "5. Interface Implementation: HakiUser",
                    "concept", "HakiUser contract interface bypassing enemy armor and defense directly",
                    "output", baos.toString().trim()
                ));
            }

            baos.reset();
            if (pirateSwordsman instanceof SpecialAbility) {
                ((SpecialAbility) pirateSwordsman).activateSpecialAbility(enemyUser);
                steps.add(Map.of(
                    "title", "6. Interface Implementation: SpecialAbility",
                    "concept", "SpecialAbility interface triggering Ashura awakening and stat escalation",
                    "output", baos.toString().trim()
                ));
            }

        } finally {
            System.setOut(originalOut);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("title", "OOP Polymorphism & Interface Live Demonstration");
        result.put("demonstrationSteps", steps);

        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, result);
    }
}
