package org.fundaciobit.pluginsib.signature.api;

/**
 * Informació sobre l'estat de la signatura. Aquesta classe és retornada per la majoria de mètodes de la interfície SignatureSet.
 * 
 * @author anadal
 * @author anadal (u80067)  21 oct 2026 9:09:24
 */
public class StatusSignaturesSet {

    public static final int STATUS_INITIALIZING = 0;

    public static final int STATUS_IN_PROGRESS = 1;

    public static final int STATUS_FINAL_OK = 2;

    public static final int STATUS_FINAL_ERROR = -1;

    public static final int STATUS_CANCELLED = -2;

    protected int status = STATUS_INITIALIZING;

    protected String errorMsg;

    protected Throwable errorException;

    /** En cas d'error, codi d'error encara que fins i tot en el cas d'error normalment valdrà null.
        Els valors d'aquest camp són enviats pels diferents plugins de firma, per tant poden variar 
        molt en funció del plugin que s'hagi utilitzat. Per a més informació sobre els valors d'aquest
        camp, consultar la documentació del plugin de firma que s'hagi utilitzat.
    */
    // SignatureWeb: Retornar CODI d'Error en estat de firma #41
    protected String errorCode;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Throwable getErrorException() {
        return errorException;
    }

    public void setErrorException(Throwable errorException) {
        this.errorException = errorException;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
