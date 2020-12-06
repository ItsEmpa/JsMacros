package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Wagyourtail
 * @since 1.2.9
 */
public class ScoreboardObjectiveHelper {
    ScoreboardObjective o;
    
    public ScoreboardObjectiveHelper(ScoreboardObjective o) {
        this.o = o;
    }
    
    /**
     * @return player name to score map
     */
    public Map<String, Integer> getPlayerScores() {
        Map<String, Integer> scores  = new LinkedHashMap<>();
        for (ScoreboardPlayerScore pl : o.getScoreboard().getAllPlayerScores(o)) {
            scores.put(pl.getPlayerName(), pl.getScore());
        }
        return scores;
    }
    
    /**
     * @return name of scoreboard
     * @since 1.2.9
     */
    public String getName() {
        return o.getName();
    }
    
    /**
     * @return name of scoreboard
     * @since 1.2.9
     */
    public TextHelper getDisplayName() {
        return new TextHelper(o.getDisplayName());
    }
    
    public ScoreboardObjective getRaw() {
        return o;
    }
}