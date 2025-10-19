package com.biuea.feature_flag.infrastructure.feature

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.feature.repository.FeatureFlagGroupRepository
import com.biuea.feature_flag.domain.feature.repository.FeatureFlagRepository
import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagGroupJpaRepository
import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagJpaRepository
import com.biuea.feature_flag.infrastructure.feature.jpa.toDomain
import com.biuea.feature_flag.infrastructure.feature.jpa.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class FeatureFlagAdaptor(
    private val featureFlagJpRepository: FeatureFlagJpaRepository,
    private val featureFlagGroupJpaRepository: FeatureFlagGroupJpaRepository
): FeatureFlagRepository, FeatureFlagGroupRepository {
    override fun save(entity: FeatureFlag): FeatureFlag {
        return featureFlagJpRepository.save(entity.toEntity()).toDomain()
    }

    override fun getFeatureFlags(): List<FeatureFlag> {
        return featureFlagJpRepository.findAll().map { it.toDomain() }
    }

    override fun getFeatureFlagBy(feature: Feature): FeatureFlag {
        return featureFlagJpRepository.findByFeatureIs(feature)?.toDomain()
            ?: throw NoSuchElementException("FeatureFlag not found for feature: $feature")
    }

    override fun save(entity: FeatureFlagGroup): FeatureFlagGroup {
        return featureFlagGroupJpaRepository.save(entity.toEntity())
            .toDomain(entity.featureFlag.toEntity())
    }

    override fun getFeatureFlagGroupOrNullBy(id: Long): FeatureFlagGroup? {
        val featureFlagGroupEntity = featureFlagGroupJpaRepository.findByIdOrNull(id) ?: return null
        val featureFlagEntity = featureFlagJpRepository.findByIdOrNull(featureFlagGroupEntity.featureFlagId)
            ?: return null
        return featureFlagGroupEntity.toDomain(featureFlagEntity)
    }

    override fun getFeatureFlagGroupOrNullBy(feature: Feature): FeatureFlagGroup? {
        val featureFlag = featureFlagJpRepository.findByFeatureIs(feature) ?: return null
        return featureFlagGroupJpaRepository.findByFeatureFlagId(featureFlag.id)
            ?.toDomain(featureFlag)
    }

    override fun getFeatureFlagGroups(): List<FeatureFlagGroup> {
        val featureFlags = featureFlagJpRepository.findAll().associateBy { it.id }
        return featureFlagGroupJpaRepository.findAll().mapNotNull { entity ->
            val featureFlag = featureFlags[entity.featureFlagId] ?: return@mapNotNull null
            entity.toDomain(featureFlag)
        }
    }
}