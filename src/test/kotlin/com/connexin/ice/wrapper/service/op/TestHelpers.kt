package com.connexin.ice.wrapper.service.op

import com.connexin.ice.wrapper.model.Gender
import com.connexin.ice.wrapper.model.Vaccine
import com.connexin.ice.wrapper.model.VaccineReport
import java.time.LocalDate

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
}