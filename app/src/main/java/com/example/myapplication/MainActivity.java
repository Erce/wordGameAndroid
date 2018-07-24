package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends Activity {
    ArrayList arrList = new ArrayList();
    TextView tv1,tv2,tv3,tv4,tv5;
    private long mLastClickTime = 0;
    private String chosenWordEnglish = "", chosenWordGerman = "";
    private int randomNumberForLanguage;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if(isConnected()) {
                try {
                    arrList = new GetJSON().execute("http://ercecanbalcioglu.com/dictionaryDatabaseFill/androidWebService.php").get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                randomWord(findViewById(R.id.button2));
            }else {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setMessage("Network Error");
                alertDialog.setButton("OK", new OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                    }
                });
                alertDialog.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        try {
            InputStream is = getAssets().open("dictionary.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("word");
            int nListLength = nList.getLength();
            Random r = new Random();
            int randomNumber = r.nextInt(nListLength - 0) + 0;
            //for (int i = 0; i < nList.getLength(); i++) {
            Node actualNode = nList.item(randomNumber);

            int[] intArr = new int[4];
            for(int j=0; j < 3; j++) {
                int randomWordNumber = r.nextInt(nListLength);
                while (randomNumber == randomWordNumber) {
                    randomWordNumber = r.nextInt(nListLength);
                }
                intArr[j] = randomWordNumber;
            }
            intArr[3] = randomNumber;

            int intArrayRandomNumber1 = r.nextInt(4);
            Element randomElement1 = (Element)nList.item(intArr[intArrayRandomNumber1]);
            int intArrayRandomNumber2 = r.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber2) {
                intArrayRandomNumber2 = r.nextInt(4);
            }
            Element randomElement2 = (Element)nList.item(intArr[intArrayRandomNumber2]);
            int intArrayRandomNumber3 = r.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber2 || intArrayRandomNumber2 == intArrayRandomNumber3) {
                intArrayRandomNumber3 = r.nextInt(4);
            }
            Element randomElement3 = (Element)nList.item(intArr[intArrayRandomNumber3]);
            int intArrayRandomNumber4 = r.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber4 || intArrayRandomNumber2 == intArrayRandomNumber4 || intArrayRandomNumber3 == intArrayRandomNumber4) {
                intArrayRandomNumber4 = r.nextInt(4);
            }
            Element randomElement4 = (Element)nList.item(intArr[intArrayRandomNumber4]);

            if (actualNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) actualNode;
                tv1.setText(tv1.getText() + "\n" + getValue("english", element2) + "\n");
                tv1.setText(tv1.getText() + "-----------------------\n");
                tv1.setText(tv1.getText() + "" + getValue("german", randomElement1) + "\n");
                tv1.setText(tv1.getText() + "" + getValue("german", randomElement2) + "\n");
                tv1.setText(tv1.getText() + "" + getValue("german", randomElement3) + "\n");
                tv1.setText(tv1.getText() + "" + getValue("german", randomElement4) + "\n");
                tv1.setText(tv1.getText() + "-----------------------");
            }
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /** Checks if there is internet connection */
    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, XmlRead.class);
        /*Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);*/
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        /*EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);*/
        startActivity(intent);
    }

    /** Display XML on new activity */
    public void displayXml(View view) {
        Intent intent = new Intent(this, DisplayXml.class);
        intent.putExtra("JSON_DATA", arrList);
        startActivity(intent);
    }

    public void showMySQLData(View view) {

    }

    public void randomWord(final View view) {
        view.getRootView().setBackgroundColor(Color.WHITE);
        final Timer timer = new Timer();
        tv1 = (TextView) findViewById(R.id.textView1);
        tv1.setText("");
        tv2 = (TextView) findViewById(R.id.textView2);
        tv2.setText("");
        tv3 = (TextView) findViewById(R.id.textView3);
        tv3.setText("");
        tv4 = (TextView) findViewById(R.id.textView4);
        tv4.setText("");
        tv5 = (TextView) findViewById(R.id.textView5);
        tv5.setText("");
        try {
            InputStream is = getAssets().open("dictionary.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("word");
            int nListLength = nList.getLength();
            Random r = new Random();
            final int randomNumber = r.nextInt(nListLength - 0) + 0;
            //for (int i = 0; i < nList.getLength(); i++) {
            final Node actualNode = nList.item(randomNumber);

            //----------------------------
            int listLength = arrList.size();
            final int randomIndex = r.nextInt(listLength - 0) + 0;
            final HashMap<String,String> actualMap = (HashMap<String,String>)arrList.get(randomIndex);

            //----------------------------

            int[] intArr = {-1,-1,-1,-1};
            for(int j=0; j < 3; j++) {
                int randomWordNumber = r.nextInt(nListLength);
                while (randomIndex == randomWordNumber || intArr[0] == randomWordNumber || intArr[1] == randomWordNumber || intArr[2] == randomWordNumber) {
                    randomWordNumber = r.nextInt(nListLength);
                }
                intArr[j] = randomWordNumber;
            }
            intArr[3] = randomIndex;

            int intArrayRandomNumber1 = r.nextInt(4);
            Element randomElement1 = (Element)nList.item(intArr[intArrayRandomNumber1]);
            HashMap<String,String> randomMap1 = (HashMap<String,String>)arrList.get(intArr[intArrayRandomNumber1]);
            int intArrayRandomNumber2 = r.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber2) {
                intArrayRandomNumber2 = r.nextInt(4);
            }
            Element randomElement2 = (Element)nList.item(intArr[intArrayRandomNumber2]);
            HashMap<String,String> randomMap2 = (HashMap<String,String>)arrList.get(intArr[intArrayRandomNumber2]);
            int intArrayRandomNumber3 = r.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber3 || intArrayRandomNumber2 == intArrayRandomNumber3) {
                intArrayRandomNumber3 = r.nextInt(4);
            }
            Element randomElement3 = (Element)nList.item(intArr[intArrayRandomNumber3]);
            HashMap<String,String> randomMap3 = (HashMap<String,String>)arrList.get(intArr[intArrayRandomNumber3]);
            int intArrayRandomNumber4 = r.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber4 || intArrayRandomNumber2 == intArrayRandomNumber4 || intArrayRandomNumber3 == intArrayRandomNumber4) {
                intArrayRandomNumber4 = r.nextInt(4);
            }
            Element randomElement4 = (Element)nList.item(intArr[intArrayRandomNumber4]);
            HashMap<String,String> randomMap4 = (HashMap<String,String>)arrList.get(intArr[intArrayRandomNumber4]);

            randomNumberForLanguage = r.nextInt(2); // 0 for english 1 for german
            if (actualNode.getNodeType() == Node.ELEMENT_NODE && randomNumberForLanguage == 0) {
                /*Element element2 = (Element) actualNode;
                chosenWordEnglish = getValue("english", element2);
                chosenWordGerman = getValue("german", element2);
                tv1.setText(getValue("english", element2) + "\n");
                tv2.setText(getValue("german", randomElement1));
                tv3.setText(getValue("german", randomElement2));
                tv4.setText(getValue("german", randomElement3));
                tv5.setText(getValue("german", randomElement4));*/

                //----------------------------------
                chosenWordEnglish = actualMap.get("English");
                chosenWordGerman = actualMap.get("German");
                tv1.setText(chosenWordEnglish);
                tv2.setText(randomMap1.get("German"));
                tv3.setText(randomMap2.get("German"));
                tv4.setText(randomMap3.get("German"));
                tv5.setText(randomMap4.get("German"));
                //----------------------------------
            }
            else if(actualNode.getNodeType() == Node.ELEMENT_NODE && randomNumberForLanguage == 1) {
                /*Element element2 = (Element) actualNode;
                chosenWordGerman = getValue("german", element2);
                chosenWordEnglish = getValue("english", element2);
                tv1.setText(getValue("german", element2) + "\n");
                tv2.setText(getValue("english", randomElement1));
                tv3.setText(getValue("english", randomElement2));
                tv4.setText(getValue("english", randomElement3));
                tv5.setText(getValue("english", randomElement4));*/

                //----------------------------------
                chosenWordEnglish = actualMap.get("English");
                chosenWordGerman = actualMap.get("German");
                tv1.setText(chosenWordGerman);
                tv2.setText(randomMap1.get("English"));
                tv3.setText(randomMap2.get("English"));
                tv4.setText(randomMap3.get("English"));
                tv5.setText(randomMap4.get("English"));
                //----------------------------------
            }



            //}

            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Meaning");
                    if(randomNumberForLanguage == 0) {
                        alertDialog.setMessage(chosenWordGerman);
                    }
                    else if(randomNumberForLanguage == 1) {
                        alertDialog.setMessage(chosenWordEnglish);
                    }
                    alertDialog.setButton("OK", new OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                        }
                    });
                    //alertDialog.setIcon(R.drawable.icon);
                    alertDialog.show();
                }
            });

            tv2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (tv2.getText().equals(chosenWordEnglish) || tv2.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    } else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                    }

                    new CountDownTimer(1500, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            randomWord(view);
                        }
                    }.start();

                }
            });

            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if(tv3.getText().equals(chosenWordEnglish) || tv3.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    }
                    else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                    }

                    new CountDownTimer(1500, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            randomWord(view);
                        }
                    }.start();
                }
            });

            tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if(tv4.getText().equals(chosenWordEnglish) || tv4.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    }
                    else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                    }

                    new CountDownTimer(1500, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            randomWord(view);
                        }
                    }.start();
                }
            });

            tv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if(tv5.getText().equals(chosenWordEnglish) || tv5.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    }
                    else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                    }

                    new CountDownTimer(1500, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {

                            randomWord(view);
                        }
                    }.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}
