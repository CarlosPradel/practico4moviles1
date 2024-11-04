package com.example.contactos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adaptador para mostrar una lista de correos electrónicos en un RecyclerView
class EmailAdapter(private val emails: List<Correo>) : RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    // Clase interna para mantener las referencias a las vistas de cada elemento de la lista
    class EmailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emailAddress: TextView = view.findViewById(R.id.emailAddress) // Campo para mostrar el correo
        val emailLabel: TextView = view.findViewById(R.id.emailLabel)     // Campo para mostrar la etiqueta del correo
    }

    // Método para crear nuevas vistas (invocado por el administrador de diseño)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        // Inflar el layout para cada elemento de la lista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    // Método para reemplazar el contenido de una vista (invocado por el administrador de diseño)
    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = emails[position]          // Obtener el correo electrónico en la posición actual
        holder.emailAddress.text = email.email // Establecer el texto del campo de dirección de correo
        holder.emailLabel.text = email.label   // Establecer el texto de la etiqueta del correo
    }

    // Devuelve el tamaño de la lista de correos electrónicos
    override fun getItemCount(): Int = emails.size
}
