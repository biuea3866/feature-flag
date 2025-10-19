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
    fun registerFeatureFlag(
        feature: Feature,
        status: FeatureFlagStatus,
    ): FeatureFlag {
        return featureFlagRepository.save(FeatureFlag.create(feature, status))
    }

    @Transactional
    fun activateFeatureFlag(
        feature: Feature,
        activate: Boolean
    ) {
        val featureFlag = featureFlagRepository.getFeatureFlagBy(feature)
        when (activate) {
            true -> featureFlag.activate()
            false -> featureFlag.inactivate()
        }
        featureFlagRepository.save(featureFlag)
    }

    @Transactional(readOnly = true)
    fun fetchFeatureFlagGroupMap(): Map<FeatureFlag, FeatureFlagGroup> {
        return featureFlagGroupRepository.getFeatureFlagGroups().associatedByFeatureFlag()
    }

    @Transactional(readOnly = true)
    fun fetchFeatureFlags(workspaceId: Int): List<FeatureFlag> {
        return featureFlagGroupRepository.getFeatureFlagGroups()
            .filter { it.containsWorkspace(workspaceId) }
            .map { it.featureFlag }
    }

    @Transactional(readOnly = true)
    fun fetchAvailableFeatureFlagGroup(feature: Feature): FeatureFlagGroup {
        return featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature)
            ?.takeIf { it.isAvailable() }
            ?: throw IllegalStateException("FeatureFlagGroup for feature $feature does not exist")
    }

    @Transactional
    fun decideFeatureFlagGroup(
        feature: Feature,
        algorithm: FeatureFlagAlgorithmOption,
        specifics: List<Int>,
        percentage: Int?,
        absolute: Int?,
    ) {
        val featureFlag = featureFlagRepository.getFeatureFlagBy(feature)
        val featureFlagGroup = featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature)

        featureFlagGroupRepository.save(FeatureFlagGroup.create(featureFlag, algorithm, specifics, absolute, percentage))
    }

    @Transactional
    fun changeFeatureFlagGroupAlgorithm(
        featureFlagGroupId: Long,
        algorithm: FeatureFlagAlgorithmOption,
        specifics: List<Int>,
        percentage: Int?,
        absolute: Int?,
    ) {
        val featureFlagGroup = featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(featureFlagGroupId)
            ?: throw NoSuchElementException("FeatureFlagGroup not found for id: $featureFlagGroupId")

        featureFlagGroup.changeAlgorithm(
            algorithmOption = algorithm,
            specifics = specifics,
            percentage = percentage,
            absolute = absolute
        )

        featureFlagGroupRepository.save(featureFlagGroup)
    }
}