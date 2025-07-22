package com.example.retrotodolistv2.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.retrotodolistv2.R

val RetroFontFamily = FontFamily(Font(R.font.press_start_2p))

val RetroTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = RetroFontFamily,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RetroFontFamily,
        fontSize   = 16.sp,
        lineHeight = 20.sp
    )
) 