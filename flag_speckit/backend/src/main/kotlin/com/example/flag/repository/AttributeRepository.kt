package com.example.flag.repository

import com.example.flag.domain.Attribute
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttributeRepository : JpaRepository<Attribute, UUID>
