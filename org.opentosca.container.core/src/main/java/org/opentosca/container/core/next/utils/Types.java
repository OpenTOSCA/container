package org.opentosca.container.core.next.utils;

public abstract class Types {

  /**
   * Utility function to create a proper type definition using generics.
   * <p>
   * Usage: Class<Map<String, String>> mapType = Types.generify(Map.class);
   *
   * @param clazz The type to generify
   * @return The generified type
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> generify(final Class<?> clazz) {
    return (Class<T>) clazz;
  }
}
