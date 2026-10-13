package com.alura.literalura.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@Tag("unit")
class ArquitecturaIndependienteTest {

    private static final JavaClasses CLASES = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.alura.literalura");

    @Test
    void elDominioNoDependeDeFrameworksNiDeLaInfraestructura() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta..",
                        "com.fasterxml..",
                        "..infrastructure..",
                        "..adapter..",
                        "..application..")
                .check(CLASES);
    }

    @Test
    void laAplicacionNoDependeDeLaArquitectura() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta..",
                        "..infrastructure..",
                        "..adapter..")
                .check(CLASES);
    }

    @Test
    void losAdaptadoresDeEntradaNoConocenJpa() {
        noClasses().that().resideInAPackage("..adapter..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure.persistence..",
                        "jakarta.persistence..")
                .check(CLASES);
    }
}
