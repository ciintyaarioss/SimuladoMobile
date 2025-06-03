package com.simuladomobile.simuladomobileJBS.model;

import java.io.Serializable;
import java.time.LocalDate;

public class RegistroCarro implements Serializable {
    private LocalDate dataSaida;
    private LocalDate dataEntrada;
    private String placa;

    public RegistroCarro() {

    }
    public RegistroCarro(LocalDate dataSaida, LocalDate dataEntrada, String placa) {
        this.dataSaida = dataSaida;
        this.dataEntrada = dataEntrada;
        this.placa = placa;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
