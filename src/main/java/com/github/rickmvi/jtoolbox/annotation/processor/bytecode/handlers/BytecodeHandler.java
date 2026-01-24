package com.github.rickmvi.jtoolbox.annotation.processor.bytecode.handlers;

import com.github.rickmvi.jtoolbox.annotation.processor.bytecode.BytecodeTransformer.ClassInfo;
import org.objectweb.asm.ClassVisitor;

import javax.lang.model.element.TypeElement;

/**
 * Interface base para handlers que transformam bytecode.
 */
public interface BytecodeHandler {

    /**
     * Transforma o bytecode da classe adicionando métodos/campos necessários.
     *
     * @param cv ClassVisitor do ASM para adicionar elementos
     * @param element Elemento da classe sendo processada
     * @param classInfo Informações coletadas sobre a classe
     */
    void transform(ClassVisitor cv, TypeElement element, ClassInfo classInfo);
}