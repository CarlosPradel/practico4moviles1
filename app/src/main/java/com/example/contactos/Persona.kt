package com.example.contactos

import com.google.gson.annotations.SerializedName

// Clase de datos que representa a una persona/contacto
data class Persona(
    val id: Long? = null,                           // ID único de la persona (puede ser null si es nuevo y aún no está en la base de datos)
    @SerializedName("name") val name: String,       // Nombre de la persona (mapeado a "name" en JSON)
    @SerializedName("last_name") val lastName: String, // Apellido de la persona (mapeado a "last_name" en JSON, asegurarse que coincide con el backend)
    @SerializedName("company") val company: String, // Empresa asociada a la persona
    @SerializedName("address") val address: String, // Dirección de la persona
    @SerializedName("city") val city: String,       // Ciudad de residencia
    @SerializedName("state") val state: String,     // Estado o provincia de residencia
    @SerializedName("phones") val phones: MutableList<Telefono> = mutableListOf(), // Lista mutable de teléfonos asociados a la persona
    @SerializedName("emails") val emails: MutableList<Correo> = mutableListOf(),   // Lista mutable de correos electrónicos asociados a la persona
    @SerializedName("profile_picture") val profileImageUrl: String? = null // URL de la imagen de perfil de la persona (opcional)
)
