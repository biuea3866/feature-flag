package com.biuea.feature_flag.infrastructure.feature.jpa

import com.biuea.feature_flag.domain.feature.entity.Feature
import org.springframework.data.jpa.repository.JpaRepository

interface FeatureFlagGroupJpaRepository: JpaRepository<FeatureFlagGroupEntity, Long> {
    fun findByFeatureFlagId(featureFlagId: Long): FeatureFlagGroupEntity?
}