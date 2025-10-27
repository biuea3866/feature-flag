package com.example.flag.controller

import com.example.flag.domain.FeatureFlag
import com.example.flag.domain.Rule
import com.example.flag.service.FlagService
import org.springframework.web.bind.annotation.*

data class CreateFlagRequest(val name: String, val description: String?)
data class UpdateFlagRequest(val enabled: Boolean)
data class CreateRuleRequest(val type: String, val percentage: Int?, val attributeName: String?, val attributeValue: String?)

@RestController
@RequestMapping("/flags")
class FlagController(private val flagService: FlagService) {

    @PostMapping
    fun createFlag(@RequestBody request: CreateFlagRequest): FeatureFlag {
        return flagService.createFlag(request.name, request.description)
    }

    @PutMapping("/{flag_name}")
    fun toggleFlag(@PathVariable flag_name: String, @RequestBody request: UpdateFlagRequest): FeatureFlag? {
        return flagService.toggleFlag(flag_name, request.enabled)
    }

    @GetMapping("/{flag_name}")
    fun getFlag(@PathVariable flag_name: String): FeatureFlag? {
        return flagService.findByName(flag_name)
    }

    @PostMapping("/{flag_name}/rules")
    fun addRule(@PathVariable flag_name: String, @RequestBody request: CreateRuleRequest): Rule? {
        val rule = Rule(type = request.type, percentage = request.percentage, attributeName = request.attributeName, attributeValue = request.attributeValue)
        return flagService.addRule(flag_name, rule)
    }
}
