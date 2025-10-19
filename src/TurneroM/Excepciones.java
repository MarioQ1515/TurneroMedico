package TurneroM;

public class Excepciones {

    /** Error de validación: campo vacío, valor inválido, etc. */
    public static class ValidationException extends RuntimeException {
        public ValidationException(String mensaje) { super(mensaje); }
    }

    /** Error por duplicación: DNI, índice único, etc. */
    public static class DuplicateException extends RuntimeException {
        public DuplicateException(String mensaje) { super(mensaje); }
    }

    /** Error cuando una entidad no se encuentra en la base. */
    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String mensaje) { super(mensaje); }
    }

    /** Error específico para turnos: médico con otro turno en esa fecha/hora. */
    public static class TurnoOcupadoException extends RuntimeException {
        public TurnoOcupadoException(String mensaje) { super(mensaje); }
    }

    /** Error genérico de acceso a base de datos (SQLException no mapeada). */
    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String mensaje, Throwable causa) { super(mensaje, causa); }
    }
}
