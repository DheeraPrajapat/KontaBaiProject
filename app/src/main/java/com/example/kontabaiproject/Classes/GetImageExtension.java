package com.example.kontabaiproject.Classes;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

 public class GetImageExtension
{
    public static String getExtension(Context context, Uri uri){
        ContentResolver contentResolver= context.getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
