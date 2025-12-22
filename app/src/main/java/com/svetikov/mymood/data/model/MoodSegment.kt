package com.svetikov.mymood.data.model

import androidx.compose.ui.graphics.Color


data class MoodSegment(
    val percentage:Float,
    val color: Color,
    val label:String
)
