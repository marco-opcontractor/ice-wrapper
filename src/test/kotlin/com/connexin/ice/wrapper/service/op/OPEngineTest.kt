package com.connexin.ice.wrapper.service.op

import com.connexin.ice.wrapper.service.op.TestHelpers.VMR_OBSERVATION_RESULT
import com.connexin.ice.wrapper.service.op.TestHelpers.VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL
import com.connexin.ice.wrapper.service.op.TestHelpers.vaccineReportNoIndicator
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.opencds.vmr.v1_0.internal.ObservationResult
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal
import java.io.File
import java.util.*

internal class OPEngineTest {

    //place drools files in once of the directories below
    private val iceDirectory = if(System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")){
        "C:/tmp/ice"
    }else{
        "/tmp/ice"
    }
    private val iceCommonKnowledgeDirectory = "$iceDirectory/knowledgeCommon"
    private val iceKnowledgeModuleDirectory = "$iceDirectory/knowledgeModule"
    private val cdmFile = "$iceDirectory/conceptDeterminationMethods/cdm.xml"


    @Test
    fun evaluateRaw() {
        val engine = OPEngine(
            kieContainer = OPKieContainer().buildContainer(
                iceCommonKnowledgeDirectoryParam = iceCommonKnowledgeDirectory,
                iceKnowledgeModuleDirectoryParam =  iceKnowledgeModuleDirectory,
                iceBasePackageNameParam = "org.cdsframework.ice",
                iceCommonBPMNFile = "org.cdsframework^ICE^1.0.0.bpmn",
                iceCommonDSLFile = "org.cdsframework^ICE^1.0.0.dsl",
                iceBaseRules = "org.cdsframework^ICE^1.0.0",
                iceCustomRules = "gov.nyc.cir^ICE^1.0.0",
                iceCustomPackageName = "gov.nyc.cir.ice"
            ),

            conceptService = OPConceptServiceImpl.build(File(cdmFile)),
            commonLogicModule = "org.cdsframework^ICE^1.0.0",
            commonKnowledgeDirectory = iceCommonKnowledgeDirectory,
            commonModuleDirectory = iceKnowledgeModuleDirectory,
            knowledgeModules = listOf("gov.nyc.cir^ICE^1.0.0")
        )

        val result = engine.evaluateRaw(vaccineReportNoIndicator)

        val observations =  result[VMR_OBSERVATION_RESULT] as List<ObservationResult>
        val proposals = result[VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL] as List<SubstanceAdministrationProposal>

        println("Found ${proposals.size} proposals and ${observations.size} observations")

    }

}