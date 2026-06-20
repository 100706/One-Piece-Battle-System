package model;

/**
 * Interface representing a character capable of using Haki.
 */
public interface HakiUser {
    /**
     * Executes Haki ability against the specified target.
     * @param target the opponent character
     */
    void useHaki(Character target);
}
