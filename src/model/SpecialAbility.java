package model;

/**
 * Interface representing a character capable of using a specialized ability.
 */
public interface SpecialAbility {
    /**
     * Activates the character's unique special ability against the target.
     * @param target the opponent character
     */
    void activateSpecialAbility(Character target);
}
