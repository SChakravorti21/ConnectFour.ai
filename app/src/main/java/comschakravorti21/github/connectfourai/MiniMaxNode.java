package comschakravorti21.github.connectfourai;

import java.util.ArrayList;

/**
 * Created by Athu on 11/4/2017.
 */

public class MiniMaxNode {

    private ArrayList<MiniMaxNode> children;
    private short[] gamestate;
    private int staticValue;
    private boolean isMax;

    public MiniMaxNode(){
        children = new ArrayList<>(7);
        gamestate = new short[6];
    }

    public MiniMaxNode(short[] prevGameState){
        this.gamestate = prevGameState;
        children = new ArrayList<>(7);
    }

    public MiniMaxNode(short[] prevGameState, int r, int c, byte currentPlayerNum){
        this.gamestate = prevGameState;
        GameBoard.placePiece(r, c, gamestate, currentPlayerNum);

        children = new ArrayList<>(7);
    }

    public void addChild(MiniMaxNode node){
        children.add(node);
    }

    public short[] getGamestate() {
        return gamestate;
    }

    public void setGameState(int r, int c, byte playerNum) {
        GameBoard.placePiece(r, c, gamestate, playerNum);
    }
}
