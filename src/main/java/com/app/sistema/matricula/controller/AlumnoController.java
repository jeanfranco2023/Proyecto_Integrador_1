package com.app.sistema.matricula.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.app.sistema.matricula.dto.AlumnoDTO;
import com.app.sistema.matricula.models.Alumnos;

import com.app.sistema.matricula.models.Nota;
import com.app.sistema.matricula.models.Padres;
import com.app.sistema.matricula.repository.NotaRepository;
import com.app.sistema.matricula.service.AlumnoService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;
    @Autowired
    private NotaRepository notaRepository;

    @GetMapping("/registroAlumno")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("alumno", new AlumnoDTO());
        model.addAttribute("alumnos", alumnoService.listar());
        return "registroAlumno";
    }

    @PostMapping("/guardarAlumno")
    public String guardarAlumno(@ModelAttribute("alumno") AlumnoDTO alumnoDTO) {
        Alumnos alumno = new Alumnos();
        alumno.setIdAlumno(alumnoDTO.getIdAlumno());
        alumno.setNombreAlumno(alumnoDTO.getNombreAlumno());
        alumno.setApellidoAlumno(alumnoDTO.getApellidoAlumno());
        alumno.setDniAlumno(alumnoDTO.getDniAlumno());
        alumno.setCorreoAlumno(alumnoDTO.getCorreoAlumno());
        alumno.setPasswordAlumno(alumnoDTO.getPasswordAlumno());
        alumno.setRol("Alumno");
        List<Padres> padresList = new ArrayList<>();
        Padres padre = new Padres();
        padre.setNombrePadre(alumnoDTO.getNombreApoderado());
        padresList.add(padre);
        alumno.setPadres(padresList);
        alumno.setFechaNacimiento(alumnoDTO.getFechaNacimiento());
        alumnoService.insertar(alumno);

        return "redirect:/alumnos/registroAlumno";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Integer id, ModelMap model) {
        Alumnos alumno = alumnoService.buscarPorId(id);
        if (alumno != null) {
            AlumnoDTO alumnoDTO = new AlumnoDTO();
            alumnoDTO.setIdAlumno(alumno.getIdAlumno());
            alumnoDTO.setNombreAlumno(alumno.getNombreAlumno());
            alumnoDTO.setApellidoAlumno(alumno.getApellidoAlumno());
            alumnoDTO.setDniAlumno(alumno.getDniAlumno());
            alumnoDTO.setCorreoAlumno(alumno.getCorreoAlumno());
            alumnoDTO.setPasswordAlumno(alumno.getPasswordAlumno());
            alumnoDTO.setNombreApoderado(alumno.getPadres().stream()
                    .findFirst()
                    .map(Padres::getNombrePadre)
                    .orElse(""));
            alumnoDTO.setFechaNacimiento(alumno.getFechaNacimiento());
            model.addAttribute("usuario", alumnoDTO);
            return "editarAlumno";
        }
        return "redirect:/usuarios/dashboard?seccion=principal";
    }

    @PostMapping("/actualizar")
    public String actualizarAlumno(@ModelAttribute("alumno") AlumnoDTO alumnoDTO) {
        Alumnos alumno = new Alumnos();
        alumno.setIdAlumno(alumnoDTO.getIdAlumno());
        alumno.setNombreAlumno(alumnoDTO.getNombreAlumno());
        alumno.setApellidoAlumno(alumnoDTO.getApellidoAlumno());
        alumno.setDniAlumno(alumnoDTO.getDniAlumno());
        alumno.setCorreoAlumno(alumnoDTO.getCorreoAlumno());
        alumno.setPasswordAlumno(alumnoDTO.getPasswordAlumno());
        alumno.setRol("Alumno");
        List<Padres> padresList = new ArrayList<>();
        Padres padre = new Padres();
        padresList.add(padre);
        alumno.setPadres(padresList);
        padre.setNombrePadre(alumnoDTO.getNombreApoderado());
        alumno.setFechaNacimiento(alumnoDTO.getFechaNacimiento());
        alumnoService.insertar(alumno);

        return "redirect:/alumnos/registro";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAlumno(@PathVariable("id") Integer id) {
        Alumnos alumno = alumnoService.buscarPorId(id);
        if (alumno != null) {
            alumnoService.eliminar(alumno);
            return "redirect:/usuarios/dashboard?seccion=lista-alumnos";
        }
        return "redirect:/alumnos/registroAlumno?error=Alumno no encontrado";
    }

    @GetMapping("/reporte-notas")
    public void generarReporteNotas(@RequestParam Integer idAlumno,
            HttpServletResponse response) throws Exception {
        Alumnos alumno = alumnoService.buscarPorId(idAlumno);
        if (alumno == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Alumno no encontrado");
            return;
        }

        // 1. Cargar plantilla .jrxml
        InputStream reporteStream = getClass().getClassLoader()
                .getResourceAsStream("templates/report/reporteNotas.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

        // 2. Obtener notas del alumno y agrupar por curso
        List<Nota> notasDelAlumno = notaRepository.findAll().stream().toList();

        Map<String, List<Nota>> notasAgrupadasPorCurso = notasDelAlumno.stream()
                .collect(Collectors.groupingBy(n -> n.getCurso().getNombreCurso()));

        // 3. Preparar datos para el reporte
        List<Map<String, Object>> datosCursos = new ArrayList<>();

        for (Map.Entry<String, List<Nota>> entry : notasAgrupadasPorCurso.entrySet()) {
            String nombreCurso = entry.getKey();
            List<Nota> notasCurso = entry.getValue().stream()
                    .sorted(Comparator.comparing(Nota::getBimestre))
                    .limit(4)
                    .collect(Collectors.toList());

            Map<String, Object> fila = new HashMap<>();
            fila.put("nombreCurso", nombreCurso);

            for (int i = 0; i < 4; i++) {
                fila.put("nota" + (i + 1), i < notasCurso.size()
                        ? (int) Math.round(notasCurso.get(i).getValorNota())
                        : 0);
            }

            BigDecimal promedio = notasCurso.isEmpty() ? BigDecimal.ZERO :
                notasCurso.stream()
                    .map(Nota::getValorNota)
                    .map(BigDecimal::valueOf)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(notasCurso.size()), 2, RoundingMode.HALF_UP);

            fila.put("promedio", promedio);

            datosCursos.add(fila);
        }

        // 4. Establecer parámetros del reporte
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nombresEstudiante", alumno.getNombreAlumno());
        parameters.put("apellidosEstudiante", alumno.getApellidoAlumno());
        parameters.put("seccionEstudiante", alumno.getMatriculas().stream()
                .findFirst()
                .map(m -> m.getDetalles().stream()
                        .findFirst()
                        .map(d -> d.getSeccion().getNombreSeccion())
                        .orElse("No asignado"))
                .orElse("No asignado"));
        parameters.put("LogoColegio", new ClassPathResource("static/img/insignia.png").getURL().toString());

        // 5. Generar el PDF
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datosCursos);
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=ficha_matricula.pdf");
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        var lista=notaRepository.findAll();
        System.out.println("Total de notas: " + lista.size());
        System.out.println("Notas del alumno " + notasDelAlumno.size());
        notasDelAlumno.forEach(System.out::println);
        System.out.println("Tamaño de datosCursos: " + datosCursos.size());
        datosCursos.forEach(System.out::println);

    }

    @ModelAttribute("rol")
    public String rol(HttpSession session) {
        if (session.getAttribute("alumnoId") != null) {
            return "Alumno";
        } else if (session.getAttribute("docenteId") != null) {
            return "Docente";
        } else if (session.getAttribute("adminIniciado") != null) {
            return "Administrador";
        }
        return null;
    }

}
