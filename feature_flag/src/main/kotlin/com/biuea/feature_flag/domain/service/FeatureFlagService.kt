package com.biuea.feature_flag.domain.service

import com.biuea.feature_flag.domain.entity.Feature
import com.biuea.feature_flag.domain.entity.FeatureFlag
import com.biuea.feature_flag.domain.entity.FeatureFlagAlgorithmDecider
import com.biuea.feature_flag.domain.entity.FeatureFlagAlgorithmOption
import com.biuea.feature_flag.domain.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.entity.FeatureFlagStatus
import com.biuea.feature_flag.domain.entity.associatedByFeatureFlag
import com.biuea.feature_flag.domain.repository.FeatureFlagGroupRepository
import com.biuea.feature_flag.domain.repository.FeatureFlagRepository
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
        val featureFlag = featureFlagRepository.getBy(feature)
        when (activate) {
            true -> featureFlag.activate()
            false -> featureFlag.inactivate()
        }
        featureFlagRepository.save(featureFlag)
    }

    @Transactional(readOnly = true)
    fun fetchFeatureFlagGroupMap(): Map<FeatureFlag, FeatureFlagGroup> {
        return featureFlagGroupRepository.getAll().associatedByFeatureFlag()
    }

    @Transactional(readOnly = true)
    fun fetchFeatureFlags(workspaceId: Int): List<FeatureFlag> {
        return featureFlagGroupRepository.getAll()
            .filter { it.containsWorkspace(workspaceId) }
            .map { it }
    }

    @Transactional(readOnly = true)
    fun fetchAvailableFeatureFlagGroup(feature: Feature): FeatureFlagGroup {
        return featureFlagGroupRepository.getOrNullBy(feature)
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
        val featureFlag = featureFlagRepository.getBy(feature)
        val featureFlagGroup = featureFlagGroupRepository.getOrNullBy(feature)

        featureFlagGroupRepository.save(FeatureFlagGroup.create(featureFlag, algorithm, specifics, absolute, percentage))
    }

    @Transactional
    fun changeFeatureFlagGroupAlgorithm(
        name: String,
        algorithm: FeatureFlagAlgorithmOption,
        specifics: List<Int>?,
        percentage: Int?,
        absolute: Int?,
    ) {
        val featureFlagGroup = featureFlagGroupRepository.getOrNullBy(name)
            ?: throw IllegalStateException("FeatureFlagGroup with name $name does not exist")

        val algorithm = FeatureFlagAlgorithmDecider.decide(
            algorithm = algorithm,
            specifics = specifics,
            percentage = percentage,
            absolute = absolute
        )

        featureFlagGroup.changeAlgorithm(algorithm)

        featureFlagGroupRepository.save(featureFlagGroup)
    }
}