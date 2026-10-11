package model;

/**
 * Represents a single turn/round event in a simulated combat encounter.
 */
public class BattleRound {
    private int round;
    private String attacker;
    private String defender;
    private String actionType;
    private String description;
    private int damage;
    private boolean critical;
    private boolean haki;
    private int attackerHp;
    private int attackerMaxHp;
    private int defenderHp;
    private int defenderMaxHp;

    public BattleRound() {}

    public BattleRound(int round, String attacker, String defender, String actionType,
                       String description, int damage, boolean critical, boolean haki,
                       int attackerHp, int attackerMaxHp, int defenderHp, int defenderMaxHp) {
        this.round = round;
        this.attacker = attacker;
        this.defender = defender;
        this.actionType = actionType;
        this.description = description;
        this.damage = damage;
        this.critical = critical;
        this.haki = haki;
        this.attackerHp = attackerHp;
        this.attackerMaxHp = attackerMaxHp;
        this.defenderHp = defenderHp;
        this.defenderMaxHp = defenderMaxHp;
    }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public String getAttacker() { return attacker; }
    public void setAttacker(String attacker) { this.attacker = attacker; }

    public String getDefender() { return defender; }
    public void setDefender(String defender) { this.defender = defender; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }

    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }

    public boolean isHaki() { return haki; }
    public void setHaki(boolean haki) { this.haki = haki; }

    public int getAttackerHp() { return attackerHp; }
    public void setAttackerHp(int attackerHp) { this.attackerHp = attackerHp; }

    public int getAttackerMaxHp() { return attackerMaxHp; }
    public void setAttackerMaxHp(int attackerMaxHp) { this.attackerMaxHp = attackerMaxHp; }

    public int getDefenderHp() { return defenderHp; }
    public void setDefenderHp(int defenderHp) { this.defenderHp = defenderHp; }

    public int getDefenderMaxHp() { return defenderMaxHp; }
    public void setDefenderMaxHp(int defenderMaxHp) { this.defenderMaxHp = defenderMaxHp; }
}
