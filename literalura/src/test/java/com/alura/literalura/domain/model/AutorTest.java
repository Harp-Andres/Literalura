package com.alura.literalura.domain.model;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class AutorTest {

    @Test
    void estaVivoEntreNacimientoYMuerteInclusive() {
        Autor autor = new Autor(null, "Shelley", 1797, 1851);

        assertThat(autor.estabaVivoEn(1797)).isTrue();
        assertThat(autor.estabaVivoEn(1818)).isTrue();
        assertThat(autor.estabaVivoEn(1851)).isTrue();
        assertThat(autor.estabaVivoEn(1796)).isFalse();
        assertThat(autor.estabaVivoEn(1852)).isFalse();
    }

    @Test
    void sinAnioDeNacimientoNoSeConsideraVivo() {
        Autor autor = new Autor(null, "Anonimo", null, null);

        assertThat(autor.estabaVivoEn(1900)).isFalse();
    }

    @Test
    void sinAnioDeMuerteSigueVivoDespuesDeNacer() {
        Autor autor = new Autor(null, "Vivo", 1980, null);

        assertThat(autor.estabaVivoEn(2026)).isTrue();
        assertThat(autor.estabaVivoEn(1979)).isFalse();
    }
}
