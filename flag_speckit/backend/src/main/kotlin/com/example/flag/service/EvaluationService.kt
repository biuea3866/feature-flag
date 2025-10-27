package com.example.flag.service

import com.example.flag.repository.FeatureFlagRepository
import com.example.flag.repository.WorkspaceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import java.util.zip.CRC32

@Service
class EvaluationService(private val featureFlagRepository: FeatureFlagRepository, private val workspaceRepository: WorkspaceRepository) {
    @Transactional
    fun evaluate(flagName: String, workspaceId: String): Boolean {
        val featureFlag = featureFlagRepository.findByName(flagName)
        featureFlag?.let {
            if (!it.enabled) return false

            println("FeatureFlag rules: ${it.rules}")
            val percentageRule = it.rules.find { r -> r.type == "PERCENTAGE_ROLLOUT" }
            println("Percentage Rule: $percentageRule")
            if (percentageRule != null) {
                val crc32 = CRC32()
                crc32.update(workspaceId.toByteArray())
                val value = crc32.value % 100
                println("Workspace ID: $workspaceId, CRC32 Value: ${crc32.value}, Calculated Value: $value, Percentage: ${percentageRule.percentage}")
                return value < percentageRule.percentage!!
            }

            return it.enabled
        }
        return false
    }

    @Transactional
    fun evaluateAll(workspaceId: String): Map<String, Boolean> {
        return featureFlagRepository.findAll().associate { flag ->
            flag.name to evaluate(flag.name, workspaceId)
        }
    }
}
