package com.example.contactos

// Clase de datos para representar un correo electrónico asociado a un contacto
data class Correo(
    val id: Long? = null,  // ID único del correo (puede ser null si aún no se ha guardado en la base de datos)
    val label: String,     // Etiqueta para identificar el tipo de correo (e.g., Personal, Trabajo)
    val email: String      // Dirección de correo electrónico
)
