package com.rotasegura.model;

import com.rotasegura.exception.VeiculoInvalidoException;

public class Sedan extends Veiculo {

    private static final long serialVersionUID = 1L;

    private static final double DIARIA_BASE = 149.90;
    private static final double SEGURO_DIARIO = 28.00;
    private static final double TAXA_MANUTENCAO = 45.00;

    public Sedan(String placa, String marca, String modelo, int ano, double quilometragem)
            throws VeiculoInvalidoException {
        super(placa, marca, modelo, ano, quilometragem);
    }

    /**
     * Diaria de Sedan:
     * - +10% se o veiculo tiver ate 2 anos
     * - -10% se tiver 8 anos ou mais (desconto por depreciação)
     * - valor base nos demais casos
     */
    @Override
    public double calcularDiaria() {
        int idade = java.time.Year.now().getValue() - getAno();
        if (idade <= 2) {
            return DIARIA_BASE * 1.10;
        } else if (idade >= 8) {
            return DIARIA_BASE * 0.90;
        }
        return DIARIA_BASE;
    }

    @Override
    public double calcularSeguro() {
        return SEGURO_DIARIO;
    }

    @Override
    public double calcularManutencao() {
        return TAXA_MANUTENCAO + (getQuilometragem() * 0.015);
    }

    @Override
    public String getCategoria() {
        return "Sedan";
    }
}
