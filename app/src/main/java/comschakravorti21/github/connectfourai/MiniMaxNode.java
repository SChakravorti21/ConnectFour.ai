package comschakravorti21.github.connectfourai;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Athu on 11/4/2017.
 */

public class MiniMaxNode {

    private ArrayList<MiniMaxNode> children;
    private short[] gameState;
    private short staticValue;
    //private boolean isMax;

    //Only used for root
    public MiniMaxNode(){
        children = new ArrayList<>(7);
        gameState = new short[6];
        staticValue = 0;
    }

    public MiniMaxNode(short[] prevGameState, short staticValue){
        this.gameState = Arrays.copyOf(prevGameState, prevGameState.length);
        this.staticValue = staticValue;
        children = null;
    }

    public MiniMaxNode(short[] prevGameState, int r, int c, short currentPlayerNum, short staticValue){
        this.gameState = Arrays.copyOf(prevGameState, prevGameState.length);
        GameBoard.placePiece(r, c, gameState, currentPlayerNum);
        //Log.d("Something", "" + gameState[r]);

        this.staticValue = staticValue;
        this.staticValue = GameBoard.staticEval(gameState, r, c, staticValue, currentPlayerNum);
        //this.staticValue = GameBoard.staticEval(gameState, r, c, (short)0, currentPlayerNum);

        children = null;
    }

    public ArrayList<MiniMaxNode> getChildren() {
        return children;
    }

    public short getStaticValue() {
        return staticValue;
    }

    public void setStaticValue(short staticValue) {
        this.staticValue = staticValue;
    }

    public void addChild(MiniMaxNode node){
        if(children == null)
            children = new ArrayList<>();

        children.add(node);
    }

    public void setChildren(ArrayList<MiniMaxNode> children) {
        this.children = children;
    }

    public short[] getGameState() {
        return gameState;
    }

    public void setGameState(int r, int c, short playerNum) {
        GameBoard.placePiece(r, c, gameState, playerNum);
    }
}
