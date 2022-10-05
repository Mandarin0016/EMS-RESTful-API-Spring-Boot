package org.modis.EmsApplication.model;

import lombok.*;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Headmaster extends User {
}
