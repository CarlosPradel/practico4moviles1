package com.example.contactos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Adaptador para mostrar una lista de contactos en un RecyclerView
class PersonaListAdapter(
    private val personas: List<Persona>,         // Lista de personas que se mostrarán
    private val onClick: (Persona) -> Unit       // Función de callback que se ejecuta al hacer clic en un contacto
) : RecyclerView.Adapter<PersonaListAdapter.PersonaViewHolder>() {

    // ViewHolder que mantiene las referencias de las vistas de cada elemento
    inner class PersonaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contactName)       // Campo para el nombre del contacto
        val number: TextView = view.findViewById(R.id.contactNumber)   // Campo para el número de teléfono del contacto
        val imageView: ImageView = view.findViewById(R.id.contactImageView) // Imagen de perfil del contacto
    }

    // Crea nuevas vistas para los elementos de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return PersonaViewHolder(view)
    }

    // Establece el contenido de cada elemento de la lista
    override fun onBindViewHolder(holder: PersonaViewHolder, position: Int) {
        val persona = personas[position]
        holder.name.text = "${persona.name} ${persona.lastName}" // Nombre completo del contacto

        // Muestra el primer número de teléfono disponible o "Sin número" si no hay ninguno
        holder.number.text = persona.phones.firstOrNull()?.number ?: "Sin número"

        // Configura el evento de clic para el elemento
        holder.itemView.setOnClickListener { onClick(persona) }

        // Carga la imagen de perfil usando Glide (muestra un placeholder si no hay imagen)
        Glide.with(holder.itemView.context)
            .load(persona.profileImageUrl)
            .placeholder(R.drawable.agregar)
            .into(holder.imageView)
    }

    // Devuelve el número total de elementos en la lista
    override fun getItemCount(): Int = personas.size
}
