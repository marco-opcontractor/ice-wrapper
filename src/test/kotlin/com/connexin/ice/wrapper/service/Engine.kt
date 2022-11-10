package com.connexin.ice.wrapper.service

import com.connexin.ice.wrapper.service.op.OPConceptServiceImpl
import com.connexin.ice.wrapper.service.op.OPEngine
import com.connexin.ice.wrapper.service.op.OPEngineTest
import com.connexin.ice.wrapper.service.op.OPKieContainer
import java.io.File

object Engine {
    //private val tempDirectory = System.getProperty("java.io.tmpdir")
    private val iceDirectory = "src/main/resources/rules/v1.36.1"
    private val iceCommonKnowledgeDirectory = "$iceDirectory/knowledgeCommon"
    private val iceKnowledgeModuleDirectory = "$iceDirectory/knowledgeModule"
    private val cdmFile = "$iceDirectory/conceptDeterminationMethods/cdm.xml"

    val opEngine : OPEngine by lazy {

        OPEngine(
            kieContainer = OPKieContainer().buildContainer(
                iceCommonKnowledgeDirectoryParam = iceCommonKnowledgeDirectory,
                iceKnowledgeModuleDirectoryParam = iceKnowledgeModuleDirectory,
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
    }
}