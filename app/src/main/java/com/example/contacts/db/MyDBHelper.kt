import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.contacts.db.MyDBNameClass

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, MyDBNameClass.DATABASE_NAME, null, MyDBNameClass.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(MyDBNameClass.SQL_CREATE_CONTACTS)
        db.execSQL(MyDBNameClass.SQL_CREATE_TAGS)
        db.execSQL(MyDBNameClass.SQL_CREATE_PREV_CALLS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(MyDBNameClass.SQL_DELETE_CONTACTS)
        db.execSQL(MyDBNameClass.SQL_DELETE_TAGS)
        db.execSQL(MyDBNameClass.SQL_DELETE_PREV_CALLS)
        onCreate(db)
    }
}
