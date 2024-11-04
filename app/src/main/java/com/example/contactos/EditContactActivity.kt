package com.example.contactos

import ApiService
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditContactActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService  // Interfaz para realizar llamadas a la API
    private var personaId: Long? = null          // ID del contacto a editar
    private var persona: Persona? = null         // Objeto de la persona cargada desde la API
    private val phones = mutableListOf<Telefono>()  // Lista mutable para teléfonos
    private val emails = mutableListOf<Correo>()    // Lista mutable para correos electrónicos
    private lateinit var phoneAdapter: PhoneAdapter  // Adaptador para la lista de teléfonos
    private lateinit var emailAdapter: EmailAdapter  // Adaptador para la lista de correos
    private lateinit var profileImageView: ImageView // Imagen de perfil del contacto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)

        // Inicialización de Retrofit y ApiService
        apiService = Retrofit.Builder()
            .baseUrl("https://apicontactos.jmacboy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Obtener el ID de la persona pasada por el intent
        personaId = intent.getLongExtra("persona_id", -1)
        profileImageView = findViewById(R.id.profileImageView)

        // Cargar los detalles del contacto si el ID es válido
        if (personaId != null && personaId != -1L) {
            loadContactDetails(personaId!!)
        } else {
            Toast.makeText(this, "Error: ID del contacto no válido", Toast.LENGTH_SHORT).show()
        }

        // Configurar adaptadores para listas de teléfonos y correos
        setupAdapters()

        // Asignar acciones a los botones de guardar y eliminar
        findViewById<Button>(R.id.saveButton).setOnClickListener { saveContact() }
        findViewById<Button>(R.id.deleteButton).setOnClickListener { confirmDeleteContact() }
    }

    // Cargar detalles del contacto desde la API
    private fun loadContactDetails(id: Long) {
        apiService.getContacto(id).enqueue(object : Callback<Persona> {
            override fun onResponse(call: Call<Persona>, response: Response<Persona>) {
                if (response.isSuccessful) {
                    persona = response.body()
                    if (persona != null) {
                        populateContactDetails(persona!!)
                    }
                }
            }
            override fun onFailure(call: Call<Persona>, t: Throwable) {
                Log.e("EditContactActivity", "Error loading contact details: ${t.message}")
            }
        })
    }

    // Mostrar los detalles del contacto en las vistas
    private fun populateContactDetails(persona: Persona) {
        findViewById<EditText>(R.id.nameField).setText(persona.name)
        findViewById<EditText>(R.id.lastNameField).setText(persona.lastName)
        findViewById<EditText>(R.id.companyField).setText(persona.company)
        findViewById<EditText>(R.id.addressField).setText(persona.address)
        findViewById<EditText>(R.id.cityField).setText(persona.city)
        findViewById<EditText>(R.id.stateField).setText(persona.state)

        // Mostrar listas de teléfonos y correos electrónicos
        phones.clear()
        phones.addAll(persona.phones ?: emptyList())
        phoneAdapter.notifyDataSetChanged()

        emails.clear()
        emails.addAll(persona.emails ?: emptyList())
        emailAdapter.notifyDataSetChanged()

        // Cargar la imagen de perfil usando Glide
        Glide.with(this)
            .load(persona.profileImageUrl)
            .placeholder(R.drawable.agregar)
            .into(profileImageView)
    }

    // Guardar los cambios realizados en el contacto
    private fun saveContact() {
        // Crear un nuevo objeto Persona con los datos actualizados
        val updatedPersona = Persona(
            id = personaId,
            name = findViewById<EditText>(R.id.nameField).text.toString(),
            lastName = findViewById<EditText>(R.id.lastNameField).text.toString(),
            company = findViewById<EditText>(R.id.companyField).text.toString(),
            address = findViewById<EditText>(R.id.addressField).text.toString(),
            city = findViewById<EditText>(R.id.cityField).text.toString(),
            state = findViewById<EditText>(R.id.stateField).text.toString(),
            phones = phones,
            emails = emails,
            profileImageUrl = persona?.profileImageUrl
        )

        // Enviar solicitud a la API para actualizar el contacto
        apiService.updateContacto(personaId!!, updatedPersona).enqueue(object : Callback<Persona> {
            override fun onResponse(call: Call<Persona>, response: Response<Persona>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditContactActivity, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditContactActivity, "Error al actualizar el contacto: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Persona>, t: Throwable) {
                Log.e("EditContactActivity", "Error saving contact: ${t.message}")
            }
        })
    }

    // Configurar adaptadores para los RecyclerViews de teléfonos y correos electrónicos
    private fun setupAdapters() {
        findViewById<RecyclerView>(R.id.phoneRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@EditContactActivity)
            phoneAdapter = PhoneAdapter(phones)
            adapter = phoneAdapter
        }

        findViewById<RecyclerView>(R.id.emailRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@EditContactActivity)
            emailAdapter = EmailAdapter(emails)
            adapter = emailAdapter
        }
    }

    // Confirmar eliminación del contacto con un diálogo de alerta
    private fun confirmDeleteContact() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Contacto")
            .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
            .setPositiveButton("Sí") { _, _ -> deleteContact() }
            .setNegativeButton("No", null)
            .show()
    }

    // Eliminar el contacto a través de la API
    private fun deleteContact() {
        apiService.deleteContacto(personaId!!).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditContactActivity, "Contacto eliminado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("EditContactActivity", "Error deleting contact: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EditContactActivity", "Error deleting contact: ${t.message}")
            }
        })
    }
}
