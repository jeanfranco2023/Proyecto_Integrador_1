package com.app.sistema.matricula.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.InputStream;
import java.sql.Date;

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
import com.app.sistema.matricula.repository.CursoRepository;
import com.app.sistema.matricula.repository.MatriculaRepository;
import com.app.sistema.matricula.service.AlumnoService;
import com.app.sistema.matricula.service.DetalleMatriculaService;
import com.app.sistema.matricula.service.MatriculaService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;

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

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute("matriculaDTO", new MatriculaDTO());
        model.addAttribute("grados", cursoRepository.findDistinctGradoCurso());
        model.addAttribute("periodos", List.of("2025-I", "2025-II"));
        model.addAttribute("fechaHoy", LocalDate.now());
        return "matricula";
    }

    @GetMapping("/cursos")
    @ResponseBody
    public List<Cursos> obtenerCursosPorGrado(@RequestParam String grado) {
        return cursoRepository.findByGradoCurso(grado);
    }

    @PostMapping("/guardar")
    public String guardarMatricula(@ModelAttribute MatriculaDTO dto,
            RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            boolean esNuevo = (dto.getIdMatricula() == null);

            // Validación de DNI
            if (dto.getAlumno().getDniAlumno() == null || dto.getAlumno().getDniAlumno().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El DNI es requerido");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            if (!dto.getAlumno().getDniAlumno().matches("\\d{8}")) {
                redirectAttributes.addFlashAttribute("error", "DNI debe tener 8 dígitos");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            // Solo verificar duplicados si es nuevo
            if (esNuevo && alumnoService.existePorDni(dto.getAlumno().getDniAlumno())) {
                redirectAttributes.addFlashAttribute("error",
                        "El DNI " + dto.getAlumno().getDniAlumno() + " ya está registrado");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            dto.getAlumno().setRol("Alumno");
            alumnoService.insertar(dto.getAlumno()); // Inserta o actualiza según el id

            Alumnos alumnoGuardado = alumnoService.buscarPorId(dto.getAlumno().getIdAlumno());
            if (alumnoGuardado == null) {
                redirectAttributes.addFlashAttribute("error", "Error al recuperar datos del alumno");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            Matricula matricula = esNuevo ? new Matricula() : matriculaService.buscarPorId(dto.getIdMatricula());
            if (matricula == null && !esNuevo) {
                redirectAttributes.addFlashAttribute("error", "Matrícula no encontrada para actualizar");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            matricula.setAlumno(dto.getAlumno());
            matricula.setFechaMatricula(dto.getFechaMatricula());
            matricula.setPeriodoAcademico(dto.getPeriodoAcademico());

            Matricula matriculaGuardada = matriculaRepository.save(matricula);

            // Si es actualización, podrías querer eliminar los cursos previos primero
            if (!esNuevo) {
                detalleMatriculaService.eliminarPorMatricula(matriculaGuardada.getIdMatricula());
            }

            List<Cursos> cursos = cursoRepository.findByGradoCurso(dto.getGrado());
            cursos.forEach(curso -> {
                DetalleMatricula detalle = new DetalleMatricula();
                detalle.setMatricula(matriculaGuardada);
                detalle.setCurso(curso);
                detalleMatriculaService.insertar(detalle);
            });

            redirectAttributes.addFlashAttribute("success",
                    "Matrícula " + (esNuevo ? "registrada" : "actualizada") + " exitosamente para "
                            + alumnoGuardado.getNombreAlumno() + " " + alumnoGuardado.getApellidoAlumno());

            return "redirect:/usuarios/dashboard?seccion=matricula";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al registrar/actualizar: "
                            + (e.getMessage() != null ? e.getMessage() : "consulte con el administrador"));
            return "redirect:/usuarios/dashboard?seccion=matricula";
        }
    }

    @PostMapping("/editar/{id}")
    public String editarMatricula(MatriculaDTO dto) {
        Matricula matricula = matriculaRepository.findById(dto.getIdMatricula()).orElse(null);
        if (matricula != null) {
            matricula.setIdMatricula(dto.getIdMatricula());
            matricula.setAlumno(dto.getAlumno());
            matricula.setFechaMatricula(dto.getFechaMatricula());
            matricula.setPeriodoAcademico(dto.getPeriodoAcademico());
            matriculaRepository.save(matricula);
        }
        return "redirect:/usuarios/dashboard?seccion=matricula";
    }

    @PostMapping("/generar/reporte")
    public String generarReporteMatricula(@ModelAttribute MatriculaDTO dto, Model model) {
        try {
            Matricula matricula = matriculaService.buscarPorId(dto.getIdMatricula());
            if (matricula == null) {
                model.addAttribute("error", "Matrícula no encontrada");
                return "redirect:/usuarios/dashboard?seccion=matricula";
            }

            model.addAttribute("matricula", matricula);
            model.addAttribute("detalleMatriculas",
                    detalleMatriculaService.buscarPorId(matricula.getIdMatricula()));
            return "reporteMatricula";

        } catch (Exception e) {
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "redirect:/usuarios/dashboard?seccion=matricula";
        }
    }

    @GetMapping("/reporte/pdf")
    public void generarFichaMatricula(@RequestParam Integer id, HttpServletResponse response) throws Exception {

        // 1. Cargar y compilar el archivo .jrxml
        InputStream reporteStream = getClass().getResourceAsStream("/templates/report/reporte.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

        // 2. Obtener datos del alumno desde tu servicio (ajusta según tu estructura)
        Alumnos alumno = alumnoService.buscarPorId(id);
        if (alumno == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alumno no encontrado");
            return;
        }

        // 3. Obtener rutas reales de imágenes en /static/img/
        File insignia = new ClassPathResource("static/img/insignia.png").getFile();
        File foto = new ClassPathResource("static/img/mclovin.jpg").getFile();

        // 4. Construir los parámetros que necesita el .jrxml
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("NombreEstudiante", alumno.getNombreAlumno());
        parametros.put("ApellidoEstudiante", alumno.getApellidoAlumno());
        parametros.put("FechaDeNacimiento", Date.valueOf(alumno.getFechaNacimiento()));
        parametros.put("CorreoElectronico", alumno.getCorreoAlumno());
        parametros.put("NombreApoderado", alumno.getNombreApoderado()); // ajusta si necesario
        parametros.put("Nivel", alumno.getMatriculas().stream()
            .findFirst()
            .map(m -> m.getDetalles().stream()
                .findFirst()
                .map(d -> {
                    if (d.getCurso() != null && d.getCurso().getGradoCurso() != null) {
                        return d.getCurso().getGradoCurso().toLowerCase().contains("secundaria") ? "Secundaria" : "Primaria";
                    } else {
                        return "No asignado";
                    }
                })
                .orElse("No asignado"))
            .orElse("No asignado"));
        
        parametros.put("Grado", alumno.getMatriculas().stream()
            .findFirst()
            .map(m -> m.getDetalles().stream()
                .findFirst()
                .map(d -> d.getSeccion() != null && d.getSeccion().getGradoSeccion() != null ? d.getSeccion().getGradoSeccion() : "No asignado")
                .orElse("No asignado"))
            .orElse("No asignado"));

        parametros.put("Seccion", alumno.getMatriculas().stream()
            .findFirst()
            .map(m -> m.getDetalles().stream()
                .findFirst()
                .map(d -> d.getSeccion() != null && d.getSeccion().getNombreSeccion() != null ? d.getSeccion().getNombreSeccion() : "No asignado")
                .orElse("No asignado"))
            .orElse("No asignado"));

        parametros.put("Turno", alumno.getMatriculas().stream()
            .findFirst()
            .map(m -> m.getDetalles().stream()
                .findFirst()
                .map(d -> d.getSeccion() != null && d.getSeccion().getTurnoSeccion() != null ? d.getSeccion().getTurnoSeccion() : "No asignado")
                .orElse("No asignado"))
            .orElse("No asignado"));

        // 5. Cargar imágenes como rutas absolutas
        parametros.put("imagenInsignia", insignia.getAbsolutePath());
        parametros.put("fotoAlumno", foto.getAbsolutePath());

        // 6. Llenar el reporte (sin datasource, solo parámetros)
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());

        // 7. Configurar respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=ficha_matricula.pdf");

        // 8. Exportar reporte al navegador
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }


}