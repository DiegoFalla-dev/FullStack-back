package com.fullstack.ticketflow.tickettype.dto;

public record StockUpdateMessage(
        Integer ticketTypeId,
        Integer availableStock
) {}