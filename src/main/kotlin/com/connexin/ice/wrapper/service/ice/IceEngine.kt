package com.connexin.ice.wrapper.service.ice

import com.connexin.ice.wrapper.model.VaccineReport
import com.connexin.ice.wrapper.service.AbstractEngine
import com.connexin.ice.wrapper.service.FailedToForecastException
import com.connexin.ice.wrapper.service.IEngine
import com.connexin.ice.wrapper.toDate
import org.cdsframework.ice.service.configurations.ICEDecisionEngineDSS7EvaluationAdapter
import org.opencds.common.structures.EvaluationRequestDataItem
import org.opencds.common.structures.EvaluationRequestKMItem
import org.opencds.config.api.KnowledgeRepository
import org.opencds.config.api.model.impl.SSIdImpl
import org.slf4j.LoggerFactory
import java.util.*

class IceEngine(private val adapter: ICEDecisionEngineDSS7EvaluationAdapter,
                private val knowledgeRepository: KnowledgeRepository) : IEngine, AbstractEngine(){

    private val SSID = SSIdImpl.create("org.opencds.vmr","VMR","1.0")
    //get the facts to compare against
    private val flb = knowledgeRepository.semanticSignifierService.getFactListsBuilder(SSID)
    //get the knowledge module to be used
    private val km = knowledgeRepository.knowledgeModuleService.find(KMID)

    override fun evaluateRaw(vaccineReport: VaccineReport): MutableMap<String, MutableList<*>> {
        log.info("Evaluating vaccines for Gender: {} and BirthDate: {}",vaccineReport.gender,vaccineReport.dateOfBirth)

        val vmr = convertToIceModel(vaccineReport)
        val jaxb = unmarshal(vmr)
        val requestDate = vaccineReport.requestTime.toDate() ?: Date()

        //build the fact map
        val facts = flb.buildFactLists(knowledgeRepository,km,jaxb,requestDate)
        //create the final eval item
        val item = EvaluationRequestKMItem(KMID,buildEvaluationRequestDataItem(requestDate),jaxb,facts)
        //evaluate
        return adapter.getOneResponse(knowledgeRepository,item) ?: throw FailedToForecastException()

    }

    private fun buildEvaluationRequestDataItem(date: Date): EvaluationRequestDataItem {
        val item = EvaluationRequestDataItem()
        item.evalTime = date
        item.clientLanguage= "en"
        item.clientTimeZoneOffset= "+0000";
        item.interactionId = ""
        item.externalFactModelSSId = "org.opencds.vmr^VMR^1.0"
        item.inputItemName = "cdsPayload"
        item.inputContainingEntityId = "org.nyc.cir^ICEData^1.0.0"
        return item
    }

    companion object{
        private val log = LoggerFactory.getLogger(IceEngine::class.java)
        private val KMID = "org.nyc.cir^ICE^1.0.0"
    }
}