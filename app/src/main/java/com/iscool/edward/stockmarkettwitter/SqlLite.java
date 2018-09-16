package com.iscool.edward.stockmarkettwitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.iscool.edward.stockmarkettwitter.database.AnswerSchema.AnswerTable;
import com.iscool.edward.stockmarkettwitter.database.PlayerSchema;
import com.iscool.edward.stockmarkettwitter.database.QuizSchema.QuizTable;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema;
import com.iscool.edward.stockmarkettwitter.database.ReadingSchema.ReadingTable;
import com.iscool.edward.stockmarkettwitter.database.TopicNameSchema;
import com.iscool.edward.stockmarkettwitter.database.TopicSchema;
import com.iscool.edward.stockmarkettwitter.database.TopicSchema.TopicTable;
import com.iscool.edward.stockmarkettwitter.database.PlayerSchema.PlayerTable;
import com.iscool.edward.stockmarkettwitter.database.AssetSchema.AssetTable;
import com.iscool.edward.stockmarkettwitter.database.TopicNameSchema.TopicNameTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

//should the methods be static? I think so because only one database exists

public class SqlLite extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database";
    public static final int VERSION = 1;
    public static final String TAG = "com.iscool.edward.stockmarkettwitter.com";
    private static SQLiteDatabase db;
    public static ArrayList<Topic>topicList;

    public SqlLite(Context context) {
        super(context,DATABASE_NAME,null,VERSION);
        db = getWritableDatabase(); //dont call from the main application thread
        //Make sure to call close() when you no longer need the database
    }

    public Cursor allRows(String tableName){
        Cursor cursor = db.query(tableName, null, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor rawQuery(String whereClause,String[]whereArgs){
        Cursor cursor = db.rawQuery(whereClause,whereArgs);
        return cursor;
    }

    public ContentValues setPlayerContentValues(int money){
        ContentValues values = new ContentValues();
        values.put(PlayerTable.Cols.MONEY,money);
        return values;
    }

    public ContentValues setTopicContentValues(String title,UUID id){
        ContentValues values = new ContentValues();
        values.put(TopicTable.Cols.TITLE,title);
        values.put(TopicTable.Cols.ID,id.toString());
        return values;
    }

    public ContentValues setTopicNameContentValues(String company,UUID topicid,UUID id,String ticker){
        ContentValues values = new ContentValues();
        values.put(TopicNameTable.Cols.COMPANY,company);
        values.put(TopicNameTable.Cols.FOREIGNID,topicid.toString());
        values.put(TopicNameTable.Cols.ID,id.toString());
        values.put(TopicNameTable.Cols.TICKER,ticker);
        return values;
    }

    public Cursor queryTopic(String whereClause, String[] whereArgs){
        Cursor cursor = db.query(
                TopicTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor queryTopicName(String whereClause, String[] whereArgs){
        Cursor cursor = db.query(
                TopicNameTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public ContentValues setReadingContentValues(String title, int available, UUID id,int progress,String ticker){
        ContentValues values = new ContentValues();
        values.put(ReadingTable.Cols.TITLE,title);
        values.put(ReadingTable.Cols.AVAILABLE,available);
        values.put(ReadingTable.Cols.UUID,id.toString());
        values.put(ReadingTable.Cols.PROGRESS,progress);
        values.put(ReadingTable.Cols.TICKER,ticker);
        return values;
    }

    public ContentValues setQuizContentValues(String paragraph,float score,UUID readId,UUID id,int available){
        ContentValues values = new ContentValues();
        values.put(QuizTable.Cols.PARAGRAPH, paragraph);
        values.put(QuizTable.Cols.SCORE,score);
        values.put(QuizTable.Cols.FOREIGNID,readId.toString());
        values.put(QuizTable.Cols.UUID,id.toString());
        values.put(QuizTable.Cols.AVAILABLE,available);
        return values;
    }

    public ContentValues setAssetContentValues(int share,String company,String ticker){
        ContentValues values = new ContentValues();
        values.put(AssetTable.Cols.SHARES,share);
        values.put(AssetTable.Cols.COMPANY,company);
        values.put(AssetTable.Cols.TICKER,ticker);
        return values;
    }

    public ContentValues setAnswerContentValues(String answer,UUID quizId){
        ContentValues values = new ContentValues();
        values.put(AnswerTable.Cols.ANSWER,answer);
        values.put(AnswerTable.Cols.FOREIGNID,quizId.toString());
        return values;
    }

    public Cursor queryAsset(String whereClause, String[] whereArgs){
        Cursor cursor = db.query(
                AssetTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor queryAnswer(String whereClause, String[] whereArgs){
        Cursor cursor = db.query(
                AnswerTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor queryReading(String whereClause,String[] whereArgs){
        //gets the 0th location of 2 dimensional database
        Cursor cursor = db.query(
                ReadingTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor queryPlayer(String whereClause,String[] whereArgs){
        //gets the 0th location of 2 dimensional database
        Cursor cursor = db.query(
                PlayerTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    //quizcursor wrapper provides convenience methods
    public QuizCursorWrapper queryQuiz(String whereClause, String[] whereArgs){
        Cursor cursor = db.query(
                QuizTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new QuizCursorWrapper(cursor);
    }

    //gets the first quiz that matches the reading uuid.
    public String getQuizUUID(String readId){
        Cursor cursor = queryQuiz(QuizTable.Cols.FOREIGNID + " = ?",
                new String[]{readId});
        try {
            CursorWrapper cw = new CursorWrapper(cursor);
            cw.moveToFirst();
            //newly created reading not added
            String uuid = cw.getString(cw.getColumnIndex(QuizTable.Cols.UUID));

            return uuid;
        } catch (CursorIndexOutOfBoundsException e){
            Log.d(TAG,"This reading is not in the database yet");
            return null;
        }
        finally {
            cursor.close();
        }
    }

    public String getReadingUUID(String url){
        Cursor cursor = queryReading(ReadingTable.Cols.TITLE + " = ?",
                new String[]{url});
        try {
            CursorWrapper cw = new CursorWrapper(cursor);
            cw.moveToFirst();
            //newly created reading not added
            String uuid = cw.getString(cw.getColumnIndex(ReadingSchema.ReadingTable.Cols.UUID));
            return uuid;
        } catch (CursorIndexOutOfBoundsException e){
            Log.d(TAG,"This reading is not in the database yet");
            return null;
        }
        finally {
            cursor.close();
        }
    }

    public boolean removeRow(String tableName, String field,String whereClause){
        long result = db.delete(tableName,field + " =?",new String[]{whereClause});
        if (result==-1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean updateRow(String tableName, ContentValues cv,String whereClause,String[] whereArgs){
        long result = db.update(tableName,cv,whereClause,whereArgs);
        if (result==-1){
            Log.d(TAG,"update Row operation failed");
            return false;
        }
        else {
            return true;
        }
    }

    public boolean insertRow(String tableName, ContentValues cv){
        long result = db.insert(tableName,null,cv);
        if (result==-1){
            Log.d(TAG,"insert Row operation failed");
            return false;
        }
        else {
            return true;
        }
    }

    public ArrayList<Topic> displayTopics(){
        //shared preferences?
        topicList = new ArrayList<Topic>();
        ArrayList<Reading>company0 = new ArrayList<Reading>();
        company0.add(new Reading("Mattel",UUID.randomUUID(),"MAT"));
        ArrayList<Reading>company1 = new ArrayList<Reading>();
        company1.add(new Reading("Kawasaki_Heavy_Industries",UUID.randomUUID(),"KWHIY"));
        ArrayList<Reading>company2 = new ArrayList<Reading>();
        company2.add(new Reading("SeaWorld",UUID.randomUUID(),"SEAS"));
        ArrayList<Reading>company3 = new ArrayList<Reading>();
        company3.add(new Reading("Pfizer",UUID.randomUUID(),"PFE"));
        ArrayList<Reading>company4 = new ArrayList<Reading>();
        company4.add(new Reading("McDonalds",UUID.randomUUID(),"MCD"));
        ArrayList<Reading>company5 = new ArrayList<Reading>();
        company5.add(new Reading("Samsung",UUID.randomUUID(),"SSNLF"));
        ArrayList<Reading>company6 = new ArrayList<Reading>();
        company6.add(new Reading("Dick's_Sporting_Goods",UUID.randomUUID(),"DKS"));
        ArrayList<Reading>company7 = new ArrayList<Reading>();
        company7.add(new Reading("Blizzard_Entertainment",UUID.randomUUID(),"ATVI"));
        Topic toy = new Topic("Toy",R.drawable.minion,company0);
        Topic robot = new Topic("Robot",R.drawable.robot,company1);
        Topic animal = new Topic("Animal",R.drawable.animal,company2);
        Topic medicine = new Topic("Medicine",R.drawable.medicine,company3);
        Topic food = new Topic("Food",R.drawable.food,company4);
        Topic phone = new Topic("Phone",R.drawable.phone,company5);
        Topic sport = new Topic("Sport",R.drawable.sports,company6);
        Topic game = new Topic("Game",R.drawable.game,company7);
        topicList.addAll(Arrays.asList(toy,robot,animal,medicine,food,phone,sport,game));
        return topicList;
    }

    //will it be created more than once?
    //if so maybe can put in sqlLite's onCreate (only called once)
    public void initTopicTable(ArrayList<Topic>topicList){
        for (int i=0;i<topicList.size();i++) {
            Topic t = topicList.get(i);
            UUID readId = UUID.randomUUID();
            ContentValues cv = setTopicContentValues(t.category, readId);
            insertRow(TopicSchema.TopicTable.NAME,cv);
            for (int j=0;j<t.list.size();j++){
                Reading reading = t.list.get(j);
                ContentValues cv2 = setTopicNameContentValues(reading.title,readId,reading.id,reading.ticker);
                insertRow(TopicNameSchema.TopicNameTable.NAME,cv2);
            }
        }
    }

    public void initPlayerTable(){
        ContentValues cv = setPlayerContentValues(0);
        insertRow(PlayerTable.NAME,cv);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //only called if db has not been created
        //initialize the tables in our database
        //dont need to specify the type
        db.execSQL("CREATE TABLE " + ReadingTable.NAME + "(id integer primary key autoincrement," +
                ReadingTable.Cols.TITLE + ", " +
                ReadingTable.Cols.AVAILABLE +  ", " +
                ReadingTable.Cols.UUID + "," +
                ReadingTable.Cols.PROGRESS + ", " +
                ReadingTable.Cols.TICKER + ")");
        db.execSQL("CREATE TABLE " + QuizTable.NAME + "(id integer primary key autoincrement," +
                QuizTable.Cols.SCORE + ", " +
                QuizTable.Cols.PARAGRAPH + ", " +
                QuizTable.Cols.FOREIGNID + ", " +
                QuizTable.Cols.UUID + ", " +
                QuizTable.Cols.AVAILABLE + ")");
        db.execSQL("CREATE TABLE " + AnswerTable.NAME + "(id integer primary key autoincrement," +
                AnswerTable.Cols.ANSWER + ", " +
                AnswerTable.Cols.FOREIGNID + ")");
        db.execSQL("CREATE TABLE " + TopicTable.NAME + "(" +
                TopicTable.Cols.ID + ", " +
                TopicTable.Cols.TITLE + ")");
        db.execSQL("CREATE TABLE " + PlayerTable.NAME + "(" +
                PlayerTable.Cols.MONEY + ")");
        db.execSQL("CREATE TABLE " + TopicNameTable.NAME + "(" +
                TopicNameTable.Cols.COMPANY + ", " +
                TopicNameTable.Cols.FOREIGNID + ", " +
                TopicNameTable.Cols.ID + ", " +
                TopicNameTable.Cols.TICKER + ")");
        db.execSQL("CREATE TABLE " + AssetTable.NAME + "(" +
                AssetTable.Cols.SHARES + ", " +
                AssetTable.Cols.COMPANY + ", " +
                AssetTable.Cols.TICKER + ")");
        //call db too early? what did we do before to fix this?
        this.db=db;
        ArrayList<Topic> al = displayTopics();
        initTopicTable(al);
        initPlayerTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + QuizTable.NAME);
        onCreate(db);
    }
}
