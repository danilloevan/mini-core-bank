package br.ufc.poo.banco.service;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.cliente.Cliente;
import br.ufc.poo.banco.domain.model.cliente.Endereco;
import br.ufc.poo.banco.domain.model.cliente.PessoaFisica;
import br.ufc.poo.banco.domain.model.cliente.PessoaJuridica;
import br.ufc.poo.banco.dto.ClienteDtos.CriarPessoaFisicaRequest;
import br.ufc.poo.banco.dto.ClienteDtos.CriarPessoaJuridicaRequest;
import br.ufc.poo.banco.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Cliente não encontrado com o ID: " + id));
    }

    @Transactional
    public PessoaFisica cadastrarPessoaFisica(CriarPessoaFisicaRequest request) {
        String cpfLimpo = request.cpf().replaceAll("\\D", "");
        if (clienteRepository.existsByDocumento(cpfLimpo)) {
            throw new NegocioException("Já existe um cliente cadastrado com o CPF: " + cpfLimpo);
        }
        Endereco endereco = new Endereco(request.logradouro(), request.cidade(), request.uf(), request.cep());
        PessoaFisica pf = new PessoaFisica(
                request.nome(),
                cpfLimpo,
                request.email(),
                request.dataNascimento(),
                endereco
        );
        return clienteRepository.save(pf);
    }

    @Transactional
    public PessoaJuridica cadastrarPessoaJuridica(CriarPessoaJuridicaRequest request) {
        String cnpjLimpo = request.cnpj().replaceAll("\\D", "");
        if (clienteRepository.existsByDocumento(cnpjLimpo)) {
            throw new NegocioException("Já existe um cliente cadastrado com o CNPJ: " + cnpjLimpo);
        }
        Endereco endereco = new Endereco(request.logradouro(), request.cidade(), request.uf(), request.cep());
        PessoaJuridica pj = new PessoaJuridica(
                request.nomeFantasia(),
                cnpjLimpo,
                request.email(),
                request.razaoSocial(),
                endereco
        );
        return clienteRepository.save(pj);
    }
}
