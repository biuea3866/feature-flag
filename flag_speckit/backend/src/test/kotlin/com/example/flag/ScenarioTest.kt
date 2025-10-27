package com.example.flag

import com.example.flag.domain.Attribute
import com.example.flag.domain.Rule
import com.example.flag.domain.Workspace
import com.example.flag.repository.WorkspaceRepository
import com.example.flag.service.EvaluationService
import com.example.flag.service.FlagService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class ScenarioTest {

    @Autowired
    private lateinit var flagService: FlagService

    @Autowired
    private lateinit var evaluationService: EvaluationService

    @Autowired
    private lateinit var workspaceRepository: WorkspaceRepository

    @Test
    fun `scenario - attribute-based feature flag for different workspaces`() {
        // 1. Create a new feature flag
        val flagName = "premium-feature"
        val flag = flagService.createFlag(flagName, "A feature for premium workspaces")
        println("✅ Feature flag '$flagName' created.")

        // 2. Create two workspaces
        val workspaceA = Workspace(name = "workspace-A")
        val workspaceB = Workspace(name = "workspace-B")
        workspaceRepository.saveAll(listOf(workspaceA, workspaceB))
        println("✅ Workspaces 'workspace-A' and 'workspace-B' created.")

        // 3. Add a rule to the flag
        val rule = Rule(
            featureFlag = flag,
            type = "ATTRIBUTE_BASED",
            percentage = null,
            attributeName = "plan",
            attributeValue = "premium"
        )
        flagService.addRule(flagName, rule)
        println("✅ Rule added to '$flagName': Enable when attribute 'plan' is 'premium'.")

        // 4. Assign an attribute to workspace-A
        val attribute = Attribute(workspace = workspaceA, key = "plan", value = "premium")
        val updatedWorkspaceA = workspaceA.copy(attributes = listOf(attribute))
        workspaceRepository.save(updatedWorkspaceA)
        println("✅ Attribute 'plan=premium' assigned to 'workspace-A'.")

        // 5. Evaluate the flag for both workspaces
        println("\n--- SCENARIO RESULTS ---")

        // Workspace A (should be enabled)
        val resultA = evaluationService.evaluate(flagName, workspaceA.id.toString())
        println("  - Workspace 'workspace-A' (plan=premium): Flag is ${if (resultA) "ENABLED" else "DISABLED"}")
        assertEquals(true, resultA)

        // Workspace B (should be disabled)
        val resultB = evaluationService.evaluate(flagName, workspaceB.id.toString())
        println("  - Workspace 'workspace-B' (plan=default): Flag is ${if (resultB) "ENABLED" else "DISABLED"}")
        assertEquals(false, resultB)

        println("------------------------\n")
    }
}
