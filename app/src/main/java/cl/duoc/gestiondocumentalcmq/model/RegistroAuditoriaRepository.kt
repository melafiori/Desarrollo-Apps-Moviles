package cl.duoc.gestiondocumentalcmq.model

object RegistroAuditoriaRepository {

    val registros = mutableListOf<RegistroAuditoria>()

    fun registrar(
        accion: String,
        usuario: String
    ) {
        registros.add(
            RegistroAuditoria(
                accion = accion,
                usuario = usuario
            )
        )
    }
}