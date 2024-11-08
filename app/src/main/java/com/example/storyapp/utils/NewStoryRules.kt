package com.example.storyapp.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

abstract class NewStoryRules : ActivityResultContract<Intent, Int>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }
}