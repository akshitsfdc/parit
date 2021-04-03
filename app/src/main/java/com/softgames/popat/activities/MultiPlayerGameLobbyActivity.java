package com.softgames.popat.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.softgames.popat.R;
import com.softgames.popat.adapters.ClickListenerAdapter;
import com.softgames.popat.broadCasts.EarphoneReceiver;
import com.softgames.popat.helper.CustomDragShadowBuilder;
import com.softgames.popat.models.GameRequest;
import com.softgames.popat.models.PopatMove;
import com.softgames.popat.models.PopatPlayer;
import com.softgames.popat.service.ReceiverService;
import com.softgames.popat.service.SenderService;
import com.google.gson.reflect.TypeToken;

import org.webrtc.DataChannel;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.media.AudioManager.ACTION_HEADSET_PLUG;

public class MultiPlayerGameLobbyActivity extends BaseActivity implements DataChannel.Observer  {


    private ImageView popatImage, box1, box2, box3, handUpIcon;
    private String popat_tag = "popat tag";

    private View backWrapper, srParent, sendPopatLinkWrapper,
            gameBoxContainer, handIndicatorWrapper, waitingTextWrapper;

    private TextView srTimerText,
            sendPopatLink,
            waitingText, lPopatWrapTimerText, chooseBoxTimer, rOpenPopatTimerTextView, roundTextValue;

    private int currentRound = 1, totalRounds = 3;
    private PopatMove currentMove, remoteMove;
    private Button sendPopatButton;
    private PopatPlayer localPopatPlayer;
    private PopatPlayer remotePopatPlayer;

    private boolean isSender;
    private String roomId;
    private SenderService senderService;
    private ReceiverService receiverService;
    private Gson gson;

    private DataChannel dataChannel;

    private ListenerRegistration gameRequestListener;

    private CountDownTimer lBoxSelectTimer, lPopatWrapTimer, rBoxSelectTimer, rPopatWrapTimer;

    //Realtime Game Boxes
    private ImageView gBox1, gBox2, gBox3;

