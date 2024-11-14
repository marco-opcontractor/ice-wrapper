package com.connexin.ice.wrapper.service.op

import com.connexin.ice.wrapper.Constants
import com.connexin.ice.wrapper.model.Interpretation
import com.connexin.ice.wrapper.model.VaccineReport
import com.connexin.ice.wrapper.service.AbstractEngine
import com.connexin.ice.wrapper.service.IEngine
import com.connexin.ice.wrapper.toDate
import org.cdsframework.ice.service.Schedule
import org.kie.api.command.Command
import org.kie.api.runtime.ExecutionResults
import org.kie.api.runtime.KieContainer
import org.kie.internal.command.CommandFactory
import org.opencds.config.api.service.ConceptService
import org.opencds.vmr.v1_0.internal.*
import org.opencds.vmr.v1_0.internal.concepts.VmrOpenCdsConcept
import org.opencds.vmr.v1_0.mappings.`in`.BuildOpenCDSConceptLists
import org.opencds.vmr.v1_0.mappings.`in`.FactLists
import org.opencds.vmr.v1_0.mappings.mappers.CDSInputMapper
import org.opencds.vmr.v1_0.mappings.mappers.EvaluatedPersonMapper
import org.opencds.vmr.v1_0.mappings.mappers.OneObjectMapper
import org.opencds.vmr.v1_0.mappings.mappers.VMRMapper
import org.opencds.vmr.v1_0.schema.CDSInput
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.xml.bind.JAXBElement


/**
 * More or less mirrors the internals of the ICEDecisionEngineDSS7EvaluationAdapter.getOneResponse
 * the main advantages of this wrapper is it allows the user to provide a differently configured drools container and a greatly simplified input
 * the goal of this call was the hide specific ICE concepts for easier use
 * To instantiate this class:
 * build a kiecontainer with the drools files, use the provided OPKieContainer class if unsure
 * create your own concept service, or use the provided OPConceptServiceImpl
 * commonLogicModule - org.cdsframework^ICE^1.0.0
 * commonKnowledgeDirectory - ~/knowledgeCommon folder
 * commonModuleDirectory - ~/knowledgeModule folder
 */
