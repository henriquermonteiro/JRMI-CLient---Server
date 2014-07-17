/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.controles.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import net.rmi.beans.Empresa;
import net.rmi.beans.Operacao;
import net.rmi.interfaces.ClientInterface;

/**
 * Classe que encapsula o Bean Empresa para disponibilizar
 * mecanismos de controle para o servidor.
 * 
 * @author Henriques
 */
public class CompanyManager {

    //Bean Empresa encapsulado.
    private final Empresa empresa;

    // Lista de operações de compra disponíveis.
    private final ArrayList<Operacao> compras;
    //Lista de operações de venda disponíveis.
    private final ArrayList<Operacao> vendas;

    // Lista de Clientes ouvindo aos eventos desta empresa.
    private final ArrayList<ClientInterface> ouvintes;

    /**
     * Construtora da classe. Recebe o Bean da empresa que irá encapsular.
     * @param empresa Bean que será encapsulado
     */
    public CompanyManager(Empresa empresa) {
        this.empresa = empresa;

        compras = new ArrayList<>();
        vendas = new ArrayList<>();
        ouvintes = new ArrayList<>();
    }

    /**
     * Permite o acesso à empresa.
     * @return Empresa encapsulada
     */
    public Empresa getEmpresa() {
        return empresa;
    }

    // ---------------------------------------------  Compra/Venda ------------

    /**
     * Adiciona uma operação de compra à lista de espera, ou realiza a compra se for possível no momento.
     * @param compra Nova operação
     * @return verdadeiro se a operação tiver sucesso, ou false do contrário.
     */
    public boolean addCompra(Operacao compra) {
        if (compra.isCompra()) { // Se for uma operação de compra
            if (!matchBuy(compra)) { // Se a operação de compra não possuir uma operação de venda compatível aguardando
                compras.add(compra); // Adiciona à fila de espera
            }

            return true;
        }

        return false;
    }

    /**
     * Adiciona uma operação de venda à lista de espera, ou realiza a venda se for possível no momento.
     * @param venda Nova operação
     * @return verdadeiro se a operação tiver sucesso, ou false do contrário.
     */
    public boolean addVenda(Operacao venda) {
        if (!venda.isCompra()) {
            if (!matchSell(venda)) {
                vendas.add(venda);
            }

            return true;
        }

        return false;
    }

    /**
     * Busca uma operação de venda na lista de espera compatível com a operação 
     * de compra fornecida como parâmetro. Se for encontrado um par a operação é realizada,
     * removendo a venda que estava aguardando e disparando a notificação para os clientes.
     * @param compra Operação que será comparada
     * @return verdadeiro se for encontrado um par e notificado os clientes, falso do contrário.
     */
    private boolean matchBuy(Operacao compra) {
        Operacao rem = null;
        
        for (Operacao venda : vendas) {
            if (compra.getPreçoUnitarioDesejado() >= venda.getPreçoUnitarioDesejado()) {
                int quantidade = Math.min(compra.getQuantidade(), venda.getQuantidade());
                int mediaPreco = (compra.getPreçoUnitarioDesejado() + venda.getPreçoUnitarioDesejado()) / 2;

                try {
                    //Notifica o comprador e o vendedor
                    compra.getClientSign().notifyCompletedOperation(compra.setPreçoUnitarioDesejado(mediaPreco).setQuantidade(quantidade));
                    venda.getClientSign().notifyCompletedOperation(venda.setPreçoUnitarioDesejado(mediaPreco).setQuantidade(quantidade));

                } catch (RemoteException ex) {
                    System.err.println(this.getClass() + " - Erro Remoto:Compra");
                }

                rem = venda;

                break;
            }
        }
        
        if(rem != null){
            vendas.remove(rem);
            
            return true;
        }

        return false;
    }

    /**
     * Busca uma operação de compra na lista de espera compatível com a operação 
     * de venda fornecida como parâmetro. Se for encontrado um par a operação é realizada,
     * removendo a compra que estava aguardando e disparando a notificação para os clientes.
     * @param venda Operação que será comparada
     * @return verdadeiro se for encontrado um par e notificado os clientes, falso do contrário.
     */
    private boolean matchSell(Operacao venda) {
        Operacao rem = null;
        
        for (Operacao compra : compras) {
            if (venda.getPreçoUnitarioDesejado() <= compra.getPreçoUnitarioDesejado()) {
                int quantidade = Math.min(venda.getQuantidade(), compra.getQuantidade());
                int mediaPreco = (venda.getPreçoUnitarioDesejado() + compra.getPreçoUnitarioDesejado()) / 2;

                try {
                    //Notifica o comprador e o vendedor
                    compra.getClientSign().notifyCompletedOperation(compra.setPreçoUnitarioDesejado(mediaPreco).setQuantidade(quantidade));
                    venda.getClientSign().notifyCompletedOperation(venda.setPreçoUnitarioDesejado(mediaPreco).setQuantidade(quantidade));

                } catch (RemoteException ex) {
                    System.err.println(this.getClass() + " - Erro Remoto:Venda");
                }

                rem = compra;

                break;
            }
        }
        
        if(rem != null){
            compras.remove(rem);
            
            return true;
        }

        return false;
    }

    //----------------------------------------Final[Compra/Venda]-----------------
    
    //----------------------------------------Ouvintes----------------------------
    /**
     * Adiciona um cliente como ouvinte dos eventos da empresa encapsulada.
     * @param ouvinte cliente que deve receber as notificações
     * @return verdadeiro se a adição for bem sucedida, falso do contrário.
     */
    public boolean addOuvinte(ClientInterface ouvinte) {
        return ouvintes.add(ouvinte);
    }

    /**
     * Método responsável por notificar os clientes ouvintes que uma alteração
     * na empresa encapsulada foi realizada.
     */
    private void notifyUpdate() {
        ArrayList<ClientInterface> remove = new ArrayList<>();

        for (ClientInterface ouvinte : ouvintes) {
            try {
                ouvinte.notifyUpdate(empresa);
            } catch (RemoteException ex) {
                remove.add(ouvinte);
            }
        }

        for (ClientInterface clientInterface : remove) {
            ouvintes.remove(clientInterface);
        }
    }

    private boolean notifying = false;

    /**
     * Método auxiliar para disparar a notificação aos clientes ouvintes.
     * Não permite notificações concorrentes.
     */
    public void fireNotify() {
        if (!notifying) {
            notifying = true;

            notifyUpdate();

            notifying = false;
        }
    }
    
    //----------------------------------------Final[Ouvintes]----------------------------
    
    //----------------------------------------Interface Com 'Empresa' ------------------
    /**
     * Seta o preço das ações da empresa e dispara a notificação aos clientes.
     * @param value Novo valor das ações.
     */
    public void setValue(Integer value){
        empresa.setValue(value);
        fireNotify();
    }
    
    //----------------------------------Final[Interface Com 'Empresa' ]------------------
}
