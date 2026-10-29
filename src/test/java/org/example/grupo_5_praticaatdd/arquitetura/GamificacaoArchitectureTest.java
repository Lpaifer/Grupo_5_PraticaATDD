package org.example.grupo_5_praticaatdd.arquitetura;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class GamificacaoArchitectureTest {

    private static final String MODULO = "org.example.grupo_5_praticaatdd.gamificacao..";
    private static final String API = "org.example.grupo_5_praticaatdd.gamificacao.api..";
    private static final String INTERNO = "org.example.grupo_5_praticaatdd.gamificacao.internal..";
    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.example.grupo_5_praticaatdd");

    @Test
    void codigoExternoNaoAcessaImplementacaoInterna() {
        noClasses().that().resideOutsideOfPackage(MODULO)
                .should().dependOnClassesThat().resideInAPackage(INTERNO)
                .because("consumidores devem utilizar o contrato público de gamificação")
                .check(CLASSES);
    }

    @Test
    void moduloDependeSomenteDeJavaEDoProprioModulo() {
        classes().that().resideInAPackage(MODULO)
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..", MODULO)
                .because("as regras são independentes de Spring, HTTP, JPA e camadas legadas")
                .check(CLASSES);
    }

    @Test
    void dadosDoContratoNaoExpoemImplementacaoInterna() {
        classes().that().areRecords().and().resideInAPackage(API)
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..", API)
                .because("entrada e saída devem expor somente valores do contrato público")
                .check(CLASSES);
    }
}
