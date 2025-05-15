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
                });
        }
