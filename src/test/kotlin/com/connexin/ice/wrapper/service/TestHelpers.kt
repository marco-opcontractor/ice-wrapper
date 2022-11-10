package com.connexin.ice.wrapper.service

import com.connexin.ice.wrapper.model.*
import com.sun.xml.internal.bind.v2.model.core.ID
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object TestHelpers {

    const val VMR_SUBSTANCE_ADMINISTRATION_PROPOSAL = "SubstanceAdministrationProposal"
    const val VMR_OBSERVATION_RESULT ="ObservationResult"
    const val VMR_SUBSTANCE_ADMINISTRATION_EVENT ="SubstanceAdministrationEvent"

    val vaccineReportNoIndicator = VaccineReport(
        Gender.M, LocalDate.of(2020,11,5),
        LocalDate.now(),
        mutableMapOf(),
        listOf(
            Vaccine("1","08","HepB", LocalDate.of(2020,11,6)),

            Vaccine("2","08","HepB", LocalDate.of(2020,12,8)),

            Vaccine("3","106","DTaP+"    , LocalDate.of(2021,1,5)),
            Vaccine("4","10","IPV"       , LocalDate.of(2021,1,5)),
            Vaccine("5","48","HIB-PRP-T" , LocalDate.of(2021,1,5)),
            Vaccine("6","133","PCV13"    , LocalDate.of(2021,1,5)),
            Vaccine("7","116","RotaVirus", LocalDate.of(2021,1,5)),

            Vaccine("8","106","DTaP+"    , LocalDate.of(2021,3,12)),
            Vaccine("9", "48","HIB-PRP-T", LocalDate.of(2021,3,12)),
            Vaccine("10","10","IPV"      , LocalDate.of(2021,3,12)),
            Vaccine("11","133","PCV13"      , LocalDate.of(2021,3,12)),
            Vaccine("12","116","RotaVirus"  , LocalDate.of(2021,3,12)),

            Vaccine("13","106","DTaP+"    , LocalDate.of(2021,5,7)),
            Vaccine("14", "48","HIB-PRP-T", LocalDate.of(2021,5,7)),
            Vaccine("15","133","PCV13"      , LocalDate.of(2021,5,7)),
            Vaccine("16","116","RotaVirus"  , LocalDate.of(2021,5,7)),

            Vaccine("17","08","HepB", LocalDate.of(2021,8,9)),

            Vaccine("18","150","FLU-IIV4 6m+ pf", LocalDate.of(2021,11,15)),
            Vaccine("19","83","HepA 2dose", LocalDate.of(2021,11,15)),
            Vaccine("20","03","MMR", LocalDate.of(2021,11,15)),
            Vaccine("21","21","Var", LocalDate.of(2021,11,15)),

            Vaccine("22","150","FLU-IIV4 6m+ pf", LocalDate.of(2021,12,20)),
        ),
        listOf()
    )

    val vaccineReportIndicator = VaccineReport(
        Gender.M, LocalDate.of(2020,11,5),
        LocalDate.now(),
        mutableMapOf(),
        listOf(
            Vaccine("1","08","HepB", LocalDate.of(2020,11,6)),

            Vaccine("2","08","HepB", LocalDate.of(2020,12,8)),

            Vaccine("3","106","DTaP+"    , LocalDate.of(2021,1,5)),
            Vaccine("4","10","IPV"       , LocalDate.of(2021,1,5)),
            Vaccine("5","48","HIB-PRP-T" , LocalDate.of(2021,1,5)),
            Vaccine("6","133","PCV13"    , LocalDate.of(2021,1,5)),
            Vaccine("7","116","RotaVirus", LocalDate.of(2021,1,5)),

            Vaccine("8","106","DTaP+"    , LocalDate.of(2021,3,12)),
            Vaccine("9", "48","HIB-PRP-T", LocalDate.of(2021,3,12)),
            Vaccine("10","10","IPV"      , LocalDate.of(2021,3,12)),
            Vaccine("11","133","PCV13"      , LocalDate.of(2021,3,12)),
            Vaccine("12","116","RotaVirus"  , LocalDate.of(2021,3,12)),

            Vaccine("13","106","DTaP+"    , LocalDate.of(2021,5,7)),
            Vaccine("14", "48","HIB-PRP-T", LocalDate.of(2021,5,7)),
            Vaccine("15","133","PCV13"      , LocalDate.of(2021,5,7)),
            Vaccine("16","116","RotaVirus"  , LocalDate.of(2021,5,7)),

            Vaccine("17","08","HepB", LocalDate.of(2021,8,9)),

            Vaccine("18","150","FLU-IIV4 6m+ pf", LocalDate.of(2021,11,15)),
            Vaccine("19","83","HepA 2dose", LocalDate.of(2021,11,15)),
            Vaccine("20","03","MMR", LocalDate.of(2021,11,15)),

            Vaccine("22","150","FLU-IIV4 6m+ pf", LocalDate.of(2021,12,20)),
        ),
        listOf(
            Indicator(id="654",name="Measles","055.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2021,12,12)),
            Indicator(id="655",name="Mumps","072.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2021,12,12)),
            Indicator(id="656",name="Rubella","056.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2021,12,12)),
            Indicator(id="657",name="Varicella","052.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2021,12,12)),
        )
    )

    val vaccineReportIndicatorOnly = VaccineReport(
        Gender.M, LocalDate.of(2020,11,5),
        LocalDate.now(),
        mutableMapOf(),
        listOf(
        ),
        listOf(
            Indicator(id="657",name="Varicella","052.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),
            Indicator(id="658",name="Hep B","070.30", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),
            //Indicator(id="654",name="Measles","055.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),
            Indicator(id="655",name="Mumps","072.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),
            //Indicator(id="656",name="Rubella","056.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),*/

        )
    )

    val vaccineReportIndicatorOnlyDisease = VaccineReport(
        Gender.M, LocalDate.of(2020,11,5),
        LocalDate.now(),
        mutableMapOf(),
        listOf(
        ),
        listOf(
            Indicator(id="657",name="Varicella","052.9", system = CodeSystem.ICD_9, interpretation = Interpretation.DISEASE , date = LocalDate.of(2020,12,12)),
            Indicator(id="658",name="Hep B","070.30", system = CodeSystem.ICD_9, interpretation = Interpretation.DISEASE, date = LocalDate.of(2020,12,12)),
            //Indicator(id="654",name="Measles","055.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),
            Indicator(id="655",name="Mumps","072.9", system = CodeSystem.ICD_9, interpretation = Interpretation.DISEASE, date = LocalDate.of(2020,12,12)),
            //Indicator(id="656",name="Rubella","056.9", system = CodeSystem.ICD_9, interpretation = Interpretation.IS_IMMUNE, date = LocalDate.of(2020,12,12)),*/

        )
    )
    /**
     * Unzips a file to the specified location
     */
    fun unzip(file : File, destinationDir: File){
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
     * Appends a slash if needed to a string
     */
    fun appendSlash(value:String):String{
        return if(value.endsWith("/")){
            value
        }else{
            "${value}/"
        }
    }

    fun getDTAPVaccine(id:String,date:LocalDate):Vaccine{
        return Vaccine(id,"01","DTP",date)
    }

    fun getTDAPVaccine(id:String,date:LocalDate):Vaccine{
        return Vaccine(id,"115","Tdap",date)
    }

    /**
     * creates a new file from the zip entry
     */
    private fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
        val destFile = File(destinationDir, zipEntry.name)
        val destDirPath = destinationDir.canonicalPath
        val destFilePath = destFile.canonicalPath
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw IOException("Entry is outside of the target dir: " + zipEntry.name)
        }
        return destFile
    }

    fun getVaccineGroup(id:String,list: List<SubstanceAdministrationProposal>):SubstanceAdministrationProposal?{
        return list.find { it.substance.substanceCode.code == id }
    }
}