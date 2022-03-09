package com.connexin.ice.wrapper.service

import com.connexin.ice.wrapper.model.VaccineReport

interface IEngine {
    /**
     * Brings back the more or less raw output for drools(note it is transformed a small about to match the ice implementation)
     */
    fun evaluateRaw(vaccineReport: VaccineReport): MutableMap<String, MutableList<*>>
}