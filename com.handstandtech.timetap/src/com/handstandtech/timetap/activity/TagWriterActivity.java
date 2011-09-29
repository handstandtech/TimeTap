package com.handstandtech.timetap.activity;

import java.util.Arrays;
import java.util.Random;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;

public class TagWriterActivity extends TimeTapBaseActivity {
  private NfcAdapter mNfcAdapter = null;
  private Tag mNfcTag = null;
  private String uri;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    setContentView(R.layout.write_tag);

    Bundle bundle = getIntent().getExtras();
    uri = bundle.getString(Constants.PROP_URI);
    
    TextView taskNameTextView = (TextView) findViewById(R.id.task_name);
    TextView projectNameTextView = (TextView) findViewById(R.id.project_name);
    TextView clientNameTextView = (TextView) findViewById(R.id.client_name);
    
    TimerResponse currTimer = getTimeTap().getCurrTimer();
    taskNameTextView.setText(currTimer.getTask());
    projectNameTextView.setText(currTimer.getProject());
    clientNameTextView.setText(currTimer.getClient());

    findViewById(R.id.writeTagBtn).setOnClickListener(mTagSetter);

    if (!"android.nfc.action.TAG_DISCOVERED".equals(getIntent().getAction())) {
      disableWrite();
    } else {
      mNfcTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
      Toast.makeText(this, "Tag ready...", Toast.LENGTH_SHORT).show();
      mNfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
      enableWrite();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
  }

  @Override
  protected void onPause() {
    super.onPause();
    mNfcAdapter.disableForegroundDispatch(this);
  }

  private View.OnClickListener mTagSetter = new View.OnClickListener() {

    public void onClick(View v) {
      try {
        NdefMessage ndefMessage = ndefForUri(uri);
        writeNfcTag(ndefMessage);
        Toast.makeText(TagWriterActivity.this, "Tag Successfully Set!", Toast.LENGTH_LONG).show();
        TagWriterActivity.this.finish();
      } catch (Exception e) {
        Log.d("tagwriter", "Error writing tag", e);
        Toast.makeText(TagWriterActivity.this, "Error writing tag.", Toast.LENGTH_SHORT).show();
        disableWrite();
      }
    }
  };

  private NdefMessage ndefForUri(String uri) {
    byte[] payload = uri.toString().getBytes();
    byte[] id;
    if (payload.length > 255) {
      id = Arrays.copyOfRange(payload, 0, 255);
    } else {
      id = payload;
    }
    NdefRecord[] records = new NdefRecord[1];
    records[0] = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, NdefRecord.RTD_URI, id, payload);
    NdefMessage message = new NdefMessage(records);
    return message;
  }

  private NdefMessage getLongishNdef() {
    int size = 2500;
    StringBuffer buffer = new StringBuffer(size);
    for (int i = 0; i < size; i++) {
      buffer.append(new Random().nextInt(10));
    }
    byte[] payload = buffer.toString().getBytes();
    byte[] id = new byte[0];

    NdefRecord[] records = new NdefRecord[1];
    records[0] = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, id, payload);
    NdefMessage message = new NdefMessage(records);
    return message;
  }

  // TODO: run on new thread.
  private void writeNfcTag(NdefMessage ndefMessage) throws Exception {
    Tag nfcTag = mNfcTag;
    Log.d(TAG, "have nfc tag " + nfcTag);
    Log.d(TAG, "tech list:");
    for (String techStr : nfcTag.getTechList()) {
      if ("android.nfc.tech.Ndef".equals(techStr)) {
        Ndef tech = Ndef.get(nfcTag);
        tech.connect();
        tech.writeNdefMessage(ndefMessage);
        tech.close();
        return;
      }
    }

    NdefFormatable ndefFormatable = NdefFormatable.get(nfcTag);
    ndefFormatable.connect();
    ndefFormatable.format(ndefMessage);
    ndefFormatable.close();
  }

  void disableWrite() {
    Button writeTagBtn = (Button) findViewById(R.id.writeTagBtn);
    writeTagBtn.setText("Hold Phone Against Tag");
    writeTagBtn.setEnabled(false);
  }

  void enableWrite() {
    Button writeTagBtn = (Button) findViewById(R.id.writeTagBtn);
    writeTagBtn.setText("Write Tag");
    writeTagBtn.setEnabled(true);
  }
}