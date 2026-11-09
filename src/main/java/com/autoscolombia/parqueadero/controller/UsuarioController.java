package com.autoscolombia.parqueadero.controller;

import com.autoscolombia.parqueadero.exception.ReglaNegocioException;
import com.autoscolombia.parqueadero.model.RolUsuario;
import com.autoscolombia.parqueadero.model.Usuario;
import com.autoscolombia.parqueadero.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ── Listado con filtros ──────────────────────────────────────────
    @GetMapping
    public String listar(@RequestParam(required = false) String documento,
                         @RequestParam(required = false) RolUsuario rol,
                         Model model) {

        List<Usuario> usuarios = usuarioService.buscarConFiltros(documento, rol);

        model.addAttribute("titulo", "Usuarios");
        model.addAttribute("seccion", "Administración");
        model.addAttribute("subseccion", "Gestión de Usuarios");
        model.addAttribute("moduloActivo", "usuarios");
        model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        model.addAttribute("contenido", "usuarios/lista-usuarios");
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("filtroDocumento", documento != null ? documento : "");
        model.addAttribute("filtroRol", rol);
        return "layout/base";
    }

    // ── Formulario Nuevo ─────────────────────────────────────────────
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("titulo", "Nuevo Usuario");
        model.addAttribute("seccion", "Administración");
        model.addAttribute("subseccion", "Registrar Usuario");
        model.addAttribute("moduloActivo", "usuarios");
        model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        model.addAttribute("contenido", "usuarios/form-usuario");
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("esEdicion", false);
        model.addAttribute("error", "");
        return "layout/base";
    }

    // ── Guardar Nuevo ────────────────────────────────────────────────
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          RedirectAttributes flash,
                          Model model) {
        try {
            usuarioService.guardar(usuario);
            flash.addFlashAttribute("exito", "Usuario \"" + usuario.getNombre() + "\" registrado correctamente.");
            return "redirect:/usuarios";
        } catch (ReglaNegocioException e) {
            model.addAttribute("titulo", "Nuevo Usuario");
            model.addAttribute("seccion", "Administración");
            model.addAttribute("subseccion", "Registrar Usuario");
            model.addAttribute("moduloActivo", "usuarios");
            model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            model.addAttribute("contenido", "usuarios/form-usuario");
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("esEdicion", false);
            model.addAttribute("error", e.getMensaje());
            return "layout/base";
        }
    }

    // ── Formulario Editar ────────────────────────────────────────────
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id,
                                   Model model,
                                   RedirectAttributes flash) {
        return usuarioService.buscarPorId(id).map(usuario -> {
            model.addAttribute("titulo", "Editar Usuario");
            model.addAttribute("seccion", "Administración");
            model.addAttribute("subseccion", "Editar Usuario");
            model.addAttribute("moduloActivo", "usuarios");
            model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            model.addAttribute("contenido", "usuarios/form-usuario");
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("esEdicion", true);
            model.addAttribute("error", "");
            return "layout/base";
        }).orElseGet(() -> {
            flash.addFlashAttribute("errorGlobal", "Usuario no encontrado.");
            return "redirect:/usuarios";
        });
    }

    // ── Actualizar ───────────────────────────────────────────────────
    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Usuario usuario,
                             RedirectAttributes flash,
                             Model model) {
        try {
            usuarioService.actualizar(id, usuario);
            flash.addFlashAttribute("exito", "Usuario actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (ReglaNegocioException e) {
            model.addAttribute("titulo", "Editar Usuario");
            model.addAttribute("seccion", "Administración");
            model.addAttribute("subseccion", "Editar Usuario");
            model.addAttribute("moduloActivo", "usuarios");
            model.addAttribute("fechaActual", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            model.addAttribute("contenido", "usuarios/form-usuario");
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", RolUsuario.values());
            model.addAttribute("esEdicion", true);
            model.addAttribute("error", e.getMensaje());
            return "layout/base";
        }
    }

    // ── Eliminar ─────────────────────────────────────────────────────
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.eliminar(id);
            flash.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorGlobal", e.getMensaje());
        }
        return "redirect:/usuarios";
    }
}
