package com.biuea.feature_flag.domain.applicant

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicantService {
    @Transactional
    fun aiScreeningFeature() {
        println("AI feature executed")
    }

    @Transactional
    fun applicantEvaluatorFeature() {
        println("Applicant Evaluator feature executed")
    }

    @Transactional
    fun commonBusiness() {
        println("Common Business executed")
    }
}