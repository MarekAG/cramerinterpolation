package pl.numericmethods.marekag.cramerinterpolation;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.DecimalFormat;


public class Derivative extends Activity {

    TextView derivativeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_derivative);
        Bundle bundle = getIntent().getExtras();
        double[] polynomial = bundle.getDoubleArray("polynomial");
        int rank = bundle.getInt("value");

        derivativeTextView = (TextView) findViewById(R.id.textViewPoly);
        derivative(polynomial, rank);

        printPoly(polynomial, derivativeTextView);

        printPolyGraph(polynomial);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void derivative(double[] a, int j) {
        for (int k = 0; k < j; k++) {
            for (int i = 0; i < a.length; i++) {
                a[i] = a[i] * (i);
            }
            for (int i = 1; i < a.length; i++) {
                a[i - 1] = a[i];
                if (i >= a.length - j) {
                    a[i] = 0;
                }
            }
        }
    }

    protected static void printPoly(double[] a, TextView polyText) {

        DecimalFormat df = new DecimalFormat("####0.00");

        for (int i = 0; i < a.length; i++) {
            if (a[i] == 0) continue;
            if (i == a.length - 1) {
                polyText.append(Html.fromHtml(df.format(a[i]) + "x<<sup>" + i + "</sup>"));
            } else if (i == 0) {
                polyText.append(Html.fromHtml(df.format(a[i])));
            } else if (i == 1) {
                polyText.append(Html.fromHtml(df.format(a[i]) + "x"));
            } else {
                polyText.append(Html.fromHtml(df.format(a[i]) + "x<<sup>" + i + "</sup>"));
            }
            if (i<a.length-1 && a[i+1] != 0) {
                polyText.append(" + ");
            }
        }
    }

    protected void printPolyGraph(double[] a) {
        int num = 150;
        GraphView.GraphViewData[] data = new GraphView.GraphViewData[num];
        double x = -5;
        for (int i = 0; i < num; i++) {
            data[i] = new GraphView.GraphViewData(x, getPolyValue(a, Math.round(x * 1000.0) / 1000.0));
            x += 0.2;
        }
        GraphView graphView = new LineGraphView(this, "Wykres pochodnej: ");
        graphView.addSeries(new GraphViewSeries(data));
        graphView.setViewPort(-5, 20);
        graphView.setScrollable(true);
        graphView.setScalable(true);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(60);


        LinearLayout layout = (LinearLayout) findViewById(R.id.graphLayout);
        layout.addView(graphView);

    }

    protected double getPolyValue(double[] a, double x) {
        double value = 0;
        for (int i = 0; i < a.length; i++) {
            value += Math.round((a[i] * Math.pow(x, i)) * 1000.0) / 1000.0;
        }
        return value;
    }
}
