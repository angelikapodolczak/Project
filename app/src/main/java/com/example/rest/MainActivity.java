package com.example.rest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    String FILE_NAME = "webServiceData.xml";

    ArrayList<HashMap<String,String>> eventList;
    Button btnCurrentEvent;
    Button btnDayEvent;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayDate = (TextView)findViewById(R.id.date);

        eventList = new ArrayList<>();

        btnCurrentEvent = findViewById(R.id.getCurrentDayEvent);
        btnDayEvent     = findViewById(R.id.getDayEvent);

        btnCurrentEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FILE_NAME = "webServiceData.xml";
                getEventList();
            }
        });

        btnDayEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FILE_NAME = "webServiceData1.xml";

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                getEventList();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                String date = year + "-" + (month+1) + "-" + day;
                mDisplayDate.setText(date);
            }
        };
    }

    public void getEventList() {
        InputStream is = null;
        try {
            is = getAssets().open(FILE_NAME);

        }catch (IOException e) {
            e.printStackTrace();
        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = documentBuilder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element element = doc.getDocumentElement();
        element.normalize();

        NodeList nList = doc.getElementsByTagName("event");

        for (int i = 0; i < nList.getLength(); i++){
            Node node = nList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element2 = (Element) node;
                String id = element2.getElementsByTagName("event_id").item(0).getTextContent();
                String time = element2.getElementsByTagName("event_created").item(0).getTextContent();
                String name = element2.getElementsByTagName("evtml_name").item(0).getTextContent();
                String description = element2.getElementsByTagName("evtml_desc").item(0).getTextContent();
                String place = element2.getElementsByTagName("street").item(0).getTextContent();
                addingValuesToHashMap(id, time, name, description, place);
            }
        }
        ListView lv = findViewById(R.id.idLvJson);
        ListAdapter adapter = new SimpleAdapter(MainActivity.this, eventList,
                R.layout.list_item, new String[] {"Id", "Time", "Name", "Description", "Place"},
                new int[] {R.id.id, R.id.time, R.id.name, R.id.description, R.id.place});
        lv.setAdapter(adapter);
    }

    private void addingValuesToHashMap(String id, String time, String name, String description, String place) {
        HashMap<String, String> event = new HashMap<>();
        event.put("Id", id);
        event.put("Time",time);
        event.put("Name",name);
        event.put("Description",description);
        event.put("Place",place);

        eventList.add(event);
    }





}
