package com.connexin.ice.wrapper.service.ice

import com.connexin.ice.wrapper.service.op.TestHelpers
import org.cdsframework.ice.service.configurations.ICEDecisionEngineDSS7EvaluationAdapter
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.opencds.config.api.ConfigData
import org.opencds.config.file.FileConfigStrategy
import org.opencds.config.service.CacheServiceImpl
import org.opencds.vmr.v1_0.internal.ObservationResult
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal
import java.util.*

internal class IceEngineTest {

    //place drools files in once of the directories below
    private val iceDirectory = if(System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")){
        "C:/tmp/ice"
    }else{
        "/tmp/ice"
    }

    //More for an example, the internal property reader for ice is hardcoded to look on a servlet classpath,
    // this isn't overridable, so I can't actually load the ICE engine this way, without a servlet container it seems
    @Test
    @Disabled
    fun evaluateRaw() {
        val config = ConfigData()
        config.configType = "SIMPLE_FILE"
        config.setConfigPath(iceDirectory)
        config.kmThreads = 2

        val strategy = FileConfigStrategy()
        val knowledgeRepository = strategy.getKnowledgeRepository(config, CacheServiceImpl())
        knowledgeRepository.knowledgePackageService.preloadKnowledgePackages(knowledgeRepository.knowledgeModuleService.all)

        val adapter = ICEDecisionEngineDSS7EvaluationAdapter()
        val nycModule = knowledgeRepository.knowledgeModuleService.all.find { it.kmId.scopingEntityId == "org.nyc.cir"}
        adapter.loadKnowledgePackages(knowledgeRepository.knowledgePackageService,nycModule,2)
        val engine = IceEngine(
            adapter = adapter,
            knowledgeRepository)

        val result = engine.evaluateRaw(TestHelpers.vaccineReportNoIndicator)

        val observations =  result[TestHelpers.VMR_OBSERVATION_RESULT] as List<ObservationResult>
        val proposals = result[TestHelpers.VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL] as List<SubstanceAdministrationProposal>

        println("Found ${proposals.size} proposals and ${observations.size} observations")
    }
}