package com.connexin.ice.wrapper.service.op

import org.kie.api.event.rule.AfterMatchFiredEvent
import org.kie.api.event.rule.DefaultAgendaEventListener
import org.kie.api.runtime.rule.Match
import org.slf4j.LoggerFactory

class TrackingAgendaEventListener() : DefaultAgendaEventListener() {
    private val matchList: MutableList<Match> = ArrayList<Match>()

    override fun afterMatchFired(event: AfterMatchFiredEvent) {
        log.debug("Rule: {} Matched",event.match.rule)
       /* val rule: Rule = event.match.rule
        val ruleName: String = rule.getName()
        val ruleMetaDataMap: Map<String, Any> = rule.getMetaData()
        matchList.add(event.match)*/
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