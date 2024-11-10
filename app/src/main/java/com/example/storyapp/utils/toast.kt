package com.example.storyapp.utils

import android.content.Context
import android.widget.Toast

fun ShowToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}