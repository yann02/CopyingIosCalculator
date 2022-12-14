package com.yyw.copyingioscalculator.exts

import java.text.NumberFormat
import java.util.*

fun String.numFormatForUS(): String = run {
    if (contains("e", true) || this == "-0") {
        this
    } else {
        val strList = split(".")
        val preDotStr = NumberFormat.getNumberInstance(Locale.US).format(strList[0].toLong())
        if (strList.size > 1) {
            preDotStr + "." + strList[1]
        } else {
            preDotStr
        }
    }
}