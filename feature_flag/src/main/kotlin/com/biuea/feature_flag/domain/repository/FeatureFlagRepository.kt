package com.biuea.feature_flag.domain.repository

import com.biuea.feature_flag.domain.entity.Feature
import com.biuea.feature_flag.domain.entity.FeatureFlag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface FeatureFlagRepository {
    fun save(entity: FeatureFlag): FeatureFlag

    fun getAll(): List<FeatureFlag>

    fun getBy(feature: Feature): FeatureFlag
}