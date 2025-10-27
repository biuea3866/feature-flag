package com.example.flag.repository

import com.example.flag.domain.Workspace
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface WorkspaceRepository : JpaRepository<Workspace, UUID>
