package comschakravorti21.github.connectfourai;

import java.util.ArrayList;

/**
 * Created by Athu on 11/4/2017.
 */

public class MiniMaxNode {

    private ArrayList<MiniMaxNode> children;
    private short[] gamestate;
    private short staticValue;
    private boolean isMax;

    public MiniMaxNode(){
        children = new ArrayList<>(7);
        gamestate = new short[6];
    }

    public MiniMaxNode(short[] prevGameState, short staticValue){
        this.gamestate = prevGameState;
        this.staticValue = staticValue;
        //children = new ArrayList<>(7);
    }

    public MiniMaxNode(short[] prevGameState, int r, int c, short currentPlayerNum, short staticValue){
        this.gamestate = prevGameState;
        GameBoard.placePiece(r, c, gamestate, currentPlayerNum);

        this.staticValue = staticValue;
        this.staticValue = GameBoard.staticEval(gamestate, r, c, currentPlayerNum, staticValue);

        //children = new ArrayList<>(7);
    }

    //Using this method to guarantee that only inner nodes have children.
    //This way, we can easily tell when a node is a leaf node ––
    //if node.getChildren() == null
    public void instantiateChildrenList() {
        children = new ArrayList<>(); //Not specifying size bc number of children can vary
    }

    public ArrayList<MiniMaxNode> getChildren() {
        return children;
    }

    public short getStaticValue() {
        return staticValue;
    }

    public void addChild(MiniMaxNode node){
        children.add(node);
    }

    public short[] getGamestate() {
        return gamestate;
    }

    public void setGameState(int r, int c, short playerNum) {
        GameBoard.placePiece(r, c, gamestate, playerNum);
    }
}
