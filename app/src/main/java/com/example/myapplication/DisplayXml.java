package com.example.myapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class DisplayXml extends Activity {
    TextView tv1;
    ListView lv1;
    ArrayList<String> arrList;

    //-----------------------------
    ArrayList<HashMap<String,String>> arrListDeneme;
    ArrayList<String> arrListDeneme1;
    //-----------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_xml);
        tv1 = (TextView) findViewById(R.id.textView8);
        tv1.setMovementMethod(new ScrollingMovementMethod());
        arrList = new ArrayList<String>();

        //-----------------------------
        arrListDeneme1 = new ArrayList<>();
        arrListDeneme = (ArrayList<HashMap<String,String>>)getIntent().getSerializableExtra("JSON_DATA");
        //-----------------------------

        try {
            //File file = new File(this.getFilesDir().getAbsolutePath() + "/dictionary.xml");
            File file = new File("dictionary.xml");
            Date lastModDate = new Date(file.lastModified());
            InputStream is = getAssets().open("dictionary.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("word");
            int nListLength = nList.getLength();
            //-----------------------------
            int listLength = arrListDeneme.size();
            //-----------------------------

            for(int j=0; j < nList.getLength(); j++) {
                if (nList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) nList.item(j);
                    /*tv1.setText(tv1.getText() + "\n" + getValue("english", element2) + "\n");
                    tv1.setText(tv1.getText() + "" + getValue("german", element2) + "\n");
                    tv1.setText(tv1.getText() + "-----------------------\n");*/

                    //erce arrList.add(getValue("german", element2) + " -- " + getValue("english", element2));
                }
            }

            //-----------------------------
            for(int j=0; j < listLength; j++) {
                arrListDeneme1.add(arrListDeneme.get(j).get("German") + " -- " + arrListDeneme.get(j).get("English") /*+ " -- "
                        + arrListDeneme1.add(arrListDeneme.get(j).get("Turkish"))*/);
            }
            //-----------------------------

            //erce tv1.setText("Number of Words: " + nListLength + "\n");

            //-----------------------------
            tv1.setText("Number of Words: " + listLength + "\n");
            //-----------------------------

            tv1.setText(tv1.getText() + "Date: " + lastModDate);

            String[] arrListArr = arrListDeneme1.toArray(new String[arrListDeneme1.size()]);
            lv1 = (ListView) findViewById(R.id.listview);
            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                    R.layout.support_simple_spinner_dropdown_item, arrListDeneme1);
            lv1.setAdapter(adapter);

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
