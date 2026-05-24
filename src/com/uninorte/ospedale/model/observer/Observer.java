package com.uninorte.ospedale.model.observer;

/**
 * Contrato del observador genérico.
 * Las vistas implementan esta interfaz para reaccionar a cambios en el repositorio.
 */
public interface Observer<E> {
    void onNotify(E event);
}
