package service;

import exception.CharacterNotFoundException;
import model.Captain;
import model.Character;
import model.Swordsman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for CharacterService.
 */
public class CharacterServiceTest {
    private CharacterService characterService;

    @BeforeEach
    public void setUp() {
        characterService = new CharacterService();
    }

    @Test
    public void testAddCharacter_Success() throws CharacterNotFoundException {
        Character luffy = new Captain("Luffy", 100, 80, 20, "StrawHats");
        characterService.addCharacter(luffy);

        Character found = characterService.searchCharacter("Luffy");
        assertNotNull(found);
        assertEquals("Luffy", found.getName());
        assertEquals(100, found.getHealth());
    }

    @Test
    public void testAddCharacter_DuplicateName_ThrowsException() {
        Character luffy1 = new Captain("Luffy", 100, 80, 20, "StrawHats");
        Character luffy2 = new Captain("Luffy", 120, 85, 25, "StrawHats");

        characterService.addCharacter(luffy1);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            characterService.addCharacter(luffy2);
        });
        assertTrue(exception.getMessage().contains("Duplicate character name"));
    }

    @Test
    public void testAddCharacter_InvalidStats_ThrowsException() {
        // Invalid health (<= 0)
        assertThrows(IllegalArgumentException.class, () -> {
            new Captain("Luffy", 0, 80, 20, "StrawHats");
            characterService.addCharacter(new Captain("Luffy", 0, 80, 20, "StrawHats"));
        });

        // Invalid attack power
        assertThrows(IllegalArgumentException.class, () -> {
            Character zoro = new Swordsman("Zoro", 100, -5, 10, 3);
            characterService.addCharacter(zoro);
        });
    }

    @Test
    public void testSearchCharacter_CaseInsensitive() throws CharacterNotFoundException {
        Character zoro = new Swordsman("Zoro", 100, 75, 25, 3);
        characterService.addCharacter(zoro);

        Character found = characterService.searchCharacter("zOrO");
        assertNotNull(found);
        assertEquals("Zoro", found.getName());
    }

    @Test
    public void testSearchCharacter_NotFound_ThrowsException() {
        assertThrows(CharacterNotFoundException.class, () -> {
            characterService.searchCharacter("NonExistent");
        });
    }

    @Test
    public void testUpdateCharacter_Success() throws CharacterNotFoundException {
        Character zoro = new Swordsman("Zoro", 100, 75, 25, 3);
        characterService.addCharacter(zoro);

        zoro.setAttackPower(90);
        zoro.setDefense(30);
        characterService.updateCharacter(zoro);

        Character updated = characterService.searchCharacter("Zoro");
        assertEquals(90, updated.getAttackPower());
        assertEquals(30, updated.getDefense());
    }

    @Test
    public void testUpdateCharacter_NotFound_ThrowsException() {
        Character zoro = new Swordsman("Zoro", 100, 75, 25, 3);
        assertThrows(CharacterNotFoundException.class, () -> {
            characterService.updateCharacter(zoro);
        });
    }

    @Test
    public void testRemoveCharacter_Success() throws CharacterNotFoundException {
        Character zoro = new Swordsman("Zoro", 100, 75, 25, 3);
        characterService.addCharacter(zoro);
        
        assertNotNull(characterService.searchCharacter("Zoro"));
        
        characterService.removeCharacter("Zoro");
        assertThrows(CharacterNotFoundException.class, () -> {
            characterService.searchCharacter("Zoro");
        });
    }
}
