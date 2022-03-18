package com.connexin.ice.wrapper.service

import com.connexin.ice.wrapper.model.*
import org.opencds.vmr.v1_0.schema.CDSInput
import org.springframework.xml.transform.StringSource
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement

abstract class AbstractEngine() {

    private val context = JAXBContext.newInstance(CDSInput::class.java);

    protected fun convertToIceModel(vaccineReport: VaccineReport): VMR {
        val obs = mutableListOf<VMRObservationResult>()
        val imms = mutableListOf<VMRImmunizationEvent>()

        //get the dosing by grouping the vaccines and sorting by date
        val vaccines = vaccineReport.vaccines.groupBy { it.cvx }
        for(v in vaccines.values){
            v.sortedBy { it.date }
        }
        for(v in vaccines) {
            v.value.forEachIndexed { index, vaccine ->
                imms.add(
                    VMRImmunizationEvent(vaccine.id,
                        CodeSystem.CVX, vaccine.cvx, vaccine.name, index + 1, vaccine.date)
                )
            }
        }
        vaccineReport.indicators.forEach {
            obs.add(
                VMRObservationResult(it.code,it.system,
                    ObservationConcept.DISEASE_DOCUMENTED,it.interpretation,it.date)
            )
        }
        return VMR(
            demographics = VMRDemographic(vaccineReport.dateOfBirth,convertGender(vaccineReport.gender)),
            obs = obs,
            imms = imms
        )
    }

    protected fun unmarshal(vmr:VMR): JAXBElement<CDSInput> {
        //Not threadsafe, so we need to create a new one everytime, we could pool it, but I'm not sure of the gains there
        return context.createUnmarshaller().unmarshal(StrSource(vmr.toXML()), CDSInput::class.java)
    }

    private fun convertGender(gender:Gender):String{
        return gender.name.first().uppercase()
    }


}


class FailedToForecastException : Throwable()