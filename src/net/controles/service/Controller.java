/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.controles.service;

import java.util.ArrayList;
import java.util.HashMap;
import net.rmi.beans.Empresa;

/**
 * Classe responsável por controlar o conjunto de empresas do servidor, repassando os
 * eventos para o encapsulador responsável.
 * 
 * @author Henriques
 */
public class Controller {

    //Lista de empresas encapsuladas disponíveis/ String(Key) -> ID da Empresa
    private HashMap<String, CompanyManager> empresas;

    /**
     * Construtora da classe
     */
    public Controller() {
        empresas = new HashMap<>();
    }

    /**
     * Adiciona uma nova empresa à lista.
     * 
     * @param empresa Nova empresa
     */
    public void addCompany(Empresa empresa) {
        if (!empresas.containsKey(empresa.getID())) {
            empresas.put(empresa.getID(), new CompanyManager(empresa));
        }
    }

    /**
     * Redireciona para gerenciador da empresa.
     * 
     * @param empresaID ID da empresa
     * @return Gerenciador/Encapsulador da empresa ou NULL se não for encontrado.
     */
    public CompanyManager getManagerFor(String empresaID) {
        return empresas.get(empresaID);
    }

    /**
     * Retorna uma lista de todas as empresas como Beans.
     * 
     * @return lista de empresas
     */
    public ArrayList<Empresa> getListaEmpresas() {
        ArrayList<Empresa> retorno = new ArrayList<>();

        for (CompanyManager empresaManager : empresas.values()) {
            retorno.add(empresaManager.getEmpresa());
        }

        return retorno;
    }

    /**
     * Método responsável por simular as flutuações da bolsa.
     * Sorteia uma empresa, uma variação monetária entre -1,00 e + 1,00 e aplica.
     */
    public void sortear() {
        int size = empresas.size();

        int qual = (int) (Math.random() * (size - 1));

        int k = 0;
        for (CompanyManager company : empresas.values()) {
            if (k == qual) {
                int variacao = (int) (Math.random() * 200) - 100;

                if (variacao != 0) {
                    company.setValue(company.getEmpresa().getValue() + variacao);
                }

                break;
            } else {
                k++;
            }
        }
    }
}
