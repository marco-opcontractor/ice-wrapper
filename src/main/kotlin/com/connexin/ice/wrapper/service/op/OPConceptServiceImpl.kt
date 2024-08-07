package com.connexin.ice.wrapper.service.op

import org.opencds.config.api.model.Concept
import org.opencds.config.api.model.ConceptDeterminationMethod
import org.opencds.config.api.model.ConceptView
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.impl.ConceptImpl
import org.opencds.config.api.model.impl.ConceptViewImpl
import org.opencds.config.api.service.ConceptService
import org.opencds.config.mapper.ConceptDeterminationMethodMapper
import org.opencds.config.schema.ConceptDeterminationMethods
import java.io.File
import javax.xml.bind.JAXBContext

/**
 * Implementation of the [ConceptService] interface that retrieves the concept views based on the provided concept maps.
 *
 * @param conceptMaps A map of concepts to their corresponding concept maps.
 */
class OPConceptServiceImpl(private val conceptMaps:Map<Concept, List<ConceptMap>>) : ConceptService {

    /**
     * Retrieves a list of ConceptView objects based on the specified code system and code.
     *
     * @param codeSystem The code system to search in.
     * @param code The code to match against.
     * @return A mutable list of ConceptView objects.
     */
    override fun getConceptViews(codeSystem: String, code: String): MutableList<ConceptView> {
        val conceptViews = mutableListOf<ConceptView>()
        val concepts = this.conceptMaps[ConceptImpl.create(code, codeSystem, null,null)]
        if (concepts != null) {
            for(cm in concepts){
                conceptViews.add(ConceptViewImpl(cm.toConcept, cm.cdmCode))
            }
        }

        return conceptViews
    }

    //This isn't used,
    override fun byKM(p0: KnowledgeModule?): ConceptService {
        throw NotImplementedError()
    }

    companion object{
        /**
         * Builds a ConceptService from the given CDM (Concept Determination Methods) file.
         *
         * @param cdmFile The CDM file to build the ConceptService from.
         * @return The built ConceptService.
         * @throws MissingCDMFileException if the CDM file does not exist.
         */
        fun build(cdmFile: File):ConceptService{
            val cdms = mutableListOf<Any?>()
            val unmarshaller = JAXBContext.newInstance("org.opencds.config.schema").createUnmarshaller()
            if(!cdmFile.exists()){
                throw MissingCDMFileException("The CDM file was expected to be at ${cdmFile.absolutePath} but was not found")
            }

            val restCdms = unmarshaller.unmarshal(cdmFile) as ConceptDeterminationMethods
            cdms.addAll(ConceptDeterminationMethodMapper.internal(restCdms))

            val concepts = mutableListOf<ConceptMap>()
            val cdm = cdms.iterator().next() as ConceptDeterminationMethod
            if (cdm.conceptMappings != null) {
                for(cm in cdm.conceptMappings){
                    val toConcept = cm.toConcept
                    val item = cm.fromConcepts.iterator()
                    while (item.hasNext()) {
                        val fromConcept = item.next() as Concept
                        concepts.add(ConceptMap(toConcept, fromConcept, cdm.cdmId.code))
                    }
                }
            }

            val map = mutableMapOf<Concept,MutableList<ConceptMap>>()

            for(c in concepts){
                if(!map.containsKey(c.fromConcept)){
                    map[c.fromConcept] = mutableListOf()
                }
                map[c.fromConcept]!!.add(c)
            }

            return OPConceptServiceImpl(map)
        }
    }
}

data class ConceptMap(val toConcept:Concept,val fromConcept:Concept,val cdmCode:String)

class MissingCDMFileException(message:String) : Throwable(message)