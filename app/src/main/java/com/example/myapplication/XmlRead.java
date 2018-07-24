package com.example.myapplication;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Erce on 4/2/2018.
 */
public class XmlRead extends Activity {
    TextView tv1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv1 = (TextView) findViewById(R.id.textView);

        try {
            AssetManager mngr = getAssets();
            InputStream is = mngr.open("dictionary.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("resources");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                /*if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    tv1.setText(tv1.getText() + "\nName : " + getValue("name", element2) + "\n");
                    tv1.setText(tv1.getText() + "Surname : " + getValue("surname", element2) + "\n");
                    tv1.setText(tv1.getText() + "-----------------------");
                }*/
            }

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