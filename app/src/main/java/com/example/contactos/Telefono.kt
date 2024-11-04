package com.example.contactos

// Clase de datos para representar un número de teléfono asociado a un contacto
data class Telefono(
    val id: Long? = null,  // ID único del teléfono (puede ser null si aún no se ha guardado en la base de datos)
    val label: String,     // Etiqueta para identificar el tipo de teléfono (e.g., Casa, Trabajo)
    val number: String     // Número de teléfono
)
