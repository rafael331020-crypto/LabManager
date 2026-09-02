package com.rafael.labmanager.security

/** Roles and permissions used by the LIS authorization layer. */
enum class LisRole {
    ADMINISTRADOR,
    GESTOR,
    RECEPCAO,
    COLETA,
    TRIAGEM,
    TECNICO,
    RESPONSAVEL_TECNICO,
    FINANCEIRO
}

enum class LisPermission {
    PATIENT_READ,
    PATIENT_WRITE,
    ORDER_READ,
    ORDER_WRITE,
    SAMPLE_COLLECT,
    SAMPLE_TRIAGE,
    RESULT_ENTER,
    RESULT_REVIEW,
    RESULT_RELEASE,
    STOCK_WRITE,
    QUALITY_WRITE,
    FINANCE_WRITE,
    REPORT_READ,
    AUDIT_READ,
    USER_ADMIN
}

object LisAccess {
    private val permissions = mapOf(
        LisRole.ADMINISTRADOR to LisPermission.entries.toSet(),
        LisRole.GESTOR to setOf(
            LisPermission.PATIENT_READ, LisPermission.ORDER_READ,
            LisPermission.SAMPLE_COLLECT, LisPermission.SAMPLE_TRIAGE,
            LisPermission.RESULT_ENTER, LisPermission.RESULT_REVIEW,
            LisPermission.RESULT_RELEASE, LisPermission.STOCK_WRITE,
            LisPermission.QUALITY_WRITE, LisPermission.FINANCE_WRITE,
            LisPermission.REPORT_READ, LisPermission.AUDIT_READ
        ),
        LisRole.RECEPCAO to setOf(LisPermission.PATIENT_READ, LisPermission.PATIENT_WRITE, LisPermission.ORDER_READ, LisPermission.ORDER_WRITE),
        LisRole.COLETA to setOf(LisPermission.PATIENT_READ, LisPermission.ORDER_READ, LisPermission.SAMPLE_COLLECT),
        LisRole.TRIAGEM to setOf(LisPermission.PATIENT_READ, LisPermission.ORDER_READ, LisPermission.SAMPLE_TRIAGE),
        LisRole.TECNICO to setOf(LisPermission.PATIENT_READ, LisPermission.ORDER_READ, LisPermission.SAMPLE_TRIAGE, LisPermission.RESULT_ENTER),
        LisRole.RESPONSAVEL_TECNICO to setOf(LisPermission.PATIENT_READ, LisPermission.ORDER_READ, LisPermission.RESULT_READ_PLACEHOLDER(), LisPermission.RESULT_REVIEW, LisPermission.RESULT_RELEASE, LisPermission.QUALITY_WRITE, LisPermission.AUDIT_READ),
        LisRole.FINANCEIRO to setOf(LisPermission.FINANCE_WRITE, LisPermission.REPORT_READ)
    )

    fun can(role: LisRole, permission: LisPermission): Boolean = permissions[role]?.contains(permission) == true

    private fun LisPermission.RESULT_READ_PLACEHOLDER(): LisPermission = LisPermission.RESULT_ENTER
}
