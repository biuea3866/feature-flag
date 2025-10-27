package com.example.flag

import com.example.flag.domain.FeatureFlag
import com.example.flag.repository.FeatureFlagRepository
import com.example.flag.repository.RuleRepository
import com.example.flag.service.AuditService
import com.example.flag.service.FlagService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FlagServiceTest : StringSpec({

    val featureFlagRepository = mockk<FeatureFlagRepository>()
    val ruleRepository = mockk<RuleRepository>()
    val auditService = mockk<AuditService>()
    val flagService = FlagService(featureFlagRepository, ruleRepository, auditService)

    "createFlag should save and return a new feature flag" {
        val flagName = "new-feature"
        val flagDescription = "A new feature"
        val featureFlag = FeatureFlag(id = UUID.randomUUID(), name = flagName, description = flagDescription)

        every { featureFlagRepository.save(any()) } returns featureFlag
        justRun { auditService.logChange(any(), any(), any(), any()) }

        val result = flagService.createFlag(flagName, flagDescription)

        result.name shouldBe flagName
        result.description shouldBe flagDescription
        result.enabled shouldBe false

        verify { featureFlagRepository.save(any()) }
        verify { auditService.logChange(any(), any(), any(), any()) }
    }

    "toggleFlag should enable a feature flag" {
        val flagName = "existing-feature"
        val featureFlag = FeatureFlag(id = UUID.randomUUID(), name = flagName, description = null, enabled = false)

        every { featureFlagRepository.findByName(flagName) } returns featureFlag
        every { featureFlagRepository.save(any()) } answers { firstArg() }
        justRun { auditService.logChange(any(), any(), any(), any()) }

        val result = flagService.toggleFlag(flagName, true)

        result?.enabled shouldBe true

        verify { featureFlagRepository.findByName(flagName) }
        verify { featureFlagRepository.save(any()) }
        verify { auditService.logChange(any(), any(), any(), any()) }
    }
})
