package com.uninorte.ospedale.model.observer;

/**
 * Contrato del sujeto observable genérico.
 * Los repositorios implementan esta interfaz para notificar a las vistas
 * cuando el estado persistido cambia.
 */
public interface Observable<E> {
    void subscribe(Observer<E> observer);
}
