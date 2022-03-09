package com.connexin.ice.wrapper

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

fun LocalDate?.toDate(): Date? {
    if(this == null){
        return null
    }
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}