package com.kalsym.socketserver;

/**
 *
 * @author zeeshan
 */
public interface ProcessSocketServerRequest {

    /**
     * Process Incoming message
     *
     * @param requestMessage
     * @return
     */
    public String doProcessMO(String requestMessage);

    /**
     * Process Incoming message
     *
     * @param requestMessage
     * @return
     */
    public String doRejectMO(String requestMessage);
}
