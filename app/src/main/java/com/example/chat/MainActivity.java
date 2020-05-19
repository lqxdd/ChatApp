package com.example.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import android.text.format.DateFormat;


public class MainActivity extends AppCompatActivity {
    private static int SIGN_IN_CODE = 1;
    private RelativeLayout activity_main;
    private FirebaseListAdapter<Message> adapter;
    private FloatingActionButton sendBtn;
    private FloatingActionButton closeBtn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_main, "You are signed-in", Snackbar.LENGTH_LONG).show();
                displayAllMessages();
            } else {
                Snackbar.make(activity_main, "You are not signed-in", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = findViewById(R.id.activity_main);
        sendBtn = findViewById(R.id.btnSend);
        closeBtn = findViewById(R.id.btnClose);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                a_builder.setMessage("Close the app?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Quit");
                alert.show();
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(activity_main, "Message has been sent", Snackbar.LENGTH_LONG).show();
                EditText textField = findViewById(R.id.messageField);
                if (textField.getText().toString() == "") return;
                FirebaseDatabase.getInstance().getReference().push().setValue(
                        new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                textField.getText().toString()
                        ));
                textField.setText("");
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        else {
            Snackbar.make(activity_main, "You are signed-in", Snackbar.LENGTH_LONG).show();
            displayAllMessages();
        }

    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView mess_user, mess_time;
                BubbleTextView mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);
                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);

    }
}
