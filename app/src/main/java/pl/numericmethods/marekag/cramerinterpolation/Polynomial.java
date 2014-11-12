package pl.numericmethods.marekag.cramerinterpolation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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

        polyText = (TextView) findViewById(R.id.textViewPolyDerivative);
        Bundle bundle = getIntent().getExtras();
        buttonDerivative = (Button) findViewById(R.id.buttonDerivative);

        new CountDet(this).execute(bundle);
    }

    private class CountDet extends AsyncTask<Bundle, Integer, double[]> {

        Context context;
        ProgressDialog dialog;

        public CountDet(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setMessage("Trwa wyznaczanie wielomianu. Proszę czekać!");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
            super.onProgressUpdate(progress);

        }

        @Override
        protected double[] doInBackground(Bundle... bundles) {
            Bundle bundle = bundles[0];
            if (bundle != null) {
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
                            publishProgress((int) (((i * rank + j) / (float) (rank * rank)) * 100));
                            vector[j] = j;
                        }

                        wArr[i + 1] = det(rank, 0, vector, matrix);
                    }


                }

                final double[] a = new double[rank];

                for (int i = 0; i < rank; i++) {
                    a[i] = (wArr[i + 1] / wArr[0]);
                    }

                return a;

            }
            return null;
        }

        @Override
        protected void onPostExecute(final double[] a) {
            dialog.dismiss();
            printPoly(a, polyText);
            printPolyGraph(a);

            int nonZeroValues = 0;

            for (double d : a) {
                if (Math.abs(d) >= 0.01) {
                    nonZeroValues++;
                }
            }

            final int nonZeroCount = nonZeroValues;

            buttonDerivative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editTextDerivative = (EditText) findViewById(R.id.editTextDerivative);
                    String textDerivative = editTextDerivative.getText().toString();
                    if (textDerivative != null && !textDerivative.equals("")) {
                        int valueDerivative = Integer.parseInt(textDerivative);
                        if (valueDerivative >= 0 && valueDerivative <= nonZeroCount) {
                            Intent derivative = new Intent(getApplicationContext(), Derivative.class);
                            derivative.putExtra("polynomial", a);
                            derivative.putExtra("value", valueDerivative);
                            startActivity(derivative);

                        } else {
                            Toast.makeText(getApplicationContext(), "Pochodna musi być rzędu od 0 do " + nonZeroCount + '!', Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Pochodna musi być rzędu od 0 do " + nonZeroCount + '!', Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
    }

    protected static void printPoly(double[] a, TextView polyText) {

        DecimalFormat df = new DecimalFormat("####0.00");

        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i]) < 0.01) continue;
            if (a[i] < 0) {
                polyText.append("(");
            }

            if (i == 0) {
                polyText.append(Html.fromHtml(df.format(a[i])));
            } else if (i == 1) {
                polyText.append(Html.fromHtml(df.format(a[i]) + "x"));
            } else {
                polyText.append(Html.fromHtml(df.format(a[i]) + "x<<sup>" + i + "</sup>"));
            }
            if(a[i] < 0)
                polyText.append(")");
            if (i<a.length-1 && Math.abs(a[i+1]) > 0.01) {
                polyText.append(" + ");
            }
        }
        if (polyText.getText().toString().trim().isEmpty()) {
            polyText.setText("0");
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

}
