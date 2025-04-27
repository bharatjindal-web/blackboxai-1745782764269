package com.example.messagingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private EditText editTextKey;
    private Button buttonSend;
    private ListView listViewMessages;

    private DatabaseHelper databaseHelper;
    private MessageAdapter messageAdapter;
    private List<String> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        editTextKey = findViewById(R.id.editTextKey);
        buttonSend = findViewById(R.id.buttonSend);
        listViewMessages = findViewById(R.id.listViewMessages);

        databaseHelper = new DatabaseHelper(this);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        listViewMessages.setAdapter(messageAdapter);

        loadMessages();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                String key = editTextKey.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(MainActivity.this, "Please enter the encryption key", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String encryptedMessage = CryptoUtils.encrypt(message, key);
                    databaseHelper.insertMessage(encryptedMessage);
                    messageList.add("Encrypted: " + encryptedMessage);

                    String decryptedMessage = CryptoUtils.decrypt(encryptedMessage, key);
                    messageList.add("Decrypted: " + decryptedMessage);

                    messageAdapter.notifyDataSetChanged();
                    editTextMessage.setText("");
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Encryption error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadMessages() {
        List<String> encryptedMessages = databaseHelper.getAllMessages();
        String key = editTextKey.getText().toString().trim();

        for (String encryptedMessage : encryptedMessages) {
            messageList.add("Encrypted: " + encryptedMessage);
            if (!key.isEmpty()) {
                try {
                    String decryptedMessage = CryptoUtils.decrypt(encryptedMessage, key);
                    messageList.add("Decrypted: " + decryptedMessage);
                } catch (Exception e) {
                    messageList.add("Decryption failed");
                }
            }
        }
        messageAdapter.notifyDataSetChanged();
    }
}
