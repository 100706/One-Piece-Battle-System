package service;

import exception.InvalidBattleException;
import model.Captain;
import model.Character;
import model.Swordsman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for BattleService.
 */
public class BattleServiceTest {
    private BattleService battleService;
    private Character luffy;
    private Character zoro;

    @BeforeEach
    public void setUp() {
        battleService = new BattleService();
        luffy = new Captain("Luffy", 100, 20, 5, "StrawHats");
        zoro = new Swordsman("Zoro", 100, 18, 6, 3);
    }

    @Test
    public void testStartBattle_SuccessAndWinnerDecided() throws InvalidBattleException {
        // Run simulation
        Character winner = battleService.startBattle(luffy, zoro);
        
        assertNotNull(winner);
        assertTrue(winner.getName().equals("Luffy") || winner.getName().equals("Zoro"));
        
        // One must be alive and the other must be defeated (0 HP)
        if (winner.getName().equals("Luffy")) {
            assertTrue(luffy.isAlive());
            assertFalse(zoro.isAlive());
            assertEquals(0, zoro.getHealth());
        } else {
            assertTrue(zoro.isAlive());
            assertFalse(luffy.isAlive());
            assertEquals(0, luffy.getHealth());
        }

        // History logs check
        List<String> logs = battleService.battleHistory();
        assertFalse(logs.isEmpty());
    }

    @Test
    public void testStartBattle_SelfBattle_ThrowsException() {
        assertThrows(InvalidBattleException.class, () -> {
            battleService.startBattle(luffy, luffy);
        });
    }

    @Test
    public void testStartBattle_DeadCombatant_ThrowsException() {
        zoro.setHealth(0); // Mark Zoro as defeated
        
        assertThrows(InvalidBattleException.class, () -> {
            battleService.startBattle(luffy, zoro);
        });
    }

    @Test
    public void testStartBattle_NullCombatant_ThrowsException() {
        assertThrows(InvalidBattleException.class, () -> {
            battleService.startBattle(luffy, null);
        });
    }

    @Test
    public void testCalculateWinner() {
        luffy.setHealth(20);
        zoro.setHealth(0);
        
        Character winner = battleService.calculateWinner(luffy, zoro);
        assertEquals("Luffy", winner.getName());
    }
}
