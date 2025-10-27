package com.example.flag.controller

import com.example.flag.service.EvaluationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class BusinessController(private val evaluationService: EvaluationService) {

    @GetMapping("/checkout")
    fun checkout(@RequestParam workspaceId: String): String {
        val newCheckoutExperienceEnabled = evaluationService.evaluate("new-checkout-experience", workspaceId)

        if (newCheckoutExperienceEnabled) {
            // Logic for new checkout experience
            return "Processing order with new checkout experience for workspace: $workspaceId"
        } else {
            // Logic for old checkout experience
            return "Processing order with old checkout experience for workspace: $workspaceId"
        }
    }

    @GetMapping("/dynamic-checkout")
    fun dynamicCheckout(@RequestParam workspaceId: String): String {
        val allFlags = evaluationService.evaluateAll(workspaceId)
        val response = StringBuilder("Dynamic checkout for workspace: $workspaceId\\n")

        allFlags.forEach { (flagName, isEnabled) ->
            if (flagName == "new-checkout-experience" && isEnabled) {
                response.append("    Applying new checkout experience logic.\\n")
            }
            if (flagName == "free-shipping" && isEnabled) {
                response.append("    Applying free shipping logic.\\n")
            }
            // Add more dynamic logic based on other flags
        }
        return response.toString()
    }
}
