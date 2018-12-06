package fr.enssat.ac.webviewproject;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final String PROGRESS_WEB_VIEW="Progress web view";

    private VideoView myVideoView;
    private WebView webview;

    private int initialPosition = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    private Handler mHandler;
    private Thread mThread;

    private String currentWebViewTitle = "Intro";
    private DataManager dataManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String wikiUrl = "https://fr.wikipedia.org/wiki/Big_Buck_Bunny";
        String videoUrl = "https://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4";

        // Get the layout from activity_main.xml
        setContentView(fr.enssat.ac.webviewproject.R.layout.activity_main);

        //DataManager
        dataManager = new DataManager();
        dataManager.add(0,"Intro","");
        dataManager.add(28,"Title","#Synopsis");
        dataManager.add(60+15,"Butterflies","#Réalisation");
        dataManager.add(2*60+40,"Assault","#Fiche_technique");
        dataManager.add(4*60+50,"Payback","#Distribution");
        dataManager.add(8*60+15,"Cast","#Annexes");

        //Video
        if (mediaControls == null) {
            mediaControls = new MediaController(MainActivity.this);
        }

        // Find your VideoView in your video_main.xml layout
        myVideoView = findViewById(fr.enssat.ac.webviewproject.R.id.video_view);

        // Create a progressbar
        progressDialog = new ProgressDialog(MainActivity.this);
        // Set progressbar title
        progressDialog.setTitle("Wait during video is loading");
        // Set progressbar message
        progressDialog.setMessage("Loading...");

        progressDialog.setCancelable(false);
        // Show progressbar
        progressDialog.show();

        try {
            myVideoView.setMediaController(mediaControls);
            myVideoView.setVideoPath(videoUrl);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        myVideoView.requestFocus();
        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                myVideoView.seekTo(initialPosition);
                if (initialPosition == 0) {
                    myVideoView.start();
                } else {
                    myVideoView.pause();
                }
            }
        });

        //WebView
        webview = findViewById(fr.enssat.ac.webviewproject.R.id.web_view);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(wikiUrl);

        //Button
        Button btnIntro = findViewById(fr.enssat.ac.webviewproject.R.id.btnIntro);
        Button btnTitle = findViewById(fr.enssat.ac.webviewproject.R.id.btnTitle);
        Button btnAssault = findViewById(fr.enssat.ac.webviewproject.R.id.btnAssault);
        Button btnButterflies = findViewById(fr.enssat.ac.webviewproject.R.id.btnButterflies);
        Button btnPayback = findViewById(fr.enssat.ac.webviewproject.R.id.btnPayback);
        Button btnCast = findViewById(fr.enssat.ac.webviewproject.R.id.btnCast);

        btnIntro.setTag("Intro");
        btnTitle.setTag("Title");
        btnAssault.setTag("Assault");
        btnButterflies.setTag("Butterflies");
        btnPayback.setTag("Payback");
        btnCast.setTag("Cast");

        btnIntro.setOnClickListener(myOnlyHandler);
        btnTitle.setOnClickListener(myOnlyHandler);
        btnAssault.setOnClickListener(myOnlyHandler);
        btnButterflies.setOnClickListener(myOnlyHandler);
        btnPayback.setOnClickListener(myOnlyHandler);
        btnCast.setOnClickListener(myOnlyHandler);

        //Handler
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                String url=msg.getData().getString(PROGRESS_WEB_VIEW);
                Log.d(TAG,"Message received :" + url);
                webview.loadUrl(url);
            }
        };
    }

    /**
     * In this function, we work on the handler (thread + send message)
     * The handler will (normaly) receive a bundle message
     * The message will be treated in the handleMessage(msg) function
     *
     */
    public void onStart(){
        Log.d(TAG,"OnStart called");
        super.onStart();
        mThread = new Thread(new Runnable() {
            //Le Bundle qui porte les données du Message et sera transmis au Handler
            Bundle messageBundle=new Bundle();
             //Le message échangé entre la Thread et le Handler
            Message myMessage;
            @Override
            public void run() {
                try {
                    while(true) {
                        if(myVideoView.isPlaying()) {
                            Log.d(TAG, "Running, current position : " + myVideoView.getCurrentPosition());
                            Log.d(TAG, "currentWebViewTitle : " + currentWebViewTitle);
                            Log.d(TAG, "dataManager.getContextByPosition(myVideoView.getCurrentPosition()/1000)) : " + dataManager.getContextByPosition(myVideoView.getCurrentPosition()/1000));

                            if(!currentWebViewTitle.equals(dataManager.getContextByPosition(myVideoView.getCurrentPosition()/1000))){

                                currentWebViewTitle = dataManager.getContextByPosition(myVideoView.getCurrentPosition()/1000);
                                // Envoyer le message au Handler (la méthode handler.obtainMessage est plus efficace
                                // que créer un message à partir de rien, optimisation du pool de message du Handler)
                                //Instanciation du message (la bonne méthode):
                                myMessage=mHandler.obtainMessage();
                                //Ajouter des données à transmettre au Handler via le Bundle
                                messageBundle.putString(PROGRESS_WEB_VIEW, dataManager.getUrlByPosition(myVideoView.getCurrentPosition()/1000));
                                //Ajouter le Bundle au message
                                myMessage.setData(messageBundle);
                                //Envoyer le message
                                mHandler.sendMessage(myMessage);
                            }
                        }
                        //Let other threads to work
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mThread.start();
    }

    private View.OnClickListener myOnlyHandler = new View.OnClickListener() {
        public void onClick(View v) {
            //Just a test
            Log.d(TAG,"Button on clicked, button tag: "+v.getTag());
            Log.d(TAG,"Button on clicked, video goes to : "+ dataManager.getPositionByContext(v.getTag().toString()));
            Log.d(TAG,"Button on clicked, video currentDuration : "+myVideoView.getCurrentPosition());
            try{
                myVideoView.seekTo(dataManager.getPositionByContext(v.getTag().toString())*1000);
            } catch (Exception e){
                Log.d(TAG, "Exception " + e);
            }

        }
    };

   @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Interrupt the current thread (or else, multiple messages are going to be sent to the handler)
        mThread.interrupt();
        //Save the current video position in order to restore
        savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
        //Pause the video (why not)
        myVideoView.suspend();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initialPosition = savedInstanceState.getInt("Position");
        myVideoView.seekTo(initialPosition);
        myVideoView.isPlaying();
    }
}
