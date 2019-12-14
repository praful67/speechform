package com.praful.speechform;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class Autofill extends AppCompatActivity {

    Toolbar mToolbar;
    ImageView addorder;


    ListView listdata;
    AlertDialog alertDialog;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autofill);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uid = FirebaseAuth.getInstance().getUid();
        Paper.init(this);

        listdata = (ListView) findViewById(R.id.listofitems);
        addorder = (ImageView) findViewById(R.id.additem);
        addorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showaddpopup();
            }
        });


        viewdata();

    }

    private void showaddpopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.additem_popup, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.Dialogslide;
        final MaterialEditText key, value;
        key = (MaterialEditText) view.findViewById(R.id.key);
        value = (MaterialEditText) view.findViewById(R.id.value);
        Button add = (Button) view.findViewById(R.id.add);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        alertDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = new Item(key.getText().toString(), value.getText().toString());
                boolean t = true;
                for (int i = 0; i < Paper.book(uid).read("items", new ArrayList<Item>()).size(); i++) {
                    if (Paper.book(uid).read("items", new ArrayList<Item>()).get(i).getKey().equals(key.getText().toString())) {

                        t = false;
                        ArrayList<Item> arrayList = Paper.book(uid).read("items", new ArrayList<Item>());
                        arrayList.set(i, item);
                        Paper.book(uid).write("items", arrayList);
                    }

                }
                if (t) {
                    ArrayList<Item> arrayList = Paper.book(uid).read("items", new ArrayList<Item>());
                    arrayList.add(item);
                    Paper.book(uid).write("items", arrayList);
                }
                alertDialog.dismiss();
                Intent intent = new Intent(Autofill.this, Autofill.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void viewdata() {

        ListAdapter adapter = new ListAdapter(Autofill.this, Paper.book(uid).read("items", new ArrayList<Item>()));

        TextView t = (TextView) findViewById(R.id.t);

        listdata.setEmptyView(t);

        listdata.setAdapter(adapter);


    }
}
