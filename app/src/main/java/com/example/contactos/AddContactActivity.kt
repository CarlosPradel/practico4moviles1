package com.example.contactos

import ApiService
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class AddContactActivity : AppCompatActivity() {
    // Lista mutable para almacenar teléfonos y correos electrónicos
    private val phones = mutableListOf<Telefono>()
    private val emails = mutableListOf<Correo>()

    // Adaptadores para mostrar listas de teléfonos y correos
    private lateinit var phoneAdapter: PhoneAdapter
    private lateinit var emailAdapter: EmailAdapter

    // Instancia del servicio de API para realizar solicitudes de red
    private lateinit var apiService: ApiService

    // Vista para la imagen de perfil y archivo para la imagen seleccionada
    private lateinit var profileImageView: ImageView
    private var profileImageFile: File? = null

    // Código de solicitud para seleccionar una imagen
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // Inicializar el servicio de API utilizando Retrofit
        apiService = Retrofit.Builder()
            .baseUrl("https://apicontactos.jmacboy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Configurar la vista de imagen de perfil
        profileImageView = findViewById(R.id.profileImageView)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)

        // Asignar acción para seleccionar una imagen
        selectImageButton.setOnClickListener {
            openGallery()
        }

        // Configurar el RecyclerView para mostrar teléfonos y asignar el adaptador
        val phoneRecyclerView = findViewById<RecyclerView>(R.id.phoneRecyclerView)
        phoneRecyclerView.layoutManager = LinearLayoutManager(this)
        phoneAdapter = PhoneAdapter(phones)
        phoneRecyclerView.adapter = phoneAdapter

        // Configurar el RecyclerView para mostrar correos electrónicos y asignar el adaptador
        val emailRecyclerView = findViewById<RecyclerView>(R.id.emailRecyclerView)
        emailRecyclerView.layoutManager = LinearLayoutManager(this)
        emailAdapter = EmailAdapter(emails)
        emailRecyclerView.adapter = emailAdapter

        // Asignar acción para agregar un teléfono
        val addPhoneButton = findViewById<Button>(R.id.addPhoneButton)
        addPhoneButton.setOnClickListener {
            showAddPhoneDialog()
        }

        // Asignar acción para agregar un correo electrónico
        val addEmailButton = findViewById<Button>(R.id.addEmailButton)
        addEmailButton.setOnClickListener {
            showAddEmailDialog()
        }

        // Asignar acción para guardar el contacto
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveContact()
        }
    }

    // Abre la galería para seleccionar una imagen
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Maneja el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                profileImageView.setImageURI(it)
                profileImageFile = uriToFile(it)
            }
        }
    }

    // Convierte un Uri de imagen en un archivo temporal
    private fun uriToFile(uri: Uri): File? {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("profile_image", ".jpg", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        outputStream.close()
        inputStream.close()
        return tempFile
    }

    // Muestra un diálogo para agregar un nuevo número de teléfono
    private fun showAddPhoneDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_phone, null)
        val phoneNumberField = dialogView.findViewById<EditText>(R.id.phoneNumberField)
        val phoneLabelSpinner = dialogView.findViewById<Spinner>(R.id.phoneLabelSpinner)

        // Opciones de etiquetas para el número de teléfono
        val labels = arrayOf("Casa", "Trabajo", "Celular", "Personalizado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        phoneLabelSpinner.adapter = adapter

        // Crear y mostrar el diálogo para añadir teléfono
        AlertDialog.Builder(this)
            .setTitle("Agregar Teléfono")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val number = phoneNumberField.text.toString()
                val label = phoneLabelSpinner.selectedItem.toString()

                // Verificar si el campo de número no está vacío
                if (number.isNotEmpty()) {
                    val phone = Telefono(id = null, label = label, number = number)
                    phones.add(phone)
                    phoneAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Número de teléfono no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Muestra un diálogo para agregar un nuevo correo electrónico
    private fun showAddEmailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_email, null)
        val emailField = dialogView.findViewById<EditText>(R.id.emailField)
        val emailLabelSpinner = dialogView.findViewById<Spinner>(R.id.emailLabelSpinner)

        // Opciones de etiquetas para el correo electrónico
        val labels = arrayOf("Personal", "Trabajo", "Universidad", "Personalizado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        emailLabelSpinner.adapter = adapter

        // Crear y mostrar el diálogo para añadir correo
        AlertDialog.Builder(this)
            .setTitle("Agregar Correo")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val email = emailField.text.toString()
                val label = emailLabelSpinner.selectedItem.toString()

                // Verificar si el campo de correo no está vacío
                if (email.isNotEmpty()) {
                    val correo = Correo(id = null, email = email, label = label)
                    emails.add(correo)
                    emailAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Guarda el contacto en la base de datos
    private fun saveContact() {
        // Obtiene los datos ingresados por el usuario
        val name = findViewById<EditText>(R.id.nameField).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.lastNameField).text.toString().trim()
        val company = findViewById<EditText>(R.id.companyField).text.toString().trim()
        val address = findViewById<EditText>(R.id.addressField).text.toString().trim()
        val city = findViewById<EditText>(R.id.cityField).text.toString().trim()
        val state = findViewById<EditText>(R.id.stateField).text.toString().trim()

        // Verificar si algún campo está vacío
        if (name.isEmpty() || lastName.isEmpty() || company.isEmpty() || address.isEmpty() || city.isEmpty() || state.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un objeto Persona para enviar al servidor
        val persona = Persona(
            id = null,
            name = name,
            lastName = lastName,
            company = company,
            address = address,
            city = city,
            state = state,
            phones = mutableListOf(),
            emails = mutableListOf(),
            profileImageUrl = null
        )

        // Realiza la solicitud de red para añadir el contacto
        apiService.addContacto(persona).enqueue(object : Callback<Persona> {
            override fun onResponse(call: Call<Persona>, response: Response<Persona>) {
                if (response.isSuccessful) {
                    val personaId = response.body()?.id // ID del nuevo contacto
                    if (personaId != null) {
                        // Agrega teléfonos y correos nuevos, y sube la imagen de perfil si existe
                        for (telefono in phones) {
                            if (telefono.id == null) {
                                addPhoneToContact(personaId.toInt(), telefono)
                            }
                        }
                        for (correo in emails) {
                            if (correo.id == null) {
                                addEmailToContact(personaId.toInt(), correo)
                            }
                        }
                        if (profileImageFile != null) {
                            uploadProfilePicture(personaId.toInt())
                        } else {
                            Toast.makeText(this@AddContactActivity, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } else {
                    Log.e("AddContactActivity", "Error al añadir contacto: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(this@AddContactActivity, "Error al añadir contacto: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Persona>, t: Throwable) {
                Log.e("AddContactActivity", "Fallo en la conexión: ${t.message}")
                Toast.makeText(this@AddContactActivity, "Error al conectar: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Agrega un teléfono al contacto existente en el servidor
    private fun addPhoneToContact(personaId: Int, telefono: Telefono) {
        apiService.addPhone(personaId.toLong(), telefono).enqueue(object : Callback<Telefono> {
            override fun onResponse(call: Call<Telefono>, response: Response<Telefono>) {
                if (!response.isSuccessful) {
                    Log.e("AddContactActivity", "Error al añadir teléfono: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Telefono>, t: Throwable) {
                Log.e("AddContactActivity", "Fallo en la conexión al añadir teléfono: ${t.message}")
            }
        })
    }

    // Agrega un correo electrónico al contacto existente en el servidor
    private fun addEmailToContact(personaId: Int, correo: Correo) {
        apiService.addEmail(personaId.toLong(), correo).enqueue(object : Callback<Correo> {
            override fun onResponse(call: Call<Correo>, response: Response<Correo>) {
                if (!response.isSuccessful) {
                    Log.e("AddContactActivity", "Error al añadir correo: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Correo>, t: Throwable) {
                Log.e("AddContactActivity", "Fallo en la conexión al añadir correo: ${t.message}")
            }
        })
    }

    // Sube la imagen de perfil del contacto al servidor
    private fun uploadProfilePicture(personaId: Int) {
        profileImageFile?.let { file ->
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val profileImagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            apiService.uploadProfilePicture(personaId, profileImagePart).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddContactActivity, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AddContactActivity, "Error al subir foto: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddContactActivity, "Error al conectar: ${t.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        } ?: run {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }
}
