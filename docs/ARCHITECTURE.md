# LabManager LIS — arquitetura

## Objetivo

Transformar o aplicativo em uma base de LIS (Laboratory Information System) com separação entre apresentação, domínio, persistência local e futura API de servidor.

## Camadas

```text
UI (Activities / futura Compose)
        |
ViewModel / casos de uso
        |
LabRepository
        |
+-------+----------------+
|                        |
Room / SQLite         API REST
local-first           servidor
|                        |
+-----------+------------+
            |
       PostgreSQL
```

## Persistência

O núcleo de dados foi iniciado com Room e entidades tipadas para pacientes, pedidos, amostras, resultados, estoque, usuários e auditoria. A aplicação não deve depender de `SharedPreferences` para o banco operacional definitivo.

## Segurança e autorização

Papéis previstos: administrador, gestor, recepção, coleta, triagem, técnico, responsável técnico e financeiro. As permissões ficam centralizadas em `LisAccess` para que a UI não decida autorização por conta própria.

Em produção, autenticação e autorização devem ser validadas no servidor. O aplicativo nunca deve ser a única barreira de segurança.

## Rastreabilidade

Eventos de auditoria devem registrar usuário, ação, entidade, identificador, data/hora e dispositivo. Alterações de resultado e liberações precisam ser auditáveis e não devem apagar o histórico anterior.

## Próxima etapa de produção

1. Migrar completamente os fluxos da `LisActivity` de JSON/SharedPreferences para Room.
2. Criar backend autenticado com API REST e PostgreSQL.
3. Implementar JWT/refresh token, RBAC no servidor e TLS.
4. Adicionar sincronização offline/online e controle de conflitos.
5. Implementar catálogo de exames, referências por perfil, lotes/reagentes, CQI, Delta Check e resultados críticos.
6. Adicionar barcode, impressão de etiquetas, interfaceamento de equipamentos e integrações externas.
7. Implementar backups, observabilidade, testes automatizados e política LGPD.

> Esta arquitetura é uma base técnica de evolução. O aplicativo continua sendo um protótipo até que autenticação, backend, validações, segurança, testes e requisitos regulatórios sejam concluídos.
