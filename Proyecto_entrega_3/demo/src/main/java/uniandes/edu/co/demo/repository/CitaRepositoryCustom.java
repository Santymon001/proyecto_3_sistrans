package uniandes.edu.co.demo.repository;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CitaRepositoryCustom 
{
    private final MongoTemplate mongoTemplate;

    public CitaRepositoryCustom(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    // Método para resolver el RFC1 - Disponibilidad en próximas 4 semanas
    public List<Document> obtenerDisponibilidadSig4Semanas(String nombreServicio, Date fechaInicio, Date fechaFin) 
    {
        List<Document> pipeline = List.of(
            new Document("$match", new Document()
                .append("agendada", false)
                .append("fecha", new Document("$gte", fechaInicio).append("$lte", fechaFin))
                .append("servicio.nombre", nombreServicio)
                .append("medico", new Document("$regex", "^[a-fA-F0-9]{24}$"))
                .append("ips", new Document("$regex", "^[a-fA-F0-9]{24}$"))
            ),
            new Document("$addFields", new Document()
                .append("medicoObjectId", new Document("$toObjectId", "$medico"))
                .append("ipsObjectId", new Document("$toObjectId", "$ips"))
            ),
            new Document("$lookup", new Document()
                .append("from", "Medico")
                .append("localField", "medicoObjectId")
                .append("foreignField", "_id")
                .append("as", "medico_info")
            ),
            new Document("$unwind", "$medico_info"),
            new Document("$lookup", new Document()
                .append("from", "IPS")
                .append("localField", "ipsObjectId")
                .append("foreignField", "_id")
                .append("as", "ips_info")
            ),
            new Document("$unwind", "$ips_info"),
            new Document("$sort", new Document("fecha", 1)),
            new Document("$project", new Document()
                .append("_id", 0)
                .append("fecha", new Document("$dateToString",
                    new Document("format", "%Y-%m-%d")
                            .append("date", "$fecha")))
                .append("hora", new Document("$dateToString",
                    new Document("format", "%H:%M:%S")
                            .append("date", "$fecha")))
                .append("nombreServicio", "$servicio.nombre")
                .append("nombreMedico", "$medico_info.nombre")
                .append("nombreIPS", "$ips_info.nombre")
            )
        );

        return mongoTemplate.getCollection("Cita").aggregate(pipeline).into(new java.util.ArrayList<>());
    }

    // Nuevo método para resolver RFC2 - Top 20 servicios más solicitados
    public List<Document> topServiciosMasSolicitados() 
    {
        List<Document> pipeline = List.of(
            new Document("$group", new Document("_id", "$servicio.nombre")
                .append("cantidad", new Document("$sum", 1))
            ),
            new Document("$sort", new Document("cantidad", -1)),
            new Document("$limit", 20)
        );

        return mongoTemplate.getCollection("Cita").aggregate(pipeline).into(new java.util.ArrayList<>());
    }
}
