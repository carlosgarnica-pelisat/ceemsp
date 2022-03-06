package com.pelisat.cesp.ceemsp.restceemsp;

import com.pelisat.cesp.ceemsp.database.model.*;
import com.pelisat.cesp.ceemsp.database.repository.*;
import com.pelisat.cesp.ceemsp.infrastructure.utils.DaoHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImportadorTest {
    @Autowired
    private DaoHelper<CommonModel> daoHelper;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    @Autowired
    private CalleRepository calleRepository;

    @Autowired
    private ColoniaRepository coloniaRepository;

    private final Logger logger = LoggerFactory.getLogger(ImportadorTest.class);

    @Ignore
    @Test
    public void importarColonias() throws Exception {
        Scanner sc = new Scanner(new File("colonias.csv"));
        sc.useDelimiter("\n");

        while(sc.hasNext()) {
            String csvRow = sc.next();
            String[] csvValues = csvRow.split(",");
            try {
                Colonia colonia = new Colonia();
                if(csvValues[3].length() == 4) {
                    colonia.setCodigoPostal("0" + csvValues[3]);
                } else {
                    colonia.setCodigoPostal(csvValues[3]);
                }
                colonia.setUuid(RandomStringUtils.randomAlphanumeric(12));
                colonia.setMunicipio(Integer.parseInt(csvValues[1]));
                colonia.setNombre(csvValues[2]);
                colonia.setEstado(Integer.parseInt(csvValues[4].replace("\r", "")));

                daoHelper.fulfillAuditorFields(true, colonia, 1);
                coloniaRepository.save(colonia);
            } catch (Exception ex) {
                logger.warn("El dato no se pudo insertar. Motivo: {}", ex.getMessage());
            }
        }
    }

    @Ignore
    @Test
    public void importarCalles() throws Exception {
        Scanner sc = new Scanner(new File("calles.csv"));
        sc.useDelimiter("\n");

        while(sc.hasNext()) {
            String csvRow = sc.next();
            String[] csvValues = csvRow.split(",");
            try {
                Calle calle = new Calle();
                calle.setUuid(RandomStringUtils.randomAlphanumeric(12));
                calle.setNombre(csvValues[1].replace("\r", ""));
                daoHelper.fulfillAuditorFields(true, calle, 1);
                calleRepository.save(calle);
            } catch (Exception ex) {
                logger.warn("El dato no se pudo insertar. Motivo: {}", ex.getMessage());
            }
        }
    }

    @Ignore
    @Test
    public void importarEstados() throws Exception {
        Scanner sc = new Scanner(new File("estados.csv"));
        sc.useDelimiter("\n");

        while(sc.hasNext()) {
            String csvRow = sc.next();
            String[] csvValues = csvRow.split(",");
            try {
                Estado estado = new Estado();
                estado.setUuid(RandomStringUtils.randomAlphanumeric(12));
                estado.setNombre(csvValues[1].replace("\r", ""));
                daoHelper.fulfillAuditorFields(true, estado, 1);
                estadoRepository.save(estado);
            } catch (Exception ex) {
                logger.warn("El dato no se pudo insertar. Motivo: {}", ex.getMessage());
            }
        }
    }

    @Ignore
    @Test
    public void importarLocalidades() throws Exception {
        Scanner sc = new Scanner(new File("localidades.csv"));
        sc.useDelimiter("\n");

        while(sc.hasNext()) {
            String csvRow = sc.next();
            String[] csvValues = csvRow.split(",");

            try {
                Localidad localidad = new Localidad();
                localidad.setUuid(RandomStringUtils.randomAlphanumeric(12));
                localidad.setMunicipio(Integer.parseInt(csvValues[2]));
                String estado = csvValues[4].replace("\r", "");
                localidad.setEstado(Integer.parseInt(estado));
                localidad.setNombre(csvValues[1]);
                daoHelper.fulfillAuditorFields(true, localidad, 1);
                localidadRepository.save(localidad);
            } catch(Exception ex) {
                logger.warn("El dato no se pudo insertar. Motivo: {}", ex.getMessage());
            }
        }
    }

    @Ignore
    @Test
    public void importarMunicipios() throws Exception {
        Scanner sc = new Scanner(new File("municipios.csv"));
        sc.useDelimiter("\n");

        while(sc.hasNext()) {
            String csvRow = sc.next();
            String[] csvValues = csvRow.split(",");

            try {
                Municipio municipio = new Municipio();
                municipio.setUuid(RandomStringUtils.randomAlphanumeric(12));
                municipio.setClave(Integer.parseInt(csvValues[1]));
                municipio.setEstado(Integer.parseInt(csvValues[2]));
                municipio.setNombre(csvValues[3].replace("\r", ""));
                daoHelper.fulfillAuditorFields(true, municipio, 1);
                municipioRepository.save(municipio);
            } catch(Exception ex) {
                logger.warn("El dato no se pudo insertar. Motivo: {}", ex.getMessage());
            }
        }
    }
}
