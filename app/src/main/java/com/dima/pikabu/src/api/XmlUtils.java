package com.dima.pikabu.src.api;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XmlUtils {
    XmlUtils() {
    }

    public static int readTagInt(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        int result = -1;
        parser.require(2, null, tagName);
        if (parser.next() == 4) {
            result = Integer.parseInt(parser.getText());
            parser.nextTag();
        }
        parser.require(3, null, tagName);
        return result;
    }

    public static String readTagString(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        String result = null;
        parser.require(2, null, tagName);
        if (parser.next() == 4) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(3, null, tagName);
        return result;
    }

    public static int readIntAttribute(XmlPullParser parser, String attributeName, int defValue) {
        try {
            defValue = Integer.parseInt(parser.getAttributeValue(null, attributeName));
        } catch (NumberFormatException e) {
        }
        return defValue;
    }

    public static long readLongAttribute(XmlPullParser parser, String attributeName, long defValue) {
        try {
            defValue = Long.parseLong(parser.getAttributeValue(null, attributeName));
        } catch (NumberFormatException e) {
        }
        return defValue;
    }

    public static String readStringAttribute(XmlPullParser parser, String attributeName) {
        return parser.getAttributeValue(null, attributeName);
    }

    public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != 2) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case 2 /*2*/:
                    depth++;
                    break;
                case 3 /*3*/:
                    depth--;
                    break;
                default:
                    break;
            }
        }
    }
}
