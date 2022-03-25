package com.connexin.ice.wrapper

import com.connexin.ice.wrapper.model.CodeSystem
import com.connexin.ice.wrapper.model.Interpretation
import com.connexin.ice.wrapper.model.ObservationConcept

const val OID_CVX = "2.16.840.1.113883.12.292"
const val OID_ICE_VACCINE_GROUP = "2.16.840.1.113883.3.795.12.100.1"
const val OID_ICE_VALIDITY = "2.16.840.1.113883.3.795.12.100.2"
const val OID_ICD_9 = "2.16.840.1.113883.6.103"
const val OID_ICD_10 = "2.16.840.1.113883.6.90"
const val OID_SNOMED = "2.16.840.1.113883.6.5"
const val OID_DISEASE_DOCUMENTED = "2.16.840.1.113883.3.795.12.100.8"
const val OID_IS_IMMUNE = "2.16.840.1.113883.3.795.12.100.9"

object Lookups {

    fun codes(system: CodeSystem): String {
        return when (system) {
            CodeSystem.ICD_9 -> OID_ICD_9
            CodeSystem.ICD_10 -> OID_ICD_10
            CodeSystem.SNOMED -> OID_SNOMED
            CodeSystem.CVX -> OID_CVX
            CodeSystem.VACCINE_GROUP -> OID_ICE_VACCINE_GROUP
            CodeSystem.ICE_VACCINE_VALIDITY -> OID_ICE_VALIDITY
        }
    }

    fun codes(concept: ObservationConcept):String{
        return when(concept){
            ObservationConcept.DISEASE_DOCUMENTED -> OID_DISEASE_DOCUMENTED
        }
    }

    fun codes(concept: Interpretation):String{
        return when(concept){
            Interpretation.IS_IMMUNE -> OID_IS_IMMUNE
            Interpretation.REFUSED -> OID_IS_IMMUNE
            Interpretation.DISEASE -> OID_IS_IMMUNE
        }
    }

    fun codeSystem(oid:String): CodeSystem?{
        return when(oid){
            OID_ICD_9 -> CodeSystem.ICD_9
            OID_ICD_10 -> CodeSystem.ICD_10
            OID_SNOMED -> CodeSystem.SNOMED
            OID_CVX -> CodeSystem.CVX
            OID_ICE_VACCINE_GROUP -> CodeSystem.VACCINE_GROUP
            OID_ICE_VALIDITY -> CodeSystem.ICE_VACCINE_VALIDITY
            else -> return null
        }
    }
}