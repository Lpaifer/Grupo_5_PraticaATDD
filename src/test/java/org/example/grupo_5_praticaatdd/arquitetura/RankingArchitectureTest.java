package org.example.grupo_5_praticaatdd.arquitetura;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class RankingArchitectureTest {
    private static final String BASE = "org.example.grupo_5_praticaatdd.ranking.";
    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.example.grupo_5_praticaatdd");

    @Test
    void classificacaoEPortaDependemSomenteDeValoresDoNucleo() {
        classes().that().resideInAnyPackage(BASE + "internal..", BASE + "port..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", BASE + "internal..", BASE + "port..", BASE + "api..")
                .check(CLASSES);
    }

    @Test
    void casoDeUsoNaoDependeDePersistenciaOuHttp() {
        classes().that().haveFullyQualifiedName(BASE + "api.ConsultarRanking")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", BASE + "api..", BASE + "port..", BASE + "internal..",
                        "org.springframework.stereotype")
                .check(CLASSES);
    }

    @Test
    void nucleoNaoConheceAdapter() {
        noClasses().that().resideInAnyPackage(BASE + "api..", BASE + "internal..", BASE + "port..")
                .should().dependOnClassesThat().resideInAPackage(BASE + "infrastructure..")
                .check(CLASSES);
    }
}
