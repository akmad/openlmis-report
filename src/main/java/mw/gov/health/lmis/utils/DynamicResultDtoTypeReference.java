package mw.gov.health.lmis.utils;

import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.Type;

import mw.gov.health.lmis.reports.dto.external.ResultDto;

/**
 * Extension of {@link ParameterizedTypeReference} from Spring that allows dynamically changing
 * the type it represents at runtime. Since generic hacks are generally ugly, so is this class.
 * It eases the usage of the rest template however, allowing easily retrieving {@link ResultDto}
 * objects with the provided generic type at runtime.
 */
public class DynamicResultDtoTypeReference<T>
    extends BaseParameterizedTypeReference<ResultDto<T>> {

  /**
   * Constructs an instance that will represents {@link ResultDto} wrappers for the given type.
   *
   * @param valueType the value type (generic type) of the {@link ResultDto} type that this will
   *                  represent
   */
  public DynamicResultDtoTypeReference(Class<?> valueType) {
    super(valueType);
  }

  @Override
  protected Type getBaseType() {
    return ResultDto.class;
  }

}
