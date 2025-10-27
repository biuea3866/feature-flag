package com.example.flag.service

import com.example.flag.domain.FeatureFlag
import com.example.flag.domain.Rule
import com.example.flag.repository.FeatureFlagRepository
import com.example.flag.repository.RuleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FlagService(private val featureFlagRepository: FeatureFlagRepository, private val ruleRepository: RuleRepository, private val auditService: AuditService) {

    @Transactional
    fun createFlag(name: String, description: String?): FeatureFlag {
        val featureFlag = FeatureFlag(name = name, description = description)
        val savedFlag = featureFlagRepository.save(featureFlag)
        auditService.logChange(savedFlag, "system", "CREATE", mapOf("name" to name, "description" to description))
        return savedFlag
    }

    @Transactional
    fun toggleFlag(name: String, enabled: Boolean): FeatureFlag? {
        val featureFlag = featureFlagRepository.findByName(name)
        featureFlag?.let {
            val oldEnabled = it.enabled
            it.enabled = enabled
            val savedFlag = featureFlagRepository.save(it)
            auditService.logChange(savedFlag, "system", "UPDATE", mapOf("enabled" to enabled, "old_enabled" to oldEnabled))
            return savedFlag
        }
        return null
    }

    @Transactional
    fun findByName(name: String): FeatureFlag? {
        return featureFlagRepository.findByName(name)
    }

    @Transactional
    fun addRule(flagName: String, rule: Rule): Rule? {
        val featureFlag = featureFlagRepository.findByName(flagName)
        featureFlag?.let {
            val newRule = rule.copy(featureFlag = it)
            val savedRule = ruleRepository.save(newRule)
            auditService.logChange(it, "system", "ADD_RULE", mapOf("rule" to savedRule))
            return savedRule
        }
        return null
    }
}
