package com.praful.speechform;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static android.view.View.GONE;

public class Home extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {


    Button searchbtn;
    MaterialEditText urltxt;
    Toolbar mToolbar;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    BottomSheetBehavior bottomSheetBehavior;

    String uid;

    DuoDrawerLayout mDuoDrawerLayout;
    DuoMenuView mDuoMenuView;

    private WebView wv1;
    ProgressDialog progressDoalog;

    SpeedDialView speedDialView;

    ArrayList<Input> arrayList = new ArrayList<>();
    ArrayList<Autofill_item> autofillist = new ArrayList<>();


    Input currinput;
    TextToSpeech t1;

    int index = 0;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);

        uid = FirebaseAuth.getInstance().getUid();

        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TextToSpeech", "Language Not Supported");
                    }

                    t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i("TextToSpeech", "On Start");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i("TextToSpeech", "On Done");
                            promptSpeechInput();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.i("TextToSpeech", "On Error");
                        }
                    });

                } else {
                    Log.i("TextToSpeech", "Initialization Failed");
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
        mDuoMenuView.setOnMenuClickListener(this);
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mDuoDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        LinearLayout autofill = (LinearLayout) mDuoMenuView.getHeaderView().findViewById(R.id.autofill);
        LinearLayout signout = (LinearLayout) mDuoMenuView.getHeaderView().findViewById(R.id.signout);
        TextView username = (TextView) mDuoMenuView.getHeaderView().findViewById(R.id.username);
        CircleImageView userimg = (CircleImageView) mDuoMenuView.getHeaderView().findViewById(R.id.userimg);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            Picasso.with(this).load(photoUrl).into(userimg);
            username.setText(name);


        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

        duoDrawerToggle.setDrawerIndicatorEnabled(true);

        autofill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDuoDrawerLayout.closeDrawer();
                new CountDownTimer(200, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        Intent intent = new Intent(Home.this, Autofill.class);
                        startActivity(intent);

                    }
                }.start();
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDuoDrawerLayout.closeDrawer();
                new CountDownTimer(200, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {


                        signOut();
                        finish();
                    }
                }.start();
            }
        });

        progressDoalog = new ProgressDialog(Home.this);
        progressDoalog.setMessage("Please wait....");
        searchbtn = (Button) findViewById(R.id.searchbtn);
        urltxt = (MaterialEditText) findViewById(R.id.urltxt);

        wv1 = (WebView) findViewById(R.id.webView);
        wv1.setWebViewClient(new Home.MyBrowser());

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                progressDoalog.show();
                String url = urltxt.getText().toString();
                index = 0;
                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(wv1, url);
                        progressDoalog.dismiss();
                        Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT).show();

                        new Home.MyTask().execute();

                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(getApplicationContext(), "Error " + description, Toast.LENGTH_SHORT).show();
                    }
                });
                wv1.loadUrl(url);

            }
        });


        final ImageView up = (ImageView) findViewById(R.id.up);
        final ImageView down = (ImageView) findViewById(R.id.down);

        View bottom = findViewById(R.id.bottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom);
        final ImageView comeup = (ImageView) findViewById(R.id.comeup);
        comeup.setVisibility(GONE);
        comeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                comeup.setVisibility(GONE);
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up.setVisibility(GONE);
                down.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down.setVisibility(GONE);
                up.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // Toast.makeText(Maps.this, "collapsed", Toast.LENGTH_SHORT).show();
                        down.setVisibility(GONE);
                        up.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        up.setVisibility(GONE);
                        down.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        comeup.setVisibility(View.VISIBLE);
                        comeup.startAnimation(animation);

                        break;


                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        speedDialView = findViewById(R.id.speedDial);

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.nav_autofill, R.drawable.autofill)
                        .setFabBackgroundColor(Color.WHITE)
                        .setLabel("Autofill")
                        .setLabelColor(Color.BLACK)
                        .setLabelBackgroundColor(Color.WHITE)
                        .setLabelClickable(true)
                        .create()
        );

        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.nav_popup, R.drawable.popup)
                        .setFabBackgroundColor(Color.WHITE)
                        .setLabel("Inputs detected")
                        .setLabelColor(Color.BLACK)
                        .setLabelBackgroundColor(Color.WHITE)
                        .setLabelClickable(true)
                        .create()
        );

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.nav_autofill:
                        showautofillpopup();
                        return false;
                    case R.id.nav_popup:
                        showinputspopup();
                        return false;

                    default:
                        return false;
                }
            }
        });

    }

    private void showautofillpopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                View view = inflater.inflate(R.layout.autofillpopup, null);
                builder.setView(view);
                final TextView inputs = (TextView) view.findViewById(R.id.inputs);
                Button autofill = (Button) view.findViewById(R.id.autofill);
                Button cancel = (Button) view.findViewById(R.id.no);
                final AlertDialog alertDialog = builder.create();


                for (int i = 0; i < autofillist.size(); i++) {
                    Autofill_item input = autofillist.get(i);

                    if ((TextUtils.isEmpty(input.getInput().getName()) == true && TextUtils.isEmpty(input.getInput().getId()) == true && TextUtils.isEmpty(input.getInput().getLabel()) == true)) {

                        inputs.append("\n - " + input.getInput().getLabel() + " : (hint) " + input.getInput().getPlaceholder() + " --- empty or dummy input skip this\n");

                    } else {
                        inputs.append("\n - " + input.getInput().getLabel() + " : (hint) " + input.getInput().getPlaceholder() + " will be filled by the value of \n key : " + input.getKey() + " | value :  " + input.getValue() + "\n");
                    }
                }
                autofill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < autofillist.size(); i++) {
                            final Autofill_item input = autofillist.get(i);

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    if (TextUtils.isEmpty(input.getInput().getLabel()) == false || TextUtils.isEmpty(input.getInput().getId()) == false) {

                                        wv1.loadUrl("javascript: (function(){document.getElementById('" + input.getInput().getId() + "').value ='" + input.getValue() + "';})();");

                                    } else {

                                        wv1.loadUrl("javascript: (function(){document.getElementsByName('" + input.getInput().getName() + "')[0].value ='" + input.getValue() + "';})();");

                                    }

                                }
                            });
                        }

                        alertDialog.dismiss();

                    }

                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }

                });

                alertDialog.setCancelable(true);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.Dialogslide;
                alertDialog.show();

            }
        });
    }

    private void showinputspopup() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                View view = inflater.inflate(R.layout.inputspopup, null);
                builder.setView(view);
                final TextView inputs = (TextView) view.findViewById(R.id.inputs);
                final Button start = (Button) view.findViewById(R.id.start);
                Button cancel = (Button) view.findViewById(R.id.no);
                final AlertDialog alertDialog = builder.create();

                for (int i = 0; i < arrayList.size(); i++) {
                    Input input = arrayList.get(i);
                    if ((TextUtils.isEmpty(input.getName()) == true && TextUtils.isEmpty(input.getId()) == true && TextUtils.isEmpty(input.getLabel()) == true)) {

                        inputs.append("\n - " + input.getLabel() + " : (hint) " + input.getPlaceholder() + " --- empty or dummy input skip this\n");

                    } else {
                        inputs.append("\n - " + input.getLabel() + " : (hint) " + input.getPlaceholder() + '\n');
                    }

                }
                if (index == arrayList.size()) {
                    start.setText("START AGAIN");

                } else if (index != 0 && index < arrayList.size()) {
                    start.setText("RESUME");
                }
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (index == arrayList.size()) {
                            index = 0;
                        }
                        startasking(index);
                        alertDialog.dismiss();

                    }

                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }

                });

                alertDialog.setCancelable(true);
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.Dialogslide;
                alertDialog.show();
            }
        });
    }

    private void startasking(int i) {

        currinput = arrayList.get(i);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            t1.speak("Speak you input for " + currinput.getLabel() + " : (hint) " + currinput.getPlaceholder(), TextToSpeech.QUEUE_FLUSH, null, "uid");

            Toast.makeText(this, "Speak you input for " + currinput.getLabel() + " : (hint) " + currinput.getPlaceholder(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Speak you input for " + currinput.getLabel() + " : (hint) " + currinput.getPlaceholder(), Toast.LENGTH_LONG).show();
            t1.speak("Speak you input for " + currinput.getLabel() + " : (hint) " + currinput.getPlaceholder(), TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {

    }

    private class MyTask extends AsyncTask<Void, Void, String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... params) {
            String title = "";
            Document document;
            try {
                document = (Document) Jsoup.connect(urltxt.getText().toString()).get();
                Elements elements = new Elements();
                arrayList.clear();

                for (final Element e : document.select("input , textarea")) {
                    String label = "";
                    for (final Element e0 : document.select("label")) {

                        if (e0.attr("for").equals(e.attr("id"))) {
                            label = e0.text();
                            break;
                        }
                    }

                    if (e.tagName().equals("input")) {

                        if (e.attr("type").equals("email") || e.attr("type").equals("password")
                                || e.attr("type").equals("text")
                        ) {
                            Input input = new Input(e.attr("id"), e.attr("name"), e.attr("type"), e.attr("placeholder"), label, e.tagName());
                            arrayList.add(input);
                        }
                    }
                    if (e.tagName().equals("textarea")) {

                        Input input = new Input(e.attr("id"), e.attr("name"), e.attr("type"), e.attr("placeholder"), label, e.tagName());
                        arrayList.add(input);
                    }


                    elements.add(e);
                }

                ArrayList<Item> peplist = Paper.book(uid).read("items", new ArrayList<Item>());
                for (int i = 0; i < arrayList.size(); i++) {

                    for (int j = 0; j < peplist.size(); j++) {

                        if (arrayList.get(i).getLabel().toLowerCase().contains(peplist.get(j).getKey().toLowerCase()) || arrayList.get(i).getPlaceholder().toLowerCase().contains(peplist.get(j).getKey().toLowerCase())) {
                            autofillist.add(new Autofill_item(arrayList.get(i), peplist.get(j).getKey(), peplist.get(j).getValue()));
                            break;
                        }
                    }
                }
                showinputspopup();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return title;
        }


        @Override
        protected void onPostExecute(final String result) {
            //if you had a ui element, you could display the title
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String skip = "skip", pass = "pass", next = "next", cancel = "cancel", stop = "stop";

                    if (result.get(0).equals(skip) || result.get(0).equals(pass) || result.get(0).equals(next)) {
                        index++;
                        if (index < arrayList.size()) {
                            startasking(index);
                        }

                    } else if (result.get(0).equals(cancel) || result.get(0).equals(stop)) {

                        currinput = null;
                        index = 0;

                        Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show();


                    } else {
                        if (currinput != null) {
                            final Input input = currinput;

                            final String[] resultstr = {result.get(0)};
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {


                                    if (TextUtils.isEmpty(input.getLabel()) == false || TextUtils.isEmpty(input.getId()) == false) {

                                        if (resultstr[0].contains("@")) {
                                            resultstr[0] = resultstr[0].replaceAll(" ", "");
                                        }
                                        wv1.loadUrl("javascript: (function(){document.getElementById('" + input.getId() + "').value ='" + resultstr[0] + "';})();");

                                    } else {

                                        wv1.loadUrl("javascript: (function(){document.getElementsByName('" + input.getName() + "')[0].value ='" + resultstr[0] + "';})();");

                                    }

                                }
                            });


                        }
                        index++;
                        if (index < arrayList.size()) {
                            startasking(index);
                        }
                    }

                    Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

}
