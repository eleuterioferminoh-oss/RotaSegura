package com.rotasegura.main;

import com.rotasegura.exception.*;
import com.rotasegura.model.*;
import com.rotasegura.service.LocadoraService;
import com.rotasegura.service.PersistenciaService;
import com.rotasegura.util.EntradaUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
   - O QUE ACONTECE AQUI?
   - EXIBI UM MENU INTERATIVO
   - LE AS OPÇÕES E DADOS DIGITADOS PELO USUARIO
   - CHAMA OS METODOS DO LocadoraService
   - CAPTURAR EXCECOES E EXIBI MENSAGENS
   
     RESUMINDO
   -  CADASTRAR -> LISTAR -> LOCAÇÃO -> DEVOLVER -> COMPROVANTE -> RELATORIO
 */

public class Main {

    private static LocadoraService service;
    private static EntradaUtil entrada;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("       SISTEMA DE LOCACAO - ROTA SEGURA");
        System.out.println("============================================================");

        // CARREGA O HISTORICO E O EXIBI
        
        PersistenciaService persistencia = new PersistenciaService("data");
        service = new LocadoraService(persistencia);

        Scanner scanner = new Scanner(System.in);
        entrada = new EntradaUtil(scanner);

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            int opcao = entrada.lerInteiro("Escolha uma opcao: ");
            System.out.println();

            // OPÇÕES DO USUARIO
            
            try {
                switch (opcao) {
                    case 1  -> cadastrarCliente();
                    case 2  -> listarClientes();
                    case 3  -> cadastrarVeiculo();
                    case 4  -> listarVeiculos();
                    case 5  -> listarVeiculosDisponiveis();
                    case 6  -> abrirLocacao();
                    case 7  -> devolverVeiculo();
                    case 8  -> listarContratos();
                    case 9  -> imprimirContrato();
                    case 10 -> gerarRelatorio();
                    case 11 -> gerenciarManutencao();
                    case 0  -> {
                        service.salvarTudo();
                        System.out.println("Dados salvos. Encerrando o sistema. Ate logo!");
                        rodando = false;
                    }
                    default -> System.out.println("[AVISO] Opcao invalida. Tente novamente.");
                }
            } catch (Exception e) {
            	
                // MENSAGEM DE ERRO CASO O USUARIO DIGITE ALGO ERRDO
            
                System.out.println("\n*** ERRO TRATADO ***");
                System.out.println("Mensagem: " + e.getMessage());
                System.out.println("(O sistema continua operando normalmente.)\n");
            }

