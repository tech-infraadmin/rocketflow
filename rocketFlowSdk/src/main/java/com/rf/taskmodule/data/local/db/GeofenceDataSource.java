package com.rf.taskmodule.data.local.db;


import com.rf.taskmodule.data.model.GeofenceData;
import com.rf.taskmodule.data.model.GeofenceData;

public interface GeofenceDataSource {

    long addGeoFenceToDB(GeofenceData geofenceData);

    GeofenceData checkIfPendingFormExists(String taskId);

    void updateSyncStatus(int isSynced, String geofenceId, int id, String trackingId);

    void deleteData(String id, String geofenceId);
}
