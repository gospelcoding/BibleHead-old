package org.gospelcoding.biblehead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickAddVerse(View v){
        Intent addVerseActivityIntent = new Intent(this, AddVerseActivity.class);
        startActivity(addVerseActivityIntent);
    }

    public void clickLearn(View v){

    }

    public void clickReview(View v){
        Intent intent = new Intent(this, ReviewActivity.class);
        startActivity(intent);
    }
}
