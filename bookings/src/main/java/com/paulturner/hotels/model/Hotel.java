package com.paulturner.hotels.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Hotel {

    @NotNull
    private String id;

    private String name;
}
