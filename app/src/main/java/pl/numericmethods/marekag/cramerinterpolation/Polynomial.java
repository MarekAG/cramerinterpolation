package pl.numericmethods.marekag.cramerinterpolation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


public class Polynomial extends Activity {

    TextView polyText;
    Button buttonDerivative;
    EditText editTextDerivative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polynomial);

        polyText = (TextView) findViewById(R.id.textViewPoly);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            ArrayList<Double> xList = (ArrayList<Double>) bundle.getSerializable("xList");
            ArrayList<Double> yList = (ArrayList<Double>) bundle.getSerializable("yList");

            final int rank = bundle.getInt("rank");

            double[][] matrix = new double[rank][rank];
            double[] y = new double[rank];
            double[] vector = new double[rank];

            for (int i = 0; i < rank; i++) {
                y[i] = yList.get(i);
                for (int j = 0; j < rank; j++) {
                    matrix[i][j] = Math.pow(xList.get(i), j);
                }
            }
            for (int i = 0; i < rank; i++) {
                vector[i] = i;
            }

            double[] wArr = new double[rank + 1];

            wArr[0] = det(rank, 0, vector, matrix);

            if (wArr[0] != 0) {
                double[] pom = Arrays.copyOf(y, rank);

                for (int i = 0; i < rank; i++) {
                    y = pom;
                    replaceColumn(rank, i, y, matrix);
                    for (int j = 0; j < rank; j++) {
                        vector[j] = j;
                    }

                    wArr[i + 1] = det(rank, 0, vector, matrix);
                }

                final double[] a = new double[rank];

                for (int i = 0; i < rank; i++)
                    a[i] = (wArr[i + 1] / wArr[0]);

                printPoly(a, polyText);
                printPolyGraph(a);

                buttonDerivative = (Button) findViewById(R.id.buttonDerivative);

                buttonDerivative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editTextDerivative = (EditText) findViewById(R.id.editTextDerivative);
                        String textDerivative = editTextDerivative.getText().toString();
                        if (textDerivative != null && !textDerivative.equals("")) {
                            int valueDerivative = Integer.parseInt(textDerivative);
                            if (valueDerivative > 0 && valueDerivative < rank) {
                                Intent derivative = new Intent(getApplicationContext(), Derivative.class);
                                derivative.putExtra("polynomial", a);
                                derivative.putExtra("value", valueDerivative);
                                startActivity(derivative);

                            } else {
                                Toast.makeText(getApplicationContext(), "Pochodna musi być rzędu 1 - " + (a.length-1) + '!', Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Pochodna musi być rzędu 1 - " + (a.length-1) + '!', Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                polyText.setText("WYznacznik główny równy 0");
            }
        }
        else {
            finish();
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
        GraphView graphView = new LineGraphView(this, "Wykres funkcji: ");
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


    private double det(int rank, int row, double[] vector,
                       double[][] matrix) {
        double columns[] = new double[20];

        double sum;
        int i, j, k, m;

        if (rank == 1) {
            return matrix[row][(int) vector[0]];
        } else {
            sum = 0;
            m = 1;
            for (i = 0; i < rank; i++) {
                k = 0;
                for (j = 0; j < rank - 1; j++) {
                    if (k == i) {
                        k++;
                    }
                    columns[j] = vector[k];
                    k++;
                }
                sum += m * matrix[row][(int) vector[i]]
                        * det(rank - 1, row + 1, columns, matrix);
                m = -m;
            }
            return sum;
        }
    }

    private void replaceColumn(int rank, int column, double[] y,
                               double[][] matrix) {
        double[] pom = new double[rank];

        if (column < 1) {
            for (int i = 0; i < rank; i++) {
                pom[i] = matrix[i][column];
                matrix[i][column] = y[i];
                y[i] = pom[i];
            }
        } else if (column == rank) {
            for (int i = 0; i < rank; i++) {
                pom[i] = matrix[i][column - 1];
                matrix[i][column - 1] = y[i];
                y[i] = pom[i];
            }
        } else {
            for (int i = 0; i < rank; i++) {
                pom[i] = matrix[i][column];
                matrix[i][column] = matrix[i][column - 1];
                matrix[i][column - 1] = y[i];
                y[i] = pom[i];
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}
