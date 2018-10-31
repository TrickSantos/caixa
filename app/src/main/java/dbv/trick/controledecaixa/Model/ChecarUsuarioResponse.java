package dbv.trick.controledecaixa.Model;

public class ChecarUsuarioResponse {
    private boolean exists;
    private String msg_erro;

    public ChecarUsuarioResponse(){}

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getError_msg() {
        return msg_erro;
    }

    public void setError_msg(String error_msg) {
        this.msg_erro = error_msg;
    }
}
