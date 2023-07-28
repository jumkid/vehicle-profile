package com.jumkid.vehicle.service.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.dto.GenericDTO;
import lombok.*;

import jakarta.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@EqualsAndHashCode(of = {"uuid"}, callSuper = false)
public class MediaFile extends GenericDTO {

    String uuid;

    @NotBlank
    private String title;

    private String content;

    private Boolean activated;

    private String module;

    private AccessScope accessScope;

}
