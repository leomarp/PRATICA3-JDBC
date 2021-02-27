
pubpublic class Controlador extends BaseControlador {

    public Controlador(Javalin app) {
        super(app);
        registrandoPlantillas();
    }

    /**
     * Registrando los sistemas de plantillas utilizados.
     */
    private void registrandoPlantillas(){
        //registrando los sistemas de plantilla.
        JavalinRenderer.register(JavalinThymeleaf.INSTANCE, ".html");
    }

    @Override
    public void aplicarRutas() {
        app.routes(() -> {
            get("/listar", ctx -> {
                //tomando el parametro utl y validando el tipo.
                List<Estudiante> lista = fakeServices.listarEstudiante();
                //
                Map<String, Object> modelo = new HashMap<>();
                modelo.put("titulo", "Listado de Estudiante");
                modelo.put("lista", lista);
                //enviando al sistema de plantilla.
                ctx.render("/templates/crud-tradicional/listar.html", modelo);
            });
        }

    }