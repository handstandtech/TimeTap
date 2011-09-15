package com.handstandtech.timetap.activity;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;
import com.handstandtech.timetap.nfc.NdefMessageParser;
import com.handstandtech.timetap.nfc.ParsedUriRecord;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class TagHandlerActivity extends TimeTapBaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.tag_viewer);
    resolveIntent(getIntent());
  }

  void resolveIntent(Intent intent) {
    // Parse the intent
    String action = intent.getAction();
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
      // When a tag is discovered we send it to the service to be save. We
      // include a PendingIntent for the service to call back onto. This
      // will cause this activity to be restarted with onNewIntent(). At
      // that time we read it from the database and view it.
      Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
      NdefMessage[] msgs;
      if (rawMsgs != null) {
        msgs = new NdefMessage[rawMsgs.length];
        for (int i = 0; i < rawMsgs.length; i++) {
          msgs[i] = (NdefMessage) rawMsgs[i];
        }
      } else {
        // Unknown tag type
        byte[] empty = new byte[] {};
        NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
        NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
        msgs = new NdefMessage[] { msg };
      }

      buildTagViews(msgs);
    } else {
      Log.e(TAG, "Unknown intent " + intent);
      finish();
      return;
    }
  }

  void buildTagViews(NdefMessage[] msgs) {
    if (msgs == null || msgs.length == 0) {
      return;
    }
    List<ParsedUriRecord> records = NdefMessageParser.parse(msgs[0]);
    final int size = records.size();
    for (int i = 0; i < size; i++) {
      ParsedUriRecord record = records.get(i);
      Log.i(TAG, "URI IS: " + record.getUri().toString());
      String projectIdString = record.getUri().getQueryParameter("project");
      String taskIdString = record.getUri().getQueryParameter("task");

      Log.i(TAG, "Project: " + projectIdString);
      Log.i(TAG, "Task: " + taskIdString);

      if (getTimeTap().isPreviouslyLoggedIn(TagHandlerActivity.this)) {
        Log.i(TAG, "Starting Intent!");
        Intent newIntent = new Intent(TagHandlerActivity.this, TaskScreenActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.PROP_PROJECT_ID, Long.parseLong(projectIdString));
        bundle.putLong(Constants.PROP_TASK_ID, Long.parseLong(taskIdString));
        newIntent.putExtras(bundle);
        startActivityForResult(newIntent, 0);
      } else {
        // Not Logged In, Redirect to Login
        Toast.makeText(TagHandlerActivity.this, "ERROR: Not Logged In.", Toast.LENGTH_LONG);
        Intent showHomeIntent = new Intent(this, LoginActivity.class);
        startActivity(showHomeIntent);
      }
    }
  }

  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
    resolveIntent(intent);
  }
}
