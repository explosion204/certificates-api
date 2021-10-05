package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.IdentifiableDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;


/**
 * This aspect modifies {@link ResponseEntity} returned by controller.
 * Subclasses override {@code processModel()} to provide external logic for processing model (e.g. adding links).
 * Spring AOP does not allow separating pointcuts and advices OR defining them fully in parent class.
 * So aspects-descendants have to define their pointcuts and advices and call {@code applyForList()}
 * and {@code applyForSingleEntity()} methods manually.
 *
 * @param <T> model type
 *
 * @author Dmitry Karnyshov
 */
abstract class BaseHateoasAspect<T extends IdentifiableDto<T>> {
    static final String LIST_POINTCUT_PATTERN
            = "execution(public org.springframework.http.ResponseEntity<java.util.List+> *(*)) && within(@Hateoas *)";
    static final String SINGLE_ENTITY_POINTCUT_PATTERN
            = "execution(public org.springframework.http.ResponseEntity+ *(*)) && " +
              "within(@Hateoas *)";
    static final String RETURN_VALUE_NAME = "responseEntity";
    final Class<?> controllerClass;

    BaseHateoasAspect(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }

    /**
     * Apply HATEOAS for {@link ResponseEntity} object that contains {@link List<T>}.
     *
     * @param responseEntity the response entity
     * @return modified response entity
     */
    final ResponseEntity<List<T>> applyForList(ResponseEntity<List<T>> responseEntity) {
        List<T> dtoList = Objects.requireNonNull(responseEntity.getBody());
        HttpStatus status = responseEntity.getStatusCode();

        dtoList.forEach(this::processModel);

        return new ResponseEntity<>(dtoList, status);
    }

    /**
     * Apply HATEOAS for {@link ResponseEntity} object.
     *
     * @param responseEntity the response entity
     * @return modified response entity
     */
    final ResponseEntity<T> applyForSingleEntity(ResponseEntity<T> responseEntity) {
        T model = Objects.requireNonNull(responseEntity.getBody());
        HttpStatus status = responseEntity.getStatusCode();

        processModel(model);

        return new ResponseEntity<>(model, status);
    }

    /**
     * Model post-processing strategy.
     *
     * @param model target model
     */
    abstract void processModel(T model);
}
