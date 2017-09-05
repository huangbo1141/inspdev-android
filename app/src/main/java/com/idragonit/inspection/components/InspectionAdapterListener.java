package com.idragonit.inspection.components;

import com.idragonit.inspection.models.RequestedInspectionInfo;
import com.idragonit.inspection.models.SyncInfo;

/**
 * Created by CJH on 2016.01.28.
 */
public interface InspectionAdapterListener {
    void onSubmit(SyncInfo item);
    void onDelete(SyncInfo item);

    void onSubmit(RequestedInspectionInfo item);
}
