package com.dmonzonis.nookpendium

import android.content.Context
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

data class Record(val id: String,
                  val name: String,
                  val price: String,
                  val time: String,
                  val season: String,
                  val imageId: Int,
                  var captured: Boolean?)

class RecordXmlParser(val context: Context) {

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
        var id = ""
        var name = ""
        var imageFilename = ""
        var price = ""
        var time = ""
        var season = ""

        // Read info from each of the tags for this record
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "id" -> id = readText(parser)
                "name" -> name = readText(parser)
                "price" -> price = readText(parser)
                "time" -> time = readText(parser)
                "season" -> season = readText(parser)
                "image" -> imageFilename = readText(parser)
            }
        }

        var imageId: Int = context.resources.getIdentifier(imageFilename, "drawable", context.packageName)
        if (imageId == 0) {
            // Resource was not found
            // Use placeholder instead
            imageId = R.drawable.blank_image
        }

        return Record(id, name, price, time, season, imageId, null)
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