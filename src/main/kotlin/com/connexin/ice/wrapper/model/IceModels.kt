package com.connexin.ice.wrapper.model

import com.connexin.ice.wrapper.Lookups
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


data class VMR(val demographics: VMRDemographic, val obs: List<VMRObservationResult>, val imms:List<VMRImmunizationEvent>){
     fun toXML(prettyPrint: Boolean = false):String{
         val options = if(prettyPrint){
             PrintOptions(
                 pretty = true,
                 singleLineTextElements = true,
                 useSelfClosingTags = true, useCharacterReference = false, indent = "\t")
         }else{
             PrintOptions(
                 pretty = false,
                 singleLineTextElements = true,
                 useSelfClosingTags = true, useCharacterReference = false, indent = "\t")
         }
         return xml("ns3:cdsInput"){
             attribute("xmlns:ns2","org.opencds.vmr.v1_0.schema.cdsinput.specification")
             attribute("xmlns:ns3","org.opencds.vmr.v1_0.schema.cdsinput")
             attribute("xmlns:ns4","org.opencds.vmr.v1_0.schema.cdsoutput")
             attribute("xmlns:ns5","org.opencds.vmr.v1_0.schema.vmr")
             "templateId" {
                 attribute("root","2.16.840.1.113883.3.795.11.1.1")
             }
             "cdsContext"{
                 "cdsSystemUserPreferredLanguage" {
                     attribute("code","en")
                     attribute("codeSystem","2.16.840.1.113883.6.99")
                     attribute("displayName","English")
                 }
             }
             "vmrInput" {
                 "templateId" {
                     attribute("root", "2.16.840.1.113883.3.795.11.1.1")
                 }
                 "patient"{
                     "templateId" {
                         attribute("root", "2.16.840.1.113883.3.795.11.2.1.1")
                     }
                     "id" {
                         attribute("root","2.16.840.1.113883.3.795.12.100.11")
                         attribute("extension","43299551")
                     }
                     "demographics" {
                         "birthTime"{
                             attribute("value", safeFormat(demographics.birthTime))
                         }
                         "gender" {
                             attribute("code",demographics.gender)
                             attribute("codeSystem","2.16.840.1.113883.5.1")
                         }
                     }
                     "clinicalStatements"{
                         if(obs.isNotEmpty()) {
                             "observationResults"{
                                 for (ob in obs) {
                                     "observationResult" {
                                         "templateId" {
                                             attribute("root", "2.16.840.1.113883.3.795.11.6.3.1")
                                         }
                                         "id" {
                                             attribute("root", UUID.randomUUID().toString())//"2.16.840.1.113883.3.795.12.100.17")
                                             /*attribute("extension", "427")*/
                                         }
                                         "observationFocus" {
                                             attribute("code", ob.code)
                                             attribute("codeSystem", Lookups.codes(ob.system))
                                         }
                                         "observationEventTime"{
                                             attribute("high", safeFormat(ob.localDate))
                                             attribute("low", safeFormat(ob.localDate))
                                         }
                                         "observationValue"{
                                             "concept"{
                                                 attribute("code",ob.concept.name)
                                                 attribute("codeSystem", Lookups.codes(ob.concept))
                                             }
                                         }
                                         "interpretation"{
                                             attribute("code","IS_IMMUNE")
                                             attribute("codeSystem", Lookups.codes(ob.interpretation))
                                         }

                                     }
                                 }
                             }
                         }
                         if(imms.isNotEmpty()){
                             "substanceAdministrationEvents"{
                                 for(imm in imms){
                                     "substanceAdministrationEvent" {
                                         "templateId" {
                                             attribute("root", "2.16.840.1.113883.3.795.11.9.1.1")
                                         }
                                         "id" {
                                             attribute("root", imm.vacId.toString())
                                         }
                                         "substanceAdministrationGeneralPurpose"{
                                             attribute("code","384810002")
                                             attribute("codeSystem","2.16.840.1.113883.6.5")
                                         }
                                         "substance" {
                                             "id" {
                                                 attribute( "root","8c6a750d-12ea-4a71-beac-648c9ffc3913")
                                             }
                                             "substanceCode" {
                                                 attribute( "code",imm.value)
                                                 attribute( "displayName",imm.name)
                                                 attribute( "codeSystem", Lookups.codes(imm.codeSystem))
                                             }
                                         }
                                         "administrationTimeInterval" {
                                             attribute( "high",safeFormat(imm.localDate))
                                             attribute( "low",safeFormat(imm.localDate))
                                         }
                                         "doseNumber" {
                                             attribute("value",imm.dose)
                                         }
                                     }
                                 }
                             }
                         }
                     }

                 }
             }
         }.toString(options)
     }
    companion object{
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        private fun safeFormat(value:LocalDate?):String{
            return if(value == null){
                ""
            }else{
                formatter.format(value)//  + "000000"
            }
        }
    }
}
data class VMRDemographic(val birthTime: LocalDate,val gender:String)
data class VMRObservationResult(
                                val code:String,
                                val system: CodeSystem,
                                val concept: ObservationConcept,
                                val interpretation: Interpretation,
                                val localDate:LocalDate)
data class VMRImmunizationEvent(val vacId:String=UUID.randomUUID().toString(), val codeSystem: CodeSystem, val value:String, val name:String, val dose:Int, val localDate:LocalDate)

