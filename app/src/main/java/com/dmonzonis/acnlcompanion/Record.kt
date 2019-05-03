package com.dmonzonis.acnlcompanion

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

data class Record(val name: String, val price: String, val time: String, val season: String)

class RecordXmlParser {

    private val ns: String? = null

    fun parse(inputStream: InputStream): List<Record> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readAllRecords(parser)
        }
    }

    private fun readAllRecords(parser: XmlPullParser): List<Record> {
        val records = mutableListOf<Record>()

        parser.require(XmlPullParser.START_TAG, ns, "RecordList")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            records.add(readRecord(parser))
        }

        return records
    }

    private fun readRecord(parser: XmlPullParser): Record {
        parser.require(XmlPullParser.START_TAG, ns, "Record")
        var name = ""
        var price = ""
        var time = ""
        var season = ""

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "name" -> name = readText(parser)
                "price" -> price = readText(parser)
                "time" -> time = readText(parser)
                "season" -> season = readText(parser)
            }
        }

        return Record(name, price, time, season)
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}