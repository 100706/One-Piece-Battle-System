package service;

import exception.CharacterNotFoundException;
import model.Captain;
import model.Character;
import model.Swordsman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for CrewService.
 */
public class CrewServiceTest {
    private CrewService crewService;

    @BeforeEach
    public void setUp() {
        crewService = new CrewService();
    }

    @Test
    public void testAddMember_Success() {
        Character luffy = new Captain("Luffy", 100, 80, 20, "StrawHats");
        crewService.addMember("StrawHats", luffy);

        List<Character> members = crewService.displayCrew("StrawHats");
        assertEquals(1, members.size());
        assertEquals("Luffy", members.get(0).getName());
    }

    @Test
    public void testAddMember_DuplicateMember_ThrowsException() {
        Character luffy = new Captain("Luffy", 100, 80, 20, "StrawHats");
        crewService.addMember("StrawHats", luffy);

        assertThrows(IllegalArgumentException.class, () -> {
            crewService.addMember("StrawHats", luffy);
        });
    }

    @Test
    public void testRemoveMember_Success() throws CharacterNotFoundException {
        Character luffy = new Captain("Luffy", 100, 80, 20, "StrawHats");
        Character zoro = new Swordsman("Zoro", 100, 75, 25, 3);
        
        crewService.addMember("StrawHats", luffy);
        crewService.addMember("StrawHats", zoro);

        List<Character> members = crewService.displayCrew("StrawHats");
        assertEquals(2, members.size());

        crewService.removeMember("StrawHats", "Zoro");
        List<Character> updatedMembers = crewService.displayCrew("StrawHats");
        assertEquals(1, updatedMembers.size());
        assertEquals("Luffy", updatedMembers.get(0).getName());
    }

    @Test
    public void testRemoveMember_NotFound_ThrowsException() {
        crewService.createCrew("StrawHats");
        assertThrows(CharacterNotFoundException.class, () -> {
            crewService.removeMember("StrawHats", "NonExistentMember");
        });
    }

    @Test
    public void testDisplayCrew_EmptyOrNonexistent() {
        List<Character> members = crewService.displayCrew("NonExistentCrew");
        assertTrue(members.isEmpty());
    }
}
