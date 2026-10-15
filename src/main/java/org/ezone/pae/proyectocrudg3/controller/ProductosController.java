package org.ezone.pae.proyectocrudg3.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.ezone.pae.proyectocrudg3.database.ConexionBD;
import org.ezone.pae.proyectocrudg3.model.Categoria;
import org.ezone.pae.proyectocrudg3.model.Marca;
import org.ezone.pae.proyectocrudg3.model.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductosController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtPrecio;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private DatePicker dpFechaCreacion;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtStock;

    @FXML
    private ComboBox<Marca> cmbMarca;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtFiltroTexto;

    @FXML
    private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML
    private DatePicker dpFiltroDesde;

    @FXML
    private DatePicker dpFiltroHasta;

    @FXML
    private TextField txtPrecioMin;

    @FXML
    private TextField txtPrecioMax;

    @FXML
    private CheckBox chkConStock;

    @FXML
    private ComboBox<String> cmbOrden;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiarFiltros;

    @FXML
    private TableView<Producto> tblProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TableColumn<Producto, LocalDate> colFecha;


    private final ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> listaCategorias =
            FXCollections.observableArrayList();

    private final ObservableList<Marca> listaMarcas =
            FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        configurarTableView();
        configurarSeleccionTableView();

        cargarCategorias();
        cargarMarcas();
        cargarOpcionesOrden();
        cargarProductos();
    }


    // Estos son los métodos iniciales para empezar a trabajar

    private void configurarTableView(){
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodigo()
                ));
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );
        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategoria()
                )
        );
        /*colDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDescripcion()
                )
        );*/
        colMarca.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getMarca()
                )
        );
        colPrecio.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getPrecio()
                )
        );
        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getStock()
                ).asObject()
        );
        colFecha.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getFechaCreacion()
                )
        );

        tblProductos.setItems(listaProductos);
    }


    // Selección

    private void configurarSeleccionTableView(){
        tblProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue != null){
                        cargarProductoEnFormulario(newValue);
                    }
                });
    }


    /**
     * Recibe como parámetro un producto seleccionado del TableView
     * @param p, es el producto seleccionado del TableView
     */
    private void cargarProductoEnFormulario(Producto p){
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        dpFechaCreacion.setValue(p.getFechaCreacion());

        /**
         * Buscamos dentro del ComboBox el valor que corresponda al ID
         */

        for(Categoria categoria: listaCategorias){
            if(categoria.getIdCategoria() == p.getIdCategoria()){
                cmbCategoria.setValue(categoria);
                break;
            }
        }

        /**
         * Buscamos dentro del ComboBox el valor que corresponda al ID
         */
        for(Marca marca: listaMarcas){
            if(marca.getIdMarca() == p.getIdMarca()){
                cmbMarca.setValue(marca);
                break;
            }
        }
    }


    /**
     * Consulta las categorias activas de la base de datos para mostrarlas dentro del ComboBox
     */
    private void cargarCategorias() {

        listaCategorias.clear();

        String sql = """
                SELECT
                    id_categoria,
                    nombre
                FROM catalogos.categorias
                WHERE estado = 1
                ORDER BY nombre
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Categoria categoria =
                        new Categoria(
                                rs.getInt("id_categoria"),
                                rs.getString("nombre")
                        );

                listaCategorias.add(categoria);
            }


            /*
             * Utilizamos copias de la lista para evitar que
             * cada ComboBox tenga problemas de selección.
             */
            cmbCategoria.setItems(
                    FXCollections.observableArrayList(
                            listaCategorias
                    )
            );

            cmbFiltroCategoria.setItems(
                    FXCollections.observableArrayList(
                            listaCategorias
                    )
            );

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las categorías.",
                    e
            );
        }
    }


    /**
     * Consulta las marcas activas almacenadas en PostgreSQL.
     *
     * Estas marcas se agregan al ComboBox utilizado en
     * el formulario de productos.
     */
    private void cargarMarcas() {

        listaMarcas.clear();

        String sql = """
                SELECT
                    id_marca,
                    nombre
                FROM catalogos.marcas
                WHERE estado = 1
                ORDER BY nombre
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Marca marca =
                        new Marca(
                                rs.getInt("id_marca"),
                                rs.getString("nombre")
                        );

                listaMarcas.add(marca);
            }

            cmbMarca.setItems(
                    FXCollections.observableArrayList(
                            listaMarcas
                    )
            );

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las marcas.",
                    e
            );
        }
    }


    /**
     * Agrega al ComboBox las opciones disponibles para
     * ordenar los resultados.
     *
     * El usuario no introduce directamente el nombre
     * de una columna SQL.
     *
     * Esto permite utilizar una whitelist de valores
     * conocidos y evita concatenar texto arbitrario
     * dentro de ORDER BY.
     */
    private void cargarOpcionesOrden() {

        cmbOrden.setItems(
                FXCollections.observableArrayList(
                        "Nombre",
                        "Precio menor a mayor",
                        "Precio mayor a menor",
                        "Fecha más reciente",
                        "Fecha más antigua",
                        "Stock mayor a menor",
                        "Stock menor a mayor"
                )
        );

        cmbOrden.setValue("Nombre");
    }


    /**
     * Consulta los productos activos almacenados en PostgreSQL
     * para mostrarlos dentro del TableView.
     */
    private void cargarProductos() {

        listaProductos.clear();

        String sql = """
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.descripcion,
                    p.precio,
                    p.stock,
                    p.fecha_creacion,
                    p.id_categoria,
                    c.nombre AS categoria,
                    p.id_marca,
                    m.nombre AS marca,
                    p.estado
                FROM catalogos.productos AS p
                INNER JOIN catalogos.categorias AS c
                    ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas AS m
                    ON p.id_marca = m.id_marca
                WHERE p.estado = 1
                ORDER BY p.id_producto
                """;

        try (
                Connection conexion =
                        ConexionBD.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {
                listaProductos.add(
                        crearProductoDesdeResultSet(rs)
                );
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar los productos.",
                    e
            );
        }
    }


    // Crear producto desde resultset permite escribir los objetos como filas

    private Producto crearProductoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Producto(

                rs.getInt(
                        "id_producto"
                ),

                rs.getString(
                        "codigo"
                ),

                rs.getString(
                        "nombre"
                ),

                rs.getString(
                        "descripcion"
                ),

                rs.getBigDecimal(
                        "precio"
                ),

                rs.getInt(
                        "stock"
                ),

                rs.getDate(
                        "fecha_creacion"
                ).toLocalDate(),

                rs.getInt(
                        "id_categoria"
                ),

                rs.getString(
                        "categoria"
                ),

                rs.getInt(
                        "id_marca"
                ),

                rs.getString(
                        "marca"
                ),

                rs.getInt(
                        "estado"
                )
        );
    }


    // CRUD

    @FXML
    private void agregarProducto() {

        /*
         * El botón Nuevo limpia cualquier producto cargado
         * para dejar el formulario listo para registrar uno diferente.
         */
        limpiarFormulario();

        dpFechaCreacion.setValue(LocalDate.now());
        txtCodigo.requestFocus();
    }


    @FXML
    private void guardarProducto() {

        // Comprobar que los datos ingresados sean válidos
        if (!validarFormulario()) {

            // Si el formulario no es válido, no es posible realizar el registro
            return;
        }


        // Obtener el código sin espacios al inicio o al final
        String codigo =
                txtCodigo.getText().trim();


        /*
         * Validación preventiva de UNIQUE.
         */
        if (existeCodigo(codigo, null)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código " + codigo +
                            " ya pertenece a otro producto."
            );

            txtCodigo.requestFocus();

            // Después de informar que el código ya existe, el registro no puede continuar
            return;
        }


        // Preparar la consulta para insertar el nuevo producto
        String sql = """
                INSERT INTO catalogos.productos
                (
                    codigo,
                    nombre,
                    descripcion,
                    precio,
                    stock,
                    fecha_creacion,
                    id_categoria,
                    id_marca,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """;


        try (
                // Abrir la conexión que se utilizará para ejecutar el INSERT
                Connection conexion =
                        ConexionBD.getConnection();

                // Preparar la consulta para asignarle los valores
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            // Asignar el código al primer parámetro
            ps.setString(
                    1,
                    codigo
            );

            // Asignar el nombre al segundo parámetro
            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );

            // Asignar la descripción al tercer parámetro
            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );

            // Convertir el precio a BigDecimal y asignarlo al cuarto parámetro
            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );

            // Convertir el stock a entero y asignarlo al quinto parámetro
            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );

            // Convertir la fecha seleccionada a Date de SQL
            ps.setDate(
                    6,
                    Date.valueOf(
                            dpFechaCreacion.getValue()
                    )
            );

            // Obtener y asignar el ID de la categoría
            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );

            // Obtener y asignar el ID de la marca
            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );


            // Ejecutar el INSERT y obtener la cantidad de filas afectadas
            int filasAfectadas =
                    ps.executeUpdate();


            // Comprobar que PostgreSQL haya insertado el producto
            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro guardado",
                        "El producto fue registrado correctamente."
                );

                // Limpiar el formulario después de terminar correctamente el registro
                limpiarFormulario();

                // Volver a consultar para mostrar el nuevo producto
                cargarProductos();
            }

        } catch (SQLException e) {

            /*
             * Aunque JavaFX realizó previamente la comprobación,
             * PostgreSQL continúa siendo la autoridad final.
             *
             * SQLState 23505 corresponde normalmente a una
             * violación de UNIQUE en PostgreSQL.
             */
            if ("23505".equals(e.getSQLState())) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Registro duplicado",
                        "El código ingresado ya existe."
                );

            } else {

                mostrarErrorBaseDatos(
                        "No se pudo guardar el producto.",
                        e
                );
            }
        }
    }


    @FXML
    private void actualizarProducto() {

        // Obtener el producto seleccionado en el TableView
        Producto productoSeleccionado =
                tblProductos.getSelectionModel()
                        .getSelectedItem();


        // Comprobar que exista un producto para actualizar
        if (productoSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto del TableView antes de actualizar."
            );

            // Sin un producto seleccionado no es posible realizar el UPDATE
            return;
        }


        // Comprobar que los nuevos datos sean válidos
        if (!validarFormulario()) {

            // Si los datos no son válidos, no es posible realizar la actualización
            return;
        }


        // Obtener el código ingresado
        String codigo =
                txtCodigo.getText().trim();


        /*
         * Se comprueba que otro producto diferente
         * no esté utilizando el mismo código.
         */
        if (existeCodigo(
                codigo,
                productoSeleccionado.getIdProducto()
        )) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código ingresado pertenece a otro producto."
            );

            txtCodigo.requestFocus();

            // Después de informar que otro producto utiliza el código, la actualización no puede continuar
            return;
        }


        // Preparar la consulta para actualizar el producto seleccionado
        String sql = """
                UPDATE catalogos.productos

                SET
                    codigo = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio = ?,
                    stock = ?,
                    fecha_creacion = ?,
                    id_categoria = ?,
                    id_marca = ?

                WHERE id_producto = ?
                """;


        try (
                // Abrir la conexión para ejecutar el UPDATE
                Connection conexion =
                        ConexionBD.getConnection();

                // Preparar la consulta para asignar los nuevos valores
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            // Asignar el nuevo código
            ps.setString(
                    1,
                    codigo
            );

            // Asignar el nuevo nombre
            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );

            // Asignar la nueva descripción
            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );

            // Convertir y asignar el nuevo precio
            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );

            // Convertir y asignar el nuevo stock
            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );

            // Convertir y asignar la nueva fecha
            ps.setDate(
                    6,
                    Date.valueOf(
                            dpFechaCreacion.getValue()
                    )
            );

            // Obtener y asignar la nueva categoría
            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );

            // Obtener y asignar la nueva marca
            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );

            // Indicar por ID cuál producto debe modificarse
            ps.setInt(
                    9,
                    productoSeleccionado.getIdProducto()
            );


            // Ejecutar el UPDATE y guardar la cantidad de filas afectadas
            int filasAfectadas =
                    ps.executeUpdate();


            // Comprobar que PostgreSQL haya realizado la actualización
            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "El producto fue actualizado correctamente."
                );

                // Limpiar los datos del producto actualizado
                limpiarFormulario();

                // Volver a consultar para mostrar los cambios
                cargarProductos();
            }

        } catch (SQLException e) {

            // Comprobar si PostgreSQL rechazó la actualización por un UNIQUE duplicado
            if ("23505".equals(e.getSQLState())) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ingresado ya está siendo utilizado."
                );

            } else {

                mostrarErrorBaseDatos(
                        "No se pudo actualizar el producto.",
                        e
                );
            }
        }
    }


    @FXML
    private void eliminarProducto() {

        // Obtener el producto seleccionado en el TableView
        Producto productoSeleccionado =
                tblProductos.getSelectionModel()
                        .getSelectedItem();


        // Comprobar que exista un producto para eliminar
        if (productoSeleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto antes de eliminarlo."
            );

            // Sin una selección no existe un producto sobre el cual realizar la eliminación
            return;
        }


        /*
         * Se solicita confirmación para evitar que el usuario
         * desactive accidentalmente un registro.
         */
        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle(
                "Confirmar eliminación"
        );

        confirmacion.setHeaderText(
                "Eliminar producto"
        );

        confirmacion.setContentText(
                "¿Desea desactivar el producto "
                        + productoSeleccionado.getNombre()
                        + "?"
        );


        // Esperar la respuesta del usuario
        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();


        if (
            // Comprobar si la ventana se cerró sin responder
                respuesta.isEmpty()
                        ||
                        // Comprobar si la opción seleccionada no fue aceptar
                        respuesta.get() != ButtonType.OK
        ) {

            // Si no se confirmó la eliminación, finalizar sin modificar el producto
            return;
        }


        /*
         * La eliminación es lógica, por lo que no se borra
         * el registro sino que su estado cambia a 0.
         */
        String sql = """
                UPDATE catalogos.productos
                SET estado = 0
                WHERE id_producto = ?
                """;


        try (
                // Abrir la conexión para ejecutar la eliminación lógica
                Connection conexion =
                        ConexionBD.getConnection();

                // Preparar el UPDATE
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            // Indicar por ID cuál producto debe desactivarse
            ps.setInt(
                    1,
                    productoSeleccionado.getIdProducto()
            );


            // Ejecutar el UPDATE y guardar la cantidad de filas afectadas
            int filasAfectadas =
                    ps.executeUpdate();


            // Comprobar que el producto haya sido desactivado
            if (filasAfectadas > 0) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue desactivado correctamente."
                );

                // Limpiar los datos del producto eliminado
                limpiarFormulario();

                // Recargar la tabla; el producto ya no aparece porque su estado es 0
                cargarProductos();
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo eliminar el producto.",
                    e
            );
        }
    }


    // Búsqueda parametrizada

    @FXML
    private void buscarProductos() {

        // Vaciar la lista antes de guardar los resultados de la nueva búsqueda
        listaProductos.clear();


        /*
         * La consulta inicia con los INNER JOIN necesarios
         * y con una condición permanente:
         *
         * p.estado = 1
         *
         * De esta forma nunca mostramos productos
         * eliminados lógicamente.
         */
        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id_producto,
                    p.codigo,
                    p.nombre,
                    p.descripcion,
                    p.precio,
                    p.stock,
                    p.fecha_creacion,
                    c.id_categoria,
                    c.nombre AS categoria,
                    m.id_marca,
                    m.nombre AS marca,
                    p.estado
                FROM catalogos.productos p
                INNER JOIN catalogos.categorias c
                    ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas m
                    ON p.id_marca = m.id_marca
                WHERE p.estado = 1
                """);


        /*
         * Aquí almacenaremos los valores que posteriormente
         * serán enviados al PreparedStatement.
         */
        List<Object> parametros =
                new ArrayList<>();


        // Obtener el texto utilizado para buscar por código o nombre
        String texto =
                txtFiltroTexto.getText().trim();


        // Comprobar si se ingresó algún texto
        if (!texto.isBlank()) {

            /*
             * OR permite que el texto coincida con código
             * o nombre.
             *
             * ILIKE permite búsquedas parciales sin importar
             * mayúsculas y minúsculas.
             */
            sql.append("""
                    AND (
                        p.codigo ILIKE ?
                        OR p.nombre ILIKE ?
                    )
                    """);


            // Agregar el texto como parámetro para buscar por código
            parametros.add(
                    "%" + texto + "%"
            );

            // Agregarlo nuevamente porque nombre utiliza otro parámetro ?
            parametros.add(
                    "%" + texto + "%"
            );
        }


        // Obtener la categoría seleccionada como filtro
        Categoria categoria =
                cmbFiltroCategoria.getValue();


        // Comprobar si se seleccionó una categoría
        if (categoria != null) {

            // Agregar la condición de categoría a la consulta
            sql.append("""
                    AND p.id_categoria = ?
                    """);

            // Guardar el ID que sustituirá el parámetro
            parametros.add(
                    categoria.getIdCategoria()
            );
        }


        // Obtener la fecha inicial del filtro
        LocalDate fechaDesde =
                dpFiltroDesde.getValue();


        // Comprobar si se indicó una fecha inicial
        if (fechaDesde != null) {

            // Agregar la condición para fechas iguales o posteriores
            sql.append("""
                    AND p.fecha_creacion >= ?
                    """);

            // Guardar la fecha como parámetro
            parametros.add(fechaDesde);
        }


        // Obtener la fecha final del filtro
        LocalDate fechaHasta =
                dpFiltroHasta.getValue();


        // Comprobar si se indicó una fecha final
        if (fechaHasta != null) {

            // Agregar la condición para fechas iguales o anteriores
            sql.append("""
                    AND p.fecha_creacion <= ?
                    """);

            // Guardar la fecha como parámetro
            parametros.add(fechaHasta);
        }


        /*
         * Verificamos que las fechas tengan sentido.
         *
         * "Desde" no debería ser posterior a "Hasta".
         */
        if (
            // Primero comprobar que exista una fecha inicial
                fechaDesde != null
                        &&
                        // Luego comprobar que también exista una fecha final
                        fechaHasta != null
                        &&
                        // Si existen ambas, comprobar si la inicial está después de la final
                        fechaDesde.isAfter(fechaHasta)
        ) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de fechas incorrecto",
                    "La fecha inicial no puede ser posterior a la fecha final."
            );

            // Después de informar el error, la búsqueda finaliza porque el rango de fechas no es válido
            return;
        }


        // Iniciar el precio mínimo sin valor porque este filtro es opcional
        BigDecimal precioMinimo = null;


        // Comprobar si se ingresó un precio mínimo
        if (!txtPrecioMin.getText().isBlank()) {

            try {

                // Convertir el texto ingresado a BigDecimal
                precioMinimo =
                        new BigDecimal(
                                txtPrecioMin
                                        .getText()
                                        .trim()
                        );


                if (
                    // Comparar el precio mínimo con cero para comprobar que no sea negativo
                        precioMinimo.compareTo(
                                BigDecimal.ZERO
                        ) < 0
                ) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "El precio mínimo no puede ser negativo."
                    );

                    // Después de informar el error, la búsqueda finaliza porque el precio mínimo no es válido
                    return;
                }


                // Agregar la condición de precio mínimo a la consulta
                sql.append("""
                        AND p.precio >= ?
                        """);

                // Guardar el precio mínimo como parámetro
                parametros.add(precioMinimo);

            } catch (NumberFormatException e) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio mínimo debe ser numérico."
                );

                txtPrecioMin.requestFocus();

                // Después de informar el error, la búsqueda finaliza porque el valor no puede utilizarse como precio
                return;
            }
        }


        // Iniciar el precio máximo sin valor porque este filtro es opcional
        BigDecimal precioMaximo = null;


        // Comprobar si se ingresó un precio máximo
        if (!txtPrecioMax.getText().isBlank()) {

            try {

                // Convertir el texto ingresado a BigDecimal
                precioMaximo =
                        new BigDecimal(
                                txtPrecioMax
                                        .getText()
                                        .trim()
                        );


                if (
                    // Comparar el precio máximo con cero para comprobar que no sea negativo
                        precioMaximo.compareTo(
                                BigDecimal.ZERO
                        ) < 0
                ) {

                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "El precio máximo no puede ser negativo."
                    );

                    // Después de informar el error, la búsqueda finaliza porque el precio máximo no es válido
                    return;
                }


                // Agregar la condición de precio máximo a la consulta
                sql.append("""
                        AND p.precio <= ?
                        """);

                // Guardar el precio máximo como parámetro
                parametros.add(precioMaximo);

            } catch (NumberFormatException e) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio máximo debe ser numérico."
                );

                txtPrecioMax.requestFocus();

                // Después de informar el error, la búsqueda finaliza porque el valor no puede utilizarse como precio
                return;
            }
        }


        /*
         * Validamos que el precio mínimo no sea superior
         * al precio máximo.
         */
        if (
            // Primero comprobar que exista un precio mínimo
                precioMinimo != null
                        &&
                        // Luego comprobar que también exista un precio máximo
                        precioMaximo != null
                        &&
                        // Si existen ambos, revisar si el mínimo resulta mayor que el máximo
                        precioMinimo.compareTo(
                                precioMaximo
                        ) > 0
        ) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de precios incorrecto",
                    "El precio mínimo no puede ser mayor que el precio máximo."
            );

            // Después de informar el error, la búsqueda finaliza porque el rango de precios no es válido
            return;
        }


        // Comprobar si se activó el filtro de productos con existencia
        if (chkConStock.isSelected()) {

            // Si está seleccionado, aceptar únicamente productos con stock mayor que cero
            sql.append("""
                    AND p.stock > 0
                    """);
        }


        // Obtener el criterio seleccionado para ordenar los resultados
        String orden =
                cmbOrden.getValue();


        // Si no se seleccionó un criterio, ordenar por nombre
        if (orden == null) {
            orden = "Nombre";
        }


        /*
         * El ORDER BY no utiliza parámetros ?, por eso las opciones
         * se controlan mediante los valores conocidos del ComboBox.
         */
        switch (orden) {

            case "Precio menor a mayor" ->
                    sql.append("""
                            ORDER BY p.precio ASC
                            """);

            case "Precio mayor a menor" ->
                    sql.append("""
                            ORDER BY p.precio DESC
                            """);

            case "Fecha más reciente" ->
                    sql.append("""
                            ORDER BY p.fecha_creacion DESC
                            """);

            case "Fecha más antigua" ->
                    sql.append("""
                            ORDER BY p.fecha_creacion ASC
                            """);

            case "Stock mayor a menor" ->
                    sql.append("""
                            ORDER BY p.stock DESC
                            """);

            case "Stock menor a mayor" ->
                    sql.append("""
                            ORDER BY p.stock ASC
                            """);

            default ->
                    sql.append("""
                            ORDER BY p.nombre ASC
                            """);
        }


        try (
                // Abrir la conexión después de terminar de construir la consulta
                Connection conexion =
                        ConexionBD.getConnection();

                // Preparar la consulta con los filtros utilizados
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql.toString()
                        )
        ) {

            /*
             * Recorremos la lista de parámetros para
             * asignarlos dinámicamente al PreparedStatement.
             */
            for (
                // Comenzar por el primer elemento de la lista
                    int i = 0;

                // Continuar mientras todavía existan parámetros
                    i < parametros.size();

                // Pasar al siguiente parámetro
                    i++
            ) {

                // Obtener el valor de la posición actual
                Object valor =
                        parametros.get(i);


                // Comprobar si el parámetro es una fecha
                if (valor instanceof LocalDate fecha) {

                    // Convertir LocalDate al Date utilizado por JDBC
                    ps.setDate(
                            i + 1,
                            Date.valueOf(fecha)
                    );

                    // Si no es fecha, comprobar si es un precio
                } else if (valor instanceof BigDecimal numero) {

                    // Asignar el BigDecimal al PreparedStatement
                    ps.setBigDecimal(
                            i + 1,
                            numero
                    );

                } else {

                    // Para String, Integer y los demás tipos utilizar setObject
                    ps.setObject(
                            i + 1,
                            valor
                    );
                }
            }


            try (
                    // Ejecutar la consulta después de asignar todos los parámetros
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                // Recorrer todos los registros encontrados
                while (rs.next()) {

                    // Convertir cada fila en un Producto y agregarla a la lista
                    listaProductos.add(
                            crearProductoDesdeResultSet(rs)
                    );
                }
            }


            // Mostrar en el TableView la lista obtenida
            tblProductos.setItems(
                    listaProductos
            );


            // Comprobar si la consulta no encontró ningún producto
            if (listaProductos.isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo realizar la búsqueda.",
                    e
            );
        }
    }


    @FXML
    private void limpiarFiltros() {

        // Limpiar los filtros utilizados para regresar a la consulta normal
        txtFiltroTexto.clear();
        cmbFiltroCategoria.setValue(null);
        dpFiltroDesde.setValue(null);
        dpFiltroHasta.setValue(null);
        txtPrecioMin.clear();
        txtPrecioMax.clear();
        chkConStock.setSelected(false);

        // Restaurar el orden predeterminado
        cmbOrden.setValue("Nombre");

        // Volver a mostrar todos los productos activos
        cargarProductos();
    }


    private boolean validarFormulario() {

        if (txtCodigo.getText().isBlank() || txtNombre.getText().isBlank()
                || txtPrecio.getText().isBlank() || txtStock.getText().isBlank()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Complete los campos obligatorios."
            );

            return false;
        }

        if (cmbCategoria.getValue() == null || cmbMarca.getValue() == null || dpFechaCreacion.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Seleccione categoría, marca y fecha."
            );

            return false;
        }

        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio inválido",
                        "El precio debe ser mayor que 0."
                );

                return false;
            }

            if (stock < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock inválido",
                        "El stock no puede ser menor que 0."
                );

                return false;
            }

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    "Precio y stock deben contener valores numéricos."
            );

            return false;
        }

        return true;
    }


    private boolean existeCodigo(String codigo, Integer idProducto) {

        String sql;

        if (idProducto == null) {
            sql = """
                    SELECT 1
                    FROM catalogos.productos
                    WHERE codigo = ?
                    """;
        } else {
            sql = """
                    SELECT 1
                    FROM catalogos.productos
                    WHERE codigo = ?
                    AND id_producto <> ?
                    """;
        }

        try (
                Connection conexion = ConexionBD.getConnection();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, codigo);

            if (idProducto != null) {
                ps.setInt(2, idProducto);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudo comprobar el código.",
                    e
            );

            return false;
        }
    }


    private void limpiarFormulario() {

        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();

        cmbCategoria.setValue(null);
        cmbMarca.setValue(null);
        dpFechaCreacion.setValue(null);

        tblProductos.getSelectionModel().clearSelection();
    }


    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }


    private void mostrarErrorBaseDatos(String mensaje, SQLException e) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error de base de datos");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(e.getMessage());

        alerta.showAndWait();
    }
}