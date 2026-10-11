package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the complete result of a battle simulation encounter.
 */
public class BattleResult {
    private String combatant1Name;
    private String combatant2Name;
    private int combatant1MaxHp;
    private int combatant2MaxHp;
    private String winnerName;
    private String loserName;
    private int winnerRemainingHp;
    private int totalRounds;
    private String summary;
    private List<BattleRound> rounds = new ArrayList<>();
    private List<String> logs = new ArrayList<>();

    public BattleResult() {}

    public BattleResult(String combatant1Name, String combatant2Name,
                        int combatant1MaxHp, int combatant2MaxHp,
                        String winnerName, String loserName,
                        int winnerRemainingHp, int totalRounds,
                        String summary, List<BattleRound> rounds, List<String> logs) {
        this.combatant1Name = combatant1Name;
        this.combatant2Name = combatant2Name;
        this.combatant1MaxHp = combatant1MaxHp;
        this.combatant2MaxHp = combatant2MaxHp;
        this.winnerName = winnerName;
        this.loserName = loserName;
        this.winnerRemainingHp = winnerRemainingHp;
        this.totalRounds = totalRounds;
        this.summary = summary;
        this.rounds = rounds;
        this.logs = logs;
    }

    public String getCombatant1Name() { return combatant1Name; }
    public void setCombatant1Name(String combatant1Name) { this.combatant1Name = combatant1Name; }

    public String getCombatant2Name() { return combatant2Name; }
    public void setCombatant2Name(String combatant2Name) { this.combatant2Name = combatant2Name; }

    public int getCombatant1MaxHp() { return combatant1MaxHp; }
    public void setCombatant1MaxHp(int combatant1MaxHp) { this.combatant1MaxHp = combatant1MaxHp; }

    public int getCombatant2MaxHp() { return combatant2MaxHp; }
    public void setCombatant2MaxHp(int combatant2MaxHp) { this.combatant2MaxHp = combatant2MaxHp; }

    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }

    public String getLoserName() { return loserName; }
    public void setLoserName(String loserName) { this.loserName = loserName; }

    public int getWinnerRemainingHp() { return winnerRemainingHp; }
    public void setWinnerRemainingHp(int winnerRemainingHp) { this.winnerRemainingHp = winnerRemainingHp; }

    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<BattleRound> getRounds() { return rounds; }
    public void setRounds(List<BattleRound> rounds) { this.rounds = rounds; }

    public List<String> getLogs() { return logs; }
    public void setLogs(List<String> logs) { this.logs = logs; }
}
