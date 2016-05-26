package com.johnbogil.voices.activities;


import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.johnbogil.voices.R;

/**
 * Created by chrislinder1 on 2/8/16.
 */
public class FloatingActivity extends Activity {

    public TextView floating_activity_message;
    public Button feedback_button;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_activity);
        feedback_button = (Button) findViewById(R.id.feedback_button);
        floating_activity_message = (TextView) findViewById(R.id.floating_activity_message);
        floating_activity_message.setLines(12);
        floating_activity_message.setText(Html.fromHtml("Hello, my name is" + "<font color=#f79b37>" +  " [your name]<br><br>" + "</font>"
                             + "and I am a constituent." + "<br><br>" + "I would like the representative to" +  "<br><br>" + "<font color=#f79b37>" +
                                    "[support or oppose]" + "<br><br>" + "[an issue that you care about]" + "<br><br>"  + "</font>"
                                        + "and I will be voting in November."));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8),(int) (height * .6));
    }

    public void onClick(View view) {
        try {
                finish();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
