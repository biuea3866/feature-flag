package com.biuea.feature_flag.domain.entity

interface FeatureFlagGroupAlgorithm {
    fun decideGroup(): List<Int>

    companion object {
        const val MAX_SPECIFIC_SIZE: Int = 10000
    }
}

object FeatureFlagAlgorithmDecider {
    fun from(
        algorithm: FeatureFlagGroupAlgorithmOption,
        workspaceIds: List<Int>?,
        percentage: Int?,
        absolute: Int?
    ): FeatureFlagGroupAlgorithm {
        return when (algorithm) {
            FeatureFlagGroupAlgorithmOption.SPECIFIC_WORKSPACES -> {
                if (workspaceIds.isNullOrEmpty()) {
                    throw IllegalArgumentException("workspaceIds is required for SPECIFIC_WORKSPACES option")
                }

                SpecificWorkspacesAlgorithm(workspaceIds)
            }
            FeatureFlagGroupAlgorithmOption.RANDOM_PERCENT_WORKSPACE -> {
                if (percentage == null || workspaceIds.isNullOrEmpty()) {
                    throw IllegalArgumentException("percentage is required for RANDOM_PERCENT_WORKSPACE option")
                }

                RandomPercentWorkspaceAlgorithm(percentage, workspaceIds)
            }
            FeatureFlagGroupAlgorithmOption.RANDOM_ABSOLUTE_WORKSPACE -> {
                if (absolute == null || workspaceIds.isNullOrEmpty()) {
                    throw IllegalArgumentException("absolute is required for RANDOM_ABSOLUTE_WORKSPACE option")
                }

                RandomAbsoluteWorkspaceAlgorithm(absolute, workspaceIds)
            }
        }
    }
}

class SpecificWorkspacesAlgorithm(
    val workspaceIds: List<Int>
): FeatureFlagGroupAlgorithm {
    override fun decideGroup(): List<Int> {
        if (FeatureFlagGroupAlgorithm.MAX_SPECIFIC_SIZE < this.workspaceIds.size) {
            throw IllegalArgumentException("workspaceIds size exceeds the maximum limit of ${FeatureFlagGroupAlgorithm.MAX_SPECIFIC_SIZE}")
        }

        return this.workspaceIds
    }
}

class RandomPercentWorkspaceAlgorithm(
    val percentage: Int,
    val workspaceIds: List<Int>
) : FeatureFlagGroupAlgorithm {
    override fun decideGroup(): List<Int> {
        return this.workspaceIds.shuffled().take(this.workspaceIds.size * this.percentage / 100)
    }
}

class RandomAbsoluteWorkspaceAlgorithm(
    val absolute: Int,
    val workspaceIds: List<Int>
) : FeatureFlagGroupAlgorithm {
    override fun decideGroup(): List<Int> {
        return this.workspaceIds.shuffled().take(this.absolute)
    }
}

enum class FeatureFlagGroupAlgorithmOption {
    SPECIFIC_WORKSPACES,
    RANDOM_PERCENT_WORKSPACE,
    RANDOM_ABSOLUTE_WORKSPACE,
}