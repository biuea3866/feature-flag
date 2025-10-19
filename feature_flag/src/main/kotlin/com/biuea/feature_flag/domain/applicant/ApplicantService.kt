package com.biuea.feature_flag.domain.applicant

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicantService {
    @Transactional
    fun aiScreeningFeature() {
        println("AI feature executed")
        commonBusiness()
    }

    @Transactional
    fun applicantEvaluatorFeature() {
        println("Applicant Evaluator feature executed")
        commonBusiness()
    }

    @Transactional
    fun commonBusiness() {
        println("Common Business executed")
    }
}