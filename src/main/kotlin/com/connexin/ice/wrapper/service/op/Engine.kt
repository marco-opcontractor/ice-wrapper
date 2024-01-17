package com.connexin.ice.wrapper.service.op

import com.connexin.ice.wrapper.service.IEngine
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object Engine {

    private val tempDirectory =  System.getProperty("java.io.tmpdir")
    private val separator = System.getProperty("file.separator")

    /**
     * Creates an instance of the OP Engine.
     * @param rulesVersion The version of the rules to be used.
     * @param enableDebugLogging Whether to enable debug logging. Default value is false.
     * @return An instance of the OP Engine.
     */
    fun createOpEngine(rulesVersion:String,enableDebugLogging:Boolean=false):IEngine{
        val rules = if(!rulesVersion.startsWith("v")){
            "v${rulesVersion}"
        }
        else{
            rulesVersion
        }

        val outputDirectory = if(tempDirectory.endsWith(separator)){
             tempDirectory + "ice/$rules"
        }else{
            tempDirectory + "${separator}ice/$rules"
        }


        val rulesPackage = "rules/packaged/${rules}.zip"

        val loader = Engine::class.java.classLoader
        val zip = saveFile("$outputDirectory.zip",loader.getResourceAsStream(rulesPackage))

        unzip(zip,File(outputDirectory))

        val iceCommonKnowledgeDirectory = "$outputDirectory/knowledgeCommon"
        val iceKnowledgeModuleDirectory = "$outputDirectory/knowledgeModule"
        val cdmFile = "$outputDirectory/conceptDeterminationMethods/cdm.xml"

        //val kd = loader.getResource(iceCommonKnowledgeDirectory)
        //val md = loader.getResource(iceCommonKnowledgeDirectory)

        return OPEngine(
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
            enableTracking = enableDebugLogging,
            conceptService = OPConceptServiceImpl.build(File(cdmFile)),
            commonLogicModule = "org.cdsframework^ICE^1.0.0",
            commonKnowledgeDirectory = iceCommonKnowledgeDirectory,
            commonModuleDirectory = iceKnowledgeModuleDirectory,
            knowledgeModules = listOf("gov.nyc.cir^ICE^1.0.0")
        )
    }

    //Don't want to change the entire method signature
    private fun saveFile(outputLocation:String, input: InputStream?):File{
        if(input ==  null){
            throw FileNotFoundException("$outputLocation was not found in the jar")
        }
        val file = File(outputLocation)
        if(!file.exists()){
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeBytes(input.readBytes())
        return file
    }

    /**
     * Unzips a file to the specified location
     */
    fun unzip(file : File,destinationDir: File){
        val zip = ZipInputStream(FileInputStream(file))
        var zipEntry = zip.nextEntry;
        val buffer = ByteArray(1024)
        while (zipEntry != null) {
            val newFile = newFile(destinationDir, zipEntry)
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw IOException("Failed to create directory $newFile")
                }
            } else {
                // fix for Windows-created archives
                val parent = newFile.parentFile
                if (!parent.isDirectory && !parent.mkdirs()) {
                    throw IOException("Failed to create directory $parent")
                }

                // write file content
                val fos = FileOutputStream(newFile)
                var len: Int
                while (zip.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
            }
            zipEntry = zip.getNextEntry()
        }
        zip.closeEntry()
        zip.close()
    }

    /**
     * creates a new file from the zip entry
     */
    fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir, zipEntry.name)
        val destDirPath = destinationDir.canonicalPath
        val destFilePath = destFile.canonicalPath
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }
        return destFile
    }
}