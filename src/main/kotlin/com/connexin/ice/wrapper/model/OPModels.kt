package com.connexin.ice.wrapper.model

import java.time.LocalDate


data class Vaccine(val id:String,var cvx:String,val name:String,val date:LocalDate)
data class Indicator(
    val id: String? = null,
    val name: String? = null,
    val code: String,
    val system: CodeSystem,
    val interpretation: Interpretation,
    val date: LocalDate,
    val deferredUntil: LocalDate? = null
)

data class VaccineReport(val gender:Gender,
                         val dateOfBirth: LocalDate,
                         val requestTime: LocalDate,
                         val flags:Map<String,Boolean>?=mapOf(),
                         val vaccines : List<Vaccine>,
                         val indicators:List<Indicator>)