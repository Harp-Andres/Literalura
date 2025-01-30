package com.alura.literalura.model.Entity;


import com.alura.literalura.model.DTO.ResultadosLibroDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class LibroEntity {
    @Column(name = "titulo_libro")
    String tituloLibro;
    String lenguaje;
    @Column(name = "numero_descargas")
    int numeroDescargas;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "autor_id") // Columna de la clave foránea
    private AutorEntity autor;

    // Constructor vacío para JPA
    public LibroEntity() {
    }

    // Constructor para inicializar desde ResultadosLibro
    public LibroEntity(ResultadosLibroDTO resultadosLibroDTO) {
        this.tituloLibro = resultadosLibroDTO.tituloLibro();
        this.lenguaje = resultadosLibroDTO.lenguajes().stream().findFirst().orElse(null);
        this.numeroDescargas = resultadosLibroDTO.numeroDescargas();
    }


    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public int getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(int numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public AutorEntity getAutor() {
        return autor;
    }

    public void setAutor(AutorEntity autor) {
        this.autor = autor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                        ", tituloLibro='" + tituloLibro + '\'' +
                        ", lenguaje='" + lenguaje + '\'' +
                        ", numeroDescargas=" + numeroDescargas +
                        ", autor=" + autor;
    }


}
