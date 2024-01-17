package com.connexin.ice.wrapper.service.op

import org.cdsframework.ice.util.FileNameWithExtensionFilterImpl
import org.kie.api.KieServices
import org.kie.api.io.Resource
import org.kie.api.io.ResourceType
import org.kie.api.runtime.KieContainer
import org.kie.internal.io.ResourceFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class OPKieContainer {

    /**
     * Extraction of the container build process found inside of ICE
     * We are expecting the folder structure of the drools files to be similar if not the same as what is provided by ICE
     * At time of writing here are the properties we are using to create this
     * @param iceCommonKnowledgeDirectoryParam - ~/knowledgeCommon
     * @param iceKnowledgeModuleDirectoryParam - ~/knowledgeModule
     * @param iceBasePackageNameParam - org.cdsframework.ice
     * @param iceCommonBPMNFile - org.cdsframework^ICE^1.0.0.bpm
     * @param iceCommonDSLFile - org.cdsframework^ICE^1.0.0.dsl
     * @param iceBaseRules - org.cdsframework^ICE^1.0.
     * @param iceCustomRules - gov.nyc.cir^ICE^1.0.0
     * @param iceCustomPackageName - gov.nyc.cir.ice
     */
    fun buildContainer(iceCommonKnowledgeDirectoryParam:String,
                     iceKnowledgeModuleDirectoryParam:String,
                     iceBasePackageNameParam:String,
                     iceCommonBPMNFile:String,
                     iceCommonDSLFile:String,
                     iceBaseRules:String,
                     iceCustomRules:String,
                     iceCustomPackageName:String

    ): KieContainer {
        log.info("Loading drools files")

        val kieServices = KieServices.Factory.get()
        val kieFileSystem = kieServices.newKieFileSystem()
        val iceBasePackageName = appendSlash(iceBasePackageNameParam)
        val iceCommonKnowledgeDirectory = appendSlash(iceCommonKnowledgeDirectoryParam)
        val iceKnowledgeModuleDirectory = appendSlash(iceKnowledgeModuleDirectoryParam)

        //BPMN First
        val bpmnLocation = iceCommonKnowledgeDirectory + iceBasePackageName + iceCommonBPMNFile
        val bpmn = File(bpmnLocation)
        val bFile = ResourceFactory.newFileResource(bpmn)
        bFile.resourceType = ResourceType.BPMN2
        kieFileSystem.write(bFile)
        log.info("Loaded {}",bpmn.absolutePath)

        //DSL next
        val dslLocation = iceCommonKnowledgeDirectory  + iceBasePackageName + iceCommonDSLFile
        val dsl = File(dslLocation)
        val dFile = ResourceFactory.newFileResource(dsl)
        dFile.resourceType = ResourceType.DSL
        kieFileSystem.write(dFile)
        log.info("Loaded {}",dsl.absolutePath)


        //add the base rules
        val baseRulesToLoad = getRules(iceBaseRules, File("$iceCommonKnowledgeDirectory/$iceBasePackageName"),listOf())
        for(f in baseRulesToLoad){
            if(f.name.endsWith(".drl") || f.name.endsWith(".DRL")) {
                val lDrlFile: Resource = kieServices.resources.newFileSystemResource(f)
                lDrlFile.resourceType = ResourceType.DRL
                kieFileSystem.write(lDrlFile)
                log.info("Loaded {}",lDrlFile)
            }
        }

        for(f in baseRulesToLoad){
            if(f.name.endsWith(".dslr") || f.name.endsWith(".DSLR")) {
                val lDrlFile: Resource = kieServices.resources.newFileSystemResource(f)
                lDrlFile.resourceType = ResourceType.DSLR
                kieFileSystem.write(lDrlFile)
                log.info("Loaded {}",lDrlFile)
            }
        }


        // Add custom rules to knowledge base - both DRL and DSLR files permitted, DRL files loaded first.
        val customRulesToLoad = getRules(iceCustomRules, File(iceKnowledgeModuleDirectory + iceCustomPackageName),listOf())

        // First do the DRL files, then the DSLR files
        for (f in customRulesToLoad) {
            if (f.name.endsWith(".drl") || f.name.endsWith(".DRL")) {
                val lDrlFile = kieServices.resources.newFileSystemResource(f)
                lDrlFile.resourceType = ResourceType.DRL
                kieFileSystem.write(lDrlFile)
                log.info("Loaded {}",lDrlFile)
            }

        }
        for (f in customRulesToLoad) {
            if (f.name.endsWith(".dslr") || f.name.endsWith(".DSLR")) {
                val lDslrFile = kieServices.resources.newFileSystemResource(f)
                lDslrFile.resourceType = ResourceType.DSLR
                kieFileSystem.write(lDslrFile)
                log.info("Loaded {}",lDslrFile)
            }
        }

        val kieBuilder = kieServices.newKieBuilder(kieFileSystem)
        log.info("Starting initialization")
        kieBuilder.buildAll()
        val kieModule = kieBuilder.kieModule
        val kie =  kieServices.newKieContainer(kieModule.releaseId)
        log.info("Finished Building drools module")
        return kie
    }

    /**
     * Retrieves the list of rules files to add to the knowledge base.
     *
     * @param pRequestedKmId The ID of the requested KM.
     * @param pDSLRFileDirectory The directory where the DSLR files are located.
     * @param pFilesToExcludeFromKB The list of files to exclude from the knowledge base.
     * @return The list of DSLR files to add to the knowledge base.
     * @throws MissingKnowledgeCommonDirectoryException If the DSLR file directory is missing or not a directory.
     */
    private fun getRules(pRequestedKmId:String, pDSLRFileDirectory: File?, pFilesToExcludeFromKB:List<File>):List<File>{

        if (pDSLRFileDirectory == null || !pDSLRFileDirectory.exists() || !pDSLRFileDirectory.isDirectory) {
            throw MissingKnowledgeCommonDirectoryException(
                "Knowledge module specific directory does not exist; cannot continue. Directory: ${pDSLRFileDirectory?.absolutePath}")
        }

        // Obtain the files in this directory that adheres to the base and extension, ordered.
        val lValidFileExtensionsForCustomRules = arrayOf("drl", "dslr", "DRL", "DSLR")
        val lResultFiles = pDSLRFileDirectory.list(FileNameWithExtensionFilterImpl(pRequestedKmId, lValidFileExtensionsForCustomRules))
        if (lResultFiles != null && lResultFiles.isNotEmpty()) {
            Arrays.sort(lResultFiles)
        }

        val drlFilesToAddToKB: MutableList<File> = ArrayList()
        var customRuleFile: File? = null
        if (lResultFiles != null) {
            // Add DRL files first to KB
            for (i in lResultFiles.indices) {
                var exclusionFound = false
                val lResultFile = lResultFiles[i]
                customRuleFile = File(pDSLRFileDirectory, lResultFile)
                for (lExclusion in pFilesToExcludeFromKB) {
                    if (customRuleFile == lExclusion) {
                        exclusionFound = true
                        break
                    }
                }
                if (exclusionFound) {
                    continue
                }
                if (customRuleFile.exists()) {
                    if (lResultFile.endsWith(".drl") || lResultFile.endsWith(".DRL") || lResultFile.endsWith(".dslr") || lResultFile.endsWith(".DSLR")) {
                        drlFilesToAddToKB.add(customRuleFile)
                    }
                }
            }
        }

        return drlFilesToAddToKB
    }

    private fun appendSlash(value:String):String{
        return if(value.endsWith("/")){
            value
        }else{
            "$value/"
        }
    }

    companion object{
        private val log = LoggerFactory.getLogger(OPKieContainer::class.java)
    }
}

class MissingKnowledgeCommonDirectoryException(message:String) : Throwable(message)