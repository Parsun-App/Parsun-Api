package com.parsun.parsun_api.infrastructure.seed.valery;

public final class ValeryAddresses {

    private static final String[] ADDRESSES = {
            "Calle Principal, Punta de Piedras",
            "Av. Bolivar, Sector El Muelle, Punta de Piedras",
            "Calle La Marina, Boca del Rio",
            "Sector Playa El Tirano, Calle Los Pescadores",
            "Av. Juan Bautista Arismendi, Punta de Piedras",
            "Calle Maneiro, Sector Los Bagres, Punta de Piedras",
            "Sector Las Maritas, Calle El Colegio",
            "Calle Guevara, Sector Centro, Punta de Piedras",
            "Sector El Cocal, Boca del Rio",
            "Calle Las Flores, Punta de Piedras",
            "Sector El Rincon, Calle Real, Punta de Piedras",
            "Av. Francisco Fajardo, Sector La Guardia"};

    private ValeryAddresses() {
    }

    public static String at(int index) {
        int position = Math.floorMod(index, ADDRESSES.length);
        return ADDRESSES[position] + ", Municipio Tubores";
    }
}
