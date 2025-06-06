package uniandes.edu.co.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import uniandes.edu.co.demo.modelo.IPS;

public interface IPSRepository extends MongoRepository<IPS,String>
{
    //CREATE
    default void insertarIPS(IPS ips)
    {
        save(ips);
    }

    //READ
    @Query(value = "{}")
    List<IPS> buscarTodasIPS();

    @Query("{_id:?0}")
    List<IPS> buscarIPSPorId(String id);

    //UPDATE
    @Query("{_id:?0}")
    @Update("{$set: {nombre:?1,direccion:?2,telefono:?3,NIT:?4,servicios:?5,medicos:?6,ordenesServ:?7}}")
    void actualizarIPS(String id, String nombre, String direccion, String telefono, String NIT, List<String> servicios, List<String> medicos, List<String> ordenesServ);

    //DELETE
    @Query(value = "{_id:?0}", delete = true)
    void eliminarIPS(String id);
}
