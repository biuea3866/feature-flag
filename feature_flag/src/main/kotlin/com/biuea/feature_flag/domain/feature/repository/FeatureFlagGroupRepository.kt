package com.biuea.feature_flag.domain.feature.repository

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagGroup

interface FeatureFlagGroupRepository {
    fun save(entity: FeatureFlagGroup): FeatureFlagGroup
    fun getFeatureFlagGroupOrNullBy(id: Long): FeatureFlagGroup?
    fun getFeatureFlagGroupOrNullBy(feature: Feature): FeatureFlagGroup?
    fun getFeatureFlagGroups(): List<FeatureFlagGroup>
    fun delete(entity: FeatureFlagGroup)
}