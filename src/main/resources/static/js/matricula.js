function cargarCursos(grado) {
    if (!grado) {
        document.getElementById('cursosContainer').innerHTML = `
                    <div class="alert alert-info">
                        Seleccione un grado para ver los cursos
                    </div>`;
        return;
    }

    fetch('/matriculas/cursos?grado=' + encodeURIComponent(grado))
        .then(response => response.json())
        .then(cursos => {
            let html = '';
            if (cursos.length > 0) {
                html = '<h6 class="mb-3">Los siguientes cursos serán asignados:</h6>';
                cursos.forEach(curso => {
                    html += `
                                <div class="curso-item">
                                    <strong>${curso.nombreCurso}</strong>
                                    <small class="text-muted d-block">Grado: ${curso.gradoCurso}</small>
                                </div>`;
                });
            } else {
                html = '<div class="alert alert-warning">No hay cursos para este grado</div>';
            }
            document.getElementById('cursosContainer').innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('cursosContainer').innerHTML = `
                        <div class="alert alert-danger">
                            Error al cargar cursos
                        </div>`;
            console.log("Cursos recibidos:", cursos); // 👈 depuración
            let html = '';
            if (cursos.length > 0) {
                html = '<h6 class="mb-3">Los siguientes cursos serán asignados:</h6>';
                cursos.forEach(curso => {
                    html += `
                <div class="curso-item">
                    <strong>${curso.nombreCurso}</strong>
                    <small class="text-muted d-block">Grado: ${curso.gradoCurso}</small>
                </div>`;
                });
            } else {
                html = '<div class="alert alert-warning">No hay cursos para este grado</div>';
            }
            document.getElementById('cursosContainer').innerHTML = html;

        });

}

$(document).ready(function () {

    // Carga dinámica cuando cambia el departamento
    $('#departamento').change(function () {
        const departamento = $(this).val();
        $('#provincia').empty().append('<option value="">Seleccione una provincia</option>');
        $('#distrito').empty().append('<option value="">Seleccione un distrito</option>');

        if (departamento) {
            $.get('/api/ubigeo/provincias', { departamento }, function (data) {
                $.each(data, function (i, provincia) {
                    $('#provincia').append('<option value="' + provincia + '">' + provincia + '</option>');
                });
            });
        }
    });

    // Carga dinámica cuando cambia la provincia
    $('#provincia').change(function () {
        const departamento = $('#departamento').val();
        const provincia = $(this).val();
        $('#distrito').empty().append('<option value="">Seleccione un distrito</option>');

        if (departamento && provincia) {
            $.get('/api/ubigeo/distritos', { departamento, provincia }, function (data) {
                $.each(data, function (i, distrito) {
                    $('#distrito').append('<option value="' + distrito + '">' + distrito + '</option>');
                });
            });
        }
    });

    // Auto-cargar provincias y distritos si ya hay valores (modo edición)
    const selectedDepartamento = $('#departamento').val();
    const selectedProvincia = $('#provincia').attr('data-value');
    const selectedDistrito = $('#distrito').attr('data-value');

    if (selectedDepartamento) {
        $.get('/api/ubigeo/provincias', { departamento: selectedDepartamento }, function (provincias) {
            $('#provincia').empty().append('<option value="">Seleccione una provincia</option>');
            $.each(provincias, function (i, prov) {
                const selected = prov === selectedProvincia ? 'selected' : '';
                $('#provincia').append('<option value="' + prov + '" ' + selected + '>' + prov + '</option>');
            });

            if (selectedProvincia) {
                $.get('/api/ubigeo/distritos', { departamento: selectedDepartamento, provincia: selectedProvincia }, function (distritos) {
                    $('#distrito').empty().append('<option value="">Seleccione un distrito</option>');
                    $.each(distritos, function (i, dist) {
                        const selected = dist === selectedDistrito ? 'selected' : '';
                        $('#distrito').append('<option value="' + dist + '" ' + selected + '>' + dist + '</option>');
                    });
                });
            }
        });
    }
});

