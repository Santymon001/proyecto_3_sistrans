package uniandes.edu.co.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import uniandes.edu.co.demo.modelo.Afiliado;
import uniandes.edu.co.demo.modelo.Cita;
import uniandes.edu.co.demo.modelo.Servicio;

public interface CitaRepository extends MongoRepository<Cita,String>  
{
    //CREATE
    default void insertarCita(Cita cita)
    {
        save(cita);
    }

    //READ
    @Query(value = "{}")
    List<Cita> buscarTodasCitas();

    @Query("{_id:?0}")
    List<Cita> buscarCitaPorId(String id);

    //UPDATE
    @Query("{_id:?0}")
    @Update("{$set:{fecha:?1,servicio:?2,agendada:?3,afiliado:?4,medico:?5,ips:?6,ordenAsociada:?7}}")
    void actualizarCita(String id, Date fecha, Servicio servicio, boolean agendada, Afiliado afiliado, String medico, String ips, String ordenAsociada);

    //DELETE
    @Query(value = "{_id:?0}", delete = true)
    void eliminarCita(String id);

    //Nuevos UPDATE para el RF7
    @Query("{_id:?0}")
    @Update("{$set:{agendada:?1,afiliado:?2}}")
    void agendarCitaSinOrden(String id, boolean agendada, Afiliado afiliado);

    @Query("{_id:?0}")
    @Update("{$set:{agendada:?1,afiliado:?2,ordenAsociada:?3}}")
    void agendarCitaConOrden(String id, boolean agendada, Afiliado afiliado, String ordenAsociada);
}
