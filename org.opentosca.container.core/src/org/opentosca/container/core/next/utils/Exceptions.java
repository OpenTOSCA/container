package org.opentosca.container.core.next.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Exceptions {

  @FunctionalInterface
  public interface Consumer_WithExceptions<T> {
    void accept(T t) throws Exception;
  }

  @FunctionalInterface
  public interface Function_WithExceptions<T, R> {
    R apply(T t) throws Exception;
  }

  @FunctionalInterface
  public interface Supplier_WithExceptions<T> {
    T get() throws Exception;
  }

  @FunctionalInterface
  public interface Runnable_WithExceptions {
    void accept() throws Exception;
  }

  /**
   * Rethrows an exception as unchecked.
   *
   * {@code .forEach(rethrow(name -> System.out.println(Class.forName(name)))) }
   *
   * {@code .forEach(rethrow(ClassNameUtil::println)) }
   *
   * @param consumer The {@link Consumer_WithExceptions} consumer
   * @param <T> Object of type T to accept
   * @return {@link Consumer} object
   */
  public static <T> Consumer<T> rethrow(final Consumer_WithExceptions<T> consumer) {
    return t -> {
      try {
        consumer.accept(t);
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  /**
   * Rethrows an exception as unchecked.
   *
   * {@code .map(rethrow(name -> Class.forName(name))) }
   *
   * {@code .map(rethrow(Class::forName)) }
   *
   * @param function The {@link Function_WithExceptions} function
   * @param <T> Object of type T to apply
   * @param <R> The type to return
   * @return {@link Function} object
   */
  public static <T, R> Function<T, R> rethrow(final Function_WithExceptions<T, R> function) {
    return t -> {
      try {
        return function.apply(t);
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * Rethrows an exception as unchecked.
   *
   * {@code rethrow(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))) }
   *
   * @param supplier The {@link Supplier_WithExceptions} supplier
   * @param <T> Object of type T to get
   * @return {@link Supplier} object
   */
  public static <T> Supplier<T> rethrow(final Supplier_WithExceptions<T> supplier) {
    return () -> {
      try {
        return supplier.get();
      } catch (final Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * Unchecks an expression.
   *
   * {@code uncheck(() -> Class.forName("xxx")); }
   *
   * @param runnable The {@link Runnable_WithExceptions} runnable
   */
  public static void uncheck(final Runnable_WithExceptions runnable) {
    try {
      runnable.accept();
    } catch (final Exception exception) {
      throwAsUnchecked(exception);
    }
  }

  /**
   * Unchecks an expression.
   *
   * {@code uncheck(() -> Class.forName("xxx")); }
   *
   * @param supplier The {@link Supplier_WithExceptions} supplier
   * @param <R> The type to return
   * @return Object of type R
   */
  public static <R> R uncheck(final Supplier_WithExceptions<R> supplier) {
    try {
      return supplier.get();
    } catch (final Exception exception) {
      throwAsUnchecked(exception);
      return null;
    }
  }

  /**
   * Unchecks an expression.
   *
   * {@code uncheck(Class::forName, "xxx"); }
   *
   * @param function The {@link Function_WithExceptions} function
   * @param t The object of type T
   * @param <T> Object of type T to apply
   * @param <R> The type to return
   * @return Object of type R
   */
  public static <T, R> R uncheck(final Function_WithExceptions<T, R> function, final T t) {
    try {
      return function.apply(t);
    } catch (final Exception exception) {
      throwAsUnchecked(exception);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static <E extends Throwable> void throwAsUnchecked(final Exception exception) throws E {
    throw (E) exception;
  }
}
