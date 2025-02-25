package eu.europa.ec.dgc.issuance.restapi.controller;

import ehn.techiop.hcert.data.Eudgc;
import eu.europa.ec.dgc.issuance.restapi.dto.EgdcCodeData;
import eu.europa.ec.dgc.issuance.service.DgciService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/context")
@AllArgsConstructor
@ConditionalOnExpression("${issuance.endpoints.backendIssuing:false}")
public class DgciBackendController {
    private final DgciService dgciService;

    @Operation(
        summary = "create qr code of edgc",
        description = "create edgc for given data"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "signed edgc qr code created"),
        @ApiResponse(responseCode = "400", description = "wrong issue data")})
    @PutMapping(value = "/issue", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EgdcCodeData> createEdgc(@Valid @RequestBody Eudgc eudgc) {
        EgdcCodeData egdcCodeData = dgciService.createEdgc(eudgc);
        return ResponseEntity.ok(egdcCodeData);
    }
}
