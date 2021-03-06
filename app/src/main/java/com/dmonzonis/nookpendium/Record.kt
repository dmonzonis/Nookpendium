package com.dmonzonis.nookpendium

import android.content.Context
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.InputStream

data class Record(
    val id: String,
    val name: Int,
    val price: Int?,
    val time: String,
    val location: Int,
    val shadowSize: Int?,
    val imageId: Int,
    val availability: IntArray,
    var captured: Boolean?,
    var donated: Boolean?
)

class Recordset(private val totalRecords: List<Record>, private val context: Context) {
    // Current subset of records after having applied filters/sorting
    var records = totalRecords

    fun applyFilter(filter: (r: Record) -> Boolean): List<Record> {
        records = records.filter(filter)
        return records
    }

    fun applySort(sortId: Int, descending: Boolean): List<Record> {
        records = when (sortId) {
            R.id.sortByName -> if (descending) {
                records.sortedByDescending { context.getString(it.name) }
            } else {
                records.sortedBy { context.getString(it.name) }
            }
            R.id.sortByPrice -> if (descending) {
                records.sortedByDescending { it.price }
            } else {
                records.sortedBy { it.price }
            }
            // Sort by id by default
            else -> if (descending) {
                records.sortedByDescending { it.id.toInt() }
            } else {
                records.sortedBy { it.id.toInt() }
            }
        }
        return records
    }

    // Removes all filters and sorting applied to the recordset
    fun restore() {
        records = totalRecords
    }
}

class RecordXmlParser(private val context: Context) {

    private val ns: String? = null

    fun parse(inputStream: InputStream): Recordset {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return Recordset(readAllRecords(parser), context)
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
        var name = R.string.unknown
        var imageFilename = ""
        var price: Int? = null
        var time = ""
        var location = R.string.unknown
        var shadowSize: Int? = null
        var availability = IntArray(12)

        // Read info from each of the tags for this record
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            when (parser.name) {
                "id" -> id = readText(parser)
                "name" -> name = readStringIdentifier(parser)
                "price" -> price = readText(parser).replace(",", "").toIntOrNull()
                "time" -> time = readText(parser)
                "location" -> location = readStringIdentifier(parser)
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

        return Record(
            id,
            name,
            price,
            time,
            location,
            shadowSize,
            imageId,
            availability,
            null,
            null
        )
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    private fun readStringIdentifier(parser: XmlPullParser): Int {
        var resource = context.resources.getIdentifier(readText(parser), "string", "com.dmonzonis.nookpendium")
        if (resource == 0)  // 0 is an invalid resource, it means it was not found
            resource = R.string.unknown
        return resource
    }
}
