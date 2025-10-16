package com.biuea.feature_flag.domain.service

import com.biuea.feature_flag.domain.entity.Feature
import com.biuea.feature_flag.domain.entity.FeatureFlag
import com.biuea.feature_flag.domain.entity.FeatureFlagAlgorithmDecider
import com.biuea.feature_flag.domain.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.entity.FeatureFlagGroupAlgorithm
import com.biuea.feature_flag.domain.entity.FeatureFlagGroupAlgorithmOption
import com.biuea.feature_flag.domain.entity.FeatureFlagStatus
import com.biuea.feature_flag.domain.repository.FeatureFlagGroupRepository
import com.biuea.feature_flag.domain.repository.FeatureFlagRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeatureFlagService(
    private val featureFlagRepository: FeatureFlagRepository,
    private val featureFlagGroupRepository: FeatureFlagGroupRepository
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
    fun fetchFeatureFlags(): List<FeatureFlag> {
        return featureFlagRepository.getAll()
    }

    @Transactional(readOnly = true)
    fun fetchAvailableFeatureFlagGroups(feature: Feature): List<FeatureFlagGroup> {
        return featureFlagGroupRepository.getAllBy(feature)
            .filter { it.isAvailable() }
    }

    @Transactional
    fun decideFeatureFlagGroupAlgorithm(
        name: String,
        feature: Feature,
        algorithm: FeatureFlagGroupAlgorithmOption,
        workspaceIds: List<Int>?,
        percentage: Int?,
        absolute: Int?,
    ) {
        val featureFlag = featureFlagRepository.getBy(feature)
        val featureFlagGroup = featureFlagGroupRepository.getOrNullBy(name)

        if (featureFlagGroup != null) {
            throw IllegalStateException("FeatureFlagGroup with name $name already exists")
        }

        val algorithm = FeatureFlagAlgorithmDecider.from(
            algorithm = algorithm,
            workspaceIds = workspaceIds,
            percentage = percentage,
            absolute = absolute
        )

        featureFlagGroupRepository.save(FeatureFlagGroup.create(name, featureFlag, algorithm))
    }

    @Transactional
    fun changeFeatureFlagGroupAlgorithm(
        name: String,
        algorithm: FeatureFlagGroupAlgorithmOption,
        workspaceIds: List<Int>?,
        percentage: Int?,
        absolute: Int?,
    ) {
        val featureFlagGroup = featureFlagGroupRepository.getOrNullBy(name)
            ?: throw IllegalStateException("FeatureFlagGroup with name $name does not exist")

        val algorithm = FeatureFlagAlgorithmDecider.from(
            algorithm = algorithm,
            workspaceIds = workspaceIds,
            percentage = percentage,
            absolute = absolute
        )

        featureFlagGroup.changeAlgorithm(algorithm)
        featureFlagGroup.decideGroup()

        featureFlagGroupRepository.save(featureFlagGroup)
    }
}