/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.handstandtech.timetap.nfc;

import java.util.ArrayList;
import java.util.List;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

/**
 * Utility class for creating {@link ParsedNdefMessage}s.
 */
public class NdefMessageParser {

    // Utility class
    private NdefMessageParser() {

    }

    /** Parse an NdefMessage */
    public static List<ParsedUriRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsedUriRecord> getRecords(NdefRecord[] records) {
        List<ParsedUriRecord> elements = new ArrayList<ParsedUriRecord>();
        for (NdefRecord record : records) {
            if (ParsedUriRecord.isUri(record)) {
                elements.add(ParsedUriRecord.parse(record));
            }
        }
        return elements;
    }
}
