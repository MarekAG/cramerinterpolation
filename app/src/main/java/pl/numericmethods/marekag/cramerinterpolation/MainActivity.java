package pl.numericmethods.marekag.cramerinterpolation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity {

    EditText editTextX;
    EditText editTextY;
    Button buttonGetPoly;
    Button buttonAdd;
    Button buttonClear;
    LinearLayout container;

    ArrayList<Double> yList = new ArrayList<Double>();
    ArrayList<Double> xList = new ArrayList<Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextX = (EditText)findViewById(R.id.editTextX);
        editTextY = (EditText)findViewById(R.id.editTextY);
        buttonAdd = (Button)findViewById(R.id.btnAdd);
        container = (LinearLayout)findViewById(R.id.container);
        buttonGetPoly = (Button) findViewById(R.id.btnGetPoly);
        buttonClear = (Button) findViewById(R.id.buttonClear);

        editTextX.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    editTextY.requestFocus();
                    return true;
                }
                return false;
            }
        });

        editTextY.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    buttonAdd.performClick();
                    return true;
                }
                return false;
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                String textX = editTextX.getText().toString();
                String textY = editTextY.getText().toString();
                if (textX.trim().matches("") || textX.trim().matches("-") || textY.trim().matches("") || textY.trim().matches("-")) {
                    Toast.makeText(getApplication(), "Współrzędne nie mogą być puste!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double doubleX = Double.parseDouble(textX);
                Double doubleY = Double.parseDouble(textY);
                if(xList.contains(doubleX)) {
                    Toast.makeText(getApplication(), "Węzły muszą być jednokrotne!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    xList.add(doubleX);
                    yList.add(doubleY);
                }

                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("InflateParams") final View addView = layoutInflater.inflate(R.layout.row, null);
                TextView textOut = (TextView)addView.findViewById(R.id.textOut);
                textOut.setText(Html.fromHtml("x<sub>"+xList.lastIndexOf(doubleX)+"</sub>= "+textX+" y<sub>"+xList.lastIndexOf(doubleX)+"</sub>= "+textY));

                editTextX.setText("");
                editTextY.setText("");
                editTextX.requestFocus();
                container.addView(addView);
            }});

        buttonClear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                container.removeAllViews();
                xList.clear();
                yList.clear();
            }});

        buttonGetPoly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rank = xList.size();

                if (rank < 2) {
                     Toast.makeText(getApplication(), "Wprowadź co najmniej dwa punkty!", Toast.LENGTH_SHORT).show();
                    return;
                }           
                Intent polyActivity = new Intent(getApplicationContext(), Polynomial.class);

                polyActivity.putExtra("xList", xList);
                polyActivity.putExtra("yList", yList);
                polyActivity.putExtra("rank", rank);
                startActivity(polyActivity);
            }
        });
    }
}
