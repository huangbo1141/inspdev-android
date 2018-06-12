package com.idragonit.inspection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.idragonit.inspection.models.RequestedInspectionInfo;
import com.idragonit.inspection.models.SpinnerInfo;
import com.idragonit.inspection.models.SyncInfo;
import com.idragonit.inspection.utils.Utils;

import java.util.ArrayList;

/**
 * Created by CJH on 2016.01.27.
 */
public class InspectionDatabase {
    private final String TAG = "Database";

    private static InspectionDatabase instance;
    private SQLiteDatabase mDb;

    private final String DB_NAME = "techtoyoullc_v3";
    // upgraded on 2017/03/16
    private final int DB_VERSION = 3;

    private final String TABLE_INSPECTION = "ins_inspection";
    private final String FIELD__INSPECTION_ID = "inspection_id";
    private final String FIELD__EDIT_INSPECTION_ID = "edit_inspection_id";
    private final String FIELD__REQ_INSPECTION_ID = "req_inspection_id";
    private final String FIELD__USER_ID = "user_id";
    private final String FIELD__INSPECTION_TYPE = "inspection_type";
    private final String FIELD__JOB_NUMBER = "job_number";
    private final String FIELD__INSPECTION_DATA = "inspection_data";
    private final String FIELD__INSPECTION_EMAIL = "inspection_email";
    private final String FIELD__INSPECTION_LEFT = "location_left";
    private final String FIELD__INSPECTION_RIGHT = "location_right";
    private final String FIELD__INSPECTION_FRONT = "location_front";
    private final String FIELD__INSPECTION_BACK = "location_back";
    private final String FIELD__INSPECTION_COMMENT = "inspect_comments";
    private final String FIELD__INSPECTION_UNIT = "inspect_unit";
    private final String FIELD__SYNC_DATE = "sync_at";
    private final String FIELD__TEMP = "temp";

    private final String TABLE_REGION = "ins_region";
    private final String FIELD__REGION_ID = "region_id";
    private final String FIELD__REGION_NAME = "region_name";

    private final String TABLE_INSPECTION_REQUESTED = "ins_inspection_requested";
    private final String FIELD__ADDRESS = "address";
    private final String FIELD__COMMUNITY = "community";
    private final String FIELD__DATE = "inspection_date";
    private final String FIELD__CITY = "city";
    private final String FIELD__AREA = "area";
    private final String FIELD__VOLUME = "volume";
    private final String FIELD__QN = "qn";

    private final String TABLE_USER = "ins_admin";
    private final String FIELD__USER_NAME = "username";


    private InspectionDatabase(Context context) {
        // create or open database
        DatabaseHelper helper = new DatabaseHelper(context);
        this.mDb = helper.getWritableDatabase();
    }

    public static InspectionDatabase getInstance(Context context) {
        if (instance == null)
            instance = new InspectionDatabase(context.getApplicationContext());

        return instance;
    }

    public boolean hasData() {
        long count = DatabaseUtils.queryNumEntries(mDb, TABLE_INSPECTION);
//        Log.i(TAG, "hasData>>>" + count);
        return count > 0 ? true : false;
    }

