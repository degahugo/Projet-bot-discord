package org.acme.bot.processors.entity;

import com.google.auto.service.AutoService;
import org.acme.bot.processors.annotations.GenerateEntity;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

/**
 *
 * For each record with annotation @GenerateEntity(table="...") : generate an ...Entity class (JPA Entity)
 *
 */
@SupportedAnnotationTypes("org.acme.bot.processors.annotations.GenerateEntity")
/**
 *
 * A Entity Annotation processor to generate a Entity class per (class,@Entity(value))
 *
 */
@AutoService(Processor.class)
public class ProcessorEntity extends AbstractProcessor {

    private static final String ENTITY_PACKAGE="org.acme.bot.infrastructure.persistence.entity";

    /**
     *
     * @return the latest supported version of Java
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     *The string format for the Entity class
     */


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GenerateEntity.class);

        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateEntity.class)) {
            TypeElement model = (TypeElement) element;

            // get the model class name
            String modelName=model.getSimpleName().toString();
            String entityName=modelName+"Entity";

            String tableName=model.getAnnotation(GenerateEntity.class).value();

            // for each field in the record class : attribute + getter + setter
            String fields="";
            String methods="";


            for (RecordComponentElement field : model.getRecordComponents()) {

                String fieldType = field.asType().toString();
                String fieldName = field.getSimpleName().toString();
                String Name = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                fields+="   private "+ fieldType + " " + fieldName+ ";\n";
                methods+="  public "+ fieldType + " get" + Name+ "(){ return "+fieldName+";}\n";
                methods+="  public void set"+Name+"("+fieldType+" "+fieldName+"){this."+fieldName+"="+fieldName+";}\n";

                //the class patern

                String ENTITY_CLASS_PATTERN= """
                    package %s;
                    
                    import jakarta.persistence.*;
                    
                    @Entity
                    @Table(name="%s")
                    public class %s {
                    
                        @Id
                        @GeneratedValue(strategy=GenerationType.IDENTITY)
                        private Long id;
                    
                    %s
                    
                        public Long getId(){return id;}
                        public void setId(Long id){this.id=id;}
                        
                    %s   
                        
                    }    
                    """.formatted(ENTITY_PACKAGE,tableName,entityName,fields,methods);

                // we write the .java file : javac will compile it

                try{
                    var file=processingEnv.getFiler().createSourceFile(ENTITY_PACKAGE+"."+entityName,model);
                    try(Writer writer=file.openWriter()){
                        writer.write(ENTITY_CLASS_PATTERN);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Impossible de générer "+entityName,e);
                }
            }
        }
        return true;
    }
}
