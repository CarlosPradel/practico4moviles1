import com.example.contactos.Correo
import com.example.contactos.Persona
import com.example.contactos.Telefono
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Obtiene una lista de todos los contactos
    @GET("api/personas")
    fun getContactos(): Call<List<Persona>>

    // Busca contactos en función de un término de búsqueda
    @GET("api/search")
    fun searchContactos(@Query("q") query: String): Call<List<Persona>>

    // Añade un nuevo contacto enviando un objeto Persona en el cuerpo de la solicitud
    @POST("https://apicontactos.jmacboy.com/api/personas")
    fun addContacto(@Body persona: Persona): Call<Persona>

    // Sube una imagen de perfil para un contacto específico
    @Multipart
    @POST("api/personas/{id}/profile-picture")
    fun uploadProfilePicture(
        @Path("id") personaId: Int,       // ID del contacto
        @Part image: MultipartBody.Part   // Imagen en formato multipart
    ): Call<Void>

    // Obtiene los detalles de un contacto específico usando su ID
    @GET("api/personas/{id}")
    fun getContacto(@Path("id") id: Long): Call<Persona>

    // Actualiza la información de un contacto existente
    @PUT("api/personas/{id}")
    fun updateContacto(@Path("id") id: Long, @Body persona: Persona): Call<Persona>

    // Elimina un contacto específico utilizando su ID
    @DELETE("api/personas/{id}")
    fun deleteContacto(@Path("id") id: Long): Call<Void>

    // Añade un número de teléfono a un contacto específico
    @POST("api/personas/{id}/phones")
    fun addPhone(
        @Path("id") personaId: Long,      // ID del contacto
        @Body telefonos: Telefono         // Teléfono en el cuerpo de la solicitud
    ): Call<Telefono>

    // Añade un correo electrónico a un contacto específico
    @POST("api/personas/{id}/emails")
    fun addEmail(
        @Path("id") personaId: Long,      // ID del contacto
        @Body correos: Correo             // Correo en el cuerpo de la solicitud
    ): Call<Correo>
}