    private AudioManager audioManager;
    private EarphoneReceiver earphonesReceiver;
    private IntentFilter intentFilter;
    private TextView lPlayerName,rPlayerName;
    private ImageView lPlayerPic, rPlayerPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_lobby);

        initViews();
        setLocalPlayer();
    }

    private void initViews(){

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

        popatImage = findViewById(R.id.popatImage);

        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box3 = findViewById(R.id.box3);
        backWrapper = findViewById(R.id.backWrapper);
        srTimerText = findViewById(R.id.srTimerText);
        srParent = findViewById(R.id.srParent);
        sendPopatButton = findViewById(R.id.sendPopatButton);
        handUpIcon = findViewById(R.id.handUpIcon);
        sendPopatLink = findViewById(R.id.sendPopatLink);
        sendPopatLinkWrapper = findViewById(R.id.sendPopatLinkWrapper);
        gameBoxContainer = findViewById(R.id.gameBoxContainer);
        waitingText = findViewById(R.id.waitingText);
        handIndicatorWrapper = findViewById(R.id.handIndicatorWrapper);
        lPopatWrapTimerText = findViewById(R.id.lPopatWrapTimerText);
        waitingTextWrapper = findViewById(R.id.waitingTextWrapper);
        chooseBoxTimer = findViewById(R.id.chooseBoxTimer);
        rOpenPopatTimerTextView = findViewById(R.id.rOpenPopatTimerTextView);
        roundTextValue = findViewById(R.id.roundTextValue);

        rPlayerPic = findViewById(R.id.rPlayerPic);
        rPlayerName = findViewById(R.id.rPlayerName);

        lPlayerPic = findViewById(R.id.lPlayerPic);
        lPlayerName = findViewById(R.id.lPlayerName);
        
        gBox1 = findViewById(R.id.gBox1);
        gBox2 = findViewById(R.id.gBox2);
        gBox3 = findViewById(R.id.gBox3);

        gson = new Gson();
        localPopatPlayer = MainActivity.CURRENT_USER;

        popatImage.setTag(popat_tag);

        setPopatListeners();

        popatImage.setOnDragListener(new customDragEventListener());
        box1.setOnDragListener(new customDragEventListener());
        box2.setOnDragListener(new customDragEventListener());
        box3.setOnDragListener(new customDragEventListener());

        backWrapper.setOnClickListener(v -> {
            srParent.setVisibility(View.GONE);
        });
        sendPopatButton.setOnClickListener(v -> {
            sendPopatClick();
        });
        sendPopatLinkWrapper.setOnClickListener(v -> {
            srParent.setVisibility(View.VISIBLE);
        });

        box1.setOnClickListener(new ClickListenerAdapter() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                boxDoubleClicked(v);
            }
        });
        box2.setOnClickListener(new ClickListenerAdapter() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                boxDoubleClicked(v);
            }
        });
        box3.setOnClickListener(new ClickListenerAdapter() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {

                boxDoubleClicked(v);
            }
        });

        showImage(R.drawable.popat, popatImage);
        showImage(R.drawable.hand_up, handUpIcon);

        gameBoxContainer.setVisibility(View.GONE);

        searchPlayer(0);

        gBox1.setOnClickListener(this::myBoxOpen);
        gBox2.setOnClickListener(this::myBoxOpen);
        gBox3.setOnClickListener(this::myBoxOpen);


    }

    private void setRemotePlayer(){
        rPlayerName.setText(remotePopatPlayer.getName());
        showRemoteImage(remotePopatPlayer.getProfilePic(), rPlayerPic);

    }
    private void setLocalPlayer(){
        lPlayerName.setText(localPopatPlayer.getName());
        showRemoteImage(localPopatPlayer.getProfilePic(), lPlayerPic);
    }

    private void myBoxOpen(View v){

        if(currentMove != null){
            uiUtils.showShortSnakeBar("You can not open your own box!");
            return;
        }
        if(v.getId() == R.id.gBox1){
            constructPopatResponse(1);
        }else if(v.getId() == R.id.gBox2){
            constructPopatResponse(2);
        }else if(v.getId() == R.id.gBox3){
            constructPopatResponse(3);
        }

    }
    private void constructPopatResponse(int openedBox){

        currentMove = new PopatMove();
        currentMove.setMove(false);
        currentMove.setRound(currentRound);
        currentMove.setOpenedBox(openedBox);

        if(remoteMove != null && remoteMove.isMove() && remoteMove.getSelectedBox() == openedBox){
            uiUtils.showShortSnakeBar("Your choice was right!");
        }else {
            uiUtils.showShortSnakeBar("Your choice was wrong!");

        }

        sendMove();

        currentMove = null;
        remoteMove = null;
        afterMyChoice();
    }
    private void createConnection(){



        if(isSender){
            senderService = new SenderService(this, roomId);
            senderService.createOffer();
            dataChannel = senderService.getDataChannel();
        }else {
            
            receiverService = new ReceiverService(this, roomId);
            receiverService.setOffer();
            
        }
    }
    private void sendPopatClick(){
        if(currentMove == null){
            return;
        }
        sendMove();
        afterMyTurn();
        if(isSender){
            ++currentRound;
        }

    }
    private void boxDoubleClicked(View v){
        if(currentMove == null){
            return;
        }
        int selectedBox = getSelectedBoxNumber(v.getId());

        if(currentMove.getSelectedBox() == selectedBox){
            popatImage.setVisibility(View.VISIBLE);
            currentMove = null;
            sendPopatButton.setEnabled(false);
        }else{
            uiUtils.showShortSnakeBar("Popat is not in this box");
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setPopatListeners() {

        popatImage.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData.Item item = new ClipData.Item((String) v.getTag());

                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        item);

                View.DragShadowBuilder myShadow = new CustomDragShadowBuilder(popatImage);

                // Starts the drag

                v.startDrag(dragData,  // the data to be dragged
                        myShadow,  // the drag shadow builder
                        null,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );
                return true;
            }


        });

    }

    


    protected class customDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public boolean onDrag(View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        ((ImageView)v).setColorFilter(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a green tint to the View. Return true; the return value is ignored.

                    ((ImageView)v).setColorFilter(Color.GREEN);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
                    ((ImageView)v).setColorFilter(Color.BLUE);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:

                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    String dragData = item.getText().toString();

                    // Turns off any color tints
                    ((ImageView)v).clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true

                    if(v.getId() != R.id.popatImage){
                        currentMove = constructMoveObject(v.getId());
                        popatImage.setVisibility(View.GONE);
                        sendPopatButton.setEnabled(true);
                    }

                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting
                    ((ImageView)v).clearColorFilter();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Does a getResult(), and displays what happened.
                    if (event.getResult()) {
                        Toast.makeText(MultiPlayerGameLobbyActivity.this, "The drop was handled.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MultiPlayerGameLobbyActivity.this, "The drop didn't work.", Toast.LENGTH_LONG).show();

                    }

                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    };




    private void setTimerText(TextView textView, String timeLocal){
        runOnUiThread(() -> {
            textView.setText("0:"+timeLocal);
        });
    }
    private  String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
    private PopatMove constructMoveObject(int selectedId){

        PopatMove popatMove = new PopatMove();

        popatMove.setMove(true);
        popatMove.setSelectedBox(getSelectedBoxNumber(selectedId));
        popatMove.setRound(currentRound);

        return popatMove;
    }
    private void sendMove(){

        Log.d(TAG, "sendMove: "+this.currentMove.toString());

        if(currentMove == null){
            return;
        }

        popatImage.setVisibility(View.VISIBLE);

        String json = gson.toJson(currentMove);

        dataChannel.send(new DataChannel.Buffer(ByteBuffer.wrap(json.getBytes()), false));

    }
    private int getSelectedBoxNumber(int selectedId){
        int selectedBox = 0;
        switch(selectedId){
            case R.id.box1:{
                selectedBox = 1;
                break;
            }
            case R.id.box2:{
                selectedBox = 2;
                break;

            }
            case R.id.box3:{
                selectedBox = 3;
                break;
            }
        }
        return selectedBox;
    }

    private void removeTimer(CountDownTimer timer){
        if(timer != null){
            timer.cancel();
        }
    }
    private void gameStarted(){
        runOnUiThread(() -> {
            String waitingStr = "Waiting for Popat boxes from You";
            waitingText.setText(waitingStr);
            gameBoxContainer.setVisibility(View.GONE);
            handIndicatorWrapper.setVisibility(View.GONE);
            startLocalPopatWrapTimer();
            currentMove = new PopatMove();
            currentMove.setStart(true);
            currentMove.setMove(false);
            currentMove.setRound(currentRound);
            roundTextValue.setText(String.valueOf(currentRound));
            sendMove();

        });


    }
    private void afterOppOpenPopat(){
        runOnUiThread(() -> {

            startRemotePopatWrapTimer();

            waitingTextWrapper.setVisibility(View.VISIBLE);
            gameBoxContainer.setVisibility(View.GONE);
            String waitingStr = "Waiting for Popat boxes from "+remotePopatPlayer.getName();
            waitingText.setText(waitingStr);

            handIndicatorWrapper.setVisibility(View.GONE);
            sendPopatLinkWrapper.setVisibility(View.GONE);

            rOpenPopatTimerTextView.setVisibility(View.GONE);
            removeTimer(rBoxSelectTimer);
        });

    }
    private void afterOppMove(){
        runOnUiThread(() ->{
            
            waitingTextWrapper.setVisibility(View.GONE);
            gameBoxContainer.setVisibility(View.VISIBLE);
            sendPopatLinkWrapper.setVisibility(View.GONE);
            handIndicatorWrapper.setVisibility(View.VISIBLE);

            startLocalBoxSelectTimer();
            
        });


    }
    private void afterMyChoice(){
        runOnUiThread(() -> {
            waitingTextWrapper.setVisibility(View.VISIBLE);
            String waitingStr = "Waiting for Popat boxes from You";
            waitingText.setText(waitingStr);
            gameBoxContainer.setVisibility(View.GONE);
          
            handIndicatorWrapper.setVisibility(View.GONE);
            sendPopatLinkWrapper.setVisibility(View.VISIBLE);

            startLocalPopatWrapTimer();
            removeTimer(lBoxSelectTimer);
        });


    }
    private void afterMyTurn(){
        runOnUiThread(() -> {

            removeTimer(lPopatWrapTimer);
            handIndicatorWrapper.setVisibility(View.GONE);
            sendPopatLinkWrapper.setVisibility(View.GONE);

            waitingTextWrapper.setVisibility(View.GONE);

            gameBoxContainer.setVisibility(View.VISIBLE);

            srParent.setVisibility(View.GONE);

            rOpenPopatTimerTextView.setVisibility(View.VISIBLE);
            startRemoteBoxSelectTimer();
        });

    }


    @Override
    public void onBufferedAmountChange(long l) {

    }

    @Override
    public void onStateChange() {

        Log.d(TAG, "onStateChange: data channel ");

        if(!isSender && this.dataChannel == null){
            this.dataChannel = receiverService.getDataChannel();
        }
        Log.d(TAG, "onStateChange: "+this.dataChannel.state() );

        hideMsgFragment();

        if(isSender && this.dataChannel.state().toString().toLowerCase().equals("open")){
            gameStarted();
        }
        setConnectionStatus(this.dataChannel.state());
    }

    @Override
    public void onMessage(DataChannel.Buffer buffer) {
        Log.d(TAG, "onMessage: ");

        ByteBuffer data = buffer.data;
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        final String command = new String(bytes);

        Type type = new TypeToken<PopatMove>() {}.getType();
        remoteMove = gson.fromJson(command, type);

        routeRemoteMove();

    }

    private void routeRemoteMove(){
        if(remoteMove.isStart()){
            String waitingStr = "Waiting for Popat boxes from "+remotePopatPlayer.getName();
            runOnUiThread(() -> {
                waitingText.setText(waitingStr);
                gameBoxContainer.setVisibility(View.GONE);
            });
            afterOppOpenPopat();

        }else if(remoteMove.isMove()){
            afterOppMove();
            roundTextValue.setText(String.valueOf(currentRound));
            if(!isSender){
                ++currentRound;
            }
        }else if(!remoteMove.isMove()){
            if(currentMove != null && currentMove.getSelectedBox() == remoteMove.getOpenedBox()){
               uiUtils.showLongSnakeBar(remotePopatPlayer.getName()+" made a right choice");
            }else {
                uiUtils.showLongSnakeBar(remotePopatPlayer.getName()+" made a wrong choice");
            }
            currentMove = null;
            afterOppOpenPopat();
        }
    }

    //Timers
    private void startLocalBoxSelectTimer(){
        runOnUiThread(() -> {
            lBoxSelectTimer = new CountDownTimer(30000, 1000) {
                int time = 19;

                public void onTick(long millisUntilFinished) {
                    setTimerText(chooseBoxTimer, checkDigit(time));
                    time--;
                }

                public void onFinish() {
                    chooseBoxTimer.setText("0:00");
                    Log.d(TAG, "onFinish: local box select timer finished");
                    removeTimer(lBoxSelectTimer);
                }

            }.start();
        });
    }
    private void startLocalPopatWrapTimer(){

        runOnUiThread(() -> {
            lPopatWrapTimer = new CountDownTimer(30000, 1000) {
                int time = 29;
                public void onTick(long millisUntilFinished) {
                    setTimerText(lPopatWrapTimerText, checkDigit(time));
                    setTimerText(srTimerText, checkDigit(time));
                    time--;
                }

                public void onFinish() {
                    srTimerText.setText("0:00");
                    removeTimer(lPopatWrapTimer);

                }

            }.start();
        });


    }
    private void startRemoteBoxSelectTimer(){
        runOnUiThread(() -> {
            rBoxSelectTimer = new CountDownTimer(30000, 1000) {
                int time = 19;

                public void onTick(long millisUntilFinished) {
                     setTimerText(rOpenPopatTimerTextView, checkDigit(time));
                    time--;
                }

                public void onFinish() {
                    srTimerText.setText("0:00");
                    removeTimer(rBoxSelectTimer);

                }

            }.start();
        });
    }
    private void startRemotePopatWrapTimer(){

        runOnUiThread(() -> {
            rPopatWrapTimer = new CountDownTimer(30000, 1000) {
                int time = 29;

                public void onTick(long millisUntilFinished) {
                    setTimerText(lPopatWrapTimerText, checkDigit(time));
                    time--;
                }

                public void onFinish() {
                   
                    removeTimer(rPopatWrapTimer);

                }

            }.start();
        });

    }
    //Player Searching
    private void searchPlayer(int popatLevel){

        Map<String, Object> map = new HashMap<>();
        map.put("popatLevel", popatLevel);
        map.put("open", true);
        map.put("hostRecognized", false);

        showMsgFragment();
        messageFragment.setMessage("Searching...");

        fireStoreService.getQueryData(getString(R.string.game_request_collection),
                map, getString(R.string.game_request_order_by), 1)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    routeDocCount(popatLevel, queryDocumentSnapshots);

                }).addOnFailureListener(e -> {
            Log.d(TAG, "searchPlayer: "+e.getMessage());
            e.printStackTrace();
            hideMsgFragment();
        });
    }

    private void routeDocCount(int popatLevel, QuerySnapshot queryDocumentSnapshots){

        if(queryDocumentSnapshots.getDocuments().size() == 0){

            GameRequest gameRequest = getGameRequestModel(popatLevel);

            fireStoreService.setData(getString(R.string.game_request_collection), MainActivity.CURRENT_USER.getUserId(), gameRequest)

                    .addOnSuccessListener(aVoid -> {
                        addGameHostListener();
                        isSender = true;
                        roomId = gameRequest.getRoomId();
                        createConnection();
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "routeDocCount: "+e.getMessage());
                        e.printStackTrace();
                    });

        }else {
            Map<String, Object> map = new HashMap<>();
            map.put("open", false);
            map.put("client", localPopatPlayer);

            DocumentReference documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
            fireStoreService.updateData(documentReference, map)
                    .addOnSuccessListener(aVoid -> {
                        addGameClientListener(documentReference);
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "getGameRequestModel: "+e.getMessage());
                    });
        }
    }




    private void addGameClientListener(DocumentReference documentReference){


        gameRequestListener = documentReference.addSnapshotListener((value, error) -> {

            if (value != null && value.exists()) {
                GameRequest gameRequest = value.toObject(GameRequest.class);

                Log.d(TAG, "addGameClientListener: "+gameRequest.getRoomId());

                if (gameRequest != null && gameRequest.isHostRecognized()){

                    isSender = false;
                    roomId = gameRequest.getRoomId();

                    remotePopatPlayer = gameRequest.getHost();
                    setRemotePlayer();

                    createConnection();

                    gameRequestListener.remove();
                    documentReference.delete();

                }
            }

        });
    }
    private void addGameHostListener(){


        DocumentReference documentReference = fireStoreService.getDocReference(getString(R.string.game_request_collection), MainActivity.CURRENT_USER.getUserId());

        gameRequestListener = documentReference.addSnapshotListener((value, error) -> {

            if (value != null && value.exists()) {
                GameRequest gameRequest = value.toObject(GameRequest.class);
                if (gameRequest != null && !gameRequest.isOpen()){

                    Map<String, Object> map = new HashMap<>();
                    map.put("hostRecognized", true);

                    remotePopatPlayer = gameRequest.getClient();
                   setRemotePlayer();
                    fireStoreService.updateData(documentReference, map)
                            .addOnSuccessListener(aVoid -> {
                                gameRequestListener.remove();
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "getGameRequestModel: "+e.getMessage());
                            });


                }
            }

        });

    }

    private GameRequest getGameRequestModel(int popatLevel){
        //PopatPlayer host, int popatLevel, boolean open, boolean hostRecognized, String roomId, long createdTime
        long currentTime = new Date().getTime();
        GameRequest gameRequest = new GameRequest(MainActivity.CURRENT_USER, popatLevel, true,
                false, MainActivity.CURRENT_USER.getUserId()+"_"+currentTime, currentTime);
        return gameRequest;
    }


    @Override
    protected void onDestroy() {

        try {
            if(senderService != null){
                senderService.close();
            }
            if(receiverService != null){
                receiverService.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }


    private void setConnectionStatus(DataChannel.State status) {
        runOnUiThread(() -> {

            try{


                if (status.equals(DataChannel.State.OPEN)){


                    if (!Objects.equals(getIntent().getAction(), ACTION_HEADSET_PLUG)){
                        audioManager.setSpeakerphoneOn(true);
                    }

                    earphonesReceiver = new EarphoneReceiver(audioManager);
                    registerReceiver(earphonesReceiver, intentFilter);

                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

                }

                if (status.equals(DataChannel.State.CLOSED)){


                    audioManager.setMode(AudioManager.MODE_NORMAL);

                    unregisterReceiver(earphonesReceiver);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

}