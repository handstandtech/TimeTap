package com.handstandtech.timetap.activity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
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

import com.google.common.primitives.Bytes;
import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;

public class TagWriterActivity extends TimeTapBaseActivity {
	private NfcAdapter mNfcAdapter = null;
	private Tag mNfcTag = null;
	private String uri;

	private static final byte[] EMPTY = new byte[0];

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

		if (!"android.nfc.action.TAG_DISCOVERED"
				.equals(getIntent().getAction())) {
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
				new Intent(this, getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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

				NdefRecord[] records = new NdefRecord[1];
				records[0] = ndefRecordForUri(Uri.parse(uri));
				NdefMessage ndefMessage = new NdefMessage(records);
				writeTag(ndefMessage);
				Toast.makeText(TagWriterActivity.this, "Tag Successfully Set!",
						Toast.LENGTH_LONG).show();
				TagWriterActivity.this.finish();
			} catch (Exception e) {
				Log.d("tagwriter", "Error writing tag", e);
				Toast.makeText(TagWriterActivity.this, "Error writing tag.",
						Toast.LENGTH_SHORT).show();
				disableWrite();
			}
		}
	};

	/**
	 * Convert a {@link Uri} to an {@link NdefRecord}
	 */
	public NdefRecord ndefRecordForUri(Uri uri) {
		byte[] uriBytes = uri.toString().getBytes(Charset.forName("UTF-8"));

		/*
		 * We prepend 0x00 to the bytes of the URI to indicate that this is the
		 * entire URI, and we are not taking advantage of the URI shortening
		 * rules in the NFC Forum URI spec section 3.2.2. This produces a
		 * NdefRecord which is slightly larger than necessary.
		 * 
		 * In the future, we should use the URI shortening rules in 3.2.2 to
		 * create a smaller NdefRecord.
		 */
		byte[] payload = Bytes.concat(new byte[] { 0x00 }, uriBytes);

		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_URI, EMPTY, payload);
		return record;
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
		records[0] = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, id, payload);
		NdefMessage message = new NdefMessage(records);
		return message;
	}

	// TODO: run on new thread.
	// TODO: Remove this old code, but see what is different.
	// private void writeTag(NdefMessage ndefMessage) throws Exception {
	// Tag nfcTag = mNfcTag;
	// Log.d(TAG, "have nfc tag " + nfcTag);
	// Log.d(TAG, "tech list:");
	// for (String techStr : nfcTag.getTechList()) {
	// if ("android.nfc.tech.Ndef".equals(techStr)) {
	// Ndef tech = Ndef.get(nfcTag);
	// tech.connect();
	// tech.writeNdefMessage(ndefMessage);
	// tech.close();
	// return;
	// }
	// }
	//
	// NdefFormatable ndefFormatable = NdefFormatable.get(nfcTag);
	// ndefFormatable.connect();
	// ndefFormatable.format(ndefMessage);
	// ndefFormatable.close();
	// }

	boolean writeTag(NdefMessage message) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(mNfcTag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					toast("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					toast("Tag capacity is " + ndef.getMaxSize()
							+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				toast("Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(mNfcTag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						toast("Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						toast("Failed to format tag.");
						return false;
					}
				} else {
					toast("Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			toast("Failed to write tag");
		}

		return false;
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

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}