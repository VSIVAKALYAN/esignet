/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.mosip.idp.core.dto;

import io.mosip.idp.core.util.ErrorConstants;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class OtpRequest {

    @NotNull(message = ErrorConstants.INVALID_REQUEST)
    @NotBlank(message = ErrorConstants.INVALID_REQUEST)
    private String transactionId;

    @NotNull(message = ErrorConstants.INVALID_REQUEST)
    @NotBlank(message = ErrorConstants.INVALID_REQUEST)
    private String individualId;

    @NotNull(message = ErrorConstants.INVALID_REQUEST)
    @Size(min = 1, max = 2)
    private List<String> otpChannels;
}
