package com.example.contactos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adaptador para mostrar una lista de números de teléfono en un RecyclerView
class PhoneAdapter(private val phones: List<Telefono>) : RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>() {

    // ViewHolder que mantiene las referencias de las vistas para cada elemento de teléfono
    class PhoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber) // Campo para mostrar el número de teléfono
        val phoneLabel: TextView = view.findViewById(R.id.phoneLabel)   // Campo para mostrar la etiqueta del teléfono
    }

    // Crea nuevas vistas para los elementos de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phone, parent, false)
        return PhoneViewHolder(view)
    }

    // Establece el contenido de cada elemento de la lista de teléfonos
    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        val phone = phones[position]
        holder.phoneNumber.text = phone.number  // Establece el número de teléfono
        holder.phoneLabel.text = phone.label    // Establece la etiqueta del teléfono (e.g., Casa, Trabajo)
    }

    // Devuelve el tamaño de la lista de teléfonos
    override fun getItemCount(): Int = phones.size
}
