package comschakravorti21.github.connectfourai;

import java.util.ArrayList;

/**
 * Created by Athu on 11/4/2017.
 */

public class MiniMaxNode {

    private ArrayList<MiniMaxNode> children;
    private byte[][] gamestate;
    private int staticValue;
    private boolean isMax;

    public MiniMaxNode(){
        children = new ArrayList<>(7);
        gamestate = new byte[6][7];
    }

    public MiniMaxNode(byte[][] prevGameState){
        this.gamestate = prevGameState;
        children = new ArrayList<>(7);
    }

    public MiniMaxNode(byte[][] prevGameState, int r, int c, byte currentPlayerNum){
        this.gamestate = prevGameState;
        gamestate[r][c] = currentPlayerNum;
        children = new ArrayList<>(7);

    }

    public void addChild(MiniMaxNode node){
        children.add(node);
    }

    public byte[][] getGamestate() {
        return gamestate;
    }

    public void setGameState(int r, int c, byte playerNum) {
        gamestate[r][c] = playerNum;
    }
}
