package com.dmonzonis.nookpendium

import android.content.Context
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.InputStream

data class Record(
    val id: String,
    val name: String,
    val price: String,
    val time: String,
    val location: String,
    val shadowSize: Int?,
    val imageId: Int,
    val availability: IntArray,
    var captured: Boolean?
)

class RecordXmlParser(private val context: Context) {

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
        var location = ""
        var shadowSize: Int? = null
        var availability = IntArray(12)

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
                "location" -> location = readText(parser)
                "shadow_size" -> shadowSize = readText(parser).toIntOrNull()
                "image" -> imageFilename = readText(parser)
                "availability" -> {
                    availability =
                        readText(parser).map { it.toString().toIntOrNull() ?: 0 }.toIntArray()
                    if (availability.size != 12) {
                        throw IllegalArgumentException("Availability data has the wrong size")
                    }
                }
                else -> readText(parser)  // Invalid tag, just read and dump contents
            }
        }

        imageFilename = File(imageFilename).nameWithoutExtension
        var imageId: Int =
            context.resources.getIdentifier(imageFilename, "drawable", context.packageName)
        if (imageId == 0) {
            // Resource was not found, use placeholder instead
            imageId = R.drawable.blank_image
        }

        return Record(id, name, price, time, location, shadowSize, imageId, availability, null)
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
