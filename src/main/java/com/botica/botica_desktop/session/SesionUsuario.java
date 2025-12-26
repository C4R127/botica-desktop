package com.botica.botica_desktop.session;

import com.botica.botica_desktop.entity.Usuario;

public class SesionUsuario {

    private static Usuario usuarioActual;

    public static void setUsuarioActual(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}
