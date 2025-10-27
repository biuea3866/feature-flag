package com.example.flag

import com.example.flag.domain.Attribute
import com.example.flag.domain.FeatureFlag
import com.example.flag.domain.Rule
import com.example.flag.domain.Workspace
import com.example.flag.repository.FeatureFlagRepository
import com.example.flag.repository.WorkspaceRepository
import com.example.flag.service.EvaluationService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.Optional
import java.util.UUID

class EvaluationServiceTest : StringSpec({

    val featureFlagRepository = mockk<FeatureFlagRepository>()
    val workspaceRepository = mockk<WorkspaceRepository>()
    val evaluationService = EvaluationService(featureFlagRepository, workspaceRepository)

    "evaluation should be false for 0 percent rollout" {
        val flagName = "percentage-feature"
        val featureFlag = FeatureFlag(id = UUID.randomUUID(), name = flagName, description = null, enabled = true)
        val rule = Rule(id = UUID.randomUUID(), featureFlag = featureFlag, type = "PERCENTAGE_ROLLOUT", percentage = 0, attributeName = null, attributeValue = null)
        featureFlag.rules = listOf(rule)

        every { featureFlagRepository.findByName(flagName) } returns featureFlag

        val workspace1 = "workspace-1"

        val result1 = evaluationService.evaluate(flagName, workspace1)

        result1 shouldBe false
    }

    "evaluation should be true for 100 percent rollout" {
        val flagName = "percentage-feature"
        val featureFlag = FeatureFlag(id = UUID.randomUUID(), name = flagName, description = null, enabled = true)
        val rule = Rule(id = UUID.randomUUID(), featureFlag = featureFlag, type = "PERCENTAGE_ROLLOUT", percentage = 100, attributeName = null, attributeValue = null)
        featureFlag.rules = listOf(rule)

        every { featureFlagRepository.findByName(flagName) } returns featureFlag

        val workspace1 = "workspace-1"

        val result1 = evaluationService.evaluate(flagName, workspace1)

        result1 shouldBe true
    }

    "evaluation should handle attribute-based targeting" {
        val flagName = "attribute-feature"
        val featureFlag = FeatureFlag(id = UUID.randomUUID(), name = flagName, description = null, enabled = true)
        val rule = Rule(id = UUID.randomUUID(), featureFlag = featureFlag, type = "ATTRIBUTE_BASED", percentage = null, attributeName = "plan", attributeValue = "premium")
        featureFlag.rules = listOf(rule)
        val workspaceId = UUID.randomUUID()
        val workspace = Workspace(id = workspaceId, name = "test-workspace")
        val attribute = Attribute(id = UUID.randomUUID(), workspace = workspace, key = "plan", value = "premium")
        workspace.attributes = listOf(attribute)


        every { featureFlagRepository.findByName(flagName) } returns featureFlag
        every { workspaceRepository.findById(workspaceId) } returns Optional.of(workspace)

        evaluationService.evaluate(flagName, workspaceId.toString()) shouldBe true
    }
})
