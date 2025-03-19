package com.connexin.ice.wrapper

class Constants {
    object FlagConstants {
        const val FLAG_RSV_INDICATED = "rsvIndicated"
        const val FLAG_SYNAGIS_INDICATED = "synagisIndicated"
        const val FLAG_MENB_SINGLE = "menBIndicatedSingle"
        const val FLAG_MENB_HIGH_RISK = "menBIndicatedHighRisk"
        const val FLAG_MENB_TRUMEMBA_2DOSE = "FLAG_MENB_TRUMEMBA_2DOSE"
        const val FLAG_MENB_TRUMEMBA_3DOSE = "FLAG_MENB_TRUMEMBA_3DOSE"
        const val FLAG_MENB_TRUMEMBA_SERIES_CHANGED = "FLAG_MENB_TRUMEMBA_SERIES_CHANGED"
    }

    object DiseaseCodes {
        const val ICE_RSV_DISEASE_CODE = "079.6"
    }
}