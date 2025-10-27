package com.example.flag.controller

import com.example.flag.service.EvaluationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/evaluate")
class EvaluationController(private val evaluationService: EvaluationService) {

    @GetMapping
    fun evaluate(@RequestParam flagName: String, @RequestParam workspaceId: String): Map<String, Boolean> {
        val result = evaluationService.evaluate(flagName, workspaceId)
        return mapOf(flagName to result)
    }
}
