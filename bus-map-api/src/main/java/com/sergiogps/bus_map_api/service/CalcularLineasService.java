package com.sergiogps.bus_map_api.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiogps.bus_map_api.dto.Parada;
import com.sergiogps.bus_map_api.dto.ParadaSecuencia;
import com.sergiogps.bus_map_api.dto.RutaResponse;

import jakarta.annotation.PostConstruct;

@Service
public class CalcularLineasService {

    // Repositorio BBDD ??
    // private final ParadaRepository paradaRepository;

    private static final Logger logger = LoggerFactory.getLogger(CalcularLineasService.class);
    
    // Aquí guardaremos las paradas en memoria
    private List<Parada> listaParadas = new ArrayList<>();
    
    private final ObjectMapper objectMapper;

    // Spring inyecta automáticamente el ObjectMapper configurado por defecto
    public CalcularLineasService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     *
     */
    @PostConstruct
    public void cargarParadas() {
        try {
            logger.info("Cargando paradas desde JSON...");
            
            // 1. Obtener el fichero del classpath
            ClassPathResource resource = new ClassPathResource("json/paradas_int.json");
            InputStream inputStream = resource.getInputStream();

            // 2. Usar Jackson para convertir el JSON a List<Parada>
            listaParadas = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<Parada>>() {}
            );

            logger.info("¡Éxito! Se han cargado {} paradas en memoria.", listaParadas.size());

        } catch (IOException e) {
            logger.error("Error fatal al cargar el fichero json/paradas.json", e);
            // Dependiendo de tu lógica, podrías querer lanzar una RuntimeException para detener la app
        }
    }

    // Método getter para que el resto de tu lógica acceda a los datos
    public List<Parada> obtenerTodasLasParadas() {
        return listaParadas;
    }

    /**
     * Método genérico para cargar cualquier JSON y evitar repetir código try-catch
     */
    private <T> List<T> cargarJson(String path, TypeReference<List<T>> typeReference) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStream inputStream = resource.getInputStream();
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException e) {
            logger.error("Error cargando el fichero: " + path, e);
            return new ArrayList<>();
        }
    }

    /**
     * Método para calcular la ruta con las coordenadas de origen y destino.
     * 
     * @param latOrigen
     * @param lonOrigen
     * @param latDestino
     * @param lonDestino
     * @return La ruta(polilínea) ya calculada
     */
    public RutaResponse calcularMejorOpcion(double latOrigen, double lonOrigen, double latDestino, double lonDestino) {
        
        // 1. Obtener todas las paradas (Simulado: esto vendría de BBDD)
        List<Parada> allStops = obtenerTodasLasParadas(); 

        // 2. Buscar paradas cercanas al Origen y al Destino (Radio 500m)
        List<Parada> paradasCercaOrigen = findClosestLocations(latOrigen, lonOrigen, allStops, 500);
        List<Parada> paradasCercaDestino = findClosestLocations(latDestino, lonDestino, allStops, 500);

        if (paradasCercaOrigen.isEmpty() || paradasCercaDestino.isEmpty()) {
            return null; // O lanzar excepción "No hay paradas cerca"
        }

        // 3. Buscar líneas comunes (Intersección)
        Set<Integer> lineasComunes = findCommonBusLines(paradasCercaOrigen, paradasCercaDestino);

        if (lineasComunes.isEmpty()) {
            return null; // No hay línea directa
        }

        // 4. Probar con las líneas encontradas (La lógica de probar IDA y VUELTA)
        // En tu Kotlin estaba "harcodeado" a la línea 2, aquí iteramos las que encontremos.
        for (Integer lineaId : lineasComunes) {
            
            // Cargar secuencias de esa línea
            List<ParadaSecuencia> secuenciaIda = cargarJson("json/paradas_linea_2_ida.json", new TypeReference<List<ParadaSecuencia>>() {});
            List<ParadaSecuencia> secuenciaVuelta = cargarJson("json/paradas_linea_2_vuelta.json", new TypeReference<List<ParadaSecuencia>>() {});

            // Intentar encontrar ruta válida en IDA o VUELTA
            RutaResponse ruta = identificarSentido(
                    paradasCercaOrigen, 
                    paradasCercaDestino, 
                    secuenciaIda, 
                    secuenciaVuelta,
                    allStops
            );

            if (ruta != null) {
                return ruta;
            }
        }

        return null; // No se encontró ruta válida
    }

    // MÉTODOS AUXILIARES

    /**
     * Equivalente a nearestLocation.findClosestLocations
     */
    private List<Parada> findClosestLocations(double lat, double lon, List<Parada> paradas, double radioMetros) {
        return paradas.stream()
                .filter(p -> calcularDistanciaMetros(lat, lon, p.getLatitud(), p.getLongitud()) <= radioMetros)
                .sorted(Comparator.comparingDouble(p -> calcularDistanciaMetros(lat, lon, p.getLatitud(), p.getLongitud())))
                .limit(10) // Max results
                .collect(Collectors.toList());
    }

    /**
     * Encuentra qué líneas coinciden entre el grupo de origen y destino
     */
    private Set<Integer> findCommonBusLines(List<Parada> origen, List<Parada> destino) {
        Set<Integer> lineasOrigen = origen.stream()
                .flatMap(p -> p.getLineas().stream())
                .collect(Collectors.toSet());

        Set<Integer> lineasDestino = destino.stream()
                .flatMap(p -> p.getLineas().stream())
                .collect(Collectors.toSet());

        // Intersección
        lineasOrigen.retainAll(lineasDestino);
        return lineasOrigen;
    }

    /**
     * Encontrar si el sentido es de ida o vuelta
     */
    private RutaResponse identificarSentido(
            List<Parada> candidatosOrigen,
            List<Parada> candidatosDestino,
            List<ParadaSecuencia> secuenciaIda,
            List<ParadaSecuencia> secuenciaVuelta,
            List<Parada> todasLasParadas) {

        // 1. Probar sentido IDA
        RutaResponse rutaIda = intentarRuta(candidatosOrigen, candidatosDestino, secuenciaIda, todasLasParadas);
        if (rutaIda != null) return rutaIda;

        // 2. Probar sentido VUELTA
        RutaResponse rutaVuelta = intentarRuta(candidatosOrigen, candidatosDestino, secuenciaVuelta, todasLasParadas);
        if (rutaVuelta != null) return rutaVuelta;

        return null;
    }

    private RutaResponse intentarRuta(List<Parada> candidatosOrg, List<Parada> candidatosDest, List<ParadaSecuencia> secuencia, List<Parada> todas) {
        // Convertir secuencia a lista ordenada de IDs de paradas para buscar índices fácil
        List<Integer> ordenParadas = secuencia.stream().map(ParadaSecuencia::getParadaId).toList();

        int mejorIndiceInicio = -1;
        int mejorIndiceFin = -1;
        
        // Buscar la mejor combinación de índices donde Inicio < Fin
        for (Parada pOrg : candidatosOrg) {
            int idxInicio = ordenParadas.indexOf(pOrg.getId());
            if (idxInicio == -1) continue; // Esta parada candidata no está en este sentido de la línea

            for (Parada pDest : candidatosDest) {
                int idxFin = ordenParadas.indexOf(pDest.getId());
                if (idxFin == -1) continue;

                // CLAVE: El inicio debe estar ANTES que el destino en la secuencia
                if (idxInicio < idxFin) {
                    // Aquí podrías añadir lógica para elegir la "más cercana" si hay varias opciones
                    // De momento devolvemos la primera válida
                    return construirRespuesta(secuencia, idxInicio, idxFin, todas);
                }
            }
        }
        return null;
    }

    /**
     * Construye la lista de waypoints (paradas intermedias)
     */
    private RutaResponse construirRespuesta(List<ParadaSecuencia> secuencia, int idxInicio, int idxFin, List<Parada> todas) {
        RutaResponse response = new RutaResponse();
        List<String> nombresParadas = new ArrayList<>();
        
        // Recorremos la secuencia desde el inicio hasta el fin
        for (int i = idxInicio; i <= idxFin; i++) {
            int paradaId = secuencia.get(i).getParadaId();
            // Buscar el nombre de la parada por ID (Optimizable con un Map)
            todas.stream().filter(p -> p.getId() == paradaId).findFirst()
                    .ifPresent(p -> nombresParadas.add(p.getNombre()));
        }
        
        response.setInstrucciones(nombresParadas);
        // Aquí calcularías tiempo estimado, etc.
        return response;
    }

    /**
     * Fórmula de Haversine para calcular distancia entre dos coordenadas en metros.
     */
    private double calcularDistanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = R * c;
        return distanceKm * 1000;
    }
}