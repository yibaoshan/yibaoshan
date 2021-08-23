package com.android.blackboard

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri


class TestContentProvider : ContentProvider() {

    private val mDatabase: SQLiteDatabase? = null
    private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private val PROVIDE_COURSE = 1
    private val COURSE_TABLE_NAME = "test_table"


    override fun onCreate(): Boolean {
        /*除onCreate()方法外，其他方法都运行在binder线程池？*/
        mUriMatcher.addURI(CourseProviders.AUTHORITIES, CourseProviders.COURSE_PATH, PROVIDE_COURSE);
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        when (mUriMatcher.match(uri)) {
            PROVIDE_COURSE -> return mDatabase?.query(COURSE_TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder)
            else -> {
            }
        }
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        var result: Uri? = null
        when (mUriMatcher.match(uri)) {
            PROVIDE_COURSE -> {
                val rowId = mDatabase?.insert(
                    COURSE_TABLE_NAME, null, values)
                result = rowId?.let { ContentUris.withAppendedId(uri, it) }
            }
            else -> {
            }
        }

        return result
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        when (mUriMatcher.match(uri)) {
            PROVIDE_COURSE -> return mDatabase!!.delete(COURSE_TABLE_NAME,
                selection, selectionArgs)
            else -> {
            }
        }

        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        when (mUriMatcher.match(uri)) {
            PROVIDE_COURSE -> return mDatabase!!.update(COURSE_TABLE_NAME,
                values, selection, selectionArgs)
            else -> {
            }
        }
        return 0
    }

    object CourseProviders {
        const val AUTHORITIES = "com.android.blackboard"
        const val COURSE_PATH = "course"
        val BASE_URI = Uri.parse("content://" + AUTHORITIES)
        val COURSE_URI = Uri.withAppendedPath(BASE_URI, COURSE_PATH)

        object CourseColumn {
            const val ID = "id"
            const val NAME = "NAME"
            const val TEACHER_NAME = "teacher_name"
            const val WATCH_COUNT = "watch_count"
            const val VIDEO_URL = "video_url"
        }
    }
}