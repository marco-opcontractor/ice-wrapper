package com.connexin.ice.wrapper.service

import com.connexin.ice.wrapper.model.*
import org.opencds.vmr.v1_0.schema.CDSInput
import org.springframework.xml.transform.StringSource
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement

/**
 * An abstract class representing an engine for converting a [VaccineReport] to a [VMR] model.
 */
abstract class AbstractEngine() {

    private val context = JAXBContext.newInstance(CDSInput::class.java);

    private val supportedIceWrapperInterpretations = setOf(Interpretation.REFUSED, Interpretation.IS_IMMUNE, Interpretation.DEFERRED, Interpretation.DISEASE)

    /**
     * Converts a [VaccineReport] object to a [VMR] object.
     *
     * @param vaccineReport The [VaccineReport] object to be converted.
     * @return The converted [VMR] object.
     */
    protected fun convertToIceModel(vaccineReport: VaccineReport): VMR {
        val obs = mutableListOf<VMRObservationResult>()
        val imms = mutableListOf<VMRImmunizationEvent>()

        //get the dosing by grouping the vaccines and sorting by date
        val vaccines = vaccineReport.vaccines.groupBy { it.cvx }
        for (v in vaccines.values) {
            v.sortedBy { it.date }
        }
        for (v in vaccines) {
            v.value.forEachIndexed { index, vaccine ->
                imms.add(
                    VMRImmunizationEvent(
                        vaccine.id,
                        CodeSystem.CVX, vaccine.cvx, vaccine.name, index + 1, vaccine.date
                    )
                )
            }
        }
        vaccineReport.indicators.filter { supportedIceWrapperInterpretations.contains(it.interpretation) }.forEach {
            obs.add(
                VMRObservationResult(
                    it.code, it.system,
                    when (it.interpretation) {
                        Interpretation.REFUSED, Interpretation.IS_IMMUNE, Interpretation.DEFERRED -> ObservationConcept.PROOF_OF_IMMUNITY
                        Interpretation.DISEASE -> ObservationConcept.DISEASE_DOCUMENTED
                        else -> ObservationConcept.PROOF_OF_IMMUNITY
                    }, it.interpretation, it.date
                )
            )
        }
        return VMR(
            demographics = VMRDemographic(vaccineReport.dateOfBirth, convertGender(vaccineReport.gender)),
            obs = obs,
            imms = imms
        )
    }

    protected fun unmarshal(vmr: VMR): JAXBElement<CDSInput> {
        //Not threadsafe, so we need to create a new one everytime, we could pool it, but I'm not sure of the gains there
        return context.createUnmarshaller().unmarshal(StrSource(vmr.toXML()), CDSInput::class.java)
    }

    private fun convertGender(gender: Gender): String {
        return gender.name.first().uppercase()
    }


}


class FailedToForecastException : Throwable()