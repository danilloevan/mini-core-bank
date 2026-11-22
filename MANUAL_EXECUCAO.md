# Manual de Execução e Testes — Mini Core Bank (POO UFC)

Sistema Bancário Simplificado desenvolvido em **Java 21 + Spring Boot 3 + PostgreSQL + Flyway** para a disciplina **SMD0017 — Programação Orientada a Objetos (UFC)**.

---

## 1. Pré-requisitos

- **Java 21** instalado (`java -version`)
- **Maven 3.9+** instalado (`mvn -version`)
- **Docker** e **Docker Compose** ativos (para subir o banco PostgreSQL)
- **Postman** instalado (para importar a collection de testes) ou navegador web (para usar o Swagger UI)

---

## 2. Como Subir o Banco de Dados e a Aplicação

### Passo 1: Iniciar o PostgreSQL via Docker
Na raiz do projeto, execute:

```bash
docker compose up -d
```

### Passo 2: Executar a Aplicação Spring Boot
Ainda na raiz do projeto, execute:

```bash
mvn spring-boot:run
```

A API estará disponível em **`http://localhost:8080`**.

> **Nota sobre as Migrations (Flyway):**
> Ao iniciar, o Flyway cria automaticamente as tabelas (`V1__criar_tabelas.sql`) e já insere dados iniciais (`V2__popular_dados_iniciais.sql`) para teste imediato:
> - **Agências:** `0001` (Campus Pici) e `0002` (Benfica)
> - **Conta ID `1` (`CORRENTE`):** Maria Silva (`PF`, isenta de tarifa PIX) — Saldo: **R$ 3.500,00** — Chave PIX: `12345678909`
> - **Conta ID `2` (`CORRENTE`):** João Souza (`PF`, isento de tarifa PIX) — Saldo: **R$ 1.200,00** — Chave PIX: `joao.souza@email.com`
> - **Conta ID `3` (`EMPRESARIAL`):** Tech Ceará Soluções (`PJ`, tarifa de 1% no PIX até R$ 50,00) — Saldo: **R$ 25.000,00** — Chave PIX: `11222333000181`

---

## 3. Como Importar a Collection no Postman e Testar os Endpoints

Na raiz do projeto o arquivo **`Mini Core Bank.postman_collection.json`** tem todos os endpoints organizados por pasta e pré-configurados com a variável `{{baseUrl}} = http://localhost:8080`.

### Passo 1: Importar o arquivo no Postman
1. Abra o **Postman**.
2. No canto superior esquerdo, clique no botão **Import** (ou pressione `Ctrl + O` no Windows/Linux, `Cmd + O` no Mac).
3. Clique em **files** (ou arraste o arquivo diretamente para a janela de importação) e selecione o arquivo:
   `Mini Core Bank.postman_collection.json` (localizado na raiz do projeto).
4. Confirme a importação. A coleção **Mini Core Bank** aparecerá na barra lateral esquerda em **Collections**.

### Passo 2: Conferir a Variável `baseUrl`
1. Clique no nome da coleção **Mini Core Bank** na barra lateral.
2. Abra a aba **Variables**.
3. Verifique se `baseUrl` está preenchida com `http://localhost:8080` (já vem configurada por padrão).

### Passo 3: Roteiro Sugerido de Testes no Postman
Com a aplicação rodando (`mvn spring-boot:run`), execute as requisições na ordem abaixo:

1. **Pasta `2. Contas e Chaves PIX` $\rightarrow$ `Listar Todas as Contas`** (`GET {{baseUrl}}/api/contas`)
   - Lista as 3 contas criadas automaticamente pelo Flyway, exibindo seus saldos iniciais e os limites de saque polimórficos (`R$ 2.000,00` para `ContaCorrente` e `R$ 10.000,00` para `ContaEmpresarial`).
2. **Pasta `3. Transações Financeiras` $\rightarrow$ `Depósito (Isento de tarifa)`** (`POST {{baseUrl}}/api/transacoes/deposito`)
   - Realiza um depósito de `R$ 1.500,00` na conta `1` com tarifa `0.00` e gera o `ComprovanteTransacao` (composição).
3. **Pasta `3. Transações Financeiras` $\rightarrow$ `Saque via ATM`** (`POST {{baseUrl}}/api/transacoes/saque`)
   - Realiza um saque de `R$ 200,00` na conta `1` validando o limite diário polimórfico da conta.
4. **Pasta `3. Transações Financeiras` $\rightarrow$ `Transferência PIX (PF - Isento)`** (`POST {{baseUrl}}/api/transacoes/pix`)
   - Transfere `R$ 250,00` da conta `1` (`PessoaFisica`) para `joao.souza@email.com` com **tarifa `0.00`**.
5. **Pasta `3. Transações Financeiras` $\rightarrow$ `Transferência PIX (PJ - Tarifa 1% até R$ 50)`** (`POST {{baseUrl}}/api/transacoes/pix`)
   - Transfere `R$ 1.000,00` da conta `3` (`PessoaJuridica`) para `12345678909`. Como o titular é `PJ`, o sistema aplica polimorficamente `TarifaPercentual`, cobrando **tarifa de `R$ 10,00`** (1%).
6. **Pasta `3. Transações Financeiras` $\rightarrow$ `Transferência PIX (Teste Saldo Insuficiente)`** (`POST {{baseUrl}}/api/transacoes/pix`)
   - Demonstra o tratamento da exceção de domínio `SaldoInsuficienteException` (HTTP `422 Unprocessable Entity`).
7. **Pasta `3. Transações Financeiras` $\rightarrow$ `Transferência TED (Tarifa Fixa R$ 10,00)`** (`POST {{baseUrl}}/api/transacoes/ted`)
   - Transfere `R$ 300,00` entre contas aplicando `TarifaFixa` de `R$ 10,00`.
8. **Pasta `4. Extratos` $\rightarrow$ `Exportar Extrato (TEXTO)` e `Exportar Extrato (CSV)`** (`GET {{baseUrl}}/api/contas/1/extrato/exportar?formato=TEXTO` ou `CSV`)
   - Demonstra o despacho polimórfico da interface `ExportadorExtrato` formatando o extrato em texto bancário ou CSV.
9. *(Opcional)* **Pasta `1. Clientes`:** permite cadastrar novos clientes `PF` (`POST /api/clientes/pf`) e `PJ` (`POST /api/clientes/pj`), abrir novas contas (`POST /api/contas`) e cadastrar novas chaves PIX (`POST /api/contas/{id}/chaves-pix`).

---

## 4. Alternativa via Navegador (Swagger UI)

Também é possível testar todos os endpoints diretamente pelo navegador sem precisar do Postman:

- **Swagger UI:** http://localhost:8080/swagger-ui.html

---

## 5. Como Executar os Testes Automatizados

Os testes automatizados validam todas as classes de domínio POO e sobem um banco em memória com as migrations `V1` e `V2` do Flyway (não precisa de Docker ativo para rodar `mvn test`):

```bash
mvn clean test
```
