package com.connexin.ice.wrapper.service.op

import com.connexin.ice.wrapper.service.TestHelpers.VMR_OBSERVATION_RESULT
import com.connexin.ice.wrapper.service.TestHelpers.VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL
import com.connexin.ice.wrapper.service.TestHelpers.vaccineReportIndicator
import com.connexin.ice.wrapper.service.TestHelpers.vaccineReportIndicatorOnly
import com.connexin.ice.wrapper.service.TestHelpers.vaccineReportNoIndicator
import com.connexin.ice.wrapper.service.op.Engine
import org.junit.jupiter.api.Test
import org.opencds.vmr.v1_0.internal.ObservationResult
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal

internal class OPEngineTest {

    private val engine = Engine.createOpEngine("v1.36.1")

    @Test
    fun evaluateRaw() {
        val result = engine.evaluateRaw(vaccineReportNoIndicator)

        val observations =  result[VMR_OBSERVATION_RESULT] as List<ObservationResult>
        val proposals = result[VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL] as List<SubstanceAdministrationProposal>

        println("Found ${proposals.size} proposals and ${observations.size} observations")

    }


    @Test
    fun evaluateRawIndicator() {
        val result = engine.evaluateRaw(vaccineReportIndicator)

        val observations =  result[VMR_OBSERVATION_RESULT] as List<ObservationResult>
        val proposals = result[VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL] as List<SubstanceAdministrationProposal>

        println("Found ${proposals.size} proposals and ${observations.size} observations")

    }

    @Test
    fun evaluateRawIndicatorOnly() {
        val result = engine.evaluateRaw(vaccineReportIndicatorOnly)

        val observations =  result[VMR_OBSERVATION_RESULT] as List<ObservationResult>
        val proposals = result[VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL] as List<SubstanceAdministrationProposal>

        println("Found ${proposals.size} proposals and ${observations.size} observations")

    }



}