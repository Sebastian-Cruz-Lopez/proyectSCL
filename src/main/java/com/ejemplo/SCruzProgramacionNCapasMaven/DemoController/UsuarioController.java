package com.ejemplo.SCruzProgramacionNCapasMaven.DemoController;

import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.ColoniaDAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.DireccionDAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.EstadoDAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.GetJPADAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.MunicipioDAOImplemeentation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.PaisDAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.RollDAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.UsuarioDAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.DAO.UsuarioJPADAOImplementation;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Colonia;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Direccion;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Result;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.ResultValidaDatos;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Roll;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Usuario;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.UsuarioDireccion;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioDAOImplementation usuarioDAOImplementation;

    @Autowired
    private PaisDAOImplementation paisDAOImplementation;

    @Autowired
    private EstadoDAOImplementation estadoDAOImplementation;

    @Autowired
    private RollDAOImplementation rollDAOImplementation;

    @Autowired
    private MunicipioDAOImplemeentation municipioDAOImplementation;

    @Autowired
    private ColoniaDAOImplementation coloniaDAOImplementation;

    @Autowired
    private DireccionDAOImplementation direccionDAOImplementation;

    @Autowired
    private UsuarioJPADAOImplementation usuarioJPADAOImplementation;

    @Autowired
    private GetJPADAOImplementation getJPADAOImplementation;

    @GetMapping
    public String Index(Model model) {

        Result result = usuarioJPADAOImplementation.GetAll();

        if (result.correct) {
            model.addAttribute("usuarioDireccion", result.objects);
            model.addAttribute("usuario", new Usuario());
            model.addAttribute("rolls", getJPADAOImplementation.GetRoll().objects);
        }

        return "UsuarioIndex";
    }

    @RequestMapping
    public String Index(@ModelAttribute Usuario usuario, Model model) {
        Result result = usuarioDAOImplementation.GetAllDinamic(usuario);
        model.addAttribute("usuario", new Usuario());

        model.addAttribute("usuarioDireccion", result.objects);
        model.addAttribute("rolls", getJPADAOImplementation.GetRoll().objects);

        return "UsuarioIndex";
    }

    @GetMapping("form/{idUsuario}") //AQUI SE PREPARA LA VISTA DEL FORMULARIO
    public String Accion(Model model, @PathVariable int idUsuario) {

        if (idUsuario < 1) {
            // Vista para agregar usuario nuevo
            model.addAttribute("paises", getJPADAOImplementation.GetPais().objects);
            model.addAttribute("rolls", getJPADAOImplementation.GetRoll().objects);
            model.addAttribute("usuarioDireccion", new UsuarioDireccion());
            return "UsuarioForm";
        } else {
            model.addAttribute("usuarioDireccion", usuarioJPADAOImplementation.GetByid(idUsuario).object);
            
            return "UsuarioDetail";
        }

    }

    @GetMapping("/formeditable")
    public String AccionEditable(@RequestParam int idUsuario, @RequestParam(required = false) Integer idDireccion, Model model) {

        if (idDireccion == null) { //EDITAR USUARIO

            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();

            usuarioDireccion = (UsuarioDireccion) usuarioJPADAOImplementation.GetByid(idUsuario).object;
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion.setIdDireccion(-1);
            model.addAttribute("usuarioDireccion", usuarioDireccion);

            //model.addAttribute("usuarioDireccion", usuarioDireccion);
            model.addAttribute("rolls", getJPADAOImplementation.GetRoll().objects);

        } else if (idDireccion == 0) { //AGREGAR DIRECCION

            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.setIdUsuario(idUsuario);
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion.setIdDireccion(0);
            model.addAttribute("usuarioDireccion", usuarioDireccion);
            model.addAttribute("paises", getJPADAOImplementation.GetPais().objects);

        } else { //EDITAR DIRECCION
            UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
            usuarioDireccion.Direccion = new Direccion();
            usuarioDireccion.Direccion = (Direccion) usuarioJPADAOImplementation.GetDireccionByid(idDireccion).object;
            usuarioDireccion.Usuario = new Usuario();
            usuarioDireccion.Usuario.setIdUsuario(idUsuario);
            //usuarioDireccion.Direccion = direccion;
            model.addAttribute("usuarioDireccion", usuarioDireccion);
            //model.addAttribute("direccion", direccion);
            model.addAttribute("paises", getJPADAOImplementation.GetPais().objects);
            model.addAttribute("estados", getJPADAOImplementation.GetEstado(usuarioDireccion.Direccion.Colonia.Municipio.Estado.getIdEstado()).objects);
            model.addAttribute("municipios", getJPADAOImplementation.GetMunicipio(usuarioDireccion.Direccion.Colonia.Municipio.getIdMunicipio()).objects);
            model.addAttribute("colonias", getJPADAOImplementation.GetColonia(usuarioDireccion.Direccion.Colonia.getIdColonia()).objects);

        }
        return "UsuarioForm";
    }

    @PostMapping("form") //AQUI SE RECUPERA LOS DATOS DEL FORMULARIO
    public String Accion(@Valid @ModelAttribute UsuarioDireccion usuarioDireccion, BindingResult bindingResult, @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarioDireccion", usuarioDireccion);
            return "UsuarioForm";
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                byte[] bytes = imagenFile.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(bytes);
                usuarioDireccion.Usuario.setImagen(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Result result = new Result();
        if (usuarioDireccion.Usuario.getIdUsuario() == 0) { // agregar usuario
            result = usuarioJPADAOImplementation.Add(usuarioDireccion);
        } else {
            if (usuarioDireccion.Direccion.getIdDireccion() == 0) { //agregar direccion
                result = usuarioJPADAOImplementation.AddDireccion(usuarioDireccion);

            } else if (usuarioDireccion.Direccion.getIdDireccion() == -1) { //editar usuario
                result = usuarioJPADAOImplementation.UpdateUsario(usuarioDireccion);
            } else {
                result = usuarioJPADAOImplementation.UpdateDireccion(usuarioDireccion); //editar direccion
            }
        }

        if (result.correct) {
            return "redirect:/usuario"; //SE REDIRECCIONA A LA VISTA DONDE ESTA EL GETALL
        } else {
            return "/usuario";
        }

    }

    @GetMapping("cargamasiva")
    public String CargaMasiva() {
        return "cargamasiva";
    }

    @PostMapping("cargamasiva")
    public String cargaMasiva(@RequestParam MultipartFile archivo, Model model, HttpSession session) throws IOException {

        if (archivo != null && !archivo.isEmpty()) {
            String fileExtention = archivo.getOriginalFilename().split("\\.")[1];

            String root = System.getProperty("user.dir");
            String path = "src/main/resources/archivos";
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String absolutePath = root + "/" + path + "/" + fecha + archivo.getOriginalFilename();

            List<UsuarioDireccion> usuariosDireccion = new ArrayList<>();

            if (fileExtention.equals("txt")) {
                usuariosDireccion = LecturaArchivoTXT(archivo);
                archivo.transferTo(new File(absolutePath));
            } else { //"xlsx"
                archivo.transferTo(new File(absolutePath));
                usuariosDireccion = LecturaArchivoXLSX((MultipartFile) new File(absolutePath));
            }

            List<ResultValidaDatos> listaErrores = ValidarDatos(usuariosDireccion);
            if (listaErrores.isEmpty()) {
                session.setAttribute("path", absolutePath);
                model.addAttribute("listaErrores", listaErrores);
                model.addAttribute("archivoCorrecto", true);
                return "redirect:/usuario/cargamasiva/Procesar";
            } else {
                model.addAttribute("listaErrores", listaErrores);
                model.addAttribute("archivoCorrecto", false);
            }
        }
        return "cargamasiva";
    }

    @GetMapping("/cargamasiva/Procesar")
    public String ProcesarCargaMasiva(HttpSession session, Model model) throws FileNotFoundException, IOException {

        String ruta = session.getAttribute("path").toString();
        if (ruta != null && !ruta.isEmpty()) {
            String fileExtention = ruta.split("\\.")[1];

            List<UsuarioDireccion> usuariosDireccion = new ArrayList<>();

            if (fileExtention.equals("txt")) {
                MultipartFile multipartFile = convertFileToMultipartFile(new File(ruta));
                usuariosDireccion = LecturaArchivoTXT(multipartFile);
            } else {
                usuariosDireccion = LecturaArchivoXLSX((MultipartFile) new File(ruta));
            }

            List<ResultValidaDatos> listaErrores = ValidarDatos(usuariosDireccion);
            if (listaErrores.isEmpty()) {
                for (UsuarioDireccion usuarioDireccion : usuariosDireccion) {
                    usuarioJPADAOImplementation.Add(usuarioDireccion);
//                      usuarioDAOImplementation.Add(usuariosDireccion);
                }
                return "redirect:/usuario";
            } else {
                model.addAttribute("listaErrores", listaErrores);
                model.addAttribute("archivoCorrecto", false);
            }
        }
        session.removeAttribute("path");

        return "redirect:/usuario/cargamasiva";
    }

    public List<UsuarioDireccion> LecturaArchivoXLSX(MultipartFile archivo) {
        List<UsuarioDireccion> listaUsuarios = new ArrayList<>();

        try (InputStream inputStream = archivo.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet primeraHoja = workbook.getSheetAt(0);
            Iterator<Row> filas = primeraHoja.iterator();

            boolean primeraFila = true;

            while (filas.hasNext()) {
                Row filaActual = filas.next();
                if (primeraFila) {
                    primeraFila = false;
                    continue;
                }

                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                Usuario usuario = new Usuario();
                Direccion direccion = new Direccion();
                Roll roll = new Roll();

                usuario.setNombre(getStringValue(filaActual.getCell(0)));
                usuario.setApellidoPaterno(getStringValue(filaActual.getCell(1)));
                usuario.setApellidoMaterno(getStringValue(filaActual.getCell(2)));
                usuario.setFechaNacimiento(filaActual.getCell(3).getDateCellValue());
                usuario.setTelefono(getStringValue(filaActual.getCell(4)));
                usuario.setEmail(getStringValue(filaActual.getCell(5)));
                usuario.setUsername(getStringValue(filaActual.getCell(6)));
                usuario.setPassword(getStringValue(filaActual.getCell(7)));
                String sexo = getStringValue(filaActual.getCell(8));
                if (sexo != null && !sexo.isEmpty()) {
                    usuario.setSexo(sexo.charAt(0));
                }
                usuario.setCelular(getStringValue(filaActual.getCell(9)));
                usuario.setCURP(getStringValue(filaActual.getCell(10)));

                Cell rollCell = filaActual.getCell(11);
                if (rollCell != null && rollCell.getCellType() == CellType.NUMERIC) {
                    roll.setIdRoll((int) rollCell.getNumericCellValue());
                    usuario.setRoll(roll);
                }

                usuario.setImagen(getStringValue(filaActual.getCell(12)));

                usuarioDireccion.setUsuario(usuario);
                usuarioDireccion.setDireccion(direccion);
                listaUsuarios.add(usuarioDireccion);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return listaUsuarios;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                    } else {
                        double num = cell.getNumericCellValue();
                        return (num % 1 == 0) ? String.valueOf((long) num) : String.valueOf(num);
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.toString().trim(); // Devuelve el valor evaluado
                case BLANK:
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public List<UsuarioDireccion> LecturaArchivoTXT(MultipartFile archivo) {

        Result result = new Result();

        List<UsuarioDireccion> usuariosDireccion = new ArrayList<>();

        try (InputStream inputStream = archivo.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            bufferedReader.readLine();
            String linea = "";
            while ((linea = bufferedReader.readLine()) != null) {
                String[] datos = linea.split("\\|");

                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                usuarioDireccion.Usuario = new Usuario();
                usuarioDireccion.Usuario.setNombre(datos[1]);
                usuarioDireccion.Usuario.setApellidoPaterno(datos[2]);
                usuarioDireccion.Usuario.setApellidoMaterno(datos[3]);
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = formato.parse(datos[4]);
                usuarioDireccion.Usuario.setFechaNacimiento(fecha);
                usuarioDireccion.Usuario.setTelefono(datos[5]);
                usuarioDireccion.Usuario.setEmail(datos[6]);
                usuarioDireccion.Usuario.setUsername(datos[7]);
                usuarioDireccion.Usuario.setPassword(datos[8]);

                if (datos[9] != null && !datos[9].isEmpty()) {
                    usuarioDireccion.Usuario.setSexo(datos[9].charAt(0));
                }

                usuarioDireccion.Usuario.setCelular(datos[10]);
                usuarioDireccion.Usuario.setCURP(datos[11]);

                Roll roll = new Roll();
                roll.setIdRoll(Integer.parseInt(datos[12]));
                usuarioDireccion.Usuario.setRoll(roll);

                usuarioDireccion.Usuario.setImagen(datos[13]);
                usuarioDireccion.Usuario.setEstatus(Integer.parseInt(datos[14]));

                Direccion direccion = new Direccion();
                direccion.setCalle(datos[15]);
                direccion.setNumeroInterior(datos[16]);
                direccion.setNumeroExterior(datos[17]);
                Colonia colonia = new Colonia();
                colonia.setIdColonia(Integer.parseInt(datos[18]));
                direccion.setColonia(colonia);
                usuarioDireccion.setDireccion(direccion);

                usuariosDireccion.add(usuarioDireccion);
            }

        } catch (Exception ex) {
            usuariosDireccion = null;
            result.errorMessage = ex.getLocalizedMessage();
        }
        return usuariosDireccion;
    }

    private List<ResultValidaDatos> ValidarDatos(List<UsuarioDireccion> usuarios) {
        List<ResultValidaDatos> listaErrores = new ArrayList<>();
        int fila = 1;

        if (usuarios == null) {
            listaErrores.add(new ResultValidaDatos(0, "Lista inexistente", "Lista inexistente"));
        } else if (usuarios.isEmpty()) {
            listaErrores.add(new ResultValidaDatos(0, "Lista vacía", "Lista vacía"));
        } else {
            for (UsuarioDireccion usuariodireccion : usuarios) {

                if (usuariodireccion.Usuario.getNombre() == null || usuariodireccion.Usuario.getNombre().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Nombre", "Campo obligatorio"));
                }

                if (usuariodireccion.Usuario.getApellidoPaterno() == null || usuariodireccion.Usuario.getApellidoPaterno().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Apellido Paterno", "Campo obligatorio"));
                }

                if (usuariodireccion.Usuario.getApellidoMaterno() == null || usuariodireccion.Usuario.getApellidoMaterno().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Apellido Materno", "Campo obligatorio"));
                }

                if (usuariodireccion.Usuario.getFechaNacimiento() == null) {
                    listaErrores.add(new ResultValidaDatos(fila, "Fecha de Nacimiento", "Campo obligatorio"));
                }

                if (!isValidPhoneNumber(usuariodireccion.Usuario.getTelefono())) {
                    listaErrores.add(new ResultValidaDatos(fila, "Número de Teléfono", "Debe contener 10 dígitos numéricos"));
                }

                if (usuariodireccion.Usuario.getEmail() == null || usuariodireccion.Usuario.getEmail().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Email", "Campo obligatorio"));
                } else if (!isValidEmail(usuariodireccion.Usuario.getEmail())) {
                    listaErrores.add(new ResultValidaDatos(fila, "Email", "Formato de email inválido"));
                }

                if (usuariodireccion.Usuario.getUsername() == null || usuariodireccion.Usuario.getUsername().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "UserName", "Campo obligatorio"));
                }

                if (usuariodireccion.Usuario.getPassword() == null || usuariodireccion.Usuario.getPassword().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Password", "Campo obligatorio"));
                } else if (usuariodireccion.Usuario.getPassword().length() < 6) {
                    listaErrores.add(new ResultValidaDatos(fila, "Password", "Debe tener al menos 6 caracteres"));
                }

                char sexo = usuariodireccion.Usuario.getSexo();
                if (sexo != 'H' && sexo != 'M') {
                    listaErrores.add(new ResultValidaDatos(fila, "Sexo", "Valor inválido. Debe ser 'H' o 'M'"));
                }

                if (usuariodireccion.Usuario.getCelular() == null || usuariodireccion.Usuario.getCelular().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Celular", "Campo obligatorio"));
                } else if (usuariodireccion.Usuario.getCelular().length() != 10) {
                    listaErrores.add(new ResultValidaDatos(fila, "Celular", "Debe tener 10 dígitos"));
                }

                if (usuariodireccion.Usuario.getCURP() == null || usuariodireccion.Usuario.getCURP().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "CURP", "Campo obligatorio"));
                } else if (!isValidCURP(usuariodireccion.Usuario.getCURP())) {
                    listaErrores.add(new ResultValidaDatos(fila, "CURP", "Formato de CURP inválido"));
                }

                if (usuariodireccion.Usuario.getImagen() == null || usuariodireccion.Usuario.getImagen().equals("")) {
                    listaErrores.add(new ResultValidaDatos(fila, "Imagen", "Campo obligatorio"));
                }
                fila++;
            }
        }

        return listaErrores;
    }

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        return convertFileToMultipartFile(file, "file");
    }

    public static MultipartFile convertFileToMultipartFile(File file, String paramName) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        try (FileInputStream input = new FileInputStream(file)) {
            return new MockMultipartFile(
                    paramName,
                    file.getName(),
                    contentType,
                    input
            );
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String numero) {
        return numero.matches("^\\d{10}$");
    }

    private boolean isValidCURP(String curp) {
        String curpRegex = "^[A-Z]{4}[0-9]{6}[A-Z]{6}[0-9A-Z]{2}$";
        return curp.toUpperCase().matches(curpRegex);
    }

    @GetMapping("/GetEstadosByPais/{idPais}")
    @ResponseBody
    public Result GetEstadosByPais(@PathVariable("idPais") int idPais) {

        return getJPADAOImplementation.GetEstado(idPais);

    }

    @GetMapping("/GetMunicipiosByEstado/{idEstado}")
    @ResponseBody
    public Result GetMunicipiosByEstado(@PathVariable("idEstado") int idEstado) {

        return getJPADAOImplementation.GetMunicipio(idEstado);
    }

    @GetMapping("/GetColoniasByMunicipio/{idMunicipio}")
    @ResponseBody
    public Result GetColoniasByMunicipio(@PathVariable("idMunicipio") int idMunicipio) {

        return getJPADAOImplementation.GetColonia(idMunicipio);
    }

    @GetMapping("/delete/{idUsuario}")
    public String DeleteUsuario(@PathVariable int idUsuario, RedirectAttributes redirectAttrs) {
        Result result = usuarioJPADAOImplementation.DeleteUsuario(idUsuario); // eliminación física

        if (result.correct) {
            redirectAttrs.addFlashAttribute("mensaje", "Usuario eliminado exitosamente.");
        } else {
            redirectAttrs.addFlashAttribute("error", "Error al eliminar el usuario.");
        }

        return "redirect:/usuario";
    }

    @GetMapping("/deletedireccion")
    public String DeleteDireccion(@RequestParam int idDireccion, @RequestParam int idUsuario, RedirectAttributes redirectAttrs) {
        Result result = usuarioJPADAOImplementation.DeleteDireccion(idDireccion); // eliminación física

        if (result.correct) {
            redirectAttrs.addFlashAttribute("mensaje", "Dirección eliminada correctamente.");
        } else {
            redirectAttrs.addFlashAttribute("error", "Error al eliminar la dirección.");
        }

        return "redirect:/usuario/form/" + idUsuario;
    }

    @PostMapping("Activo")
    @ResponseBody
    public Result ActivoUsuario(@RequestParam int idUsuario, @RequestParam int Estatus) {
        return usuarioDAOImplementation.UpdateActivo(idUsuario, Estatus);
    }

//    @GetMapping("usuario/form/GetColoniaByCP/{CodigoPostal}")
//    @ResponseBody
//    public Result GetColoniaByCP(@PathVariable("CodigoPostal") String codigoPostal) {
//        return getJPADAOImplementation.GetColoniaByCP(codigoPostal);
//    }
}
