package com.biuea.feature_flag.presentation.feature.request

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmOption
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus

data class RegisterFeatureFlagRequest(val feature: Feature)

data class ActivateFeatureFlagRequest(val status: FeatureFlagStatus)

data class RegisterFeatureFlagAlgorithmRequest(
    val status: FeatureFlagStatus,
    val feature: Feature,
    val algorithm: FeatureFlagAlgorithmOption,
    val specifics: List<Int>?,
    val percentage: Int?,
    val absolute: Int?
)