class OPEngine(private val kieContainer: KieContainer,
               private val conceptService: ConceptService,
               private val enableTracking:Boolean,
               commonLogicModule:String,
               commonKnowledgeDirectory:String,
               commonModuleDirectory:String,
               knowledgeModules :List<String>) : IEngine, AbstractEngine(){

    private val schedule: Schedule = Schedule("requestedKmId",
        commonLogicModule,
        File(commonKnowledgeDirectory),
        knowledgeModules,
        File(commonModuleDirectory)
    )

    /**
     * Evaluates the given vaccine report and returns the evaluation result.
     *
     * @param vaccineReport The vaccine report to be evaluated.
     * @return The evaluation result as a mutable map with string keys and mutable list values.
     */
    override fun evaluateRaw(vaccineReport: VaccineReport):  MutableMap<String, MutableList<*>> {
        log.info("Evaluating vaccine report with vaccine count:{} indicator: {} for gender {} and birthdate:{}",
            vaccineReport.vaccines.size,vaccineReport.indicators.size,vaccineReport.gender,vaccineReport.dateOfBirth)
        val namedObject = HashMap<String,Any>()
        val session = kieContainer.newStatelessKieSession()
        if(enableTracking) {
            val agendaEventListener = TrackingAgendaEventListener()
            session.addEventListener(agendaEventListener)
        }
        val isRsvIndicated = vaccineReport.flags?.get(Constants.FlagConstants.FLAG_RSV_INDICATED) ?: vaccineReport.flags?.get(Constants.FlagConstants.FLAG_SYNAGIS_INDICATED)
        val isMenBSharedDecision = vaccineReport.flags?.get(Constants.FlagConstants.FLAG_MENB_SINGLE) ?: false
        val isMenBHighRisk = vaccineReport.flags?.get(Constants.FlagConstants.FLAG_MENB_HIGH_RISK) ?: false
        val mommyVaxGiven = vaccineReport.indicators.any {
            it.interpretation == Interpretation.PREGNANCY_VACCINATED && it.code == Constants.DiseaseCodes.ICE_RSV_DISEASE_CODE
                    && it.date.isBefore(vaccineReport.dateOfBirth.minusDays(13))
        }

        val cmds = mutableListOf<Command<*>>()
        cmds.add(CommandFactory.newSetGlobal("evalTime",vaccineReport.requestTime.toDate()))
        cmds.add(CommandFactory.newSetGlobal("clientLanguage","en"))
        cmds.add(CommandFactory.newSetGlobal("clientTimeZoneOffset","+0000"))
        cmds.add(CommandFactory.newSetGlobal("assertions", HashSet<String>()))
        cmds.add(CommandFactory.newSetGlobal("namedObjects",namedObject))
        cmds.add(CommandFactory.newSetGlobal("patientAgeTimeOfInterest",null))
        cmds.add(CommandFactory.newSetGlobal("schedule",schedule))
        cmds.add(CommandFactory.newSetGlobal("outputEarliestOverdueDates",java.lang.Boolean("true")))
        cmds.add(CommandFactory.newSetGlobal("doseOverrideFeatureEnabled",java.lang.Boolean("false")))
        cmds.add(CommandFactory.newSetGlobal("outputSupplementalText",java.lang.Boolean("true")))
        cmds.add(CommandFactory.newSetGlobal("outputRuleName",java.lang.Boolean("true")))
        cmds.add(CommandFactory.newSetGlobal("enableUnsupportedVaccinesGroup",java.lang.Boolean("true")))
        cmds.add(CommandFactory.newSetGlobal("vaccineGroupExclusions", listOf<Any>()))
        cmds.add(CommandFactory.newSetGlobal("rsvSeasonStartMonthDay", org.joda.time.MonthDay(10, 1)))
        cmds.add(CommandFactory.newSetGlobal("rsvSeasonEndMonthDay", org.joda.time.MonthDay(3, 31)))
        cmds.add(CommandFactory.newSetGlobal("februaryStartMonthDay", org.joda.time.MonthDay(2, 1)))
        cmds.add(CommandFactory.newSetGlobal("isRSVHighRisk", isRsvIndicated == true))
        cmds.add(CommandFactory.newSetGlobal("wasMommyVaxGiven", mommyVaxGiven))
        cmds.add(CommandFactory.newSetGlobal("isMenBSharedDecision", isMenBSharedDecision))
        cmds.add(CommandFactory.newSetGlobal("isMenBHighRisk", isMenBHighRisk))




        val vmr = convertToIceModel(vaccineReport)
        val jaxb = unmarshal(vmr)
        val requestDate = vaccineReport.requestTime.toDate() ?: Date()

        //add the facts
        val facts = buildFactList(vaccineReport,requestDate,jaxb)
        for(f in facts){
            if(f.value.isNotEmpty()){
                cmds.add(
                    CommandFactory.newInsertElements(
                        f.value,
                        f.key.simpleName,
                        true,
                        null
                    ))
            }
        }

        cmds.add(CommandFactory.newStartProcess("PrimaryProcess"))
        val result =  session.execute(CommandFactory.newBatchExecution((cmds)))
     /*   agendaEventListener.getMatchList().forEach {
            println(it)
        }*/

        return convertExecutionResult(result,namedObject.toMap())
    }

    /**
     * Converts the execution results and named objects into a mutable map.
     *
     * @param results The execution results containing the original objects passed in via CMD.
     * @param namedObjects The map of named objects returned by the rules.
     * @return The converted result as a mutable map with string keys and mutable list values.
     */
    private fun convertExecutionResult(results: ExecutionResults, namedObjects: Map<String, Any>): MutableMap<String, MutableList<*>> {
        val resultFactLists = mutableMapOf<String, MutableList<*>>()

        // update original entries from allFactLists to capture any new or updated elements
        // ** need to look for every possible fact list, because rules may have created new ones...
        // NOTE that results contains the original objects passed in via CMD
        // structure, with any changes introduced by rules.
        val allResultNames: Collection<String> = results.getIdentifiers()
        // includes concepts but not globals?
        // includes concepts but not globals?
        for (oneName in allResultNames) {
            if (!FILTERED_GLOBALS.contains(oneName)) {
                // ignore these submitted globals, they should not have been
                // changed by rules, and look at everything else
                resultFactLists.put(oneName, results.getValue(oneName) as MutableList<*>)
            }
        }

        /**
         * now process the returned namedObjects and add them to the
         * resultFactLists
         */
        for (key in namedObjects.keys) {
            val oneNamedObject = namedObjects[key]
            if (oneNamedObject != null) {
                val className = oneNamedObject.javaClass.simpleName
                var oneList = resultFactLists[className] as MutableList<Any>?
                if (oneList == null) {
                    oneList = mutableListOf()
                    oneList.add(oneNamedObject)
                } else {
                    oneList.add(oneNamedObject)
                }
                resultFactLists[className] = oneList
            }
        }

        return resultFactLists
    }

    /**
     * Builds a map of facts for evaluation.
     *
     * @param report The vaccine report.
     * @param evalTime The evaluation time.
     * @param payload The payload containing the CDSInput.
     * @return A map of facts where the key is the class type and the value is a list of instances of that class type.
     */
    private fun buildFactList(report:VaccineReport, evalTime: Date, payload: JAXBElement<CDSInput>):Map<Class<*>,List<*>>{
        //Avoid using the MappingUtility where possible, its fine to use it as static, but it has stateful fields which can cause a lot of issues.

        val allFacts = HashMap<Class<*>,List<*>>()
        val facts = FactLists()

        //Set eval time
        val evalTimeFact = EvalTime()
        evalTimeFact.evalTimeValue = evalTime
        facts.put(EvalTime::class.java, evalTimeFact)

        //Set the CDS input
        val internalCDSInput = org.opencds.vmr.v1_0.internal.CDSInput()
        CDSInputMapper.pullIn(payload.value as CDSInput, internalCDSInput, null)//it brings in a static class... which has stateful fields, just send null and let it use the static reference
        facts.put(
            org.opencds.vmr.v1_0.internal.CDSInput::class.java,
            internalCDSInput
        )

        //Set the Focal Person
        val focalPerson = FocalPersonId(internalCDSInput.focalPersonId)
        facts.put(FocalPersonId::class.java, focalPerson)

        val vmrInput = payload.value.vmrInput
        val internalVMR = VMR()
        VMRMapper.pullIn(vmrInput, internalVMR, null)
        facts.put(VMR::class.java, internalVMR)


        val internalPatient = EvaluatedPerson()

        val inputPatient = vmrInput.patient
        EvaluatedPersonMapper.pullIn(
            inputPatient,
            internalPatient,
            null,
            null,
            focalPerson.id,
            focalPerson.id,
            facts
        )

        facts.put(EvaluatedPerson::class.java,internalPatient)

        val list = getAgeFacts(report.dateOfBirth, report.requestTime.atStartOfDay(), focalPerson.id)
        for(l in list){
            facts.put(EvaluatedPersonAgeAtEvalTime::class.java,l)
        }


        if(inputPatient.clinicalStatements?.observationResults?.observationResult != null) {
            for (obr in inputPatient.clinicalStatements.observationResults.observationResult){
                OneObjectMapper.pullInClinicalStatement(obr, ObservationResult(),focalPerson.id,focalPerson.id,facts)
            }
        }

        if(inputPatient.clinicalStatements?.substanceAdministrationEvents?.substanceAdministrationEvent != null) {
            for (imm in inputPatient.clinicalStatements.substanceAdministrationEvents.substanceAdministrationEvent){
                OneObjectMapper.pullInClinicalStatement(imm,
                    SubstanceAdministrationEvent(),focalPerson.id,focalPerson.id,facts)
            }
        }

        val builder = BuildOpenCDSConceptLists()
        builder.buildConceptLists<VmrOpenCdsConcept>(conceptService,facts,allFacts)

        facts.put(EvaluatedPerson::class.java, internalPatient)
        facts.populateAllFactLists(allFacts)
        return allFacts
    }


    /**
     * Retrieves a list of age facts for a person at a specific evaluation time.
     *
     * @param birthTime The birth date of the person.
     * @param evalTime The evaluation time.
     * @param personId The ID of the person.
     * @return A list of EvaluatedPersonAgeAtEvalTime objects representing the person's age at the evaluation time.
     */
    private fun getAgeFacts(birthTime : LocalDate, evalTime: LocalDateTime, personId:String):List<EvaluatedPersonAgeAtEvalTime>{

        return listOf(
            buildAgeFact(
                ChronoUnit.SECONDS.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_SECOND,personId),
            buildAgeFact(
                ChronoUnit.MINUTES.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_MINUTE,personId),
            buildAgeFact(
                ChronoUnit.HOURS.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_HOUR,personId),
            buildAgeFact(
                ChronoUnit.DAYS.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_DAY,personId),
            buildAgeFact(
                ChronoUnit.WEEKS.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_WEEK,personId),
            buildAgeFact(
                ChronoUnit.MONTHS.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_MONTH,personId),
            buildAgeFact(
                ChronoUnit.YEARS.between(evalTime,birthTime.atStartOfDay()).toInt(),
                EvaluatedPersonAgeAtEvalTime.AGE_UNIT_YEAR,personId)
        )

    }

    /**
     * Builds an EvaluatedPersonAgeAtEvalTime object representing a person's age at the evaluation time.
     *
     * @param value The age value.
     * @param ageUnit The age unit.
     * @param personId The ID of the person.
     * @return An EvaluatedPersonAgeAtEvalTime object representing the person's age at the evaluation time.
     */
    private fun buildAgeFact(value:Int,ageUnit:String, personId:String): EvaluatedPersonAgeAtEvalTime {
        val personAgeInUnit = EvaluatedPersonAgeAtEvalTime()
        personAgeInUnit.age = value
        personAgeInUnit.ageUnit = ageUnit
        personAgeInUnit.personId = personId
        return personAgeInUnit
    }


    companion object {
        private val log = LoggerFactory.getLogger(OPEngine::class.java)
        private const val EVAL_TIME = "evalTime"
        private const val CLIENT_LANG = "clientLanguage"
        private const val CLIENT_TZ_OFFSET = "clientTimeZoneOffset"
        private const val FOCAL_PERSON_ID = "focalPersonId"
        private const val ASSERTIONS = "assertions"
        private const val NAMED_OBJECTS = "namedObjects"
        private val FILTERED_GLOBALS: Set<String> = HashSet(
            setOf(
                EVAL_TIME,
                CLIENT_LANG,
                CLIENT_TZ_OFFSET,
                FOCAL_PERSON_ID
            )
        )
    }

}