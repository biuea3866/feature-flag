package com.biuea.feature_flag.domain.feature.repository

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag

interface FeatureFlagRepository {
    fun save(entity: FeatureFlag): FeatureFlag

    fun getFeatureFlags(): List<FeatureFlag>

    fun getFeatureFlagBy(feature: Feature): FeatureFlag
}