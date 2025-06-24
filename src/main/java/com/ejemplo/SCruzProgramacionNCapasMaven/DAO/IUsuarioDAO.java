package com.ejemplo.SCruzProgramacionNCapasMaven.DAO;

import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Direccion;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Result;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.Usuario;
import com.ejemplo.SCruzProgramacionNCapasMaven.ML.UsuarioDireccion;

public interface IUsuarioDAO {

    Result GetAll();

    Result Add(UsuarioDireccion usuarioDireccion);

    Result UsuarioGetByid(int idUsuario);

    Result GetAllDinamic(Usuario usuario);

    Result UpdateUsuario(Usuario usuario);

    Result UpdateDireccion(Direccion direccion);
    
    Result DeleteUsuario(int idUsuario);
    
    Result DeleteDireccion(int idDireccion);

}
