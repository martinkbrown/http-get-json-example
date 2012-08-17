package co.martinbrown.example.httpgetjson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import co.martinbrown.example.httpgetparsing.R;

public class HttpGetJsonExample extends Activity {

    EditText mEditMoviePlot;
    Button mButtonSubmit;
    TextView mTextMoviePlot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mEditMoviePlot = (EditText) findViewById(R.id.editText1);
        mButtonSubmit = (Button) findViewById(R.id.buttonSubmit);
        mTextMoviePlot = (TextView) findViewById(R.id.textPlot);
    }

    public void getMoviePlot(View v) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    executeHttpGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void setPlotFromJson(String raw) throws Exception {

        JSONObject json = new JSONObject(raw);

        final String moviePlot = "" + json.get("Plot");

        mTextMoviePlot.post(new Runnable() {

            @Override
            public void run() {
                mTextMoviePlot.setText(moviePlot);
            }
        });
    }

    public void executeHttpGet() throws Exception {
        BufferedReader in = null;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://www.imdbapi.com/?i=&t=" + mEditMoviePlot.getText().toString());
            //request.setURI(new URI("http://www.imdbapi.com/?i=&r=xml&t=" + mTextMoviePlot.getText().toString()));
            HttpResponse response = client.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();

            switch(statusCode) {

                case 200:

                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    String NL = System.getProperty("line.separator");

                    while ((line = in.readLine()) != null) {
                        sb.append(line + NL);
                    }

                    in.close();

                    setPlotFromJson(sb.toString());

                    break;

            }

        }
        finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}