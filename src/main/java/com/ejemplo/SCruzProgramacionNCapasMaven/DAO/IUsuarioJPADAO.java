package com.ejemplo.SCruzProgramacionNCapasMaven.DAO;

import com.ejemplo.SCruzProgramacionNCapasMaven.JPA.Direccion;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Result;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.UsuarioDireccion;

public interface IUsuarioJPADAO {

    Result Add(UsuarioDireccion usuarioDireccion);

    Result DeleteUsuario(int idUsuario);

    Result DeleteDireccion(int idDireccion);
    
    Result UpdateUsario(UsuarioDireccion usuarioDireccion);
    
    Result UpdateDireccion(UsuarioDireccion usuarioDireccion);
    
    //pendiente AGREGAR DIRECCION
    
    Result GetAll();

}
