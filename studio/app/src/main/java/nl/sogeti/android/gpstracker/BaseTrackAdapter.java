/*------------------------------------------------------------------------------
 **     Ident: Sogeti Smart Mobile Solutions
 **    Author: René de Groot
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
package nl.sogeti.android.gpstracker;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import nl.sogeti.android.gpstracker.integration.ContentConstants;

public class BaseTrackAdapter {

    private Context context;

    public Context getContext() {
        return context;
    }

    public void start(Context context) {
        this.context = context;
    }

    public void stop() {
        context = null;
    }

    public interface ResultHandler {

        void addTrack(String name);

        void addSegment();
        String getWaypointSelection();

        String getWaypointSelectionArgs();
        void addWaypoint(LatLng latLng);
    }

    public void readTrack(Uri trackUri, ResultHandler handler) {
        ContentResolver resolver = context.getContentResolver();
        Cursor track = null;
        try {
            track = resolver.query(trackUri, new String[]{ContentConstants.Tracks.NAME}, null, null, null);
            if (track != null && track.moveToFirst()) {
                String name = track.getString(0);
                handler.addTrack(name);
            }
        } finally {
            close(track);
        }
        long trackId = ContentUris.parseId(trackUri);
        Uri segmentsUri = Uri.withAppendedPath(trackUri, "segments");
        Cursor segmentsCursor = null;
        try {
            segmentsCursor = resolver.query(segmentsUri, new String[]{ContentConstants.Segments._ID}, null, null, null);
            if (segmentsCursor != null && segmentsCursor.moveToFirst()) {
                do {
                    long segmentId = segmentsCursor.getLong(0);
                    handler.addSegment();
                    Uri waypointsUri = ContentConstants.buildUri(trackId, segmentId);
                    Cursor waypointsCursor = null;
                    try {
                        waypointsCursor = resolver.query(waypointsUri, new String[]{ContentConstants.Waypoints.LATITUDE, ContentConstants.Waypoints.LONGITUDE}, null, null, null);
                        if (waypointsCursor != null && waypointsCursor.moveToFirst()) {
                            do {
                                LatLng latLng = new LatLng(waypointsCursor.getDouble(0), waypointsCursor.getDouble(1));
                                handler.addWaypoint(latLng);
                            } while (waypointsCursor.moveToNext());
                        }
                    } finally {
                        close(waypointsCursor);
                    }
                } while (segmentsCursor.moveToNext());
            }
        } finally {
            close(segmentsCursor);
        }
    }

    private static void close(Cursor track) {
        if (track != null) {
            track.close();
        }
    }
}