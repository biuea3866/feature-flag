package com.biuea.feature_flag.domain.feature.service

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmDecider
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmOption
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus
import com.biuea.feature_flag.domain.feature.entity.associatedByFeatureFlag
import com.biuea.feature_flag.domain.feature.repository.FeatureFlagGroupRepository
import com.biuea.feature_flag.domain.feature.repository.FeatureFlagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeatureFlagService(
    private val featureFlagRepository: FeatureFlagRepository,
    private val featureFlagGroupRepository: FeatureFlagGroupRepository,
) {
    @Transactional
    fun registerFeatureFlag(feature: Feature): FeatureFlag {
        if (featureFlagRepository.getFeatureFlagOrNullBy(feature) != null) {
            throw IllegalStateException("FeatureFlag for feature $feature already exists")
        }

        return featureFlagRepository.save(FeatureFlag.create(feature))
    }

    @Transactional
    fun activateFeatureFlag(
        featureFlagGroupId: Long,
        status: FeatureFlagStatus
    ) {
        val featureFlagGroup = featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(featureFlagGroupId)
            ?: throw NoSuchElementException("FeatureFlagGroup not found for id: $featureFlagGroupId")
        when (status) {
            FeatureFlagStatus.ACTIVE -> featureFlagGroup.activate()
            FeatureFlagStatus.INACTIVE -> featureFlagGroup.inactivate()
        }
        featureFlagGroupRepository.save(featureFlagGroup)
    }

    @Transactional(readOnly = true)
    fun fetchFeatureFlagGroups(workspaceId: Int): List<FeatureFlagGroup> {
        return featureFlagGroupRepository.getFeatureFlagGroups()
            .filter { it.isEnabled(workspaceId) }
    }

    @Transactional
    fun decideFeatureFlagGroup(
        feature: Feature,
        status: FeatureFlagStatus,
        algorithm: FeatureFlagAlgorithmOption,
        specifics: List<Int>,
        percentage: Int?,
        absolute: Int?,
    ) {
        if (featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) != null) {
            throw IllegalStateException("FeatureFlagGroup for feature $feature already exists")
        }

        val featureFlag = featureFlagRepository.getFeatureFlagBy(feature)

        featureFlagGroupRepository.save(FeatureFlagGroup.create(featureFlag, status, algorithm, specifics, absolute, percentage))
    }

    @Transactional
    fun deleteFeatureFlagGroup(
        featureFlagGroupId: Long
    ) {
        val featureFlagGroup = featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(featureFlagGroupId)
            ?: throw NoSuchElementException("FeatureFlagGroup not found for feature: $featureFlagGroupId")

        featureFlagGroupRepository.delete(featureFlagGroup)
    }
}
