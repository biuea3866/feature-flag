package com.biuea.feature_flag.domain.entity

import com.biuea.feature_flag.domain.entity.FeatureFlagGroup.Companion.MAX_SPECIFIC_SIZE

interface FeatureFlagAlgorithm {
    fun isEnable(workspaceId: Int): Boolean
}

object FeatureFlagAlgorithmDecider {
    fun decide(
        algorithm: FeatureFlagAlgorithmOption,
        specifics: List<Int>?,
        percentage: Int?,
        absolute: Int?
    ): FeatureFlagAlgorithm {
        return when (algorithm) {
            FeatureFlagAlgorithmOption.SPECIFIC -> {
                if (specifics.isNullOrEmpty()) {
                    throw IllegalArgumentException("specifics is required for SPECIFIC option")
                }

                SpecificAlgorithm(specifics)
            }
            FeatureFlagAlgorithmOption.PERCENT -> {
                if (percentage == null) {
                    throw IllegalArgumentException("percentage is required for PERCENT option")
                }

                RandomPercentAlgorithm(percentage)
            }
            FeatureFlagAlgorithmOption.ABSOLUTE -> {
                if (absolute == null) {
                    throw IllegalArgumentException("absolute is required for ABSOLUTE option")
                }

                RandomAbsoluteAlgorithm(absolute)
            }
        }
    }
}

class SpecificAlgorithm(
    val specifics: List<Int>
): FeatureFlagAlgorithm {
    override fun isEnable(workspaceId: Int): Boolean {
        if (MAX_SPECIFIC_SIZE < this.specifics.size) {
            throw IllegalArgumentException("specifics size exceeds the maximum limit of $MAX_SPECIFIC_SIZE")
        }

        return this.specifics.contains(workspaceId)
    }
}

class RandomPercentAlgorithm(
    val percentage: Int
) : FeatureFlagAlgorithm {
    override fun isEnable(workspaceId: Int): Boolean {
        return workspaceId % 100 <= this.percentage
    }
}

class RandomAbsoluteAlgorithm(
    val absolute: Int
) : FeatureFlagAlgorithm {
    override fun isEnable(workspaceId: Int): Boolean {
        return workspaceId <= this.absolute
    }
}

enum class FeatureFlagAlgorithmOption {
    SPECIFIC,
    PERCENT,
    ABSOLUTE,
}