package com.connexin.ice.wrapper.service.op

import org.kie.api.event.rule.AfterMatchFiredEvent
import org.kie.api.event.rule.DefaultAgendaEventListener
import org.kie.api.runtime.rule.Match
import org.slf4j.LoggerFactory

/**
 * A class that tracks the agenda events fired during drools execution.
 */
class TrackingAgendaEventListener() : DefaultAgendaEventListener() {
    private val matchList: MutableList<Match> = ArrayList<Match>()

    override fun afterMatchFired(event: AfterMatchFiredEvent) {
        if (log.isDebugEnabled) {
            val seriesName = if (event.match.factHandles.toString().contains("getSeriesName()=")) event.match.factHandles.toString()
                .substringAfter("getSeriesName()=")
                .substringBefore(",")
                .trim() else "All series"
            log.debug("Rule: {} For Series: {} Matched", event.match.rule, seriesName)
        }
    }

    fun isRuleFired(ruleName: String?): Boolean {
        for (a: Match in matchList) {
            if (a.getRule().getName().equals(ruleName)) {
                return true
            }
        }
        return false
    }

    fun reset() {
        matchList.clear()
    }

    fun getMatchList(): List<Match> {
        return matchList
    }

    fun matchsToString(): String {
        if (matchList.size == 0) {
            return "No matchs occurred."
        } else {
            val sb = StringBuilder("Matchs: ")
            for (match: Match in matchList) {
                sb.append("\n  rule: ").append(match.getRule().getName())
            }
            return sb.toString()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TrackingAgendaEventListener::class.java)
    }
}