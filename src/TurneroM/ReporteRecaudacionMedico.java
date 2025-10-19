package TurneroM;

import java.time.LocalDate;

public class ReporteRecaudacionMedico {
    private final long medicoId;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final int cantidadTurnos;
    private final double totalCobrado;

    public ReporteRecaudacionMedico(long medicoId, LocalDate desde, LocalDate hasta, int cantidadTurnos, double totalCobrado) {
        this.medicoId = medicoId;
        this.desde = desde;
        this.hasta = hasta;
        this.cantidadTurnos = cantidadTurnos;
        this.totalCobrado = totalCobrado;
    }
    public long getMedicoId() { return medicoId; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public int getCantidadTurnos() { return cantidadTurnos; }
    public double getTotalCobrado() { return totalCobrado; }
}
