/*Object containing information about the setup.
 * Contains information about number of players,
 * rules, win conditions, and card conditions.
 */
package gmc;

public class Board {
    private long id;
    private GameMap map;
    private Rule[] rules;
    private Player[] players;
    private int cardTurninNumber;
    private int armyPool;
}