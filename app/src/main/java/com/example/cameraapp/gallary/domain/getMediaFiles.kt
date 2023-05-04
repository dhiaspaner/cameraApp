package com.example.cameraapp.gallary.domain

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore


fun getMedia(context: Context):List<Uri> {
    val projection = arrayOf(
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns._ID,
    )
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}" +
            " OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}"
    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
    val uri = MediaStore.Files.getContentUri("external")

    val mediaList = mutableListOf< Uri>()
    context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val contentUri = ContentUris.withAppendedId(uri, id)
            mediaList.add(contentUri)
        }
    }

    return mediaList
}