            if (rodando) {
                System.out.println("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /* É O QUE APARECE NO TERMINAL AO EXECUTAR O CODIGO */
    
    private static void exibirMenu() {
        System.out.println("\n------------------------ MENU PRINCIPAL ------------------------");
        System.out.println(" 1. Cadastrar cliente");
        System.out.println(" 2. Listar clientes");
        System.out.println(" 3. Cadastrar veiculo");
        System.out.println(" 4. Listar todos os veiculos");
        System.out.println(" 5. Listar veiculos disponiveis");
        System.out.println(" 6. Abrir nova locacao");
        System.out.println(" 7. Devolver veiculo / Fechar contrato");
        System.out.println(" 8. Listar contratos");
        System.out.println(" 9. Imprimir / Exportar comprovante de contrato");
        System.out.println("10. Relatorio de fechamento");
        System.out.println("11. Gerenciar manutencao de veiculo");
        System.out.println(" 0. Sair e salvar");
        System.out.println("----------------------------------------------------------------");
    }

    // OPCAO 1 e 2 - CLIENTES

    // LE OS DADOS DO USUARIO , CRIA O OBJETO E PEDA PARA CADASTRAR. //
    
    private static void cadastrarCliente() throws ClienteInvalidoException {
        System.out.println("--- Cadastro de Cliente ---");
        String cpf = entrada.lerTexto("CPF (somente numeros): ");
        String nome = entrada.lerTexto("Nome completo: ");
        String telefone = entrada.lerTexto("Telefone: ");
        String email = entrada.lerTexto("E-mail: ");
        int idade = entrada.lerInteiro("Idade: ");

        // VALIDAÇÃO
        
        Cliente cliente = new Cliente(cpf, nome, telefone, email, idade);
        service.cadastrarCliente(cliente);
        System.out.println("\n[OK] Cliente cadastrado com sucesso!");
        System.out.println(cliente);
    }

    private static void listarClientes() {
        List<Cliente> lista = service.listarClientes();
        if (lista.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        System.out.println("--- Clientes cadastrados (" + lista.size() + ") ---");
        lista.forEach(c -> System.out.println("  • " + c));
    }

    // OPCAO 3, 4 e 5 - VEICULOS

    // LE OS DADOS E INSTANCIA A SUBCLASSE CORRETA (Popular/Sedan/SUV)
    // CONFORME A CATEGORIA ESCOLHIDA — É UMA DESMONSTRAÇÃO PRATICA DE HERANCA.
    
    private static void cadastrarVeiculo() throws VeiculoInvalidoException {
        System.out.println("--- Cadastro de Veiculo ---");
        System.out.println("Categorias: 1=Popular | 2=Sedan | 3=SUV");
        int tipo = entrada.lerInteiro("Categoria: ");
        String placa = entrada.lerTexto("Placa: ");
        String marca = entrada.lerTexto("Marca: ");
        String modelo = entrada.lerTexto("Modelo: ");
        int ano = entrada.lerInteiro("Ano: ");
        double km = entrada.lerDouble("Quilometragem: ");

        // POLIMORFISMO DE CRIAÇÃO: A VARIAVEL E O TIPO ABSTRATO DO VEICULO,
        
        Veiculo veiculo;
        switch (tipo) {
            case 1 -> veiculo = new Popular(placa, marca, modelo, ano, km);
            case 2 -> veiculo = new Sedan(placa, marca, modelo, ano, km);
            case 3 -> veiculo = new SUV(placa, marca, modelo, ano, km);
            default -> throw new VeiculoInvalidoException("Categoria invalida. Use 1, 2 ou 3.");
        }
        
        service.cadastrarVeiculo(veiculo);
        System.out.println("\n[OK] Veiculo cadastrado com sucesso!");
        System.out.println(veiculo);
        
        // MOSTRA OS VALORES CALCULADOS
        
        System.out.printf("  Diaria: R$ %.2f | Seguro/dia: R$ %.2f | Manutencao: R$ %.2f%n",
                veiculo.calcularDiaria(), veiculo.calcularSeguro(), veiculo.calcularManutencao());
    }

    private static void listarVeiculos() {
        List<Veiculo> lista = service.listarVeiculos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum veiculo na frota.");
            return;
        }
        System.out.println("--- Frota completa (" + lista.size() + ") ---");
        lista.forEach(v -> System.out.println("  • " + v));
    }

    private static void listarVeiculosDisponiveis() {
        List<Veiculo> lista = service.listarVeiculosDisponiveis();
        if (lista.isEmpty()) {
            System.out.println("Nenhum veiculo disponivel no momento.");
            return;
        }
        System.out.println("--- Veiculos disponiveis (" + lista.size() + ") ---");
        lista.forEach(v -> System.out.println("  • " + v));
    }

    // OPCAO 6 e 7 - LOCACAO E DEVOLUCAO

    // FLUXO DE ABERTURA DE LOCAÇÃO.
    // AS VALIDAÇÕES (CLIENTE EXISTE? VEICULO DISPONIVEL? DATAS?)
    // SÃO FEITAS DENTRO DO LocadoraService E CONTRATO.
    
    private static void abrirLocacao()
            throws ClienteInvalidoException, VeiculoIndisponivelException, DataInvalidaException {
        System.out.println("--- Nova Locacao ---");
        String cpf = entrada.lerTexto("CPF do cliente: ");
        String placa = entrada.lerTexto("Placa do veiculo: ");
        LocalDate inicio = entrada.lerData("Data de inicio");
        LocalDate fim = entrada.lerData("Data de devolucao prevista");

        Contrato contrato = service.abrirLocacao(cpf, placa, inicio, fim);
        System.out.println("\n[OK] Locacao aberta com sucesso!");
        contrato.imprimir();
    }

     //DEVOLUÇÃO: FECHA O CONTRATO, RECALCULA VALORES E
     //EXPORTA O COMPROVANTE AUTOMATICAMENTE.
  
    private static void devolverVeiculo()
            throws DataInvalidaException, VeiculoIndisponivelException {
        System.out.println("--- Devolucao / Fechamento de Contrato ---");
        String id = entrada.lerTexto("ID do contrato: ");
        LocalDate dataDev = entrada.lerData("Data efetiva de devolucao");

        Contrato contrato = service.devolverVeiculo(id, dataDev);
        System.out.println("\n[OK] Veiculo devolvido e contrato fechado!");
        contrato.imprimir();
        service.exportarComprovante(contrato);
    }

    // OPCAO 8, 9 e 10 - CONSULTAS E RELATORIOS

    private static void listarContratos() {
        System.out.println("1=Todos | 2=Ativos | 3=Fechados");
        int filtro = entrada.lerInteiro("Filtro: ");
        List<Contrato> lista;
        switch (filtro) {
            case 2 -> lista = service.listarContratosAtivos();
            case 3 -> lista = service.listarContratosFechados();
            default -> lista = service.listarContratos();
        }
        
        if (lista.isEmpty()) {
            System.out.println("Nenhum contrato encontrado.");
            return;
        }
        
        System.out.println("--- Contratos (" + lista.size() + ") ---");
        lista.forEach(c -> System.out.println("  • " + c));
    }
    
    private static void imprimirContrato() {
        String id = entrada.lerTexto("ID do contrato: ");
        service.buscarContratoPorId(id).ifPresentOrElse(
                c -> {
                    c.imprimir();
                    if (entrada.confirmar("Deseja exportar para arquivo texto?")) {
                        service.exportarComprovante(c);
                    }
                },
                () -> System.out.println("Contrato nao encontrado.")
        );
    }

    private static void gerarRelatorio() {
        String relatorio = service.gerarRelatorioFechamento();
        System.out.println(relatorio);
    }

    // OPCAO 11 - MANUTENCAO

    private static void gerenciarManutencao() throws VeiculoIndisponivelException {
        System.out.println("1=Colocar em manutencao | 2=Liberar de manutencao");
        int op = entrada.lerInteiro("Opcao: ");
        String placa = entrada.lerTexto("Placa: ");
        if (op == 1) {
            service.colocarEmManutencao(placa);
            System.out.println("[OK] Veiculo colocado em manutencao.");
        } else if (op == 2) {
            service.liberarDeManutencao(placa);
            System.out.println("[OK] Veiculo liberado e disponivel novamente.");
        } else {
            System.out.println("Opcao invalida.");
        }
    }
}
