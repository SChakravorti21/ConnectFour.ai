package comschakravorti21.github.connectfourai;

import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    GridLayout board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        board = (GridLayout)findViewById(R.id.board);

//        for(int i = 0; i < 6; i++) {
//            for(int j = 0; j < 7; j++) {
//                ImageView view = new ImageView(this);
//                view.setBackgroundResource(R.mipmap.piece_empty);
//                board.addView(view, i*6 + j);
//                Log.d("This", "On " + (i*6 + j));
//            }
//        }
    }
}