    public boolean deleteInspection(int inspection_id) {
        try {
            if (mDb.delete(TABLE_INSPECTION, FIELD__INSPECTION_ID + "=" + inspection_id, null) > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertInspection(int user_id, int requested_inspection_id, int edit_inspection_id, int type, String job_number, String data, String email, String left, String right, String front, String back, String comments, String unit) {
        try {
            ContentValues values = new ContentValues();

            values.put(FIELD__USER_ID, user_id);
            values.put(FIELD__REQ_INSPECTION_ID, requested_inspection_id);
            values.put(FIELD__EDIT_INSPECTION_ID, edit_inspection_id);
            values.put(FIELD__INSPECTION_TYPE, type);
            values.put(FIELD__JOB_NUMBER, job_number);
            values.put(FIELD__INSPECTION_DATA, data);
            values.put(FIELD__INSPECTION_EMAIL, email);
            values.put(FIELD__INSPECTION_LEFT, left);
            values.put(FIELD__INSPECTION_RIGHT, right);
            values.put(FIELD__INSPECTION_FRONT, front);
            values.put(FIELD__INSPECTION_BACK, back);
            values.put(FIELD__INSPECTION_COMMENT, comments);
            values.put(FIELD__INSPECTION_UNIT, unit);
            values.put(FIELD__SYNC_DATE, Utils.getTodayWithTime());

            int inspection_id = (int) mDb.insert(TABLE_INSPECTION, "NULL", values);
            if (inspection_id == -1)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<SyncInfo> loadInspection(int user_id) {
        ArrayList<SyncInfo> result = new ArrayList<SyncInfo>();

        try {
            Cursor cursor;

            String whereClause = FIELD__USER_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(user_id)};
            String[] select = new String[]{FIELD__INSPECTION_ID, FIELD__INSPECTION_TYPE, FIELD__JOB_NUMBER,
                        FIELD__INSPECTION_DATA, FIELD__INSPECTION_EMAIL,
                        FIELD__INSPECTION_LEFT, FIELD__INSPECTION_RIGHT, FIELD__INSPECTION_FRONT, FIELD__INSPECTION_BACK,
                        FIELD__INSPECTION_COMMENT, FIELD__SYNC_DATE, FIELD__REQ_INSPECTION_ID, FIELD__INSPECTION_UNIT,
                        FIELD__EDIT_INSPECTION_ID
            };

            cursor = mDb.query(TABLE_INSPECTION, select, whereClause, whereArgs, null, null, FIELD__SYNC_DATE, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    SyncInfo item = new SyncInfo();

                    item.inspection_id = Utils.checkNull(cursor.getInt(0), 0);
                    item.type = Utils.checkNull(cursor.getInt(1), 0);
                    item.job_number = Utils.checkNull(cursor.getString(2));
                    item.data = Utils.checkNull(cursor.getString(3));
                    item.email = Utils.checkNull(cursor.getString(4));
                    item.left = Utils.checkNull(cursor.getString(5));
                    item.right = Utils.checkNull(cursor.getString(6));
                    item.front = Utils.checkNull(cursor.getString(7));
                    item.back = Utils.checkNull(cursor.getString(8));
                    item.comment = Utils.checkNull(cursor.getString(9));
                    item.date = Utils.checkNull(cursor.getString(10));
                    item.requested_inspection_id = Utils.checkNull(cursor.getInt(11), 0);
                    item.unit = Utils.checkNull(cursor.getString(12));
                    item.edit_inspection_id= Utils.checkNull(cursor.getInt(13), 0);

                    result.add(item);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public SyncInfo getInspection(int inspection_id) {
        SyncInfo item = null;

        try {
            Cursor cursor;

            String whereClause = FIELD__REQ_INSPECTION_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(inspection_id)};
            String[] select = new String[]{
                    FIELD__INSPECTION_ID, FIELD__INSPECTION_TYPE, FIELD__JOB_NUMBER,
                    FIELD__INSPECTION_DATA, FIELD__INSPECTION_EMAIL, FIELD__INSPECTION_LEFT, FIELD__INSPECTION_RIGHT, FIELD__INSPECTION_FRONT, FIELD__INSPECTION_BACK,
                    FIELD__INSPECTION_COMMENT, FIELD__SYNC_DATE,
                    FIELD__REQ_INSPECTION_ID, FIELD__INSPECTION_UNIT,
                    FIELD__EDIT_INSPECTION_ID
            };

            cursor = mDb.query(TABLE_INSPECTION, select, whereClause, whereArgs, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                item = new SyncInfo();

                item.inspection_id = Utils.checkNull(cursor.getInt(0), 0);
                item.type = Utils.checkNull(cursor.getInt(1), 0);
                item.job_number = Utils.checkNull(cursor.getString(2));
                item.data = Utils.checkNull(cursor.getString(3));
                item.email = Utils.checkNull(cursor.getString(4));
                item.left = Utils.checkNull(cursor.getString(5));
                item.right = Utils.checkNull(cursor.getString(6));
                item.front = Utils.checkNull(cursor.getString(7));
                item.back = Utils.checkNull(cursor.getString(8));
                item.comment = Utils.checkNull(cursor.getString(9));
                item.date = Utils.checkNull(cursor.getString(10));
                item.requested_inspection_id = Utils.checkNull(cursor.getInt(11), 0);
                item.unit = Utils.checkNull(cursor.getString(12));
                item.edit_inspection_id= Utils.checkNull(cursor.getInt(13), 0);
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public ArrayList<RequestedInspectionInfo> loadRequestedInspection() {
        ArrayList<RequestedInspectionInfo> result = new ArrayList<RequestedInspectionInfo>();

        try {
            Cursor cursor;

            String[] select = new String[]{
                    FIELD__REQ_INSPECTION_ID, FIELD__INSPECTION_TYPE, FIELD__JOB_NUMBER, FIELD__DATE, FIELD__SYNC_DATE,
                    FIELD__TEMP
            };

            cursor = mDb.query(TABLE_INSPECTION_REQUESTED, select, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    RequestedInspectionInfo item = new RequestedInspectionInfo();

                    item.id = Utils.checkNull(cursor.getInt(0), 0);
                    item.type = Utils.checkNull(cursor.getInt(1), 0);
                    item.job_number = Utils.checkNull(cursor.getString(2));
                    item.inspection_date = Utils.checkNull(cursor.getString(3));

                    item.fromTemp(Utils.checkNull(cursor.getString(5)));

                    if (item.type==Constants.INSPECTION_WCI || item.type==Constants.INSPECTION_PULTE_DUCT) {
                    } else {
                        item.community = item.job_number.substring(0, 4);
                        item.lot = item.job_number.substring(5, 7);
                    }

                    result.add(item);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public RequestedInspectionInfo getRequestedInspection(int inspection_id) {
        try {
            Cursor cursor;

            String[] select = new String[]{
                    FIELD__REQ_INSPECTION_ID, FIELD__INSPECTION_TYPE, FIELD__JOB_NUMBER, FIELD__DATE, FIELD__SYNC_DATE,
                    FIELD__TEMP
            };
            String whereClause = FIELD__REQ_INSPECTION_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(inspection_id)};

            RequestedInspectionInfo item = new RequestedInspectionInfo();

            cursor = mDb.query(TABLE_INSPECTION_REQUESTED, select, whereClause, whereArgs, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                item.id = Utils.checkNull(cursor.getInt(0), 0);
                item.type = Utils.checkNull(cursor.getInt(1), 0);
                item.job_number = Utils.checkNull(cursor.getString(2));
                item.inspection_date = Utils.checkNull(cursor.getString(3));

                item.fromTemp(Utils.checkNull(cursor.getString(5)));

                if (item.type==Constants.INSPECTION_WCI || item.type==Constants.INSPECTION_PULTE_DUCT) {
                } else {
                    item.community = item.job_number.substring(0, 4);
                    item.lot = item.job_number.substring(5, 7);
                }
            }

            cursor.close();

            return item;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertRequestedInspection(int inspection_id, int type, String job_number, String date, String sync, String temp) {
        try {
            ContentValues values = new ContentValues();

            values.put(FIELD__REQ_INSPECTION_ID, inspection_id);
            values.put(FIELD__INSPECTION_TYPE, type);
            values.put(FIELD__JOB_NUMBER, job_number);
            values.put(FIELD__DATE, date);
            values.put(FIELD__SYNC_DATE, sync);
            values.put(FIELD__TEMP, temp);

            int new_id = (int) mDb.insert(TABLE_INSPECTION_REQUESTED, "NULL", values);
            if (new_id == -1)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteRequestedInspection() {
        try {
            if (mDb.delete(TABLE_INSPECTION_REQUESTED, null, null) > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteRequestedInspection(int inspection_id) {
        try {
            if (mDb.delete(TABLE_INSPECTION_REQUESTED, FIELD__REQ_INSPECTION_ID + "=" + inspection_id, null) > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public ArrayList<SpinnerInfo> loadRegion() {
        ArrayList<SpinnerInfo> result = new ArrayList<SpinnerInfo>();

        try {
            Cursor cursor;

            String[] select = new String[]{FIELD__REGION_ID, FIELD__REGION_NAME };

            cursor = mDb.query(TABLE_REGION, select, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    SpinnerInfo item = new SpinnerInfo();

                    item.id = Utils.checkNull(cursor.getInt(0), 0);
                    item.name = Utils.checkNull(cursor.getString(1));

                    result.add(item);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isExistRegion(int region_id) {
        boolean exist = false;
        try {
            Cursor cursor;

            String[] select = new String[]{FIELD__REGION_ID, FIELD__REGION_NAME };
            String whereClause = FIELD__REGION_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(region_id)};

            cursor = mDb.query(TABLE_REGION, select, whereClause, whereArgs, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                exist = true;
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exist;
    }

    public boolean insertRegion(int id, String region) {
        try {
            ContentValues values = new ContentValues();

            values.put(FIELD__REGION_ID, id);
            values.put(FIELD__REGION_NAME, region);

            int new_id = (int) mDb.insert(TABLE_REGION, "NULL", values);
            if (new_id == -1)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateRegion(int id, String region) {

        try {
            ContentValues values = new ContentValues();

//            values.put(FIELD__REGION_ID, id);
            values.put(FIELD__REGION_NAME, region);

            if (mDb.update(TABLE_REGION, values, FIELD__REGION_ID+"="+id, null)<0)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteRegion() {
        try {
            if (mDb.delete(TABLE_REGION, null, null) > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteRegion(int region_id) {
        try {
            if (mDb.delete(TABLE_REGION, FIELD__REGION_ID + "=" + region_id, null) > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public ArrayList<SpinnerInfo> loadFieldManager() {
        ArrayList<SpinnerInfo> result = new ArrayList<SpinnerInfo>();

        try {
            Cursor cursor;

            String[] select = new String[]{FIELD__USER_ID, FIELD__SYNC_DATE };

            cursor = mDb.query(TABLE_USER, select, null, null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    SpinnerInfo item = new SpinnerInfo();

                    item.id = Utils.checkNull(cursor.getInt(0), 0);
                    item.name = Utils.checkNull(cursor.getString(1));

                    result.add(item);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<SpinnerInfo> loadFieldManager(int region) {
        ArrayList<SpinnerInfo> result = new ArrayList<>();

        try {
            Cursor cursor;

            String[] select = new String[]{FIELD__USER_ID, FIELD__USER_NAME };
            String whereClause = FIELD__REGION_ID + " like '%r"+region+"r%' ";

            cursor = mDb.query(TABLE_USER, select, whereClause, null, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                do {
                    SpinnerInfo item = new SpinnerInfo();

                    item.id = Utils.checkNull(cursor.getInt(0), 0);
                    item.name = Utils.checkNull(cursor.getString(1));

                    result.add(item);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isExistFieldManager(int manager_id) {
        boolean exist = false;
        try {
            Cursor cursor;

            String[] select = new String[]{FIELD__USER_ID, FIELD__USER_NAME };
            String whereClause = FIELD__USER_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(manager_id)};

            cursor = mDb.query(TABLE_USER, select, whereClause, whereArgs, null, null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                exist = true;
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exist;
    }

    public boolean insertFieldManager(int manager_id, String region, String username, String sync) {
        try {
            ContentValues values = new ContentValues();

            values.put(FIELD__USER_ID, manager_id);
            values.put(FIELD__REGION_ID, region);
            values.put(FIELD__USER_NAME, username);
            values.put(FIELD__SYNC_DATE, sync);

            int new_id = (int) mDb.insert(TABLE_USER, "NULL", values);
            if (new_id == -1)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateFieldManager(int manager_id, String region, String username, String sync) {

        try {
            ContentValues values = new ContentValues();

            values.put(FIELD__REGION_ID, region);
            values.put(FIELD__USER_NAME, username);
            values.put(FIELD__SYNC_DATE, sync);

            if (mDb.update(TABLE_USER, values, FIELD__USER_ID+"="+manager_id, null)<0)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteFieldManager(int manager_id) {
        try {
            if (mDb.delete(TABLE_USER, FIELD__USER_ID + "=" + manager_id, null) > 0)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }




    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                String sql = " " + " CREATE TABLE "
                        + TABLE_INSPECTION + " ( "
                        + FIELD__INSPECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                        + FIELD__REQ_INSPECTION_ID + " INTEGER NOT NULL, "
                        + FIELD__EDIT_INSPECTION_ID + " INTEGER NOT NULL DEFAULT 0, "
                        + FIELD__USER_ID + " INTEGER NOT NULL, "
                        + FIELD__INSPECTION_TYPE + " INTEGER NOT NULL, "
                        + FIELD__JOB_NUMBER + " TEXT, "
                        + FIELD__INSPECTION_DATA + " TEXT, "
                        + FIELD__INSPECTION_EMAIL + " TEXT, "
                        + FIELD__INSPECTION_LEFT + " TEXT, "
                        + FIELD__INSPECTION_RIGHT + " TEXT, "
                        + FIELD__INSPECTION_FRONT + " TEXT, "
                        + FIELD__INSPECTION_BACK + " TEXT, "
                        + FIELD__INSPECTION_COMMENT + " TEXT, "
                        + FIELD__INSPECTION_UNIT + " TEXT, "
                        + FIELD__SYNC_DATE + " CHAR(14) NOT NULL "
                        + " ); ";

                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String sql = " " + " CREATE TABLE "
                        + TABLE_REGION + " ( "
                        + FIELD__REGION_ID + " INTEGER PRIMARY KEY NOT NULL, "
                        + FIELD__REGION_NAME + " TEXT, "
                        + FIELD__TEMP + " TEXT "
                        + " ); ";

                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String sql = " " + " CREATE TABLE "
                        + TABLE_INSPECTION_REQUESTED + " ( "
                        + FIELD__REQ_INSPECTION_ID + " INTEGER PRIMARY KEY NOT NULL, "
                        + FIELD__INSPECTION_TYPE + " INTEGER NOT NULL, "
                        + FIELD__JOB_NUMBER + " TEXT, "
                        + FIELD__DATE + " TEXT, "
                        + FIELD__SYNC_DATE + " CHAR(14), "
                        + FIELD__TEMP + " TEXT "
                        + " ); ";

                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String sql = " " + " CREATE TABLE "
                        + TABLE_USER + " ( "
                        + FIELD__USER_ID + " INTEGER PRIMARY KEY NOT NULL, "
                        + FIELD__REGION_ID + " TEXT, "
                        + FIELD__USER_NAME + " TEXT, "
                        + FIELD__SYNC_DATE + " CHAR(14), "
                        + FIELD__TEMP + " TEXT "
                        + " ); ";

                db.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading Database " + oldVersion + ".0 to " + newVersion + ".0");

            if (oldVersion==1 || oldVersion==2) {
                try {
                    String sql = " " + " ALTER TABLE "
                            + TABLE_INSPECTION + " "
                            + " ADD COLUMN " + FIELD__EDIT_INSPECTION_ID + " INTEGER NOT NULL DEFAULT 0 "
                            + " ; ";

                    db.execSQL(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (oldVersion==2) {
                try {
                    String sql = " " + " DROP TABLE IF EXIST "
                            + TABLE_USER + " "
                            + " ; ";

                    db.execSQL(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    String sql = " " + " CREATE TABLE "
                            + TABLE_USER + " ( "
                            + FIELD__USER_ID + " INTEGER PRIMARY KEY NOT NULL, "
                            + FIELD__REGION_ID + " TEXT, "
                            + FIELD__USER_NAME + " TEXT, "
                            + FIELD__SYNC_DATE + " CHAR(14), "
                            + FIELD__TEMP + " TEXT "
                            + " ); ";

                    db.execSQL(sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            SQLiteDatabase db;

            try {
                return super.getWritableDatabase();
            } catch (Exception e) {
                try {
                    db = SQLiteDatabase.openOrCreateDatabase(InspectionApplication.getAppContext().getDatabasePath(DB_NAME), null);
                } catch (SQLiteException e2) {
                    Log.w(TAG, "SQLite database could not be created! Media library cannot be saved.");
                    db = SQLiteDatabase.create(null);
                }
            }

            int version = db.getVersion();
            if (version != DB_VERSION) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        onUpgrade(db, version, DB_VERSION);
                    }

                    db.setVersion(DB_VERSION);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            return db;
        }
    }

}
