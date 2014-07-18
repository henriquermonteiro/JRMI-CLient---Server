/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.controles.simulador;

import net.controles.service.Controller;

/**
 * Thread para realizar a flutuação do valor das ações.
 * 
 * @author Henriques
 */
public class SimuladorBolsa extends Thread {

    private boolean keepRunning;
    private Controller controle;

    /**
     * Construtora da classe.
     * 
     * @param server Controlador do conjunto de empresas
     */
    public SimuladorBolsa(Controller server) {
        this.controle = server;
    }

    /**
     * Método que inicializa a Thread, e altera o valor de uma ação a cada meio segundo.
     */
    @Override
    public void run() {
        super.run();

        keepRunning = true;

        try {
            while (keepRunning) {
                controle.sortear();
                
                sleep((long)500);
            }
        } catch (InterruptedException ex) {
            System.err.println(this.getClass() + " - Sleep Interrompido");
        }

    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

}
