package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends Activity {
    ArrayList arrList = new ArrayList();
    TextView tv1, word1, word2, word3, word4;
    private long mLastClickTime = 0;
    private String chosenWordEnglish = "", chosenWordGerman = "";
    private int randomNumberForLanguage;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            if (!isConnected()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date currentDate = new Date();
                    String currentDateStr = sdf.format(currentDate);
                    arrList = new GetJSON().execute("http://ercecanbalcioglu.com/dictionaryDatabaseFill/androidWebService.php").get();
                    SQLiteDatabase db = openOrCreateDatabase("dictionary", MODE_PRIVATE, null);
                    //String SQL_DELETE_DATE =
                    //       "DROP TABLE IF EXISTS " + "date";
                    String SQL_CREATE_DATE =
                            "CREATE TABLE IF NOT EXISTS " + "date" + " (" +
                                    "id" + " INTEGER PRIMARY KEY," +
                                    "Date" + " TEXT)";
                    //db.execSQL(SQL_DELETE_DATE);
                    db.execSQL(SQL_CREATE_DATE);

                    Cursor cursor = db.rawQuery("SELECT * FROM date " +
                            "WHERE id = (SELECT MAX(id) FROM date);", null);
                    cursor.moveToFirst();
                    long diffDays = 0;
                    String dateFromLocalDb;
                    boolean flag = false;
                    if (cursor.getCount() != 0) {
                        dateFromLocalDb = cursor.getString(1);
                        cursor.close();
                        diffDays = getDifferenceDays(sdf.parse(dateFromLocalDb), sdf.parse(currentDateStr));
                        flag = true;
                    }

                    if (!flag || (flag && diffDays > 7)) {
                        int listLength = arrList.size();
                        String SQL_DELETE_ENTRIES =
                                "DROP TABLE IF EXISTS " + "dictionary";
                        String SQL_CREATE_ENTRIES =
                                "CREATE TABLE " + "dictionary" + " (" +
                                        "id" + " INTEGER PRIMARY KEY," +
                                        "German" + " TEXT," +
                                        "English" + " TEXT)";

                        db.execSQL(SQL_DELETE_ENTRIES);
                        db.execSQL(SQL_CREATE_ENTRIES);
                        ContentValues value = new ContentValues();
                        value.put("Date", currentDateStr);
                        db.insert("date", null, value);

                        for (int i = 0; i < listLength; i++) {
                            final HashMap<String, String> map = (HashMap<String, String>) arrList.get(i);
                            ContentValues values = new ContentValues();
                            values.put("German", map.get("German"));
                            values.put("English", map.get("English"));
                            long newRowId = db.insert("dictionary", null, values);
                            //db.execSQL("INSERT INTO dictionary VALUES("+map.get("German")+", "+map.get("English")+");");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //randomWord(findViewById(R.id.button2));
                engine(findViewById(R.id.button2));
            } else if (true) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                alertDialog.setMessage("Network Error. Local database will be used");
                alertDialog.setButton("OK", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = openOrCreateDatabase("dictionary", MODE_PRIVATE, null);
                        Cursor cursor = db.rawQuery("SELECT * FROM dictionary", null);
                        if (cursor.moveToFirst()) {
                            ArrayList arrListTemp = new ArrayList();
                            for (int i = 0; i < cursor.getCount(); i++) {
                                HashMap<String, HashMap<String, String>> node = new HashMap<>();
                                HashMap<String, String> nodeChild = new HashMap<>();
                                nodeChild.put("German", cursor.getString(1));
                                nodeChild.put("English", cursor.getString(2));
                                nodeChild.put("id", cursor.getString(0));
                                nodeChild.put("Turkish", "");
                                arrListTemp.add(nodeChild);
                                cursor.moveToNext();
                            }
                            cursor.close();
                            try {
                                arrList = arrListTemp;
                                System.out.println("aa");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        engine(findViewById(R.id.button2));
                    }
                });
                alertDialog.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * Checks if there is internet connection
     */
    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    /**
     * Called when the user taps the Send button
     */
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

    public void updateLocalDatabase(View view) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date currentDate = new Date();
            String currentDateStr = sdf.format(currentDate);
            arrList = new GetJSON().execute("http://ercecanbalcioglu.com/dictionaryDatabaseFill/androidWebService.php").get();
            SQLiteDatabase db = openOrCreateDatabase("dictionary", MODE_PRIVATE, null);
            String SQL_CREATE_DATE =
                    "CREATE TABLE IF NOT EXISTS " + "date" + " (" +
                            "id" + " INTEGER PRIMARY KEY," +
                            "Date" + " TEXT)";
            db.execSQL(SQL_CREATE_DATE);

            int listLength = arrList.size();
            String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + "dictionary";
            String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + "dictionary" + " (" +
                            "id" + " INTEGER PRIMARY KEY," +
                            "German" + " TEXT," +
                            "English" + " TEXT)";

            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES);
            ContentValues value = new ContentValues();
            value.put("Date", currentDateStr);
            db.insert("date", null, value);

            for (int i = 0; i < listLength; i++) {
                final HashMap<String, String> map = (HashMap<String, String>) arrList.get(i);
                ContentValues values = new ContentValues();
                values.put("German", map.get("German"));
                values.put("English", map.get("English"));
                db.insert("dictionary", null, values);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display XML on new activity
     */
    public void displayXml(View view) {
        Intent intent = new Intent(this, DisplayXml.class);
        intent.putExtra("JSON_DATA", arrList);
        startActivity(intent);
    }

    /**
     * Engine to create buttons, listeners and logic
     */
    public void engine(final View view) {
        view.getRootView().setBackgroundColor(Color.WHITE);
        tv1 = findViewById(R.id.textView1);
        tv1.setText("");
        word1 = findViewById(R.id.textView2);
        word1.setText("");
        word2 = findViewById(R.id.textView3);
        word2.setText("");
        word3 = findViewById(R.id.textView4);
        word3.setText("");
        word4 = findViewById(R.id.textView5);
        word4.setText("");
        final TextView textViewArr[] = {word1, word2, word3, word4};
        tv1.setTextColor(Color.DKGRAY);
        for (int i = 0; i < textViewArr.length; i++) {
            textViewArr[i].setTextColor(Color.DKGRAY);
        }

        try {
            //Initialize first words
            randomWord(findViewById(R.id.button2));

            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Meaning");
                    if (randomNumberForLanguage == 0) {
                        alertDialog.setMessage(chosenWordGerman);
                    } else if (randomNumberForLanguage == 1) {
                        alertDialog.setMessage(chosenWordEnglish);
                    }
                    alertDialog.setButton("OK", new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.show();
                }
            });

            word1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (word1.getText().equals(chosenWordEnglish) || word1.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    } else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                        showTheRightChoice(textViewArr, chosenWordGerman, chosenWordEnglish);
                    }

                    new CountDownTimer(1000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            randomWord(view);
                        }
                    }.start();

                }
            });

            word2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (word2.getText().equals(chosenWordEnglish) || word2.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    } else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                        showTheRightChoice(textViewArr, chosenWordGerman, chosenWordEnglish);
                    }

                    new CountDownTimer(1000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            randomWord(view);
                        }
                    }.start();
                }
            });

            word3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (word3.getText().equals(chosenWordEnglish) || word3.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    } else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                        showTheRightChoice(textViewArr, chosenWordGerman, chosenWordEnglish);
                    }

                    new CountDownTimer(1000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            randomWord(view);
                        }
                    }.start();
                }
            });

            word4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (word4.getText().equals(chosenWordEnglish) || word4.getText().equals(chosenWordGerman)) {
                        view.getRootView().setBackgroundColor(Color.argb(255, 153, 255, 51));
                    } else {
                        view.getRootView().setBackgroundColor(Color.argb(255, 255, 40, 40));
                        showTheRightChoice(textViewArr, chosenWordGerman, chosenWordEnglish);
                    }

                    new CountDownTimer(1000, 1000) {

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

    public void randomWord(final View view) {
        view.getRootView().setBackgroundColor(Color.WHITE);
        tv1 = findViewById(R.id.textView1);
        tv1.setText("");
        word1 = findViewById(R.id.textView2);
        word1.setText("");
        word2 = findViewById(R.id.textView3);
        word2.setText("");
        word3 = findViewById(R.id.textView4);
        word3.setText("");
        word4 = findViewById(R.id.textView5);
        word4.setText("");
        final TextView textViewArr[] = {word1, word2, word3, word4};
        tv1.setTextColor(Color.DKGRAY);
        for (int i = 0; i < textViewArr.length; i++) {
            textViewArr[i].setTextColor(Color.DKGRAY);
        }

        try {
            Random random = new Random();
            //getting word json from service(actual working right now)
            //----------------------------
            int listLength = arrList.size();
            final int randomIndex = random.nextInt(listLength - 0) + 0;
            final HashMap<String, String> actualMap = (HashMap<String, String>) arrList.get(randomIndex);
            //----------------------------

            //setting the actual word with 3 different words
            int[] intArr = {-1, -1, -1, -1};
            for (int j = 0; j < 3; j++) {
                int randomWordNumber = random.nextInt(listLength);
                while (randomIndex == randomWordNumber || intArr[0] == randomWordNumber || intArr[1] == randomWordNumber || intArr[2] == randomWordNumber) {
                    randomWordNumber = random.nextInt(listLength);
                }
                intArr[j] = randomWordNumber;
            }
            intArr[3] = randomIndex;

            //random orders for the words
            int intArrayRandomNumber1 = random.nextInt(4);
            HashMap<String, String> randomMap1 = (HashMap<String, String>) arrList.get(intArr[intArrayRandomNumber1]);
            int intArrayRandomNumber2 = random.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber2) {
                intArrayRandomNumber2 = random.nextInt(4);
            }
            HashMap<String, String> randomMap2 = (HashMap<String, String>) arrList.get(intArr[intArrayRandomNumber2]);
            int intArrayRandomNumber3 = random.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber3 || intArrayRandomNumber2 == intArrayRandomNumber3) {
                intArrayRandomNumber3 = random.nextInt(4);
            }
            HashMap<String, String> randomMap3 = (HashMap<String, String>) arrList.get(intArr[intArrayRandomNumber3]);
            int intArrayRandomNumber4 = random.nextInt(4);
            while (intArrayRandomNumber1 == intArrayRandomNumber4 || intArrayRandomNumber2 == intArrayRandomNumber4 || intArrayRandomNumber3 == intArrayRandomNumber4) {
                intArrayRandomNumber4 = random.nextInt(4);
            }
            HashMap<String, String> randomMap4 = (HashMap<String, String>) arrList.get(intArr[intArrayRandomNumber4]);

            randomNumberForLanguage = random.nextInt(2); // 0 for english 1 for german
            if (randomNumberForLanguage == 0) {
                chosenWordEnglish = actualMap.get("English");
                chosenWordGerman = actualMap.get("German");
                tv1.setText(chosenWordEnglish);
                word1.setText(randomMap1.get("German"));
                word2.setText(randomMap2.get("German"));
                word3.setText(randomMap3.get("German"));
                word4.setText(randomMap4.get("German"));
            } else if (randomNumberForLanguage == 1) {
                chosenWordEnglish = actualMap.get("English");
                chosenWordGerman = actualMap.get("German");
                tv1.setText(chosenWordGerman);
                word1.setText(randomMap1.get("English"));
                word2.setText(randomMap2.get("English"));
                word3.setText(randomMap3.get("English"));
                word4.setText(randomMap4.get("English"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showTheRightChoice(TextView arr[], String chosenWordGerman, String chosenWordEnglish) {
        if (arr[0].getText().equals(chosenWordEnglish) || arr[0].getText().equals(chosenWordGerman)) {
            arr[0].setTextColor(Color.argb(255, 153, 255, 51));
        } else if (arr[1].getText().equals(chosenWordEnglish) || arr[1].getText().equals(chosenWordGerman)) {
            arr[1].setTextColor(Color.argb(255, 153, 255, 51));
        } else if (arr[2].getText().equals(chosenWordEnglish) || arr[2].getText().equals(chosenWordGerman)) {
            arr[2].setTextColor(Color.argb(255, 153, 255, 51));
        } else if (arr[3].getText().equals(chosenWordEnglish) || arr[3].getText().equals(chosenWordGerman)) {
            arr[3].setTextColor(Color.argb(255, 153, 255, 51));
        }
    }
}
