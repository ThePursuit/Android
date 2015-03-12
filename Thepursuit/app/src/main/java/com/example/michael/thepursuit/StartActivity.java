package com.example.michael.thepursuit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createGame(View view){

        Intent intent = new Intent(this, CreateGameActivity.class);
        startActivity(intent);

    }

    public void joinGame(View view){

        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);

    }

    public void helloParse(View view){
        HashMap<String, Object> request = new HashMap<>();
        request.put("firstName", "Michael");
        request.put("lastName", "Tran");
        ParseCloud.callFunctionInBackground("hello", request, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    TextView t = (TextView) findViewById(R.id.textView);
                    t.setText(result);
                }
            }
        });
    }

}
