package com.example.contactos

import ApiService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Actividad principal que muestra la lista de contactos y permite buscar, añadir o editar contactos
class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService               // Interfaz para realizar llamadas a la API
    private lateinit var recyclerView: RecyclerView           // RecyclerView para mostrar la lista de contactos
    private lateinit var personaListAdapter: PersonaListAdapter // Adaptador para el RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de Retrofit y ApiService
        apiService = Retrofit.Builder()
            .baseUrl("https://apicontactos.jmacboy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.contactRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val searchButton = findViewById<Button>(R.id.searchButton) // Botón para realizar búsquedas
        val searchField = findViewById<EditText>(R.id.searchField) // Campo de texto para ingresar el término de búsqueda

        // Acciones al hacer clic en el botón de búsqueda
        searchButton.setOnClickListener {
            val query = searchField.text.toString().trim()
            if (query.isNotEmpty()) {
                searchContactos(query) // Realizar búsqueda si hay texto
            } else {
                fetchContactos() // Muestra todos los contactos si no hay búsqueda
            }
        }

        val addContactButton = findViewById<Button>(R.id.addContactButton) // Botón para añadir nuevo contacto
        addContactButton.setOnClickListener {
            val intent = Intent(this, AddContactActivity::class.java)
            startActivity(intent)
        }

        fetchContactos() // Carga inicial de contactos
    }

    override fun onResume() {
        super.onResume()
        fetchContactos() // Actualizar la lista de contactos al volver a MainActivity
    }

    // Método para obtener todos los contactos de la API
    private fun fetchContactos() {
        apiService.getContactos().enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    val contactos = response.body() ?: emptyList()
                    personaListAdapter = PersonaListAdapter(contactos) { persona ->
                        val intent = Intent(this@MainActivity, EditContactActivity::class.java)
                        intent.putExtra("persona_id", persona.id) // Enviar ID de contacto para edición
                        startActivity(intent)

                        // Log para confirmar que el ID del contacto se está pasando correctamente
                        Log.d("MainActivity", "Contact ID: ${persona.id}")
                    }
                    recyclerView.adapter = personaListAdapter
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta de la API: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al cargar contactos: ${t.message}", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }

    // Método para buscar contactos mediante un término específico
    private fun searchContactos(query: String) {
        apiService.searchContactos(query).enqueue(object : Callback<List<Persona>> {
            override fun onResponse(call: Call<List<Persona>>, response: Response<List<Persona>>) {
                if (response.isSuccessful) {
                    val contactos = response.body() ?: emptyList()
                    personaListAdapter = PersonaListAdapter(contactos) { persona ->
                        val intent = Intent(this@MainActivity, EditContactActivity::class.java)
                        intent.putExtra("persona_id", persona.id)
                        startActivity(intent)
                    }
                    recyclerView.adapter = personaListAdapter
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta de búsqueda: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Persona>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al buscar contactos: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
