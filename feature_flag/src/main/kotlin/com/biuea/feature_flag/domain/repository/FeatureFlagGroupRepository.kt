package com.biuea.feature_flag.domain.repository

import com.biuea.feature_flag.domain.entity.Feature
import com.biuea.feature_flag.domain.entity.FeatureFlagGroup

interface FeatureFlagGroupRepository {
    fun save(entity: FeatureFlagGroup): FeatureFlagGroup
    fun getOrNullBy(name: String): FeatureFlagGroup?
    fun getAllBy(feature: Feature): List<FeatureFlagGroup>
}