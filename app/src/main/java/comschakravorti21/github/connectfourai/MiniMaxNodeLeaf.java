package comschakravorti21.github.connectfourai;

/**
 * Created by Athu on 11/4/2017.
 */

public class MiniMaxNodeLeaf {

    private int[][][] gameStates;

    private static final MiniMaxNodeLeaf ourInstance = new MiniMaxNodeLeaf();

    public static MiniMaxNodeLeaf getInstance() {
        return ourInstance;
    }

    private MiniMaxNodeLeaf() {
    }
}
