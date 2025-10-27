package com.example.flag.repository

import com.example.flag.domain.Rule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RuleRepository : JpaRepository<Rule, UUID>
