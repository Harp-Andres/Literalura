package com.alura.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "autores")
public class AutorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    private int anioNacimiento;
    private int anioMuerte;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LibroEntity> libro = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public AutorEntity() {
    }

    // Constructor simple para un autor
    public AutorEntity(String nombre, Integer anioNacimiento, Integer anioMuerte) {
        this.nombre = nombre;
        this.anioNacimiento = anioNacimiento;
        this.anioMuerte = anioMuerte;
    }


    public void agregarLibro(LibroEntity libro) {
        if (this.libro == null) {
            this.libro = new ArrayList<>();
        }
        this.libro.add(libro);
        libro.setAutor(this); // Establecer la relación bidireccional
    }

    public List<LibroEntity> getLibro() {
        if (libro == null) {
            libro = new ArrayList<>();
        }
        return Collections.unmodifiableList(libro); // Retorna una lista no modificable
    }

    public void setLibro(List<LibroEntity> libro) {
        libro.forEach(l -> l.setAutor(this));
        this.libro = libro;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(int anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public int getAnioMuerte() {
        return anioMuerte;
    }

    public void setAnioMuerte(int anioMuerte) {
        this.anioMuerte = anioMuerte;
    }

    @Override
    public String toString() {
        return "AutorEntity{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", anioNacimiento=" + anioNacimiento +
                ", anioMuerte=" + anioMuerte +
                ", libro=" + libro +
                '}';
    }
}


