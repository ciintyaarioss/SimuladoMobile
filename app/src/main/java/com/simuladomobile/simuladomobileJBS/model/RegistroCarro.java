package com.simuladomobile.simuladomobileJBS.model;

import java.io.Serializable;
import java.util.Date;

public class RegistroCarro implements Serializable {
    private Date dataSaida;
    private Date dataEntrada;
    private String placa;
    private String usuarioEmail;

    private transient String documentId;

    public RegistroCarro() {

    }
    public RegistroCarro(Date dataSaida, Date dataEntrada, String placa, String usuarioEmail) {
        this.dataSaida = dataSaida;
        this.dataEntrada = dataEntrada;
        this.placa = placa;
        this.usuarioEmail = usuarioEmail;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public String getDocumentId() {
        return documentId;
    }
    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }
}
