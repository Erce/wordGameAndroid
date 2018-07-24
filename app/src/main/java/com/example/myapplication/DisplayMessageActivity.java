package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        /*Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);*/
    }

    public void setSettings(View view) {
        try {
            InputStream is = getAssets().open("dictionary.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nodes = doc.getElementsByTagName("word");
            Text englishText = doc.createTextNode("asd");
            Text germanText = doc.createTextNode("qwe");
            Element word = doc.createElement("word");
            Element german = doc.createElement("german");
            Element english = doc.createElement("english");
            german.appendChild(germanText);
            english.appendChild(englishText);
            word.appendChild(german);
            word.appendChild(english);
            nodes.item(nodes.getLength()-1).getParentNode().appendChild(word);
            
            NodeList nList = doc.getElementsByTagName("dictionary");
        }
        catch (Exception e) {
            e.printStackTrace();
        }




       /* DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setIgnoringComments(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(new File("XmlTest.xml"));
        NodeList nodes = doc.getElementsByTagName("CustomerId");*/




    }
}
