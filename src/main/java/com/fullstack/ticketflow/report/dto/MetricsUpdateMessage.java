package com.fullstack.ticketflow.report.dto;

// Aviso ligero que se publica por WebSocket cuando cambian las ventas
// (compra o cancelación). El payload es mínimo: solo dispara el refetch
// de las métricas en los dashboards del administrador.
public record MetricsUpdateMessage(long timestamp) {
}
