package com.biuea.feature_flag.presentation.applicant

import com.biuea.feature_flag.application.applicant.ApplicantFacade
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicantApiController(
    private val applicantFacade: ApplicantFacade
) {
    @GetMapping("/applicants/business")
    fun executeBusiness(@RequestHeader("X-Workspace-Id") workspaceId: Int): ResponseEntity<Unit> {
        applicantFacade.execute(workspaceId)

        return ResponseEntity.ok().build()
    }
}