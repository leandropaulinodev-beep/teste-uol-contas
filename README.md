# API de Banco Digital (TESTE)

API para banco digital simplificado. Permite cadastro de contas, transferências entre elas e consulta de extrato.

## Tecnologias

- Java 17
- Spring Boot 3.4.1
- H2 (banco em memória)
- Lombok
- README em MD
- Maven

## Como rodar

```bash
mvn spring-boot:run
```

Acesse:
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:bancodb`, user `sa`, sem senha)

## Testes

```bash
mvn test
```

Inclui testes unitários e teste de concorrência com 10 threads simultâneas.

## Endpoints

| Método | Rota | O que faz |
|--------|------|-----------|
| POST | `/api/contas` | Criar conta |
| GET | `/api/contas` | Listar contas |
| GET | `/api/contas/{id}` | Buscar conta |
| POST | `/api/transferencias` | Transferir entre contas |
| GET | `/api/contas/{id}/transacoes` | Extrato da conta |

## Exemplos

Criar conta:
```json
POST /api/contas
{ "nome": "Gollum", "saldo": 1500.00 }
```

Transferir:
```json
POST /api/transferencias
{ "contaOrigemId": 1, "contaDestinoId": 2, "valor": 200.00 }
```

## Dados iniciais

O sistema sobe com 3 contas:

| ID | Nome | Saldo |
|----|------|-------|
| 1 | Gandalf | 1000.00 |
| 2 | Bilbo   | 2500.00 |
| 3 | Frodo   | 500.00 |

## Decisões técnicas

**Banco H2** - Não é preciso instalar nada. Sobe junto com a aplicação e morre junto. Apenas para teste, mas pode ser implementado.

**Lock pessimista com ordenação de IDs** - As transferências travam as contas em ordem crescente de ID pra evitar deadlock quando duas transferências concorrentes envolvem as mesmas contas. 

**Notificações assíncronas** - Depois de completar a transferência, as notificações são disparadas com `@Async` pra não travar a resposta pro cliente. Ficam salvas na tabela `notification` pra auditoria.

**Lombok** - Reduz boilerplate. `@Data` nos DTOs, `@Getter/@Setter` nas entidades (evitando `@Data` por causa do `equals/hashCode` do JPA), `@RequiredArgsConstructor` nos services e controllers.
