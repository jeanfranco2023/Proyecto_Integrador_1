package com.app.sistema.matricula.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.sistema.matricula.dto.MatriculaDTO;
import com.app.sistema.matricula.models.Alumnos;
import com.app.sistema.matricula.models.Cursos;
import com.app.sistema.matricula.models.DetalleMatricula;
import com.app.sistema.matricula.models.Matricula;
import com.app.sistema.matricula.models.Padres;
import com.app.sistema.matricula.repository.CursoRepository;
import com.app.sistema.matricula.repository.MatriculaRepository;
import com.app.sistema.matricula.service.AlumnoService;
import com.app.sistema.matricula.service.DetalleMatriculaService;
import com.app.sistema.matricula.service.MatriculaService;
import com.app.sistema.matricula.service.PadresService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;

@Controller
@RequestMapping("/matriculas")
public class MatriculaController {

    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private MatriculaService matriculaService;
    @Autowired
    private MatriculaRepository matriculaRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private DetalleMatriculaService detalleMatriculaService;
    @Autowired
    private PadresService padresService;

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("matriculaDTO", new MatriculaDTO());
        model.addAttribute("grados", cursoRepository.findDistinctGradosDesdeSeccion());
        model.addAttribute("periodos", List.of("2025-I", "2025-II"));
        model.addAttribute("fechaHoy", LocalDate.now());
        return "matricula";
    }

    @GetMapping("/cursos")
    @ResponseBody
    public List<Cursos> obtenerCursosPorGrado(@RequestParam String grado) {
        return cursoRepository.findByGradoCurso(grado);
    }

    @SuppressWarnings("null")
    @PostMapping("/guardar")
    public String guardarMatricula(@ModelAttribute MatriculaDTO dto,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            boolean esNuevo = (dto.getIdMatricula() == null);

            Alumnos alumno = dto.getAlumno();

            // Validación de DNI
            if (alumno.getDniAlumno() == null || alumno.getDniAlumno().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El DNI es requerido");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            if (!alumno.getDniAlumno().matches("\\d{8}")) {
                redirectAttributes.addFlashAttribute("error", "DNI debe tener 8 dígitos");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            if (esNuevo && alumnoService.existePorDni(alumno.getDniAlumno())) {
                redirectAttributes.addFlashAttribute("error",
                        "El DNI " + alumno.getDniAlumno() + " ya está registrado");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            // Rol por defecto
            alumno.setRol("Alumno");

            // Guardar o actualizar alumno
            alumnoService.insertar(alumno);

            // Buscar el alumno ya persistido para tener el ID correcto
            Alumnos alumnoGuardado = alumnoService.buscarPorId(alumno.getIdAlumno());
            if (alumnoGuardado == null) {
                redirectAttributes.addFlashAttribute("error", "Error al guardar el alumno");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            // ------------------------------
            // Guardar Padres (si vienen)
            // ------------------------------
            if (alumno.getPadres() != null && !alumno.getPadres().isEmpty()) {
                for (Padres padre : alumno.getPadres()) {
                    padre.setAlumno(alumnoGuardado); // asignar relación
                    padresService.insertar(padre); // guarda o actualiza
                }
            }

            Matricula matricula = esNuevo ? new Matricula() : matriculaService.buscarPorId(dto.getIdMatricula());
            if (matricula == null && !esNuevo) {
                redirectAttributes.addFlashAttribute("error", "No se encontró la matrícula a actualizar");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            matricula.setAlumno(alumnoGuardado);
            matricula.setFechaMatricula(dto.getFechaMatricula());
            matricula.setPeriodoAcademico(dto.getPeriodoAcademico());

            Matricula matriculaGuardada = matriculaRepository.save(matricula);

            if (!esNuevo) {
                detalleMatriculaService.eliminarPorMatricula(matriculaGuardada.getIdMatricula());
            }

            List<Cursos> cursos = cursoRepository.findByGradoCurso(dto.getGrado());
            for (Cursos curso : cursos) {
                DetalleMatricula detalle = new DetalleMatricula();
                detalle.setMatricula(matriculaGuardada);
                detalle.setCurso(curso);
                detalleMatriculaService.insertar(detalle);
            }

            redirectAttributes.addFlashAttribute("success",
                    "Matrícula " + (esNuevo ? "registrada" : "actualizada") + " exitosamente para "
                            + alumnoGuardado.getNombreAlumno() + " " + alumnoGuardado.getApellidoAlumno());

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar/actualizar: "
                            + (e.getMessage() != null ? e.getMessage() : "consulte con el administrador"));
        }

        return "redirect:/usuarios/dashboard?seccion=matricula";
    }

    @GetMapping("/reporte/pdf")
    public void generarFichaMatricula(@RequestParam Integer id, HttpServletResponse response) throws Exception {

        InputStream reporteStream = getClass().getClassLoader()
                .getResourceAsStream("templates/report/reportePrueba.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

        Alumnos alumno = alumnoService.buscarPorId(id);
        if (alumno == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alumno no encontrado");
            return;
        }

        File insignia = new ClassPathResource("static/img/insignia.png").getFile();

        Map<String, Object> parametros = new HashMap<>();

        // Datos del alumno
        parametros.put("nombre", alumno.getNombreAlumno() != null ? alumno.getNombreAlumno() : "No asignado");
        parametros.put("Apellidos", alumno.getApellidoAlumno() != null ? alumno.getApellidoAlumno() : "No asignado");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        parametros.put("fecha de nacimiento",
                alumno.getFechaNacimiento() != null ? alumno.getFechaNacimiento().format(formatter) : "No asignado");

        parametros.put("DNI", alumno.getDniAlumno() != null ? alumno.getDniAlumno() : "No asignado");
        parametros.put("correoAlumno", alumno.getCorreoAlumno() != null ? alumno.getCorreoAlumno() : "No asignado");
        parametros.put("Departamento", alumno.getDepartamento() != null ? alumno.getDepartamento() : "No asignado");
        parametros.put("provincia", alumno.getProvincia() != null ? alumno.getProvincia() : "No asignado");
        parametros.put("distrito", alumno.getDistrito() != null ? alumno.getDistrito() : "No asignado");
        parametros.put("sexo", alumno.getSexo() != null ? alumno.getSexo() : "No asignado");
        parametros.put("edad", alumno.getEdad() != 0 ? String.valueOf(alumno.getEdad()) : "No asignado");

        // Datos del padre (primer apoderado si existe)
        Padres padre = (alumno.getPadres() != null && !alumno.getPadres().isEmpty()) ? alumno.getPadres().get(0) : null;

        parametros.put("NombrePadres", padre != null
                ? (padre.getNombrePadre() + " " + padre.getApellidoPadre())
                : "No asignado");
        parametros.put("telefonoPadres", (padre != null && padre.getTelefonoPadre() != null)
                ? padre.getTelefonoPadre()
                : "No asignado");
        parametros.put("dni padres", (padre != null && padre.getDniPadre() != null)
                ? padre.getDniPadre()
                : "No asignado");
        parametros.put("parentesco", (padre != null && padre.getParentesco() != null)
                ? padre.getParentesco()
                : "No asignado");

        parametros.put("Nivel", alumnoService.ObtenerNivelPorAlumno(id));

        parametros.put("Grado", alumno.getMatriculas().stream()
                .findFirst()
                .map(m -> m.getDetalles().stream()
                        .findFirst()
                        .map(d -> d.getCurso() != null && d.getCurso().getGradoCurso() != null
                                ? d.getCurso().getGradoCurso()
                                : "No asignado")
                        .orElse("No asignado"))
                .orElse("No asignado"));

        parametros.put("Seccion", alumno.getMatriculas().stream()
                .findFirst()
                .map(m -> m.getDetalles().stream()
                        .findFirst()
                        .map(d -> d.getSeccion() != null ? d.getSeccion().getNombreSeccion() : "A")
                        .orElse("No asignado"))
                .orElse("No asignado"));

        parametros.put("LogoColegio", new FileInputStream(insignia)); // Imagen como InputStream

        // Recomendado: evitar paginación si es un solo reporte simple
        parametros.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);

        // Generar reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=ficha_matricula.pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    @GetMapping("/reporte/general/pdf")
    public void generarFichaMatriculaCompleta(HttpServletResponse response) throws Exception {
        Alumnos alumnoEjemplo = alumnoService.listar().stream().findFirst().orElse(null);
        if (alumnoEjemplo == null) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "No hay alumnos para mostrar");
            return;
        }

        // 1. Cargar plantilla
        InputStream reporteStream = getClass().getResourceAsStream("/templates/report/reporteGeneralMatricula.jrxml");
        JasperDesign jasperDesign = JRXmlLoader.load(reporteStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        // 2. Preparar los datos
        List<Alumnos> alumnos = alumnoService.listar();
        List<Map<String, Object>> listaDatos = new ArrayList<>();

        for (Alumnos alumno : alumnos) {
            Map<String, Object> datos = new HashMap<>();

            datos.put("nombre", alumno.getNombreAlumno());
            datos.put("Apellidos", alumno.getApellidoAlumno());
            datos.put("DNI", alumno.getDniAlumno());
            datos.put("sexo", alumno.getSexo());
            datos.put("fecha de nacimiento", alumno.getFechaNacimiento() != null
                    ? alumno.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : "No asignado");
            datos.put("edad", String.valueOf(alumno.getEdad()));
            datos.put("Departamento", alumno.getDepartamento());
            datos.put("provincia", alumno.getProvincia());
            datos.put("distrito", alumno.getDistrito());

            // Datos del padre o apoderado
            Padres padre = (alumno.getPadres() != null && !alumno.getPadres().isEmpty()) ? alumno.getPadres().get(0)
                    : null;
            datos.put("NombrePadres",
                    padre != null ? padre.getNombrePadre() + " " + padre.getApellidoPadre() : "No asignado");
            datos.put("telefono padres", padre != null ? padre.getTelefonoPadre() : "No asignado");
            datos.put("dni padres", padre != null ? padre.getDniPadre() : "No asignado");
            datos.put("parentesco", padre != null ? padre.getParentesco() : "No asignado");

            // Datos académicos
            datos.put("Nivel", alumnoService.ObtenerNivelPorAlumno(alumno.getIdAlumno()));
            datos.put("Grado", alumno.getMatriculas().stream()
                    .findFirst()
                    .flatMap(m -> m.getDetalles().stream().findFirst()
                            .map(d -> d.getCurso() != null ? d.getCurso().getGradoCurso() : "No asignado"))
                    .orElse("No asignado"));
            datos.put("Seccion", alumno.getMatriculas().stream()
                    .findFirst()
                    .flatMap(m -> m.getDetalles().stream().findFirst()
                            .map(d -> d.getSeccion() != null ? d.getSeccion().getNombreSeccion() : "No asignado"))
                    .orElse("No asignado"));

            listaDatos.add(datos);
        }

        // 3. Parámetros del reporte
        Map<String, Object> parametros = new HashMap<>();
        File logoFile = new ClassPathResource("static/img/insignia.png").getFile();
        parametros.put("LogoColegio", new FileInputStream(logoFile));

        // 4. Data source y generación del reporte
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaDatos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

        // 5. Exportar a PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte_general_matricula.pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        System.out.println("Total de alumnos: " + listaDatos.size());
        listaDatos.forEach(System.out::println);
    }

}