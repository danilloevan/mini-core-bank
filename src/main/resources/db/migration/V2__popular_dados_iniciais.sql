-- ============================================================================
-- V2: CARGA INICIAL DE DADOS PARA DEMONSTRACAO E TESTES IMEDIATOS
-- ============================================================================

-- 1. Agencias
INSERT INTO agencias (id, codigo, nome, cidade) VALUES
(1, '0001', 'Agência Campus Pici', 'Fortaleza'),
(2, '0002', 'Agência Benfica', 'Fortaleza');

-- 2. Clientes (2 Pessoas Fisicas e 1 Pessoa Juridica)
INSERT INTO clientes (id, tipo_cliente, nome, documento, email, data_nascimento, razao_social, logradouro, cidade, uf, cep) VALUES
(1, 'PF', 'Maria Silva', '12345678909', 'maria.silva@email.com', '1998-05-14', NULL, 'Av. Humberto Monte, 2977', 'Fortaleza', 'CE', '60440-593'),
(2, 'PF', 'João Souza', '98765432100', 'joao.souza@email.com', '1995-10-22', NULL, 'Av. da Universidade, 2853', 'Fortaleza', 'CE', '60020-181'),
(3, 'PJ', 'Tech Ceará Soluções', '11222333000181', 'contato@techceara.com.br', NULL, 'Tech Ceará Soluções Digitais LTDA', 'Rua Sena Madureira, 1000', 'Fortaleza', 'CE', '60055-080');

-- 3. Contas Bancarias ja com saldo inicial
INSERT INTO contas (id, tipo_conta, numero_conta, saldo, status, cliente_id, agencia_id) VALUES
(1, 'CORRENTE', '00001-9', 3500.00, 'ATIVA', 1, 1),
(2, 'CORRENTE', '00002-7', 1200.00, 'ATIVA', 2, 2),
(3, 'EMPRESARIAL', '00003-5', 25000.00, 'ATIVA', 3, 1);

-- 4. Chaves PIX pre-cadastradas
INSERT INTO chaves_pix (id, tipo, valor, conta_id) VALUES
(1, 'CPF', '12345678909', 1),
(2, 'EMAIL', 'joao.souza@email.com', 2),
(3, 'CNPJ', '11222333000181', 3);

-- 5. Transacoes iniciais com comprovantes
INSERT INTO transacoes (
    id, tipo_transacao, valor, tarifa, data_hora, descricao,
    conta_origem_id, conta_destino_id,
    canal_deposito, terminal_atm, chave_pix_destino, end_to_end_id, codigo_banco_destino,
    codigo_autenticacao, resumo_operacao, comprovante_emitido_em
) VALUES
(1, 'DEPOSITO', 3700.00, 0.00, '2026-11-20 09:00:00', 'Depósito inicial de abertura de conta',
 NULL, 1, 'AGENCIA_CAIXA', NULL, NULL, NULL, NULL,
 'AUTH-DEP-0001', 'DEPOSITO de R$ 3700.00 (Tarifa: R$ 0.00)', '2026-11-20 09:00:00'),

(2, 'DEPOSITO', 1000.00, 0.00, '2026-11-20 09:30:00', 'Depósito inicial via terminal',
 NULL, 2, 'ATM_AUTOATENDIMENTO', NULL, NULL, NULL, NULL,
 'AUTH-DEP-0002', 'DEPOSITO de R$ 1000.00 (Tarifa: R$ 0.00)', '2026-11-20 09:30:00'),

(3, 'DEPOSITO', 25000.00, 0.00, '2026-11-20 10:00:00', 'Aporte de capital social',
 NULL, 3, 'TED_CORPORATIVO', NULL, NULL, NULL, NULL,
 'AUTH-DEP-0003', 'DEPOSITO de R$ 25000.00 (Tarifa: R$ 0.00)', '2026-11-20 10:00:00'),

(4, 'PIX', 200.00, 0.00, '2026-11-21 14:15:00', 'Transferência PIX para João Souza',
 1, 2, NULL, NULL, 'joao.souza@email.com', 'E12320261121141500001', NULL,
 'AUTH-PIX-0004', 'PIX de R$ 200.00 (Tarifa: R$ 0.00)', '2026-11-21 14:15:00');

-- Ajusta as sequences para que novos registros inseridos pelo JPA nao conflitem com os IDs do seed
ALTER TABLE agencias ALTER COLUMN id RESTART WITH 10;
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 10;
ALTER TABLE contas ALTER COLUMN id RESTART WITH 10;
ALTER TABLE chaves_pix ALTER COLUMN id RESTART WITH 10;
ALTER TABLE transacoes ALTER COLUMN id RESTART WITH 10;
