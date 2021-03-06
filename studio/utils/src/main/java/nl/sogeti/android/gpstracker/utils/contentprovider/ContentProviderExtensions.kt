/*------------------------------------------------------------------------------
 **     Ident: Sogeti Smart Mobile Solutions
 **    Author: rene
 ** Copyright: (c) 2016 Sogeti Nederland B.V. All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Sogeti Nederland B.V.            |  No part of this file may be reproduced
 ** Distributed Software Engineering |  or transmitted in any form or by any
 ** Lange Dreef 17                   |  means, electronic or mechanical, for the
 ** 4131 NJ Vianen                   |  purpose, without the express written
 ** The Netherlands                  |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 *
 *   This file is part of OpenGPSTracker.
 *
 *   OpenGPSTracker is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OpenGPSTracker is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OpenGPSTracker.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package nl.sogeti.android.gpstracker.utils.contentprovider

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import timber.log.Timber

/* **
 * Extensions for dealing with the data in content providers
 * **/

/**
 * Read a single String value from a cursor using the columns name
 *
 * @param columnName name of the column
 *
 * @return null if the column doesn't exist or stores a null value
 */
fun Cursor.getString(columnName: String): String? {
    return this.applyGetter(columnName, Cursor::getString)
}

/**
 * Read a single Long value from a cursor using the columns name
 *
 * @param columnName name of the column
 *
 * @return null if the column doesn't exist or stores a null value
 */
fun Cursor.getLong(columnName: String): Long? {
    return this.applyGetter(columnName, Cursor::getLong)
}

/**
 * Read a single Double value from a cursor using the columns name
 *
 * @param columnName name of the column
 *
 * @return null if the column doesn't exist or stores a null value
 */
fun Cursor.getDouble(columnName: String): Double? {
    return this.applyGetter(columnName, Cursor::getDouble)
}

/**
 * Read a single Int value from a cursor using the columns name
 *
 * @param columnName name of the column
 *
 * @return null if the column doesn't exist or stores a null value
 */
fun Cursor.getInt(columnName: String): Int? {
    return this.applyGetter(columnName, Cursor::getInt)
}

private fun <T> Cursor.applyGetter(columnName: String, getter: (Cursor, Int) -> T): T? {
    val index = this.getColumnIndex(columnName)
    val value: T?
    if (index >= 0) {
        value = getter(this, index)
    } else {
        value = null
    }

    return value
}

/**
 * Apply a single operation to a cursor on the all items in a Uri
 * to build a list
 *
 * @param operation the operation to execute
 * @param projection optional projection
 * @param selection optional selection, runQuery string with parameter arguments listed
 *
 * @return List of T consisting of operation results, empty when there are no rows
 */
fun <T> Uri.map(resolver: ContentResolver,
                selection: Pair<String, List<String>>? = null,
                projection: List<String>? = null,
                operation: (Cursor) -> T): List<T> {
    val result = mutableListOf<T>()

    this.runQuery(resolver, selection, projection) {
        do {
            result.add(operation(it))
        } while (it.moveToNext())
    }

    return result
}

/**
 * Apply a single operation to a cursor on the first row of a Uri
 *
 * @param operation the operation to execute
 * @param projection optional projection
 * @param selectionPair optional selection
 *
 * @return Null or T when the operation was applied to the first row of the cursor
 */
fun <T> Uri.runQuery(resolver: ContentResolver,
                     selectionPair: Pair<String, List<String>>? = null,
                     projection: List<String>? = null,
                     operation: (Cursor) -> T): T? {
    val selectionArgs = selectionPair?.second?.toTypedArray()
    val selection = selectionPair?.first
    var result: T? = null
    var cursor: Cursor? = null
    try {
        cursor = resolver.query(this, projection?.toTypedArray(), selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            result = operation(cursor)
        }
    } finally {
        cursor?.close()
    }

    return result
}

fun Uri.append(path: String): Uri {
    return Uri.withAppendedPath(this, path)
}

fun Uri.append(id: Long): Uri {
    return ContentUris.withAppendedId(this, id)
}

fun Uri.count(resolver: ContentResolver,
              selectionPair: Pair<String, List<String>>? = null): Int {
    val selectionArgs = selectionPair?.second?.toTypedArray()
    val selection = selectionPair?.first
    var result = 0
    var cursor: Cursor? = null
    try {
        val projection = arrayOf("count(*) AS count")
        cursor = resolver.query(this, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getInt(0)
        } else {
            Timber.w("Uri $this count operation didn't have results")
        }
    } finally {
        cursor?.close()
    }

    return result
}

fun Uri.countResult(resolver: ContentResolver,
                    projection: Array<String> = arrayOf(BaseColumns._ID),
                    selectionPair: Pair<String, List<String>>? = null): Int {
    val selectionArgs = selectionPair?.second?.toTypedArray()
    val selection = selectionPair?.first
    var result = 0
    var cursor: Cursor? = null
    try {
        cursor = resolver.query(this, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.count
        } else {
            Timber.w("Uri $this countResult operation didn't have results")
        }
    } finally {
        cursor?.close()
    }

    return result
}
