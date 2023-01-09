package com.rf.taskmodule.ui.geofencing;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.rf.taskmodule.R;
import com.rf.taskmodule.data.local.db.DatabaseHelper;
import com.rf.taskmodule.data.local.db.GeofenceDataSource;
import com.rf.taskmodule.data.local.prefs.PreferencesHelper;
import com.rf.taskmodule.data.model.GeofenceData;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.utils.Log;
import com.rf.taskmodule.data.local.db.DatabaseHelper;
import com.rf.taskmodule.data.local.db.GeofenceDataSource;
import com.rf.taskmodule.data.model.GeofenceData;
import com.rf.taskmodule.data.model.response.config.Task;
import com.rf.taskmodule.utils.Log;
import com.trackthat.lib.TrackThat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Listener for geofence transition changes.
 * <p>
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 573;

    private static final String TAG = "GeofenceTransitionsIS";

    private static final String CHANNEL_ID = "channel_01";

    @Inject
    PreferencesHelper preferencesHelper;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleWork(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        GeofenceDataSource geofenceDataSource = DatabaseHelper.getInstance(getApplicationContext());
        GeofenceData.Builder geofenceBuilder = new GeofenceData.Builder();
        Task task = preferencesHelper.getCurrentTask();
        if (task != null) {
            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

                // Get the geofences that were triggered. A single event can trigger multiple geofences.
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

                // Get the transition details as a String.
                String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                        triggeringGeofences);

                String geofenceId;
                if (triggeringGeofences.size() > 1) {
                    geofenceId = triggeringGeofences.get(triggeringGeofences.size() - 1).getRequestId();
                } else {
                    geofenceId = triggeringGeofences.get(0).getRequestId();
                }

                geofenceBuilder.setIsSync(0);
                geofenceBuilder.setIsFormFilled(0);
                geofenceBuilder.setIsFormSubmitted(0);
                geofenceBuilder.setTrackingId(TrackThat.getCurrentTrackingId());
                geofenceBuilder.setGeofenceId(geofenceId);
                geofenceBuilder.setTaskId(task.getTaskId());

                //add geofence data to the database.
                long addedAt = geofenceDataSource.addGeoFenceToDB(geofenceBuilder.build());
                Log.i(TAG, "geofence data added at:--> " + addedAt + " with geofence id & tracking id:---> "
                        + geofenceId + " <---> " + TrackThat.getCurrentTrackingId());

                // Send notification and log the transition details.
                Log.i(TAG, geofenceTransitionDetails);

            } else {
                // Log the error.
                Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
            }
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition  The ID of the geofence transition.
     * @param triggeringGeofences The geofence(s) triggered.
     * @return The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the SplashActivity.
     */

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
