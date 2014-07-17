/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.controles.simulador;

import net.controles.service.Controller;

/**
 * Thread para realizar a flutuação do valor das ações.
 * @author Henriques
 */
public class SimuladorBolsa extends Thread {

    private boolean keepRunning;
    private Controller controle;

    public SimuladorBolsa(Controller server) {
        this.controle = server;
    }

    @Override
    public void run() {
        super.run();

        keepRunning = true;

        try {
            while (keepRunning) {
                controle.sortear();
                
                sleep(500l);
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