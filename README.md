# Mini Core Bank — Sistema Bancário Simplificado (POO UFC)

Projeto final da disciplina **SMD0017 — Programação Orientada a Objetos** (Universidade Federal do Ceará — Prof. Gilvan Maia).

Esta aplicação implementa um núcleo bancário 100% síncrono utilizando **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **PostgreSQL** e **Flyway Migrations**, com foco na aplicação clara dos pilares de **Orientação a Objetos**:

- **3 Hierarquias de Herança (`extends`):**
  - `Cliente` (abstrata) $\rightarrow$ `PessoaFisica` e `PessoaJuridica`
  - `Conta` (abstrata) $\rightarrow$ `ContaCorrente` e `ContaEmpresarial`
  - `Transacao` (abstrata) $\rightarrow$ `TransacaoDeposito`, `TransacaoSaque`, `TransacaoPix` e `TransacaoTed`
- **4 Tipos de Associações UML:**
  - **Composição:** `Cliente *-- Endereco` e `Transacao *-- ComprovanteTransacao`
  - **Agregação:** `Agencia o-- Conta`
  - **Associação Simples:** `Conta --> Cliente`, `ChavePix --> Conta`, `Transacao --> Conta`
  - **Dependência:** `Transacao ..> CalculadoraTarifa` e `ContaService ..> ExportadorExtrato`
- **Polimorfismo, Classes Abstratas, Interfaces, Encapsulamento (`public`, `protected`, `private`) e Membros `static`**.

---

## Execução Rápida

```bash
# 1. Subir o banco PostgreSQL
docker compose up -d

# 2. Iniciar a aplicação (o Flyway cria as tabelas em V1 e já popula os dados iniciais em V2)
mvn spring-boot:run
```

Acesse o **Swagger UI** em: **http://localhost:8080/swagger-ui.html**

---

## Documentação do Projeto

- [Manual de Execução e Roteiro de Testes](MANUAL_EXECUCAO.md)
- [Identificação da Equipe e Mapeamento de Implementação](EQUIPE.txt)
- [Diagrama de Classes UML (Vetorial SVG)](docs/diagrama-classes.svg)
- [Auditoria dos Critérios de POO do Edital](docs/AUDITORIA_CRITERIOS_POO.md)
