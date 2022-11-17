package com.example.bai_qua_trinh_3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    int temp;
    ArrayList<String> arrayTitle,arrayLink,arrayDescription,arrayDate;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ReadRss().execute("https://www.petfoodindustry.com/rss/topic/296-vitamins");
        lv= (ListView) findViewById(R.id.listviewTieude);
        arrayTitle=new ArrayList<>();
        arrayLink= new ArrayList<>();
        arrayDescription= new ArrayList<>();
        arrayDate= new ArrayList<>();
        adapter=new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,arrayTitle);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                temp=i;
                dialog(Gravity.CENTER);
            }
        });
    }
    private class ReadRss extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                InputStreamReader inputStreamReader= new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
                String line="";
                while ((line=bufferedReader.readLine())!=null){
                    content.append(line);
                }
                bufferedReader.close();

            }catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            XMLDOMPParser parser = new XMLDOMPParser();
            Document document= parser.getDocument(s);
            NodeList nodeList = document.getElementsByTagName("item");
            String Title="";
            for(int i=0;i<nodeList.getLength();i++) {
                Element element = (Element) nodeList.item(i);
                Title = parser.getValue(element, "title") ;
                arrayTitle.add(Title);
                arrayLink.add(parser.getValue(element,"link"));
                arrayDescription.add(parser.getValue(element,"media:description"));
                arrayDate.add(parser.getValue(element,"pubDate"));
            }
            adapter.notifyDataSetChanged();


        }
    }
    private void dialog(int gravity)
    {
        final Dialog dialog = new Dialog(MainActivity.this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        Button btnMore= (Button)dialog.findViewById(R.id.btnmore);
        Button btnClose= (Button)dialog.findViewById(R.id.btnclose);
        TextView Title= (TextView)dialog.findViewById(R.id.title) ;
        TextView Description= (TextView)dialog.findViewById(R.id.descripton) ;
        TextView Date= (TextView)dialog.findViewById(R.id.date) ;
        Title.setText(arrayTitle.get(temp));
        Description.setText(arrayDescription.get(temp));
        Date.setText(arrayDate.get(temp));
        //Window window = dialog.getWindow();
        //if(window==null){
          //  return;
        //}
        //window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //WindowManager.LayoutParams windowAttributes = window.getAttributes();
        //window.setAttributes(windowAttributes);
        dialog.setCancelable(false);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(arrayLink.get(temp)));
                startActivity(intent);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
