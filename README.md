# Ônibus Horários API

API REST para consultar linhas e horários de ônibus de Águas Lindas de Goiás para o Distrito Federal, com importação dos dados públicos da ANTT.

## Stack

- Java 25 e Spring Boot 4
- Spring MVC, Spring Data JPA, Validation e Spring Security com JWT
- PostgreSQL 17 e Flyway
- MapStruct
- Maven e Testcontainers

## Requisitos

- JDK 25
- Docker Desktop ou Docker Engine com Docker Compose

## Ambiente local

Na raiz do projeto, crie seu arquivo de ambiente a partir do exemplo:

```powershell
Copy-Item .env.example .env
```

Edite `.env` com suas credenciais de desenvolvimento. Para gerar um segredo JWT aleatório no PowerShell:

```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
[Convert]::ToBase64String($bytes)
```

No Bash, use `openssl rand -base64 32`. Copie o resultado para `ADMIN_JWT_SECRET` no `.env`. O `.env` é ignorado pelo Git; não compartilhe credenciais reais.

Suba o PostgreSQL com o Compose da raiz do projeto:

```powershell
docker compose up -d
```

O banco fica disponível em `localhost:5455`, no database `onibus_db`. O Compose lê `.env` automaticamente. Os valores padrão definidos no Compose são apenas para desenvolvimento local.

Para executar a API no PowerShell, carregue as variáveis do `.env` no processo antes de iniciar o Maven:

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
    }
}
.\mvnw.cmd spring-boot:run
```

A API inicia em `http://localhost:8087`. As migrations do Flyway são aplicadas automaticamente.

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `DB_USER` | Usuário do PostgreSQL |
| `DB_PASSWORD` | Senha do PostgreSQL |
| `ADMIN_USERNAME` | Usuário administrativo da API |
| `ADMIN_PASSWORD` | Senha administrativa |
| `ADMIN_JWT_SECRET` | Chave Base64 com pelo menos 32 bytes |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas para o front-end; configuração planejada |

Os testes usam PostgreSQL via Testcontainers e valores fictícios em `src/test/resources/application.yaml`; não dependem das variáveis de ambiente de desenvolvimento.

## Endpoints disponíveis

| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Público | Autentica o administrador e retorna JWT |
| `GET` | `/api/v1/linhas` | Público | Lista linhas ativas; filtros `bairroOrigem`, `terminalDestino`, `pagina` e `tamanho` |
| `GET` | `/api/v1/linhas/{id}` | Público | Consulta uma linha ativa |
| `POST` | `/api/v1/linhas` | ADMIN | Cadastra uma linha |
| `PUT` | `/api/v1/linhas/{id}` | ADMIN | Atualiza uma linha |
| `DELETE` | `/api/v1/linhas/{id}` | ADMIN | Desativa uma linha (soft delete) |
| `GET` | `/api/v1/linhas/{linhaId}/horarios` | Público | Lista horários ativos da linha; filtros `sentido` e `tipoDia` |
| `GET` | `/api/v1/horarios` | Público | Busca horários por origem, destino, código, bairro, sentido e tipo de dia |
| `GET` | `/api/v1/horarios/proximas-saidas` | Público | Busca as próximas saídas para um trajeto |
| `POST` | `/api/v1/admin/importacoes/antt` | ADMIN | Importa arquivo CSV da ANTT; campo multipart `arquivo` |
| `GET` | `/actuator/health` | Público | Verifica a saúde da aplicação |

Para rotas protegidas, envie o token no cabeçalho `Authorization: Bearer <token>`. Valores aceitos para `sentido`: `IDA` e `VOLTA`; para `tipoDia`: `DIA_UTIL`, `SABADO` e `DOMINGO_FERIADO`.

Exemplo de busca:

```text
GET http://localhost:8087/api/v1/horarios?origem=AGUAS%20LINDAS%20DE%20GOIAS&destino=W3&sentido=IDA&tipoDia=DIA_UTIL
```

## Importação do CSV da ANTT

1. Obtenha na ANTT o arquivo de dados de horários em CSV.
2. Faça login em `POST /api/v1/auth/login` com `ADMIN_USERNAME` e `ADMIN_PASSWORD`.
3. Envie o CSV como `multipart/form-data`, no campo `arquivo`, para `POST /api/v1/admin/importacoes/antt` com o token Bearer.

Exemplo com `curl`:

```bash
curl -X POST http://localhost:8087/api/v1/admin/importacoes/antt \
  -H "Authorization: Bearer <token>" \
  -F "arquivo=@horarios.csv"
```

A importação considera as rotas de Águas Lindas de Goiás e é idempotente para horários já cadastrados.

## Testes

Com Docker disponível, execute a suíte completa:

```powershell
.\mvnw.cmd -B verify
```

Os testes de integração iniciam PostgreSQL 17 com Testcontainers.
